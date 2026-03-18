import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, List, Button, Input, Avatar, Space, message, Popconfirm, Empty } from 'antd';
import { ArrowLeftOutlined, UserOutlined, DeleteOutlined, SendOutlined } from '@ant-design/icons';
import { documentService } from '@/services/document';
import { commentService } from '@/services/comment';
import type { Comment, Document } from '@/types';
import { useAuth } from '@/context';
import dayjs from 'dayjs';

const { TextArea } = Input;

const DocumentComments: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [document, setDocument] = useState<Document | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [loading, setLoading] = useState(true);
  const [newComment, setNewComment] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [replyingTo, setReplyingTo] = useState<number | null>(null);
  const [replyContent, setReplyContent] = useState('');

  useEffect(() => {
    loadData();
  }, [id]);

  const loadData = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const [docRes, commentRes] = await Promise.all([
        documentService.getById(Number(id)),
        commentService.list(Number(id))
      ]);
      setDocument(docRes.data);
      setComments(commentRes.data);
    } catch (error) {
      message.error('加载数据失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmitComment = async () => {
    if (!newComment.trim()) return;
    setSubmitting(true);
    try {
      await commentService.create(Number(id), { content: newComment });
      setNewComment('');
      message.success('评论成功');
      loadData();
    } catch {
      // error handled
    } finally {
      setSubmitting(false);
    }
  };

  const handleReply = async (parentId: number) => {
    if (!replyContent.trim()) return;
    setSubmitting(true);
    try {
      await commentService.create(Number(id), { 
        content: replyContent, 
        parentId 
      });
      setReplyContent('');
      setReplyingTo(null);
      message.success('回复成功');
      loadData();
    } catch {
      // error handled
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (commentId: number) => {
    try {
      await commentService.delete(commentId);
      message.success('删除成功');
      loadData();
    } catch {
      // error handled
    }
  };

  // 渲染评论项
  const renderCommentItem = (comment: Comment, isReply = false) => (
    <List.Item
      key={comment.id}
      actions={!isReply && comment.userId === user?.id ? [
        <Button 
          type="link" 
          size="small" 
          onClick={() => setReplyingTo(comment.id)}
        >
          回复
        </Button>,
        <Popconfirm
          title="确认删除"
          description="确定要删除这条评论吗？"
          onConfirm={() => handleDelete(comment.id)}
        >
          <Button type="link" size="small" danger icon={<DeleteOutlined />}>
            删除
          </Button>
        </Popconfirm>
      ] : !isReply ? [
        <Button 
          type="link" 
          size="small" 
          onClick={() => setReplyingTo(comment.id)}
        >
          回复
        </Button>
      ] : undefined}
    >
      <List.Item.Meta
        avatar={<Avatar icon={<UserOutlined />} src={comment.user?.avatarUrl} />}
        title={
          <Space>
            <span style={{ fontWeight: 500 }}>{comment.user?.username || '未知用户'}</span>
            <span style={{ color: '#999', fontSize: 12 }}>
              {dayjs(comment.createdAt).format('YYYY-MM-DD HH:mm')}
            </span>
          </Space>
        }
        description={
          <div>
            <div style={{ marginTop: 8 }}>{comment.content}</div>
            
            {/* 回复输入框 */}
            {replyingTo === comment.id && (
              <div style={{ marginTop: 12, padding: 12, background: '#f5f5f5', borderRadius: 4 }}>
                <TextArea
                  placeholder="请输入回复内容"
                  value={replyContent}
                  onChange={(e) => setReplyContent(e.target.value)}
                  rows={2}
                  style={{ marginBottom: 8 }}
                />
                <Space>
                  <Button 
                    type="primary" 
                    size="small" 
                    loading={submitting}
                    onClick={() => handleReply(comment.id)}
                  >
                    提交回复
                  </Button>
                  <Button size="small" onClick={() => { setReplyingTo(null); setReplyContent(''); }}>
                    取消
                  </Button>
                </Space>
              </div>
            )}
          </div>
        }
      />
    </List.Item>
  );

  // 构建评论树（平铺的评论+回复）
  // const flatComments = comments.reduce<Comment[]>((acc, comment) => {
  //   acc.push(comment);
  //   if (comment.replies && comment.replies.length > 0) {
  //     acc.push(...comment.replies);
  //   }
  //   return acc;
  // }, []);

  return (
    <div style={{ padding: 24 }}>
      <Card
        title={
          <Space>
            <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(-1)}>返回</Button>
            <span>评论 - {document?.title}</span>
          </Space>
        }
        loading={loading}
      >
        {/* 发表评论 */}
        <div style={{ marginBottom: 24 }}>
          <TextArea
            placeholder="写下你的评论..."
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            rows={3}
            style={{ marginBottom: 12 }}
          />
          <Button 
            type="primary" 
            icon={<SendOutlined />}
            loading={submitting}
            onClick={handleSubmitComment}
          >
            发表评论
          </Button>
        </div>

        {/* 评论列表 */}
        {comments.length === 0 ? (
          <Empty description="暂无评论，快来抢沙发吧~" />
        ) : (
          <List
            dataSource={comments}
            renderItem={(comment) => renderCommentItem(comment)}
            locale={{ emptyText: '暂无评论' }}
          />
        )}
      </Card>
    </div>
  );
};

export default DocumentComments;