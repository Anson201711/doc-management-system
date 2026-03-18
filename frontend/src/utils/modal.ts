import React from 'react';
import { Modal } from 'antd';
import { ExclamationCircleOutlined } from '@ant-design/icons';

const { confirm } = Modal;

interface ConfirmOptions {
  title?: string;
  content: string;
  onOk?: () => void;
  onCancel?: () => void;
  okText?: string;
  cancelText?: string;
  type?: 'info' | 'success' | 'error' | 'warning' | 'question';
}

export const showConfirm = (options: ConfirmOptions) => {
  const { 
    title = '确认', 
    content, 
    onOk, 
    onCancel, 
    okText = '确定', 
    cancelText = '取消'
  } = options;

  return confirm({
    title,
    icon: React.createElement(ExclamationCircleOutlined),
    content,
    okText,
    cancelText,
    onOk,
    onCancel,
    centered: true,
  });
};

export const showDeleteConfirm = (onOk: () => void) => {
  showConfirm({
    title: '确认删除',
    content: '此操作不可恢复，确定要删除吗？',
    okText: '删除',
    cancelText: '取消',
    type: 'error',
    onOk,
  });
};

export { Modal };