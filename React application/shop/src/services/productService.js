import axios from "axios";

const API_URL = "http://localhost:3000/api/product";

export const getProducts = async () => {
  const response = await axios.get(API_URL);
  return response.data;
};

export const addProduct = async (product) => {
  return axios.post(API_URL, product);
};