package io.joshuasalcedo.fx.infrastructure.configuration;

import io.joshuasalcedo.clipboard.core.ClipboardListenerProvider;
import io.joshuasalcedo.clipboard.core.ClipboardListenerProviderFactory;
import io.joshuasalcedo.clipboard.core.ClipboardMonitor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClipboardConfiguration {

  @Bean
  public ClipboardListenerProvider clipboardListenerProvider() {
    return ClipboardListenerProviderFactory.create();
  }

  @Bean
  public ClipboardMonitor clipboardMonitor(ClipboardListenerProvider provider) {
    return new ClipboardMonitor();
  }
}
