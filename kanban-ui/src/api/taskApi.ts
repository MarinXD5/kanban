import { http } from "./http";
import type { Task } from "../types/Task";

export async function getTasks(
  projectId: number
): Promise<Task[]> {
  const res = await http.get("/api/tasks", {
    params: {
      projectId,
      size: 100,
    },
  });

  return res.data.content;
}

export async function createTask(data: {
  title: string;
  status: string;
  priority: string;
  order: number;
  projectId: number;
}) {
  await http.post("/api/tasks", data);
}

export async function updateTask(
  id: number,
  data: Partial<Task>
) {
  await http.patch(
    `/api/tasks/${id}`,
    data,
    {
      headers: {
        "Content-Type": "application/merge-patch+json",
      },
    }
  );
}
