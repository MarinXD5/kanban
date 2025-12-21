import { useEffect, useState, useContext } from "react";
import { Navigate } from "react-router-dom";
import { AuthContext } from "./AuthContext";
import { getMyProjects } from "../api/projectApi";
import type { Project } from "../types/Project";

export default function ProjectsRedirect() {
  const auth = useContext(AuthContext);
  const [projects, setProjects] = useState<Project[] | null>(null);

  useEffect(() => {
    if (!auth?.user) return;
    getMyProjects(auth.user.id).then(setProjects);
  }, [auth?.user]);

  if (!projects) return null;

  if (projects.length === 0) {
    return <div>No projects yet</div>;
  }

  return <Navigate to={`/projects/${projects[0].id}`} replace />;
}
