import React from 'react';
import { Layout, Menu } from 'antd';
import { FolderOutlined, FileTextOutlined, TeamOutlined, ShareAltOutlined, AuditOutlined, SettingOutlined, HomeOutlined } from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { useApp } from '@/context';

const { Sider } = Layout;

const Sidebar: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { sidebarCollapsed, setSidebarCollapsed } = useApp();

  const menuItems = [
    {
      key: '/',
      icon: <HomeOutlined />,
      label: '首页'
    },
    {
      key: '/documents',
      icon: <FileTextOutlined />,
      label: '我的文档'
    },
    {
      key: '/folders',
      icon: <FolderOutlined />,
      label: '文件夹'
    },
    {
      key: '/shared',
      icon: <ShareAltOutlined />,
      label: '共享文档'
    },
    {
      key: '/workflows',
      icon: <AuditOutlined />,
      label: '审批流程'
    },
    {
      key: '/team',
      icon: <TeamOutlined />,
      label: '团队管理'
    },
    { type: 'divider' as const },
    {
      key: '/settings',
      icon: <SettingOutlined />,
      label: '系统设置'
    }
  ];

  return (
    <Sider
      collapsible
      collapsed={sidebarCollapsed}
      onCollapse={setSidebarCollapsed}
      theme="light"
      style={{ borderRight: '1px solid #f0f0f0' }}
    >
      <div style={{ height: 64, display: 'flex', alignItems: 'center', justifyContent: 'center', borderBottom: '1px solid #f0f0f0' }}>
        {!sidebarCollapsed && <span style={{ fontSize: 18, fontWeight: 'bold', color: '#1677ff' }}>文档管理</span>}
      </div>
      <Menu
        mode="inline"
        selectedKeys={[location.pathname]}
        items={menuItems}
        onClick={({ key }) => navigate(key)}
        style={{ borderRight: 0 }}
      />
    </Sider>
  );
};

export default Sidebar;