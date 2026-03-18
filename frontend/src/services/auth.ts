import { request } from './api';
import type { User, LoginParams, LoginResponse } from '@/types';

export interface RegisterParams {
  username: string;
  password: string;
  email: string;
  fullName?: string;
}

// 认证服务
export const authService = {
  login: (params: LoginParams) => 
    request.post<LoginResponse>('/auth/login', params),
  
  register: (params: RegisterParams) =>
    request.post<User>('/auth/register', params),
  
  logout: () => 
    request.post('/auth/logout'),
  
  refreshToken: () => 
    request.post<{ token: string }>('/auth/refresh'),
  
  getCurrentUser: () => 
    request.get<User>('/auth/me'),
  
  updateProfile: (data: Partial<User>) =>
    request.put<User>('/auth/profile', data),
  
  changePassword: (oldPassword: string, newPassword: string) =>
    request.post('/auth/change-password', { oldPassword, newPassword })
};