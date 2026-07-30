# ---- 1단계: 빌드 ----
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Wrapper 먼저 복사
COPY gradlew .
COPY gradle ./gradle
RUN chmod +x ./gradlew

# 의존성 캐싱
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon || true

# 소스 복사 후 빌드
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# ---- 2단계: 실행 ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENV TZ=UTC
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar app.jar"]