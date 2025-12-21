import { useState } from "react";
import { createProject } from "../api/projectApi";
import type { Project } from "../types/Project";

export default function CreateProjectModal({
  onClose,
  onCreated,
}: {
  onClose: () => void;
  onCreated: (project: Project) => void;
}) {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async () => {
    if (!name.trim()) return;

    setLoading(true);
    try {
      const project = await createProject({
        name: name.trim(),
        description: description.trim(),
      });

      onCreated(project);
      onClose();
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <h2>Create project</h2>

        <label>Name</label>
        <input
          value={name}
          onChange={e => setName(e.target.value)}
          placeholder="Project name"
        />

        <label>Description</label>
        <textarea
          value={description}
          onChange={e => setDescription(e.target.value)}
          placeholder="Optional description"
        />

        <div style={{ display: "flex", gap: 8, marginTop: 16 }}>
          <button className="primary" onClick={submit} disabled={loading}>
            Create
          </button>
          <button onClick={onClose}>Cancel</button>
        </div>
      </div>
    </div>
  );
}
