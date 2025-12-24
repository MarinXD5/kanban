import { useEffect, useState } from "react";
import type { Task, TaskStatus } from "../types/Task";
import { getTasks, updateTask} from "../api/taskApi";
import { connectSocket } from "../api/socket";
import KanbanColumn from "./KanbanColumn";
import type { DragStartEvent } from "@dnd-kit/core";
import { DragOverlay } from "@dnd-kit/core";
import { pointerWithin } from "@dnd-kit/core";
import { arrayMove } from "@dnd-kit/sortable";
import { useParams } from "react-router-dom";
import { toast } from "react-toastify";

import {
  DndContext,
  type DragEndEvent,
} from "@dnd-kit/core";

const statuses: TaskStatus[] = ["TO_DO", "IN_PROGRESS", "DONE"];

export default function KanbanBoard() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [activeTask, setActiveTask] = useState<Task | null>(null);
  const { projectId } = useParams<{ projectId: string }>();
  const [dragOverStatus, setDragOverStatus] = useState<TaskStatus | null>(null);

  const tasksByStatus = (status: TaskStatus) =>
    tasks
      .filter(t => t.status === status)
      .sort((a, b) => a.order - b.order);

  useEffect(() => {
    if (!projectId) return;

    getTasks(Number(projectId)).then(setTasks);

    const client = connectSocket(event => {
      // OPTIONAL: ignore events for other projects
      //if (event.task.projectId !== Number(projectId)) return;

      switch (event.type) {
        case "CREATED":
          setTasks(prev => [...prev, event.task]);
          toast.info("Task uspješno kreiran!");
          break;

        case "UPDATED":
          setTasks(prev =>
            prev.map(t =>
              t.id === event.task.id ? event.task : t
            )
          );
          toast.info("Task uspješno uređen!");
          break;

        case "DELETED":
          setTasks(prev =>
            prev.filter(t => t.id !== event.task.id)
          );
          toast.info("Task uspješno uklonjen!");
          break;
      }
    });

    return () => {
      client.deactivate();
    };
  }, [projectId]);

  const handleDragStart = (event: DragStartEvent) => {
    const taskId = Number(event.active.id);
    const task = tasks.find(t => t.id === taskId);
    if (task) setActiveTask(task);
  };

  const handleDragEnd = async (event: DragEndEvent) => {
    const { active, over } = event;
    setActiveTask(null);
    setDragOverStatus(null);

    if (!over) return;

    const activeId = Number(active.id);
    const activeTask = tasks.find(t => t.id === activeId);
    if (!activeTask) return;

    let newStatus: TaskStatus;
    let targetIndex: number;

    if (statuses.includes(over.id as TaskStatus)) {
      newStatus = over.id as TaskStatus;
      targetIndex = tasks.filter(t => t.status === newStatus).length;
    }
    else {
      const overTaskId = Number(over.id);
      const overTask = tasks.find(t => t.id === overTaskId);
      if (!overTask) return;

      newStatus = overTask.status;

      targetIndex = tasks
        .filter(t => t.status === newStatus)
        .sort((a, b) => a.order - b.order)
        .findIndex(t => t.id === overTaskId);
    }

    setTasks(prev => {
      const updated = [...prev];

      const sourceTasks = updated
        .filter(t => t.status === activeTask.status)
        .sort((a, b) => a.order - b.order);

      const targetTasks =
        activeTask.status === newStatus
          ? sourceTasks
          : updated
              .filter(t => t.status === newStatus)
              .sort((a, b) => a.order - b.order);

      const sourceIndex = sourceTasks.findIndex(t => t.id === activeId);

      let newSourceTasks = [...sourceTasks];
      let newTargetTasks = [...targetTasks];

      if (activeTask.status === newStatus) {
        newSourceTasks = arrayMove(
          newSourceTasks,
          sourceIndex,
          targetIndex
        );
        newTargetTasks = newSourceTasks;
      }
      else {
        const [moved] = newSourceTasks.splice(sourceIndex, 1);

        newTargetTasks.splice(targetIndex, 0, {
          ...moved,
          status: newStatus,
        });
      }

      return updated.map(t => {
        if (t.status === activeTask.status) {
          const idx = newSourceTasks.findIndex(x => x.id === t.id);
          return idx !== -1 ? { ...t, order: idx } : t;
        }

        if (t.status === newStatus) {
          const idx = newTargetTasks.findIndex(x => x.id === t.id);
          return idx !== -1 ? { ...t, order: idx } : t;
        }

        return t;
      });
    });

    await updateTask(activeId, { status: newStatus });
  };

  const handleTaskUpdated = (updatedTask: Task) => {
    setTasks(prev =>
      prev.map(t =>
        t.id === updatedTask.id ? updatedTask : t
      )
    );
  };

  return (
    <DndContext
  collisionDetection={pointerWithin}
  onDragStart={handleDragStart}
  onDragEnd={handleDragEnd}
>
  <div className="kanban-board">
    {statuses.map(status => (
      <KanbanColumn
        key={status}
        status={status}
        tasks={tasksByStatus(status)}
        onTaskUpdated={handleTaskUpdated}
        dragOverStatus={dragOverStatus}
      />
    ))}
  </div>

  <DragOverlay>
    {activeTask ? (
      <div className="task-card dragging">
        <div className="task-title">{activeTask.title}</div>
        <div className="task-priority">{activeTask.priority}</div>
      </div>
    ) : null}
  </DragOverlay>
</DndContext>

  );
}
