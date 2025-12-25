import { useState, useContext } from "react";
import { login, register } from "../api/authApi";
import { AuthContext } from "./AuthContext";
import { toast } from "react-toastify";

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
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const submit = async () => {
  setLoading(true);

  try {
    const token =
      mode === "login"
        ? await login(email, password)
        : await register(email, password, name);

    authLogin(token);
  } catch (err: any) {
    if (err.response) {
      if (err.response.status === 401) {
        toast.error("Neispravni podaci za prijavu.");
      } else if (err.response.status === 404) {
        toast.error("Korisnik ne postoji. Registrirajte se.");
      } else if (err.response.status === 409) {
        toast.error("Email je već registriran.");
      } else {
        toast.error(err.response.data?.message || "Došlo je do greške.");
      }
    } else {
      toast.error("Greška mreže. Pokušajte ponovno.");
    }
  } finally {
    setLoading(false);
  }
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

      <button
        className="primary"
        onClick={submit}
        disabled={loading}
      >
        {loading
          ? "Molimo pričekajte..."
          : mode === "login"
            ? "Prijavi se"
            : "Registriraj se"}
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
