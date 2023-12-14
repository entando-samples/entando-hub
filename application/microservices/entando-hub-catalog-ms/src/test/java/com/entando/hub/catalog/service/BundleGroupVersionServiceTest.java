package com.entando.hub.catalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import antlr.collections.impl.IntRange;
import com.entando.hub.catalog.persistence.BundleGroupRepository;
import com.entando.hub.catalog.persistence.BundleGroupVersionRepository;
import com.entando.hub.catalog.persistence.BundleRepository;
import com.entando.hub.catalog.persistence.CategoryRepository;
import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.Category;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.response.BundleGroupVersionFilteredResponseView;
import com.entando.hub.catalog.rest.PagedContent;
import com.entando.hub.catalog.rest.dto.BundleGroupVersionDto;
import com.entando.hub.catalog.service.dto.BundleGroupVersionEntityDto;
import com.entando.hub.catalog.service.mapper.BundleGroupVersionMapper;
import com.entando.hub.catalog.service.mapper.BundleGroupVersionMapperImpl;
import com.entando.hub.catalog.service.mapper.inclusion.BundleGroupVersionEntityMapper;
import com.entando.hub.catalog.service.mapper.inclusion.BundleGroupVersionEntityMapperImpl;
import com.entando.hub.catalog.service.mapper.inclusion.BundleGroupVersionStandardMapper;
import com.entando.hub.catalog.service.mapper.inclusion.BundleGroupVersionStandardMapperImpl;
import com.entando.hub.catalog.testhelper.TestHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.Silent.class)
@ComponentScan(basePackageClasses = {BundleGroupVersionMapper.class, BundleGroupVersionMapperImpl.class,
		BundleGroupVersionStandardMapper.class, BundleGroupVersionStandardMapperImpl.class,
		BundleGroupVersionEntityMapper.class, BundleGroupVersionEntityMapperImpl.class })
public class BundleGroupVersionServiceTest {

	@InjectMocks
	BundleGroupVersionService bundleGroupVersionService;
	@Mock
	BundleGroupVersionRepository bundleGroupVersionRepository;
	@Mock
	BundleService bundleService;
	@Mock
	BundleGroupRepository bundleGroupRepository;
	@Mock
	BundleRepository bundleRepository;
	@Mock
	CategoryRepository categoryRepository;
	@Mock
	Environment environment;

	@Spy
	BundleGroupVersionStandardMapper bundleGroupVersionStandardMapper = new BundleGroupVersionStandardMapperImpl();

	@Spy
	private BundleGroupVersionEntityMapper bundleGroupVersionEntityMapper = new BundleGroupVersionEntityMapperImpl();

	private static final Long BUNDLE_GROUP_VERSION_ID = 1002L;
    private static final String BUNDLE_GROUP_VERSION_DESCRIPTION = "Test Bundle Group Version Decription";
    private static final String BUNDLE_GROUP_VERSION_VERSION = "v1.0.0";
    private static final String BUNDLE_GROUP_VERSION_DES_IMAGE = "TEST IMAGE CONTENTS";
    private static final String BUNDLE_GROUP_VERSION_DOC_URL = "testdocurl@testdoc.com";
    private static final Long BUNDLE_GROUP_ID = 1000L;
    private static final String BUNDLE_GROUP_NAME = "Test Bundle Group Name";
    private static final Long BUNDLE_ID = 1001L; 
	private static final String BUNDLE_NAME = "Test Bundle Name";
	private static final String BUNDLE_DESCRIPTION = "Test Bundle Decription";
	private static final String BUNDLE_GIT_REPO_ADDRESS = "https://github.com/entando/TEST-portal.git";
	private static final String BUNDLE_DEPENDENCIES = "Test Dependencies";
	private static final Long CATEGORY_ID = 3000L;
    private static final String CATEGORY_NAME = "Test Category Name";
    private static final String CATEGORY_DESCRIPTION = "Test Category Description";
    private static final Long ORG_ID = 2000L;
    private static final String ORG_NAME = "Test Org Name";
    private static final String ORG_DESCRIOPTION = "Test Org Decription";
	private static final Long CATALOG_ID = 1L;

	private static final String BUNDLE_SRC_REPO_ADDRESS = "bundle_src";
	@Test
	public void getBundleGroupVersionTest() {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		String bundleGroupVersionId = String.valueOf(bundleGroupVersion.getId());
		Optional<BundleGroupVersion> optbundleGroupVersion = Optional.of(bundleGroupVersion);
		when(bundleGroupVersionRepository.findById(Long.parseLong(bundleGroupVersionId))).thenReturn(optbundleGroupVersion);
		Optional<BundleGroupVersion> bundleGroupVersionResult = bundleGroupVersionService.getBundleGroupVersion(bundleGroupVersionId);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(optbundleGroupVersion.get().getId(),bundleGroupVersionResult.get().getId());
	}
	
