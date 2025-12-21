import { useEffect, useState } from "react";
import { getProjectUsers } from "../api/userApi";
import type { User } from "../types/User";
import type { TaskUser } from "../types/Task";

export function TaskAssigneeSelect({
  projectId,
  value,
  onChange,
}: {
  projectId: number;
  value?: number;
  onChange: (user?: TaskUser) => void;
}) {
  const [users, setUsers] = useState<User[]>([]);

  useEffect(() => {
  getProjectUsers(projectId).then(users => {
    setUsers(users);
  });
}, [projectId]);

  return (
    <select
      className="task-assignee-select"
      value={value != null ? String(value) : ""}
      onChange={e => {
        const id = e.target.value ? Number(e.target.value) : undefined;
        const selectedUser = users.find(u => u.id === id);
        onChange(selectedUser);
      }}
    >
      <option value="">Unassigned</option>
        {users.map(u => (
          <option key={u.id} value={String(u.id)}>
            {u.name}
          </option>
        ))}
    </select>
  );
}
