package com.secondhand.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.secondhand.common.Result;
import com.secondhand.dto.OrderVO;
import com.secondhand.entity.Address;
import com.secondhand.entity.Order;
import com.secondhand.entity.Product;
import com.secondhand.entity.User;
import com.secondhand.mapper.AddressMapper;
import com.secondhand.mapper.OrderMapper;
import com.secondhand.mapper.ProductMapper;
import com.secondhand.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 订单接口
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private UserMapper userMapper;

    /** 下单：选择收货地址，生成待付款订单 */
    @PostMapping("/create")
    public Result<Order> create(@RequestBody Map<String, Long> body, HttpServletRequest request) {
        Long buyerId = (Long) request.getAttribute("userId");
        Long productId = body.get("productId");
        Long addressId = body.get("addressId");
        if (productId == null || addressId == null) {
            return Result.error("参数不完整");
        }
        Product p = productMapper.selectById(productId);
        if (p == null) {
            return Result.error("商品不存在");
        }
        if (!"在售".equals(p.getStatus())) {
            return Result.error("商品已下架或已售出");
        }
        if (p.getUserId().equals(buyerId)) {
            return Result.error("不能购买自己发布的商品");
        }
        Address addr = addressMapper.selectById(addressId);
        if (addr == null || !addr.getUserId().equals(buyerId)) {
            return Result.error("收货地址无效");
        }
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setProductId(productId);
        order.setBuyerId(buyerId);
        order.setSellerId(p.getUserId());
        order.setAmount(p.getPrice());
        order.setReceiverName(addr.getReceiverName());
        order.setReceiverPhone(addr.getPhone());
        order.setReceiverAddress(addr.getDetailAddress());
        order.setStatus("待付款");
        order.setPayStatus("未支付");
        orderMapper.insert(order);
        return Result.ok("下单成功，请尽快支付", order);
    }

    /** 我的买入订单 */
    @GetMapping("/buy")
    public Result<List<OrderVO>> buy(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Order> list = orderMapper.selectList(new QueryWrapper<Order>()
                .eq("buyer_id", userId)
                .orderByDesc("create_time"));
        return Result.ok(toVOs(list));
    }

    /** 我的卖出订单（别人买我发布的商品） */
    @GetMapping("/sell")
    public Result<List<OrderVO>> sell(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Order> list = orderMapper.selectList(new QueryWrapper<Order>()
                .eq("seller_id", userId)
                .orderByDesc("create_time"));
        return Result.ok(toVOs(list));
    }

    /** 立即支付 */
    @PostMapping("/{id}/pay")
    public Result<Order> pay(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Order db = orderMapper.selectById(id);
        if (db == null || !db.getBuyerId().equals(userId)) {
            return Result.error("订单不存在或无权限");
        }
        if (!"待付款".equals(db.getStatus())) {
            return Result.error("当前订单状态不可支付");
        }
        db.setStatus("待收货");
        db.setPayStatus("已支付");
        db.setPayTime(LocalDateTime.now());
        orderMapper.updateById(db);
        // 商品标记为已售
        Product p = productMapper.selectById(db.getProductId());
        if (p != null) {
            p.setStatus("已售");
            productMapper.updateById(p);
        }
        return Result.ok("支付成功", db);
    }

    /** 取消订单（仅待付款状态，恢复商品可售） */
    @PostMapping("/{id}/cancel")
    public Result<Order> cancel(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Order db = orderMapper.selectById(id);
        if (db == null || !db.getBuyerId().equals(userId)) {
            return Result.error("订单不存在或无权限");
        }
        if (!"待付款".equals(db.getStatus())) {
            return Result.error("当前订单状态不可取消");
        }
        db.setStatus("已取消");
        orderMapper.updateById(db);
        Product p = productMapper.selectById(db.getProductId());
        if (p != null && "已售".equals(p.getStatus())) {
            p.setStatus("在售");
            productMapper.updateById(p);
        }
        return Result.ok("订单已取消", db);
    }

    /** 确认收货（完成交易） */
    @PostMapping("/{id}/confirm")
    public Result<Order> confirm(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Order db = orderMapper.selectById(id);
        if (db == null || !db.getBuyerId().equals(userId)) {
            return Result.error("订单不存在或无权限");
        }
        if (!"待收货".equals(db.getStatus())) {
            return Result.error("当前订单状态不可确认收货");
        }
        db.setStatus("已完成");
        orderMapper.updateById(db);
        return Result.ok("交易完成", db);
    }

    /** 订单详情 */
    @GetMapping("/detail/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        Order db = orderMapper.selectById(id);
        if (db == null) {
            return Result.error("订单不存在");
        }
        return Result.ok(toVO(db));
    }

    /**
     * 管理员：查询全部订单（支持订单号、状态筛选）
     */
    @GetMapping("/admin/list")
    public Result<List<OrderVO>> adminList(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status
    ) {
        QueryWrapper<Order> qw = new QueryWrapper<>();
        if (orderNo != null && !orderNo.trim().isEmpty()) {
            qw.like("order_no", orderNo.trim());
        }
        if (status != null && !status.trim().isEmpty()) {
            qw.eq("status", status);
        }
        qw.orderByDesc("create_time");
        List<Order> orderList = orderMapper.selectList(qw);
        // 调用toVOs自动填充商品、买家、卖家昵称
        return Result.ok(toVOs(orderList));
    }

    /**
     * 管理员：订单详情（和用户端detail是同一个接口，但是确认放行）
     * 接口就是 /api/order/detail/{id}，已经存在，只需要确认前端调用这个接口
     */


    /** ============ 组装 VO ============ */

    private List<OrderVO> toVOs(List<Order> list) {
        List<OrderVO> vos = new ArrayList<>();
        for (Order o : list) {
            vos.add(toVO(o));
        }
        return vos;
    }

    private OrderVO toVO(Order o) {
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
        return vo;
    }

    private String generateOrderNo() {
        return "SH" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));
    }
}
