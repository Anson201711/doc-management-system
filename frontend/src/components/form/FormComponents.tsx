import React, { useState } from 'react';
import { Modal, Form, Input, Select, DatePicker, InputNumber, Switch } from 'antd';
import type { FormProps } from 'antd';

const { TextArea } = Input;

export interface FormField {
  name: string;
  label: string;
  type: 'text' | 'textarea' | 'select' | 'date' | 'daterange' | 'number' | 'switch' | 'password';
  placeholder?: string;
  options?: { label: string; value: any }[];
  width?: number | 'sm' | 'md' | 'lg';
  required?: boolean;
  initialValue?: any;
  disabled?: boolean;
}

export interface SearchFormProps {
  fields: FormField[];
  onSearch: (values: any) => void;
  onReset?: () => void;
}

export interface FormModalProps {
  title: string;
  open: boolean;
  onCancel: () => void;
  onSubmit: (values: any) => Promise<void>;
  fields: FormField[];
  initialValues?: any;
  width?: number;
  formProps?: FormProps;
}

export const SearchForm: React.FC<SearchFormProps> = ({ fields, onSearch, onReset }) => {
  const [form] = Form.useForm();

  const handleFinish = (values: any) => {
    onSearch(values);
  };

  const renderField = (field: FormField) => {
    const commonStyle = { width: field.width === 'sm' ? 120 : field.width === 'md' ? 200 : field.width === 'lg' ? 300 : field.width || 200 };
    
    switch (field.type) {
      case 'text':
        return <Input placeholder={field.placeholder} style={commonStyle} />;
      case 'textarea':
        return <TextArea placeholder={field.placeholder} rows={1} style={{ width: 200 }} />;
      case 'select':
        return <Select placeholder={field.placeholder} style={commonStyle} options={field.options} />;
      case 'number':
        return <InputNumber placeholder={field.placeholder} style={{ width: field.width === 'sm' ? 100 : field.width || 150 }} />;
      case 'date':
        return <DatePicker placeholder={field.placeholder} />;
      case 'switch':
        return <Switch checkedChildren="是" unCheckedChildren="否" />;
      case 'password':
        return <Input.Password placeholder={field.placeholder} style={commonStyle} />;
      default:
        return <Input placeholder={field.placeholder} style={commonStyle} />;
    }
  };

  return (
    <Form form={form} layout="inline" onFinish={handleFinish} style={{ marginBottom: 16 }}>
      {fields.map((field) => (
        <Form.Item key={field.name} name={field.name} label={field.label} initialValue={field.initialValue}>
          {renderField(field)}
        </Form.Item>
      ))}
      <Form.Item>
        <div style={{ display: 'flex', gap: 8 }}>
          <button type="submit" style={{ display: 'inline-flex', alignItems: 'center', padding: '4px 15px', border: '1px solid #d9d9d9', borderRadius: 4, cursor: 'pointer', background: '#fff' }}>
            查询
          </button>
          <button type="button" onClick={() => { form.resetFields(); onReset?.(); }} style={{ display: 'inline-flex', alignItems: 'center', padding: '4px 15px', border: '1px solid #d9d9d9', borderRadius: 4, cursor: 'pointer', background: '#fff' }}>
            重置
          </button>
        </div>
      </Form.Item>
    </Form>
  );
};

export const FormModal: React.FC<FormModalProps> = ({ title, open, onCancel, onSubmit, fields, initialValues, width = 500, formProps }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const handleFinish = async (values: any) => {
    setLoading(true);
    try {
      await onSubmit(values);
      form.resetFields();
    } finally {
      setLoading(false);
    }
  };

  const renderField = (field: FormField) => {
    switch (field.type) {
      case 'text':
        return <Input placeholder={field.placeholder} disabled={field.disabled} />;
      case 'textarea':
        return <TextArea placeholder={field.placeholder} rows={3} disabled={field.disabled} />;
      case 'select':
        return <Select placeholder={field.placeholder} options={field.options} disabled={field.disabled} />;
      case 'number':
        return <InputNumber placeholder={field.placeholder} style={{ width: '100%' }} disabled={field.disabled} />;
      case 'date':
        return <DatePicker placeholder={field.placeholder} style={{ width: '100%' }} disabled={field.disabled} />;
      case 'switch':
        return <Switch checkedChildren="是" unCheckedChildren="否" disabled={field.disabled} />;
      case 'password':
        return <Input.Password placeholder={field.placeholder} disabled={field.disabled} />;
      default:
        return <Input placeholder={field.placeholder} disabled={field.disabled} />;
    }
  };

  return (
    <Modal
      title={title}
      open={open}
      onCancel={onCancel}
      onOk={() => form.submit()}
      confirmLoading={loading}
      width={width}
      destroyOnClose
    >
      <Form form={form} layout="vertical" initialValues={initialValues} onFinish={handleFinish} {...formProps}>
        {fields.map((field) => (
          <Form.Item key={field.name} name={field.name} label={field.label} rules={field.required ? [{ required: true, message: `请输入${field.label}` }] : []}>
            {renderField(field)}
          </Form.Item>
        ))}
      </Form>
    </Modal>
  );
};

// 创建/编辑表单的便捷组件
interface EditFormProps {
  open: boolean;
  initialValues?: any;
  onSubmit: (values: any) => Promise<void>;
  onCancel: () => void;
}

export const DocumentForm: React.FC<EditFormProps> = ({ open, initialValues, onSubmit, onCancel }) => {
  const fields: FormField[] = [
    { name: 'title', label: '文档标题', type: 'text', placeholder: '请输入文档标题', required: true },
    { name: 'documentType', label: '文档类型', type: 'select', placeholder: '请选择文档类型', options: [
      { label: 'Word', value: 'word' },
      { label: 'Excel', value: 'excel' },
      { label: 'PDF', value: 'pdf' },
      { label: 'Markdown', value: 'markdown' },
      { label: '其他', value: 'other' }
    ]},
    { name: 'tags', label: '标签', type: 'text', placeholder: '多个标签用逗号分隔' },
    { name: 'content', label: '文档内容', type: 'textarea', placeholder: '请输入文档内容' }
  ];

  return <FormModal title={initialValues?.id ? '编辑文档' : '新建文档'} open={open} fields={fields} initialValues={initialValues} onSubmit={onSubmit} onCancel={onCancel} width={600} />;
};

export const FolderForm: React.FC<EditFormProps> = ({ open, initialValues, onSubmit, onCancel }) => {
  const fields: FormField[] = [
    { name: 'name', label: '文件夹名称', type: 'text', placeholder: '请输入文件夹名称', required: true }
  ];

  return <FormModal title={initialValues?.id ? '编辑文件夹' : '新建文件夹'} open={open} fields={fields} initialValues={initialValues} onSubmit={onSubmit} onCancel={onCancel} width={400} />;
};