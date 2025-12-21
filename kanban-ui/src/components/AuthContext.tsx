import { createContext, useState } from "react";
import { authStorage, type AuthUser } from "../api/authStorage";
import type { AuthResponse } from "../types/AuthResponse";

type AuthContextType = {
  user: AuthUser | null;
  setUser: (user: AuthUser) => void;
  login: (data: AuthResponse) => void;
  logout: () => void;
};

// eslint-disable-next-line react-refresh/only-export-components
export const AuthContext =
  createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => {
    return authStorage.get()?.user ?? null;
  });

  const login = (res: AuthResponse) => {
    authStorage.set(res.token, {
      id: res.id,
      email: res.email,
      name: res.name,
      avatarColor: res.avatarColor
    });
    setUser({
      id: res.id,
      email: res.email,
      name: res.name,
      avatarColor: res.avatarColor
    });
  };

  const logout = () => {
    authStorage.clear();
    setUser(null);
  };

  return (
     <AuthContext.Provider
      value={{
        user,
        setUser,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
