// API 统一响应格式
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  timestamp: string;
}

// 分页响应
export interface PaginatedResponse<T> {
  list: T[];
  total: number;
  page: number;
  pageSize: number;
}

// 用户相关类型
export interface User {
  id: number;
  username: string;
  email: string;
  fullName?: string;
  avatarUrl?: string;
  status: 'active' | 'inactive';
  createdAt: string;
  updatedAt: string;
}

export interface LoginParams {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  refreshToken: string;
  user: User;
}

// 文档相关类型
export interface Document {
  id: number;
  title: string;
  content?: string;
  folderId?: number;
  creatorId: number;
  creator?: User;
  status: 'draft' | 'published' | 'archived';
  documentType?: string;
  tags?: string[];
  currentVersion: number;
  createdAt: string;
  updatedAt: string;
}

export interface DocumentVersion {
  id: number;
  documentId: number;
  version: number;
  content?: string;
  changeSummary?: string;
  creatorId: number;
  creator?: User;
  createdAt: string;
}

export interface DocumentListParams {
  page?: number;
  pageSize?: number;
  folderId?: number;
  status?: string;
  keyword?: string;
}

// 文件夹相关类型
export interface Folder {
  id: number;
  name: string;
  parentId?: number;
  ownerId: number;
  owner?: User;
  children?: Folder[];
  createdAt: string;
  updatedAt: string;
}

// 权限相关类型
export interface Permission {
  id: number;
  documentId: number;
  userId: number;
  user?: User;
  permissionType: 'read' | 'write' | 'admin';
  createdBy: number;
  createdAt: string;
}

// 评论相关类型
export interface Comment {
  id: number;
  documentId: number;
  userId: number;
  user?: User;
  parentId?: number;
  content: string;
  replies?: Comment[];
  createdAt: string;
  updatedAt: string;
}

// 工作流相关类型
export interface Workflow {
  id: number;
  documentId: number;
  document?: Document;
  creatorId: number;
  creator?: User;
  title: string;
  currentStatus: 'pending' | 'approved' | 'rejected';
  currentApproverId?: number;
  currentApprover?: User;
  createdAt: string;
  completedAt?: string;
}

export interface WorkflowLog {
  id: number;
  workflowId: number;
  approverId: number;
  approver?: User;
  action: 'approve' | 'reject';
  comment?: string;
  createdAt: string;
}

// 附件相关类型
export interface Attachment {
  id: number;
  documentId: number;
  fileName: string;
  fileSize: number;
  fileType: string;
  storageKey: string;
  uploaderId: number;
  uploader?: User;
  createdAt: string;
}

// 协作会话类型
export interface CollabSession {
  documentId: number;
  userId: number;
  user?: User;
  sessionId: string;
  cursorPosition?: number;
  lastActiveAt: string;
}