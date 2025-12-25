import { useContext } from "react";
import { Routes, Route } from "react-router-dom";
import { AuthContext } from "./components/AuthContext";
import AuthPage from "./components/AuthPage";
import KanbanBoard from "./components/KanbanBoard";
import Header from "./components/Header";
import SettingsPage from "./components/SettingsPage";
import SideNav from "./components/SideNav";
import ProjectsRedirect from "./components/ProjectsRedirect";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

export default function App() {
  const auth = useContext(AuthContext);
  if (!auth) throw new Error("AuthProvider missing");

  return (
    <>
      <ToastContainer
        position="bottom-right"
        autoClose={1500}
      />

      {!auth.user ? (
        <Routes>
          <Route path="*" element={<AuthPage />} />
        </Routes>
      ) : (
        <>
          <Header />
          <div className="app-layout">
            <SideNav />
            <Routes>
              <Route path="/projects/:projectId" element={<KanbanBoard />} />
              <Route path="/settings" element={<SettingsPage />} />
              <Route path="*" element={<ProjectsRedirect />} />
            </Routes>
          </div>
        </>
      )}
    </>
  );
}
