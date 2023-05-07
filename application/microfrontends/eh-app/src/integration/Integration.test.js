import { deleteData, getData, postData } from './Http';
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
  });
});
