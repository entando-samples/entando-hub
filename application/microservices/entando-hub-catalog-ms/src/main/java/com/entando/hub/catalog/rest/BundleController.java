package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.Bundle.DescriptorVersion;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.rest.validation.BundleGroupValidator;
import com.entando.hub.catalog.service.BundleService;
import com.entando.hub.catalog.service.exception.ConflictException;
import com.entando.hub.catalog.service.exception.NotFoundException;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static com.entando.hub.catalog.config.AuthoritiesConstants.*;

@RestController
@RequestMapping("/api/bundles/")
public class BundleController {

    private final Logger logger = LoggerFactory.getLogger(BundleController.class);

    final private BundleService bundleService;
    private BundleGroupValidator bundleGroupValidator;
    private final SecurityHelperService securityHelperService;

    public BundleController(BundleService bundleService, BundleGroupValidator bundleGroupValidator, SecurityHelperService securityHelperService) {
        this.bundleService = bundleService;
        this.bundleGroupValidator = bundleGroupValidator;
        this.securityHelperService = securityHelperService;
    }

    @Operation(summary = "Get all the bundles of a bundle group version", description = "Public api, no authentication required. You can provide a bundleGroupVersionId to get all the bundles in that")
    @GetMapping(value = "/", produces = {"application/json"})
    public ResponseEntity<List<Bundle>> getBundles(@RequestParam(required = false) String bundleGroupVersionId, @RequestParam(required = false) Long catalogId) {
        // If not Authenticated that request a private catalog
        boolean isUserAuthenticated = securityHelperService.isUserAuthenticated();
        if (null != catalogId && Boolean.FALSE.equals(isUserAuthenticated)) {
            return (new ResponseEntity<>(HttpStatus.FORBIDDEN));
        }
        if (Boolean.TRUE.equals(isUserAuthenticated)) {
            if (null != bundleGroupVersionId) {
                bundleGroupValidator.validateBundleGroupVersionPrivateCatalogRequest(catalogId, bundleGroupVersionId);
            } else {
                bundleGroupValidator.validateBundlePrivateCatalogRequest(catalogId);
            }
        } else {
            if (null != bundleGroupVersionId) {
                bundleGroupValidator.validateBundleGroupVersionPrivateCatalogRequest(catalogId, bundleGroupVersionId);
            }
        }
        List<Bundle> bundles = bundleService.getBundles(bundleGroupVersionId,catalogId).stream().map(BundleController.Bundle::new).collect(Collectors.toList());
        return new ResponseEntity<>(bundles, HttpStatus.OK);
    }

