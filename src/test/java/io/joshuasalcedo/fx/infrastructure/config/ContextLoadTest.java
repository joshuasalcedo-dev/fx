package io.joshuasalcedo.fx.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
    locations = "classpath:application-test.properties",
    properties = {"springdoc.api-docs.enabled=false"})
public class ContextLoadTest {

  @Test
  public void contextLoad() {}
}
