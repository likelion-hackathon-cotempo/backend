package cultureland.hackathon.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String ACCESS_TOKEN_COOKIE = "accessToken";
    private static final String SECURITY_SCHEME_NAME = "cookieAuth";

    @Bean
    public OpenAPI openAPI() {
        // 인증 토큰을 헤더가 아닌 쿠키로 주고받으므로 APIKEY + COOKIE로 정의
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name(ACCESS_TOKEN_COOKIE);

        return new OpenAPI()
                .info(new Info()
                        .title("CoTempo API")
                        .description("글로벌 팀을 위한 일정 조율 서비스 API")
                        .version("v1"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme));
    }
}