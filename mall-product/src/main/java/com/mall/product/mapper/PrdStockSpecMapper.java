package com.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.api.product.dto.StockSpecDTO;
import com.mall.product.entity.PrdStockSpec;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface PrdStockSpecMapper extends BaseMapper<PrdStockSpec> {

    @Select("""
            SELECT s.id,
                   s.spec_code,
                   s.spec_name,
                   s.unit_name,
                   r.conversion_rate,
                   s.status,
                   r.default_flag
              FROM prd_sku_stock_spec r
              JOIN prd_stock_spec s ON s.id = r.stock_spec_id AND s.is_deleted = 0
             WHERE r.sku_id = #{skuId}
               AND r.is_deleted = 0
             ORDER BY r.default_flag DESC, s.id ASC
            """)
    List<StockSpecDTO> selectBySkuId(@Param("skuId") Long skuId);
}
