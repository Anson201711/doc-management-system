import { message, Modal, notification } from 'antd';

// 消息提示封装
export const messageService = {
  success: (content: string) => message.success(content),
  error: (content: string) => message.error(content),
  warning: (content: string) => message.warning(content),
  info: (content: string) => message.info(content),
  loading: (content: string) => message.loading(content)
};

// 通知提醒封装
export const notificationService = {
  success: (options: { title?: string; description?: string }) => {
    notification.success({ message: options.title, description: options.description, placement: 'topRight' });
  },
  error: (options: { title?: string; description?: string }) => {
    notification.error({ message: options.title, description: options.description, placement: 'topRight' });
  },
  warning: (options: { title?: string; description?: string }) => {
    notification.warning({ message: options.title, description: options.description, placement: 'topRight' });
  },
  info: (options: { title?: string; description?: string }) => {
    notification.info({ message: options.title, description: options.description, placement: 'topRight' });
  }
};

// 确认对话框封装
export const confirmService = {
  confirm: (options: { title?: string; content: string; onOk?: () => void; onCancel?: () => void }) => {
    Modal.confirm({
      title: options.title || '确认',
      content: options.content,
      onOk: options.onOk,
      onCancel: options.onCancel
    });
  },
  info: (options: { title?: string; content: string; onOk?: () => void }) => {
    Modal.info({
      title: options.title || '提示',
      content: options.content,
      onOk: options.onOk
    });
  },
  success: (options: { title?: string; content: string; onOk?: () => void }) => {
    Modal.success({
      title: options.title || '成功',
      content: options.content,
      onOk: options.onOk
    });
  },
  error: (options: { title?: string; content: string; onOk?: () => void }) => {
    Modal.error({
      title: options.title || '错误',
      content: options.content,
      onOk: options.onOk
    });
  },
  warning: (options: { title?: string; content: string; onOk?: () => void }) => {
    Modal.warning({
      title: options.title || '警告',
      content: options.content,
      onOk: options.onOk
    });
  }
};