package io.joshuasalcedo.fx.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for SpringDoc OpenAPI.
 *
 * <p>This class configures the OpenAPI documentation for the application's REST API. It defines
 * metadata such as API title, description, version, and contact information.
 */
@Configuration
public class OpenApiConfig {

  /**
   * Configures the OpenAPI documentation.
   *
   * @return the OpenAPI configuration
   */
  @Bean
  public OpenAPI clipboardOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Clipboard API")
                .description("REST API for managing clipboard entries")
                .version("1.0.0")
                .contact(
                    new Contact().name("Joshua Salcedo").url("https://github.com/joshuasalcedo/fx"))
                .license(
                    new License().name("MIT License").url("https://opensource.org/licenses/MIT")));
  }
}
