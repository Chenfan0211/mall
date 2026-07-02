-- 用户端 H5 分类与分类商品接口数据增量脚本
-- 日期：2026-07-01
-- 说明：执行到远端 192.168.28.242 对应 mall 数据库后，用户端 H5 分类和商品接口会返回新增 6 个分类与每类 10 个商品。

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 模块：用户端 H5 分类与分类商品
-- 操作类型：初始化
-- 涉及表：prd_category, sys_file_asset, prd_product, prd_sku, sale_publish_period, sale_publish_sku
-- 日期：2026-07-01
-- 操作人：codex
-- 用途说明：补齐首页和分类页接口联调数据，新增 6 个前台分类且每类至少 10 个广州在线团期可售商品
-- ============================
INSERT IGNORE INTO prd_category
(id, parent_id, category_code, category_name, category_type, sort_no, status, create_time, update_time, is_deleted)
VALUES
(810001, 0, 'FRONT_SEAFOOD', '海鲜水产', 1, 30, 1, NOW(), NOW(), 0),
(810002, 0, 'FRONT_COOKED', '熟食卤味', 1, 40, 1, NOW(), NOW(), 0),
(810003, 0, 'FRONT_MEAT_EGG', '肉禽蛋品', 1, 50, 1, NOW(), NOW(), 0),
(810004, 0, 'FRONT_SNACK', '休闲零食', 1, 60, 1, NOW(), NOW(), 0),
(810005, 0, 'FRONT_BREAKFAST', '早餐烘焙', 1, 70, 1, NOW(), NOW(), 0),
(810006, 0, 'FRONT_GRAIN_OIL', '粮油调味', 1, 80, 1, NOW(), NOW(), 0);

INSERT IGNORE INTO sys_file_asset
(id, file_no, biz_type, biz_no, file_name, file_url, file_size, file_type, uploader_account_id, status, create_time, update_time, is_deleted)
VALUES
(810010, 'FILE_USER_CATEGORY_FRONT_SEAFOOD', 'USER_CATEGORY_ICON', 'FRONT_SEAFOOD', '用户端海鲜水产分类图标', '/static/user-home/shop-seafood.jpg', 193980, 'jpg', 700003, 1, NOW(), NOW(), 0),
(810011, 'FILE_USER_CATEGORY_FRONT_COOKED', 'USER_CATEGORY_ICON', 'FRONT_COOKED', '用户端熟食卤味分类图标', '/static/user-home/shop-cooked.jpg', 132981, 'jpg', 700003, 1, NOW(), NOW(), 0),
(810012, 'FILE_USER_CATEGORY_FRONT_MEAT_EGG', 'USER_CATEGORY_ICON', 'FRONT_MEAT_EGG', '用户端肉禽蛋品分类图标', '/static/user-home/shop-meat.jpg', 144913, 'jpg', 700003, 1, NOW(), NOW(), 0),
(810013, 'FILE_USER_CATEGORY_FRONT_SNACK', 'USER_CATEGORY_ICON', 'FRONT_SNACK', '用户端休闲零食分类图标', '/static/user-home/shop-list.jpg', 83021, 'jpg', 700003, 1, NOW(), NOW(), 0),
(810014, 'FILE_USER_CATEGORY_FRONT_BREAKFAST', 'USER_CATEGORY_ICON', 'FRONT_BREAKFAST', '用户端早餐烘焙分类图标', '/static/user-home/shop-bakery.jpg', 191641, 'jpg', 700003, 1, NOW(), NOW(), 0),
(810015, 'FILE_USER_CATEGORY_FRONT_GRAIN_OIL', 'USER_CATEGORY_ICON', 'FRONT_GRAIN_OIL', '用户端粮油调味分类图标', '/static/user-home/shop-grain.jpg', 92274, 'jpg', 700003, 1, NOW(), NOW(), 0),
(810016, 'FILE_USER_CATEGORY_FRONT_FRUIT', 'USER_CATEGORY_ICON', 'FRONT_FRUIT', '用户端水果鲜食分类图标', '/static/user-home/shop-home.jpg', 158236, 'jpg', 700003, 1, NOW(), NOW(), 0),
(810017, 'FILE_USER_CATEGORY_FRONT_VEGETABLE', 'USER_CATEGORY_ICON', 'FRONT_VEGETABLE', '用户端蔬菜豆制分类图标', '/static/user-home/shop-category.jpg', 100122, 'jpg', 700003, 1, NOW(), NOW(), 0);

UPDATE sys_file_asset
SET biz_type = 'USER_CATEGORY_ICON',
    biz_no = 'FRONT_SEAFOOD',
    file_name = '用户端海鲜水产分类图标',
    file_url = '/static/user-home/shop-seafood.jpg',
    file_size = 193980,
    file_type = 'jpg',
    status = 1,
    is_deleted = 0,
    update_time = NOW()
WHERE file_no = 'FILE_USER_CATEGORY_FRONT_SEAFOOD';

UPDATE sys_file_asset
SET biz_type = 'USER_CATEGORY_ICON',
    biz_no = 'FRONT_COOKED',
    file_name = '用户端熟食卤味分类图标',
    file_url = '/static/user-home/shop-cooked.jpg',
    file_size = 132981,
    file_type = 'jpg',
    status = 1,
    is_deleted = 0,
    update_time = NOW()
WHERE file_no = 'FILE_USER_CATEGORY_FRONT_COOKED';

UPDATE sys_file_asset
SET biz_type = 'USER_CATEGORY_ICON',
    biz_no = 'FRONT_MEAT_EGG',
    file_name = '用户端肉禽蛋品分类图标',
    file_url = '/static/user-home/shop-meat.jpg',
    file_size = 144913,
    file_type = 'jpg',
    status = 1,
    is_deleted = 0,
    update_time = NOW()
WHERE file_no = 'FILE_USER_CATEGORY_FRONT_MEAT_EGG';

UPDATE sys_file_asset
SET biz_type = 'USER_CATEGORY_ICON',
    biz_no = 'FRONT_SNACK',
    file_name = '用户端休闲零食分类图标',
    file_url = '/static/user-home/shop-list.jpg',
    file_size = 83021,
    file_type = 'jpg',
    status = 1,
    is_deleted = 0,
    update_time = NOW()
