import { request } from './api';
import type { Document } from '@/types';

// 搜索结果类型
export interface SearchResult {
  documents: Document[];
  total: number;
  keyword: string;
}

// 搜索服务
export const searchService = {
  // 全局搜索
  search: (keyword: string, page = 1, pageSize = 20) =>
    request.get<SearchResult>('/search', { params: { keyword, page, pageSize } })
};