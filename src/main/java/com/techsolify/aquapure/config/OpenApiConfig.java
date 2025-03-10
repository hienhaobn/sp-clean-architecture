package com.techsolify.aquapure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Value("${spring.application.name:AquaPure API}")
  private String applicationName;

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title(applicationName)
                .description("API documentation for AquaPure application")
                .version("v1.0")
                .contact(
                    new Contact()
                        .name("TechSolify")
                        .email("contact@techsolify.com")
                        .url("https://techsolify.com"))
                .license(
                    new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0")))
        .servers(List.of(new Server().url("/").description("Default Server URL")));
  }
}
