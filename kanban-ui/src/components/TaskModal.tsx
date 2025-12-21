import { useState } from "react";
import type { Task, TaskUpdatePayload } from "../types/Task";
import EditableText from "./EditableText";
import { updateTask } from "../api/taskApi";
import { TaskStatusSelect } from "./TaskStatusSelect";
import { RichTextEditor } from "./RichTextEditor";
import { TaskPrioritySelect } from "./TaskPrioritySelect";
import { TaskAssigneeSelect } from "./TaskAssigneeSelect";
import { Avatar } from "./Avatar";

export function TaskModal({
  task: initialTask,
  onClose,
  onTaskUpdated,
}: {
  task: Task;
  onClose: () => void;
  onTaskUpdated: (task: Task) => void;
}) {
  const [task, setTask] = useState<Task>(initialTask);

  async function patchTask(patch: Partial<Task>, apiPatch?: TaskUpdatePayload) {
    const updated = { ...task, ...patch };
    setTask(updated);

    await updateTask(task.id, apiPatch ?? patch);
    onTaskUpdated(updated);
  }

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal task-modal" onClick={e => e.stopPropagation()}>
        <div className="task-modal-layout">

          <div className="task-modal-left">
            <div className="task-title-block">
              <EditableText
                value={task.title}
                placeholder="Neimenovani task"
                onSave={value => patchTask({ title: value })}
              />
            </div>

            <div className="task-description-block">
              <RichTextEditor
                value={task.description}
                onSave={html => patchTask({ description: html })}
              />
            </div>
          </div>

          <div className="task-modal-right">
            <div className="task-meta-block">
              <div className="task-meta-label">Status: </div>
              <TaskStatusSelect
                value={task.status}
                onChange={status => patchTask({ status })}
              />
            </div>

            <div className="task-meta-block">
              <div className="task-meta-label">Task dodijeljen:</div>

              <div className="assignee-row">
                {task.assignee ? (
                  <Avatar
                    name={task.assignee.name}
                    color={task.assignee.avatarColor}
                  />
                ) : (
                  <div className="assignee-empty-avatar">
                    <span className="material-icons">person</span>
                  </div>
                )}

                <TaskAssigneeSelect
                  projectId={task.projectId}
                  value={task.assignee?.id}
                  onChange={user =>
                    patchTask(
                      { assignee: user ?? null },
                      { assigneeId: user?.id ?? null }
                    )
                  }
                />
              </div>
            </div>

            <div className="task-meta-block">
              <div className="task-meta-label">Kreiran: </div>
              <div className="task-meta-value">
                {new Date(task.createdAt).toLocaleString("hr-HR", {
                  hour: "2-digit",
                  minute: "2-digit",
                  day: "2-digit",
                  month: "2-digit",
                  year: "numeric",
                })}
              </div>
            </div>

            <div className="task-meta-block">
              <div className="task-meta-label">Prioritet: </div>
              <TaskPrioritySelect
                value={task.priority}
                onChange={priority => patchTask({ priority })}
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
