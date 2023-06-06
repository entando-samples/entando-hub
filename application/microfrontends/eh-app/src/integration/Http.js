import axios from 'axios';
import { getDefaultOptions } from '../helpers/helpers';

const handleErrorResponse = (e) => {
  let data = e.response ? e.response.data : e;
  let isError = (data.hasOwnProperty("toJSON") && data.toJSON().name === "Error") || data.error;
  return { data, isError };
};

const request = async (method, url, payload, id) => {
  url = id ? `${url}${id}` : url;
  try {
    const response = await axios.request({
      method,
      url,
      ...(payload ? { data: payload } : {}),
      ...getDefaultOptions(),
    });
    return { data: response.data, isError: false };
  } catch (e) {
    return handleErrorResponse(e);
  }
};

export const getData = (url, id) => request('get', url, undefined, id);
export const postData = (url, payload, id) => request('post', url, payload, id);
export const deleteData = (url, id) => request('delete', url, undefined, id);
export const putData = (url, payload, id) => request('put', url, payload, id);
