import { useEffect, useRef, useState } from "react";
import type { TaskStatus } from "../types/Task";
import { createTask } from "../api/taskApi";
import { useParams } from "react-router-dom";

const PLACEHOLDER_BY_STATUS: Record<TaskStatus, string> = {
  TO_DO: "Što bi trebalo napraviti?",
  IN_PROGRESS: "Na čemu radimo?",
  DONE: "Što smo napravili?",
};

export default function InlineCreateTask({
  status,
  order,
  onCancel,
  onCreate,
}: {
  status: TaskStatus;
  order: number;
  onCancel: () => void;
  onCreate: () => void;
}) {
  const [title, setTitle] = useState("");
  const inputRef = useRef<HTMLInputElement>(null);

  const { projectId } = useParams<{ projectId: string }>();
  useEffect(() => {
    inputRef.current?.focus();
  }, []);

  const submit = async () => {
    if (!title.trim()) {
      onCancel();
      return;
    }

    await createTask({
      title: title.trim(),
      status,
      priority: "MEDIUM",
      order,
      projectId: Number(projectId),
    });

    onCreate();
  };

  return (
    <div className="task-card creating">
      <input
        ref={inputRef}
        className="task-input"
        placeholder={PLACEHOLDER_BY_STATUS[status]}
        value={title}
        onChange={e => setTitle(e.target.value)}
        onKeyDown={e => {
          if (e.key === "Enter") submit();
          if (e.key === "Escape") onCancel();
        }}
        onBlur={onCancel}
      />
    </div>
  );
}
