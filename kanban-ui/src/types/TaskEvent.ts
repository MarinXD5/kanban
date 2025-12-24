import type { Task } from "./Task";

export type TaskEventType = "CREATED" | "UPDATED" | "DELETED";

export interface TaskEvent{
    type: TaskEventType;
    task: Task
}