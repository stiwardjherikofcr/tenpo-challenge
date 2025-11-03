package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Percentage Calculator Service API")
                        .version("1.0.0")
                        .description("REST API that sums two numbers and applies a dynamic percentage with cache and async call history")
                        .contact(new Contact()
                                .name("Tenpo Challenge")
                                .email("challenge@tenpo.cl"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
