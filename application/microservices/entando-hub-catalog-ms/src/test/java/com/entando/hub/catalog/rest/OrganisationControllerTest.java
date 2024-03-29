package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.dto.OrganisationDto;
import com.entando.hub.catalog.service.OrganisationService;
import com.entando.hub.catalog.service.mapper.OrganizationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class OrganisationControllerTest {

	@Autowired
	private OrganizationMapper organizationMapper;


	@Autowired
	private MockMvc mockMvc;
	@MockBean
	OrganisationService organisationService;

	private static final String URI = "/api/organisation/";
	private static final Long ORG_ID = 2000L;
	private static final String ORG_NAME = "Test Org Name";
	private static final String ORG_DESCRIOPTION = "Test Org Decription";

	private static final Long BUNDLE_GROUP_ID = 1000L;
	private static final String BUNDLE_GROUP_NAME = "Test Bundle Group Name";

	@Test
	void testGetOrganisations() throws Exception {
		Organisation organisation = getOrganisationObj();
		Mockito.when(organisationService.getOrganisations()).thenReturn(List.of(organisation));
		mockMvc.perform(MockMvcRequestBuilders.get(URI).accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.[*].organisationId").value(organisation.getId().toString()))
				.andExpect(jsonPath("$.[*].name").value(organisation.getName())).andReturn();
	}

	@Test
	void testGetOrganisation() throws Exception {
		Organisation organisation = getOrganisationObj();
		Long organisationId = organisation.getId();
		Mockito.when(organisationService.getOrganisation(organisationId)).thenReturn(Optional.of(organisation));
		mockMvc.perform(MockMvcRequestBuilders.get(URI + organisationId).accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.organisationId").value(organisation.getId().toString()))
				.andExpect(jsonPath("$.name").value(organisation.getName()));
	}

	@Test
	void testGetOrganisationFails() throws Exception {
		Organisation organisation = getOrganisationObj();
		Long organisationId = organisation.getId();
		Mockito.when(organisationService.getOrganisation(null)).thenReturn(Optional.of(organisation));
		mockMvc.perform(MockMvcRequestBuilders.get(URI + organisationId)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound());

	}

	@Test
	@WithMockUser(roles = { ADMIN })
	void testCreateOrganisation() throws Exception {
		Organisation organisation = getOrganisationObj();
		BundleGroup bundleGroup = getBundleGroupObj();
		organisation.setBundleGroups(Set.of(bundleGroup));
		OrganisationDto organisationNoId = new OrganisationDto();
		organisationNoId.setName(organisationNoId.getName());
		organisationNoId.setDescription(organisationNoId.getDescription());

		Organisation entity = organizationMapper.toEntity(organisationNoId);
		assertNull(entity.getId());
		assertNull(organisationNoId.getOrganisationId());
		Mockito.when(organisationService.createOrganisation(entity, organisationNoId)).thenReturn(organisation);
		mockMvc.perform(MockMvcRequestBuilders.post(URI).contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(organisationNoId)).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.organisationId").value(organisation.getId().toString()))
				.andExpect(jsonPath("$.name").value(organisation.getName()));

	}

	@Test
	@WithMockUser(roles = { ADMIN })
	void testUpdateOrganisation() throws Exception {
		Organisation organisation = getOrganisationObj();
		BundleGroup bundleGroup = getBundleGroupObj();
		organisation.setBundleGroups(Set.of(bundleGroup));
		Long organisationId = organisation.getId();
		OrganisationDto organisationNoId = new OrganisationDto(); // new OrganisationDto(organisation.getName(), organisation.getDescription());
		organisationNoId.setName(organisation.getName());
		organisationNoId.setDescription(organisation.getDescription());
		// we have the organization id!
		organisationNoId.setOrganisationId(organisationId.toString());

		Mockito.when(organisationService.getOrganisation(organisationId)).thenReturn(Optional.of(organisation));

		Organisation entity = organizationMapper.toEntity(organisationNoId);
		assertNotNull(entity.getId());
		assertNotNull(organisationNoId.getOrganisationId());
		Mockito.when(organisationService.createOrganisation(entity,organisationNoId)).thenReturn(organisation);

		mockMvc.perform(MockMvcRequestBuilders.post(URI + organisationId).contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(organisationNoId)).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.organisationId").value(organisation.getId().toString()))
				.andExpect(jsonPath("$.name").value(organisation.getName()));
	}

	@Test
	@WithMockUser(roles = { ADMIN })
	void testUpdateOrganisationFails() throws Exception {
		Organisation organisation = getOrganisationObj();
		BundleGroup bundleGroup = getBundleGroupObj();
		organisation.setBundleGroups(Set.of(bundleGroup));
		Long organisationId = organisation.getId();
		OrganisationDto organisationNoId = new OrganisationDto(null, organisation.getName(), organisation.getDescription());

		Mockito.when(organisationService.getOrganisation(organisation.getId())).thenReturn(Optional.empty());
		mockMvc.perform(MockMvcRequestBuilders.post(URI + organisationId).contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(organisationNoId)).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));

	}

	@Test
	@WithMockUser(roles = { ADMIN })
	void testDeleteOrganisation() throws Exception {
		Organisation organisation = getOrganisationObj();
		BundleGroup bundleGroup = getBundleGroupObj();
		organisation.setBundleGroups(Set.of(bundleGroup));
		Long organisationId = organisation.getId();
		Mockito.when(organisationService.getOrganisation(organisationId)).thenReturn(Optional.of(organisation));
		organisationService.deleteOrganisation(organisationId);
		mockMvc.perform(MockMvcRequestBuilders.delete(URI + organisationId).accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNoContent());
	}

	@Test
	@WithMockUser(roles = { ADMIN })
	void testDeleteOrganisationFails() throws Exception {
		Organisation organisation = getOrganisationObj();
		Long organisationId = organisation.getId();
		Mockito.when(organisationService.getOrganisation(null)).thenReturn(Optional.of(organisation));
		Mockito.when(organisationService.getOrganisation(organisationId)).thenReturn(Optional.empty());
		organisationService.deleteOrganisation(organisationId);
		mockMvc.perform(MockMvcRequestBuilders.delete(URI + organisationId).accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNotFound());
	}

	private Organisation getOrganisationObj() {
		Organisation organisation = new Organisation();
		organisation.setId(ORG_ID);
		organisation.setName(ORG_NAME);
		organisation.setDescription(ORG_DESCRIOPTION);
		return organisation;
	}

	private BundleGroup getBundleGroupObj() {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(BUNDLE_GROUP_ID);
		bundleGroup.setName(BUNDLE_GROUP_NAME);
		return bundleGroup;
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
