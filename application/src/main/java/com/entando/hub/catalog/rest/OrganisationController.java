package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.service.OrganisationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;

@RestController
@RequestMapping("/api/organisation")
public class OrganisationController {
    
    private final Logger logger = LoggerFactory.getLogger(OrganisationController.class);

    private final OrganisationService organisationService;

    public OrganisationController(OrganisationService organisationService) {
        this.organisationService = organisationService;
    }

    @Operation(summary = "Get all the organisations", description = "Public api, no authentication required.")
    //@CrossOrigin
    @GetMapping("/")
    public List<Organisation> getOrganisations() {
        logger.debug("REST request to get organisations");
        return organisationService.getOrganisations().stream().map(Organisation::new).collect(Collectors.toList());
    }

    @Operation(summary = "Get the organisation details", description = "Public api, no authentication required. You have to provide the organisationId")
    //@CrossOrigin
    @GetMapping("/{organisationId}")
    public ResponseEntity<Organisation> getOrganisation(@PathVariable String organisationId) {
        logger.debug("REST request to get organisation by id: {}", organisationId);
        Optional<com.entando.hub.catalog.persistence.entity.Organisation> organisationOptional = organisationService.getOrganisation(organisationId);
        if (organisationOptional.isPresent()) {
            return new ResponseEntity<>(organisationOptional.map(Organisation::new).get(), HttpStatus.OK);
        } else {
            logger.warn("Requested organisation '{}' does not exists", organisationId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Create a new organisation", description = "Protected api, only eh-admin can access it.")
    @RolesAllowed({ADMIN})
    //@CrossOrigin
    @PostMapping("/")
    public ResponseEntity<Organisation> createOrganisation(@RequestBody OrganisationNoId organisation) {
        logger.debug("REST request to create new organisation: {}", organisation);
        com.entando.hub.catalog.persistence.entity.Organisation entity = organisationService.createOrganisation(organisation.createEntity(Optional.empty()), organisation);
        return new ResponseEntity<>(new Organisation(entity), HttpStatus.CREATED);
    }


    @Operation(summary = "Update an organisation", description = "Protected api, only eh-admin can access it. You have to provide the organisationId identifying the organisation")
    @RolesAllowed({ADMIN})
    //@CrossOrigin
    @PostMapping("/{organisationId}")
    public ResponseEntity<Organisation> updateOrganisation(@PathVariable String organisationId, @RequestBody OrganisationNoId organisation) {
        logger.debug("REST request to update organisation {}: {}", organisationId, organisation);
        Optional<com.entando.hub.catalog.persistence.entity.Organisation> organisationOptional = organisationService.getOrganisation(organisationId);
        if (!organisationOptional.isPresent()) {
            logger.warn("Requested organisation '{}' does not exists", organisationId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            //com.entando.hub.catalog.persistence.entity.Organisation storedEntity = organisationOptional.get();
            com.entando.hub.catalog.persistence.entity.Organisation entity = organisationService.createOrganisation(organisation.createEntity(Optional.of(organisationId)), organisation);
            return new ResponseEntity<>(new Organisation(entity), HttpStatus.OK);
        }
    }

    @Operation(summary = "Delete an organisation", description = "Protected api, only eh-admin can access it. You have to provide the organisationId identifying the organisation")
    @RolesAllowed({ADMIN})
    //@CrossOrigin
    @DeleteMapping("/{organisationId}")
    public ResponseEntity<Organisation> deleteOrganisation(@PathVariable String organisationId) {
        logger.debug("REST request to delete organisation {}", organisationId);
        Optional<com.entando.hub.catalog.persistence.entity.Organisation> organisationOptional = organisationService.getOrganisation(organisationId);
        if (!organisationOptional.isPresent()) {
            logger.warn("Requested organisation '{}' does not exists", organisationId);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            organisationService.deleteOrganisation(organisationId);
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }


    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class Organisation extends OrganisationNoId {
        private final String organisationId;

        public Organisation(com.entando.hub.catalog.persistence.entity.Organisation entity) {
            super(entity);
            this.organisationId = entity.getId().toString();
        }

        public Organisation(String organisationId, String name, String description) {
            super(name, description);
            this.organisationId = organisationId;
        }
    }


    @Data
    public static class OrganisationNoId {
        protected final String name;
        protected final String description;
        protected List<String> bundleGroups;

        public OrganisationNoId(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public OrganisationNoId(com.entando.hub.catalog.persistence.entity.Organisation entity) {
            this.name = entity.getName();
            this.description = entity.getDescription();
            if (entity.getBundleGroups() != null) {
                this.bundleGroups = entity.getBundleGroups().stream().map(bundleGroup -> bundleGroup.getId().toString()).collect(Collectors.toList());
            }
        }


        public com.entando.hub.catalog.persistence.entity.Organisation createEntity(Optional<String> id) {
            com.entando.hub.catalog.persistence.entity.Organisation ret = new com.entando.hub.catalog.persistence.entity.Organisation();
            ret.setDescription(this.getDescription());
            ret.setName(this.getName());
            id.map(Long::valueOf).ifPresent(ret::setId);
            return ret;
        }

    }

}
