package io.joshuasalcedo.fx.infrastructure.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc configuration for API documentation.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Clipboard API",
                version = "1.0.0",
                description = "REST API for managing clipboard entries",
                contact = @Contact(
                        name = "Joshua Salcedo",
                        url = "https://github.com/joshuasalcedo/fx"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(url = "http://localhost:5000", description = "Local server")
        }
)
public class SpringDocConfig {
    
    private static final Logger log = LoggerFactory.getLogger(SpringDocConfig.class);
    
    @Bean
    public GroupedOpenApi publicApi() {
        log.info("Configuring SpringDoc API documentation...");
        return GroupedOpenApi.builder()
                .group("clipboard-api")
                .pathsToMatch("/api/**")
                .packagesToScan("io.joshuasalcedo.fx.api")
                .build();
    }
}