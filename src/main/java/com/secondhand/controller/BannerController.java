package com.secondhand.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.secondhand.common.Result;
import com.secondhand.entity.Banner;
import com.secondhand.mapper.BannerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 轮播图接口（前台展示启用中的轮播图）
 */
@RestController
@RequestMapping("/api/banner")
public class BannerController {

    @Autowired
    private BannerMapper bannerMapper;

    @GetMapping("/list")
    public Result<List<Banner>> list() {
        return Result.ok(bannerMapper.selectList(new QueryWrapper<Banner>()
                .eq("status", 1)
                .orderByAsc("sort")));
    }
}
