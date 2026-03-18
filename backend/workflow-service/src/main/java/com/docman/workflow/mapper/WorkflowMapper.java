package com.docman.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.docman.workflow.entity.Workflow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 工作流Mapper
 */
@Mapper
public interface WorkflowMapper extends BaseMapper<Workflow> {
    
    /**
     * 查询用户的待办工作流
     */
    @Select("SELECT * FROM workflows WHERE approver_id = #{approverId} AND current_status = 'pending' AND deleted = 0 ORDER BY created_at DESC")
    List<Workflow> findPendingByApproverId(@Param("approverId") Long approverId);
    
    /**
     * 查询用户创建的工作流
     */
    @Select("SELECT * FROM workflows WHERE creator_id = #{creatorId} AND deleted = 0 ORDER BY created_at DESC")
    List<Workflow> findByCreatorId(@Param("creatorId") Long creatorId);
    
    /**
     * 根据文档ID查询工作流
     */
    @Select("SELECT * FROM workflows WHERE document_id = #{documentId} AND deleted = 0 ORDER BY created_at DESC")
    List<Workflow> findByDocumentId(@Param("documentId") Long documentId);
}