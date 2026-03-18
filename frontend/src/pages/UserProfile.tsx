import React, { useState } from 'react';
import { Card, Form, Input, Button, Avatar, Upload, message, Tabs, Space, Modal, Divider } from 'antd';
import { UserOutlined, UploadOutlined, LockOutlined, SaveOutlined } from '@ant-design/icons';
import { useAuth } from '@/context';
import { authService } from '@/services/auth';
import type { User } from '@/types';

const UserProfile: React.FC = () => {
  const { user, refreshUser } = useAuth();
  const [profileLoading, setProfileLoading] = useState(false);
  const [passwordLoading, setPasswordLoading] = useState(false);
  const [profileForm] = Form.useForm();
  const [passwordForm] = Form.useForm();

  // 初始化表单数据
  React.useEffect(() => {
    if (user) {
      profileForm.setFieldsValue({
        username: user.username,
        email: user.email,
        fullName: user.fullName
      });
    }
  }, [user]);

  const handleProfileUpdate = async (values: any) => {
    setProfileLoading(true);
    try {
      await authService.updateProfile({
        fullName: values.fullName,
        email: values.email
      });
      message.success('个人信息更新成功');
      await refreshUser();
    } catch {
      // error handled
    } finally {
      setProfileLoading(false);
    }
  };

  const handlePasswordChange = async (values: any) => {
    if (values.newPassword !== values.confirmPassword) {
      message.error('两次输入的密码不一致');
      return;
    }
    setPasswordLoading(true);
    try {
      await authService.changePassword(values.oldPassword, values.newPassword);
      message.success('密码修改成功，请重新登录');
      passwordForm.resetFields();
      Modal.success({
        title: '密码已修改',
        content: '密码修改成功，请使用新密码重新登录',
        onOk: () => {
          // 触发登出
          window.location.href = '/login';
        }
      });
    } catch {
      // error handled
    } finally {
      setPasswordLoading(false);
    }
  };

  const tabItems = [
    {
      key: 'profile',
      label: '基本信息',
      children: (
        <Form
          form={profileForm}
          layout="vertical"
          onFinish={handleProfileUpdate}
          style={{ maxWidth: 400 }}
        >
          <div style={{ textAlign: 'center', marginBottom: 24 }}>
            <Avatar 
              size={80} 
              icon={<UserOutlined />} 
              src={user?.avatarUrl}
              style={{ marginBottom: 12 }}
            />
            <Upload showUploadList={false}>
              <Button icon={<UploadOutlined />}>更换头像</Button>
            </Upload>
          </div>
          
          <Form.Item name="username" label="用户名">
            <Input disabled />
          </Form.Item>
          
          <Form.Item name="fullName" label="姓名">
            <Input placeholder="请输入姓名" />
          </Form.Item>
          
          <Form.Item name="email" label="邮箱" rules={[
            { type: 'email', message: '请输入有效的邮箱地址' }
          ]}>
            <Input placeholder="请输入邮箱" />
          </Form.Item>
          
          <Form.Item>
            <Button 
              type="primary" 
              htmlType="submit" 
              icon={<SaveOutlined />}
              loading={profileLoading}
            >
              保存修改
            </Button>
          </Form.Item>
        </Form>
      )
    },
    {
      key: 'password',
      label: '修改密码',
      children: (
        <Form
          form={passwordForm}
          layout="vertical"
          onFinish={handlePasswordChange}
          style={{ maxWidth: 400 }}
        >
          <Form.Item 
            name="oldPassword" 
            label="当前密码"
            rules={[{ required: true, message: '请输入当前密码' }]}
          >
            <Input.Password placeholder="请输入当前密码" prefix={<LockOutlined />} />
          </Form.Item>
          
          <Form.Item 
            name="newPassword" 
            label="新密码"
            rules={[
              { required: true, message: '请输入新密码' },
              { min: 6, message: '密码至少6位' }
            ]}
          >
            <Input.Password placeholder="请输入新密码" prefix={<LockOutlined />} />
          </Form.Item>
          
          <Form.Item 
            name="confirmPassword" 
            label="确认新密码"
            rules={[
              { required: true, message: '请确认新密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('newPassword') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'));
                },
              }),
            ]}
          >
            <Input.Password placeholder="请再次输入新密码" prefix={<LockOutlined />} />
          </Form.Item>
          
          <Form.Item>
            <Button 
              type="primary" 
              htmlType="submit" 
              icon={<LockOutlined />}
              loading={passwordLoading}
            >
              修改密码
            </Button>
          </Form.Item>
        </Form>
      )
    },
    {
      key: 'preferences',
      label: '偏好设置',
      children: (
        <div style={{ maxWidth: 400 }}>
          <Form layout="vertical">
            <Form.Item label="语言">
              <Input value="简体中文" disabled />
            </Form.Item>
            
            <Form.Item label="时区">
              <Input value="Asia/Shanghai (GMT+8)" disabled />
            </Form.Item>
            
            <Form.Item label="主题">
              <Input value="浅色模式" disabled />
            </Form.Item>
          </Form>
        </div>
      )
    }
  ];

  return (
    <div style={{ padding: 24 }}>
      <Card title="个人中心">
        <Tabs items={tabItems} />
      </Card>
    </div>
  );
};

export default UserProfile;