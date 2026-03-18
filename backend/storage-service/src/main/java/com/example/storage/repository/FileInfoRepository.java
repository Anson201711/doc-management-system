package com.example.storage.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.storage.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * File Info Repository
 * MyBatis-Plus mapper for file information
 */
@Mapper
public interface FileInfoRepository extends BaseMapper<FileInfo> {
}