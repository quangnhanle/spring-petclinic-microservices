package org.springframework.samples.petclinic.customers.web;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.web.mapper.OwnerEntityMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OwnerResource.class)
@Import(OwnerEntityMapper.class)
@ActiveProfiles("test")
class OwnerResourceTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    OwnerRepository ownerRepository;

    @Test
    void shouldCreateOwner() throws Exception {
        given(ownerRepository.save(any(Owner.class))).willAnswer(invocation -> invocation.getArgument(0));

        mvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"George\",\"lastName\":\"Bush\",\"address\":\"1600 Pennsylvania Ave\",\"city\":\"Washington\",\"telephone\":\"1234567890\"}"))
            .andExpect(status().isCreated())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.firstName").value("George"))
            .andExpect(jsonPath("$.lastName").value("Bush"))
            .andExpect(jsonPath("$.address").value("1600 Pennsylvania Ave"))
            .andExpect(jsonPath("$.city").value("Washington"))
            .andExpect(jsonPath("$.telephone").value("1234567890"));
    }

    @Test
    void shouldFindOwner() throws Exception {
        given(ownerRepository.findById(1)).willReturn(Optional.of(ownerFixture()));

        mvc.perform(get("/owners/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.firstName").value("George"))
            .andExpect(jsonPath("$.lastName").value("Bush"));
    }

    @Test
    void shouldFindAllOwners() throws Exception {
        given(ownerRepository.findAll()).willReturn(List.of(ownerFixture()));

        mvc.perform(get("/owners").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("George"))
            .andExpect(jsonPath("$[0].lastName").value("Bush"));
    }

    @Test
    void shouldUpdateOwner() throws Exception {
        given(ownerRepository.findById(1)).willReturn(Optional.of(ownerFixture()));
        given(ownerRepository.save(any(Owner.class))).willAnswer(invocation -> invocation.getArgument(0));

        mvc.perform(put("/owners/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"George\",\"lastName\":\"Bush\",\"address\":\"1600 Pennsylvania Ave\",\"city\":\"Washington\",\"telephone\":\"1234567890\"}"))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingOwner() throws Exception {
        given(ownerRepository.findById(1)).willReturn(Optional.empty());

        mvc.perform(put("/owners/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"George\",\"lastName\":\"Bush\",\"address\":\"1600 Pennsylvania Ave\",\"city\":\"Washington\",\"telephone\":\"1234567890\"}"))
            .andExpect(status().isNotFound());
    }

    private Owner ownerFixture() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");
        owner.setAddress("1600 Pennsylvania Ave");
        owner.setCity("Washington");
        owner.setTelephone("1234567890");
        return owner;
    }
}