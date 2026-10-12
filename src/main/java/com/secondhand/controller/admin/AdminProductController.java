package com.secondhand.controller.admin;

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
 * 管理员：物品管理
 */
@RestController
@RequestMapping("/api/admin/product")
public class AdminProductController {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private UserMapper userMapper;

    /** 全部商品列表（可分页、关键词搜索） */
    @GetMapping("/list")
    public Result<Page<ProductVO>> list(@RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) String status,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size) {
        QueryWrapper<Product> qw = new QueryWrapper<Product>().orderByDesc("create_time");
        if (keyword != null && !keyword.trim().isEmpty()) {
            qw.like("title", keyword.trim());
        }
        if (status != null && !status.trim().isEmpty()) {
            qw.eq("status", status);
        }
        Page<Product> productPage = productMapper.selectPage(new Page<>(page, size), qw);
        Page<ProductVO> voPage = new Page<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());
        List<ProductVO> vos = new ArrayList<>();
        for (Product p : productPage.getRecords()) {
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
                vo.setNickname(u.getNickname());
                vo.setUsername(u.getUsername());
            }
            vos.add(vo);
        }
        voPage.setRecords(vos);
        return Result.ok(voPage);
    }

    /** 商品详情（弹窗） */
    @GetMapping("/detail/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        Product p = productMapper.selectById(id);
        if (p == null) {
            return Result.error("商品不存在");
        }
        return Result.ok(p);
    }

    /** 下架违规商品 */
    @PostMapping("/{id}/takedown")
    public Result<Void> takedown(@PathVariable Long id) {
        Product p = productMapper.selectById(id);
        if (p == null) {
            return Result.error("商品不存在");
        }
        p.setStatus("已下架");
        productMapper.updateById(p);
        return Result.ok("已下架");
    }

    /** 恢复上架 */
    @PostMapping("/{id}/onsale")
    public Result<Void> onsale(@PathVariable Long id) {
        Product p = productMapper.selectById(id);
        if (p == null) {
            return Result.error("商品不存在");
        }
        p.setStatus("在售");
        productMapper.updateById(p);
        return Result.ok("已上架");
    }
}