	@Test
	public void createBundleGroupVersionTest() {
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		BundleGroup bundleGroup = createBundleGroup();
		bundleGroupVersion.setBundleGroup(bundleGroup);
		Bundle bundle = createBundle();

		bundleGroupVersion.setBundles(Set.of(bundle));
		List<Bundle> bundlesList = new ArrayList<>();
		bundlesList.add(bundle);		
		BundleGroupVersionDto bundleGroupVersionView1 = bundleGroupVersionStandardMapper.toViewDto(bundleGroupVersion);// new BundleGroupVersionView(bundleGroupVersion);
		String bundleId = bundle.getId().toString();
		BundleGroupVersion bundleGroupVersion2 = new BundleGroupVersion();
		bundleGroupVersion2.setId(1001L);
		bundleGroupVersion2.setDescription("Test Description");
		bundleGroupVersion2.setStatus(BundleGroupVersion.Status.ARCHIVE);
		bundleGroupVersion2.setVersion("v1.0.0");
		bundleGroupVersion2.setBundleGroup(bundleGroup);
		bundleGroupVersion2.setBundles(Set.of(bundle));
		BundleGroupVersionDto bundleGroupVersionView2 =  bundleGroupVersionStandardMapper.toViewDto(bundleGroupVersion2); // new BundleGroupVersionView(bundleGroupVersion2);
		
		when(bundleRepository.save(bundle)).thenReturn(bundle);
		when(bundleRepository.findById(Long.valueOf(bundleId))).thenReturn(Optional.of(bundle));
		
		//Case 1: Creating a Published Version
		when(bundleService.createBundleEntitiesAndSave(bundleGroupVersionView1.getBundles())).thenReturn(bundlesList);
		when(bundleGroupVersionRepository.save(bundleGroupVersion)).thenReturn(bundleGroupVersion);
		when(bundleRepository.findByBundleGroupVersions(bundleGroupVersion, null)).thenReturn(bundlesList);
		when(bundleGroupVersionRepository.findByBundleGroupAndStatus(bundleGroupVersion.getBundleGroup(), BundleGroupVersion.Status.PUBLISHED)).thenReturn(bundleGroupVersion);
		BundleGroupVersion bundleGroupVersionResult = bundleGroupVersionService.createBundleGroupVersion(bundleGroupVersion, bundleGroupVersionView1);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(bundleGroupVersionResult.getId(), bundleGroupVersion.getId());
		
		//Case 2: Creating a non-published version (any other status)
		when(bundleService.createBundleEntitiesAndSave(bundleGroupVersionView2.getBundles())).thenReturn(bundlesList);
		when(bundleGroupVersionRepository.save(bundleGroupVersion2)).thenReturn(bundleGroupVersion2);
		when(bundleRepository.findByBundleGroupVersions(bundleGroupVersion2, null)).thenReturn(bundlesList);
		when(bundleGroupVersionRepository.save(bundleGroupVersion2)).thenReturn(bundleGroupVersion2);
		BundleGroupVersion bundleGroupVersionResult2 = bundleGroupVersionService.createBundleGroupVersion(bundleGroupVersion2, bundleGroupVersionView2);
		assertNotNull(bundleGroupVersionResult2);
		assertEquals(bundleGroupVersionResult2.getId(), bundleGroupVersion2.getId());
		
		Set<BundleGroupVersion> versionsSet = new HashSet<>();
		versionsSet.add(bundleGroupVersion);
		versionsSet.add(bundleGroupVersion2);
		bundle.setBundleGroupVersions(versionsSet);
		
		//Case 3: Bundle Group Version has bundles
		bundleGroupVersion.setBundles(null);
		bundleGroupVersionView1.setBundles(null);
		when(bundleGroupVersionRepository.save(bundleGroupVersion)).thenReturn(bundleGroupVersion);
		BundleGroupVersion bundleGroupVersionResult3 = bundleGroupVersionService.createBundleGroupVersion(bundleGroupVersion, bundleGroupVersionView1);
		assertNotNull(bundleGroupVersionResult3);
		assertEquals(bundleGroupVersionResult3.getId(), bundleGroupVersion.getId());
	}
	
