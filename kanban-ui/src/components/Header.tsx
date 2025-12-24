import { useContext, useState } from "react";
import { AuthContext } from "./AuthContext";
import AvatarMenu from "./AvatarMenu";
import { useNavigate } from "react-router-dom";

export default function Header() {
  const auth = useContext(AuthContext);
  const navigate = useNavigate();
  if (!auth || !auth.user) return null;

  const { user } = auth;
  const [open, setOpen] = useState(false);

  return (
    <header style={styles.header}>
      <div
        style={styles.logo}
        onClick={() => navigate("/")}
      >
        KANBAN
      </div>

      <div style={{ position: "relative" }}>
        <div onClick={() => setOpen(o => !o)}>
          <Avatar
            name={user.name}
            color={user.avatarColor}
          />
        </div>

        {open && <AvatarMenu close={() => setOpen(false)} />}
      </div>
    </header>
  );
}

function Avatar({
  name,
  color,
}: {
  name: string;
  color?: string | null;
}) {
  const initials = name
    .split(" ")
    .map(n => n[0])
    .join("")
    .toUpperCase();

    console.log("Color", color);

  const avatarColor = color ?? "#cccccc";

  return (
    <div
      style={{
        ...styles.avatar,
        background: avatarColor,
      }}
    >
      {initials}
    </div>
  );
}

const styles = {
  header: {
    height: 56,
    padding: "0 24px",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    background: "#020617",
    borderBottom: "1px solid #1e293b",
  },
  logo: {
    fontWeight: 700,
    fontSize: 18,
    cursor: "pointer",
  },
  avatar: {
    width: 36,
    height: 36,
    borderRadius: "50%",
    color: "white",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontWeight: 600,
    cursor: "pointer",
  },
};
