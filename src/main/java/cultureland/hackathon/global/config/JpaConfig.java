package cultureland.hackathon.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
/*
@EnableJpaAuditing을 HackathonApplication에 붙이면 @WebMvcTest 같은 슬라이스 테스트가 깨짐
CI에 테스트가 걸려 있으니 지금은 분리
 */
