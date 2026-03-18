package com.docman.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.docman.workflow.entity.WorkflowLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 工作流日志Mapper
 */
@Mapper
public interface WorkflowLogMapper extends BaseMapper<WorkflowLog> {
    
    /**
     * 查询工作流的审批记录
     */
    @Select("SELECT * FROM workflow_logs WHERE workflow_id = #{workflowId} AND deleted = 0 ORDER BY created_at DESC")
    List<WorkflowLog> findByWorkflowId(@Param("workflowId") Long workflowId);
}