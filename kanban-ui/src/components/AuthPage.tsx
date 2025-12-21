import { useState, useContext } from "react";
import { login, register } from "../api/authApi";
import { AuthContext } from "./AuthContext";

export default function AuthPage() {
  const auth = useContext(AuthContext);
  if (!auth) {
    throw new Error("AuthPage must be used inside AuthProvider");
  }

  const { login: authLogin } = auth;

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [mode, setMode] = useState<"login" | "register">("login");

  const submit = async () => {
    const res =
      mode === "login"
        ? await login(email, password)
        : await register(email, password, name);

    authLogin(res);
  };

  return (
  <div className="auth-page">
    <div className="auth-card">
      <h2>{mode === "login" ? "Prijavi se" : "Registriraj se"}</h2>

      {mode === "register" && (
        <>
          <label>Ime</label>
          <input
            value={name}
            onChange={e => setName(e.target.value)}
          />
        </>
      )}

      <label>Email</label>
      <input
        value={email}
        onChange={e => setEmail(e.target.value)}
      />

      <label>Lozinka</label>
      <input
        type="password"
        value={password}
        onChange={e => setPassword(e.target.value)}
      />

      <button className="primary" onClick={submit}>
        {mode === "login" ? "Prijavi se" : "Registriraj se"}
      </button>

      <button
        className="link"
        onClick={() =>
          setMode(mode === "login" ? "register" : "login")
        }
      >
        Promijeni na {mode === "login" ? "Registraciju" : "Prijavu"}
      </button>
    </div>
  </div>
);
}
