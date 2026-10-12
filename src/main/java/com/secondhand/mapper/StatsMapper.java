package com.secondhand.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 数据统计 Mapper：使用原生 SQL 聚合查询
 */
public interface StatsMapper {

    /** 各分类商品数量（饼图数据） */
    @Select("SELECT c.name AS name, COUNT(p.id) AS value " +
            "FROM t_category c LEFT JOIN t_product p ON p.category_id = c.id " +
            "GROUP BY c.id, c.name")
    List<Map<String, Object>> categoryProductCount();

    /** 近 6 个月订单量（柱状图数据） */
    @Select("SELECT DATE_FORMAT(create_time,'%Y-%m') AS month, COUNT(*) AS count " +
            "FROM t_order WHERE create_time >= #{start} GROUP BY month ORDER BY month")
    List<Map<String, Object>> orderMonthly(@Param("start") String start);
}
