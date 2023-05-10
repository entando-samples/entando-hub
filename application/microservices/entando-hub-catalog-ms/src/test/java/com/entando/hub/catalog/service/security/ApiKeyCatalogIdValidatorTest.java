package com.entando.hub.catalog.service.security;

import static org.mockito.Mockito.when;

import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.service.CatalogService;
import com.entando.hub.catalog.service.KeycloakService;
import com.entando.hub.catalog.service.PrivateCatalogApiKeyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ApiKeyCatalogIdValidatorTest {

    private static final String API_KEY = "api-key";
    private static final Long CATALOG_ID = 1L;
    private static final Long CATALOG_ID_2 = 2L;
    private static final String USERNAME = "username";

    @Autowired
    private ApiKeyCatalogIdValidator appBuilderCatalogValidator;

    @MockBean
    private CatalogService catalogService;

    @MockBean
    private PrivateCatalogApiKeyService privateCatalogApiKeyService;

    @MockBean
    KeycloakService keycloakService;

    @Test
    void validateApiKeyCatalogIdTest() {
        Catalog catalog1 = new Catalog();
        catalog1.setId(CATALOG_ID);

        when(catalogService.getCatalogByApiKey(API_KEY)).thenReturn(catalog1);
        when(privateCatalogApiKeyService.getUsernameByApiKey(API_KEY)).thenReturn(USERNAME);
        //When the api-key is valid but the catalogId is empty should return false
        boolean result = this.appBuilderCatalogValidator.validateApiKeyCatalogId(API_KEY, null);
        Assertions.assertFalse(result);

        //When the catalogId is valid but the catalogId is empty should return false

        result = this.appBuilderCatalogValidator.validateApiKeyCatalogId(null, CATALOG_ID);
        Assertions.assertFalse(result);

        //When the api-key is valid but related to a different catalog and the validator should return false
        result = appBuilderCatalogValidator.validateApiKeyCatalogId(API_KEY, CATALOG_ID_2);
        Assertions.assertFalse(result);

        //Valid Api key and catalogId should return true
        result = this.appBuilderCatalogValidator.validateApiKeyCatalogId(API_KEY, CATALOG_ID);
        Assertions.assertTrue(result);

    }

}
