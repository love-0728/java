package com.fenglin.springboottest.controller;

import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.dto.CategoryNode;
import com.fenglin.springboottest.entity.HomeItem;
import com.fenglin.springboottest.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页接口：展示列表 + 搜索 + 分级分类
 * 公开接口（无需登录态），供前端首页调用
 */
@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private HomeService homeService;

    /**
     * 首页内容列表 / 搜索
     * GET /api/home/items?keyword=&categoryId=&page=1&size=100
     */
    @GetMapping("/items")
    public Result<Map<String, Object>> items(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "100") long size) {

        IPage<HomeItem> p = homeService.searchPage(keyword, categoryId, page, size);
        Map<String, Object> data = new HashMap<>();
        data.put("list", p.getRecords());
        data.put("total", p.getTotal());
        data.put("page", p.getCurrent());
        data.put("size", p.getSize());
        return Result.success("ok", data);
    }

    /**
     * 首页分级分类树（筛选用）
     * GET /api/home/categories
     */
    @GetMapping("/categories")
    public Result<List<CategoryNode>> categories() {
        return Result.success("ok", homeService.categoryTree());
    }
}
