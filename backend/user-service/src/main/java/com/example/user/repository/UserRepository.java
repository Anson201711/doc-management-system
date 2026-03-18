package com.example.user.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserRepository extends BaseMapper<User> {
    
    @Select("SELECT * FROM t_user WHERE username = #{username}")
    User findByUsername(String username);
}