	@Test
	@Ignore
	public void getBundleGroupVersionsByOrganisationTest() {
		Integer pageNum = 1;
		Integer pageSize = 12;
		Sort.Order order = new Sort.Order(Sort.Direction.DESC, "lastUpdated");
		Pageable paging = PageRequest.of(pageNum, pageSize, Sort.by(order));
		
		Category category = createCategory();
		Set<Category> categories = Set.of(category);
		String[] categoryIds = new String[]{category.getId().toString()};
		
		String[] statuses = new String[]{BundleGroupVersion.Status.PUBLISHED.toString()};
		Set<BundleGroupVersion.Status> statusesSet = Set.of(BundleGroupVersion.Status.PUBLISHED);
		
		List<BundleGroupVersion> bundleGroupVersionsList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		Organisation organisation = createOrganisation();
		List<BundleGroup> bundleGroupsList = new ArrayList<>();
		BundleGroup bundleGroup = createBundleGroup();
		bundleGroupVersion.setBundleGroup(bundleGroup);
		bundleGroup.setOrganisation(organisation);	
		bundleGroupsList.add(bundleGroup);
		bundleGroupVersionsList.add(bundleGroupVersion);
		
		String organisationId = organisation.getId().toString();
		Page<BundleGroupVersion> response = new PageImpl<>(bundleGroupVersionsList);

		when(environment.getProperty("HUB_GROUP_DETAIL_BASE_URL")).thenReturn("http://hubdev.okd-entando.org/entando-de-app/en/test.page#/");
		
		//Case 1: organisation is present
		when(bundleGroupRepository.findDistinctByOrganisationAndCategoriesIn(organisation, categories)).thenReturn(bundleGroupsList);
		when(bundleGroupVersionRepository.findByBundleGroupInAndStatusIn(bundleGroupsList, statusesSet, paging)).thenReturn(response);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> bundleGroupVersionResult = bundleGroupVersionService.getBundleGroupVersions(pageNum, pageSize, Optional.of(organisationId), categoryIds, statuses);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(bundleGroupVersion.getId(), bundleGroupVersionResult.getPayload().get(0).getBundleGroupVersionId());
		
		//Case 2: organisation is not present and pageSize equal to 0
		pageSize = 0;
		Pageable paging2 = Pageable.unpaged();
		when(bundleGroupRepository.findDistinctByCategoriesIn(categories)).thenReturn(bundleGroupsList);
		when(bundleGroupVersionRepository.findByBundleGroupInAndStatusIn(bundleGroupsList, statusesSet, paging2)).thenReturn(response);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> bundleGroupVersionResult2 = bundleGroupVersionService.getBundleGroupVersions(pageNum, pageSize, Optional.empty(), categoryIds, statuses);
		assertNotNull(bundleGroupVersionResult2);
		assertEquals(bundleGroupVersion.getId(), bundleGroupVersionResult2.getPayload().get(0).getBundleGroupVersionId());
	}
	
	@Test
	@Ignore
	public void getBundleGroupVersionsByStatusesTest() {
		Integer pageNum = 1;
		Integer pageSize = 12;
		Sort.Order order = new Sort.Order(Sort.Direction.DESC, "lastUpdated");
		Pageable paging = PageRequest.of(pageNum, pageSize, Sort.by(order));
		
		Category category = createCategory();
		category.setBundleGroups(null);
		String[] statuses = new String[]{BundleGroupVersion.Status.PUBLISHED.toString()};
		Set<BundleGroupVersion.Status> statusesSet = Set.of(BundleGroupVersion.Status.PUBLISHED);
		
		List<BundleGroupVersion> bundleGroupVersionsList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();

		List<BundleGroup> bundleGroupsList = new ArrayList<>();
		BundleGroup bundleGroup = createBundleGroup();
		bundleGroupVersion.setBundleGroup(bundleGroup);
		bundleGroupsList.add(bundleGroup);
		bundleGroupVersionsList.add(bundleGroupVersion);
		
		when(this.environment.getProperty("HUB_GROUP_DETAIL_BASE_URL")).thenReturn("http://hubdev.okd-entando.org/entando-de-app/en/test.page#/");
		
		//Case 1: pageSize not equal to 0
		Page<BundleGroupVersion> response = new PageImpl<>(bundleGroupVersionsList);
		when(bundleGroupVersionRepository.findByBundleGroupAndStatusIn(bundleGroup, statusesSet, paging)).thenReturn(response);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> bundleGroupVersionResult = bundleGroupVersionService.getBundleGroupVersions(pageNum, pageSize, statuses, bundleGroup);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(bundleGroupVersion.getId(), bundleGroupVersionResult.getPayload().get(0).getBundleGroupVersionId());
		
		//Case 2: pageSize equal to 0
		pageSize = 0;
		Pageable paging2 = Pageable.unpaged();
		when(bundleGroupVersionRepository.findByBundleGroupAndStatusIn(bundleGroup, statusesSet, paging2)).thenReturn(new PageImpl<>(new ArrayList<BundleGroupVersion>()));
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> bundleGroupVersionResult2 = bundleGroupVersionService.getBundleGroupVersions(pageNum, pageSize, statuses, bundleGroup);
		assertNotNull(bundleGroupVersionResult2);
	}

