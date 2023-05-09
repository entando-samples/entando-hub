package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.ApplicationConstants.API_KEY_HEADER;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.entando.hub.catalog.rest.helpers.RoleMappingsRepresentationTestHelper;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.CatalogService;
import com.entando.hub.catalog.service.KeycloakService;
import com.entando.hub.catalog.service.PrivateCatalogApiKeyService;
import com.entando.hub.catalog.service.dto.BundleGroupVersionEntityDto;
import com.entando.hub.catalog.service.mapper.BundleGroupVersionMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class AppBuilderBundleGroupsControllerTest {


	@Autowired
	private BundleGroupVersionMapper bundleGroupVersionMapper;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	BundleGroupVersionService bundleGroupVersionService;

	@MockBean
	PrivateCatalogApiKeyService privateCatalogApiKeyService;
	@MockBean
	CatalogService catalogService;
	@MockBean
	KeycloakService keycloakService;
	private final Long BUNDLE_GROUP_VERSION_ID =  2001L;
	private final Long BUNDLE_GROUPID =  2002L;
	private final Long BUNDLE_ID =  2004L;
	private final Long ORG_ID =  2005L;
	private final String NAME = "New Name";
	private final String DESCRIPTION = "New Description";
	private final String DESCRIPTION_IMAGE = "New Description Image";
	private final String DOCUMENTATION_URL = "New Documentation Url";
	private final String VERSION = "V1.V2";
	private final String GIT_REPO_ADDRESS = "Test Git Rep";
	private final String DEPENDENCIES = "Test Dependencies";
	private static final String API_KEY = "api-key";
	private static final String CATALOG_ID_PARAM = "catalogId";
	private static final Long CATALOG_ID = 1L;
	private static final String CLIENT_NAME = "internal";
	@Test
	void getBundleGroupVersionsTest() throws Exception {

		List<BundleGroupVersion> bundleGroupVersionsList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		bundleGroupVersionsList.add(bundleGroupVersion);
		Integer page = 0;
		Integer pageSize = 89;
		List<BundleGroupVersionFilteredResponseView> list = new ArrayList<>();
		BundleGroupVersionFilteredResponseView viewObj = new BundleGroupVersionFilteredResponseView();
		viewObj.setBundleGroupVersionId(bundleGroupVersion.getId());
		viewObj.setDescription(bundleGroupVersion.getDescription());
		viewObj.setStatus(bundleGroupVersion.getStatus());
		viewObj.setVersion(bundleGroupVersion.getVersion());
		list.add(viewObj);

		String inputJsonPage = mapToJson(page);
		String inputJsonPageSize = mapToJson(pageSize);

		Page<BundleGroupVersion> response = new PageImpl<>(bundleGroupVersionsList);
		PageImpl<BundleGroupVersionEntityDto> responseDto = convertoToDto(response);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> pagedContent = new PagedContent<>(list, responseDto);

		//Case 1: api key is not passed as parameter
		Mockito.when(bundleGroupVersionService.getPublicCatalogPublishedBundleGroupVersions(page, pageSize)).thenReturn(pagedContent);
		mockMvc.perform(MockMvcRequestBuilders.get("/appbuilder/api/bundlegroups/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("page", inputJsonPage)
				.param("pageSize", inputJsonPageSize))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.payload.[*].description").value(bundleGroupVersion.getDescription()))
				.andExpect(jsonPath("$.payload.[*].version").value(bundleGroupVersion.getVersion()));

		//Case 2: when passing a valid api-key
		Long userCatalogId = 1L;
		Catalog catalog = new Catalog();
		catalog.setId(userCatalogId);
		Mockito.when(keycloakService.getRolesByUsername(any())).thenReturn(RoleMappingsRepresentationTestHelper.getMockRoleMappingsRepresentation(CLIENT_NAME, false));
		Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
		Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(true);
		Mockito.when(bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupVersions(userCatalogId, page, pageSize)).thenReturn(pagedContent);
		mockMvc.perform(MockMvcRequestBuilders.get("/appbuilder/api/bundlegroups/")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.header(API_KEY_HEADER, API_KEY)
						.param(CATALOG_ID_PARAM, String.valueOf(CATALOG_ID))
						.param("page", inputJsonPage)
						.param("pageSize", inputJsonPageSize))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.payload.[*].description").value(bundleGroupVersion.getDescription()))
				.andExpect(jsonPath("$.payload.[*].version").value(bundleGroupVersion.getVersion()));

		//Case 3: when passing a valid admin api-key, any catalog id is fine
		Mockito.when(keycloakService.getRolesByUsername(any())).thenReturn(RoleMappingsRepresentationTestHelper.getMockRoleMappingsRepresentation(CLIENT_NAME, true));
		Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(true);
		Mockito.when(bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupVersions(userCatalogId, page, pageSize)).thenReturn(pagedContent);
		mockMvc.perform(MockMvcRequestBuilders.get("/appbuilder/api/bundlegroups/")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.header(API_KEY_HEADER, API_KEY)
						.param(CATALOG_ID_PARAM, String.valueOf(CATALOG_ID))
						.param("catalogId", "150")
						.param("page", inputJsonPage)
						.param("pageSize", inputJsonPageSize))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.payload.[*].description").value(bundleGroupVersion.getDescription()))
				.andExpect(jsonPath("$.payload.[*].version").value(bundleGroupVersion.getVersion()));

		//Case 4: when passing an invalid api-key returns Unauthorized
		Mockito.when(keycloakService.getRolesByUsername(any())).thenReturn(RoleMappingsRepresentationTestHelper.getMockRoleMappingsRepresentation(CLIENT_NAME, false));
		Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
		Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(false);
		Mockito.when(bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupVersions(userCatalogId, page, pageSize)).thenReturn(pagedContent);

		mockMvc.perform(MockMvcRequestBuilders.get("/appbuilder/api/bundlegroups/")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.header(API_KEY_HEADER, API_KEY)
						.param("page", inputJsonPage)
						.param("pageSize", inputJsonPageSize))
				.andExpect(status().isUnauthorized());

		//Case 5: when passing an valid api-key without catalogId returns Unauthorized
		Mockito.when(keycloakService.getRolesByUsername(any())).thenReturn(RoleMappingsRepresentationTestHelper.getMockRoleMappingsRepresentation(CLIENT_NAME, true));
		Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(true);
		Mockito.when(bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupVersions(userCatalogId, page, pageSize)).thenReturn(pagedContent);

		mockMvc.perform(MockMvcRequestBuilders.get("/appbuilder/api/bundlegroups/")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.header(API_KEY_HEADER, API_KEY)
						.param("page", inputJsonPage)
						.param("pageSize", inputJsonPageSize))
				.andExpect(status().isUnauthorized());

	}

	private String mapToJson(Object obj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}

	private BundleGroupVersion createBundleGroupVersion() {
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(BUNDLE_GROUP_VERSION_ID);
		bundleGroupVersion.setDescription(DESCRIPTION);
		bundleGroupVersion.setDescriptionImage(DESCRIPTION_IMAGE);
		bundleGroupVersion.setDocumentationUrl(DOCUMENTATION_URL);
		bundleGroupVersion.setVersion(VERSION);
		bundleGroupVersion.setStatus(Status.PUBLISHED);
		BundleGroup bundleGroup = createBundleGroup();
		bundleGroupVersion.setBundleGroup(bundleGroup);
		Bundle bundle = createBundle();
		bundle.setBundleGroupVersions(Set.of(bundleGroupVersion));
		bundleGroupVersion.setBundles(Set.of(bundle));
		return bundleGroupVersion;
	}

	private BundleGroup createBundleGroup() {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(BUNDLE_GROUPID);
		bundleGroup.setName(NAME);
		Organisation organisation = createOrganisation();
		bundleGroup.setOrganisation(organisation);
		return bundleGroup;
	}

	private Bundle createBundle() {
		Bundle bundle = new Bundle();
		bundle.setId(BUNDLE_ID);
		bundle.setName(NAME);
		bundle.setDescription(DESCRIPTION);
		bundle.setGitRepoAddress(GIT_REPO_ADDRESS);
		bundle.setDependencies(DEPENDENCIES);
		return bundle;
	}

	private Organisation createOrganisation() {
		Organisation organisation = new Organisation();
		organisation.setId(ORG_ID);
		organisation.setName(NAME);
		organisation.setDescription(DEPENDENCIES);
		return organisation;
	}

	private PageImpl<BundleGroupVersionEntityDto> convertoToDto(Page<BundleGroupVersion> page) {
		return new PageImpl<>(page.getContent()
				.stream()
				.map(e -> bundleGroupVersionMapper.toEntityDto(e))
				.collect(Collectors.toList()),
				page.getPageable(), page.getNumberOfElements());
	}

}
