package com.secondhand.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单展示 VO：订单 + 商品信息 + 买卖双方昵称
 */
@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long productId;
    private Long buyerId;
    private Long sellerId;
    private BigDecimal amount;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String status;
    private String payStatus;
    private LocalDateTime payTime;
    private LocalDateTime createTime;

    // 联表字段
    private String productTitle;
    private String productImage;
    private String buyerNickname;
    private String sellerNickname;
}
