import { documentService, documentVersionService } from '@/services/document'
import type { Document, DocumentListParams } from '@/types'

// 文档列表查询
export const getDocumentList = (params: DocumentListParams) => {
  return documentService.list(params)
}

// 删除文档
export const deleteDocument = (id: number) => {
  return documentService.delete(id)
}

// 新建文档
export const createDocument = (data: Partial<Document>) => {
  return documentService.create(data)
}

// 更新文档
export const updateDocument = (id: number, data: Partial<Document>) => {
  return documentService.update(id, data)
}

// 获取文档详情
export const getDocumentDetail = (id: number) => {
  return documentService.getById(id)
}

// 复制文档
export const copyDocument = (id: number, targetFolderId?: number) => {
  return documentService.copy(id, targetFolderId)
}

// 移动文档
export const moveDocument = (id: number, targetFolderId: number) => {
  return documentService.move(id, targetFolderId)
}

// 文档版本相关
export const getDocumentVersions = (documentId: number) => {
  return documentVersionService.list(documentId)
}

export const rollbackDocumentVersion = (documentId: number, version: number) => {
  return documentVersionService.rollback(documentId, version)
}