WHERE file_no = 'FILE_USER_CATEGORY_FRONT_SNACK';

UPDATE sys_file_asset
SET biz_type = 'USER_CATEGORY_ICON',
    biz_no = 'FRONT_BREAKFAST',
    file_name = '用户端早餐烘焙分类图标',
    file_url = '/static/user-home/shop-bakery.jpg',
    file_size = 191641,
    file_type = 'jpg',
    status = 1,
    is_deleted = 0,
    update_time = NOW()
WHERE file_no = 'FILE_USER_CATEGORY_FRONT_BREAKFAST';

UPDATE sys_file_asset
SET biz_type = 'USER_CATEGORY_ICON',
    biz_no = 'FRONT_GRAIN_OIL',
    file_name = '用户端粮油调味分类图标',
    file_url = '/static/user-home/shop-grain.jpg',
    file_size = 92274,
    file_type = 'jpg',
    status = 1,
    is_deleted = 0,
    update_time = NOW()
WHERE file_no = 'FILE_USER_CATEGORY_FRONT_GRAIN_OIL';

UPDATE sys_file_asset
SET biz_type = 'USER_CATEGORY_ICON',
    biz_no = 'FRONT_FRUIT',
    file_name = '用户端水果鲜食分类图标',
    file_url = '/static/user-home/shop-home.jpg',
    file_size = 158236,
    file_type = 'jpg',
    status = 1,
    is_deleted = 0,
    update_time = NOW()
WHERE file_no = 'FILE_USER_CATEGORY_FRONT_FRUIT';

UPDATE sys_file_asset
SET biz_type = 'USER_CATEGORY_ICON',
    biz_no = 'FRONT_VEGETABLE',
    file_name = '用户端蔬菜豆制分类图标',
    file_url = '/static/user-home/shop-category.jpg',
    file_size = 100122,
    file_type = 'jpg',
    status = 1,
    is_deleted = 0,
    update_time = NOW()
WHERE file_no = 'FILE_USER_CATEGORY_FRONT_VEGETABLE';

