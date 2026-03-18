import { request } from './api';
import type { Document, DocumentVersion, DocumentListParams, PaginatedResponse, Attachment } from '@/types';

// 文档服务
export const documentService = {
  // 获取文档列表
  list: (params?: DocumentListParams) =>
    request.get<PaginatedResponse<Document>>('/documents', { params }),
  
  // 获取文档详情
  getById: (id: number) =>
    request.get<Document>(`/documents/${id}`),
  
  // 创建文档
  create: (data: Partial<Document>) =>
    request.post<Document>('/documents', data),
  
  // 更新文档
  update: (id: number, data: Partial<Document>) =>
    request.put<Document>(`/documents/${id}`, data),
  
  // 删除文档
  delete: (id: number) =>
    request.delete(`/documents/${id}`),
  
  // 复制文档
  copy: (id: number, targetFolderId?: number) =>
    request.post<Document>(`/documents/${id}/copy`, { targetFolderId }),
  
  // 移动文档
  move: (id: number, targetFolderId: number) =>
    request.post(`/documents/${id}/move`, { targetFolderId }),
  
  // 上传文档附件
  uploadAttachment: (id: number, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return request.post<Attachment>(`/documents/${id}/upload`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  },
  
  // 下载文档
  download: (id: number) =>
    request.get(`/documents/${id}/download`, { responseType: 'blob' })
};

// 文档版本服务
export const documentVersionService = {
  // 获取版本列表
  list: (documentId: number) =>
    request.get<DocumentVersion[]>(`/documents/${documentId}/versions`),
  
  // 获取指定版本
  get: (documentId: number, version: number) =>
    request.get<DocumentVersion>(`/documents/${documentId}/versions/${version}`),
  
  // 回滚版本
  rollback: (documentId: number, version: number) =>
    request.post(`/documents/${documentId}/versions/${version}/rollback`),
  
  // 恢复版本
  restore: (documentId: number, version: number) =>
    request.post(`/documents/${documentId}/versions/${version}/restore`)
};