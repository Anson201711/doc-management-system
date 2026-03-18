import { request } from './api';
import type { Folder } from '@/types';

// 文件夹服务
export const folderService = {
  // 获取文件夹树
  getTree: () =>
    request.get<Folder[]>('/folders/tree'),
  
  // 获取文件夹列表
  list: (parentId?: number) =>
    request.get<Folder[]>('/folders', { params: { parentId } }),
  
  // 获取文件夹详情
  getById: (id: number) =>
    request.get<Folder>(`/folders/${id}`),
  
  // 创建文件夹
  create: (data: { name: string; parentId?: number }) =>
    request.post<Folder>('/folders', data),
  
  // 重命名文件夹
  rename: (id: number, name: string) =>
    request.put<Folder>(`/folders/${id}`, { name }),
  
  // 删除文件夹
  delete: (id: number) =>
    request.delete(`/folders/${id}`),
  
  // 移动文件夹
  move: (id: number, targetParentId: number) =>
    request.post(`/folders/${id}/move`, { targetParentId })
};