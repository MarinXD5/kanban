/* eslint-disable react-hooks/rules-of-hooks */
import { useContext, useState } from "react";
import { AuthContext } from "./AuthContext";
import { updateUser } from "../api/userApi";

export default function SettingsPage() {
  const auth = useContext(AuthContext);
  if (!auth?.user) return null;

  const [form, setForm] = useState({
    email: auth.user.email,
    currentPassword: "",
    newPassword: "",
    avatarColor: auth.user.avatarColor || "#cccccc",
  });

  const userId = auth.user.id;
  const setUser = auth.setUser;

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  function updateField(key: string, value: string) {
    setForm(f => ({ ...f, [key]: value }));
  }

  async function handleSubmit(e: { preventDefault: () => void; }) {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(false);

    try {
      const res = await updateUser(userId, {
      email: form.email,
      currentPassword: form.currentPassword,
      password: form.newPassword || null,
      avatarColor: form.avatarColor,
      });

      setUser(res);

      setForm(f => ({
        ...f,
        currentPassword: "",
        newPassword: "",
      }));

      setSuccess(true);
    } catch (err) {
      setError(err?.message || "Something went wrong");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h2>Postavke</h2>

        <label>Email</label>
        <input
          value={form.email}
          onChange={e => updateField("email", e.target.value)}
        />

        <label>Trenutna lozinka</label>
        <input
          type="password"
          value={form.currentPassword}
          onChange={e => updateField("currentPassword", e.target.value)}
          required
        />

        <label>Nova lozinka (opcionalno)</label>
        <input
          type="password"
          value={form.newPassword}
          onChange={e => updateField("newPassword", e.target.value)}
          placeholder="Ostavite prazno za trenutnu lozinku"
        />

        <label>Boja avatara</label>
        <input
          type="color"
          value={form.avatarColor}
          onChange={e => updateField("avatarColor", e.target.value)}
        />

        {error && <p className="error">{error}</p>}
        {success && <p className="success">Promjene spremljene</p>}

        <button className="primary" disabled={loading}>
          {loading ? "Spremanje..." : "Spremi promjene"}
        </button>
      </form>
    </div>
  );
}
