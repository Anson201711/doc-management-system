package com.example.workflow.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.workflow.entity.Workflow;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkflowRepository extends BaseMapper<Workflow> {
}