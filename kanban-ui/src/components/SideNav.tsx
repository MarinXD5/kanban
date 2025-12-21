import { useEffect, useRef, useState, useContext } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { AuthContext } from "./AuthContext";
import { getMyProjects } from "../api/projectApi";
import type { Project } from "../types/Project";
import CreateProjectModal from "./CreateProjectModal";
import ManageUsersModal from "./ManageUsersModal";

export default function SideNav() {
  const auth = useContext(AuthContext);
  const navigate = useNavigate();
  const { projectId } = useParams();
  const sidenavRef = useRef<HTMLDivElement | null>(null);

  const [projects, setProjects] = useState<Project[]>([]);
  const [creating, setCreating] = useState(false);
  const [menuProject, setMenuProject] = useState<Project | null>(null);
  const [managingProject, setManagingProject] = useState<Project | null>(null);

  useEffect(() => {
    if (!auth?.user) return;
    getMyProjects(auth.user.id).then(setProjects);
  }, [auth?.user]);

  useEffect(() => {
  function handleClickOutside(e: MouseEvent) {
    if (
      sidenavRef.current &&
      !sidenavRef.current.contains(e.target as Node)
    ) {
      setMenuProject(null);
    }
  }

  if (menuProject) {
    document.addEventListener("mousedown", handleClickOutside);
  }

  return () => {
    document.removeEventListener("mousedown", handleClickOutside);
  };
}, [menuProject]);

  return (
  <>
    <aside className="sidenav" ref={sidenavRef}>
      <div className="sidenav-header">
        <span>Projekti</span>
        <button
          className="sidenav-add"
          onClick={() => setCreating(true)}
          title="Create project"
        >
          +
        </button>
      </div>

      <div className="sidenav-list">
        {projects.map(p => (
          <div key={p.id} className="sidenav-project-row">
            <div
              className={`sidenav-item ${
                String(p.id) === projectId ? "active" : ""
              }`}
              onClick={() => navigate(`/projects/${p.id}`)}
            >
              {p.name}
            </div>

            <div className="project-menu-wrapper">
              <button
                className="project-menu-btn"
                onClick={e => {
                  e.stopPropagation();
                  setMenuProject(prev =>
                    prev?.id === p.id ? null : p
                  );
                }}
              >
                ⋮
              </button>

              {menuProject?.id === p.id && (
                <div className="project-menu">
                  <button
                    onClick={() => {
                      setMenuProject(null);
                      setManagingProject(p);
                    }}
                  >
                    Upravljaj korisnicima
                  </button>
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    </aside>

    {creating && (
      <CreateProjectModal
        onClose={() => setCreating(false)}
        onCreated={project => {
          setProjects(prev => [...prev, project]);
          navigate(`/projects/${project.id}`)}
        }
      />
    )}

    {managingProject && (
      <ManageUsersModal
        project={managingProject}
        onClose={() => setManagingProject(null)}
      />
    )}
  </>
);
}
