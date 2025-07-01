package io.joshuasalcedo.fx.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Controller to handle web routes and serve the Next.js static export. */
@Controller
public class WebController {

  /**
   * Serve the index.html for the root path. This allows the Next.js app to be served from the root
   * URL.
   */
  @GetMapping("/")
  public String index() {
    return "forward:/index.html";
  }

  /**
   * Handle any other routes that should be handled by the frontend. This ensures client-side
   * routing works properly.
   */
  @GetMapping(value = "/{path:[^\\.]*}")
  public String redirect() {
    return "forward:/index.html";
  }
}
