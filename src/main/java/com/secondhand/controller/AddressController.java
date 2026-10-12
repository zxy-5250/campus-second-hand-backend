package com.secondhand.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.secondhand.common.Result;
import com.secondhand.entity.Address;
import com.secondhand.mapper.AddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 收货地址接口
 */
@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressMapper addressMapper;

    /** 我的地址列表 */
    @GetMapping("/list")
    public Result<List<Address>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Address> list = addressMapper.selectList(new QueryWrapper<Address>()
                .eq("user_id", userId)
                .orderByDesc("is_default"));
        return Result.ok(list);
    }

    /** 新增地址 */
    @PostMapping
    public Result<Address> add(@RequestBody Address address, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (address.getReceiverName() == null || address.getPhone() == null || address.getDetailAddress() == null) {
            return Result.error("请填写完整的收货信息");
        }
        address.setId(null);
        address.setUserId(userId);
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        // 若设置为默认，先取消其他默认
        if (address.getIsDefault() == 1) {
            clearDefault(userId);
        }
        addressMapper.insert(address);
        return Result.ok("新增成功", address);
    }

    /** 修改地址 */
    @PutMapping("/{id}")
    public Result<Address> update(@PathVariable Long id, @RequestBody Address address, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Address db = addressMapper.selectById(id);
        if (db == null || !db.getUserId().equals(userId)) {
            return Result.error("地址不存在或无权限");
        }
        address.setId(id);
        address.setUserId(userId);
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            clearDefault(userId);
        }
        addressMapper.updateById(address);
        return Result.ok("修改成功", address);
    }

    /** 删除地址 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Address db = addressMapper.selectById(id);
        if (db == null || !db.getUserId().equals(userId)) {
            return Result.error("地址不存在或无权限");
        }
        addressMapper.deleteById(id);
        return Result.ok();
    }

    /** 设为默认地址 */
    @PostMapping("/{id}/default")
    public Result<Void> setDefault(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Address db = addressMapper.selectById(id);
        if (db == null || !db.getUserId().equals(userId)) {
            return Result.error("地址不存在或无权限");
        }
        clearDefault(userId);
        db.setIsDefault(1);
        addressMapper.updateById(db);
        return Result.ok();
    }

    private void clearDefault(Long userId) {
        List<Address> list = addressMapper.selectList(new QueryWrapper<Address>().eq("user_id", userId));
        for (Address a : list) {
            if (a.getIsDefault() != null && a.getIsDefault() == 1) {
                a.setIsDefault(0);
                addressMapper.updateById(a);
            }
        }
    }
}
