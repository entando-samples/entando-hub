import { deleteData, getData, postData, putData } from './Http';
import i18n from '../i18n';
import {
  getAllOrganisations,
  getSingleOrganisation,
  addNewOrganisation,
  editOrganisation,
  deleteOrganisation,
  getAllBundleGroups,
  getAllBundleGroupsFilteredPaged,
  getSingleBundleGroup,
  addNewBundleGroup,
  editBundleGroup,
  deleteBundleGroup,
  addNewBundleGroupVersion,
  getAllBundleGroupVersionByBundleGroupId,
  deleteBundleGroupVersion,
  editBundleGroupVersion,
  getBundleGroupDetailsByBundleGroupVersionId,
  createAUserForAnOrganisation,
  getAllUsers,
  getAllUserForAnOrganisation,
  deleteUser,
  getAllCategories,
  addNewCategory,
  getSingleCategory,
  editCategory,
  deleteCategory,
  createPrivateCatalog,
  getPrivateCatalogs,
  getPrivateCatalog,
  generateCatalogApiKey,
  getCatalogApiKeys,
  updateCatalogApiKey,
  regenerateCatalogApiKey,
  deleteCatalogApiKey
} from './Integration';

jest.mock('./http');
jest.mock('../i18n');

const mockApiUrl = 'https://mock.api';
const mockId = 1;
const mockData = {
  name: 'Test Organisation',
};

const mockDataResponse = {
  data: mockData,
  isError: false,
};

const mockErrorResponse = {
  data: {
    message: 'Error message',
  },
  isError: true,
};

const mockTranslation = (key) => key;

