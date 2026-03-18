import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Space, Tag, message, Dropdown } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, CopyOutlined, MoreOutlined, FileTextOutlined, HistoryOutlined, ShareAltOutlined, CommentOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { PageTable } from '@/components/table';
import { SearchForm } from '@/components/form';
import { confirmService } from '@/components/common';
import { documentService } from '@/services/document';
import type { Document, DocumentListParams } from '@/types';
import dayjs from 'dayjs';

const DocumentsPage: React.FC = () => {
  const navigate = useNavigate();
  const [data, setData] = useState<Document[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchParams, setSearchParams] = useState<DocumentListParams>({});

  const fetchData = async () => {
    setLoading(true);
    try {
      const res = await documentService.list({ page, pageSize, ...searchParams });
      setData(res.data.list);
      setTotal(res.data.total);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [page, pageSize, searchParams]);

  const handleSearch = (values: any) => {
    setSearchParams(values);
    setPage(1);
  };

  const handleDelete = (record: Document) => {
    confirmService.confirm({
      title: '确认删除',
      content: `确定要删除文档"${record.title}"吗？`,
      onOk: async () => {
        try {
          await documentService.delete(record.id);
          message.success('删除成功');
          fetchData();
        } catch {
          // error handled
        }
      }
    });
  };

  const handleCopy = async (record: Document) => {
    try {
      await documentService.copy(record.id);
      message.success('复制成功');
      fetchData();
    } catch {
      // error handled
    }
  };

  // 更多操作菜单
  const getMenuItems = (record: Document) => [
    { key: 'edit', icon: <EditOutlined />, label: '编辑', onClick: () => navigate(`/documents/${record.id}/edit`) },
    { key: 'versions', icon: <HistoryOutlined />, label: '版本历史', onClick: () => navigate(`/documents/${record.id}/versions`) },
    { key: 'permissions', icon: <ShareAltOutlined />, label: '权限管理', onClick: () => navigate(`/documents/${record.id}/permissions`) },
    { key: 'comments', icon: <CommentOutlined />, label: '评论', onClick: () => navigate(`/documents/${record.id}/comments`) },
    { type: 'divider' as const },
    { key: 'copy', icon: <CopyOutlined />, label: '复制', onClick: () => handleCopy(record) },
    { key: 'delete', icon: <DeleteOutlined />, label: '删除', danger: true, onClick: () => handleDelete(record) }
  ];

  const columns: ColumnsType<Document> = [
    { 
      title: '标题', 
      dataIndex: 'title', 
      key: 'title', 
      width: 300,
      render: (title: string, record: Document) => (
        <a onClick={() => navigate(`/documents/${record.id}/edit`)} style={{ fontWeight: 500 }}>
          <FileTextOutlined style={{ marginRight: 8 }} />
          {title}
        </a>
      )
    },
    { title: '类型', dataIndex: 'documentType', key: 'documentType', width: 100, render: (type) => <Tag>{type || '-'}</Tag> },
    { title: '状态', dataIndex: 'status', key: 'status', width: 100, render: (status) => <Tag color={status === 'published' ? 'green' : status === 'draft' ? 'default' : 'orange'}>{status === 'published' ? '已发布' : status === 'draft' ? '草稿' : '已归档'}</Tag> },
    { title: '版本', dataIndex: 'currentVersion', key: 'currentVersion', width: 80 },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 180, render: (val) => dayjs(val).format('YYYY-MM-DD HH:mm') },
    { title: '更新时间', dataIndex: 'updatedAt', key: 'updatedAt', width: 180, render: (val) => dayjs(val).format('YYYY-MM-DD HH:mm') },
    {
      title: '操作',
      key: 'action',
      width: 120,
      render: (_, record) => (
        <Space>
          <Button type="text" icon={<EditOutlined />} onClick={() => navigate(`/documents/${record.id}/edit`)} title="编辑" />
          <Dropdown menu={{ items: getMenuItems(record) }} trigger={['click']}>
            <Button type="text" icon={<MoreOutlined />} title="更多" />
          </Dropdown>
        </Space>
      )
    }
  ];

  const searchFields = [
    { name: 'keyword', label: '关键词', type: 'text', placeholder: '搜索标题' },
    { name: 'status', label: '状态', type: 'select', options: [{ label: '草稿', value: 'draft' }, { label: '已发布', value: 'published' }, { label: '已归档', value: 'archived' }] }
  ];

  return (
    <div>
      <SearchForm fields={searchFields} onSearch={handleSearch} onReset={() => setSearchParams({})} />
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/documents/new')}>新建文档</Button>
      </div>
      <PageTable
        columns={columns}
        dataSource={data}
        loading={loading}
        rowKey="id"
        pagination={{ current: page, pageSize, total, onChange: (p, ps) => { setPage(p); setPageSize(ps); }, showTotal: (t) => `共 ${t} 条`, showSizeChanger: true }}
      />
    </div>
  );
};

export default DocumentsPage;