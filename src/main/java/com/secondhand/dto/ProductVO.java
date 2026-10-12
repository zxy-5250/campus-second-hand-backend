package com.secondhand.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品展示 VO：商品 + 分类名 + 发布人昵称
 */
@Data
public class ProductVO {
    private Long id;
    private Long userId;
    private Long categoryId;
    private String title;
    private String description;
    private BigDecimal price;
    private String location;
    private String images;
    private String status;
    private Integer viewCount;
    private LocalDateTime createTime;

    // 联表字段
    private String categoryName;
    private String username;
    private String nickname;
}
