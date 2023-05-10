package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.BundleRepository;
import com.entando.hub.catalog.persistence.entity.*;
import com.entando.hub.catalog.rest.dto.BundleDto;
import com.entando.hub.catalog.service.mapper.inclusion.BundleStandardMapper;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import com.entando.hub.catalog.service.specifications.BundleQueryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BundleService {

    private final BundleRepository bundleRepository;
    private final SecurityHelperService securityHelperService;

	private final BundleStandardMapper bundleMapper;
    private final Logger logger = LoggerFactory.getLogger(BundleService.class);
    private final String CLASS_NAME = this.getClass().getSimpleName();
    private final PortalUserService portalUserService;

    public BundleService(BundleRepository bundleRepository,
                         SecurityHelperService securityHelperService,
                         BundleStandardMapper bundleMapper,
                         PortalUserService portalUserService) {
        this.bundleRepository = bundleRepository;
        this.securityHelperService = securityHelperService;
        this.bundleMapper = bundleMapper;
        this.portalUserService = portalUserService;
    }

    private Set<DescriptorVersion> getDescriptorVersions(Set<DescriptorVersion> descriptorVersions){
        // Controllers can override but default to all versions otherwise.
        if (descriptorVersions.isEmpty()) {
            return EnumSet.allOf(DescriptorVersion.class);
        }
        return descriptorVersions;
    }

    public Page<Bundle> getBundles(Integer pageNum, Integer pageSize, String bundleGroupId, Set<DescriptorVersion> descriptorVersions, Long catalogId) {

        logger.debug("{}: getBundles: Get bundles paginated by bundle group  id, descriptorVersions, catalogId", CLASS_NAME);

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "name"));
        Pageable paging = PageHelperService.getPaging(pageNum, pageSize, sort);

        Set<DescriptorVersion> parsedDescriptorVersions = getDescriptorVersions(descriptorVersions);
        logger.debug("{}: getBundles: parsed descriptorVersions", CLASS_NAME);

        return this.getBundlesWithFilters(catalogId, parsedDescriptorVersions, bundleGroupId, paging);
    }

    public Page<Bundle> getBundlesWithFilters(Long catalogId, Set<DescriptorVersion> descriptorVersions, String bundleGroupId, Pageable paging){
        List<Specification<Bundle>> filters = new ArrayList<>();
        boolean publicCatalog=false;

        logger.debug("{}: adding filter by published status", CLASS_NAME);
        filters.add(BundleQueryManager.addPublishedStatus());

        logger.debug("{}: adding filter by hasGitRepoAddress", CLASS_NAME);
        filters.add(BundleQueryManager.hasGitRepoAddress());

        logger.debug("{}: adding filter by descriptorVersions", CLASS_NAME);
        filters.add(BundleQueryManager.descriptorVersionsIn(descriptorVersions));

        if(bundleGroupId!=null){
            logger.debug("{}: adding filter by bundleGroupId", CLASS_NAME);
            filters.add(BundleQueryManager.hasBundleGroupId(bundleGroupId));
        }

        if(catalogId!=null) {
            publicCatalog = true;
            logger.debug("{}: adding filter by catalogId", CLASS_NAME);
            filters.add(BundleQueryManager.hasCatalogId(catalogId));
        }

        logger.debug("{}: adding filter by publicCatalog {}", CLASS_NAME, publicCatalog);
        filters.add(BundleQueryManager.isInPublicCatalog(publicCatalog));

        return this.findAllBundleGroups(filters, paging);
    }


    public Page<Bundle> findAllBundleGroups(List<Specification<Bundle>> filters, Pageable paging){
        if(!filters.isEmpty()) {
            return bundleRepository.findAll(BundleQueryManager.getSpecificationFromFilters(filters), paging);
        }else {
            return bundleRepository.findAll(paging);
        }
    }

    public Page<Bundle> getBundles(Integer pageNum, Integer pageSize, String bundleGroupId, Set<DescriptorVersion> descriptorVersions) {
        return getBundles(pageNum, pageSize, bundleGroupId,  descriptorVersions, null);
    }
    public List<Bundle> getBundles() {
        return bundleRepository.findAll();
    }
    public List<Bundle> getPublicBundles() {
        return bundleRepository.findByBundleGroupVersionsBundleGroupPublicCatalogTrue();
    }

    public List<Bundle> getBundles(Optional<String> bundleGroupVersionId) {
        logger.debug("{}: getBundles: Get Bundles by bundle group version id: {}", CLASS_NAME, bundleGroupVersionId);
        if (securityHelperService.isAdmin()) {
			if (bundleGroupVersionId.isPresent()) {
				Long bundleGroupVersionEntityId = Long.parseLong(bundleGroupVersionId.get());
				return bundleRepository.findByBundleGroupVersionsId(bundleGroupVersionEntityId, Sort.by("id"));
			}
			return bundleRepository.findAll();
		}
		else{
            if (bundleGroupVersionId.isPresent()) {
        		Long bundleGroupVersionEntityId = Long.parseLong(bundleGroupVersionId.get());
                return bundleRepository.findByBundleGroupVersionsId(bundleGroupVersionEntityId, Sort.by("id"));
			}
            return this.getPublicBundles();
		}
    }

    public List<Bundle> getBundlesByCatalogId(Long catalogId) {
        return bundleRepository.findByBundleGroupVersionsBundleGroupCatalogId(catalogId);
    }

    public List<Bundle> getBundlesByCatalogIdAndBundleGroupVersionId(Long catalogId, Long bundleGroupVersionId) {
        return bundleRepository.findByBundleGroupVersionsBundleGroupCatalogIdAndBundleGroupVersionsId(catalogId, bundleGroupVersionId);
    }

    public Optional<Bundle> getBundle(String bundleId) {
        logger.debug("{}: getBundle: Get a Bundle by bundle id: {}", CLASS_NAME, bundleId);
        return bundleRepository.findById(Long.parseLong(bundleId));
    }

    public Bundle createBundle(Bundle toSave) {
        logger.debug("{}: createBundle: Create a Bundle: {}", CLASS_NAME, toSave);
        return bundleRepository.save(toSave);
    }

    public void deleteBundle(Bundle toDelete) {
        logger.debug("{}: deleteBundle: Delete a Bundle: {}", CLASS_NAME, toDelete);
        bundleRepository.delete(toDelete);
    }

    /**
     * Save list of bundles
     *
     * @param bundles
     * @return list of saved bundles
     */
    public List<Bundle> createBundles(List<Bundle> bundles) {
        logger.debug("{}: createBundles: Create bundles: {}", CLASS_NAME, bundles);
        return bundleRepository.saveAll(bundles);
    }

    /**
     * Convert list of bundle request into list of Bundle entity.
     *
     * @param bundleRequest
     * @return list of saved bundles or empty list
     */
    public List<Bundle> createBundleEntitiesAndSave(List<BundleDto> bundleRequest) {
        logger.debug("{}: createBundleEntitiesAndSave: Create bundles: {}", CLASS_NAME, bundleRequest);
        try {
            List<Bundle> bundles = new ArrayList<Bundle>();
            if (!CollectionUtils.isEmpty(bundleRequest)) {
                bundleRequest.forEach(element -> bundles.add(bundleMapper.toEntity(element)));
                return createBundles(bundles);
            }
        } catch (Exception e) {
            logger.debug("{}: createBundleEntitiesAndSave: Error {} {}", CLASS_NAME, e.getMessage(), e.getStackTrace());
        }
        return Collections.emptyList();
    }

    public List<Bundle> getBundles(String bundleGroupVersionId, Long catalogId) {
        List<Bundle> bundles;
        Boolean isUserAuthenticated = securityHelperService.isUserAuthenticated();
        if (Boolean.TRUE.equals(isUserAuthenticated)) {
            if (null == bundleGroupVersionId && null == catalogId) {
                if (securityHelperService.isAdmin()) {
                    return this.getBundles();
                }
                bundles = this.getPublicBundles();
                bundles.addAll(this.getBundlesByAuthenticatedUserOrganizations());
                return bundles.stream().distinct().collect(Collectors.toList());
            }

            if (null != catalogId) {
                if (null != bundleGroupVersionId) {
                    bundles = this.getBundlesByCatalogIdAndBundleGroupVersionId(catalogId, Long.valueOf(bundleGroupVersionId));
                } else {
                    bundles = this.getBundlesByCatalogId(catalogId);
                }
            } else {
                bundles = this.getBundles(Optional.of(bundleGroupVersionId));
            }
        } else {
            bundles = this.getBundles(Optional.ofNullable(bundleGroupVersionId));
        }
        return bundles;
    }

    public List<Bundle> getBundlesByAuthenticatedUserOrganizations() {
        Set<Organisation> userOrganizations = portalUserService.getAuthenticatedUserOrganizations();
        List<Bundle> bundles = new ArrayList<>();
        userOrganizations.forEach (organisation -> {
            List<Bundle> organisationBundles = bundleRepository.findByBundleGroupVersionsBundleGroupOrganisation(organisation);
            bundles.addAll(organisationBundles);
        });
        return bundles;
    }

}
