package com.fenglin.springboottest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fenglin.springboottest.entity.GreetLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GreetLogMapper extends BaseMapper<GreetLog> {
}
