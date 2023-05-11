package com.entando.hub.catalog.rest.dto;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BundleGroupTemplateDto { //this is a bundle group version containing templates
        @Schema(example = "Entando 7.1 Tutorials")
        private String bundleGroupName;
        private Long bundleGroupVersionId;

        public BundleGroupTemplateDto(BundleGroupVersion bundleGroupVersion, BundleGroup bundleGroup) {
            this.bundleGroupName = bundleGroup.getName();
            this.bundleGroupVersionId = bundleGroupVersion.getId();
        }
    }

