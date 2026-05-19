package org.springframework.samples.petclinic.config;

import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.Test;

class ConfigServerApplicationTest {

	@Test
	void mainShouldStartApplication() {
		assertThatCode(() -> ConfigServerApplication.main(new String[] {
			"--spring.main.web-application-type=none",
			"--spring.cloud.config.enabled=false"
		})).doesNotThrowAnyException();
	}
}