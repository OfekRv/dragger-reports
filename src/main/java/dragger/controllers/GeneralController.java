package dragger.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneralController {
	@Value("${IS_DEV_MODE}")
	private boolean isDevMode;

	@GetMapping("/api/isDeveloperMode")
	public boolean isDeveloperMode() {
		return isDevMode;
	}
}
