import React from 'react';
import { Layout, Dropdown, Avatar, Space, Input, Badge } from 'antd';
import { UserOutlined, BellOutlined, SearchOutlined, SettingOutlined, LogoutOutlined } from '@ant-design/icons';
import { useAuth } from '@/context';
import { useNavigate } from 'react-router-dom';
import { authService } from '@/services/auth';
import { message } from 'antd';

const { Header: AntHeader } = Layout;

const Header: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await authService.logout();
    } catch {
      // ignore error
    }
    logout();
    message.success('已退出登录');
    navigate('/login');
  };

  const userMenu = {
    items: [
      {
        key: 'profile',
        icon: <UserOutlined />,
        label: '个人中心'
      },
      {
        key: 'settings',
        icon: <SettingOutlined />,
        label: '设置'
      },
      { type: 'divider' as const },
      {
        key: 'logout',
        icon: <LogoutOutlined />,
        label: '退出登录',
        danger: true
      }
    ],
    onClick: ({ key }: { key: string }) => {
      switch (key) {
        case 'profile':
          navigate('/profile');
          break;
        case 'settings':
          navigate('/settings');
          break;
        case 'logout':
          handleLogout();
          break;
      }
    }
  };

  return (
    <AntHeader style={{ padding: '0 24px', background: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
      <Input
        placeholder="搜索文档..."
        prefix={<SearchOutlined />}
        style={{ width: 300, borderRadius: 4 }}
        onPressEnter={(e) => {
          const value = (e.target as HTMLInputElement).value;
          if (value) {
            navigate(`/search?q=${value}`);
          }
        }}
      />
      <Space size="large">
        <Badge count={5} size="small">
          <BellOutlined style={{ fontSize: 18, cursor: 'pointer' }} />
        </Badge>
        <Dropdown menu={userMenu} placement="bottomRight">
          <Space style={{ cursor: 'pointer' }}>
            <Avatar src={user?.avatarUrl} icon={!user?.avatarUrl && <UserOutlined />} />
            <span>{user?.fullName || user?.username}</span>
          </Space>
        </Dropdown>
      </Space>
    </AntHeader>
  );
};

export default Header;