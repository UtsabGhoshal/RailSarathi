import { request } from './client';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../types/auth.types';

export const authApi = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    return request<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials),
    });
  },

  register: async (payload: RegisterRequest): Promise<AuthResponse> => {
    return request<AuthResponse>('/auth/register', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  getProfile: async (): Promise<User> => {
    return request<User>('/users/profile', {
      method: 'GET',
    });
  },
};
