package cultureland.hackathon.global.openai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class OpenAiConfig {

    private static final String OPENAI_BASE_URL = "https://api.openai.com/v1";

    @Bean
    public RestClient openAiRestClient(
        RestClient.Builder builder,
        // key 값 주입
        @Value("${openai.api-key}")
        String apiKey,
        // timeout 값 Duration으로 주입
        @Value("${openai.timeout}")
        Duration timeout
    ) {
        // OpenAI 서버 연결 및 응답 대기 제한 시간 설정
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);

        return builder
                // 공통 주소 설정
                .baseUrl(OPENAI_BASE_URL)
                // 모든 요청에 API 인증 헤더 자동 추가
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + apiKey
                )
                // 요청과 응답은 JSON 형식 사용
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                // 타임아웃 적용
                .requestFactory(requestFactory)
                .build();
    }
}
