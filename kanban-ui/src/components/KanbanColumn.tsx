import { useState } from "react";
import { useDroppable } from "@dnd-kit/core";
import { SortableContext, verticalListSortingStrategy } from "@dnd-kit/sortable";
import type { Task, TaskStatus } from "../types/Task";
import TaskCard from "./TaskCard";
import InlineCreateTask from "./InlineCreateTask.tsx";

export default function KanbanColumn({
  status,
  tasks,
  onTaskUpdated,
  dragOverStatus,
}: {
  status: TaskStatus;
  tasks: Task[];
  onTaskUpdated: (task: Task) => void;
  dragOverStatus: TaskStatus | null;
}) {
  const { setNodeRef } = useDroppable({ id: status });
  const [creating, setCreating] = useState(false);

  return (
    <div ref={setNodeRef} className={`
    kanban-column
    ${dragOverStatus === "DONE" && status === "DONE" ? "done-glow" : ""}
  `}>
      <div className="column-header">
        <div className="column-title">
          {status.replace("_", " ")}
          <span className="column-count">
            {tasks.length}
          </span>
        </div>

        <button
          className="add-task-btn"
          onClick={() => setCreating(true)}
        >
          +
        </button>
      </div>

      <SortableContext
        items={tasks.map(t => t.id)}
        strategy={verticalListSortingStrategy}
      >
        {tasks.map(task => (
          <TaskCard
            key={task.id}
            task={task}
            onTaskUpdated={onTaskUpdated}
          />
        ))}

        {creating && (
          <InlineCreateTask
            status={status}
            order={tasks.length}
            onCancel={() => setCreating(false)}
            onCreate={() => setCreating(false)}
          />
        )}
      </SortableContext>
    </div>
  );
}
