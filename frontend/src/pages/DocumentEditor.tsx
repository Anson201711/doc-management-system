import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Button, Space, message, Dropdown, Tag, Modal, Input } from 'antd';
import { 
  SaveOutlined, 
  ArrowLeftOutlined, 
  HistoryOutlined, 
  ShareAltOutlined,
  CommentOutlined,
  MoreOutlined,
  FileTextOutlined,
  CheckCircleOutlined
} from '@ant-design/icons';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import MDEditor from '@uiw/react-md-editor';
import { documentService, documentVersionService } from '@/services/document';
import type { Document, DocumentVersion } from '@/types';
import dayjs from 'dayjs';

const { confirm } = Modal;

const DocumentEditor: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isNew = !id || id === 'new';

  const [document, setDocument] = useState<Partial<Document>>({
    title: '',
    content: '',
    documentType: 'markdown',
    status: 'draft'
  });
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  // const [hasChanges, setHasChanges] = useState(false);
  const [editorMode, setEditorMode] = useState<'rich' | 'markdown'>('markdown');
  const [versions, setVersions] = useState<DocumentVersion[]>([]);
  const [showVersions, setShowVersions] = useState(false);
  // const [showPermissions, setShowPermissions] = useState(false);
  // const [showComments, setShowComments] = useState(false);

  // 加载文档
  useEffect(() => {
    if (!isNew && id) {
      loadDocument();
    }
  }, [id]);

  const loadDocument = async () => {
    setLoading(true);
    try {
      const res = await documentService.getById(Number(id));
      setDocument(res.data);
    } catch (error) {
      message.error('加载文档失败');
      navigate('/documents');
    } finally {
      setLoading(false);
    }
  };

  // 保存文档
  const handleSave = async () => {
    if (!document.title?.trim()) {
      message.error('请输入文档标题');
      return;
    }

    setSaving(true);
    try {
      if (isNew) {
        const res = await documentService.create(document);
        message.success('创建成功');
        navigate(`/documents/${res.data.id}/edit`, { replace: true });
      } else {
        await documentService.update(Number(id), document);
        message.success('保存成功');
      }
    } catch (error) {
      // error handled in api
    } finally {
      setSaving(false);
    }
  };

  // 内容变化
  const handleContentChange = useCallback((value: string) => {
    setDocument(prev => ({ ...prev, content: value }));
  }, []);

  // 标题变化
  const handleTitleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setDocument(prev => ({ ...prev, title: e.target.value }));
  };

  // 发布文档
  const handlePublish = async () => {
    confirm({
      title: '确认发布',
      content: '发布后文档将对有权限的用户可见，是否继续？',
      onOk: async () => {
        try {
          await documentService.update(Number(id), { ...document, status: 'published' });
          setDocument(prev => ({ ...prev, status: 'published' }));
          message.success('发布成功');
        } catch {
          // error handled
        }
      }
    });
  };

  // 查看版本历史
  const handleViewVersions = async () => {
    try {
      const res = await documentVersionService.list(Number(id));
      setVersions(res.data);
      setShowVersions(true);
    } catch {
      message.error('加载版本历史失败');
    }
  };

  // 回滚版本
  const handleRollback = async (version: number) => {
    confirm({
      title: '确认回滚',
      content: `确定要回滚到版本 ${version} 吗？`,
      onOk: async () => {
        try {
          await documentVersionService.rollback(Number(id), version);
          message.success('回滚成功');
          loadDocument();
          setShowVersions(false);
        } catch {
          // error handled
        }
      }
    });
  };

  // 更多菜单
  const menuItems = [
    { key: 'versions', icon: <HistoryOutlined />, label: '版本历史', onClick: handleViewVersions },
  ];

  // 工具栏配置
  const quillModules = {
    toolbar: [
      [{ header: [1, 2, 3, false] }],
      ['bold', 'italic', 'underline', 'strike'],
      [{ list: 'ordered' }, { list: 'bullet' }],
      ['link', 'image'],
      ['clean']
    ]
  };

  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* 顶部工具栏 */}
      <div style={{ 
        padding: '12px 24px', 
        borderBottom: '1px solid #f0f0f0', 
        display: 'flex', 
        justifyContent: 'space-between',
        alignItems: 'center',
        background: '#fff'
      }}>
        <Space>
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/documents')}>
            返回
          </Button>
          <Input
            value={document.title}
            onChange={handleTitleChange}
            placeholder="请输入文档标题"
            style={{ width: 300, fontSize: 16, fontWeight: 500 }}
            variant="borderless"
          />
          {document.status && (
            <Tag color={document.status === 'published' ? 'green' : 'default'}>
              {document.status === 'published' ? <CheckCircleOutlined /> : null} {document.status === 'published' ? '已发布' : '草稿'}
            </Tag>
          )}
        </Space>

        <Space>
          <Button 
            type={editorMode === 'markdown' ? 'primary' : 'default'}
            onClick={() => setEditorMode('markdown')}
          >
            Markdown
          </Button>
          <Button 
            type={editorMode === 'rich' ? 'primary' : 'default'}
            onClick={() => setEditorMode('rich')}
          >
            富文本
          </Button>
          <Button 
            icon={<HistoryOutlined />} 
            onClick={handleViewVersions}
            disabled={isNew}
          >
            历史
          </Button>
          <Dropdown menu={{ items: menuItems }} disabled={isNew}>
            <Button icon={<MoreOutlined />}>更多</Button>
          </Dropdown>
          <Button 
            type="primary" 
            icon={<SaveOutlined />} 
            loading={saving}
            onClick={handleSave}
          >
            保存
          </Button>
          {!isNew && document.status !== 'published' && (
            <Button icon={<FileTextOutlined />} onClick={handlePublish}>
              发布
            </Button>
          )}
        </Space>
      </div>

      {/* 编辑器区域 */}
      <div style={{ flex: 1, overflow: 'auto', padding: 24 }}>
        {loading ? (
          <div style={{ textAlign: 'center', padding: 100 }}>加载中...</div>
        ) : editorMode === 'markdown' ? (
          <div data-color-mode="light">
            <MDEditor
              value={document.content || ''}
              onChange={(val) => handleContentChange(val || '')}
              height="100%"
              preview="edit"
            />
          </div>
        ) : (
          <ReactQuill
            theme="snow"
            value={document.content || ''}
            onChange={handleContentChange}
            modules={quillModules}
            style={{ height: 'calc(100% - 44px)' }}
          />
        )}
      </div>

      {/* 版本历史弹窗 */}
      <Modal
        title="版本历史"
        open={showVersions}
        onCancel={() => setShowVersions(false)}
        footer={null}
        width={600}
      >
        <div style={{ maxHeight: 400, overflow: 'auto' }}>
          {versions.length === 0 ? (
            <div style={{ textAlign: 'center', padding: 40, color: '#999' }}>暂无版本历史</div>
          ) : (
            versions.map((v) => (
              <div key={v.id} style={{ 
                padding: 12, 
                borderBottom: '1px solid #f0f0f0',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center'
              }}>
                <div>
                  <Space>
                    <Tag color="blue">v{v.version}</Tag>
                    <span style={{ color: '#666' }}>
                      {v.creator?.username || '未知用户'}
                    </span>
                  </Space>
                  <div style={{ fontSize: 12, color: '#999', marginTop: 4 }}>
                    {dayjs(v.createdAt).format('YYYY-MM-DD HH:mm:ss')}
                  </div>
                  {v.changeSummary && (
                    <div style={{ fontSize: 12, color: '#666', marginTop: 4 }}>
                      {v.changeSummary}
                    </div>
                  )}
                </div>
                <Button size="small" onClick={() => handleRollback(v.version)}>
                  回滚
                </Button>
              </div>
            ))
          )}
        </div>
      </Modal>
    </div>
  );
};

export default DocumentEditor;