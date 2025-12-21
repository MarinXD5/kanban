export type AuthUser = {
  id: number;
  email: string;
  name: string;
  avatarColor?: string;
};

const KEY = "very-secret-key-change-me-very-secret";

export const authStorage = {
  set(token: string, user: AuthUser) {
    sessionStorage.setItem(
      KEY,
      JSON.stringify({ token, user })
    );
  },

  get() {
    const raw = sessionStorage.getItem(KEY);
    return raw ? JSON.parse(raw) : null;
  },

  clear() {
    sessionStorage.removeItem(KEY);
  },
};
