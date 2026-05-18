package org.springframework.samples.petclinic.vets;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class VetsServiceApplicationTest {

	@Test
	void mainShouldStartApplication() {
		assertThatCode(() -> VetsServiceApplication.main(new String[] {
			"--spring.profiles.active=test",
			"--spring.main.web-application-type=none"
		})).doesNotThrowAnyException();
	}
}