package com.fenglin.springboottest.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类树节点（用于首页分级分类筛选）
 */
public class CategoryNode {

    private Long id;

    private Long parentId;

    private String name;

    private Integer sort;

    private List<CategoryNode> children = new ArrayList<>();

    public CategoryNode() {
    }

    public CategoryNode(Long id, Long parentId, String name, Integer sort) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.sort = sort;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public List<CategoryNode> getChildren() {
        return children;
    }

    public void setChildren(List<CategoryNode> children) {
        this.children = children;
    }
}
