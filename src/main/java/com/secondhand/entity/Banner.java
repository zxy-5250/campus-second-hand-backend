package com.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 轮播图实体
 */
@Data
@TableName("t_banner")
public class Banner {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String image;
    private String title;
    private String link;
    private Integer sort;
    /** 1=显示，0=隐藏 */
    private Integer status;
}
