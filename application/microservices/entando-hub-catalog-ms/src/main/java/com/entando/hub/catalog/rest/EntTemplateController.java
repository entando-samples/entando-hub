package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.ApplicationConstants.API_KEY_HEADER;
import static com.entando.hub.catalog.config.ApplicationConstants.CATALOG_ID_PARAM;

import com.entando.hub.catalog.config.SwaggerConstants;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.rest.dto.BundleGroupTemplateDto;
import com.entando.hub.catalog.rest.dto.BundleTemplateDto;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ent/api/templates")
public class EntTemplateController {

    private final BundleGroupVersionService bundleGroupVersionService;

    private CatalogService catalogService;

    public EntTemplateController(BundleGroupVersionService bundleGroupVersionService,
            CatalogService catalogService) {
        this.bundleGroupVersionService = bundleGroupVersionService;
        this.catalogService = catalogService;
    }

    @Operation(summary = "Get all the templates for the bundle that are in the hub", description = "Public api, no authentication required.")
    @GetMapping(value = "/bundles", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.BAD_REQUEST_RESPONSE_CODE, description = SwaggerConstants.BAD_REQUEST_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public List<BundleTemplateDto> getBundleTemplates(
            @RequestHeader(name = API_KEY_HEADER, required = false) String apiKey,
            @RequestParam(name = CATALOG_ID_PARAM, required = false) Long catalogId) {
        Catalog userCatalog;
        if (StringUtils.isNotEmpty(apiKey)) {
            userCatalog = catalogService.getCatalogByApiKey(apiKey);
            return bundleGroupVersionService.getPrivateCatalogPublishedBundleTemplates(userCatalog.getId());
        }
        return bundleGroupVersionService.getPublicCatalogPublishedBundleTemplates();
    }

    @Operation(summary = "Get all the bundle groups having templates in them, they can be filtered by name part", description = "Public api, no authentication required.")
    @GetMapping(value = "/bundlegroups", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.BAD_REQUEST_RESPONSE_CODE, description = SwaggerConstants.BAD_REQUEST_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public List<BundleGroupTemplateDto> getBundleGroupsWithTemplates(
            @RequestHeader(name = API_KEY_HEADER, required = false) String apiKey,
            @RequestParam(name = CATALOG_ID_PARAM, required = false) Long catalogId,
            @RequestParam(required = false) String name) {
        Catalog userCatalog;
        if (StringUtils.isNotEmpty(apiKey)) {
            // Private Catalog published
            userCatalog = catalogService.getCatalogByApiKey(apiKey);
            if (name != null) {
                return bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupTemplatesByName(userCatalog.getId(), name);
            }
            return bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupTemplates(userCatalog.getId());
        } else {
            // Public
            if (name != null) {
                return bundleGroupVersionService.getPublicCatalogPublishedBundleGroupTemplatesByName(name);
            }
            return bundleGroupVersionService.getPublicCatalogPublishedBundleGroupTemplates();
        }
    }

    @Operation(summary = "Get the templates for the bundle given the bundlegroup id", description = "Public api, no authentication required.")
    @GetMapping(value = "/bundlegroups/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.BAD_REQUEST_RESPONSE_CODE, description = SwaggerConstants.BAD_REQUEST_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public List<BundleTemplateDto> getBundleTemplateByBundleGroupId(
            @RequestHeader(name = API_KEY_HEADER, required = false) String apiKey,
            @RequestParam(name = CATALOG_ID_PARAM, required = false) Long catalogId,
            @PathVariable Long id) {
        Catalog userCatalog;
        if (StringUtils.isNotEmpty(apiKey)) {
            // Return Templates from Private Catalog with state Published
            userCatalog = catalogService.getCatalogByApiKey(apiKey);
            if (id != null) {
                return bundleGroupVersionService.getPrivateCatalogPublishedBundleTemplatesById(userCatalog.getId(), id);
            }
            return bundleGroupVersionService.getPrivateCatalogPublishedBundleTemplates(userCatalog.getId());
        } else {
            // Return  Public Templates
            if (id != null) {
                return bundleGroupVersionService.getPublicCatalogPublishedBundleTemplatesById(id);
            }
            return bundleGroupVersionService.getPublicCatalogPublishedBundleTemplates();
        }
    }
}
