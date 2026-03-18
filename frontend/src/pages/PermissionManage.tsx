import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Table, Button, Tag, Space, Modal, Select, message, Popconfirm } from 'antd';
import { ArrowLeftOutlined, PlusOutlined, DeleteOutlined, UserOutlined } from '@ant-design/icons';
import { documentService } from '@/services/document';
import { permissionService } from '@/services/permission';
import type { Permission, Document } from '@/types';

const PermissionManage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [document, setDocument] = useState<Document | null>(null);
  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalVisible, setModalVisible] = useState(false);
  const [selectedUser, setSelectedUser] = useState<number | null>(null);
  const [permissionType, setPermissionType] = useState<string>('read');
  const [submitting, setSubmitting] = useState(false);

  // 模拟用户列表（实际应从用户服务获取）
  const availableUsers = [
    { id: 1, username: 'user1', fullName: '用户1' },
    { id: 2, username: 'user2', fullName: '用户2' },
    { id: 3, username: 'user3', fullName: '用户3' },
    { id: 4, username: 'user4', fullName: '用户4' },
    { id: 5, username: 'user5', fullName: '用户5' },
  ];

  useEffect(() => {
    loadData();
  }, [id]);

  const loadData = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const [docRes, permRes] = await Promise.all([
        documentService.getById(Number(id)),
        permissionService.list(Number(id))
      ]);
      setDocument(docRes.data);
      setPermissions(permRes.data);
    } catch (error) {
      message.error('加载数据失败');
    } finally {
      setLoading(false);
    }
  };

  const handleAddPermission = async () => {
    if (!selectedUser || !id) return;
    setSubmitting(true);
    try {
      await permissionService.create(Number(id), {
        userId: selectedUser,
        permissionType
      });
      message.success('添加成功');
      setModalVisible(false);
      setSelectedUser(null);
      loadData();
    } catch (error) {
      // error handled
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (permissionId: number) => {
    try {
      await permissionService.delete(Number(id), permissionId);
      message.success('删除成功');
      loadData();
    } catch {
      // error handled
    }
  };

  const getPermissionColor = (type: string) => {
    switch (type) {
      case 'admin': return 'red';
      case 'write': return 'orange';
      case 'read': return 'blue';
      default: return 'default';
    }
  };

  const getPermissionLabel = (type: string) => {
    switch (type) {
      case 'admin': return '管理员';
      case 'write': return '可编辑';
      case 'read': return '可查看';
      default: return type;
    }
  };

  const columns = [
    {
      title: '用户',
      dataIndex: 'user',
      key: 'user',
      render: (user: any) => (
        <Space>
          <UserOutlined />
          <span>{user?.username || '未知'}</span>
          {user?.fullName && <span style={{ color: '#999' }}>({user.fullName})</span>}
        </Space>
      )
    },
    {
      title: '权限',
      dataIndex: 'permissionType',
      key: 'permissionType',
      render: (type: string) => (
        <Tag color={getPermissionColor(type)}>{getPermissionLabel(type)}</Tag>
      )
    },
    {
      title: '授权时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (val: string) => new Date(val).toLocaleString()
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: Permission) => (
        <Popconfirm
          title="确认移除"
          description="确定要移除该用户的权限吗？"
          onConfirm={() => handleDelete(record.id)}
        >
          <Button type="text" danger icon={<DeleteOutlined />}>移除</Button>
        </Popconfirm>
      )
    }
  ];

  // 过滤掉已有权限的用户
  const usedUserIds = permissions.map(p => p.userId);
  const filteredUsers = availableUsers.filter(u => !usedUserIds.includes(u.id));

  return (
    <div style={{ padding: 24 }}>
      <Card
        title={
          <Space>
            <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(-1)}>返回</Button>
            <span>权限管理 - {document?.title}</span>
          </Space>
        }
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalVisible(true)}>
            添加权限
          </Button>
        }
        loading={loading}
      >
        <Table
          columns={columns}
          dataSource={permissions}
          rowKey="id"
          pagination={false}
          locale={{ emptyText: '暂无权限配置' }}
        />
      </Card>

      <Modal
        title="添加权限"
        open={modalVisible}
        onCancel={() => { setModalVisible(false); setSelectedUser(null); }}
        onOk={handleAddPermission}
        confirmLoading={submitting}
      >
        <div style={{ display: 'flex', gap: 16, marginBottom: 16 }}>
          <Select
            style={{ width: 200 }}
            placeholder="选择用户"
            value={selectedUser}
            onChange={setSelectedUser}
            options={filteredUsers.map(u => ({ 
              label: u.fullName || u.username, 
              value: u.id 
            }))}
          />
          <Select
            style={{ width: 120 }}
            value={permissionType}
            onChange={setPermissionType}
            options={[
              { label: '可查看', value: 'read' },
              { label: '可编辑', value: 'write' },
              { label: '管理员', value: 'admin' }
            ]}
          />
        </div>
      </Modal>
    </div>
  );
};

export default PermissionManage;