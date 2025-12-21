import type { User } from "./User";

export interface Project {
  id: number;
  name: string;
  description: string;
  users: User[];
}
