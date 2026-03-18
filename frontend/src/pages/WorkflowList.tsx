import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Table, Tag, Button, Space, message } from 'antd';
import { PlusOutlined, EyeOutlined, SyncOutlined } from '@ant-design/icons';
import { workflowService } from '@/services/workflow';
import type { Workflow } from '@/types';
import dayjs from 'dayjs';

const WorkflowList: React.FC = () => {
  const navigate = useNavigate();
  const [data, setData] = useState<Workflow[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshKey, setRefreshKey] = useState(0);

  useEffect(() => {
    loadData();
  }, [refreshKey]);

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await workflowService.getMyTasks();
      setData(res.data || []);
    } catch (error) {
      message.error('加载数据失败');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'pending': return 'processing';
      case 'approved': return 'success';
      case 'rejected': return 'error';
      default: return 'default';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'pending': return '待审批';
      case 'approved': return '已通过';
      case 'rejected': return '已拒绝';
      default: return status;
    }
  };

  const columns = [
    {
      title: '流程标题',
      dataIndex: 'title',
      key: 'title',
      render: (title: string, record: Workflow) => (
        <a onClick={() => navigate(`/workflows/${record.id}`)}>{title}</a>
      )
    },
    {
      title: '文档',
      dataIndex: 'document',
      key: 'document',
      render: (doc: any) => doc?.title || '-'
    },
    {
      title: '申请人',
      dataIndex: 'creator',
      key: 'creator',
      render: (user: any) => user?.username || '-'
    },
    {
      title: '状态',
      dataIndex: 'currentStatus',
      key: 'currentStatus',
      render: (status: string) => (
        <Tag color={getStatusColor(status)}>{getStatusLabel(status)}</Tag>
      )
    },
    {
      title: '当前审批人',
      dataIndex: 'currentApprover',
      key: 'currentApprover',
      render: (user: any) => user?.username || '-'
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (val: string) => dayjs(val).format('YYYY-MM-DD HH:mm')
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: Workflow) => (
        <Space>
          <Button 
            type="link" 
            icon={<EyeOutlined />} 
            onClick={() => navigate(`/workflows/${record.id}`)}
          >
            查看
          </Button>
          {record.currentStatus === 'pending' && (
            <Button 
              type="link" 
              onClick={() => navigate(`/documents/${record.documentId}/edit`)}
            >
              审批
            </Button>
          )}
        </Space>
      )
    }
  ];

  return (
    <div>
      <Card
        title="审批流程"
        extra={
          <Space>
            <Button 
              icon={<SyncOutlined />} 
              onClick={() => setRefreshKey(k => k + 1)}
            >
              刷新
            </Button>
            <Button type="primary" icon={<PlusOutlined />}>
              创建申请
            </Button>
          </Space>
        }
      >
        <Table
          columns={columns}
          dataSource={data}
          rowKey="id"
          loading={loading}
          pagination={{ 
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条`
          }}
        />
      </Card>
    </div>
  );
};

export default WorkflowList;