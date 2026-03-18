import axios, { AxiosInstance, AxiosRequestConfig } from 'axios';
import { message } from 'antd';
import type { ApiResponse } from '@/types';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1';

const axiosInstance: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器 - 添加Token
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器 - 统一处理错误
axiosInstance.interceptors.response.use(
  (response) => {
    const { data } = response;
    const { code, message: msg } = data || {};
    
    if (code === 200) {
      return response;
    }
    
    // 处理业务错误
    if (code === 401) {
      message.error('登录已过期，请重新登录');
      localStorage.removeItem('token');
      window.location.href = '/login';
      return Promise.reject(new Error(msg));
    }
    
    message.error(msg || '请求失败');
    return Promise.reject(new Error(msg));
  },
  (error) => {
    // 处理网络错误
    if (error.response) {
      const status = error.response.status;
      if (status === 401) {
        message.error('登录已过期，请重新登录');
        localStorage.removeItem('token');
        window.location.href = '/login';
      } else if (status === 403) {
        message.error('没有权限访问');
      } else if (status >= 500) {
        message.error('服务器错误，请稍后重试');
      } else {
        message.error(error.response.data?.message || '请求失败');
      }
    } else if (error.request) {
      message.error('网络连接失败，请检查网络');
    } else {
      message.error('请求配置错误');
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;

// 封装请求方法
export const request = {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return axiosInstance.get<ApiResponse<T>>(url, config).then(res => res.data);
  },
  
  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return axiosInstance.post<ApiResponse<T>>(url, data, config).then(res => res.data);
  },
  
  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return axiosInstance.put<ApiResponse<T>>(url, data, config).then(res => res.data);
  },
  
  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return axiosInstance.delete<ApiResponse<T>>(url, config).then(res => res.data);
  },
  
  patch<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return axiosInstance.patch<ApiResponse<T>>(url, data, config).then(res => res.data);
  }
};

// 上传文件
export const uploadFile = async (file: File, onProgress?: (percent: number) => void): Promise<ApiResponse<string>> => {
  const formData = new FormData();
  formData.append('file', file);
  
  return axiosInstance.post('/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    onUploadProgress: (progressEvent) => {
      if (progressEvent.total && onProgress) {
        const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total);
        onProgress(percent);
      }
    }
  }).then(res => res.data);
};