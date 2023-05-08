package com.entando.hub.catalog.service.security;

import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.service.CatalogService;
import com.entando.hub.catalog.service.KeycloakService;
import com.entando.hub.catalog.service.PrivateCatalogApiKeyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyCatalogIdValidator {
    private final CatalogService catalogService;
    private final KeycloakService keycloakService;
    private final PrivateCatalogApiKeyService privateCatalogApiKeyService;

    public ApiKeyCatalogIdValidator(CatalogService catalogService, KeycloakService keycloakService, PrivateCatalogApiKeyService privateCatalogApiKeyService) {
        this.catalogService = catalogService;
        this.keycloakService = keycloakService;
        this.privateCatalogApiKeyService = privateCatalogApiKeyService;
    }

    public boolean validateApiKeyCatalogId(String apiKey, Long catalogId) {
        if ((StringUtils.isEmpty(apiKey) && null != catalogId) || (StringUtils.isNotEmpty(apiKey) && null == catalogId)){
            return false;
        } else if (StringUtils.isNotEmpty(apiKey)) {
            String username = this.privateCatalogApiKeyService.getUsernameByApiKey(apiKey);
            if(this.keycloakService.userIsAdmin(username)){
                return true;
            }
            try {
                Catalog userCatalog = catalogService.getCatalogByApiKey(apiKey);
                Long userCatalogId = null;
                if (null != userCatalog) {
                    userCatalogId = userCatalog.getId();
                }
                return userCatalogId == null || userCatalogId.equals(catalogId);
            } catch (Exception ex){
                // TODO: Currently an api key can be created without association with catalogId
                return false;
            }

        } else {
            return true;
        }
    }
}
