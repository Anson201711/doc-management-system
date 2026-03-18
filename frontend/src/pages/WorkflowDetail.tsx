import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Steps, Button, Space, Input, message, Tag, Timeline, Divider } from 'antd';
import { ArrowLeftOutlined, CheckCircleOutlined, CloseCircleOutlined, SyncOutlined } from '@ant-design/icons';
import { workflowService } from '@/services/workflow';
import type { Workflow, WorkflowLog } from '@/types';
import { useAuth } from '@/context';
import dayjs from 'dayjs';

const { TextArea } = Input;

const WorkflowDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [workflow, setWorkflow] = useState<Workflow | null>(null);
  const [logs, setLogs] = useState<WorkflowLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [comment, setComment] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    loadData();
  }, [id]);

  const loadData = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const [workflowRes, logsRes] = await Promise.all([
        workflowService.getById(Number(id)),
        workflowService.getLogs(Number(id))
      ]);
      setWorkflow(workflowRes.data);
      setLogs(logsRes.data || []);
    } catch (error) {
      message.error('加载数据失败');
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async () => {
    setSubmitting(true);
    try {
      await workflowService.approve(Number(id), comment);
      message.success('审批通过');
      loadData();
      setComment('');
    } catch {
      // error handled
    } finally {
      setSubmitting(false);
    }
  };

  const handleReject = async () => {
    if (!comment.trim()) {
      message.error('请输入拒绝原因');
      return;
    }
    setSubmitting(true);
    try {
      await workflowService.reject(Number(id), comment);
      message.success('已拒绝');
      loadData();
      setComment('');
    } catch {
      // error handled
    } finally {
      setSubmitting(false);
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

  // 判断当前用户是否是审批人
  const isApprover = workflow?.currentApproverId === user?.id;

  const currentStep = workflow?.currentStatus === 'pending' ? 1 : 
    workflow?.currentStatus === 'approved' ? 2 : 0;

  return (
    <div style={{ padding: 24 }}>
      <Card
        title={
          <Space>
            <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/workflows')}>返回</Button>
            <span>审批详情</span>
            <Tag color={getStatusColor(workflow?.currentStatus || '')}>
              {getStatusLabel(workflow?.currentStatus || '')}
            </Tag>
          </Space>
        }
        loading={loading}
      >
        {/* 基本信息 */}
        <div style={{ marginBottom: 24 }}>
          <h3>流程信息</h3>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
            <div>
              <span style={{ color: '#999' }}>流程标题：</span>
              <span>{workflow?.title}</span>
            </div>
            <div>
              <span style={{ color: '#999' }}>申请人：</span>
              <span>{workflow?.creator?.username}</span>
            </div>
            <div>
              <span style={{ color: '#999' }}>关联文档：</span>
              <a onClick={() => navigate(`/documents/${workflow?.documentId}/edit`)}>
                {workflow?.document?.title}
              </a>
            </div>
            <div>
              <span style={{ color: '#999' }}>创建时间：</span>
              <span>{dayjs(workflow?.createdAt).format('YYYY-MM-DD HH:mm:ss')}</span>
            </div>
          </div>
        </div>

        {/* 审批流程步骤 */}
        <div style={{ marginBottom: 24 }}>
          <h3>审批进度</h3>
          <Steps
            current={currentStep}
            items={[
              { title: '提交申请' },
              { title: '审批中', status: workflow?.currentStatus === 'pending' ? 'process' : 'wait' },
              { title: '完成' }
            ]}
          />
        </div>

        <Divider />

        {/* 审批操作（仅当前审批人可见） */}
        {isApprover && workflow?.currentStatus === 'pending' && (
          <div style={{ marginBottom: 24 }}>
            <h3>审批操作</h3>
            <TextArea
              placeholder="请输入审批意见（拒绝时必填）"
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              rows={3}
              style={{ marginBottom: 12, width: '100%' }}
            />
            <Space>
              <Button 
                type="primary" 
                icon={<CheckCircleOutlined />}
                loading={submitting}
                onClick={handleApprove}
              >
                通过
              </Button>
              <Button 
                danger 
                icon={<CloseCircleOutlined />}
                loading={submitting}
                onClick={handleReject}
              >
                拒绝
              </Button>
            </Space>
          </div>
        )}

        {/* 审批历史 */}
        <div>
          <h3>审批记录</h3>
          <Timeline
            items={logs.map((log) => ({
              color: log.action === 'approve' ? 'green' : 'red',
              children: (
                <div>
                  <Space>
                    <span style={{ fontWeight: 500 }}>{log.approver?.username}</span>
                    <Tag color={log.action === 'approve' ? 'green' : 'red'}>
                      {log.action === 'approve' ? '通过' : '拒绝'}
                    </Tag>
                  </Space>
                  <div style={{ color: '#666', marginTop: 4 }}>
                    {log.comment && <div>审批意见：{log.comment}</div>}
                    <div style={{ fontSize: 12, color: '#999' }}>
                      {dayjs(log.createdAt).format('YYYY-MM-DD HH:mm:ss')}
                    </div>
                  </div>
                </div>
              )
            }))}
          />
        </div>
      </Card>
    </div>
  );
};

export default WorkflowDetail;