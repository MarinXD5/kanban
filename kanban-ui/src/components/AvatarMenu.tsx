import { useContext } from "react";
import { AuthContext } from "./AuthContext";
import { useNavigate } from "react-router-dom";

export default function AvatarMenu({ close }: { close: () => void }) {
  const auth = useContext(AuthContext);
  const navigate = useNavigate();
  if (!auth) return null;

  const { logout } = auth;

  const goToSettings = () => {
    close();
    navigate("/settings");
  };

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <div style={styles.menu}>
      <button style={styles.item} onClick={goToSettings}>
        ⚙️ Postavke
      </button>
      <button style={styles.item} onClick={handleLogout}>
        🚪 Odjava
      </button>
    </div>
  );
}

const styles = {
  menu: {
    position: "absolute",
    right: 0,
    top: 44,
    background: "white",
    border: "1px solid #ddd",
    borderRadius: 6,
    minWidth: 160,
    boxShadow: "0 4px 10px rgba(0,0,0,0.1)",
    zIndex: 100,
  },
  item: {
    width: "100%",
    padding: "10px 12px",
    textAlign: "left" as const,
    background: "none",
    border: "none",
    cursor: "pointer",
  },
};
