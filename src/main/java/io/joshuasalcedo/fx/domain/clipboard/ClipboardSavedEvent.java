package io.joshuasalcedo.fx.domain.clipboard;

import org.springframework.context.ApplicationEvent;

/** Event published when a new clipboard entry needs to be saved. */
public class ClipboardSavedEvent extends ApplicationEvent {

  private final String content;

  public ClipboardSavedEvent(Object source, String content) {
    super(source);
    this.content = content;
  }

  public String getContent() {
    return content;
  }
}