INSERT IGNORE INTO prd_product
(id, product_no, product_name, front_category_id, stat_category_id, supplier_id, main_image_url, image_json, audit_status, sale_status, city_id, create_account_id, create_time, update_time, is_deleted)
VALUES
(810100, 'PRD_H5_CAT_01_01', '舟山带鱼段', 810001, 750003, 710001, '/static/user-home/shop-seafood.jpg', JSON_ARRAY('/static/user-home/shop-seafood.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810101, 'PRD_H5_CAT_01_02', '鲜冻白虾', 810001, 750003, 710001, '/static/user-home/shop-seafood.jpg', JSON_ARRAY('/static/user-home/shop-seafood.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810102, 'PRD_H5_CAT_01_03', '蒜蓉粉丝扇贝', 810001, 750003, 710001, '/static/user-home/shop-seafood.jpg', JSON_ARRAY('/static/user-home/shop-seafood.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810103, 'PRD_H5_CAT_01_04', '深海鳕鱼排', 810001, 750003, 710001, '/static/user-home/shop-seafood.jpg', JSON_ARRAY('/static/user-home/shop-seafood.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810104, 'PRD_H5_CAT_01_05', '青岛蛤蜊', 810001, 750003, 710001, '/static/user-home/shop-seafood.jpg', JSON_ARRAY('/static/user-home/shop-seafood.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810105, 'PRD_H5_CAT_01_06', '鲜活鲈鱼', 810001, 750003, 710001, '/static/user-home/shop-seafood.jpg', JSON_ARRAY('/static/user-home/shop-seafood.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810106, 'PRD_H5_CAT_01_07', '三文鱼切片', 810001, 750003, 710001, '/static/user-home/shop-seafood.jpg', JSON_ARRAY('/static/user-home/shop-seafood.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810107, 'PRD_H5_CAT_01_08', '东海小黄鱼', 810001, 750003, 710001, '/static/user-home/shop-seafood.jpg', JSON_ARRAY('/static/user-home/shop-seafood.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810108, 'PRD_H5_CAT_01_09', '冷冻鱿鱼圈', 810001, 750003, 710001, '/static/user-home/shop-seafood.jpg', JSON_ARRAY('/static/user-home/shop-seafood.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810109, 'PRD_H5_CAT_01_10', '即食海蜇丝', 810001, 750003, 710001, '/static/user-home/shop-seafood.jpg', JSON_ARRAY('/static/user-home/shop-seafood.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810200, 'PRD_H5_CAT_02_01', '卤香鸡腿', 810002, 750003, 710001, '/static/user-home/shop-cooked.jpg', JSON_ARRAY('/static/user-home/shop-cooked.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810201, 'PRD_H5_CAT_02_02', '酱香牛肉', 810002, 750003, 710001, '/static/user-home/shop-cooked.jpg', JSON_ARRAY('/static/user-home/shop-cooked.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810202, 'PRD_H5_CAT_02_03', '藤椒鸡爪', 810002, 750003, 710001, '/static/user-home/shop-cooked.jpg', JSON_ARRAY('/static/user-home/shop-cooked.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810203, 'PRD_H5_CAT_02_04', '盐水鸭半只', 810002, 750003, 710001, '/static/user-home/shop-cooked.jpg', JSON_ARRAY('/static/user-home/shop-cooked.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810204, 'PRD_H5_CAT_02_05', '蜜汁叉烧', 810002, 750003, 710001, '/static/user-home/shop-cooked.jpg', JSON_ARRAY('/static/user-home/shop-cooked.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810205, 'PRD_H5_CAT_02_06', '卤味拼盘', 810002, 750003, 710001, '/static/user-home/shop-cooked.jpg', JSON_ARRAY('/static/user-home/shop-cooked.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810206, 'PRD_H5_CAT_02_07', '五香豆干', 810002, 750003, 710001, '/static/user-home/shop-cooked.jpg', JSON_ARRAY('/static/user-home/shop-cooked.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810207, 'PRD_H5_CAT_02_08', '红油耳片', 810002, 750003, 710001, '/static/user-home/shop-cooked.jpg', JSON_ARRAY('/static/user-home/shop-cooked.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810208, 'PRD_H5_CAT_02_09', '黑椒烤肠', 810002, 750003, 710001, '/static/user-home/shop-cooked.jpg', JSON_ARRAY('/static/user-home/shop-cooked.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810209, 'PRD_H5_CAT_02_10', '香卤鸭翅', 810002, 750003, 710001, '/static/user-home/shop-cooked.jpg', JSON_ARRAY('/static/user-home/shop-cooked.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810300, 'PRD_H5_CAT_03_01', '土猪前腿肉', 810003, 750003, 710001, '/static/user-home/shop-meat.jpg', JSON_ARRAY('/static/user-home/shop-meat.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810301, 'PRD_H5_CAT_03_02', '精品五花肉', 810003, 750003, 710001, '/static/user-home/shop-meat.jpg', JSON_ARRAY('/static/user-home/shop-meat.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810302, 'PRD_H5_CAT_03_03', '黄油土鸡', 810003, 750003, 710001, '/static/user-home/shop-meat.jpg', JSON_ARRAY('/static/user-home/shop-meat.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810303, 'PRD_H5_CAT_03_04', '鲜牛腱子肉', 810003, 750003, 710001, '/static/user-home/shop-meat.jpg', JSON_ARRAY('/static/user-home/shop-meat.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810304, 'PRD_H5_CAT_03_05', '黑椒牛排', 810003, 750003, 710001, '/static/user-home/shop-meat.jpg', JSON_ARRAY('/static/user-home/shop-meat.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810305, 'PRD_H5_CAT_03_06', '琵琶鸡翅', 810003, 750003, 710001, '/static/user-home/shop-meat.jpg', JSON_ARRAY('/static/user-home/shop-meat.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810306, 'PRD_H5_CAT_03_07', '散养土鸡蛋', 810003, 750003, 710001, '/static/user-home/shop-meat.jpg', JSON_ARRAY('/static/user-home/shop-meat.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810307, 'PRD_H5_CAT_03_08', '鲜鸭蛋礼盒', 810003, 750003, 710001, '/static/user-home/shop-meat.jpg', JSON_ARRAY('/static/user-home/shop-meat.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810308, 'PRD_H5_CAT_03_09', '猪肉馅', 810003, 750003, 710001, '/static/user-home/shop-meat.jpg', JSON_ARRAY('/static/user-home/shop-meat.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810309, 'PRD_H5_CAT_03_10', '羔羊肉卷', 810003, 750003, 710001, '/static/user-home/shop-meat.jpg', JSON_ARRAY('/static/user-home/shop-meat.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810400, 'PRD_H5_CAT_04_01', '每日坚果包', 810004, 750003, 710001, '/static/user-home/shop-list.jpg', JSON_ARRAY('/static/user-home/shop-list.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810401, 'PRD_H5_CAT_04_02', '手撕面包', 810004, 750003, 710001, '/static/user-home/shop-list.jpg', JSON_ARRAY('/static/user-home/shop-list.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810402, 'PRD_H5_CAT_04_03', '山楂小方', 810004, 750003, 710001, '/static/user-home/shop-list.jpg', JSON_ARRAY('/static/user-home/shop-list.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810403, 'PRD_H5_CAT_04_04', '海苔脆片', 810004, 750003, 710001, '/static/user-home/shop-list.jpg', JSON_ARRAY('/static/user-home/shop-list.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810404, 'PRD_H5_CAT_04_05', '芝麻薄脆', 810004, 750003, 710001, '/static/user-home/shop-list.jpg', JSON_ARRAY('/static/user-home/shop-list.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810405, 'PRD_H5_CAT_04_06', '黄桃果干', 810004, 750003, 710001, '/static/user-home/shop-list.jpg', JSON_ARRAY('/static/user-home/shop-list.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810406, 'PRD_H5_CAT_04_07', '紫薯脆条', 810004, 750003, 710001, '/static/user-home/shop-list.jpg', JSON_ARRAY('/static/user-home/shop-list.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810407, 'PRD_H5_CAT_04_08', '牛轧饼干', 810004, 750003, 710001, '/static/user-home/shop-list.jpg', JSON_ARRAY('/static/user-home/shop-list.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810408, 'PRD_H5_CAT_04_09', '酸奶溶豆', 810004, 750003, 710001, '/static/user-home/shop-list.jpg', JSON_ARRAY('/static/user-home/shop-list.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810409, 'PRD_H5_CAT_04_10', '原味薯片', 810004, 750003, 710001, '/static/user-home/shop-list.jpg', JSON_ARRAY('/static/user-home/shop-list.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810500, 'PRD_H5_CAT_05_01', '全麦吐司', 810005, 750003, 710001, '/static/user-home/shop-bakery.jpg', JSON_ARRAY('/static/user-home/shop-bakery.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810501, 'PRD_H5_CAT_05_02', '鲜奶小餐包', 810005, 750003, 710001, '/static/user-home/shop-bakery.jpg', JSON_ARRAY('/static/user-home/shop-bakery.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810502, 'PRD_H5_CAT_05_03', '核桃贝果', 810005, 750003, 710001, '/static/user-home/shop-bakery.jpg', JSON_ARRAY('/static/user-home/shop-bakery.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810503, 'PRD_H5_CAT_05_04', '原味手抓饼', 810005, 750003, 710001, '/static/user-home/shop-bakery.jpg', JSON_ARRAY('/static/user-home/shop-bakery.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810504, 'PRD_H5_CAT_05_05', '香葱花卷', 810005, 750003, 710001, '/static/user-home/shop-bakery.jpg', JSON_ARRAY('/static/user-home/shop-bakery.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810505, 'PRD_H5_CAT_05_06', '黑米馒头', 810005, 750003, 710001, '/static/user-home/shop-bakery.jpg', JSON_ARRAY('/static/user-home/shop-bakery.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810506, 'PRD_H5_CAT_05_07', '鲜肉小笼包', 810005, 750003, 710001, '/static/user-home/shop-bakery.jpg', JSON_ARRAY('/static/user-home/shop-bakery.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810507, 'PRD_H5_CAT_05_08', '玉米蒸饺', 810005, 750003, 710001, '/static/user-home/shop-bakery.jpg', JSON_ARRAY('/static/user-home/shop-bakery.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810508, 'PRD_H5_CAT_05_09', '燕麦酸奶', 810005, 750003, 710001, '/static/user-home/shop-bakery.jpg', JSON_ARRAY('/static/user-home/shop-bakery.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810509, 'PRD_H5_CAT_05_10', '鲜牛奶950ml', 810005, 750003, 710001, '/static/user-home/shop-bakery.jpg', JSON_ARRAY('/static/user-home/shop-bakery.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810600, 'PRD_H5_CAT_06_01', '东北长粒香米', 810006, 750003, 710001, '/static/user-home/shop-grain.jpg', JSON_ARRAY('/static/user-home/shop-grain.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810601, 'PRD_H5_CAT_06_02', '五常稻花香', 810006, 750003, 710001, '/static/user-home/shop-grain.jpg', JSON_ARRAY('/static/user-home/shop-grain.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810602, 'PRD_H5_CAT_06_03', '压榨花生油', 810006, 750003, 710001, '/static/user-home/shop-grain.jpg', JSON_ARRAY('/static/user-home/shop-grain.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810603, 'PRD_H5_CAT_06_04', '低芥酸菜籽油', 810006, 750003, 710001, '/static/user-home/shop-grain.jpg', JSON_ARRAY('/static/user-home/shop-grain.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810604, 'PRD_H5_CAT_06_05', '有机小米', 810006, 750003, 710001, '/static/user-home/shop-grain.jpg', JSON_ARRAY('/static/user-home/shop-grain.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810605, 'PRD_H5_CAT_06_06', '陈醋500ml', 810006, 750003, 710001, '/static/user-home/shop-grain.jpg', JSON_ARRAY('/static/user-home/shop-grain.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810606, 'PRD_H5_CAT_06_07', '生抽酱油', 810006, 750003, 710001, '/static/user-home/shop-grain.jpg', JSON_ARRAY('/static/user-home/shop-grain.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810607, 'PRD_H5_CAT_06_08', '火锅底料', 810006, 750003, 710001, '/static/user-home/shop-grain.jpg', JSON_ARRAY('/static/user-home/shop-grain.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810608, 'PRD_H5_CAT_06_09', '海盐食用盐', 810006, 750003, 710001, '/static/user-home/shop-grain.jpg', JSON_ARRAY('/static/user-home/shop-grain.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0),
(810609, 'PRD_H5_CAT_06_10', '高筋面粉', 810006, 750003, 710001, '/static/user-home/shop-grain.jpg', JSON_ARRAY('/static/user-home/shop-grain.jpg'), 20, 1, 440100, 700003, NOW(), NOW(), 0);

INSERT IGNORE INTO prd_sku
(id, sku_no, product_id, sku_name, barcode, sale_spec_text, base_unit, supplier_id, weight_gram, status, create_time, update_time, is_deleted)
VALUES
(811100, 'SKU_H5_CAT_01_01', 810100, '舟山带鱼段500g装', '690000811100', '500g装/份', '份', 710001, 500, 1, NOW(), NOW(), 0),
(811101, 'SKU_H5_CAT_01_02', 810101, '鲜冻白虾1kg装', '690000811101', '1kg装/份', '份', 710001, 1000, 1, NOW(), NOW(), 0),
(811102, 'SKU_H5_CAT_01_03', 810102, '蒜蓉粉丝扇贝2斤装', '690000811102', '2斤装/份', '份', 710001, 1000, 1, NOW(), NOW(), 0),
(811103, 'SKU_H5_CAT_01_04', 810103, '深海鳕鱼排家庭装', '690000811103', '家庭装/份', '份', 710001, 1500, 1, NOW(), NOW(), 0),
(811104, 'SKU_H5_CAT_01_05', 810104, '青岛蛤蜊尝鲜装', '690000811104', '尝鲜装/份', '份', 710001, 450, 1, NOW(), NOW(), 0),
(811105, 'SKU_H5_CAT_01_06', 810105, '鲜活鲈鱼3包装', '690000811105', '3包装/份', '份', 710001, 900, 1, NOW(), NOW(), 0),
(811106, 'SKU_H5_CAT_01_07', 810106, '三文鱼切片礼盒装', '690000811106', '礼盒装/份', '份', 710001, 1200, 1, NOW(), NOW(), 0),
(811107, 'SKU_H5_CAT_01_08', 810107, '东海小黄鱼整只装', '690000811107', '整只装/份', '份', 710001, 1100, 1, NOW(), NOW(), 0),
(811108, 'SKU_H5_CAT_01_09', 810108, '冷冻鱿鱼圈切片装', '690000811108', '切片装/份', '份', 710001, 800, 1, NOW(), NOW(), 0),
(811109, 'SKU_H5_CAT_01_10', 810109, '即食海蜇丝大份装', '690000811109', '大份装/份', '份', 710001, 1600, 1, NOW(), NOW(), 0),
(811200, 'SKU_H5_CAT_02_01', 810200, '卤香鸡腿500g装', '690000811200', '500g装/份', '份', 710001, 500, 1, NOW(), NOW(), 0),
(811201, 'SKU_H5_CAT_02_02', 810201, '酱香牛肉1kg装', '690000811201', '1kg装/份', '份', 710001, 1000, 1, NOW(), NOW(), 0),
(811202, 'SKU_H5_CAT_02_03', 810202, '藤椒鸡爪2斤装', '690000811202', '2斤装/份', '份', 710001, 1000, 1, NOW(), NOW(), 0),
(811203, 'SKU_H5_CAT_02_04', 810203, '盐水鸭半只家庭装', '690000811203', '家庭装/份', '份', 710001, 1500, 1, NOW(), NOW(), 0),
(811204, 'SKU_H5_CAT_02_05', 810204, '蜜汁叉烧尝鲜装', '690000811204', '尝鲜装/份', '份', 710001, 450, 1, NOW(), NOW(), 0),
(811205, 'SKU_H5_CAT_02_06', 810205, '卤味拼盘3包装', '690000811205', '3包装/份', '份', 710001, 900, 1, NOW(), NOW(), 0),
(811206, 'SKU_H5_CAT_02_07', 810206, '五香豆干礼盒装', '690000811206', '礼盒装/份', '份', 710001, 1200, 1, NOW(), NOW(), 0),
(811207, 'SKU_H5_CAT_02_08', 810207, '红油耳片整只装', '690000811207', '整只装/份', '份', 710001, 1100, 1, NOW(), NOW(), 0),
(811208, 'SKU_H5_CAT_02_09', 810208, '黑椒烤肠切片装', '690000811208', '切片装/份', '份', 710001, 800, 1, NOW(), NOW(), 0),
(811209, 'SKU_H5_CAT_02_10', 810209, '香卤鸭翅大份装', '690000811209', '大份装/份', '份', 710001, 1600, 1, NOW(), NOW(), 0),
(811300, 'SKU_H5_CAT_03_01', 810300, '土猪前腿肉500g装', '690000811300', '500g装/份', '份', 710001, 500, 1, NOW(), NOW(), 0),
(811301, 'SKU_H5_CAT_03_02', 810301, '精品五花肉1kg装', '690000811301', '1kg装/份', '份', 710001, 1000, 1, NOW(), NOW(), 0),
(811302, 'SKU_H5_CAT_03_03', 810302, '黄油土鸡2斤装', '690000811302', '2斤装/份', '份', 710001, 1000, 1, NOW(), NOW(), 0),
(811303, 'SKU_H5_CAT_03_04', 810303, '鲜牛腱子肉家庭装', '690000811303', '家庭装/份', '份', 710001, 1500, 1, NOW(), NOW(), 0),
(811304, 'SKU_H5_CAT_03_05', 810304, '黑椒牛排尝鲜装', '690000811304', '尝鲜装/份', '份', 710001, 450, 1, NOW(), NOW(), 0),
(811305, 'SKU_H5_CAT_03_06', 810305, '琵琶鸡翅3包装', '690000811305', '3包装/份', '份', 710001, 900, 1, NOW(), NOW(), 0),
(811306, 'SKU_H5_CAT_03_07', 810306, '散养土鸡蛋礼盒装', '690000811306', '礼盒装/份', '份', 710001, 1200, 1, NOW(), NOW(), 0),
(811307, 'SKU_H5_CAT_03_08', 810307, '鲜鸭蛋礼盒整只装', '690000811307', '整只装/份', '份', 710001, 1100, 1, NOW(), NOW(), 0),
(811308, 'SKU_H5_CAT_03_09', 810308, '猪肉馅切片装', '690000811308', '切片装/份', '份', 710001, 800, 1, NOW(), NOW(), 0),
(811309, 'SKU_H5_CAT_03_10', 810309, '羔羊肉卷大份装', '690000811309', '大份装/份', '份', 710001, 1600, 1, NOW(), NOW(), 0),
(811400, 'SKU_H5_CAT_04_01', 810400, '每日坚果包500g装', '690000811400', '500g装/份', '份', 710001, 500, 1, NOW(), NOW(), 0),
(811401, 'SKU_H5_CAT_04_02', 810401, '手撕面包1kg装', '690000811401', '1kg装/份', '份', 710001, 1000, 1, NOW(), NOW(), 0),
(811402, 'SKU_H5_CAT_04_03', 810402, '山楂小方2斤装', '690000811402', '2斤装/份', '份', 710001, 1000, 1, NOW(), NOW(), 0),
(811403, 'SKU_H5_CAT_04_04', 810403, '海苔脆片家庭装', '690000811403', '家庭装/份', '份', 710001, 1500, 1, NOW(), NOW(), 0),
(811404, 'SKU_H5_CAT_04_05', 810404, '芝麻薄脆尝鲜装', '690000811404', '尝鲜装/份', '份', 710001, 450, 1, NOW(), NOW(), 0),
(811405, 'SKU_H5_CAT_04_06', 810405, '黄桃果干3包装', '690000811405', '3包装/份', '份', 710001, 900, 1, NOW(), NOW(), 0),
(811406, 'SKU_H5_CAT_04_07', 810406, '紫薯脆条礼盒装', '690000811406', '礼盒装/份', '份', 710001, 1200, 1, NOW(), NOW(), 0),
(811407, 'SKU_H5_CAT_04_08', 810407, '牛轧饼干整只装', '690000811407', '整只装/份', '份', 710001, 1100, 1, NOW(), NOW(), 0),
(811408, 'SKU_H5_CAT_04_09', 810408, '酸奶溶豆切片装', '690000811408', '切片装/份', '份', 710001, 800, 1, NOW(), NOW(), 0),
(811409, 'SKU_H5_CAT_04_10', 810409, '原味薯片大份装', '690000811409', '大份装/份', '份', 710001, 1600, 1, NOW(), NOW(), 0),
(811500, 'SKU_H5_CAT_05_01', 810500, '全麦吐司500g装', '690000811500', '500g装/份', '份', 710001, 500, 1, NOW(), NOW(), 0),
(811501, 'SKU_H5_CAT_05_02', 810501, '鲜奶小餐包1kg装', '690000811501', '1kg装/份', '份', 710001, 1000, 1, NOW(), NOW(), 0),
(811502, 'SKU_H5_CAT_05_03', 810502, '核桃贝果2斤装', '690000811502', '2斤装/份', '份', 710001, 1000, 1, NOW(), NOW(), 0),
(811503, 'SKU_H5_CAT_05_04', 810503, '原味手抓饼家庭装', '690000811503', '家庭装/份', '份', 710001, 1500, 1, NOW(), NOW(), 0),
(811504, 'SKU_H5_CAT_05_05', 810504, '香葱花卷尝鲜装', '690000811504', '尝鲜装/份', '份', 710001, 450, 1, NOW(), NOW(), 0),
(811505, 'SKU_H5_CAT_05_06', 810505, '黑米馒头3包装', '690000811505', '3包装/份', '份', 710001, 900, 1, NOW(), NOW(), 0),
(811506, 'SKU_H5_CAT_05_07', 810506, '鲜肉小笼包礼盒装', '690000811506', '礼盒装/份', '份', 710001, 1200, 1, NOW(), NOW(), 0),
(811507, 'SKU_H5_CAT_05_08', 810507, '玉米蒸饺整只装', '690000811507', '整只装/份', '份', 710001, 1100, 1, NOW(), NOW(), 0),
(811508, 'SKU_H5_CAT_05_09', 810508, '燕麦酸奶切片装', '690000811508', '切片装/份', '份', 710001, 800, 1, NOW(), NOW(), 0),
(811509, 'SKU_H5_CAT_05_10', 810509, '鲜牛奶950ml大份装', '690000811509', '大份装/份', '份', 710001, 1600, 1, NOW(), NOW(), 0),
(811600, 'SKU_H5_CAT_06_01', 810600, '东北长粒香米500g装', '690000811600', '500g装/份', '份', 710001, 500, 1, NOW(), NOW(), 0),
(811601, 'SKU_H5_CAT_06_02', 810601, '五常稻花香1kg装', '690000811601', '1kg装/份', '份', 710001, 1000, 1, NOW(), NOW(), 0),
(811602, 'SKU_H5_CAT_06_03', 810602, '压榨花生油2斤装', '690000811602', '2斤装/份', '份', 710001, 1000, 1, NOW(), NOW(), 0),
(811603, 'SKU_H5_CAT_06_04', 810603, '低芥酸菜籽油家庭装', '690000811603', '家庭装/份', '份', 710001, 1500, 1, NOW(), NOW(), 0),
(811604, 'SKU_H5_CAT_06_05', 810604, '有机小米尝鲜装', '690000811604', '尝鲜装/份', '份', 710001, 450, 1, NOW(), NOW(), 0),
(811605, 'SKU_H5_CAT_06_06', 810605, '陈醋500ml3包装', '690000811605', '3包装/份', '份', 710001, 900, 1, NOW(), NOW(), 0),
(811606, 'SKU_H5_CAT_06_07', 810606, '生抽酱油礼盒装', '690000811606', '礼盒装/份', '份', 710001, 1200, 1, NOW(), NOW(), 0),
(811607, 'SKU_H5_CAT_06_08', 810607, '火锅底料整只装', '690000811607', '整只装/份', '份', 710001, 1100, 1, NOW(), NOW(), 0),
(811608, 'SKU_H5_CAT_06_09', 810608, '海盐食用盐切片装', '690000811608', '切片装/份', '份', 710001, 800, 1, NOW(), NOW(), 0),
(811609, 'SKU_H5_CAT_06_10', 810609, '高筋面粉大份装', '690000811609', '大份装/份', '份', 710001, 1600, 1, NOW(), NOW(), 0);

INSERT IGNORE INTO sale_publish_period
(id, period_no, period_name, region_id, city_id, warehouse_id, sale_start_time, sale_end_time, actual_cutoff_time, delivery_date, stock_mode, status, publish_account_id, publish_time, create_time, update_time, is_deleted)
VALUES
(810900, 'PERIOD_H5_CATEGORY_ONLINE', '用户端 H5 分类商品在线团期', 440000, 440100, 50001, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_ADD(NOW(), INTERVAL 18 HOUR), DATE_ADD(NOW(), INTERVAL 18 HOUR), DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1, 20, 700002, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), NOW(), 0);

INSERT IGNORE INTO sale_publish_sku
(id, period_id, product_id, sku_id, supplier_id, sale_price, purchase_price, limit_qty, min_buy_qty, planned_stock_qty, sold_qty, locked_qty, production_date, shelf_life_days, status, create_time, update_time, is_deleted)
VALUES
(812100, 810900, 810100, 811100, 710001, 12.9000, 8.0000, 3, 1, 360, 35, 2, CURDATE(), 5, 1, NOW(), NOW(), 0),
(812101, 810900, 810101, 811101, 710001, 14.6000, 9.1000, 5, 1, 375, 40, 3, CURDATE(), 7, 1, NOW(), NOW(), 0),
(812102, 810900, 810102, 811102, 710001, 16.3000, 10.1000, 2, 1, 390, 45, 4, CURDATE(), 3, 1, NOW(), NOW(), 0),
(812103, 810900, 810103, 811103, 710001, 18.0000, 11.2000, 4, 1, 405, 50, 5, CURDATE(), 10, 1, NOW(), NOW(), 0),
(812104, 810900, 810104, 811104, 710001, 19.7000, 12.2000, 0, 1, 420, 55, 6, CURDATE(), 4, 1, NOW(), NOW(), 0),
(812105, 810900, 810105, 811105, 710001, 21.4000, 13.3000, 6, 1, 435, 60, 2, CURDATE(), 6, 1, NOW(), NOW(), 0),
(812106, 810900, 810106, 811106, 710001, 23.1000, 14.3000, 2, 1, 450, 65, 3, CURDATE(), 8, 1, NOW(), NOW(), 0),
(812107, 810900, 810107, 811107, 710001, 24.8000, 15.4000, 3, 1, 465, 70, 4, CURDATE(), 5, 1, NOW(), NOW(), 0),
(812108, 810900, 810108, 811108, 710001, 26.5000, 16.4000, 4, 1, 480, 75, 5, CURDATE(), 12, 1, NOW(), NOW(), 0),
(812109, 810900, 810109, 811109, 710001, 28.2000, 17.5000, 0, 1, 495, 80, 6, CURDATE(), 9, 1, NOW(), NOW(), 0),
(812200, 810900, 810200, 811200, 710001, 15.9000, 9.9000, 3, 1, 400, 43, 2, CURDATE(), 5, 1, NOW(), NOW(), 0),
(812201, 810900, 810201, 811201, 710001, 17.6000, 10.9000, 5, 1, 415, 48, 3, CURDATE(), 7, 1, NOW(), NOW(), 0),
(812202, 810900, 810202, 811202, 710001, 19.3000, 12.0000, 2, 1, 430, 53, 4, CURDATE(), 3, 1, NOW(), NOW(), 0),
(812203, 810900, 810203, 811203, 710001, 21.0000, 13.0000, 4, 1, 445, 58, 5, CURDATE(), 10, 1, NOW(), NOW(), 0),
(812204, 810900, 810204, 811204, 710001, 22.7000, 14.1000, 0, 1, 460, 63, 6, CURDATE(), 4, 1, NOW(), NOW(), 0),
(812205, 810900, 810205, 811205, 710001, 24.4000, 15.1000, 6, 1, 475, 68, 2, CURDATE(), 6, 1, NOW(), NOW(), 0),
(812206, 810900, 810206, 811206, 710001, 26.1000, 16.2000, 2, 1, 490, 73, 3, CURDATE(), 8, 1, NOW(), NOW(), 0),
(812207, 810900, 810207, 811207, 710001, 27.8000, 17.2000, 3, 1, 505, 78, 4, CURDATE(), 5, 1, NOW(), NOW(), 0),
(812208, 810900, 810208, 811208, 710001, 29.5000, 18.3000, 4, 1, 520, 83, 5, CURDATE(), 12, 1, NOW(), NOW(), 0),
(812209, 810900, 810209, 811209, 710001, 31.2000, 19.3000, 0, 1, 535, 88, 6, CURDATE(), 9, 1, NOW(), NOW(), 0),
(812300, 810900, 810300, 811300, 710001, 18.9000, 11.7000, 3, 1, 440, 51, 2, CURDATE(), 5, 1, NOW(), NOW(), 0),
(812301, 810900, 810301, 811301, 710001, 20.6000, 12.8000, 5, 1, 455, 56, 3, CURDATE(), 7, 1, NOW(), NOW(), 0),
(812302, 810900, 810302, 811302, 710001, 22.3000, 13.8000, 2, 1, 470, 61, 4, CURDATE(), 3, 1, NOW(), NOW(), 0),
(812303, 810900, 810303, 811303, 710001, 24.0000, 14.9000, 4, 1, 485, 66, 5, CURDATE(), 10, 1, NOW(), NOW(), 0),
(812304, 810900, 810304, 811304, 710001, 25.7000, 15.9000, 0, 1, 500, 71, 6, CURDATE(), 4, 1, NOW(), NOW(), 0),
(812305, 810900, 810305, 811305, 710001, 27.4000, 17.0000, 6, 1, 515, 76, 2, CURDATE(), 6, 1, NOW(), NOW(), 0),
(812306, 810900, 810306, 811306, 710001, 29.1000, 18.0000, 2, 1, 530, 81, 3, CURDATE(), 8, 1, NOW(), NOW(), 0),
(812307, 810900, 810307, 811307, 710001, 30.8000, 19.1000, 3, 1, 545, 86, 4, CURDATE(), 5, 1, NOW(), NOW(), 0),
(812308, 810900, 810308, 811308, 710001, 32.5000, 20.2000, 4, 1, 560, 91, 5, CURDATE(), 12, 1, NOW(), NOW(), 0),
(812309, 810900, 810309, 811309, 710001, 34.2000, 21.2000, 0, 1, 575, 96, 6, CURDATE(), 9, 1, NOW(), NOW(), 0),
(812400, 810900, 810400, 811400, 710001, 21.9000, 13.6000, 3, 1, 480, 59, 2, CURDATE(), 5, 1, NOW(), NOW(), 0),
(812401, 810900, 810401, 811401, 710001, 23.6000, 14.6000, 5, 1, 495, 64, 3, CURDATE(), 7, 1, NOW(), NOW(), 0),
(812402, 810900, 810402, 811402, 710001, 25.3000, 15.7000, 2, 1, 510, 69, 4, CURDATE(), 3, 1, NOW(), NOW(), 0),
(812403, 810900, 810403, 811403, 710001, 27.0000, 16.7000, 4, 1, 525, 74, 5, CURDATE(), 10, 1, NOW(), NOW(), 0),
(812404, 810900, 810404, 811404, 710001, 28.7000, 17.8000, 0, 1, 540, 79, 6, CURDATE(), 4, 1, NOW(), NOW(), 0),
(812405, 810900, 810405, 811405, 710001, 30.4000, 18.8000, 6, 1, 555, 84, 2, CURDATE(), 6, 1, NOW(), NOW(), 0),
(812406, 810900, 810406, 811406, 710001, 32.1000, 19.9000, 2, 1, 570, 89, 3, CURDATE(), 8, 1, NOW(), NOW(), 0),
(812407, 810900, 810407, 811407, 710001, 33.8000, 21.0000, 3, 1, 585, 94, 4, CURDATE(), 5, 1, NOW(), NOW(), 0),
(812408, 810900, 810408, 811408, 710001, 35.5000, 22.0000, 4, 1, 600, 99, 5, CURDATE(), 12, 1, NOW(), NOW(), 0),
(812409, 810900, 810409, 811409, 710001, 37.2000, 23.1000, 0, 1, 615, 104, 6, CURDATE(), 9, 1, NOW(), NOW(), 0),
(812500, 810900, 810500, 811500, 710001, 24.9000, 15.4000, 3, 1, 520, 67, 2, CURDATE(), 5, 1, NOW(), NOW(), 0),
(812501, 810900, 810501, 811501, 710001, 26.6000, 16.5000, 5, 1, 535, 72, 3, CURDATE(), 7, 1, NOW(), NOW(), 0),
(812502, 810900, 810502, 811502, 710001, 28.3000, 17.5000, 2, 1, 550, 77, 4, CURDATE(), 3, 1, NOW(), NOW(), 0),
(812503, 810900, 810503, 811503, 710001, 30.0000, 18.6000, 4, 1, 565, 82, 5, CURDATE(), 10, 1, NOW(), NOW(), 0),
(812504, 810900, 810504, 811504, 710001, 31.7000, 19.7000, 0, 1, 580, 87, 6, CURDATE(), 4, 1, NOW(), NOW(), 0),
(812505, 810900, 810505, 811505, 710001, 33.4000, 20.7000, 6, 1, 595, 92, 2, CURDATE(), 6, 1, NOW(), NOW(), 0),
(812506, 810900, 810506, 811506, 710001, 35.1000, 21.8000, 2, 1, 610, 97, 3, CURDATE(), 8, 1, NOW(), NOW(), 0),
(812507, 810900, 810507, 811507, 710001, 36.8000, 22.8000, 3, 1, 625, 102, 4, CURDATE(), 5, 1, NOW(), NOW(), 0),
(812508, 810900, 810508, 811508, 710001, 38.5000, 23.9000, 4, 1, 640, 107, 5, CURDATE(), 12, 1, NOW(), NOW(), 0),
(812509, 810900, 810509, 811509, 710001, 40.2000, 24.9000, 0, 1, 655, 112, 6, CURDATE(), 9, 1, NOW(), NOW(), 0),
(812600, 810900, 810600, 811600, 710001, 27.9000, 17.3000, 3, 1, 560, 75, 2, CURDATE(), 5, 1, NOW(), NOW(), 0),
(812601, 810900, 810601, 811601, 710001, 29.6000, 18.4000, 5, 1, 575, 80, 3, CURDATE(), 7, 1, NOW(), NOW(), 0),
(812602, 810900, 810602, 811602, 710001, 31.3000, 19.4000, 2, 1, 590, 85, 4, CURDATE(), 3, 1, NOW(), NOW(), 0),
(812603, 810900, 810603, 811603, 710001, 33.0000, 20.5000, 4, 1, 605, 90, 5, CURDATE(), 10, 1, NOW(), NOW(), 0),
(812604, 810900, 810604, 811604, 710001, 34.7000, 21.5000, 0, 1, 620, 95, 6, CURDATE(), 4, 1, NOW(), NOW(), 0),
(812605, 810900, 810605, 811605, 710001, 36.4000, 22.6000, 6, 1, 635, 100, 2, CURDATE(), 6, 1, NOW(), NOW(), 0),
(812606, 810900, 810606, 811606, 710001, 38.1000, 23.6000, 2, 1, 650, 105, 3, CURDATE(), 8, 1, NOW(), NOW(), 0),
(812607, 810900, 810607, 811607, 710001, 39.8000, 24.7000, 3, 1, 665, 110, 4, CURDATE(), 5, 1, NOW(), NOW(), 0),
(812608, 810900, 810608, 811608, 710001, 41.5000, 25.7000, 4, 1, 680, 115, 5, CURDATE(), 12, 1, NOW(), NOW(), 0),
(812609, 810900, 810609, 811609, 710001, 43.2000, 26.8000, 0, 1, 695, 120, 6, CURDATE(), 9, 1, NOW(), NOW(), 0);

UPDATE prd_product
SET main_image_url = CASE
        WHEN front_category_id = 810001 THEN '/static/user-home/shop-seafood.jpg'
        WHEN front_category_id = 810002 THEN '/static/user-home/shop-cooked.jpg'
        WHEN front_category_id = 810003 THEN '/static/user-home/shop-meat.jpg'
        WHEN front_category_id = 810004 THEN '/static/user-home/shop-list.jpg'
        WHEN front_category_id = 810005 THEN '/static/user-home/shop-bakery.jpg'
        WHEN front_category_id = 810006 THEN '/static/user-home/shop-grain.jpg'
        ELSE main_image_url
    END,
    image_json = CASE
        WHEN front_category_id = 810001 THEN JSON_ARRAY('/static/user-home/shop-seafood.jpg')
        WHEN front_category_id = 810002 THEN JSON_ARRAY('/static/user-home/shop-cooked.jpg')
        WHEN front_category_id = 810003 THEN JSON_ARRAY('/static/user-home/shop-meat.jpg')
        WHEN front_category_id = 810004 THEN JSON_ARRAY('/static/user-home/shop-list.jpg')
        WHEN front_category_id = 810005 THEN JSON_ARRAY('/static/user-home/shop-bakery.jpg')
        WHEN front_category_id = 810006 THEN JSON_ARRAY('/static/user-home/shop-grain.jpg')
        ELSE image_json
    END,
    update_time = NOW()
WHERE product_no LIKE 'PRD_H5_CAT_%'
  AND front_category_id IN (810001, 810002, 810003, 810004, 810005, 810006);

UPDATE prd_product
SET main_image_url = '/static/user-home/shop-home.jpg',
    image_json = JSON_ARRAY('/static/user-home/shop-home.jpg'),
    update_time = NOW()
WHERE product_no = 'PRD_H5_ACTIVITY_FRUIT_BOX';

INSERT IGNORE INTO sys_file_asset
(id, file_no, biz_type, biz_no, file_name, file_url, file_size, file_type, uploader_account_id, status, create_time, update_time, is_deleted)
VALUES
(809000, 'FILE_USER_HOME_BANNER_MAIN', 'USER_HOME_BANNER', 'HOME_MAIN', '用户端首页鲜果专场活动图', '/static/user-home/shop-home.jpg', 158236, 'jpg', 700003, 1, NOW(), NOW(), 0),
(809001, 'FILE_USER_HOME_PROMO_ORCHARD', 'USER_HOME_PROMO', 'ORCHARD_DIRECT', '用户端果园直供活动图', '/static/user-home/shop-seafood.jpg', 193980, 'jpg', 700003, 1, NOW(), NOW(), 0),
(809002, 'FILE_USER_HOME_PROMO_NEW', 'USER_HOME_PROMO', 'TODAY_NEW', '用户端今日上新活动图', '/static/user-home/shop-list.jpg', 83021, 'jpg', 700003, 1, NOW(), NOW(), 0),
(809003, 'FILE_USER_HOME_PROMO_BREAKFAST', 'USER_HOME_PROMO', 'BREAKFAST_BAKERY', '用户端早餐烘焙活动图', '/static/user-home/shop-bakery.jpg', 191641, 'jpg', 700003, 1, NOW(), NOW(), 0);

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
WHERE file_no = 'FILE_USER_HOME_BANNER_MAIN';

UPDATE sys_file_asset
SET biz_type = 'USER_HOME_PROMO',
    biz_no = 'ORCHARD_DIRECT',
    file_name = '用户端果园直供活动图',
    file_url = '/static/user-home/shop-seafood.jpg',
    file_size = 193980,
    file_type = 'jpg',
    status = 1,
    is_deleted = 0,
    update_time = NOW()
WHERE file_no = 'FILE_USER_HOME_PROMO_ORCHARD';

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
WHERE file_no = 'FILE_USER_HOME_PROMO_NEW';

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
WHERE file_no = 'FILE_USER_HOME_PROMO_BREAKFAST';

SET FOREIGN_KEY_CHECKS = 1;