    @Operation(summary = "Get the bundle details", description = "Public api, no authentication required. You have to provide the bundleId")
    @GetMapping(value = "/{bundleId}", produces = {"application/json"})
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<Bundle> getBundle(@PathVariable() String bundleId) {
        Optional<com.entando.hub.catalog.persistence.entity.Bundle> bundleOptional = bundleService.getBundle(bundleId);
        if (bundleOptional.isPresent()) {
//            return new ResponseEntity<>(bundleOptional.map(Bundle::new).get(), HttpStatus.OK);
            return new ResponseEntity<>(bundleMapper.toDto(bundleOptional.get()), HttpStatus.OK);
        } else {
            logger.warn("Requested bundle '{}' does not exist", bundleId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Create a new bundle", description = "Protected api, only eh-admin, eh-author or eh-manager can access it.")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping(value = "/", produces = {"application/json"})
    public ResponseEntity<Bundle> createBundle(@RequestBody BundleNoId bundle) {
        Optional<String> opt = Objects.nonNull(bundle.getBundleId())
                ? Optional.of(bundle.getBundleId())
                : Optional.empty();

        com.entando.hub.catalog.persistence.entity.Bundle entity = bundleService.createBundle(bundle.createEntity(opt));
        return new ResponseEntity<>(new Bundle(entity), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a bundle", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You have to provide the bundleId identifying the bundle")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/{bundleId}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<Bundle> updateBundle(@PathVariable String bundleId, @RequestBody BundleNoId bundle) {
        Optional<com.entando.hub.catalog.persistence.entity.Bundle> bundleOptional = bundleService.getBundle(bundleId);

        if (!bundleOptional.isPresent()) {
            logger.warn("Bundle '{}' does not exist", bundleId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            bundleDto.setBundleId(bundleId);
            Bundle eBundle = bundleMapper.toEntity(bundleDto);
            Bundle entity = bundleService.createBundle(eBundle);
            return new ResponseEntity<>(bundleMapper.toDto(entity), HttpStatus.OK);
        }
    }

    @Operation(summary = "Delete a bundle", description = "Protected api, only eh-admin can access it. You have to provide the bundlegId")
    @RolesAllowed({ADMIN})
    @DeleteMapping(value = "/{bundleId}", produces = {"application/json"})
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "200", description = "OK")
    @Transactional
    public ResponseEntity<Bundle> deleteBundle(@PathVariable String bundleId) {
        Optional<com.entando.hub.catalog.persistence.entity.Bundle> bundleOptional = bundleService.getBundle(bundleId);
        if (!bundleOptional.isPresent()) {
            logger.warn("Bundle '{}' does not exist", bundleId);
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        } else {
            bundleService.deleteBundle(bundleOptional.get());
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class Bundle extends BundleNoId {
        @Schema(example = "bundle identifier")
        private final String bundleId;

        public Bundle(String bundleId, String name, String description, String gitRepoAddress, String gitSrcRepoAddress, List<String> dependencies, List<String> bundleGroups, String descriptorVersion) {
            super(bundleId, name, description, gitRepoAddress, gitSrcRepoAddress, dependencies, bundleGroups, descriptorVersion);
            this.bundleId = bundleId;
        }

        public Bundle(com.entando.hub.catalog.persistence.entity.Bundle entity) {
            super(entity);
            this.bundleId = entity.getId().toString();
        }
    }

    @Data
    public static class BundleNoId {
        @Schema(example = "bundle identifier")
        protected final String bundleId;

        @Schema(example = "bundle-sample")
        protected final String name;

        @Schema(example = "This is a example bundle")
        @Setter(AccessLevel.PUBLIC)
        protected String description;

        @Schema(example = "data:image/png;base64,base64code")
        @Setter(AccessLevel.PUBLIC)
        protected String descriptionImage;

        @Schema(example = "V5")
        protected final String descriptorVersion;

        @Schema(example = "docker://registry.hub.docker.com/organization/bundle-sample")
        protected final String gitRepoAddress;
        @Schema(example = "https://github.com/organization/bundle-sample")
        private final String gitSrcRepoAddress;

        protected final List<String> dependencies;
        protected final List<String> bundleGroups; //Used for bundle group versions, need to make it bundleGroupVersions

        public BundleNoId(String id, String name, String description, String gitRepoAddress, String gitSrcRepoAddress, List<String> dependencies, List<String> bundleGroupVersions, String descriptorVersion) {
            this.bundleId = id;
            this.name = name;
            this.description = description;
            this.gitRepoAddress = gitRepoAddress;
            this.gitSrcRepoAddress = gitSrcRepoAddress;
            this.dependencies = dependencies;
            this.bundleGroups = bundleGroupVersions;
            this.descriptorVersion = descriptorVersion;
        }

        public BundleNoId(com.entando.hub.catalog.persistence.entity.Bundle entity) {
            this.bundleId = entity.getId().toString();
            this.name = entity.getName();
            this.description = entity.getDescription();
            this.gitRepoAddress = entity.getGitRepoAddress();
            this.gitSrcRepoAddress = entity.getGitSrcRepoAddress();
            this.dependencies = Arrays.asList(entity.getDependencies().split(","));
            this.bundleGroups = entity.getBundleGroupVersions().stream().map(bundleGroupVersion -> bundleGroupVersion.getId().toString()).collect(Collectors.toList());
            this.descriptorVersion = entity.getDescriptorVersion().toString();
        }

        public com.entando.hub.catalog.persistence.entity.Bundle createEntity(Optional<String> id) {
            com.entando.hub.catalog.persistence.entity.Bundle ret = new com.entando.hub.catalog.persistence.entity.Bundle();
            ret.setDescription(this.getDescription());
            ret.setName(this.getName());
            ret.setGitRepoAddress(this.getGitRepoAddress());
            ret.setGitSrcRepoAddress(this.getGitSrcRepoAddress());
            ret.setDependencies(String.join(",", this.getDependencies()));

            //for now, if the repo address does not start with docker, we assume it's a V1 bundle.
            boolean isDocker = (this.getGitRepoAddress() != null) && (this.getGitRepoAddress().startsWith("docker:"));
            ret.setDescriptorVersion(isDocker ? DescriptorVersion.V5 : DescriptorVersion.V1);

            //TODO bundlegroups contains bundle group version id! fix it!
            Set<BundleGroupVersion> bundleGroupVersions = this.bundleGroups.stream().map((bundleGroupVersionId) -> {
                BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
                bundleGroupVersion.setId(Long.valueOf(bundleGroupVersionId));
                return bundleGroupVersion;
            }).collect(Collectors.toSet());
            ret.setBundleGroupVersions(bundleGroupVersions);
            id.map(Long::valueOf).ifPresent(ret::setId);
            return ret;
        }
    }

    @ExceptionHandler({ NotFoundException.class, AccessDeniedException.class, IllegalArgumentException.class, ConflictException.class })
    public ResponseEntity<String> handleException(Exception exception) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (exception instanceof AccessDeniedException) {
            status = HttpStatus.FORBIDDEN;
        } else if (exception instanceof NotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (exception instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (exception instanceof  ConflictException){
            status = HttpStatus.CONFLICT;
        }
        return ResponseEntity.status(status).body(String.format("{\"message\": \"%s\"}", exception.getMessage()));
    }
}
