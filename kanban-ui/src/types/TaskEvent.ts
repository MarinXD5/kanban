import type { Task } from "./Task";

export interface TaskEvent {
  type: "CREATED" | "UPDATED" | "DELETED";
  task: Task;
  projectId: number;
  projectName: string;
}