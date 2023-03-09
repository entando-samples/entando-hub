package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.entando.hub.catalog.rest.BundleController.BundleNoId;
import com.entando.hub.catalog.service.*;
import com.entando.hub.catalog.service.exception.ForbiddenException;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * Controller for Bundle Group Version operations
 *
 */
@RestController
@RequestMapping("/api/bundlegroupversions")
public class BundleGroupVersionController {

    private final Logger logger = LoggerFactory.getLogger(BundleGroupVersionController.class);

    private final BundleGroupVersionService bundleGroupVersionService;

    private final BundleGroupService bundleGroupService;

    private final CategoryService categoryService;

    private final SecurityHelperService securityHelperService;

    private final BundleGroupVersionMapper bundleGroupVersionMapper;

    public BundleGroupVersionController(BundleGroupVersionService bundleGroupVersionService, BundleGroupService bundleGroupService, CategoryService categoryService, SecurityHelperService securityHelperService, BundleGroupVersionMapper bundleGroupVersionMapper) {
    	this.bundleGroupVersionService = bundleGroupVersionService;
    	this.bundleGroupService = bundleGroupService;
    	this.categoryService = categoryService;
    	this.securityHelperService = securityHelperService;
      this.bundleGroupVersionMapper = bundleGroupVersionMapper;
    }

