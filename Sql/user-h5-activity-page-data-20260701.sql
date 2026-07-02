-- 用户端 H5 运营活动页数据增量脚本
-- 日期：2026-07-01
-- 说明：执行到 192.168.28.242 的 mall 数据库后，首页活动图和活动页商品接口返回可售活动商品。

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 模块：用户端 H5 运营活动页
-- 操作类型：初始化/修正
-- 涉及表：sys_file_asset, prd_product, prd_sku, sale_publish_period, sale_publish_sku
-- 日期：2026-07-01
-- 操作人：codex
-- 用途说明：补齐首页运营活动图片和活动页商品列表联调数据
-- ============================

INSERT IGNORE INTO sys_file_asset
(id, file_no, biz_type, biz_no, file_name, file_url, file_size, file_type, uploader_account_id, status, create_time, update_time, is_deleted)
VALUES
(819100, 'FILE_USER_HOME_BANNER_MAIN_242', 'USER_HOME_BANNER', 'HOME_MAIN', '用户端首页鲜果专场活动图', '/static/user-home/shop-home.jpg', 158236, 'jpg', 700003, 1, NOW(), NOW(), 0),
(819101, 'FILE_USER_HOME_PROMO_ORCHARD_242', 'USER_HOME_PROMO', 'ORCHARD_DIRECT', '用户端果园直供活动图', '/static/user-home/shop-home.jpg', 158236, 'jpg', 700003, 1, NOW(), NOW(), 0),
(819102, 'FILE_USER_HOME_PROMO_NEW_242', 'USER_HOME_PROMO', 'TODAY_NEW', '用户端今日上新活动图', '/static/user-home/shop-list.jpg', 83021, 'jpg', 700003, 1, NOW(), NOW(), 0),
(819103, 'FILE_USER_HOME_PROMO_BREAKFAST_242', 'USER_HOME_PROMO', 'BREAKFAST_BAKERY', '用户端早餐烘焙活动图', '/static/user-home/shop-bakery.jpg', 191641, 'jpg', 700003, 1, NOW(), NOW(), 0);

UPDATE sys_file_asset
SET biz_type = 'USER_HOME_BANNER',
    biz_no = 'HOME_MAIN',
    file_name = '用户端首页鲜果专场活动图',
    file_url = '/static/user-home/shop-home.jpg',
    file_size = 158236,
    file_type = 'jpg',
    status = 1,
    is_deleted = 0,
    update_time = NOW()
WHERE file_no IN ('FILE_USER_HOME_BANNER_MAIN', 'FILE_USER_HOME_BANNER_MAIN_242');

UPDATE sys_file_asset
SET biz_type = 'USER_HOME_PROMO',
    biz_no = 'ORCHARD_DIRECT',
    file_name = '用户端果园直供活动图',
    file_url = '/static/user-home/shop-home.jpg',
    file_size = 158236,
    file_type = 'jpg',
    status = 1,
    is_deleted = 0,
    update_time = NOW()
WHERE file_no IN ('FILE_USER_HOME_PROMO_ORCHARD', 'FILE_USER_HOME_PROMO_ORCHARD_242');

UPDATE sys_file_asset
SET biz_type = 'USER_HOME_PROMO',
    biz_no = 'TODAY_NEW',
    file_name = '用户端今日上新活动图',
    file_url = '/static/user-home/shop-list.jpg',
    file_size = 83021,
    file_type = 'jpg',
    status = 1,
    is_deleted = 0,
    update_time = NOW()
WHERE file_no IN ('FILE_USER_HOME_PROMO_NEW', 'FILE_USER_HOME_PROMO_NEW_242');

UPDATE sys_file_asset
SET biz_type = 'USER_HOME_PROMO',
    biz_no = 'BREAKFAST_BAKERY',
    file_name = '用户端早餐烘焙活动图',
    file_url = '/static/user-home/shop-bakery.jpg',
    file_size = 191641,
    file_type = 'jpg',
    status = 1,
    is_deleted = 0,
    update_time = NOW()
WHERE file_no IN ('FILE_USER_HOME_PROMO_BREAKFAST', 'FILE_USER_HOME_PROMO_BREAKFAST_242');

