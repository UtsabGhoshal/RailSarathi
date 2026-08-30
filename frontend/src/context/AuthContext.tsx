import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { User, LoginRequest, RegisterRequest, AuthResponse } from '../types/auth.types';
import { authApi } from '../api/authApi';

interface AuthContextType {
  user: User | null;
  token: string | null;
  sessionId: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  isAuthModalOpen: boolean;
  authMode: 'login' | 'register';
  openAuthModal: (mode?: 'login' | 'register') => void;
  closeAuthModal: () => void;
  setAuthMode: (mode: 'login' | 'register') => void;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (payload: RegisterRequest) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const TOKEN_KEY = 'railsarathi_token';
const USER_KEY = 'railsarathi_user';
const SESSION_KEY = 'railsarathi_session_id';

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    const saved = localStorage.getItem(USER_KEY);
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState<string | null>(() => localStorage.getItem(TOKEN_KEY));
  const [sessionId, setSessionId] = useState<string | null>(() => localStorage.getItem(SESSION_KEY));
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [isAuthModalOpen, setIsAuthModalOpen] = useState<boolean>(false);
  const [authMode, setAuthMode] = useState<'login' | 'register'>('login');

  const handleAuthSuccess = (data: AuthResponse) => {
    setToken(data.accessToken);
    setUser(data.user);
    setSessionId(data.sessionId);
    localStorage.setItem(TOKEN_KEY, data.accessToken);
    localStorage.setItem(USER_KEY, JSON.stringify(data.user));
    localStorage.setItem(SESSION_KEY, data.sessionId);
    setIsAuthModalOpen(false);
  };

  const logout = useCallback(() => {
    setToken(null);
    setUser(null);
    setSessionId(null);
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(SESSION_KEY);
  }, []);

  // Synchronize profile on initial load if token exists
  useEffect(() => {
    const initAuth = async () => {
      const storedToken = localStorage.getItem(TOKEN_KEY);
      if (storedToken) {
        try {
          const profile = await authApi.getProfile();
          setUser(profile);
          localStorage.setItem(USER_KEY, JSON.stringify(profile));
        } catch {
          // Token expired or invalid
          logout();
        }
      }
      setIsLoading(false);
    };

    initAuth();
  }, [logout]);

  const login = async (credentials: LoginRequest) => {
    const data = await authApi.login(credentials);
    handleAuthSuccess(data);
  };

  const register = async (payload: RegisterRequest) => {
    const data = await authApi.register(payload);
    handleAuthSuccess(data);
  };

  const openAuthModal = (mode: 'login' | 'register' = 'login') => {
    setAuthMode(mode);
    setIsAuthModalOpen(true);
  };

  const closeAuthModal = () => {
    setIsAuthModalOpen(false);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        sessionId,
        isAuthenticated: !!token && !!user,
        isLoading,
        isAuthModalOpen,
        authMode,
        openAuthModal,
        closeAuthModal,
        setAuthMode,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
