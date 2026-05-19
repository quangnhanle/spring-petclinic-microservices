package org.springframework.samples.petclinic.vets;

import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.Test;

class VetsServiceApplicationTest {

	@Test
	void mainShouldStartApplication() {
		assertThatCode(() -> VetsServiceApplication.main(new String[] {
			"--spring.profiles.active=test",
			"--spring.main.web-application-type=none"
		})).doesNotThrowAnyException();
	}
}