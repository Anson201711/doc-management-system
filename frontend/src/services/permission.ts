import { request } from './api';
import type { Permission } from '@/types';

// 权限服务
export const permissionService = {
  // 获取权限列表
  list: (documentId: number) =>
    request.get<Permission[]>(`/documents/${documentId}/permissions`),
  
  // 添加权限
  create: (documentId: number, data: { userId: number; permissionType: string }) =>
    request.post<Permission>(`/documents/${documentId}/permissions`, data),
  
  // 更新权限
  update: (documentId: number, permissionId: number, data: { permissionType: string }) =>
    request.put<Permission>(`/documents/${documentId}/permissions/${permissionId}`, data),
  
  // 删除权限
  delete: (documentId: number, permissionId: number) =>
    request.delete(`/documents/${documentId}/permissions/${permissionId}`)
};