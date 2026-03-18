import React, { useState, useEffect } from 'react';
import { Button, Tree, Space, message } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { folderService } from '@/services/folder';
import { confirmService } from '@/components/common';
import { FormModal, FormField } from '@/components/form';
import type { Folder } from '@/types';

const FoldersPage: React.FC = () => {
  const [data, setData] = useState<Folder[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editData, setEditData] = useState<Folder | undefined>();
  const [parentId, setParentId] = useState<number | undefined>();

  const fetchData = async () => {
    setLoading(true);
    try {
      const res = await folderService.getTree();
      setData(res.data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleAdd = (parent?: Folder) => {
    setParentId(parent?.id);
    setEditData(undefined);
    setModalOpen(true);
  };

  const handleEdit = (record: Folder) => {
    setEditData(record);
    setModalOpen(true);
  };

  const handleDelete = (record: Folder) => {
    confirmService.confirm({
      title: '确认删除',
      content: `确定要删除文件夹"${record.name}"吗？`,
      onOk: async () => {
        try {
          await folderService.delete(record.id);
          message.success('删除成功');
          fetchData();
        } catch {
          // error handled
        }
      }
    });
  };

  const handleSubmit = async (values: any) => {
    try {
      if (editData?.id) {
        await folderService.rename(editData.id, values.name);
        message.success('重命名成功');
      } else {
        await folderService.create({ name: values.name, parentId });
        message.success('创建成功');
      }
      setModalOpen(false);
      setEditData(undefined);
      fetchData();
    } catch {
      // error handled
    }
  };

  const fields: FormField[] = [
    { name: 'name', label: '文件夹名称', type: 'text', placeholder: '请输入文件夹名称', required: true }
  ];

  const renderTreeNodes = (nodes: Folder[]): any[] => {
    return nodes.map((node) => ({
      key: node.id,
      title: (
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
          <span>{node.name}</span>
          <Space size="small">
            <Button type="text" size="small" icon={<PlusOutlined />} onClick={(e) => { e.stopPropagation(); handleAdd(node); }} />
            <Button type="text" size="small" icon={<EditOutlined />} onClick={(e) => { e.stopPropagation(); handleEdit(node); }} />
            <Button type="text" size="small" danger icon={<DeleteOutlined />} onClick={(e) => { e.stopPropagation(); handleDelete(node); }} />
          </Space>
        </div>
      ),
      children: node.children ? renderTreeNodes(node.children) : []
    }));
  };

  const treeData = [{ key: 'root', title: '全部文件夹', children: renderTreeNodes(data) }];

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => handleAdd()}>新建根文件夹</Button>
      </div>
      {loading ? (
        <div>加载中...</div>
      ) : (
        <Tree showIcon defaultExpandAll treeData={treeData} />
      )}
      <FormModal title={editData?.id ? '编辑文件夹' : '新建文件夹'} open={modalOpen} onCancel={() => { setModalOpen(false); setEditData(undefined); }} onSubmit={handleSubmit} fields={fields} initialValues={editData} width={400} />
    </div>
  );
};

export default FoldersPage;