package io.joshuasalcedo.fx.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ContextLoadTest {

    @Test
    public void contextLoad() {
        
    }
}
