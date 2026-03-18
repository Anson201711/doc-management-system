import { request } from './api';
import type { Workflow, WorkflowLog } from '@/types';

// 工作流服务
export const workflowService = {
  // 创建审批流程
  create: (data: { documentId: number; title: string }) =>
    request.post<Workflow>('/workflows', data),
  
  // 获取流程详情
  getById: (id: number) =>
    request.get<Workflow>(`/workflows/${id}`),
  
  // 审批通过
  approve: (id: number, comment?: string) =>
    request.post(`/workflows/${id}/approve`, { comment }),
  
  // 审批拒绝
  reject: (id: number, comment?: string) =>
    request.post(`/workflows/${id}/reject`, { comment }),
  
  // 获取我的待办流程
  getMyTasks: () =>
    request.get<Workflow[]>('/workflows/my'),
  
  // 获取流程日志
  getLogs: (id: number) =>
    request.get<WorkflowLog[]>(`/workflows/${id}/logs`)
};