export interface LoginForm {
  username: string
  password: string
  remember?: boolean
  captcha?: string
  uuid?: string
}

export interface RegisterForm {
  username: string
  email: string
  password: string
  confirmPassword: string
  agreement?: boolean
}

export interface UserInfo {
  id: number
  username: string
  nickname?: string
  email?: string
  avatar?: string
  avatarUrl?: string
  role?: string
  fullName?: string
}

export interface LoginResponse {
  token: string
  user: UserInfo
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 保持原有类型兼容
export interface User extends UserInfo {}
export interface LoginParams extends LoginForm {}

export interface Comment {
  id: number
  documentId: number
  userId: number
  user?: UserInfo
  content: string
  createdAt: string
}

export interface Document {
  id: number
  title: string
  content: string
  folderId?: number
  createdBy: number
  creator?: UserInfo
  createdAt: string
  updatedAt: string
  status?: string
  documentType?: string
}

export interface DocumentVersion {
  id: number
  documentId: number
  version: number
  content: string
  createdBy: number
  creator?: UserInfo
  createdAt: string
  changeSummary?: string
}

export interface DocumentListParams {
  page?: number
  pageSize?: number
  folderId?: number
  keyword?: string
}

export interface PaginatedResponse<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
}

export interface Folder {
  id: number
  name: string
  parentId?: number
  createdBy: number
  createdAt: string
  children?: Folder[]
}

export interface Permission {
  id: number
  documentId: number
  userId: number
  role: string
  createdAt: string
}

export interface Workflow {
  id: number
  documentId: number
  document?: Document
  title?: string
  status: string
  currentStep: number
  currentStatus?: string
  currentApproverId?: number
  createdBy: number
  creator?: UserInfo
  createdAt: string
}

export interface WorkflowLog {
  id: number
  workflowId: number
  step: number
  action: string
  userId: number
  approver?: UserInfo
  comment?: string
  createdAt: string
}

export interface Attachment {
  id: number
  documentId: number
  filename: string
  url: string
  size: number
  createdAt: string
}

export type FormField = {
  name: string
  label: string
  type: 'text' | 'textarea' | 'select' | 'date' | 'daterange' | 'switch' | 'password' | 'number'
  placeholder?: string
  options?: { label: string; value: string }[]
}