	@Test
	public void deleteBundleGroupVersionTest() {
		Category category = createCategory();
		Set<Category> categories = new HashSet<>();
		categories.add(category);
		BundleGroup bundleGroup = createBundleGroup();
		bundleGroup.setCategories(categories);
		Set<BundleGroup> bundleGroups = new HashSet<>();
		bundleGroups.add(bundleGroup);		
		category.setBundleGroups(bundleGroups);
		Bundle bundle = createBundle();
		List<Bundle> bundlesList = new ArrayList<>();
		bundlesList.add(bundle);	
		
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		bundleGroupVersion.setBundleGroup(bundleGroup);	
		bundleGroupVersion.setBundles(new HashSet<>(bundlesList));
		Set<BundleGroupVersion> bundleGroupVersions = new HashSet<>();
		bundleGroupVersions.add(bundleGroupVersion);
		bundleGroup.setVersion(bundleGroupVersions);	
		bundle.setBundleGroupVersions(bundleGroupVersions);	
		when(categoryRepository.save(category)).thenReturn(category);
		when(bundleRepository.save(bundle)).thenReturn(bundle);
		bundleGroupVersionService.deleteBundleGroupVersion(Optional.of(bundleGroupVersion));
		assertEquals(0, bundle.getBundleGroupVersions().size());
		
		//Check that Exception is thrown
		bundleGroupVersion.setBundleGroup(null);	
		bundleGroupVersionService.deleteBundleGroupVersion(Optional.of(bundleGroupVersion));
	}
	
	@Test
	public void getBundleGroupVersionsByBundleGroupTest() {
		List<BundleGroupVersion> bundleGroupVersionList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		BundleGroup bundleGroup = createBundleGroup();
		bundleGroupVersion.setBundleGroup(bundleGroup);
		bundleGroupVersionList.add(bundleGroupVersion);
		when(bundleGroupVersionRepository.findByBundleGroupAndVersion(bundleGroup, bundleGroupVersion.getVersion())).thenReturn(bundleGroupVersionList);
		List<BundleGroupVersion> bundleGroupVersionResult = bundleGroupVersionService.getBundleGroupVersions(bundleGroup, bundleGroupVersion.getVersion());
		assertNotNull(bundleGroupVersionResult);
		assertEquals(bundleGroupVersionList.get(0).getId(),bundleGroupVersionResult.get(0).getId());
	}
	
	@Test
	public void isBundleGroupEditableTest() {
		List<BundleGroupVersion> bundleGroupVersionList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		BundleGroup bundleGroup = createBundleGroup();
		bundleGroupVersion.setBundleGroup(bundleGroup);
		bundleGroupVersionList.add(bundleGroupVersion);
		when(bundleGroupVersionRepository.countByBundleGroup(bundleGroup)).thenReturn(1);
		Boolean bundleGroupVersionResult = bundleGroupVersionService.isBundleGroupEditable(bundleGroup);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(true,bundleGroupVersionResult);
	}
	
	@Test
	public void isBundleGroupEditableTestFails() {
		List<BundleGroupVersion> bundleGroupVersionList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		BundleGroup bundleGroup = createBundleGroup();
		bundleGroupVersion.setBundleGroup(bundleGroup);
		bundleGroupVersionList.add(bundleGroupVersion);
		when(bundleGroupVersionRepository.countByBundleGroup(bundleGroup)).thenReturn(7);
		Boolean bundleGroupVersionResult = bundleGroupVersionService.isBundleGroupEditable(bundleGroup);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(false,bundleGroupVersionResult);
	}
	
	@Test
	public void canAddNewVersionTest() {
		BundleGroup bundleGroup = createBundleGroup();
		List<BundleGroupVersion> bundleGroupVersionList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		bundleGroupVersion.setBundleGroup(bundleGroup);	
		bundleGroupVersionList.add(bundleGroupVersion);
		
		when(bundleGroupVersionRepository.getByBundleGroupAndStatuses(bundleGroup.getId())).thenReturn(bundleGroupVersionList);
		Boolean bundleGroupVersionResult = bundleGroupVersionService.canAddNewVersion(bundleGroup);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(true, bundleGroupVersionResult);
	}
	
