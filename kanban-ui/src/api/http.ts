import axios from "axios";
import { authStorage } from "./authStorage";

export const http = axios.create({
  baseURL: "http://localhost:8080",
});

http.interceptors.request.use(config => {
  const auth = authStorage.get();

  if (auth?.token) {
    config.headers.Authorization = `Bearer ${auth.token}`;
  }

  return config;
});
