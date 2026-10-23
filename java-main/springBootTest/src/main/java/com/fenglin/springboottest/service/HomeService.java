package com.fenglin.springboottest.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fenglin.springboottest.dto.CategoryNode;
import com.fenglin.springboottest.entity.HomeItem;

import java.util.List;

/**
 * 首页服务：展示列表 / 搜索（支持分级分类）/ 分类树
 */
public interface HomeService extends IService<HomeItem> {

    /**
     * 分页搜索首页内容
     *
     * @param keyword    关键字（匹配 标题/简介/标签，可为空）
     * @param categoryId 分类 id（含其所有子分类）；为空或 0 表示不按分类过滤
     * @param page       页码（从 1 开始）
     * @param size       每页大小
     */
    IPage<HomeItem> searchPage(String keyword, Long categoryId, long page, long size);

    /**
     * 返回分级分类树（首页筛选用）
     */
    List<CategoryNode> categoryTree();
}
