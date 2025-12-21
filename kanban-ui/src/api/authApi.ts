import axios from "axios";

const API = "http://localhost:8080/api/auth";

export async function login(email: string, password: string) {
  const res = await axios.post(`${API}/login`, { email, password });
  return res.data.token;
}

export async function register(
  email: string,
  password: string,
  name: string
) {
  const res = await axios.post(`${API}/register`, {
    email,
    password,
    name,
  });
  return res.data.token;
}
