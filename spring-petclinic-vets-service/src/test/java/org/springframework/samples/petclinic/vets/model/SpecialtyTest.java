package org.springframework.samples.petclinic.vets.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyTest {

	@Test
	void shouldExposeIdAndName() {
		Specialty specialty = new Specialty();
		specialty.setName("Dentistry");

		assertThat(specialty.getId()).isNull();
		assertThat(specialty.getName()).isEqualTo("Dentistry");
	}
}