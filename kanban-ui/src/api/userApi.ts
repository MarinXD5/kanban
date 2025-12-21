import { http } from "./http";
import type { User } from "../types/User";

export type UpdateUserPayload = {
  email: string;
  currentPassword: string;
  password?: string | null;
  avatarColor?: string;
};

export async function updateUser(
  userId: number,
  data: UpdateUserPayload
): Promise<User> {
  const res = await http.put(`/api/users/change/${userId}`, data);
  return res.data;
}

export async function getProjectUsers(projectId: number): Promise<User[]> {
  const res = await http.get(`/api/projects/${projectId}/users`);
  return res.data;
}