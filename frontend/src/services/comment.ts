import { request } from './api';
import type { Comment } from '@/types';

// 评论服务
export const commentService = {
  // 获取评论列表
  list: (documentId: number) =>
    request.get<Comment[]>(`/documents/${documentId}/comments`),
  
  // 添加评论
  create: (documentId: number, data: { content: string; parentId?: number }) =>
    request.post<Comment>(`/documents/${documentId}/comments`, data),
  
  // 删除评论
  delete: (commentId: number) =>
    request.delete(`/comments/${commentId}`)
};