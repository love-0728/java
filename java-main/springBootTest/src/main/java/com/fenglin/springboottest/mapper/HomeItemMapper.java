package com.fenglin.springboottest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fenglin.springboottest.entity.HomeItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HomeItemMapper extends BaseMapper<HomeItem> {
}
