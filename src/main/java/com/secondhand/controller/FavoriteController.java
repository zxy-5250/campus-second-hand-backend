package com.secondhand.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.secondhand.common.Result;
import com.secondhand.dto.ProductVO;
import com.secondhand.entity.Favorite;
import com.secondhand.entity.Product;
import com.secondhand.mapper.FavoriteMapper;
import com.secondhand.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 收藏接口
 */
@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private ProductMapper productMapper;

    /** 我的收藏列表（带商品信息） */
    @GetMapping("/list")
    public Result<List<ProductVO>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Favorite> favs = favoriteMapper.selectList(new QueryWrapper<Favorite>()
                .eq("user_id", userId)
                .orderByDesc("create_time"));
        List<ProductVO> vos = new ArrayList<>();
        for (Favorite f : favs) {
            Product p = productMapper.selectById(f.getProductId());
            if (p != null) {
                ProductVO vo = new ProductVO();
                vo.setId(p.getId());
                vo.setUserId(p.getUserId());
                vo.setTitle(p.getTitle());
                vo.setPrice(p.getPrice());
                vo.setLocation(p.getLocation());
                vo.setImages(p.getImages());
                vo.setStatus(p.getStatus());
                vo.setCreateTime(p.getCreateTime());
                vos.add(vo);
            }
        }
        return Result.ok(vos);
    }

    /** 添加收藏 */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Map<String, Long> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long productId = body.get("productId");
        if (productId == null) {
            return Result.error("参数错误");
        }
        Long count = favoriteMapper.selectCount(new QueryWrapper<Favorite>()
                .eq("user_id", userId).eq("product_id", productId));
        if (count > 0) {
            return Result.error("已收藏过该商品");
        }
        Favorite f = new Favorite();
        f.setUserId(userId);
        f.setProductId(productId);
        favoriteMapper.insert(f);
        return Result.ok("收藏成功");
    }

    /** 取消收藏 */
    @DeleteMapping("/{productId}")
    public Result<Void> remove(@PathVariable Long productId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        favoriteMapper.delete(new QueryWrapper<Favorite>()
                .eq("user_id", userId).eq("product_id", productId));
        return Result.ok();
    }

    /** 判断是否已收藏 */
    @GetMapping("/check/{productId}")
    public Result<Boolean> check(@PathVariable Long productId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long count = favoriteMapper.selectCount(new QueryWrapper<Favorite>()
                .eq("user_id", userId).eq("product_id", productId));
        return Result.ok(count > 0);
    }
}
