package com.secondhand.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.common.Result;
import com.secondhand.dto.OrderVO;
import com.secondhand.entity.Order;
import com.secondhand.entity.Product;
import com.secondhand.entity.User;
import com.secondhand.mapper.OrderMapper;
import com.secondhand.mapper.ProductMapper;
import com.secondhand.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理员：订单管理
 */
@RestController
@RequestMapping("/api/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private UserMapper userMapper;

    /** 全部订单列表，可按订单编号搜索 */
    @GetMapping("/list")
    public Result<Page<OrderVO>> list(@RequestParam(required = false) String orderNo,
                                      @RequestParam(required = false) String status,
                                      @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size) {
        QueryWrapper<Order> qw = new QueryWrapper<Order>().orderByDesc("create_time");
        if (orderNo != null && !orderNo.trim().isEmpty()) {
            qw.like("order_no", orderNo.trim());
        }
        if (status != null && !status.trim().isEmpty()) {
            qw.eq("status", status);
        }
        Page<Order> orderPage = orderMapper.selectPage(new Page<>(page, size), qw);
        Page<OrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<OrderVO> vos = new ArrayList<>();
        for (Order o : orderPage.getRecords()) {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(o, vo);
            Product p = productMapper.selectById(o.getProductId());
            if (p != null) {
                vo.setProductTitle(p.getTitle());
                if (p.getImages() != null && !p.getImages().isEmpty()) {
                    vo.setProductImage(p.getImages().split(",")[0]);
                }
            }
            User buyer = userMapper.selectById(o.getBuyerId());
            if (buyer != null) {
                vo.setBuyerNickname(buyer.getNickname());
            }
            User seller = userMapper.selectById(o.getSellerId());
            if (seller != null) {
                vo.setSellerNickname(seller.getNickname());
            }
            vos.add(vo);
        }
        voPage.setRecords(vos);
        return Result.ok(voPage);
    }

    /** 订单详情（弹窗） */
    @GetMapping("/detail/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        Order o = orderMapper.selectById(id);
        if (o == null) {
            return Result.error("订单不存在");
        }
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(o, vo);
        Product p = productMapper.selectById(o.getProductId());
        if (p != null) {
            vo.setProductTitle(p.getTitle());
            if (p.getImages() != null && !p.getImages().isEmpty()) {
                vo.setProductImage(p.getImages().split(",")[0]);
            }
        }
        return Result.ok(vo);
    }

    /** 删除订单 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        orderMapper.deleteById(id);
        return Result.ok("删除成功");
    }
}
