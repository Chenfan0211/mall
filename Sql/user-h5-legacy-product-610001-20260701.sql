-- 用户端 H5 兼容旧商品详情 ID 610001 增量脚本
-- 日期：2026-07-01
-- 说明：执行到 192.168.28.242 的 mall 数据库后，/api/user/products/610001 返回可售多规格商品详情。

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 模块：用户端 H5 商品详情
-- 操作类型：初始化/修正
-- 涉及表：prd_product, prd_sku, sale_publish_period, sale_publish_sku
-- 日期：2026-07-01
-- 操作人：codex
-- 用途说明：兼容前端旧兜底商品 ID 610001，并提供多规格加购联调数据
-- ============================

INSERT IGNORE INTO prd_product
(id, product_no, product_name, front_category_id, stat_category_id, supplier_id, main_image_url, image_json, audit_status, sale_status, city_id, create_account_id, create_time, update_time, is_deleted)
VALUES
(610001, 'PRD_H5_LEGACY_610001', '四川爱媛果冻橙 5斤装', 816001, 750003, 710001, '/static/user-home/shop-detail.jpg', JSON_ARRAY('/static/user-home/shop-detail.jpg', '/static/user-home/shop-detail2.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0);

UPDATE prd_product
SET product_no = 'PRD_H5_LEGACY_610001',
    product_name = '四川爱媛果冻橙 5斤装',
    front_category_id = 816001,
    stat_category_id = 750003,
    supplier_id = 710001,
    main_image_url = '/static/user-home/shop-detail.jpg',
    image_json = JSON_ARRAY('/static/user-home/shop-detail.jpg', '/static/user-home/shop-detail2.jpg'),
    audit_status = 20,
    sale_status = 1,
    city_id = 440100,
    create_account_id = 700003,
    is_deleted = 0,
    update_time = NOW()
WHERE id = 610001;

INSERT IGNORE INTO prd_sku
(id, sku_no, product_id, sku_name, barcode, sale_spec_text, base_unit, supplier_id, weight_gram, status, create_time, update_time, is_deleted)
VALUES
(819160, 'SKU_H5_LEGACY_610001_5JIN', 610001, '5斤装', '690000819160', '四川爱媛果冻橙 5斤/箱', '箱', 710001, 2500, 1, NOW(), NOW(), 0),
(819161, 'SKU_H5_LEGACY_610001_PLUS', 610001, '5斤装 加量装', '690000819161', '四川爱媛果冻橙 5斤加量/箱', '箱', 710001, 3000, 1, NOW(), NOW(), 0);

UPDATE prd_sku
SET product_id = 610001,
    supplier_id = 710001,
    status = 1,
    is_deleted = 0,
    update_time = NOW()
WHERE sku_no IN ('SKU_H5_LEGACY_610001_5JIN', 'SKU_H5_LEGACY_610001_PLUS');

UPDATE prd_sku
SET sku_name = '5斤装',
    sale_spec_text = '四川爱媛果冻橙 5斤/箱',
    base_unit = '箱',
    weight_gram = 2500,
    update_time = NOW()
WHERE sku_no = 'SKU_H5_LEGACY_610001_5JIN';

UPDATE prd_sku
SET sku_name = '5斤装 加量装',
    sale_spec_text = '四川爱媛果冻橙 5斤加量/箱',
    base_unit = '箱',
    weight_gram = 3000,
    update_time = NOW()
WHERE sku_no = 'SKU_H5_LEGACY_610001_PLUS';

INSERT IGNORE INTO sale_publish_period
(id, period_no, period_name, region_id, city_id, warehouse_id, sale_start_time, sale_end_time, actual_cutoff_time, delivery_date, stock_mode, status, publish_account_id, publish_time, create_time, update_time, is_deleted)
VALUES
(819170, 'PERIOD_H5_LEGACY_610001_ONLINE', '用户端 H5 兼容商品在线团期', 440000, 440100, 50001, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_ADD(NOW(), INTERVAL 18 HOUR), DATE_ADD(NOW(), INTERVAL 18 HOUR), DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1, 20, 700002, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), NOW(), 0);

UPDATE sale_publish_period
SET region_id = 440000,
    city_id = 440100,
    warehouse_id = 50001,
    sale_start_time = DATE_SUB(NOW(), INTERVAL 2 HOUR),
    sale_end_time = DATE_ADD(NOW(), INTERVAL 18 HOUR),
    actual_cutoff_time = DATE_ADD(NOW(), INTERVAL 18 HOUR),
    delivery_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY),
    stock_mode = 1,
    status = 20,
    publish_account_id = 700002,
    publish_time = DATE_SUB(NOW(), INTERVAL 2 HOUR),
    is_deleted = 0,
    update_time = NOW()
WHERE period_no = 'PERIOD_H5_LEGACY_610001_ONLINE';

INSERT IGNORE INTO sale_publish_sku
(id, period_id, product_id, sku_id, supplier_id, sale_price, purchase_price, limit_qty, min_buy_qty, planned_stock_qty, sold_qty, locked_qty, production_date, shelf_life_days, status, create_time, update_time, is_deleted)
VALUES
(819180, 819170, 610001, 819160, 710001, 29.9000, 18.0000, 3, 1, 320, 86, 8, CURDATE(), 7, 1, NOW(), NOW(), 0),
(819181, 819170, 610001, 819161, 710001, 39.9000, 25.0000, 2, 1, 180, 46, 6, CURDATE(), 7, 1, NOW(), NOW(), 0);

UPDATE sale_publish_sku
SET period_id = 819170,
    product_id = 610001,
    supplier_id = 710001,
    planned_stock_qty = GREATEST(planned_stock_qty, sold_qty + locked_qty + 50),
    status = 1,
    is_deleted = 0,
    update_time = NOW()
WHERE id IN (819180, 819181);

UPDATE sale_publish_sku
SET sku_id = 819160,
    sale_price = 29.9000,
    purchase_price = 18.0000,
    limit_qty = 3,
    min_buy_qty = 1,
    planned_stock_qty = GREATEST(320, sold_qty + locked_qty + 50),
    production_date = CURDATE(),
    shelf_life_days = 7,
    update_time = NOW()
WHERE id = 819180;

UPDATE sale_publish_sku
SET sku_id = 819161,
    sale_price = 39.9000,
    purchase_price = 25.0000,
    limit_qty = 2,
    min_buy_qty = 1,
    planned_stock_qty = GREATEST(180, sold_qty + locked_qty + 50),
    production_date = CURDATE(),
    shelf_life_days = 7,
    update_time = NOW()
WHERE id = 819181;

SET FOREIGN_KEY_CHECKS = 1;
