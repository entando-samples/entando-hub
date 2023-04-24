import axios from 'axios';
import { getData, postData, deleteData, putData } from './Http';

jest.mock('axios');

const mockUrl = 'https://example.com/api/';
const mockId = '1234';
const mockPayload = { name: 'Test Data' };

describe('HTTP functions', () => {
  afterEach(() => {
    axios.get.mockReset();
    axios.post.mockReset();
    axios.delete.mockReset();
    axios.put.mockReset();
  });

  test('getData', async () => {
    axios.get.mockResolvedValue({ data: mockPayload });

    const result = await getData(mockUrl, mockId);
    expect(axios.get).toHaveBeenCalledWith(`${mockUrl}${mockId}`, expect.any(Object));
    expect(result).toEqual({ data: mockPayload, isError: false });
  });

  test('postData', async () => {
    axios.post.mockResolvedValue({ data: mockPayload });

    const result = await postData(mockUrl, mockPayload, mockId);
    expect(axios.post).toHaveBeenCalledWith(`${mockUrl}${mockId}`, mockPayload, expect.any(Object));
    expect(result).toEqual({ data: mockPayload, isError: false });
  });

  test('deleteData', async () => {
    axios.delete.mockResolvedValue({ data: mockPayload });

    const result = await deleteData(mockUrl, mockId);
    expect(axios.delete).toHaveBeenCalledWith(`${mockUrl}${mockId}`, expect.any(Object));
    expect(result).toEqual({ data: mockPayload, isError: false });
  });

  test('putData', async () => {
    axios.put.mockResolvedValue({ data: mockPayload });

    const result = await putData(mockUrl, mockPayload, mockId);
    expect(axios.put).toHaveBeenCalledWith(`${mockUrl}${mockId}`, mockPayload, expect.any(Object));
    expect(result).toEqual({ data: mockPayload, isError: false });
  });

  test('error handling', async () => {
    const errorResponse = {
      toJSON: () => ({
        name: 'Error',
      }),
    };
    axios.get.mockRejectedValue(errorResponse);
  
    const result = await getData(mockUrl, mockId);
    expect(axios.get).toHaveBeenCalledWith(`${mockUrl}${mockId}`, expect.any(Object));
    expect(result).toEqual({ data: errorResponse, isError: true });
  });  
});
