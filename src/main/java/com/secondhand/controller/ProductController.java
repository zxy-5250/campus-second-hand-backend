package com.secondhand.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.common.Result;
import com.secondhand.dto.ProductVO;
import com.secondhand.entity.Category;
import com.secondhand.entity.Product;
import com.secondhand.entity.User;
import com.secondhand.mapper.CategoryMapper;
import com.secondhand.mapper.ProductMapper;
import com.secondhand.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品接口：浏览 / 详情 / 发布 / 我的物品 / 编辑 / 下架
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private UserMapper userMapper;

    /** 前台分页浏览（可按分类、关键词搜索），仅展示在售 */
    @GetMapping("/list")
    public Result<Page<ProductVO>> list(@RequestParam(required = false) Long categoryId,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "12") Integer size) {
        QueryWrapper<Product> qw = new QueryWrapper<Product>()
                .eq("status", "在售")
                .orderByDesc("create_time");
        if (categoryId != null) {
            qw.eq("category_id", categoryId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            qw.like("title", keyword.trim());
        }
        Page<Product> productPage = productMapper.selectPage(new Page<>(page, size), qw);
        return Result.ok(toVOPage(productPage));
    }

    /** 商品详情，浏览量 +1 */
    @GetMapping("/detail/{id}")
    public Result<ProductVO> detail(@PathVariable Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        product.setViewCount((product.getViewCount() == null ? 0 : product.getViewCount()) + 1);
        productMapper.updateById(product);
        return Result.ok(toVO(product));
    }

    /** 发布商品 */
    @PostMapping("/publish")
    public Result<Product> publish(@RequestBody Product product, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (product.getTitle() == null || product.getTitle().trim().isEmpty()) {
            return Result.error("商品标题不能为空");
        }
        if (product.getPrice() == null) {
            return Result.error("请填写商品价格");
        }
        product.setId(null);
        product.setUserId(userId);
        product.setStatus("在售");
        product.setViewCount(0);
        productMapper.insert(product);
        return Result.ok("发布成功", product);
    }

    /** 我的物品（我发布的商品） */
    @GetMapping("/mine")
    public Result<List<ProductVO>> mine(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Product> list = productMapper.selectList(new QueryWrapper<Product>()
                .eq("user_id", userId)
                .orderByDesc("create_time"));
        List<ProductVO> vos = new ArrayList<>();
        for (Product p : list) {
            vos.add(toVO(p));
        }
        return Result.ok(vos);
    }

    /** 编辑商品（仅本人） */
    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Long id, @RequestBody Product product, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Product db = productMapper.selectById(id);
        if (db == null) {
            return Result.error("商品不存在");
        }
        if (!db.getUserId().equals(userId)) {
            return Result.error("只能修改自己发布的商品");
        }
        product.setId(id);
        product.setUserId(userId);   // 保持归属不变
        product.setViewCount(db.getViewCount());
        product.setStatus(db.getStatus());
        productMapper.updateById(product);
        return Result.ok("修改成功", product);
    }

    /** 下架商品（本人或管理员） */
    @PostMapping("/{id}/takedown")
    public Result<Void> takedown(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        Product db = productMapper.selectById(id);
        if (db == null) {
            return Result.error("商品不存在");
        }
        if (!"admin".equals(role) && !db.getUserId().equals(userId)) {
            return Result.error("无权限操作该商品");
        }
        db.setStatus("已下架");
        productMapper.updateById(db);
        return Result.ok();
    }

    /** 重新上架 */
    @PostMapping("/{id}/onsale")
    public Result<Void> onsale(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Product db = productMapper.selectById(id);
        if (db == null) {
            return Result.error("商品不存在");
        }
        if (!db.getUserId().equals(userId)) {
            return Result.error("无权限操作该商品");
        }
        db.setStatus("在售");
        productMapper.updateById(db);
        return Result.ok();
    }

    /** ============ 组装 VO ============ */

    private Page<ProductVO> toVOPage(Page<Product> productPage) {
        Page<ProductVO> voPage = new Page<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());
        List<ProductVO> vos = new ArrayList<>();
        for (Product p : productPage.getRecords()) {
            vos.add(toVO(p));
        }
        voPage.setRecords(vos);
        return voPage;
    }

    private ProductVO toVO(Product p) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(p, vo);
        if (p.getCategoryId() != null) {
            Category c = categoryMapper.selectById(p.getCategoryId());
            if (c != null) {
                vo.setCategoryName(c.getName());
            }
        }
        User u = userMapper.selectById(p.getUserId());
        if (u != null) {
            vo.setUsername(u.getUsername());
            vo.setNickname(u.getNickname());
        }
        return vo;
    }
}
