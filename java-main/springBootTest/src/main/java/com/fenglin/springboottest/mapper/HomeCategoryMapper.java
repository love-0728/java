package com.fenglin.springboottest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fenglin.springboottest.entity.HomeCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HomeCategoryMapper extends BaseMapper<HomeCategory> {
}
