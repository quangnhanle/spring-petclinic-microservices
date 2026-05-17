package org.springframework.samples.petclinic.customers.web;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetRepository;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author Maciej Szarlinski
 */
@WebMvcTest(PetResource.class)
@ActiveProfiles("test")
class PetResourceTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    PetRepository petRepository;

    @MockitoBean
    OwnerRepository ownerRepository;

    @Test
    void shouldGetAPetInJSonFormat() throws Exception {

        Pet pet = setupPet();

        given(petRepository.findById(2)).willReturn(Optional.of(pet));


        mvc.perform(get("/owners/2/pets/2").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("Basil"))
            .andExpect(jsonPath("$.type.id").value(6));
    }

    @Test
    void shouldGetPetTypes() throws Exception {
        PetType cat = new PetType();
        cat.setId(7);
        cat.setName("cat");

        PetType dog = new PetType();
        dog.setId(6);
        dog.setName("dog");

        given(petRepository.findPetTypes()).willReturn(java.util.List.of(cat, dog));

        mvc.perform(get("/petTypes").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(7))
            .andExpect(jsonPath("$[0].name").value("cat"))
            .andExpect(jsonPath("$[1].id").value(6))
            .andExpect(jsonPath("$[1].name").value("dog"));
    }

    @Test
    void shouldCreatePet() throws Exception {
        PetType petType = new PetType();
        petType.setId(6);
        petType.setName("dog");

        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");

        given(ownerRepository.findById(2)).willReturn(Optional.of(owner));
        given(petRepository.findPetTypeById(6)).willReturn(Optional.of(petType));
        given(petRepository.save(any(Pet.class))).willAnswer(invocation -> invocation.getArgument(0));

        mvc.perform(post("/owners/2/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"birthDate\":\"2010-01-02\",\"name\":\"Basil\",\"typeId\":6}"))
            .andExpect(status().isCreated())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.name").value("Basil"))
            .andExpect(jsonPath("$.type.id").value(6));
    }

    @Test
    void shouldUpdatePet() throws Exception {
        PetType petType = new PetType();
        petType.setId(6);
        petType.setName("dog");

        Pet pet = setupPet();

        given(petRepository.findById(2)).willReturn(Optional.of(pet));
        given(petRepository.findPetTypeById(6)).willReturn(Optional.of(petType));
        given(petRepository.save(any(Pet.class))).willAnswer(invocation -> invocation.getArgument(0));

        mvc.perform(put("/owners/2/pets/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":2,\"birthDate\":\"2010-01-02\",\"name\":\"Updated Basil\",\"typeId\":6}"))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenPetDoesNotExist() throws Exception {
        given(petRepository.findById(2)).willReturn(Optional.empty());

        mvc.perform(get("/owners/2/pets/2").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenOwnerDoesNotExist() throws Exception {
        given(ownerRepository.findById(2)).willReturn(Optional.empty());

        mvc.perform(post("/owners/2/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                "birthDate": "2010-01-02",
                "name": "Basil",
                "typeId": 6
                }
                """))
            .andExpect(status().isNotFound());
    }

    private Pet setupPet() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");

        Pet pet = new Pet();

        pet.setName("Basil");
        pet.setId(2);

        PetType petType = new PetType();
        petType.setId(6);
        pet.setType(petType);

        owner.addPet(pet);
        return pet;
    }
}
