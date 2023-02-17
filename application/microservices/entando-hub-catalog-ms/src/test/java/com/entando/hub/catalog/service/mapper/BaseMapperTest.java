package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.DescriptorVersion;
import com.entando.hub.catalog.persistence.entity.Organisation;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaseMapperTest {

  static Bundle generateBundleEntity(Long id) {
    Bundle bundle = new Bundle();

    bundle.setId(id);
    bundle.setName(BundleMapperTest.BUNDLE_NAME);
    bundle.setGitRepoAddress(BundleMapperTest.GIT_REPO_ADDRESS);
    bundle.setGitSrcRepoAddress(BundleMapperTest.GIT_SRC_REPO_ADDRESS);
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
    bundle.setDescription(BundleMapperTest.BUNDLE_DESCRIPTION);

    return bundle;
  }

  protected BundleGroup generateBundleGroupEntity(Long id) {
    BundleGroup bg = new BundleGroup();
    Organisation org = new Organisation();
    BundleGroupVersion bgv = new BundleGroupVersion();


    org.setId(ORGANIZATION_ID);
    org.setName(ORGANIZATION_NAME);
    org.setDescription(ORGANISATION_DESCRIPTION);

    bg.setOrganisation(org);

    bg.setId(id);
    bgv.setDescription("Bundle group Version description");
    bgv.setDocumentationUrl("documentation URL");
    bgv.setVersion("v0.0.1");
    bgv.setDescriptionImage("description image");
    bgv.setStatus(BundleGroupVersion.Status.ARCHIVE);

    Set<BundleGroupVersion> versions = Stream.of(bgv, bgv)
      .collect(Collectors.toCollection(HashSet::new));
    bg.setVersion(versions);

    Category cat1 = new Category();

    cat1.setId(27147L); // FIXME use constant
    cat1.setName("category name 1");
    cat1.setDescription("category description 1");

    Category cat2 = new Category();

    cat2.setId(191045L); // FIXME use constant
    cat2.setName("category name 2");
    cat2.setDescription("category description 2");

    Set<Category> categories = Stream.of(cat2, cat1).collect(Collectors.toSet());
    bg.setCategories(categories);

    bg.setName(BUNDLE_GROUP_NAME);

    return bg;
  }

  public static final Long BUNDLE_GROUP_ID = 2677L;
  public static final String BUNDLE_GROUP_ID_STR = "2677";
  public final static String BUNDLE_GROUP_NAME = "bundleGroupName";
  public final static String ORGANIZATION_NAME = "Organization2677Name";
  public final static String ORGANISATION_DESCRIPTION = "organisation description";
  public  static final Long ORGANIZATION_ID = 27147L;
  public  static final String ORGANIZATION_ID_STR = "27147";

  public static final String BUNDLE_NAME = "bundle name";
  public static final String BUNDLE_DESCRIPTION = "description";
  public static final String GIT_REPO_ADDRESS = "repo address";
  public static final String GIT_SRC_REPO_ADDRESS = "src repo address";

}
