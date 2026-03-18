import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Timeline, Button, Tag, Space, Empty, Modal, message } from 'antd';
import { ArrowLeftOutlined, RollbackOutlined, EyeOutlined } from '@ant-design/icons';
import { documentService, documentVersionService } from '@/services/document';
import type { DocumentVersion, Document } from '@/types';
import dayjs from 'dayjs';

const DocumentVersions: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [document, setDocument] = useState<Document | null>(null);
  const [versions, setVersions] = useState<DocumentVersion[]>([]);
  const [loading, setLoading] = useState(true);
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewContent, setPreviewContent] = useState('');

  useEffect(() => {
    loadData();
  }, [id]);

  const loadData = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const [docRes, versionRes] = await Promise.all([
        documentService.getById(Number(id)),
        documentVersionService.list(Number(id))
      ]);
      setDocument(docRes.data);
      setVersions(versionRes.data);
    } catch (error) {
      message.error('加载数据失败');
    } finally {
      setLoading(false);
    }
  };

  const handleRollback = (version: number) => {
    Modal.confirm({
      title: '确认回滚',
      content: `确定要回滚到版本 ${version} 吗？当前内容将被覆盖。`,
      onOk: async () => {
        try {
          await documentVersionService.rollback(Number(id), version);
          message.success('回滚成功');
          loadData();
        } catch {
          // error handled
        }
      }
    });
  };

  const handlePreview = (content: string) => {
    setPreviewContent(content || '');
    setPreviewVisible(true);
  };

  const getStatusColor = (version: DocumentVersion) => {
    // 如果是最新版本，显示绿色
    if (versions.length > 0 && version.version === versions[0].version) {
      return 'green';
    }
    return 'blue';
  };

  return (
    <div style={{ padding: 24 }}>
      <Card
        title={
          <Space>
            <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(-1)}>返回</Button>
            <span>版本历史 - {document?.title}</span>
          </Space>
        }
        loading={loading}
      >
        {versions.length === 0 ? (
          <Empty description="暂无版本历史" />
        ) : (
          <Timeline
            items={versions.map((v) => ({
              color: getStatusColor(v),
              label: (
                <div style={{ marginBottom: 8 }}>
                  <Tag color={getStatusColor(v)}>
                    {versions.length > 0 && v.version === versions[0].version ? '最新版本' : `v${v.version}`}
                  </Tag>
                  <span style={{ color: '#999', fontSize: 12 }}>
                    {dayjs(v.createdAt).format('YYYY-MM-DD HH:mm:ss')}
                  </span>
                </div>
              ),
              children: (
                <div style={{ padding: '8px 0' }}>
                  <div style={{ marginBottom: 8 }}>
                    <Space>
                      <span style={{ fontWeight: 500 }}>{v.creator?.username || '未知用户'}</span>
                      {v.changeSummary && (
                        <span style={{ color: '#666' }}> - {v.changeSummary}</span>
                      )}
                    </Space>
                  </div>
                  <Space>
                    <Button 
                      size="small" 
                      icon={<EyeOutlined />}
                      onClick={() => handlePreview(v.content || '')}
                    >
                      预览
                    </Button>
                    <Button 
                      size="small" 
                      icon={<RollbackOutlined />}
                      onClick={() => handleRollback(v.version)}
                    >
                      回滚到此版本
                    </Button>
                  </Space>
                </div>
              )
            }))}
          />
        )}
      </Card>

      <Modal
        title="版本预览"
        open={previewVisible}
        onCancel={() => setPreviewVisible(false)}
        footer={[
          <Button key="close" onClick={() => setPreviewVisible(false)}>
            关闭
          </Button>
        ]}
        width={800}
      >
        <div style={{ 
          maxHeight: 500, 
          overflow: 'auto', 
          padding: 16, 
          background: '#f9f9f9',
          whiteSpace: 'pre-wrap',
          fontFamily: 'monospace'
        }}>
          {previewContent || '（无内容）'}
        </div>
      </Modal>
    </div>
  );
};

export default DocumentVersions;