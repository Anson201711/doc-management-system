import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Row, Col, Statistic } from 'antd';
import { FileTextOutlined, FolderOutlined, TeamOutlined, ClockCircleOutlined } from '@ant-design/icons';

const HomePage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div>
      <h1 style={{ marginBottom: 24 }}>欢迎使用文档管理系统</h1>
      <Row gutter={16}>
        <Col span={6}>
          <Card hoverable onClick={() => navigate('/documents')}>
            <Statistic title="我的文档" value={12} prefix={<FileTextOutlined />} valueStyle={{ color: '#1677ff' }} />
          </Card>
        </Col>
        <Col span={6}>
          <Card hoverable onClick={() => navigate('/folders')}>
            <Statistic title="文件夹" value={5} prefix={<FolderOutlined />} valueStyle={{ color: '#52c41a' }} />
          </Card>
        </Col>
        <Col span={6}>
          <Card hoverable onClick={() => navigate('/shared')}>
            <Statistic title="共享文档" value={8} prefix={<TeamOutlined />} valueStyle={{ color: '#faad14' }} />
          </Card>
        </Col>
        <Col span={6}>
          <Card hoverable onClick={() => navigate('/workflows')}>
            <Statistic title="待审批" value={3} prefix={<ClockCircleOutlined />} valueStyle={{ color: '#f5222d' }} />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default HomePage;