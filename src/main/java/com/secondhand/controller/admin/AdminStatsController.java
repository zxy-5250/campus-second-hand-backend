package com.secondhand.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.secondhand.common.Result;
import com.secondhand.entity.Order;
import com.secondhand.entity.Product;
import com.secondhand.mapper.OrderMapper;
import com.secondhand.mapper.ProductMapper;
import com.secondhand.mapper.StatsMapper;
import com.secondhand.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员首页：数据大屏统计
 */
@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private StatsMapper statsMapper;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();

        // 顶部统计卡片
        Long userTotal = userMapper.selectCount(null);
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        Long newUsersThisMonth = userMapper.selectCount(
                new QueryWrapper<com.secondhand.entity.User>().ge("create_time", monthStart));
        Long productTotal = productMapper.selectCount(null);
        Long orderTotal = orderMapper.selectCount(null);
        Long monthOrders = orderMapper.selectCount(
                new QueryWrapper<Order>().ge("create_time", monthStart));

        // 交易总额（已支付订单金额合计）
        BigDecimal transactionTotal = BigDecimal.ZERO;
        List<Order> paidOrders = orderMapper.selectList(new QueryWrapper<Order>().eq("pay_status", "已支付"));
        for (Order o : paidOrders) {
            if (o.getAmount() != null) {
                transactionTotal = transactionTotal.add(o.getAmount());
            }
        }

        data.put("userTotal", userTotal);
        data.put("newUsersThisMonth", newUsersThisMonth);
        data.put("productTotal", productTotal);
        data.put("orderTotal", orderTotal);
        data.put("transactionTotal", transactionTotal);
        data.put("monthOrders", monthOrders);

        // 饼图：分类商品数
        data.put("categoryPie", statsMapper.categoryProductCount());

        // 柱状图：近 6 个月订单量
        String sixMonthsAgo = LocalDate.now().minusMonths(5).withDayOfMonth(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        data.put("monthlyOrders", statsMapper.orderMonthly(sixMonthsAgo));

        return Result.ok(data);
    }
}
