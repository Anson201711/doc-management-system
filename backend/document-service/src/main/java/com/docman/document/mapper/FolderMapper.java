package com.docman.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.docman.document.entity.Folder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件夹Mapper
 */
@Mapper
public interface FolderMapper extends BaseMapper<Folder> {
    
    /**
     * 根据父文件夹ID查询子文件夹
     */
    List<Folder> selectByParentId(@Param("parentId") Long parentId);
}