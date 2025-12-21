import { useEffect, useState } from "react";
import { getAllUsers, addUserToProject, removeUserFromProject } from "../api/projectApi";
import type { Project } from "../types/Project";
import type { User } from "../types/User";

export default function ManageUsersModal({
  project,
  onClose,
}: {
  project: Project;
  onClose: () => void;
}) {
  const [mode, setMode] = useState<"add" | "remove">("add");
  const [users, setUsers] = useState<User[]>([]);
  const [projectUsers, setProjectUsers] = useState<User[]>(project.users);

  useEffect(() => {
    getAllUsers().then(setUsers);
  }, []);

  const projectUserIds = projectUsers.map(u => u.id);

  const addable = users.filter(u => !projectUserIds.includes(u.id));
  const removable = users.filter(u => projectUserIds.includes(u.id));

  const addUser = async (user: User) => {
    await addUserToProject(project.id, user.id);

    setProjectUsers(prev => [...prev, user]);
  };

  const removeUser = async (user: User) => {
    await removeUserFromProject(project.id, user.id);

    setProjectUsers(prev =>
        prev.filter(u => u.id !== user.id)
    );
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
  <div className="modal manage-users-modal" onClick={e => e.stopPropagation()}>
    <h2>Upravljanje korisnicima</h2>

    <div className="modal-tabs">
      <button
        className={mode === "add" ? "active" : ""}
        onClick={() => setMode("add")}
      >
        Dodaj korisnike
      </button>
      <button
        className={mode === "remove" ? "active" : ""}
        onClick={() => setMode("remove")}
      >
        Ukloni korisnike
      </button>
    </div>

    <div className="modal-content">
      {mode === "add" && addable.map(u => (
        <div key={u.id} className="user-row">
          <span>{u.name}</span>
          <button onClick={() => addUser(u)}>Dodaj</button>
        </div>
      ))}

      {mode === "remove" && removable.map(u => (
        <div key={u.id} className="user-row">
          <span>{u.name}</span>
          <button className="danger" onClick={() => removeUser(u)}>
            Ukloni
          </button>
        </div>
      ))}
    </div>

    <div className="modal-footer">
      <button onClick={onClose}>Close</button>
    </div>
  </div>
</div>

  );
}
