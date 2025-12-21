import { useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import type { Task } from "../types/Task";
import { useState } from "react";
import {TaskModal} from "./TaskModal";
import { Avatar } from "./Avatar";

export default function TaskCard({ task, onTaskUpdated, }: { task: Task; onTaskUpdated: (task: Task) => void; }) {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
  } = useSortable({ id: task.id });

  const [open, setOpen] = useState(false);

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  return (
    <>
      <div
        ref={setNodeRef}
        style={style}
        className="task-card"
      >
        <div
          className="drag-handle"
          {...attributes}
          {...listeners}
        >
          ⠿
        </div>

        <div
          className="task-content"
          onClick={() => setOpen(true)}
        >
          <div className="task-title">{task.title}</div>
          <div className="task-priority">{task.priority}</div>
        </div>

        {task.assignee && (
          <div className="task-card-avatar">
            <Avatar
              name={task.assignee.name}
              color={task.assignee.avatarColor}
            />
          </div>
        )}
      </div>
      {open && (
        <TaskModal
          task={task}
          onClose={() => setOpen(false)}
          onTaskUpdated={onTaskUpdated}
        />
      )}
    </>
  );
}
