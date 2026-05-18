package org.springframework.samples.petclinic.discovery;

import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.Test;

class DiscoveryServerApplicationTest {

	@Test
	void mainShouldStartApplication() {
		assertThatCode(() -> DiscoveryServerApplication.main(new String[] {
			"--spring.cloud.config.enabled=false",
			"--server.port=0"
		})).doesNotThrowAnyException();
	}
}