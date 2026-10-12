package com.secondhand.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.secondhand.common.Result;
import com.secondhand.entity.Category;
import com.secondhand.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员：类别管理
 */
@RestController
@RequestMapping("/api/admin/category")
public class AdminCategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.ok(categoryMapper.selectList(new QueryWrapper<Category>().orderByAsc("sort")));
    }

    @PostMapping
    public Result<Category> add(@RequestBody Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return Result.error("分类名称不能为空");
        }
        category.setId(null);
        categoryMapper.insert(category);
        return Result.ok("新增成功", category);
    }

    @PutMapping("/{id}")
    public Result<Category> update(@PathVariable Long id, @RequestBody Category category) {
        Category db = categoryMapper.selectById(id);
        if (db == null) {
            return Result.error("分类不存在");
        }
        category.setId(id);
        categoryMapper.updateById(category);
        return Result.ok("修改成功", category);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryMapper.deleteById(id);
        return Result.ok("删除成功");
    }
}