	@Test
	public void canAddNewVersionTestFails() {
		BundleGroup bundleGroup = createBundleGroup();
		List<BundleGroupVersion> bundleGroupVersionList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		bundleGroupVersion.setBundleGroup(bundleGroup);	
		bundleGroupVersionList.add(bundleGroupVersion);
		BundleGroupVersion bundleGroupVersion2 = createBundleGroupVersion();
		bundleGroupVersion.setBundleGroup(bundleGroup);	
		bundleGroupVersionList.add(bundleGroupVersion2);
		
		when(bundleGroupVersionRepository.getByBundleGroupAndStatuses(bundleGroup.getId())).thenReturn(bundleGroupVersionList);
		Boolean bundleGroupVersionResult = bundleGroupVersionService.canAddNewVersion(bundleGroup);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(false, bundleGroupVersionResult);
	}

	@Test
	@Ignore
	public void searchBundleGroupVersionsTest() {
		List<Category> categoryList = new ArrayList<>();
		Category category = createCategory();
		category.setBundleGroups(null);
		categoryList.add(category);
		String[] categoryIds = new String[]{category.getId().toString()};
		Set<Category> categoriesSet = new HashSet<>();
		categoriesSet.add(category);
		
		List<BundleGroupVersion> bundleGroupVersionsList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		List<BundleGroup> bundleGroupsList = new ArrayList<>();
		BundleGroup bundleGroup = createBundleGroup();
		bundleGroup.setCategories(Set.of(category));
		bundleGroupVersion.setBundleGroup(bundleGroup);
		bundleGroupVersionsList.add(bundleGroupVersion);
		Organisation organisation = createOrganisation();
		bundleGroup.setOrganisation(organisation);	
		bundleGroupsList.add(bundleGroup);
		Long organisationId = organisation.getId();
	
		Integer page = 0;
		Integer pageSize = 89;
		Pageable paging = PageRequest.of(page, pageSize, Sort.by(new Sort.Order(Sort.Direction.ASC, "bundleGroup.name")));
		String[] statuses = new String[]{BundleGroupVersion.Status.PUBLISHED.toString()};
		Set<BundleGroupVersion.Status> statusSet = Arrays.stream(statuses).map(BundleGroupVersion.Status::valueOf).collect(Collectors.toSet());
		List<BundleGroupVersionFilteredResponseView> list = new ArrayList<>();
		BundleGroupVersionFilteredResponseView viewObj = new BundleGroupVersionFilteredResponseView();
		viewObj.setBundleGroupVersionId(bundleGroupVersion.getId());
		viewObj.setDescription(bundleGroupVersion.getDescription());
		viewObj.setStatus(bundleGroupVersion.getStatus());
		viewObj.setVersion(bundleGroupVersion.getVersion());
		viewObj.setOrganisationName(bundleGroupVersion.getBundleGroup().getOrganisation().getName());
		viewObj.setOrganisationId(bundleGroupVersion.getBundleGroup().getOrganisation().getId());
		list.add(viewObj);
	
		Page<BundleGroupVersion> response = new PageImpl<>(bundleGroupVersionsList);
		when(bundleGroupRepository.findAll()).thenReturn(bundleGroupsList);
		when(bundleGroupRepository.findDistinctByOrganisationAndCategoriesIn(organisation, categoriesSet)).thenReturn(bundleGroupsList);
		when(bundleGroupRepository.findDistinctByCategoriesIn(categoriesSet)).thenReturn(bundleGroupsList);
		when(bundleGroupVersionRepository.findByBundleGroupInAndStatusIn(bundleGroupsList, statusSet, paging)).thenReturn(response);
		
		//Case 1: all optional parameters given
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> bundleGroupVersionResult = bundleGroupVersionService.searchBundleGroupVersions(page, pageSize, organisationId, null, categoryIds, statuses, null, true);
		assertNotNull(bundleGroupVersionResult);
		assertEquals(bundleGroupVersionResult.getPayload().get(0).getBundleGroupVersionId(), bundleGroupVersionsList.get(0).getId());
		
		//Case 2: when search text is not empty
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> bundleGroupVersionResult2 = bundleGroupVersionService.searchBundleGroupVersions(page, pageSize, organisationId, null, categoryIds, statuses, "New", true);
		assertNotNull(bundleGroupVersionResult2);
		assertEquals(bundleGroupVersionResult2.getPayload().get(0).getBundleGroupVersionId(), bundleGroupVersionsList.get(0).getId());
		
		//Case 3: when search text is not empty, organisation id not present
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> bundleGroupVersionResult3 = bundleGroupVersionService.searchBundleGroupVersions(page, pageSize, null, null, categoryIds, statuses, "New", true);
		assertNotNull(bundleGroupVersionResult3);
		assertEquals(bundleGroupVersionResult3.getPayload().get(0).getBundleGroupVersionId(), bundleGroupVersionsList.get(0).getId());
		
		//Case 4: when search text is not null but empty
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> bundleGroupVersionResult4 = bundleGroupVersionService.searchBundleGroupVersions(page, pageSize, organisationId, null, categoryIds, statuses, "", true);
		assertNotNull(bundleGroupVersionResult4);
		assertEquals(bundleGroupVersionResult4.getPayload().get(0).getBundleGroupVersionId(), bundleGroupVersionsList.get(0).getId());
	
		//Case 5: organisation id not present
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> bundleGroupVersionResult5 = bundleGroupVersionService.searchBundleGroupVersions(page, pageSize, null, null, categoryIds, statuses, null, true);
		assertNotNull(bundleGroupVersionResult5);
		assertEquals(bundleGroupVersionResult5.getPayload().get(0).getBundleGroupVersionId(), bundleGroupVersionsList.get(0).getId());
	
		//Case 6: page number pageSize == 0
		pageSize = 0;
		paging = Pageable.unpaged();
		when(bundleGroupVersionRepository.findByBundleGroupInAndStatusIn(bundleGroupsList, statusSet, paging)).thenReturn(response);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> bundleGroupVersionResult6 = bundleGroupVersionService.searchBundleGroupVersions(page, pageSize, organisationId, null, categoryIds, statuses, null, true);
		assertNotNull(bundleGroupVersionResult6);
		assertEquals(bundleGroupVersionResult6.getPayload().get(0).getBundleGroupVersionId(), bundleGroupVersionsList.get(0).getId());
		
		//Case 7: bundleGroups empty
		bundleGroupsList = new ArrayList<>();
		when(bundleGroupVersionRepository.findByBundleGroupInAndStatusIn(bundleGroupsList, statusSet, paging)).thenReturn(response);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> bundleGroupVersionResult7 = bundleGroupVersionService.searchBundleGroupVersions(page, pageSize, null, null, categoryIds, statuses, "Old", true);
		assertNotNull(bundleGroupVersionResult7);
		assertEquals(bundleGroupVersionResult7.getPayload().get(0).getBundleGroupVersionId(), bundleGroupVersionsList.get(0).getId());
	}

