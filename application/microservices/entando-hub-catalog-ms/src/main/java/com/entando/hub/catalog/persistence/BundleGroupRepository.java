package com.entando.hub.catalog.persistence;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Set;

public interface BundleGroupRepository extends JpaRepository<BundleGroup, Long>, JpaSpecificationExecutor<BundleGroup> {

    List<BundleGroup> findByOrganisationId(Long organisationId);

    Page<BundleGroup> findByOrganisationId(Long organisationId, Pageable pageable);

    List<BundleGroup> findDistinctByOrganisationAndCategoriesIn(Organisation organisation, Set<Category> categories);
    
    List<BundleGroup> findDistinctByCategoriesIn(Set<Category> categories);
 }
