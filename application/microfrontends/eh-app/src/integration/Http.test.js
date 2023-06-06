import axios from 'axios';
import { getData, postData, deleteData, putData } from './Http';

jest.mock('axios');

const mockUrl = 'https://example.com/api/';
const mockId = '1234';
const mockPayload = { name: 'Test Data' };

describe('HTTP functions', () => {
  afterEach(() => {
    axios.request.mockReset();
  });

  test('getData', async () => {
    axios.request.mockResolvedValue({ data: mockPayload });

    const result = await getData(mockUrl, mockId);
    expect(axios.request).toHaveBeenCalledWith({ method: 'get', url: `${mockUrl}${mockId}` });
    expect(result).toEqual({ data: mockPayload, isError: false });
  });

  test('postData', async () => {
    axios.request.mockResolvedValue({ data: mockPayload });

    const result = await postData(mockUrl, mockPayload, mockId);
    expect(axios.request).toHaveBeenCalledWith({ method: 'post', url: `${mockUrl}${mockId}`, data: mockPayload });
    expect(result).toEqual({ data: mockPayload, isError: false });
  });

  test('deleteData', async () => {
    axios.request.mockResolvedValue({ data: mockPayload });

    const result = await deleteData(mockUrl, mockId);
    expect(axios.request).toHaveBeenCalledWith({ method: 'delete', url: `${mockUrl}${mockId}` });
    expect(result).toEqual({ data: mockPayload, isError: false });
  });

  test('putData', async () => {
    axios.request.mockResolvedValue({ data: mockPayload });

    const result = await putData(mockUrl, mockPayload, mockId);
    expect(axios.request).toHaveBeenCalledWith({ method: 'put', url: `${mockUrl}${mockId}`, data: mockPayload });
    expect(result).toEqual({ data: mockPayload, isError: false });
  });

  test('error handling', async () => {
    const errorResponse = {
      toJSON: () => ({
        name: 'Error',
      }),
    };
    axios.request.mockRejectedValue(errorResponse);
  
    const result = await getData(mockUrl, mockId);
    expect(axios.request).toHaveBeenCalledWith({ method: 'get', url: `${mockUrl}${mockId}` });
    expect(result).toEqual({ data: errorResponse, isError: true });
  });  
});