	@Test
	public void getPrivateCatalogPublishedBundleGroupVersionsTest(){
		Long userCatalogId =1L;
		List<BundleGroupVersion> bundleGroupVersionsList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		BundleGroup bundleGroup = createPrivateCatalogBundleGroup();
		bundleGroupVersion.setBundleGroup(bundleGroup);
		bundleGroupVersionsList.add(bundleGroupVersion);
		Page<BundleGroupVersion> response = new PageImpl<>(bundleGroupVersionsList);
		when(bundleGroupVersionRepository.getPrivateCatalogPublished(eq(userCatalogId), any())).thenReturn(response);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> privateCatalogPublishedBundleGroupVersions = bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupVersions(userCatalogId, 1, 25);
		assertNotNull(privateCatalogPublishedBundleGroupVersions);
	}
	@Test
	public void getPublicCatalogPublishedBundleGroupVersionsTest(){
		List<BundleGroupVersion> bundleGroupVersionsList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersion();
		BundleGroup bundleGroup = createBundleGroup();
		bundleGroupVersion.setBundleGroup(bundleGroup);
		bundleGroupVersionsList.add(bundleGroupVersion);
		Page<BundleGroupVersion> response = new PageImpl<>(bundleGroupVersionsList);
		when(bundleGroupVersionRepository.getPublicCatalogPublished(any())).thenReturn(response);
		PagedContent<BundleGroupVersionFilteredResponseView, BundleGroupVersionEntityDto> privateCatalogPublishedBundleGroupVersions = bundleGroupVersionService.getPublicCatalogPublishedBundleGroupVersions(1, 25);
		assertNotNull(privateCatalogPublishedBundleGroupVersions);
	}

