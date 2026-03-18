package com.example.workflow.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.workflow.entity.ApprovalTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApprovalTaskRepository extends BaseMapper<ApprovalTask> {
}