describe('Integration', () => {
  beforeEach(() => {
    getData.mockClear();
    postData.mockClear();
    deleteData.mockClear();
    i18n.t.mockImplementation(mockTranslation);
  });

  describe('Organisations', () => {
    describe('getAllOrganisations', () => {
      it('should fetch all organisations and return organisationList', async () => {
        getData.mockResolvedValue(mockDataResponse);

        const result = await getAllOrganisations(mockApiUrl);

        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/organisation/`);
        expect(result).toEqual({
          organisationList: mockData,
          isError: false,
        });
      });

      it('should handle error and return errorBody', async () => {
        getData.mockResolvedValue(mockErrorResponse);

        const result = await getAllOrganisations(mockApiUrl);

        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/organisation/`);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.impossibleToLoadOrganisations');
      });
    });

    describe('getSingleOrganisation', () => {
      it('should fetch a single organisation and return organisation', async () => {
        getData.mockResolvedValue(mockDataResponse);

        const result = await getSingleOrganisation(mockApiUrl, mockId);

        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/organisation/`, mockId);
        expect(result).toEqual({
          organisation: mockData,
          isError: false,
        });
      });

      it('should handle error and return errorBody', async () => {
        getData.mockResolvedValue(mockErrorResponse);

        const result = await getSingleOrganisation(mockApiUrl, mockId);

        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/organisation/`, mockId);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.impossibleToLoadOrganisation');
      });
    });

    describe('addNewOrganisation', () => {
      it('should add a new organisation and return newOrganisation', async () => {
        postData.mockResolvedValue(mockDataResponse);

        const result = await addNewOrganisation(mockApiUrl, mockData);

        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/organisation/`, mockData);
        expect(result).toEqual({
          newOrganisation: mockData,
          isError: false,
        });
      });

      it('should handle error and return errorBody', async () => {
        postData.mockResolvedValue(mockErrorResponse);

        const result = await addNewOrganisation(mockApiUrl, mockData);

        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/organisation/`, mockData);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.impossibleToCreateOrganisation');
      });
    });

    describe('editOrganisation', () => {
      it('should edit an organisation and return editedOrganisation', async () => {
        postData.mockResolvedValue(mockDataResponse);
        const result = await editOrganisation(mockApiUrl, mockData, mockId);

        expect(postData).toHaveBeenCalledWith(
          `${mockApiUrl}/api/organisation/`,
          mockData,
          mockId
        );
        expect(result).toEqual({
          editedOrganisation: mockData,
          isError: false,
        });
      });
      
      it('should handle error and return errorBody', async () => {
        postData.mockResolvedValue(mockErrorResponse);
      
        const result = await editOrganisation(mockApiUrl, mockData, mockId);
      
        expect(postData).toHaveBeenCalledWith(
          `${mockApiUrl}/api/organisation/`,
          mockData,
          mockId
        );
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.impossibleToUpdateOrganisation');
      });
    });

    describe('deleteOrganisation', () => {
      it('should delete an organisation and return deletedOrganisation', async () => {
        deleteData.mockResolvedValue(mockDataResponse);
        const result = await deleteOrganisation(mockApiUrl, mockId);

        expect(deleteData).toHaveBeenCalledWith(`${mockApiUrl}/api/organisation/`, mockId);
        expect(result).toEqual({
          deletedOrganisation: mockData,
          isError: false,
        });
      });
      
      it('should handle error and return errorBody', async () => {
        deleteData.mockResolvedValue(mockErrorResponse);
      
        const result = await deleteOrganisation(mockApiUrl, mockId);
      
        expect(deleteData).toHaveBeenCalledWith(`${mockApiUrl}/api/organisation/`, mockId);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.impossibleToDeleteOrganisation');
      });
    });
  });

  describe('Bundle Groups', () => {
    describe('getAllBundleGroups', () => {
      it('should fetch all bundle groups and return bundleGroupList', async () => {
        getData.mockResolvedValue(mockDataResponse);

        const result = await getAllBundleGroups(mockApiUrl, mockId);

        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroups/?organisationId=${mockId}`);
        expect(result).toEqual({
          bundleGroupList: mockData,
          isError: false,
        });
      });

      it('should handle error and return errorBody', async () => {
        getData.mockResolvedValue(mockErrorResponse);

        const result = await getAllBundleGroups(mockApiUrl, mockId);

        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroups/?organisationId=${mockId}`);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.impossibleToLoadBundleGroups');
      });
    });

    describe('getAllBundleGroupsFilteredPaged', () => {
      it('should fetch filtered and paged bundle groups and return bundleGroupList', async () => {
        getData.mockResolvedValue(mockDataResponse);
        const filterOptions = {
          page: 1,
          pageSize: 10,
          organisationId: 1,
          categoryIds: [1, 2],
          statuses: ['active', 'inactive'],
          searchText: 'test',
        };
    
        const result = await getAllBundleGroupsFilteredPaged(mockApiUrl, filterOptions);
    
        const expectedUrl = `${mockApiUrl}/api/bundlegroupversions/filtered?page=1&pageSize=10&categoryIds=1&categoryIds=2&statuses=active&statuses=inactive&organisationId=1&searchText=test`;
        expect(getData).toHaveBeenCalledWith(expectedUrl);
        expect(result).toEqual({
          bundleGroupList: mockData,
          isError: false,
        });
      });
    
      it('should handle error and return errorBody', async () => {
        getData.mockResolvedValue(mockErrorResponse);
        const filterOptions = {
          page: 1,
          pageSize: 10,
          organisationId: 1,
          categoryIds: [1, 2],
          statuses: ['active', 'inactive'],
          searchText: 'test',
        };
    
        const result = await getAllBundleGroupsFilteredPaged(mockApiUrl, filterOptions);
    
        const expectedUrl = `${mockApiUrl}/api/bundlegroupversions/filtered?page=1&pageSize=10&categoryIds=1&categoryIds=2&statuses=active&statuses=inactive&organisationId=1&searchText=test`;
        expect(getData).toHaveBeenCalledWith(expectedUrl);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
      });
    });

    describe('getSingleBundleGroup', () => {
      it('should fetch a single bundle group and return bundleGroup', async () => {
        getData.mockResolvedValue(mockDataResponse);

        const result = await getSingleBundleGroup(mockApiUrl, mockId);

        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroups/`, mockId);
        expect(result).toEqual({
          bundleGroup: mockData,
          isError: false,
        });
      });

      it('should handle error and return errorBody', async () => {
        getData.mockResolvedValue(mockErrorResponse);

        const result = await getSingleBundleGroup(mockApiUrl, mockId);

        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroups/`, mockId);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
      });
    });

    describe('addNewBundleGroup', () => {
      it('should add a new bundle group and return newBundleGroup', async () => {
        postData.mockResolvedValue(mockDataResponse);
    
        const result = await addNewBundleGroup(mockApiUrl, mockData);
    
        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroups/`, mockData);
        expect(result).toEqual({
          newBundleGroup: mockData,
          isError: false,
        });
      });
    
      it('should handle error and return errorBody', async () => {
        postData.mockResolvedValue(mockErrorResponse);
    
        const result = await addNewBundleGroup(mockApiUrl, mockData);
    
        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroups/`, mockData);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.impossibleToCreateBundleGroup');
      });
    });

    describe('editBundleGroup', () => {
      it('should edit a bundle group and return editedBundleGroup', async () => {
        postData.mockResolvedValue(mockDataResponse);
    
        const result = await editBundleGroup(mockApiUrl, mockData, mockId);
    
        expect(postData).toHaveBeenCalledWith(
          `${mockApiUrl}/api/bundlegroups/`,
          mockData,
          mockId
        );
        expect(result).toEqual({
          editedBundleGroup: mockData,
          isError: false,
        });
      });
    
      it('should handle error and return errorBody', async () => {
        postData.mockResolvedValue(mockErrorResponse);
    
        const result = await editBundleGroup(mockApiUrl, mockData, mockId);
    
        expect(postData).toHaveBeenCalledWith(
          `${mockApiUrl}/api/bundlegroups/`,
          mockData,
          mockId
        );
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.impossibleToUpdateBundleGroup');
      });
    });
    
    describe('deleteBundleGroup', () => {
      it('should delete a bundle and return deletedBundle', async () => {
        deleteData.mockResolvedValue(mockDataResponse);
    
        const result = await deleteBundleGroup(mockApiUrl, mockId, 'Test Bundle');
    
        expect(deleteData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroups/`, mockId);
        expect(result).toEqual({
          deletedBundle: mockData,
          isError: false,
        });
      });
    
      it('should handle error and return errorBody', async () => {
        deleteData.mockResolvedValue(mockErrorResponse);
    
        const result = await deleteBundleGroup(mockApiUrl, mockId, 'Test Bundle');
    
        expect(deleteData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroups/`, mockId);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.impossibleToDeleteBundle');
      });
    });

    describe("addNewBundleGroupVersion", () => {
      it("should add a new bundle group version and return editedBundleGroup", async () => {
        postData.mockResolvedValue(mockDataResponse);
        const bundleGroupVersionData = { name: "TestBundleGroupVersion" };

        const result = await addNewBundleGroupVersion(mockApiUrl, bundleGroupVersionData);

        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroupversions/`, bundleGroupVersionData);
        expect(result).toEqual({
          editedBundleGroup: mockData,
          isError: false,
        });
      });

      it("should handle error and return errorBody", async () => {
        postData.mockResolvedValue(mockErrorResponse);
        const bundleGroupVersionData = { name: "TestBundleGroupVersion" };

        const result = await addNewBundleGroupVersion(mockApiUrl, bundleGroupVersionData);

        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroupversions/`, bundleGroupVersionData);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.unableToAddBundleGroupVersion');
      });
    });

    describe("getAllBundleGroupVersionByBundleGroupId", () => {
      it("should fetch all bundle group versions by bundle group id and return versions", async () => {
        getData.mockResolvedValue(mockDataResponse);
        const bundleGroupId = 1;
        const page = 1;
        const pageSize = 10;
        const bundleStatuses = [1, 2];

        const result = await getAllBundleGroupVersionByBundleGroupId(mockApiUrl, bundleGroupId, page, pageSize, bundleStatuses);

        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroupversions/versions/1?page=1&pageSize=10&statuses=1,2`);
        expect(result).toEqual({
          versions: mockData,
          isError: false,
        });
      });

      it("should handle error and return errorBody", async () => {
        getData.mockResolvedValue(mockErrorResponse);
        const bundleGroupId = 1;
        const page = 1;
        const pageSize = 10;
        const bundleStatuses = [1, 2];

        const result = await getAllBundleGroupVersionByBundleGroupId(mockApiUrl, bundleGroupId, page, pageSize, bundleStatuses);

        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroupversions/versions/1?page=1&pageSize=10&statuses=1,2`);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.impossibleToLoadBundleGroupVersions');
      });
    });

    describe("deleteBundleGroupVersion", () => {
      it("should delete a bundle group version and return deletedBundle", async () => {
        deleteData.mockResolvedValue(mockDataResponse);
        const bundleGroupVersionId = 1;

        const result = await deleteBundleGroupVersion(mockApiUrl, bundleGroupVersionId);

        expect(deleteData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroupversions/`, bundleGroupVersionId);
        expect(result).toEqual({
          deletedBundle: mockData,
          isError: false,
        });
      });

      it("should handle error and return errorBody", async () => {
        deleteData.mockResolvedValue(mockErrorResponse);
        const bundleGroupVersionId = 1;

        const result = await deleteBundleGroupVersion(mockApiUrl, bundleGroupVersionId);

        expect(deleteData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroupversions/`, bundleGroupVersionId);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.impossibleToDeleteBundle');
      });
    });

    describe("editBundleGroupVersion", () => {
      it("should update a bundle group version and return editedBundleGroup", async () => {
        postData.mockResolvedValue(mockDataResponse);
        const bundleGroupVersionData = { name: "UpdatedBundleGroupVersion" };
        const bundleGroupVersionId = 1;

        const result = await editBundleGroupVersion(mockApiUrl, bundleGroupVersionData, bundleGroupVersionId);

        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroupversions/`, bundleGroupVersionData, bundleGroupVersionId);
        expect(result).toEqual({
          editedBundleGroup: mockData,
          isError: false,
        });
      });

      it("should handle error and return errorBody", async () => {
        postData.mockResolvedValue(mockErrorResponse);
        const bundleGroupVersionData = { name: "UpdatedBundleGroupVersion" };
        const bundleGroupVersionId = 1;

        const result = await editBundleGroupVersion(mockApiUrl, bundleGroupVersionData, bundleGroupVersionId);

        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroupversions/`, bundleGroupVersionData, bundleGroupVersionId);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.impossibleToUpdateBundleGroup');
      });
    });

    describe("getBundleGroupDetailsByBundleGroupVersionId", () => {
      it("should fetch bundle group details by bundle group version id and return bgVersionDetails", async () => {
        getData.mockResolvedValue(mockDataResponse);
        const bundleGroupVersionId = 1;
        const params = { catalogId: 2 };

        const result = await getBundleGroupDetailsByBundleGroupVersionId(mockApiUrl, bundleGroupVersionId, params);

        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroupversions/1?catalogId=2`);
        expect(result).toEqual({
          bgVersionDetails: mockData,
          isError: false,
        });
      });

      it("should handle error and return errorBody", async () => {
        getData.mockResolvedValue(mockErrorResponse);
        const bundleGroupVersionId = 1;
        const params = { catalogId: 2 };

        const result = await getBundleGroupDetailsByBundleGroupVersionId(mockApiUrl, bundleGroupVersionId, params);

        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/bundlegroupversions/1?catalogId=2`);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.impossibleToLoadBundleGroup');
      });
    });
  });

  describe('Users', () => {
    describe("createAUserForAnOrganisation", () => {
      it("should create a user for an organisation and return newUserForOrganization", async () => {
        postData.mockResolvedValue(mockDataResponse);
        const organisationId = 1;
        const userData = { username: "testuser" };
        const type = "create";

        const result = await createAUserForAnOrganisation(mockApiUrl, organisationId, userData, type);

        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/users/1`, { username: userData });
        expect(result).toEqual({
          newUserForOrganization: mockData,
          isError: false,
        });
      });

      it("should handle error and return errorBody", async () => {
        postData.mockResolvedValue(mockErrorResponse);
        const organisationId = 1;
        const userData = { username: "testuser" };
        const type = "create";

        const result = await createAUserForAnOrganisation(mockApiUrl, organisationId, userData, type);

        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/users/1`, { username: userData });
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.impossibleToCreateUser');
      });
    });

    describe("getAllUsers", () => {
      it("should fetch all users and return userList", async () => {
        getData.mockResolvedValue(mockDataResponse);

        const result = await getAllUsers(mockApiUrl);

        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/users/`);
        expect(result).toEqual({
          userList: mockData,
          isError: false,
        });
      });

      it("should handle error and return errorBody", async () => {
        getData.mockResolvedValue(mockErrorResponse);

        const result = await getAllUsers(mockApiUrl);

        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/users/`);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.impossibleToLoadUsers');
      });
    });

    describe("getAllUserForAnOrganisation", () => {
      it("should fetch all users for an organisation and return userList", async () => {
        getData.mockResolvedValue(mockDataResponse);
        const organisationId = 1;

        const result = await getAllUserForAnOrganisation(mockApiUrl, organisationId);

        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/users/?organisationId=1`);
        expect(result).toEqual({
          userList: mockData,
          isError: false,
        });
      });

      it("should handle error and return errorBody", async () => {
        getData.mockResolvedValue(mockErrorResponse);
        const organisationId = 1;

        const result = await getAllUserForAnOrganisation(mockApiUrl, organisationId);

        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/users/?organisationId=1`);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.impossibleToLoadUsers');
      });
    });

    describe("deleteUser", () => {
      it("should delete a user and return data", async () => {
        deleteData.mockResolvedValue(mockDataResponse);
        const username = "testuser";

        const result = await deleteUser(mockApiUrl, username);

        expect(deleteData).toHaveBeenCalledWith(`${mockApiUrl}/api/users/testuser`);
        expect(result).toEqual(mockData);
      });

      it("should handle error and return errorBody", async () => {
        deleteData.mockResolvedValue(mockErrorResponse);
        const username = "testuser";

        const result = await deleteUser(mockApiUrl, username);

        expect(deleteData).toHaveBeenCalledWith(`${mockApiUrl}/api/users/testuser`);
        expect(result).toEqual(mockErrorResponse.data);
        expect(i18n.t).toHaveBeenCalledWith('toasterMessage.impossibleToDeleteUser');
      });
    });
  })
  
  describe('Categories', () => {
    describe("getAllCategories", () => {
      it("should fetch all categories and return categoryList", async () => {
        getData.mockResolvedValue(mockDataResponse);
  
        const result = await getAllCategories(mockApiUrl);
  
        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/category/`);
        expect(result).toEqual({
          categoryList: mockData,
          isError: false,
        });
      });
  
      it("should handle error and return errorBody", async () => {
        getData.mockResolvedValue(mockErrorResponse);
  
        const result = await getAllCategories(mockApiUrl);
  
        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/category/`);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
      });
    });

    describe("getSingleCategory", () => {
      it("should fetch a single category and return category", async () => {
        getData.mockResolvedValue(mockDataResponse);
  
        const result = await getSingleCategory(mockApiUrl, mockId);
  
        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/category/`, mockId);
        expect(result).toEqual({
          category: mockData,
          isError: false,
        });
      });
  
      it("should handle error and return errorBody", async () => {
        getData.mockResolvedValue(mockErrorResponse);
  
        const result = await getSingleCategory(mockApiUrl, mockId);
  
        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/category/`, mockId);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
      });
    });
  
    describe("addNewCategory", () => {
      it("should add a new category and return newCategory", async () => {
        postData.mockResolvedValue(mockDataResponse);
  
        const result = await addNewCategory(mockApiUrl, mockData);
  
        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/category/`, mockData);
        expect(result).toEqual({
          newCategory: mockData,
          isError: false,
        });
      });
  
      it("should handle error and return errorBody", async () => {
        postData.mockResolvedValue(mockErrorResponse);
  
        const result = await addNewCategory(mockApiUrl, mockData);
  
        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/category/`, mockData);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
      });
    });

    describe("editCategory", () => {
      it("should edit a category and return editedCategory", async () => {
        postData.mockResolvedValue(mockDataResponse);

        const result = await editCategory(mockApiUrl, mockData, mockId);

        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/category/`, mockData, mockId);
        expect(result).toEqual({
          editedCategory: mockData,
          isError: false,
        });
      });

      it("should handle error and return errorBody", async () => {
        postData.mockResolvedValue(mockErrorResponse);
        const result = await editCategory(mockApiUrl, mockData, mockId);

        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/category/`, mockData, mockId);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
      });
    });

    describe("deleteCategory", () => {
      it("should delete a category and return deletedCategory", async () => {
        deleteData.mockResolvedValue(mockDataResponse);
  
        const result = await deleteCategory(mockApiUrl, mockId, mockData.name);
  
        expect(deleteData).toHaveBeenCalledWith(`${mockApiUrl}/api/category/`, mockId);
        expect(result).toEqual({
          deletedCategory: mockData,
          isError: false,
        });
      });
  
      it("should handle error and return errorBody", async () => {
        deleteData.mockResolvedValue(mockErrorResponse);
  
        const result = await deleteCategory(mockApiUrl, mockId, mockData.name);
  
        expect(deleteData).toHaveBeenCalledWith(`${mockApiUrl}/api/category/`, mockId);
        expect(result).toEqual({
          errorBody: mockErrorResponse.data,
          isError: true,
        });
      });
  
      it("should handle error with 417 status and return custom error message", async () => {
        deleteData.mockResolvedValue({
          data: { message: "Error 417" },
          isError: true
        });
  
        const result = await deleteCategory(mockApiUrl, mockId, mockData.name);
  
        expect(deleteData).toHaveBeenCalledWith(`${mockApiUrl}/api/category/`, mockId);
        expect(result).toEqual({
          errorBody: { message: "This category is already in use." },
          isError: true,
        });
      });
    });
  });

  describe("Catalogs", () => {
    describe("createPrivateCatalog", () => {
      it("should create private catalog and return data", async () => {
        postData.mockResolvedValue(mockDataResponse);
  
        const result = await createPrivateCatalog(mockApiUrl, mockId);
  
        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/catalog/`, null, mockId);
        expect(result).toEqual({
          data: mockData,
          isError: false,
        });
      });
  
      it("should handle error and return errorBody", async () => {
        postData.mockResolvedValue(mockErrorResponse);
  
        const result = await createPrivateCatalog(mockApiUrl, mockId);
  
        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/catalog/`, null, mockId);
        expect(result).toEqual({
          data: mockErrorResponse.data,
          isError: true,
        });
      });
    });
  
    describe("getPrivateCatalogs", () => {
      it("should fetch private catalogs and return data", async () => {
        getData.mockResolvedValue(mockDataResponse);
  
        const result = await getPrivateCatalogs(mockApiUrl);
  
        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/catalog/`);
        expect(result).toEqual({
          data: mockData,
          isError: false,
        });
      });
  
      it("should handle error and return errorBody", async () => {
        getData.mockResolvedValue(mockErrorResponse);
  
        const result = await getPrivateCatalogs(mockApiUrl);
  
        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/catalog/`);
        expect(result).toEqual({
          data: mockErrorResponse.data,
          isError: true,
        });
      });
    });
  
    describe("getPrivateCatalog", () => {
      it("should fetch a private catalog and return data", async () => {
        getData.mockResolvedValue(mockDataResponse);
  
        const result = await getPrivateCatalog(mockApiUrl, mockId);
  
        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/catalog/`, mockId);
        expect(result).toEqual({
          data: mockData,
          isError: false,
        });
      });
  
      it("should handle error and return errorBody", async () => {
        getData.mockResolvedValue(mockErrorResponse);
  
        const result = await getPrivateCatalog(mockApiUrl, mockId);
  
        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/catalog/`, mockId);
        expect(result).toEqual({
          data: mockErrorResponse.data,
          isError: true,
        });
      });
    });
  });

  describe("Catalog API Keys", () => {
    const mockApiKeyDataResponse = {
      data: { payload: mockData },
      isError: false,
    };

    const mockApiKeyErrorResponse = {
      data: { payload: mockErrorResponse.data },
      isError: true,
    }

    describe("getCatalogApiKeys", () => {
      it("should fetch catalog api keys and return data payload", async () => {
        getData.mockResolvedValue(mockApiKeyDataResponse);
  
        const result = await getCatalogApiKeys(mockApiUrl, { page: 0, pageSize: 0 });
  
        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/private-catalog-api-key/?page=0&pageSize=0`);
        expect(result).toEqual({
          data: mockData,
          isError: false,
        });
      });
  
      it("should handle error and return errorBody", async () => {
        getData.mockResolvedValue(mockApiKeyErrorResponse);
  
        const result = await getCatalogApiKeys(mockApiUrl, { page: 0, pageSize: 0 });
  
        expect(getData).toHaveBeenCalledWith(`${mockApiUrl}/api/private-catalog-api-key/?page=0&pageSize=0`);
        expect(result).toEqual({
          data: mockErrorResponse.data,
          isError: true,
        });
      });
    });
  
    describe("generateCatalogApiKey", () => {
      it("should generate catalog api key and return data", async () => {
        postData.mockResolvedValue(mockDataResponse);
  
        const result = await generateCatalogApiKey(mockApiUrl, mockData);
  
        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/private-catalog-api-key/`, mockData);
        expect(result).toEqual({
          data: mockData,
          isError: false,
        });
      });
  
      it("should handle error and return errorBody", async () => {
        postData.mockResolvedValue(mockErrorResponse);
  
        const result = await generateCatalogApiKey(mockApiUrl, mockData);
  
        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/private-catalog-api-key/`, mockData);
        expect(result).toEqual({
          data: mockErrorResponse.data,
          isError: true,
        });
      });
    });

    describe("updateCatalogApiKey", () => {
      it("should update catalog api key and return data", async () => {
        putData.mockResolvedValue(mockDataResponse);
  
        const result = await updateCatalogApiKey(mockApiUrl, mockData, mockId);
  
        expect(putData).toHaveBeenCalledWith(`${mockApiUrl}/api/private-catalog-api-key/`, mockData, mockId);
        expect(result).toEqual({
          data: mockData,
          isError: false,
        });
      });
  
      it("should handle error and return errorBody", async () => {
        putData.mockResolvedValue(mockErrorResponse);
  
        const result = await updateCatalogApiKey(mockApiUrl, mockData, mockId);
  
        expect(putData).toHaveBeenCalledWith(`${mockApiUrl}/api/private-catalog-api-key/`, mockData, mockId);
        expect(result).toEqual({
          data: mockErrorResponse.data,
          isError: true,
        });
      });
    });
  
    describe("regenerateCatalogApiKey", () => {
      it("should regenerate catalog api key and return data", async () => {
        postData.mockResolvedValue(mockDataResponse);
  
        const result = await regenerateCatalogApiKey(mockApiUrl, mockId);
  
        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/private-catalog-api-key/regenerate/`, null, mockId);
        expect(result).toEqual({
          data: mockData,
          isError: false,
        });
      });
  
      it("should handle error and return errorBody", async () => {
        postData.mockResolvedValue(mockErrorResponse);
  
        const result = await regenerateCatalogApiKey(mockApiUrl, mockId);
  
        expect(postData).toHaveBeenCalledWith(`${mockApiUrl}/api/private-catalog-api-key/regenerate/`, null, mockId);
        expect(result).toEqual({
          data: mockErrorResponse.data,
          isError: true,
        });
      });
    });
  
    describe("deleteCatalogApiKey", () => {
      it("should delete catalog api key and return data", async () => {
        deleteData.mockResolvedValue(mockDataResponse);
  
        const result = await deleteCatalogApiKey(mockApiUrl, mockId);
  
        expect(deleteData).toHaveBeenCalledWith(`${mockApiUrl}/api/private-catalog-api-key/`, mockId);
        expect(result).toEqual({
          data: mockData,
          isError: false,
        });
      });
  
      it("should handle error and return errorBody", async () => {
        deleteData.mockResolvedValue(mockErrorResponse);
  
        const result = await deleteCatalogApiKey(mockApiUrl, mockId);
  
        expect(deleteData).toHaveBeenCalledWith(`${mockApiUrl}/api/private-catalog-api-key/`, mockId);
        expect(result).toEqual({
          data: mockErrorResponse.data,
          isError: true,
        });
      });
    });
  });
});
