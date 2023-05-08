package com.entando.hub.catalog.rest;

import static com.entando.hub.catalog.config.ApplicationConstants.API_KEY_HEADER;
import static com.entando.hub.catalog.config.ApplicationConstants.CATALOG_ID_PARAM;

import com.entando.hub.catalog.config.SwaggerConstants;
import com.entando.hub.catalog.rest.dto.BundleGroupTemplateDto;
import com.entando.hub.catalog.rest.dto.BundleTemplateDto;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import javax.validation.constraints.NotNull;
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

    public EntTemplateController(BundleGroupVersionService bundleGroupVersionService) {
        this.bundleGroupVersionService = bundleGroupVersionService;
    }

    @Operation(summary = "Get all the templates for the bundle that are in the hub", description = "Public api, no authentication required.")
    @GetMapping(value = "/bundles", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.BAD_REQUEST_RESPONSE_CODE, description = SwaggerConstants.BAD_REQUEST_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public List<BundleTemplateDto> getBundleTemplates(
            @RequestHeader(name = API_KEY_HEADER, required = false) String apiKey,
            @RequestParam(name = CATALOG_ID_PARAM, required = false) Long catalogId) {
        List<BundleTemplateDto> result;
        if (null != catalogId) {
            result = bundleGroupVersionService.getPrivateCatalogPublishedBundleTemplates(catalogId);
        } else {
            result = bundleGroupVersionService.getPublicCatalogPublishedBundleTemplates();
        }
        return result;
    }

    @Operation(summary = "Get all the bundle groups having templates in them, they can be filtered by name part", description = "Public api, no authentication required.")
    @GetMapping(value = "/bundlegroups", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.BAD_REQUEST_RESPONSE_CODE, description = SwaggerConstants.BAD_REQUEST_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public List<BundleGroupTemplateDto> getBundleGroupsWithTemplates(
            @RequestHeader(name = API_KEY_HEADER, required = false) String apiKey,
            @RequestParam(name = CATALOG_ID_PARAM, required = false) Long catalogId,
            @RequestParam(required = false) String name) {
        List<BundleGroupTemplateDto> result;
        if (null != catalogId) {
            if (name != null) {
                result = bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupTemplatesByName(catalogId, name);
            } else {
                result = bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupTemplates(catalogId);
            }
        } else {
            if (name != null) {
                result = bundleGroupVersionService.getPublicCatalogPublishedBundleGroupTemplatesByName(name);
            } else {
                result = bundleGroupVersionService.getPublicCatalogPublishedBundleGroupTemplates();
            }
        }
        return result;
    }

    @Operation(summary = "Get the templates for the bundle given the bundlegroup id", description = "Public api, no authentication required.")
    @GetMapping(value = "/bundlegroups/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = SwaggerConstants.BAD_REQUEST_RESPONSE_CODE, description = SwaggerConstants.BAD_REQUEST_DESCRIPTION, content = @Content)
    @ApiResponse(responseCode = SwaggerConstants.OK_RESPONSE_CODE, description = SwaggerConstants.OK_DESCRIPTION)
    public List<BundleTemplateDto> getBundleTemplateByBundleGroupId(
            @RequestHeader(name = API_KEY_HEADER, required = false) String apiKey,
            @RequestParam(name = CATALOG_ID_PARAM, required = false) Long catalogId,
            @NotNull @PathVariable Long id) {
        List<BundleTemplateDto> result;
        if (null != catalogId) {
                result = bundleGroupVersionService.getPrivateCatalogPublishedBundleTemplatesById(catalogId, id);
        } else {
                result = bundleGroupVersionService.getPublicCatalogPublishedBundleTemplatesById(id);
        }
        return result;
    }
}
