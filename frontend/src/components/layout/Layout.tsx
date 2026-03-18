import React from 'react';
import { Layout as AntLayout } from 'antd';
import Header from './Header';
import Sidebar from './Sidebar';

const { Content } = AntLayout;

interface LayoutProps {
  children?: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  return (
    <AntLayout style={{ minHeight: '100vh' }}>
      <Sidebar />
      <AntLayout>
        <Header />
        <Content style={{ margin: '24px 16px', padding: 24, background: '#fff', minHeight: 280 }}>
          {children}
        </Content>
      </AntLayout>
    </AntLayout>
  );
};

export default Layout;