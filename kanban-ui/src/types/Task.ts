export type TaskStatus = "TO_DO" | "IN_PROGRESS" | "DONE";
export type TaskPriority = "LOW" | "MEDIUM" | "HIGH";

export interface TaskUser {
  id: number;
  name: string;
  avatarColor?: string;
}

export type TaskUpdatePayload = {
  assigneeId?: number | null;
};

export interface Task {
  id: number;
  title: string;
  projectId: number;
  description?: string;
  status: TaskStatus;
  priority: TaskPriority;
  createdAt: Date
  order: number;
  assignee?: TaskUser | null;
}
