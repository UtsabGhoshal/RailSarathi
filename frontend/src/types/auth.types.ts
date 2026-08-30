export type UserRole = 'ROLE_PASSENGER' | 'ROLE_ADMIN' | 'ROLE_OPERATOR';

export interface User {
  id: number;
  fullName: string;
  email: string;
  username: string;
  phoneNumber?: string;
  phone?: string;
  role: UserRole;
  createdAt?: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  sessionId: string;
  user: User;
}

export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

export interface RegisterRequest {
  fullName: string;
  username: string;
  email: string;
  password: string;
  phoneNumber?: string;
  phone?: string;
  role?: UserRole;
}