	@Operation(summary = "Create a new Bundle Group Version", description = "Protected api, only eh-admin, eh-author or eh-manager can access it.")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<BundleGroupVersionDto> createBundleGroupVersion(@RequestBody BundleGroupVersionDto bundleGroupVersionView) {
        logger.debug("REST request to create BundleGroupVersion: {}", bundleGroupVersionView);
        Optional<com.entando.hub.catalog.persistence.entity.BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(Long.parseLong(bundleGroupVersionView.getBundleGroupId()));
        if (bundleGroupOptional.isPresent()) {
        	logger.debug("BundleGroupDto is present with id: {}", bundleGroupOptional.get().getId());
            List<BundleGroupVersion> bundleGroupVersions = bundleGroupVersionService.getBundleGroupVersions(bundleGroupOptional.get(), bundleGroupVersionView.getVersion());
            if (CollectionUtils.isEmpty(bundleGroupVersions)) {
            	logger.info("Bundle group version list found with size: {}", bundleGroupVersions.size());

              final BundleGroup bundleGroup = bundleGroupOptional.get();
              BundleGroupVersion bundleGroupVersionEntity = bundleGroupVersionMapper.toEntity(bundleGroupVersionView, bundleGroup);

              bundleGroupVersionEntity.setId(null); // EHUB-296 impose expected null id
              BundleGroupVersion saved = bundleGroupVersionService.createBundleGroupVersion(bundleGroupVersionEntity, bundleGroupVersionView);
              BundleGroupVersionDto dto = bundleGroupVersionMapper.toDto(saved);
		        return new ResponseEntity<>(dto, HttpStatus.CREATED);
            } else {
            	logger.warn("Bundle group version list found with size: {}", bundleGroupVersions.size());
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
        	logger.warn("Requested bundleGroupVersion '{}' does not exist", bundleGroupVersionView.getBundleGroupId().toString());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

	//PUBLIC
    @Operation(summary = "Get all the bundle group versions in the hub, provides filter functionality", description = "Public api, no authentication required. You can provide the organisationId the categoryIds and the statuses [NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED]")
    @GetMapping(value = "/filtered", produces = {"application/json"})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public PagedContent<BundleGroupVersionFilteredResponseView, com.entando.hub.catalog.persistence.entity.BundleGroupVersion> getBundleGroupsAndFilterThem(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) Long organisationId, @RequestParam(required = false) String[] categoryIds, @RequestParam(required = false) String[] statuses, @RequestParam(required = false) String searchText) {
    	logger.debug("REST request to get bundle group versions by organisation Id: {}, categoryIds {}, statuses {}", organisationId, categoryIds, statuses);
        Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;

        String[] categoryIdFilterValues = categoryIds;
        if (categoryIdFilterValues == null) {
            categoryIdFilterValues = categoryService.getCategories().stream().map(c -> c.getId().toString()).toArray(String[]::new);
        }

        String[] statusFilterValues = statuses;
        if (statusFilterValues == null) {
            statuses = Arrays.stream(BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
        }

        logger.debug("Organisation Id: {}, categoryIds {}, statuses {}", organisationId, categoryIds, statuses);
        return bundleGroupVersionService.searchBundleGroupVersions(sanitizedPageNum, pageSize, organisationId, categoryIdFilterValues, statuses, searchText);
    }

    @Operation(summary = "Get all the private bundle group versions in the hub for the selected catalog, provides filter functionality", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You can provide the catalogId, the categoryIds and the statuses [NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED]")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @GetMapping(value = "catalog/{catalogId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public PagedContent<BundleGroupVersionFilteredResponseView, com.entando.hub.catalog.persistence.entity.BundleGroupVersion> getPrivateBundleGroupsAndFilterThem(@PathVariable Long catalogId, @RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String[] categoryIds, @RequestParam(required = false) String[] statuses, @RequestParam(required = false) String searchText) {
        logger.debug("REST request to get bundle group versions by catalog Id: {}, categoryIds {}, statuses {}", catalogId, categoryIds, statuses);

        if (!this.securityHelperService.isAdmin() && !this.securityHelperService.userCanAccessTheCatalog(catalogId)){
            throw new ForbiddenException(String.format("Only %s users can get bundle groups for any catalog, the other ones can get bundle groups only for their catalog", ADMIN));
        }

        Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;

        if (categoryIds == null) {
            categoryIds = categoryService.getCategories().stream().map(c -> c.getId().toString()).toArray(String[]::new);
        }

        if (statuses == null) {
            statuses = Arrays.stream(com.entando.hub.catalog.persistence.entity.BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
        }

        logger.debug("Catalog Id: {}, categoryIds {}, statuses {}", catalogId, categoryIds, statuses);
        return bundleGroupVersionService.searchPrivateBundleGroupVersions(sanitizedPageNum, pageSize, catalogId, categoryIds, statuses, searchText);
    }

    @Operation(summary = "Update a Bundle Group Version", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You have to provide the bundleGroupVersionId identifying the bundleGroupVersion")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/{bundleGroupVersionId}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<BundleGroupVersionDto> updateBundleGroupVersion(@PathVariable String bundleGroupVersionId, @RequestBody BundleGroupVersionDto bundleGroupVersionView) {
        logger.debug("REST request to update BundleGroupVersionDto with id {}, request object: {}", bundleGroupVersionId, bundleGroupVersionView);
        Optional<BundleGroupVersion> bundleGroupVersionOptional = bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId);
        if (!bundleGroupVersionOptional.isPresent()) {
            logger.warn("BundleGroupVersionDto '{}' does not exist", bundleGroupVersionId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            //if the user is not ADMIN
            if (!securityHelperService.hasRoles(Set.of(ADMIN))) {
                //I'm going to check the organisation
                BundleGroupVersion bundleGroupVersionEntity = bundleGroupVersionOptional.get();

                //must exist and the user mat be in it
                if (bundleGroupVersionEntity.getBundleGroup().getOrganisation() == null || !securityHelperService.userIsInTheOrganisation(bundleGroupVersionEntity.getBundleGroup().getOrganisation().getId())) {
                    logger.warn("Only {} users can update bundle groups for any organisation, the other ones can update bundle groups only for their organisation", ADMIN);
                    return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
                }
            }
//            BundleGroupVersion saved = bundleGroupVersionService.createBundleGroupVersion(bundleGroupVersionView.createEntity(Optional.of(bundleGroupVersionId), bundleGroupVersionOptional.get().getBundleGroup()), bundleGroupVersionView);

            final BundleGroup bundleGroup = bundleGroupVersionOptional.get().getBundleGroup();
            bundleGroupVersionView.setBundleGroupVersionId(bundleGroupVersionId);
            BundleGroupVersion bundlegroupVersionEntity = bundleGroupVersionMapper.toEntity(bundleGroupVersionView, bundleGroup);
            BundleGroupVersion saved = bundleGroupVersionService.createBundleGroupVersion(bundlegroupVersionEntity, bundleGroupVersionView);
            BundleGroupVersionDto dto = bundleGroupVersionMapper.toDto(saved);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
    }

    //PUBLIC
    @Operation(summary = "Get all the bundle group versions in the hub filtered by bundleGroupId and statuses", description = "Public api, no authentication required. You can provide the bundleGroupId, the statuses [NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED]")
    @GetMapping(value = "/versions/{bundleGroupId}",produces = {"application/json"})
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> getBundleGroupVersions(@PathVariable Long bundleGroupId, @RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String[] statuses) {
    	logger.debug("REST request to get bundle group versions by bundleGroupId: {} and statuses {}", bundleGroupId, statuses);
        Integer sanitizedPageNum = page >= 1 ? page - 1 : 0;
        String[] statusFilterValues = statuses;
        PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> pagedContent = null;
        if (statusFilterValues == null) {
            statuses = Arrays.stream(BundleGroupVersion.Status.values()).map(Enum::toString).toArray(String[]::new);
        }
        Optional<BundleGroup> bundleGroupOptional = bundleGroupService.getBundleGroup(bundleGroupId);
        if (bundleGroupOptional.isPresent()) {
        	pagedContent = bundleGroupVersionService.getBundleGroupVersions(sanitizedPageNum, pageSize, statuses, bundleGroupOptional.get());
            return pagedContent;
        } else {
            // TODO check the impact on the FE if we return a non null object
            logger.warn("Requested bundleGroup '{}' does not exist", bundleGroupId);
            return pagedContent;
        }
    }

    @Operation(summary = "Delete a Bundle Group Version  by id", description = "Protected api, only eh-admin and eh-manager can access it. A Bundle Group Version can be deleted only if it is in DELETE_REQ status, you have to provide the bundlegroupVersionId")
    @RolesAllowed({ADMIN, MANAGER})
    @DeleteMapping(value = "/{bundleGroupVersionId}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    @Transactional
    public ResponseEntity<BundleGroupVersionDto> deleteBundleGroupVersion(@PathVariable String bundleGroupVersionId) {
        logger.debug("REST request to delete BundleGroupVersionDto by id: {}", bundleGroupVersionId);
        Optional<BundleGroupVersion> bundleGroupVersionOptional = bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId);
        if (!bundleGroupVersionOptional.isPresent() || !bundleGroupVersionOptional.get().getStatus().equals(BundleGroupVersion.Status.DELETE_REQ)) {
            bundleGroupVersionOptional.ifPresentOrElse(
                    bundleGroupVersion -> logger.warn("Requested BundleGroupVersionDto '{}' is not in DELETE_REQ status: {}", bundleGroupVersionId, bundleGroupVersion.getStatus()),
                    () -> logger.warn("Requested bundleGroupVersion '{}' does not exist", bundleGroupVersionId)
            );
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
        	bundleGroupVersionService.deleteBundleGroupVersion(bundleGroupVersionOptional);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

	// PUBLIC
	@Operation(summary = "Get the BundleGroupVersionDto details by id", description = "Public api, no authentication required. You have to provide the bundleGroupVersionId")
	@GetMapping(value = "/{bundleGroupVersionId}", produces = {"application/json"})
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
	public ResponseEntity<BundleGroupVersionDto> getBundleGroupVersion(@PathVariable String bundleGroupVersionId) {
		logger.debug("REST request to get BundleGroupVersionDto by Id: {}", bundleGroupVersionId);
		Optional<BundleGroupVersion> bundleGroupVersionOptional = bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId);
		if (bundleGroupVersionOptional.isPresent()) {
		    BundleGroupVersion version = bundleGroupVersionOptional.get();
		    //Prevent this view unless the user is authenticated or the version is published
		    if (securityHelperService.isUserAuthenticated() || version.getStatus().equals(BundleGroupVersion.Status.PUBLISHED)) {
//                BundleGroupVersionView bundleGroupVersionView = new BundleGroupVersionView(version);
                BundleGroupVersionDto bundleGroupVersionView = bundleGroupVersionMapper.toViewDto(version);
                return new ResponseEntity<>(bundleGroupVersionView, HttpStatus.OK);
            }
            logger.warn("Requested bundleGroupVersion '{}' exists but is protected", bundleGroupVersionOptional);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} else {
			logger.warn("Requested bundleGroupVersion '{}' does not exist", bundleGroupVersionOptional);
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}


    @ExceptionHandler({ ForbiddenException.class })
    public ResponseEntity<String> handleException(Exception exception) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (exception instanceof ForbiddenException) {
            status = HttpStatus.FORBIDDEN;
        }
        return ResponseEntity.status(status).body(String.format("{\"message\": \"%s\"}", exception.getMessage()));
    }

}
