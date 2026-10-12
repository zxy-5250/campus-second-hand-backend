package com.secondhand.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.secondhand.common.Result;
import com.secondhand.entity.Banner;
import com.secondhand.mapper.BannerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员：轮播图管理
 */
@RestController
@RequestMapping("/api/admin/banner")
public class AdminBannerController {

    @Autowired
    private BannerMapper bannerMapper;

    @GetMapping("/list")
    public Result<List<Banner>> list() {
        return Result.ok(bannerMapper.selectList(new QueryWrapper<Banner>().orderByAsc("sort")));
    }

    @PostMapping
    public Result<Banner> add(@RequestBody Banner banner) {
        if (banner.getImage() == null || banner.getImage().trim().isEmpty()) {
            return Result.error("请上传图片");
        }
        banner.setId(null);
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        bannerMapper.insert(banner);
        return Result.ok("新增成功", banner);
    }

    @PutMapping("/{id}")
    public Result<Banner> update(@PathVariable Long id, @RequestBody Banner banner) {
        Banner db = bannerMapper.selectById(id);
        if (db == null) {
            return Result.error("轮播图不存在");
        }
        banner.setId(id);
        bannerMapper.updateById(banner);
        return Result.ok("修改成功", banner);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bannerMapper.deleteById(id);
        return Result.ok("删除成功");
    }
}
