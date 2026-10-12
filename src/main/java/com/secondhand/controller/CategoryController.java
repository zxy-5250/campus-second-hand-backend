package com.secondhand.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.secondhand.common.Result;
import com.secondhand.entity.Category;
import com.secondhand.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类接口
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.ok(categoryMapper.selectList(new QueryWrapper<Category>().orderByAsc("sort")));
    }
}