INSERT IGNORE INTO prd_product
(id, product_no, product_name, front_category_id, stat_category_id, supplier_id, main_image_url, image_json, audit_status, sale_status, city_id, create_account_id, create_time, update_time, is_deleted)
VALUES
(819110, 'PRD_H5_ACTIVITY_PAGE_FRUIT_BOX', '本周鲜果礼盒 5斤装', 750001, 750003, 710001, '/static/user-home/shop-home.jpg', JSON_ARRAY('/static/user-home/shop-home.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(819111, 'PRD_H5_ACTIVITY_PAGE_ORANGE', '果园直供赣南脐橙', 750001, 750003, 710001, '/static/user-home/shop-home.jpg', JSON_ARRAY('/static/user-home/shop-home.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(819112, 'PRD_H5_ACTIVITY_PAGE_BLUEBERRY', '今日上新云南蓝莓', 750001, 750003, 710001, '/static/user-home/shop-home.jpg', JSON_ARRAY('/static/user-home/shop-home.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(819113, 'PRD_H5_ACTIVITY_PAGE_BREAKFAST', '早餐烘焙牛奶吐司组合', 810005, 750003, 710001, '/static/user-home/shop-bakery.jpg', JSON_ARRAY('/static/user-home/shop-bakery.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(819114, 'PRD_H5_ACTIVITY_PAGE_EGG', '今日上新谷物鲜鸡蛋', 810003, 750003, 710001, '/static/user-home/shop-meat.jpg', JSON_ARRAY('/static/user-home/shop-meat.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(819115, 'PRD_H5_ACTIVITY_PAGE_BAGEL', '早餐软欧面包组合', 810005, 750003, 710001, '/static/user-home/shop-bakery.jpg', JSON_ARRAY('/static/user-home/shop-bakery.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0);

UPDATE prd_product
SET audit_status = 20,
    sale_status = 1,
    city_id = 440100,
    product_name = CASE
        WHEN product_no = 'PRD_H5_ACTIVITY_PAGE_BAGEL' THEN '早餐软欧面包组合'
        ELSE product_name
    END,
    update_time = NOW()
WHERE product_no IN (
    'PRD_H5_ACTIVITY_PAGE_FRUIT_BOX',
    'PRD_H5_ACTIVITY_PAGE_ORANGE',
    'PRD_H5_ACTIVITY_PAGE_BLUEBERRY',
    'PRD_H5_ACTIVITY_PAGE_BREAKFAST',
    'PRD_H5_ACTIVITY_PAGE_EGG',
    'PRD_H5_ACTIVITY_PAGE_BAGEL',
    'PRD_H5_ACTIVITY_FRUIT_BOX',
    'PRD_H5_ACTIVITY_BLUEBERRY',
    'PRD_H5_ACTIVITY_BREAKFAST_SET',
    'PRD_H5_ACTIVITY_FRESH_EGG'
);

INSERT IGNORE INTO prd_sku
(id, sku_no, product_id, sku_name, barcode, sale_spec_text, base_unit, supplier_id, weight_gram, status, create_time, update_time, is_deleted)
VALUES
(819120, 'SKU_H5_ACTIVITY_PAGE_FRUIT_BOX', 819110, '5斤装', '690000819120', '5斤/箱', '箱', 710001, 2500, 1, NOW(), NOW(), 0),
(819121, 'SKU_H5_ACTIVITY_PAGE_ORANGE', 819111, '3斤装', '690000819121', '3斤/袋', '袋', 710001, 1500, 1, NOW(), NOW(), 0),
(819122, 'SKU_H5_ACTIVITY_PAGE_BLUEBERRY', 819112, '125g*4盒', '690000819122', '125g*4盒/份', '份', 710001, 500, 1, NOW(), NOW(), 0),
(819123, 'SKU_H5_ACTIVITY_PAGE_BREAKFAST', 819113, '早餐组合', '690000819123', '牛奶950ml+吐司1袋/份', '份', 710001, 1200, 1, NOW(), NOW(), 0),
(819124, 'SKU_H5_ACTIVITY_PAGE_EGG', 819114, '30枚装', '690000819124', '30枚/盒', '盒', 710001, 1800, 1, NOW(), NOW(), 0),
(819125, 'SKU_H5_ACTIVITY_PAGE_BAGEL', 819115, '6只装', '690000819125', '6只/袋', '袋', 710001, 900, 1, NOW(), NOW(), 0);

UPDATE prd_sku
SET status = 1,
    update_time = NOW()
WHERE sku_no IN (
    'SKU_H5_ACTIVITY_PAGE_FRUIT_BOX',
    'SKU_H5_ACTIVITY_PAGE_ORANGE',
    'SKU_H5_ACTIVITY_PAGE_BLUEBERRY',
    'SKU_H5_ACTIVITY_PAGE_BREAKFAST',
    'SKU_H5_ACTIVITY_PAGE_EGG',
    'SKU_H5_ACTIVITY_PAGE_BAGEL',
    'SKU_H5_ACTIVITY_FRUIT_BOX',
    'SKU_H5_ACTIVITY_BLUEBERRY',
    'SKU_H5_ACTIVITY_BREAKFAST_SET',
    'SKU_H5_ACTIVITY_FRESH_EGG'
);

INSERT IGNORE INTO sale_publish_period
(id, period_no, period_name, region_id, city_id, warehouse_id, sale_start_time, sale_end_time, actual_cutoff_time, delivery_date, stock_mode, status, publish_account_id, publish_time, create_time, update_time, is_deleted)
VALUES
(819130, 'PERIOD_H5_ACTIVITY_PAGE_ONLINE', '用户端 H5 运营活动在线团期', 440000, 440100, 50001, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_ADD(NOW(), INTERVAL 18 HOUR), DATE_ADD(NOW(), INTERVAL 18 HOUR), DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1, 20, 700002, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), NOW(), 0);

UPDATE sale_publish_period
SET city_id = 440100,
    sale_start_time = DATE_SUB(NOW(), INTERVAL 2 HOUR),
    sale_end_time = DATE_ADD(NOW(), INTERVAL 18 HOUR),
    actual_cutoff_time = DATE_ADD(NOW(), INTERVAL 18 HOUR),
    delivery_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY),
    status = 20,
    publish_time = DATE_SUB(NOW(), INTERVAL 2 HOUR),
    update_time = NOW()
WHERE period_no IN ('PERIOD_H5_ACTIVITY_PAGE_ONLINE', 'PERIOD_H5_ACTIVITY_ONLINE');

INSERT IGNORE INTO sale_publish_sku
(id, period_id, product_id, sku_id, supplier_id, sale_price, purchase_price, limit_qty, min_buy_qty, planned_stock_qty, sold_qty, locked_qty, production_date, shelf_life_days, status, create_time, update_time, is_deleted)
VALUES
(819140, 819130, 819110, 819120, 710001, 39.9000, 26.0000, 2, 1, 500, 188, 12, CURDATE(), 7, 1, NOW(), NOW(), 0),
(819141, 819130, 819111, 819121, 710001, 24.9000, 16.0000, 3, 1, 460, 165, 9, CURDATE(), 7, 1, NOW(), NOW(), 0),
(819142, 819130, 819112, 819122, 710001, 29.9000, 19.0000, 3, 1, 360, 126, 8, CURDATE(), 5, 1, NOW(), NOW(), 0),
(819143, 819130, 819113, 819123, 710001, 24.9000, 15.0000, 2, 1, 280, 96, 6, CURDATE(), 3, 1, NOW(), NOW(), 0),
(819144, 819130, 819114, 819124, 710001, 32.9000, 22.0000, 2, 1, 320, 75, 5, CURDATE(), 10, 1, NOW(), NOW(), 0),
(819145, 819130, 819115, 819125, 710001, 19.9000, 12.0000, 2, 1, 260, 88, 4, CURDATE(), 3, 1, NOW(), NOW(), 0);

UPDATE sale_publish_sku
SET planned_stock_qty = GREATEST(planned_stock_qty, sold_qty + locked_qty + 50),
    status = 1,
    update_time = NOW()
WHERE id IN (809040, 809041, 809042, 809043, 819140, 819141, 819142, 819143, 819144, 819145);

SET FOREIGN_KEY_CHECKS = 1;
