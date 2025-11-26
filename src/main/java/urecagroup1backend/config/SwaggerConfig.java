package urecagroup1backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.ArrayList;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class SwaggerConfig {

    // 환경변수로 서버 URL 주입
    @Value("${swagger.server-url}")
    private String serverUrl;

    @Bean
    public OpenAPI nongDamAPI() {
        Info info = new Info()
                .title("울트라 킹왕짱 동시성 제어 연습 API")
                .description("울트라 킹왕짱 동시성 제어 연습 API 명세서")
                .version("1.0.0");

        String jwtSchemeName = "JWT TOKEN";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        // 서버 URL을 환경변수에서 가져옴
        Server localServer = new Server()
                .url(serverUrl)  // 환경변수로 설정된 URL
                .description("Local Server");

        return new OpenAPI()
                .info(info)
                .servers(Collections.singletonList(localServer))
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    public SwaggerConfig(MappingJackson2HttpMessageConverter converter) {
        var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        supportedMediaTypes.add(new MediaType("application", "octet-stream"));
        converter.setSupportedMediaTypes(supportedMediaTypes);
    }
}
