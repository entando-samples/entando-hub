package com.entando.hub.catalog.service.specifications;

import com.entando.hub.catalog.persistence.entity.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;

import static org.springframework.data.jpa.domain.Specification.where;

public class BundleQueryManager {

    public static Join<Bundle, BundleGroupVersion> joinBundleGroupVersion(Root<Bundle> root) {
        return root.join(Bundle_.BUNDLE_GROUP_VERSIONS);
    }

    public static Join<BundleGroupVersion, BundleGroup> joinBundleGroup(Root<Bundle> root) {
        return root.join(Bundle_.BUNDLE_GROUP_VERSIONS).join(BundleGroupVersion_.BUNDLE_GROUP);
    }

    public static Specification<Bundle> isInPublicCatalog(boolean publicCatalog){
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            Join<BundleGroupVersion,BundleGroup> bgJoin = joinBundleGroup(root);
            return criteriaBuilder.equal(bgJoin.get(BundleGroup_.PUBLIC_CATALOG), publicCatalog);
        };
    }

    public static Specification<Bundle> addPublishedStatus() {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            Join<Bundle, BundleGroupVersion> bgvJoin = joinBundleGroupVersion(root);
            return criteriaBuilder.equal(bgvJoin.get(BundleGroupVersion_.STATUS), BundleGroupVersion.Status.PUBLISHED);
        };
    }

    public static Specification<Bundle> hasCatalogId(Long catalogId) {
        return (root, query, criteriaBuilder) -> {
            Join<BundleGroupVersion,BundleGroup> bgJoin = joinBundleGroup(root);
            return criteriaBuilder.equal(bgJoin.get(BundleGroup_.CATALOG_ID), catalogId);
        };
    }

    public static Specification<Bundle> hasBundleGroupId(String bundleGroupId) {
        return (root, query, criteriaBuilder) -> {
            Join<Bundle,BundleGroupVersion> bgJoin = joinBundleGroupVersion(root);
            return criteriaBuilder.equal(bgJoin.get(BundleGroupVersion_.ID), bundleGroupId);
        };
    }
    public static Specification<Bundle> descriptorVersionsIn(Set<DescriptorVersion> descriptorVersions) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return root.get(Bundle_.DESCRIPTOR_VERSION).in(descriptorVersions);
        };
    }

    public static Specification<Bundle> hasGitRepoAddress() {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.isNotNull(root.get(Bundle_.GIT_REPO_ADDRESS));
        };
    }
    public static Specification<Bundle> getSpecificationFromFilters(List<Specification<Bundle>> filter) {
        Specification<Bundle> specification = where(filter.remove(0));
        for (Specification<Bundle> input : filter) {
            specification = specification.and(input);
        }
        return specification;
    }
    
}
