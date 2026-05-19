package org.springframework.samples.petclinic.vets.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VetTest {

	@Test
	void shouldExposeBasicFields() {
		Vet vet = new Vet();
		vet.setId(7);
		vet.setFirstName("James");
		vet.setLastName("Carter");

		assertThat(vet.getId()).isEqualTo(7);
		assertThat(vet.getFirstName()).isEqualTo("James");
		assertThat(vet.getLastName()).isEqualTo("Carter");
	}

	@Test
	void shouldStoreAndSortSpecialties() {
		Vet vet = new Vet();

		Specialty surgery = new Specialty();
		surgery.setName("Surgery");
		Specialty dentistry = new Specialty();
		dentistry.setName("Dentistry");

		vet.addSpecialty(surgery);
		vet.addSpecialty(dentistry);

		assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
		assertThat(vet.getSpecialties()).extracting(Specialty::getName)
			.containsExactly("Dentistry", "Surgery");
	}
}