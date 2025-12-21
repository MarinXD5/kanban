import { http } from "./http";
import type { Project } from "../types/Project";
import type { User } from "../types/User";

export async function getMyProjects(userId: number): Promise<Project[]> {
  const res = await http.get(`/api/projects/user/${userId}`);
  return res.data;
}

export async function getAllUsers(): Promise<User[]> {
  const res = await http.get("/api/users");
  return res.data;
}

export async function createProject(data: {
  name: string;
  description?: string;
}): Promise<Project> {
  const res = await http.post("/api/projects", data);
  return res.data;
}

export async function addUserToProject(
  projectId: number,
  userId: number
) {
  await http.post(`/api/projects/${projectId}/users/${userId}`);
}

export async function removeUserFromProject(
  projectId: number,
  userId: number
) {
  await http.delete(`/api/projects/${projectId}/users/${userId}`);
}