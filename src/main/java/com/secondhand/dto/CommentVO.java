package com.secondhand.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 留言展示 VO：留言 + 留言人昵称/头像
 */
@Data
public class CommentVO {
    private Long id;
    private Long productId;
    private Long userId;
    private String content;
    private String reply;
    private LocalDateTime createTime;

    // 联表字段
    private String nickname;
    private String avatar;
    /** 商品标题（留言信息页展示用） */
    private String productTitle;
}
