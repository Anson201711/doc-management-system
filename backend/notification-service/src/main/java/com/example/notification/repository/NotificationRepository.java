package com.example.notification.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.notification.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationRepository extends BaseMapper<Notification> {
}