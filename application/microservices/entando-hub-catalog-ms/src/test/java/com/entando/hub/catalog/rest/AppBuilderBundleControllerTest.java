package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.ApplicationConstants.API_KEY_HEADER;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.persistence.entity.DescriptorVersion;
import com.entando.hub.catalog.rest.dto.BundleDto;
import com.entando.hub.catalog.rest.helpers.RoleMappingsRepresentationTestHelper;
import com.entando.hub.catalog.service.*;
import com.entando.hub.catalog.service.mapper.BundleMapper;
import com.entando.hub.catalog.service.mapper.BundleMapperImpl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
class AppBuilderBundleControllerTest {

    @Autowired
    private BundleMapper bundleMapper = new BundleMapperImpl();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    BundleService bundleService;

    @MockBean
    BundleGroupVersionService bundleGroupVersionService;

    @MockBean
    PrivateCatalogApiKeyService privateCatalogApiKeyService;

    @MockBean
    private CatalogService catalogService;

    @MockBean
    private KeycloakService keycloakService;

    private static final String URI = "/appbuilder/api/bundles/";
    private static final String PAGE_PARAM = "page";
    private static final String PAGE_SIZE_PARAM = "pageSize";
    private static final String DESCRIPTOR_VERSIONS = "descriptorVersions";

    private static final Long BUNDLE_GROUP_ID = 1000L;
    private static final String BUNDLE_GROUP_NAME = "Test Bundle Group Name";

    private static final Long BUNDLE_GROUP_VERSION_ID = 1002L;
    private static final String BUNDLE_GROUP_VERSION_DESCRIPTION = "Test Bundle Group Version Decription";
    private static final String BUNDLE_GROUP_VERSION_VERSION = "v1.0.0";

    private static final Long BUNDLE_ID = 1001L;
    private static final String BUNDLE_NAME = "Test Bundle Name";
    private static final String BUNDLE_DESCRIPTION = "Test Bundle Decription";
    private static final String BUNDLE_GIT_REPO_ADDRESS = "https://github.com/entando/TEST-portal.git";
    private static final String BUNDLE_DEPENDENCIES = "Test Dependencies";

    private static final String API_KEY = "api-key";
    private static final String CATALOG_ID_PARAM = "catalogId";
    private static final Long CATALOG_ID = 1L;
    private static final String CLIENT_NAME = "internal";

    @Test
    void getBundlesTest() throws Exception {
        Integer page = 0;
        Integer pageSize = 89;
        BundleGroup bundleGroup = getBundleGroupObj();
        String bundleGroupId = bundleGroup.getId().toString();

        BundleGroupVersion bundleGroupVersion = getBundleGroupVersionObj();
        List<Bundle> bundlesList = new ArrayList<>();
        Bundle bundle = getBundleObj();
        bundlesList.add(bundle);

        BundleDto bundleC = bundleMapper.toDto(bundle);
        List<BundleDto> bundlesCList = new ArrayList<>();
        bundlesCList.add(bundleC);

        Page<Bundle> response = new PageImpl<>(bundlesList);

        Set<DescriptorVersion> versions = new HashSet<>();
        versions.add(DescriptorVersion.V5);

        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);

