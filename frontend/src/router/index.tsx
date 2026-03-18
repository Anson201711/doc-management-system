import { createBrowserRouter, Navigate } from 'react-router-dom';
import { Layout } from '@/components/layout';
import HomePage from '@/pages/Home';
import LoginPage from '@/pages/Login';
import RegisterPage from '@/pages/Register';
import DocumentsPage from '@/pages/Documents';
import DocumentEditor from '@/pages/DocumentEditor';
import FoldersPage from '@/pages/Folders';
import DocumentVersions from '@/pages/DocumentVersions';
import PermissionManage from '@/pages/PermissionManage';
import DocumentComments from '@/pages/DocumentComments';
import WorkflowList from '@/pages/WorkflowList';
import WorkflowDetail from '@/pages/WorkflowDetail';
import UserProfile from '@/pages/UserProfile';
import { useAuth } from '@/context';

// 路由守卫组件
const ProtectedRoute = ({ children }: { children: React.ReactNode }) => {
  const { user, loading } = useAuth();
  
  if (loading) {
    return <div>加载中...</div>;
  }
  
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  
  return <>{children}</>;
};

const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />
  },
  {
    path: '/register',
    element: <RegisterPage />
  },
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <Layout />
      </ProtectedRoute>
    ),
    children: [
      {
        index: true,
        element: <HomePage />
      },
      {
        path: 'documents',
        element: <DocumentsPage />
      },
      {
        path: 'documents/new',
        element: <DocumentEditor />
      },
      {
        path: 'documents/:id/edit',
        element: <DocumentEditor />
      },
      {
        path: 'folders',
        element: <FoldersPage />
      },
      {
        path: 'documents/:id/versions',
        element: <DocumentVersions />
      },
      {
        path: 'documents/:id/permissions',
        element: <PermissionManage />
      },
      {
        path: 'documents/:id/comments',
        element: <DocumentComments />
      },
      {
        path: 'shared',
        element: <div>共享文档</div>
      },
      {
        path: 'workflows',
        element: <WorkflowList />
      },
      {
        path: 'workflows/:id',
        element: <WorkflowDetail />
      },
      {
        path: 'team',
        element: <div>团队管理</div>
      },
      {
        path: 'settings',
        element: <div>系统设置</div>
      },
      {
        path: 'profile',
        element: <UserProfile />
      },
      {
        path: 'search',
        element: <div>搜索结果</div>
      }
    ]
  }
]);

export default router;