	@Test
	public void getPrivateCatalogPublishedBundleGroupTemplatesTest(){
		List<BundleGroupVersion> response = createBundleGroupVersionTemplateList();
		when(bundleGroupVersionRepository.getPrivateCatalogPublishedTemplates(CATALOG_ID)).thenReturn(response);
		List<BundleGroupVersion> templates = bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupTemplates(CATALOG_ID);
		assertNotNull(templates);
	}

	@Test
	public void getPublicCatalogPublishedBundleGroupTemplatesTest(){
		List<BundleGroupVersion> response = createBundleGroupVersionTemplateList();
		when(bundleGroupVersionRepository.getPublicCatalogPublishedTemplates()).thenReturn(response);
		List<BundleGroupVersion> templates = bundleGroupVersionService.getPublicCatalogPublishedBundleGroupTemplates();
		assertNotNull(templates);
	}

	@Test
	public void getPrivateCatalogPublishedBundleGroupTemplatesByNameTest(){
		List<BundleGroupVersion> response = createBundleGroupVersionTemplateList();
		when(bundleGroupVersionRepository.getPrivateCatalogPublishedTemplatesByName(CATALOG_ID, BUNDLE_GROUP_NAME)).thenReturn(response);
		List<BundleGroupVersion> templates = bundleGroupVersionService.getPrivateCatalogPublishedBundleGroupTemplatesByName(CATALOG_ID, BUNDLE_GROUP_NAME);
		assertNotNull(templates);
	}

	@Test
	public void getPublicCatalogPublishedBundleGroupTemplatesByNameTest(){
		List<BundleGroupVersion> response = createBundleGroupVersionTemplateList();
		when(bundleGroupVersionRepository.getPublicCatalogPublishedTemplatesByName(BUNDLE_GROUP_NAME)).thenReturn(response);
		List<BundleGroupVersion> templates = bundleGroupVersionService.getPublicCatalogPublishedBundleGroupTemplatesByName(BUNDLE_GROUP_NAME);
		assertNotNull(templates);
	}

	@Test
	public void getPublicCatalogPublishedBundleTemplatesTest(){
		List<BundleGroupVersion> response = createBundleGroupVersionTemplateList();
		when(bundleGroupVersionRepository.getPublicCatalogPublishedTemplates()).thenReturn(response);
		List<BundleGroupVersion> templates = bundleGroupVersionService.getPublicCatalogPublishedBundleTemplates();
		assertNotNull(templates);
	}

	@Test
	public void getPrivateCatalogPublishedBundleTemplatesByIdTest(){
		List<BundleGroupVersion> response = createBundleGroupVersionTemplateList();
		when(bundleGroupVersionRepository.getPrivateCatalogPublishedTemplatesById(CATALOG_ID, BUNDLE_GROUP_VERSION_ID)).thenReturn(response);
		List<BundleGroupVersion> templates = bundleGroupVersionService.getPrivateCatalogPublishedBundleTemplatesById(
				CATALOG_ID, BUNDLE_GROUP_VERSION_ID);
		assertNotNull(templates);
	}

	@Test
	public void getPublicCatalogPublishedBundleTemplatesByIdTest(){
		List<BundleGroupVersion> response = createBundleGroupVersionTemplateList();
		when(bundleGroupVersionRepository.getPublicCatalogPublishedTemplatesById(BUNDLE_GROUP_VERSION_ID)).thenReturn(response);
		List<BundleGroupVersion> templates = bundleGroupVersionService.getPublicCatalogPublishedBundleTemplatesById(
				BUNDLE_GROUP_VERSION_ID);
		assertNotNull(templates);
	}

 	@Test
	public void getPrivateCatalogPublishedBundleTemplatesTest(){
		List<BundleGroupVersion> response = createBundleGroupVersionTemplateList();
		when(bundleGroupVersionRepository.getPrivateCatalogPublishedTemplates(CATALOG_ID)).thenReturn(response);
		List<BundleGroupVersion> templates = bundleGroupVersionService.getPrivateCatalogPublishedBundleTemplates(
				CATALOG_ID);
		assertNotNull(templates);
	}

