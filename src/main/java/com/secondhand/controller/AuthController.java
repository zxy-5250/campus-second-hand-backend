package com.secondhand.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.secondhand.common.JwtUtil;
import com.secondhand.common.Result;
import com.secondhand.dto.LoginDTO;
import com.secondhand.entity.User;
import com.secondhand.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证接口：登录 / 注册 / 当前用户信息
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    /** 登录 */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto) {
        if (dto.getUsername() == null || dto.getPassword() == null) {
            return Result.error("用户名或密码不能为空");
        }
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", dto.getUsername()));
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!user.getPassword().equals(dto.getPassword())) {
            return Result.error("密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            return Result.error("账号已被禁用，请联系管理员");
        }
        String token = JwtUtil.createToken(user.getId(), user.getRole());
        user.setPassword(null);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        return Result.ok("登录成功", data);
    }

    /** 注册（默认普通用户） */
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        Long count = userMapper.selectCount(new QueryWrapper<User>().eq("username", user.getUsername().trim()));
        if (count > 0) {
            return Result.error("用户名已存在");
        }
        user.setUsername(user.getUsername().trim());
        user.setRole("user");
        user.setStatus(1);
        if (user.getNickname() == null || user.getNickname().isEmpty()) {
            user.setNickname(user.getUsername());
        }
        userMapper.insert(user);
        user.setPassword(null);
        return Result.ok("注册成功", user);
    }

    /** 当前登录用户信息 */
    @GetMapping("/info")
    public Result<User> info(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(401, "用户不存在");
        }
        user.setPassword(null);
        return Result.ok(user);
    }
}
