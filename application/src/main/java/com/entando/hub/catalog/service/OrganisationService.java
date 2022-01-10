package com.entando.hub.catalog.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.OrganisationRepository;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.OrganisationController;

@Service
public class OrganisationService {
    final private OrganisationRepository organisationRepository;
    final private BundleGroupRepository bundleGroupRepository;
    
    private final Logger logger = LoggerFactory.getLogger(KeycloakService.class);
    private final String CLASS_NAME = this.getClass().getSimpleName();

    public OrganisationService(OrganisationRepository organisationRepository, BundleGroupRepository bundleGroupRepository) {
        this.organisationRepository = organisationRepository;
        this.bundleGroupRepository = bundleGroupRepository;
    }

    /**
     * Update the Organisation entity bundleGroups collection
     * with the mappedBy organisation field in every bundleGroup
     *
     * @param toUpdate
     * @param organisation
     */
    public void updateMappedBy(com.entando.hub.catalog.persistence.entity.Organisation toUpdate, OrganisationController.OrganisationNoId organisation) {
    	logger.debug("{}: updateMappedBy: update organisation entity bundle groups by organisationNo id: {} ", CLASS_NAME);
    	Objects.requireNonNull(toUpdate.getId());
        if (organisation.getBundleGroups() != null) {
            //TODO replace with native query
            //delete all old connections
            bundleGroupRepository.findByOrganisationId(toUpdate.getId()).stream().forEach(bundleGroup -> {
                bundleGroup.setOrganisation(null); //this is the mappedBy field
                bundleGroupRepository.save(bundleGroup);
            });
            Set<BundleGroup> newBundleGroups = organisation.getBundleGroups().stream().map(bundleGroupId -> {
                BundleGroup bundleGroup = bundleGroupRepository.findById(Long.valueOf(bundleGroupId)).get();
                bundleGroup.setOrganisation(toUpdate);
                bundleGroupRepository.save(bundleGroup);
                return bundleGroup;
            }).collect(Collectors.toSet());
            toUpdate.setBundleGroups(newBundleGroups);
        }
    }

    public List<Organisation> getOrganisations() {
        return organisationRepository.findAll(Sort.by(Sort.Order.asc("name")));
    }

    public Optional<Organisation> getOrganisation(String organisationId) {
        return organisationRepository.findById(Long.parseLong(organisationId));
    }

    @Transactional
    public Organisation createOrganisation(Organisation organisationEntity, OrganisationController.OrganisationNoId organisation) {
    	logger.debug("{}: createOrganisation: Organisation created by organisationNo id: {}", CLASS_NAME, organisation);
        Organisation entity = organisationRepository.save(organisationEntity);
        updateMappedBy(entity, organisation);
        return entity;
    }

    public void deleteOrganisation(String organisationId){
        organisationRepository.deleteById(Long.valueOf(organisationId));
    }
}

