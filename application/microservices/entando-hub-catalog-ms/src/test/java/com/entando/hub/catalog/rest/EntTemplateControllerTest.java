package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.ApplicationConstants.API_KEY_HEADER;
import static com.entando.hub.catalog.config.ApplicationConstants.CATALOG_ID_PARAM;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.CatalogService;
import com.entando.hub.catalog.service.KeycloakService;
import com.entando.hub.catalog.service.PrivateCatalogApiKeyService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class EntTemplateControllerTest {

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
    private static final String API_KEY = "api-key";
    private static final Long CATALOG_ID = 1L;
    private static final Long INVALID_CATALOG_ID = 2L;
    private static final String BUNDLES_URI = "/ent/api/templates/bundles";
    private static final String BUNDLE_GROUPS_URI = "/ent/api/templates/bundlegroups";
    private static final String SRC_REPO_ADDRESS = "SRC_REPO_ADDRESS";
    private static final String BUNDLE_GROUP_NAME = "BUNDLE_GROUP_NAME";
    private static final String BUNDLE_GROUP_NAME_PARAM = "name";
    private static final String BUNDLE_NAME = "BUNDLE_NAME";
    private static final Long BUNDLE_ID = 1L;
    private static final Long BUNDLE_GROUP_ID = 2L;
    private static final Long  BUNDLE_GROUP_VERSION_ID = 2L;
    private static final String BUNDLE_GROUPS_BY_ID_URI = "/ent/api/templates/bundlegroups/";


    @Test
    void testGetPrivateCatalogPublishedBundleTemplates() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        List<BundleGroupVersion> bundleTemplateList = createBundleTemplateList();
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
        Mockito.when(catalogService.exist(CATALOG_ID)).thenReturn(true);
        Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(true);
        Mockito.when(bundleGroupVersionService.getPrivateCatalogPublishedBundleTemplates(CATALOG_ID)).thenReturn(bundleTemplateList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLES_URI)
                        .header(API_KEY_HEADER, API_KEY)
                        .param(CATALOG_ID_PARAM, CATALOG_ID.toString())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleId").value(BUNDLE_ID))
                .andExpect(jsonPath("$.[0].bundleGroupId").value(BUNDLE_GROUP_ID))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(BUNDLE_GROUP_VERSION_ID))
                .andExpect(jsonPath("$.[0].gitSrcRepoAddress").value(SRC_REPO_ADDRESS))
                .andExpect(jsonPath("$.[0].bundleName").value(BUNDLE_NAME))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(BUNDLE_GROUP_NAME));
    }

    @Test
    void testGetPublicCatalogPublishedBundleTemplates() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        List<BundleGroupVersion> bundleTemplateDtoList = createBundleTemplateList();
        Mockito.when(bundleGroupVersionService.getPublicCatalogPublishedBundleTemplates()).thenReturn(bundleTemplateDtoList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLES_URI)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleId").value(BUNDLE_ID))
                .andExpect(jsonPath("$.[0].bundleGroupId").value(BUNDLE_GROUP_ID))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(BUNDLE_GROUP_VERSION_ID))
                .andExpect(jsonPath("$.[0].gitSrcRepoAddress").value(SRC_REPO_ADDRESS))
                .andExpect(jsonPath("$.[0].bundleName").value(BUNDLE_NAME))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(BUNDLE_GROUP_NAME));
    }

    @Test
    void testGetBundlesTemplatesApiKeyNotExists() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
        Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLES_URI)
                        .header(API_KEY_HEADER, API_KEY)
                        .param(CATALOG_ID_PARAM, String.valueOf(CATALOG_ID))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetBundlesTemplatesApiKeyCatalogUnauthorized() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
        Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLES_URI)
                        .header(API_KEY_HEADER, API_KEY)
                        .param(CATALOG_ID_PARAM, String.valueOf(INVALID_CATALOG_ID))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetPrivateCatalogPublishedBundleGroupsTemplates() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        List<BundleGroupVersion> bundleGroupTemplateList = createBundleGroupTemplateList();
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
        Mockito.when(catalogService.exist(CATALOG_ID)).thenReturn(true);
        Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(true);
        Mockito.when(bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupTemplates(CATALOG_ID)).thenReturn(bundleGroupTemplateList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_URI)
                        .header(API_KEY_HEADER, API_KEY)
                        .param(CATALOG_ID_PARAM, CATALOG_ID.toString())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(BUNDLE_GROUP_NAME))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(BUNDLE_GROUP_VERSION_ID));
    }

    @Test
    void testGetPublicCatalogPublishedBundleGroupsTemplates() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        List<BundleGroupVersion> bundleGroupTemplateDtoList = createBundleGroupTemplateList();
        Mockito.when(bundleGroupVersionService.getPublicCatalogPublishedBundleGroupTemplates()).thenReturn(bundleGroupTemplateDtoList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_URI)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(BUNDLE_GROUP_NAME))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(BUNDLE_GROUP_VERSION_ID));
    }

    @Test
    void testGetPrivateCatalogPublishedBundleGroupsTemplatesByName() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        List<BundleGroupVersion> bundleGroupTemplateDtoList = createBundleGroupTemplateList();
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
        Mockito.when(catalogService.exist(CATALOG_ID)).thenReturn(true);
        Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(true);
        Mockito.when(bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupTemplatesByName(CATALOG_ID,BUNDLE_GROUP_NAME))
                .thenReturn(bundleGroupTemplateDtoList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_URI)
                        .header(API_KEY_HEADER, API_KEY)
                        .param(CATALOG_ID_PARAM, CATALOG_ID.toString())
                        .param(BUNDLE_GROUP_NAME_PARAM, BUNDLE_GROUP_NAME)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(BUNDLE_GROUP_NAME))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(BUNDLE_GROUP_VERSION_ID));
    }

    @Test
    void testGetPublicCatalogPublishedBundleGroupsTemplatesByName() throws Exception {
        List<BundleGroupVersion> bundleGroupTemplateList = createBundleGroupTemplateList();
        Mockito.when(bundleGroupVersionService.getPublicCatalogPublishedBundleGroupTemplatesByName(BUNDLE_GROUP_NAME))
                .thenReturn(bundleGroupTemplateList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_URI)
                        .param(BUNDLE_GROUP_NAME_PARAM, BUNDLE_GROUP_NAME)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(BUNDLE_GROUP_NAME))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(BUNDLE_GROUP_VERSION_ID));
    }

    @Test
    void testGetPublicCatalogPublishedBundleGroupsTemplatesFilteredByName() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        List<BundleGroupVersion> bundleGroupTemplateDtoList = createBundleGroupTemplateList();
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
        Mockito.when(bundleGroupVersionService.getPublicCatalogPublishedBundleGroupTemplates()).thenReturn(bundleGroupTemplateDtoList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_URI)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(BUNDLE_GROUP_NAME))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(BUNDLE_GROUP_VERSION_ID));
    }

    @Test
    void testGetPrivateCatalogPublishedBundleTemplatesByID() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        List<BundleGroupVersion> bundleTemplateList = createBundleTemplateList();
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
        Mockito.when(catalogService.exist(CATALOG_ID)).thenReturn(true);
        Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(true);
        Mockito.when(bundleGroupVersionService.getPrivateCatalogPublishedBundleTemplatesById(CATALOG_ID, BUNDLE_GROUP_ID)).thenReturn(bundleTemplateList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_BY_ID_URI+BUNDLE_GROUP_ID)
                        .header(API_KEY_HEADER, API_KEY)
                        .param(CATALOG_ID_PARAM, CATALOG_ID.toString())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleId").value(BUNDLE_ID))
                .andExpect(jsonPath("$.[0].bundleGroupId").value(BUNDLE_GROUP_ID))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(BUNDLE_GROUP_VERSION_ID))
                .andExpect(jsonPath("$.[0].gitSrcRepoAddress").value(SRC_REPO_ADDRESS))
                .andExpect(jsonPath("$.[0].bundleName").value(BUNDLE_NAME))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(BUNDLE_GROUP_NAME));

    }

    @Test
    void testGetPublicCatalogPublishedBundleTemplatesByID() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        List<BundleGroupVersion> bundleTemplateList = createBundleTemplateList();
        Mockito.when(bundleGroupVersionService.getPublicCatalogPublishedBundleTemplatesById(BUNDLE_GROUP_ID)).thenReturn(bundleTemplateList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_BY_ID_URI+BUNDLE_GROUP_ID)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleId").value(BUNDLE_ID))
                .andExpect(jsonPath("$.[0].bundleGroupId").value(BUNDLE_GROUP_ID))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(BUNDLE_GROUP_VERSION_ID))
                .andExpect(jsonPath("$.[0].gitSrcRepoAddress").value(SRC_REPO_ADDRESS))
                .andExpect(jsonPath("$.[0].bundleName").value(BUNDLE_NAME))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(BUNDLE_GROUP_NAME));
    }

    @Test
    void testGetBundleTemplatesByIDEmptyList1() throws Exception {
        List<BundleGroupVersion> bundleTemplateList = createBundleTemplateList();
        Mockito.when(bundleGroupVersionService.getPublicCatalogPublishedBundleTemplatesById(BUNDLE_GROUP_ID)).thenReturn(bundleTemplateList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_BY_ID_URI)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void testGetBundleTemplatesByIDWithApiKeyEmptyList() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        List<BundleGroupVersion> bundleTemplateList = createBundleTemplateList();
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
        Mockito.when(catalogService.exist(CATALOG_ID)).thenReturn(true);
        Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(true);
        Mockito.when(bundleGroupVersionService.getPublicCatalogPublishedBundleTemplatesById(BUNDLE_GROUP_ID)).thenReturn(bundleTemplateList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_BY_ID_URI)
                        .header(API_KEY_HEADER, API_KEY)
                        .param(CATALOG_ID_PARAM, String.valueOf(CATALOG_ID))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }


    @Test
    void testGetBundlesTemplatesByIDApiKeyNotExists() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
        Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_BY_ID_URI+BUNDLE_GROUP_ID)
                        .header(API_KEY_HEADER, API_KEY)
                        .param(CATALOG_ID_PARAM, String.valueOf(CATALOG_ID))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetBundlesTemplatesByIDApiKeyCatalogUnauthorized() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
        Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_BY_ID_URI+BUNDLE_GROUP_ID)
                        .header(API_KEY_HEADER, API_KEY)
                        .param(CATALOG_ID_PARAM, String.valueOf(INVALID_CATALOG_ID))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetBundleGroupsTemplatesApiKeyNotExists() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
        Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_URI)
                        .header(API_KEY_HEADER, API_KEY)
                        .param(CATALOG_ID_PARAM, String.valueOf(CATALOG_ID))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetBundleGroupsTemplatesApiKeyCatalogUnauthorized() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
        Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_URI)
                        .header(API_KEY_HEADER, API_KEY)
                        .param(CATALOG_ID_PARAM, String.valueOf(INVALID_CATALOG_ID))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    private List<BundleGroupVersion> createBundleGroupTemplateList() {
        List<BundleGroupVersion> bundleGroupTemplateList = new ArrayList<>();
        bundleGroupTemplateList.add(createBundleGroupTemplate(BUNDLE_GROUP_NAME, BUNDLE_GROUP_VERSION_ID));
        return bundleGroupTemplateList;
    }

    private BundleGroupVersion createBundleGroupTemplate(String bundleGroupName,Long bundleGroupVersionId) {
        BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
        BundleGroup bundleGroup = new BundleGroup();
        bundleGroup.setName(bundleGroupName);
        bundleGroupVersion.setId(bundleGroupVersionId);
        bundleGroupVersion.setBundleGroup(bundleGroup);
        return bundleGroupVersion;
    }


    private List<BundleGroupVersion> createBundleTemplateList() {
        List<BundleGroupVersion> bundleTemplateList= new ArrayList<>();
        bundleTemplateList.add(createBundleTemplate(BUNDLE_GROUP_VERSION_ID, BUNDLE_GROUP_ID, BUNDLE_ID));
        return bundleTemplateList;
    }

    private BundleGroupVersion createBundleTemplate(Long bundleGroupVersionId, Long bundleGroupId, Long bundleId) {
        BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
        Bundle bundle = new Bundle();
        BundleGroup bundleGroup = new BundleGroup();
        bundleGroupVersion.setId(bundleGroupVersionId);
        bundle.setId(bundleId);
        bundle.setGitSrcRepoAddress(SRC_REPO_ADDRESS);
        bundleGroup.setName(BUNDLE_GROUP_NAME);
        bundle.setName(BUNDLE_NAME);
        bundleGroup.setId(bundleGroupId);
        Set<Bundle> bundleSet = new HashSet<>();
        bundleSet.add(bundle);
        bundleGroupVersion.setBundles(bundleSet);
        bundleGroupVersion.setBundleGroup(bundleGroup);
        return bundleGroupVersion;
    }

}
