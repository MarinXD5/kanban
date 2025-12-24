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
      <div className="modal modal-elevated" onClick={e => e.stopPropagation()}>
        
        <div className="modal-header">
          <h2>Kreiraj novi projekt</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        <div className="modal-body">
          <div className="form-group">
            <label>Naziv projekta</label>
            <input
              value={name}
              onChange={e => setName(e.target.value)}
              placeholder="npr. Stranica za marketing"
              autoFocus
            />
          </div>

          <div className="form-group">
            <label>Opis</label>
            <textarea
              value={description}
              onChange={e => setDescription(e.target.value)}
              placeholder="Proizvoljni opis ili cilj..."
            />
          </div>
        </div>

        <div className="modal-footer">
          <button className="btn-secondary" onClick={onClose}>
            Odustani
          </button>
          <button
            className="btn-primary"
            onClick={submit}
            disabled={loading || !name.trim()}
          >
            {loading ? "Kreiram..." : "Kreiraj projekt"}
          </button>
        </div>
      </div>
    </div>
  );
}