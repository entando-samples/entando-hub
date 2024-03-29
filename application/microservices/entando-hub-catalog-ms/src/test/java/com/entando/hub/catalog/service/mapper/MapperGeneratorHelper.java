package com.entando.hub.catalog.service.mapper;


import com.entando.hub.catalog.persistence.entity.*;
import com.entando.hub.catalog.rest.dto.BundleGroupDto;
import com.entando.hub.catalog.rest.dto.BundleGroupVersionDto;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.entando.hub.catalog.service.mapper.BundleGroupVersionMapperTest.BUNDLE_GROUP_VERSION_ID;

public class MapperGeneratorHelper {

    public static Bundle generateBundleEntityForTest(Long id) {
        Bundle bundle = new Bundle();

        bundle.setId(id);
        bundle.setName(BUNDLE_NAME);
        bundle.setGitRepoAddress(GIT_REPO_ADDRESS);
        bundle.setGitSrcRepoAddress(GIT_SRC_REPO_ADDRESS);
        bundle.setDependencies("dep 1,dep 2");
        bundle.setDescriptorVersion(DescriptorVersion.V5);

        BundleGroupVersion bgv = new BundleGroupVersion();

        bgv.setId(2381L);
        bgv.setDescription("Bundle group Version description");
        bgv.setDocumentationUrl("documentation URL");
        bgv.setVersion("v0.0.1");
        bgv.setDescriptionImage("description image");
        bgv.setStatus(BundleGroupVersion.Status.ARCHIVE);

        java.util.Set<BundleGroupVersion> versions = java.util.stream.Stream.of(bgv, bgv)
                .collect(java.util.stream.Collectors.toSet());
        bundle.setBundleGroupVersions(versions);
        bundle.setDescription(BUNDLE_DESCRIPTION);

        return bundle;
    }

    public static BundleGroup generateBundleGroupEntityForTest(Long id) {
        BundleGroup bg = new BundleGroup();
        Organisation org = new Organisation();
        BundleGroupVersion bgv = new BundleGroupVersion();


        org.setId(ORGANIZATION_ID);
        org.setName(ORGANIZATION_NAME);
        org.setDescription(ORGANIZATION_DESCRIPTION);

        bg.setOrganisation(org);

        bgv.setId(BUNDLE_GROUP_VERSION_ID);
        bgv.setDescription("Bundle group Version description");
        bgv.setDocumentationUrl("documentation URL");
        bgv.setVersion("v0.0.1");
        bgv.setDescriptionImage("description image");
        bgv.setStatus(BundleGroupVersion.Status.ARCHIVE);

        java.util.Set<BundleGroupVersion> versions = java.util.stream.Stream.of(bgv, bgv)
                .collect(java.util.stream.Collectors.toCollection(java.util.HashSet::new));
        bg.setVersion(versions);

        Category cat1 = new Category();

        cat1.setId(CAT_ID_1);
        cat1.setName("category name 1");
        cat1.setDescription("category description 1");

        Category cat2 = new Category();

        cat2.setId(CAT_ID_2);
        cat2.setName("category name 2");
        cat2.setDescription("category description 2");

        java.util.Set<Category> categories = java.util.stream.Stream.of(cat2, cat1).collect(java.util.stream.Collectors.toSet());

        bg.setCategories(categories);
        bg.setName(BUNDLE_GROUP_NAME);
        bg.setId(id);

        return bg;
    }

    public  static BundleGroupDto generateBundleGroupDto(String id) {
        BundleGroupDto dto = new BundleGroupDto();

        dto.setBundleGroupId(id);
        dto.setName(BUNDLE_GROUP_NAME);
        dto.setOrganisationId(ORGANIZATION_ID);
        dto.setOrganisationName(ORGANIZATION_NAME);
        dto.setCategories(Arrays.asList("cat 1", "cat 2"));

        BundleGroupVersionDto bundleGroupVersion = BundleGroupVersionDto.builder()
                .bundleGroupId("191045")
                .description("description")
                .descriptionImage("descriptionImage")
                .version("v123")
                .build();

        dto.setVersionDetails(bundleGroupVersion);
        return dto;
    }

    public static BundleGroup generateBundleGroupEntity(Long id) {
        BundleGroup bg = new BundleGroup();
        Organisation org = new Organisation();
        BundleGroupVersion bgv = new BundleGroupVersion();


        org.setId(ORGANIZATION_ID);
        org.setName(ORGANIZATION_NAME);
        org.setDescription(ORGANIZATION_DESCRIPTION);

        bg.setOrganisation(org);

        bgv.setId(BUNDLE_GROUP_VERSION_ID);
        bgv.setDescription("Bundle group Version description");
        bgv.setDocumentationUrl("documentation URL");
        bgv.setVersion("v0.0.1");
        bgv.setDescriptionImage("description image");
        bgv.setStatus(BundleGroupVersion.Status.ARCHIVE);

        Set<BundleGroupVersion> versions = Stream.of(bgv, bgv)
                .collect(Collectors.toCollection(HashSet::new));
        bg.setVersion(versions);

        Category cat1 = new Category();

        cat1.setId(CAT_ID_1);
        cat1.setName("category name 1");
        cat1.setDescription("category description 1");

        Category cat2 = new Category();

        cat2.setId(CAT_ID_2);
        cat2.setName("category name 2");
        cat2.setDescription("category description 2");

        Set<Category> categories = Stream.of(cat2, cat1).collect(Collectors.toSet());

        bg.setCategories(categories);
        bg.setName(BUNDLE_GROUP_NAME);
        bg.setId(id);

        return bg;
    }

    public static Bundle generateBundleEntity(Long id) {
        Bundle bundle = new Bundle();

        bundle.setId(id);
        bundle.setName(BUNDLE_NAME);
        bundle.setGitRepoAddress(GIT_REPO_ADDRESS);
        bundle.setGitSrcRepoAddress(GIT_SRC_REPO_ADDRESS);
        bundle.setDependencies("dep 1,dep 2");
        bundle.setDescriptorVersion(DescriptorVersion.V5);

        BundleGroupVersion bgv = new BundleGroupVersion();

        bgv.setId(2381L);
        bgv.setDescription("Bundle group Version description");
        bgv.setDocumentationUrl("documentation URL");
        bgv.setVersion("v0.0.1");
        bgv.setDescriptionImage("description image");
        bgv.setStatus(BundleGroupVersion.Status.ARCHIVE);

        Set<BundleGroupVersion> versions = Stream.of(bgv, bgv)
                .collect(Collectors.toSet());
        bundle.setBundleGroupVersions(versions);
        bundle.setDescription(BUNDLE_DESCRIPTION);

        return bundle;
    }

    public static final Long BUNDLE_GROUP_ID = 2677L;
    public static final String BUNDLE_GROUP_ID_STR = "2677";

    public final static String BUNDLE_GROUP_NAME = "bundleGroupName";
    public final static String ORGANIZATION_NAME = "Organization2677Name";
    public final static String ORGANIZATION_DESCRIPTION = "organisation description";
    public  static final Long ORGANIZATION_ID = 27147L;
    public static final String BUNDLE_NAME = "bundle name";
    public static final String BUNDLE_DESCRIPTION = "description";
    public static final String GIT_REPO_ADDRESS = "repo address";
    public static final String GIT_SRC_REPO_ADDRESS = "src repo address";

    public static final Long CAT_ID_1 = 45L;
    public static final Long CAT_ID_2 = 41L;

}
