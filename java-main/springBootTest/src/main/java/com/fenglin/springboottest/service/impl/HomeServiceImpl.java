package com.fenglin.springboottest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fenglin.springboottest.dto.CategoryNode;
import com.fenglin.springboottest.entity.HomeCategory;
import com.fenglin.springboottest.entity.HomeItem;
import com.fenglin.springboottest.mapper.HomeCategoryMapper;
import com.fenglin.springboottest.mapper.HomeItemMapper;
import com.fenglin.springboottest.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

@Service
public class HomeServiceImpl extends ServiceImpl<HomeItemMapper, HomeItem> implements HomeService {

    @Autowired
    private HomeCategoryMapper categoryMapper;

    @Override
    public IPage<HomeItem> searchPage(String keyword, Long categoryId, long page, long size) {
        Page<HomeItem> p = new Page<>(page, size);
        QueryWrapper<HomeItem> qw = new QueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            qw.and(w -> w.like("title", kw)
                    .or().like("description", kw)
                    .or().like("tags", kw));
        }

        if (categoryId != null && categoryId > 0) {
            List<Long> ids = descendantIds(categoryId);
            qw.in("category_id", ids);
        }

        qw.orderByAsc("sort").orderByDesc("id");
        return this.page(p, qw);
    }

    @Override
    public List<CategoryNode> categoryTree() {
        List<HomeCategory> all = categoryMapper.selectList(
                new QueryWrapper<HomeCategory>().orderByAsc("sort").orderByAsc("id"));

        Map<Long, CategoryNode> map = new HashMap<>();
        for (HomeCategory c : all) {
            map.put(c.getId(), new CategoryNode(c.getId(), c.getParentId(), c.getName(), c.getSort()));
        }

        List<CategoryNode> roots = new ArrayList<>();
        for (CategoryNode n : map.values()) {
            if (n.getParentId() == null || n.getParentId() == 0) {
                roots.add(n);
            } else {
                CategoryNode parent = map.get(n.getParentId());
                if (parent != null) {
                    parent.getChildren().add(n);
                } else {
                    roots.add(n);
                }
            }
        }
        return roots;
    }

    /**
     * 计算某个分类下的所有子孙分类 id（含自身），用于分级分类搜索
     */
    private List<Long> descendantIds(Long rootId) {
        List<HomeCategory> all = categoryMapper.selectList(null);
        List<Long> result = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        Queue<Long> queue = new LinkedList<>();
        queue.add(rootId);
        visited.add(rootId);

        while (!queue.isEmpty()) {
            Long cur = queue.poll();
            result.add(cur);
            for (HomeCategory c : all) {
                if (c.getParentId() != null && c.getParentId().equals(cur) && !visited.contains(c.getId())) {
                    visited.add(c.getId());
                    queue.add(c.getId());
                }
            }
        }
        return result;
    }
}
