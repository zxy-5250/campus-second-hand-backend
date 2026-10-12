package com.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("t_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long productId;
    private Long buyerId;
    private Long sellerId;
    private BigDecimal amount;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    /** 待付款/待收货/已完成/已取消 */
    private String status;
    /** 未支付/已支付 */
    private String payStatus;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
}
