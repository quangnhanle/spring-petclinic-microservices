package org.springframework.samples.petclinic.vets.system;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VetsPropertiesTest {

	@Test
	void shouldExposeCacheProperties() {
		VetsProperties properties = new VetsProperties(new VetsProperties.Cache(10, 25));

		assertThat(properties.cache()).isNotNull();
		assertThat(properties.cache().ttl()).isEqualTo(10);
		assertThat(properties.cache().heapSize()).isEqualTo(25);
	}
}