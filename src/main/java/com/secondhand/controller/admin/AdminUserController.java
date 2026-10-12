package com.secondhand.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.common.Result;
import com.secondhand.entity.User;
import com.secondhand.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员：用户管理
 */
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    @Autowired
    private UserMapper userMapper;

    /** 用户列表（分页、关键词搜索、排除自己） */
    @GetMapping("/list")
    public Result<Page<User>> list(@RequestParam(required = false) String keyword,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   @RequestAttribute Long userId) {
        QueryWrapper<User> qw = new QueryWrapper<User>().ne("id", userId).orderByDesc("create_time");
        if (keyword != null && !keyword.trim().isEmpty()) {
            qw.and(w -> w.like("username", keyword.trim()).or().like("nickname", keyword.trim()));
        }
        Page<User> userPage = userMapper.selectPage(new Page<>(page, size), qw);
        for (User u : userPage.getRecords()) {
            u.setPassword(null);
        }
        return Result.ok(userPage);
    }

    /** 禁用/启用用户 */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody User body) {
        User u = userMapper.selectById(id);
        if (u == null) {
            return Result.error("用户不存在");
        }
        if (body.getStatus() == null) {
            return Result.error("参数错误");
        }
        u.setStatus(body.getStatus());
        userMapper.updateById(u);
        return Result.ok("操作成功");
    }

    /** 删除用户 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userMapper.deleteById(id);
        return Result.ok("删除成功");
    }
}
