package com.mall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.api.user.dto.UserProductPurchaseRecordDTO;
import com.mall.user.entity.OrdOrderItem;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrdOrderItemMapper extends BaseMapper<OrdOrderItem> {

    @Select("""
            <script>
            SELECT COALESCE(SUM(GREATEST(COALESCE(oi.qty, 0) - COALESCE(oi.refunded_qty, 0), 0)), 0)
            FROM ord_order_item oi
            INNER JOIN ord_order o ON o.id = oi.order_id AND o.is_deleted = 0
            WHERE oi.is_deleted = 0
              AND oi.product_id = #{productId}
              AND COALESCE(o.pay_time, oi.create_time) &gt;= #{since}
              AND o.trade_status IN (20, 50)
              AND o.pay_status IN (30, 50)
              <if test="cityId != null">AND oi.city_id = #{cityId}</if>
              <if test="stationId != null">AND oi.station_id = #{stationId}</if>
            </script>
            """)
    Long sumRecentSoldQty(
            @Param("productId") Long productId,
            @Param("cityId") Long cityId,
            @Param("stationId") Long stationId,
            @Param("since") LocalDateTime since);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM (
                SELECT o.user_id
                FROM ord_order_item oi
                INNER JOIN ord_order o ON o.id = oi.order_id AND o.is_deleted = 0
                WHERE oi.is_deleted = 0
                  AND oi.product_id = #{productId}
                  AND COALESCE(o.pay_time, oi.create_time) &gt;= #{since}
                  AND o.trade_status IN (20, 50)
                  AND o.pay_status IN (30, 50)
                  <if test="cityId != null">AND oi.city_id = #{cityId}</if>
                  <if test="stationId != null">AND oi.station_id = #{stationId}</if>
                GROUP BY o.user_id
                HAVING COUNT(DISTINCT oi.order_id) &gt; 1
            ) repurchase_users
            </script>
            """)
    Long countRecentRepurchaseUsers(
            @Param("productId") Long productId,
            @Param("cityId") Long cityId,
            @Param("stationId") Long stationId,
            @Param("since") LocalDateTime since);

    @Select("""
            <script>
            SELECT
              COALESCE(NULLIF(u.nickname, ''), NULLIF(o.pickup_name, ''), CONCAT('用户', o.user_id)) AS userName,
              oi.product_name AS productName,
              oi.sku_name AS skuName,
              GREATEST(COALESCE(oi.qty, 0) - COALESCE(oi.refunded_qty, 0), 0) AS qty,
              COALESCE(o.pay_time, oi.create_time) AS createTime
            FROM ord_order_item oi
            INNER JOIN ord_order o ON o.id = oi.order_id AND o.is_deleted = 0
            LEFT JOIN usr_user u ON u.id = o.user_id AND u.is_deleted = 0
            WHERE oi.is_deleted = 0
              AND oi.product_id = #{productId}
              AND o.trade_status IN (20, 50)
              AND o.pay_status IN (30, 50)
              AND GREATEST(COALESCE(oi.qty, 0) - COALESCE(oi.refunded_qty, 0), 0) &gt; 0
              <if test="cityId != null">AND oi.city_id = #{cityId}</if>
              <if test="stationId != null">AND oi.station_id = #{stationId}</if>
            ORDER BY COALESCE(o.pay_time, oi.create_time) DESC, oi.id DESC
            LIMIT #{offset}, #{pageSize}
            </script>
            """)
    List<UserProductPurchaseRecordDTO> listPurchaseRecords(
            @Param("productId") Long productId,
            @Param("cityId") Long cityId,
            @Param("stationId") Long stationId,
            @Param("offset") Long offset,
            @Param("pageSize") Long pageSize);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM ord_order_item oi
            INNER JOIN ord_order o ON o.id = oi.order_id AND o.is_deleted = 0
            WHERE oi.is_deleted = 0
              AND oi.product_id = #{productId}
              AND o.trade_status IN (20, 50)
              AND o.pay_status IN (30, 50)
              AND GREATEST(COALESCE(oi.qty, 0) - COALESCE(oi.refunded_qty, 0), 0) &gt; 0
              <if test="cityId != null">AND oi.city_id = #{cityId}</if>
              <if test="stationId != null">AND oi.station_id = #{stationId}</if>
            </script>
            """)
    Long countPurchaseRecords(
            @Param("productId") Long productId,
            @Param("cityId") Long cityId,
            @Param("stationId") Long stationId);
}
