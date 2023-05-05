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
import com.entando.hub.catalog.rest.dto.BundleGroupTemplateDto;
import com.entando.hub.catalog.rest.dto.BundleTemplateDto;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.CatalogService;
import com.entando.hub.catalog.service.PrivateCatalogApiKeyService;
import java.util.ArrayList;
import java.util.List;
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

    private final Long ID = 1001L;
    private final String ADMIN_USERNAME = "Admin";
    private static final String API_KEY = "api-key";
    private static final Long CATALOG_ID = 1L;
    private static final Long INVALID_CATALOG_ID = 2L;
    private static final String BUNDLES_URI = "/ent/api/templates/bundles";
    private static final String BUNDLE_GROUPS_URI = "/ent/api/templates/bundlegroups";
    private static final String SRC_REPO_ADDRESS = "SRC_REPO_ADDRESS";
    private static final String BUNDLE_GROUP_NAME = "BUNDLE_GROUP_NAME";
    private static final String BUNDLE_GROUP_NAME_PARAM = "name";
    private static final String BUNDLE_NAME = "BUNDLE_NAME";
    private static final Long BUNDLE_GROUP_ID = 1L;
    private static final String BUNDLE_GROUPS_BY_ID_URI = "/ent/api/templates/bundlegroups/";

    @Test
    void testGetPrivateCatalogPublishedBundleTemplates() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        List<BundleTemplateDto> bundleTemplateDtoList = createBundleTemplateDtoList();
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
        Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(true);
        Mockito.when(bundleGroupVersionService.getPrivateCatalogPublishedBundleTemplates(CATALOG_ID)).thenReturn(bundleTemplateDtoList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLES_URI)
                        .header(API_KEY_HEADER, API_KEY)
                        .param(CATALOG_ID_PARAM, CATALOG_ID.toString())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleId").value(bundleTemplateDtoList.get(0).getBundleId()))
                .andExpect(jsonPath("$.[0].bundleGroupId").value(bundleTemplateDtoList.get(0).getBundleGroupId()))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(bundleTemplateDtoList.get(0).getBundleGroupVersionId()))
                .andExpect(jsonPath("$.[0].gitSrcRepoAddress").value(bundleTemplateDtoList.get(0).getGitSrcRepoAddress()))
                .andExpect(jsonPath("$.[0].bundleName").value(bundleTemplateDtoList.get(0).getBundleName()))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(bundleTemplateDtoList.get(0).getBundleGroupName()));
    }

    @Test
    void testGetPublicCatalogPublishedBundleTemplates() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        List<BundleTemplateDto> bundleTemplateDtoList = createBundleTemplateDtoList();
        Mockito.when(bundleGroupVersionService.getPublicCatalogPublishedBundleTemplates()).thenReturn(bundleTemplateDtoList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLES_URI)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleId").value(bundleTemplateDtoList.get(0).getBundleId()))
                .andExpect(jsonPath("$.[0].bundleGroupId").value(bundleTemplateDtoList.get(0).getBundleGroupId()))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(bundleTemplateDtoList.get(0).getBundleGroupVersionId()))
                .andExpect(jsonPath("$.[0].gitSrcRepoAddress").value(bundleTemplateDtoList.get(0).getGitSrcRepoAddress()))
                .andExpect(jsonPath("$.[0].bundleName").value(bundleTemplateDtoList.get(0).getBundleName()))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(bundleTemplateDtoList.get(0).getBundleGroupName()));
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
                .andDo(print())
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
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetPrivateCatalogPublishedBundleGroupsTemplates() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        List<BundleGroupTemplateDto> bundleGroupTemplateDtoList = createBundleGroupTemplateDtoList();
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
        Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(true);
        Mockito.when(bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupTemplates(CATALOG_ID)).thenReturn(bundleGroupTemplateDtoList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_URI)
                        .header(API_KEY_HEADER, API_KEY)
                        .param(CATALOG_ID_PARAM, CATALOG_ID.toString())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(bundleGroupTemplateDtoList.get(0).getBundleGroupName()))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(bundleGroupTemplateDtoList.get(0).getBundleGroupVersionId()));
    }

    @Test
    void testGetPublicCatalogPublishedBundleGroupsTemplates() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        List<BundleGroupTemplateDto> bundleGroupTemplateDtoList = createBundleGroupTemplateDtoList();
        Mockito.when(bundleGroupVersionService.getPublicCatalogPublishedBundleGroupTemplates()).thenReturn(bundleGroupTemplateDtoList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_URI)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(bundleGroupTemplateDtoList.get(0).getBundleGroupName()))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(bundleGroupTemplateDtoList.get(0).getBundleGroupVersionId()));
    }

    @Test
    void testGetPrivateCatalogPublishedBundleGroupsTemplatesByName() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        List<BundleGroupTemplateDto> bundleGroupTemplateDtoList = createBundleGroupTemplateDtoList();
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
        Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(true);
        Mockito.when(bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupTemplatesByName(CATALOG_ID,BUNDLE_GROUP_NAME))
                .thenReturn(bundleGroupTemplateDtoList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_URI)
                        .header(API_KEY_HEADER, API_KEY)
                        .param(CATALOG_ID_PARAM, CATALOG_ID.toString())
                        .param(BUNDLE_GROUP_NAME_PARAM, BUNDLE_GROUP_NAME)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(bundleGroupTemplateDtoList.get(0).getBundleGroupName()))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(bundleGroupTemplateDtoList.get(0).getBundleGroupVersionId()));
    }

    @Test
    void testGetPublicCatalogPublishedBundleGroupsTemplatesByName() throws Exception {
        List<BundleGroupTemplateDto> bundleGroupTemplateDtoList = createBundleGroupTemplateDtoList();
        Mockito.when(bundleGroupVersionService.getPublicCatalogPublishedBundleGroupTemplatesByName(BUNDLE_GROUP_NAME))
                .thenReturn(bundleGroupTemplateDtoList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_URI)
                        .param(BUNDLE_GROUP_NAME_PARAM, BUNDLE_GROUP_NAME)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(bundleGroupTemplateDtoList.get(0).getBundleGroupName()))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(bundleGroupTemplateDtoList.get(0).getBundleGroupVersionId()));
    }

    @Test
    void testGetPublicCatalogPublishedBundleGroupsTemplatesFilteredByName() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        List<BundleGroupTemplateDto> bundleGroupTemplateDtoList = createBundleGroupTemplateDtoList();
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
        Mockito.when(bundleGroupVersionService.getPublicCatalogPublishedBundleGroupTemplates()).thenReturn(bundleGroupTemplateDtoList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_URI)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(bundleGroupTemplateDtoList.get(0).getBundleGroupName()))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(bundleGroupTemplateDtoList.get(0).getBundleGroupVersionId()));
    }

    @Test
    void testGetPrivateCatalogPublishedBundleTemplatesByID() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        List<BundleTemplateDto> bundleTemplateDtoList = createBundleTemplateDtoList();
        Mockito.when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog);
        Mockito.when(privateCatalogApiKeyService.doesApiKeyExist(API_KEY)).thenReturn(true);
        Mockito.when(bundleGroupVersionService.getPrivateCatalogPublishedBundleTemplatesById(CATALOG_ID, BUNDLE_GROUP_ID)).thenReturn(bundleTemplateDtoList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_BY_ID_URI+BUNDLE_GROUP_ID)
                        .header(API_KEY_HEADER, API_KEY)
                        .param(CATALOG_ID_PARAM, CATALOG_ID.toString())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleId").value(bundleTemplateDtoList.get(0).getBundleId()))
                .andExpect(jsonPath("$.[0].bundleGroupId").value(bundleTemplateDtoList.get(0).getBundleGroupId()))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(bundleTemplateDtoList.get(0).getBundleGroupVersionId()))
                .andExpect(jsonPath("$.[0].gitSrcRepoAddress").value(bundleTemplateDtoList.get(0).getGitSrcRepoAddress()))
                .andExpect(jsonPath("$.[0].bundleName").value(bundleTemplateDtoList.get(0).getBundleName()))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(bundleTemplateDtoList.get(0).getBundleGroupName()));
    }

    @Test
    void testGetPublicCatalogPublishedBundleTemplatesByID() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(CATALOG_ID);
        List<BundleTemplateDto> bundleTemplateDtoList = createBundleTemplateDtoList();
        Mockito.when(bundleGroupVersionService.getPublicCatalogPublishedBundleTemplatesById(BUNDLE_GROUP_ID)).thenReturn(bundleTemplateDtoList);
        mockMvc.perform(MockMvcRequestBuilders.get(BUNDLE_GROUPS_BY_ID_URI+BUNDLE_GROUP_ID)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[0].bundleId").value(bundleTemplateDtoList.get(0).getBundleId()))
                .andExpect(jsonPath("$.[0].bundleGroupId").value(bundleTemplateDtoList.get(0).getBundleGroupId()))
                .andExpect(jsonPath("$.[0].bundleGroupVersionId").value(bundleTemplateDtoList.get(0).getBundleGroupVersionId()))
                .andExpect(jsonPath("$.[0].gitSrcRepoAddress").value(bundleTemplateDtoList.get(0).getGitSrcRepoAddress()))
                .andExpect(jsonPath("$.[0].bundleName").value(bundleTemplateDtoList.get(0).getBundleName()))
                .andExpect(jsonPath("$.[0].bundleGroupName").value(bundleTemplateDtoList.get(0).getBundleGroupName()));
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
                .andDo(print())
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

    private List<BundleGroupTemplateDto> createBundleGroupTemplateDtoList() {
        List<BundleGroupTemplateDto> bundleGroupTemplateDtoList = new ArrayList<>();
        bundleGroupTemplateDtoList.add(createBundleGroupTemplateDto("test1",1L));
        bundleGroupTemplateDtoList.add(createBundleGroupTemplateDto("test2",2L));
        return bundleGroupTemplateDtoList;
    }

    private BundleGroupTemplateDto createBundleGroupTemplateDto(String bundleGroupName,Long bundleGroupVersionId) {
        BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
        BundleGroup bundleGroup = new BundleGroup();
        bundleGroup.setName(bundleGroupName);
        bundleGroupVersion.setId(bundleGroupVersionId);
        return new BundleGroupTemplateDto(bundleGroupVersion, bundleGroup);
    }


    private List<BundleTemplateDto> createBundleTemplateDtoList() {
        List<BundleTemplateDto> bundleTemplateDtoList= new ArrayList<>();
        bundleTemplateDtoList.add(createBundleTemplateDto(1L,1L, 1L));
        bundleTemplateDtoList.add(createBundleTemplateDto(1L,1L, 2L));
        bundleTemplateDtoList.add(createBundleTemplateDto(1L,2L, 1L));
        bundleTemplateDtoList.add(createBundleTemplateDto(1L,2L, 2L));
        return bundleTemplateDtoList;
    }

    private BundleTemplateDto createBundleTemplateDto(Long bundleGroupVersionId, Long bundleGroupId, Long bundleId) {
        BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
        Bundle bundle = new Bundle();
        BundleGroup bundleGroup = new BundleGroup();
        bundleGroupVersion.setId(bundleGroupVersionId);
        bundle.setId(bundleId);
        bundle.setGitSrcRepoAddress(SRC_REPO_ADDRESS + bundleId);
        bundleGroup.setName(BUNDLE_GROUP_NAME + bundleGroupId);
        bundle.setName(BUNDLE_NAME + bundleId);
        bundleGroup.setId(bundleGroupId);
        bundleGroupVersion.setBundleGroup(bundleGroup);
        return new BundleTemplateDto(bundleGroupVersion, bundle);
    }

}