        //BundleGroupId not provided, page = 0, bundle has null versions
        Mockito.when(bundleService.getBundles(page, pageSize, null, versions, null))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get(URI)
                        .param(PAGE_PARAM, page.toString())
                        .param(PAGE_SIZE_PARAM, pageSize.toString()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.payload").exists())
                .andExpect(jsonPath("$.metadata").exists())
                .andExpect(status().isOk());

        //Bundle has a version
        bundle.setBundleGroupVersions(Set.of(bundleGroupVersion));
        mockMvc.perform(MockMvcRequestBuilders.get(URI)
                        .param(PAGE_PARAM, page.toString())
                        .param(PAGE_SIZE_PARAM, pageSize.toString()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.payload").exists())
                .andExpect(jsonPath("$.metadata").exists())
                .andExpect(status().isOk());

        //BundleGroupId provided
        Mockito.when(bundleService.getBundles(page, pageSize, bundleGroupId, versions, null))
                .thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.get(URI)
                        .queryParam("bundleGroupId", bundleGroupId)
                        .param(PAGE_PARAM, page.toString())
                        .param(PAGE_SIZE_PARAM, pageSize.toString()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.payload").hasJsonPath())
                .andExpect(jsonPath("$.metadata").hasJsonPath())
                .andExpect(status().isOk());

		//bundleGroupId is empty and passing a valid admin api-key
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
		Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(true);
        Mockito.when(keycloakService.getRolesByUsername(any())).thenReturn(RoleMappingsRepresentationTestHelper.getMockRoleMappingsRepresentation(CLIENT_NAME, true));

		Mockito.when(bundleService.getBundles(page, pageSize, null, versions, CATALOG_ID)).thenReturn(response);
		mockMvc.perform(MockMvcRequestBuilders.get(URI).
						header(API_KEY_HEADER, API_KEY).
                        param(CATALOG_ID_PARAM, String.valueOf(CATALOG_ID)).
						param(PAGE_PARAM, page.toString()).
						param(PAGE_SIZE_PARAM, pageSize.toString())).
				andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).
				andExpect(jsonPath("$.payload").exists()).
				andExpect(jsonPath("$.metadata").exists()).
				andExpect(status().isOk());

        //When passing an invalid api-key should return Unauthorized
		Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(false);
		mockMvc.perform(MockMvcRequestBuilders.get(URI).
						contentType(MediaType.APPLICATION_JSON_VALUE).
						header(API_KEY_HEADER, API_KEY).
						param(PAGE_PARAM, page.toString()).
						param(PAGE_SIZE_PARAM, pageSize.toString())).
				andExpect(status().isUnauthorized());

        //When passing a valid api-key but catalogId is null should return Unauthorized
        Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.get(URI).
                        contentType(MediaType.APPLICATION_JSON_VALUE).
                        header(API_KEY_HEADER, API_KEY).
                        param(PAGE_PARAM, page.toString()).
                        param(PAGE_SIZE_PARAM, pageSize.toString())).
                andExpect(status().isUnauthorized());

        //Provide one more good descriptorVersion as well as a bad one (which should be excluded).
        versions.add(DescriptorVersion.V1);
        bundle.setBundleGroupVersions(Set.of(bundleGroupVersion));
        Mockito.when(bundleService.getBundles(page, pageSize, null, versions, null))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get(URI)
                        .param(PAGE_PARAM, page.toString())
                        .param(PAGE_SIZE_PARAM, pageSize.toString())
                        .param(DESCRIPTOR_VERSIONS, "v1")
                        .param(DESCRIPTOR_VERSIONS, "v5")
                        .param(DESCRIPTOR_VERSIONS, "vInvalid"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.payload").exists())
                .andExpect(jsonPath("$.metadata").exists())
                .andExpect(status().isOk());
    }

	private Bundle getBundleObj() {
        Bundle bundle = new Bundle();
        bundle.setId(BUNDLE_ID);
        bundle.setName(BUNDLE_NAME);
        bundle.setDescription(BUNDLE_DESCRIPTION);
        bundle.setGitRepoAddress(BUNDLE_GIT_REPO_ADDRESS);
        bundle.setDependencies(BUNDLE_DEPENDENCIES);
        bundle.setBundleGroupVersions(new HashSet<>());
        return bundle;
    }

    private BundleGroup getBundleGroupObj() {
        BundleGroup bundleGroup = new BundleGroup();
        bundleGroup.setId(BUNDLE_GROUP_ID);
        bundleGroup.setName(BUNDLE_GROUP_NAME);
        return bundleGroup;
    }

    private BundleGroupVersion getBundleGroupVersionObj() {
        BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
        bundleGroupVersion.setId(BUNDLE_GROUP_VERSION_ID);
        bundleGroupVersion.setDescription(BUNDLE_GROUP_VERSION_DESCRIPTION);
        bundleGroupVersion.setStatus(BundleGroupVersion.Status.PUBLISHED);
        bundleGroupVersion.setVersion(BUNDLE_GROUP_VERSION_VERSION);
        return bundleGroupVersion;
    }
}
