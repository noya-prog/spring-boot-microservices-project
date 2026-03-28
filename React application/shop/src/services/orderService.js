import axios from "axios";

const API_URL = "http://localhost:3000/api/order";

// order should be an object containing at least { sku, quantity, price }
// the backend uses the SKU code along with quantity and price to create the order
export const placeOrder = async (order) => {
  return axios.post(API_URL, order);
};