	@Test
	public void shouldCorrectlyMapEntityToDto() {

		final int totalElements = 13;
		final int pageSize = 12;

		Pageable pageable = mock(Pageable.class);
		when(pageable.getPageSize()).thenReturn(pageSize);
		when(pageable.getOffset()).thenReturn(0L);
		when(pageable.getPageNumber()).thenReturn(0);

		final List<BundleGroupVersion> bundleGroupVersions = IntStream.range(0, pageSize)
				.mapToObj(i -> TestHelper.stubBundleGroupVersion(i + ""))
				.collect(Collectors.toList());
		Page<BundleGroupVersion> page = new PageImpl<BundleGroupVersion>(bundleGroupVersions, pageable, totalElements);

		final Page<BundleGroupVersionEntityDto> pageDto = bundleGroupVersionService.convertToDto(
				page);

		assertThat(pageDto.getTotalElements()).isEqualTo(totalElements);
		assertThat(pageDto.getTotalPages()).isEqualTo(2);
		assertThat(pageDto.getNumber()).isEqualTo(0);
		assertThat(pageDto.getNumberOfElements()).isEqualTo(pageSize);

		final List<BundleGroupVersionEntityDto> bgvDtos = pageDto.getContent();
		IntStream.range(0, bundleGroupVersions.size())
						.forEach(i -> {
							final BundleGroupVersion expected = bundleGroupVersions.get(i);
							final BundleGroupVersionEntityDto actual = bgvDtos.get(i);
							assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
							assertThat(actual.getDocumentationUrl()).isEqualTo(expected.getDocumentationUrl());
							assertThat(actual.getVersion()).isEqualTo(expected.getVersion());
							assertThat(actual.getDescriptionImage()).isEqualTo(expected.getDescriptionImage());
							assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
						});
	}

	private List<BundleGroupVersion> createBundleGroupVersionTemplateList() {
		List<BundleGroupVersion> bgvList = new ArrayList<>();
		BundleGroupVersion bundleGroupVersion = createBundleGroupVersionTemplate();
		bgvList.add(bundleGroupVersion);
		return bgvList;
	}

	private BundleGroupVersion createBundleGroupVersionTemplate() {
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroupVersion.setBundleGroup(bundleGroup);
		Bundle bundle = new Bundle();
		bundleGroup.setName(BUNDLE_GROUP_NAME);
		bundle.setName(BUNDLE_NAME);
		bundle.setGitSrcRepoAddress(BUNDLE_SRC_REPO_ADDRESS);
		bundleGroupVersion.setId(BUNDLE_GROUP_VERSION_ID);
		bundleGroup.setId(BUNDLE_GROUP_ID);
		bundle.setId(BUNDLE_ID);
		Set<Bundle> bundles = new HashSet<>();
		bundles.add(bundle);
		bundleGroupVersion.setBundles(bundles);
		return bundleGroupVersion;
	}

	private BundleGroupVersion createBundleGroupVersion() {
		BundleGroupVersion bundleGroupVersion = new BundleGroupVersion();
		bundleGroupVersion.setId(BUNDLE_GROUP_VERSION_ID);
		bundleGroupVersion.setDescription(BUNDLE_GROUP_VERSION_DESCRIPTION);
		bundleGroupVersion.setStatus(BundleGroupVersion.Status.PUBLISHED);
		bundleGroupVersion.setVersion(BUNDLE_GROUP_VERSION_VERSION);
		bundleGroupVersion.setDescriptionImage(BUNDLE_GROUP_VERSION_DES_IMAGE);
		bundleGroupVersion.setDocumentationUrl(BUNDLE_GROUP_VERSION_DOC_URL);
		return bundleGroupVersion;
	}
	
	private BundleGroup createBundleGroup() {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(BUNDLE_GROUP_ID);
		bundleGroup.setName(BUNDLE_GROUP_NAME);
		bundleGroup.setPublicCatalog(true);
		return bundleGroup;
	}
	private BundleGroup createPrivateCatalogBundleGroup() {
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setId(BUNDLE_GROUP_ID);
		bundleGroup.setName(BUNDLE_GROUP_NAME);
		bundleGroup.setPublicCatalog(false);
		return bundleGroup;
	}

	private Bundle createBundle() {
		Bundle bundle = new Bundle();
		bundle.setId(BUNDLE_ID);
		bundle.setName(BUNDLE_NAME);
		bundle.setDescription(BUNDLE_DESCRIPTION);
		bundle.setGitRepoAddress(BUNDLE_GIT_REPO_ADDRESS);
		bundle.setDependencies(BUNDLE_DEPENDENCIES);
		return bundle;
	}
	
	private Category createCategory() {
		Category category = new Category();
		category.setId(CATEGORY_ID);
		category.setName(CATEGORY_NAME);
		category.setDescription(CATEGORY_DESCRIPTION);
		return category;
	}
	
	private Organisation createOrganisation() {
		Organisation organisation = new Organisation();
		organisation.setId(ORG_ID);
		organisation.setName(ORG_NAME);
		organisation.setDescription(ORG_DESCRIOPTION);
		return organisation;
	}
}
