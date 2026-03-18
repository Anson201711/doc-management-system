// 重新导出已有的类型，避免重复定义
export type { Document, DocumentListParams } from './user'

// 文档类型选项
export const documentTypes = [
  { label: '技术文档', value: 'technical' },
  { label: '产品文档', value: 'product' },
  { label: '需求文档', value: 'requirement' },
  { label: '测试文档', value: 'test' },
  { label: '用户手册', value: 'manual' }
]

// 文档状态选项
export const documentStatuses = [
  { label: '草稿', value: 'draft' },
  { label: '发布', value: 'published' },
  { label: '归档', value: 'archived' }
]