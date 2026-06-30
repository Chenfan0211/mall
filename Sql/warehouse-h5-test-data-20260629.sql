-- Warehouse H5 test data exported from Sql/mall.sql
-- Import this after the base schema/data when validating http://192.168.28.211/warehouse/
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
INSERT INTO sys_account
(id, account_no, username, mobile, password_hash, account_type, login_portal_code, subject_type, subject_id, status, last_login_time, last_login_ip, remark, create_time, update_time, is_deleted)
VALUES
(807000, 'ACC_TEST_WMS_BUYER_H5', 'test_wms_buyer', '13800008000', '$2a$10$oxgynGL/z8Bad5ExH/or0eNrsHypVvBIttfG7BaymYuYhBint5Rhy', 3, 'warehouse-h5', 3, 50001, 1, NOW(), '127.0.0.1', '仓配H5采购员测试账号', NOW(), NOW(), 0),
(807001, 'ACC_TEST_WMS_MANAGER_H5', 'test_wms_manager', '13800008001', '$2a$10$oxgynGL/z8Bad5ExH/or0eNrsHypVvBIttfG7BaymYuYhBint5Rhy', 3, 'warehouse-h5', 3, 50001, 1, NOW(), '127.0.0.1', '仓配H5主管测试账号', NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
id = VALUES(id),
account_no = VALUES(account_no),
mobile = VALUES(mobile),
password_hash = VALUES(password_hash),
account_type = VALUES(account_type),
login_portal_code = VALUES(login_portal_code),
subject_type = VALUES(subject_type),
subject_id = VALUES(subject_id),
status = VALUES(status),
remark = VALUES(remark),
update_time = NOW(),
is_deleted = 0;

INSERT IGNORE INTO sys_role
(id, role_code, role_name, portal_code, role_type, status, remark, create_time, update_time, is_deleted)
VALUES
(807002, 'wms_h5_manager', '仓配H5仓库主管', 'warehouse-h5', 17, 1, '仓配移动端主管汇总、任务和库存只读联调角色', NOW(), NOW(), 0);

INSERT IGNORE INTO sys_account_role
(id, account_id, role_id, create_time, update_time, is_deleted)
VALUES
(807010, 807000, 21002, NOW(), NOW(), 0),
(807011, 807001, 807002, NOW(), NOW(), 0);

INSERT IGNORE INTO sys_account_data_scope
(id, account_id, scope_id, create_time, update_time, is_deleted)
VALUES
(807020, 807000, 60003, NOW(), NOW(), 0),
(807021, 807001, 60003, NOW(), NOW(), 0);

INSERT IGNORE INTO sys_role_button
(id, role_id, portal_code, module_code, button_code, button_name, risk_level, status, create_time, update_time, is_deleted)
VALUES
(807030, 807002, 'warehouse-h5', 'wms:warehouse', 'view', '查看仓库', 1, 1, NOW(), NOW(), 0),
(807031, 807002, 'warehouse-h5', 'wms:zone', 'view', '查看库区', 1, 1, NOW(), NOW(), 0),
(807032, 807002, 'warehouse-h5', 'wms:location', 'view', '查看库位', 1, 1, NOW(), NOW(), 0),
(807033, 807002, 'warehouse-h5', 'wms:line', 'view', '查看线路', 1, 1, NOW(), NOW(), 0),
(807034, 807002, 'warehouse-h5', 'wms:line-station', 'view', '查看线路自提点', 1, 1, NOW(), NOW(), 0),
(807035, 807002, 'warehouse-h5', 'wms:inbound', 'view', '查看入库单', 1, 1, NOW(), NOW(), 0),
(807036, 807002, 'warehouse-h5', 'wms:inbound-item', 'view', '查看入库明细', 1, 1, NOW(), NOW(), 0),
(807037, 807002, 'warehouse-h5', 'wms:receive', 'view', '查看收货记录', 1, 1, NOW(), NOW(), 0),
(807038, 807002, 'warehouse-h5', 'wms:quality', 'view', '查看质检文件', 1, 1, NOW(), NOW(), 0),
(807039, 807002, 'warehouse-h5', 'wms:putaway', 'view', '查看上架任务', 1, 1, NOW(), NOW(), 0),
(807040, 807002, 'warehouse-h5', 'wms:inventory', 'view', '查看库存', 1, 1, NOW(), NOW(), 0),
(807041, 807002, 'warehouse-h5', 'wms:inventory-lock', 'view', '查看库存锁定', 1, 1, NOW(), NOW(), 0),
(807042, 807002, 'warehouse-h5', 'wms:inventory-log', 'view', '查看库存流水', 1, 1, NOW(), NOW(), 0),
(807043, 807002, 'warehouse-h5', 'wms:stocktake', 'view', '查看盘点单', 1, 1, NOW(), NOW(), 0),
(807044, 807002, 'warehouse-h5', 'wms:wave', 'view', '查看波次', 1, 1, NOW(), NOW(), 0),
(807045, 807002, 'warehouse-h5', 'wms:pick', 'view', '查看拣货任务', 1, 1, NOW(), NOW(), 0),
(807046, 807002, 'warehouse-h5', 'wms:pick-item', 'view', '查看拣货明细', 1, 1, NOW(), NOW(), 0),
(807047, 807002, 'warehouse-h5', 'wms:loading', 'view', '查看装车单', 1, 1, NOW(), NOW(), 0),
(807048, 807002, 'warehouse-h5', 'wms:loading-item', 'view', '查看装车明细', 1, 1, NOW(), NOW(), 0),
(807049, 807002, 'warehouse-h5', 'wms:outbound', 'view', '查看出库单', 1, 1, NOW(), NOW(), 0),
(807050, 807002, 'warehouse-h5', 'wms:delivery', 'view', '查看配送单', 1, 1, NOW(), NOW(), 0),
(807051, 807002, 'warehouse-h5', 'wms:delivery-station', 'view', '查看配送自提点', 1, 1, NOW(), NOW(), 0),
(807052, 807002, 'warehouse-h5', 'wms:return', 'view', '查看回仓交接', 1, 1, NOW(), NOW(), 0),
(807053, 807002, 'warehouse-h5', 'wms:operation-log', 'view', '查看WMS操作日志', 1, 1, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_inbound_order
(id, inbound_no, source_type, source_no, purchase_id, delivery_id, supplier_id, warehouse_id, status, receive_account_id, receive_time, create_time, update_time, is_deleted)
VALUES
(807100, 'INB_H5_WAIT_ARRIVE_001', 1, 'PDEL_H5_WAIT_ARRIVE_001', 806302, 806320, 610001, 50001, 10, NULL, NULL, NOW(), NOW(), 0),
(807101, 'INB_H5_WAIT_RECEIVE_001', 1, 'PDEL_H5_WAIT_RECEIVE_001', 806303, 806321, 610001, 50001, 20, 700008, NULL, NOW(), NOW(), 0),
(807102, 'INB_H5_RECEIVING_001', 1, 'PDEL_H5_RECEIVING_001', 806303, 806322, 610001, 50001, 30, 700008, NULL, NOW(), NOW(), 0),
(807103, 'INB_H5_RECEIVED_PUTAWAY_001', 1, 'PDEL_H5_RECEIVED_PUTAWAY_001', 806304, 806323, 610001, 50001, 40, 700008, DATE_SUB(NOW(), INTERVAL 50 MINUTE), NOW(), NOW(), 0),
(807104, 'INB_H5_QC_EXCEPTION_001', 1, 'PDEL_H5_QC_EXCEPTION_001', 806304, 806323, 610001, 50001, 50, 700008, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), NOW(), 0);

INSERT IGNORE INTO wms_inbound_item
(id, inbound_id, sku_id, supplier_id, expected_qty, received_qty, rejected_qty, damaged_qty, batch_no, production_date, shelf_life_days, diff_reason, create_time, update_time, is_deleted)
VALUES
(807110, 807100, 806030, 610001, 120, 0, 0, 0, 'BATCH_H5_INB_WAIT_001', CURDATE(), 10, NULL, NOW(), NOW(), 0),
(807111, 807101, 806031, 610001, 90, 0, 0, 0, 'BATCH_H5_INB_WAIT_002', CURDATE(), 5, NULL, NOW(), NOW(), 0),
(807112, 807102, 806030, 610001, 150, 80, 0, 0, 'BATCH_H5_INB_RECEIVING_001', CURDATE(), 10, '收货中，待补扫库位', NOW(), NOW(), 0),
(807113, 807103, 806030, 610001, 180, 178, 0, 2, 'BATCH_H5_INB_PUTAWAY_001', CURDATE(), 10, '破损2箱，待上架', NOW(), NOW(), 0),
(807114, 807104, 806031, 610001, 80, 72, 5, 3, 'BATCH_H5_INB_QC_001', CURDATE(), 5, '质检异常，包装破损', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_receive_record
(id, receive_no, inbound_id, inbound_item_id, sku_id, receive_qty, reject_qty, diff_qty, receive_account_id, quality_status, remark, create_time, update_time, is_deleted)
VALUES
(807120, 'RCV_H5_RECEIVING_001', 807102, 807112, 806030, 80, 0, 70, 700008, 4, '收货中待确认', NOW(), NOW(), 0),
(807121, 'RCV_H5_PUTAWAY_001', 807103, 807113, 806030, 178, 0, 2, 700008, 1, '已收货待上架', NOW(), NOW(), 0),
(807122, 'RCV_H5_QC_EXCEPTION_001', 807104, 807114, 806031, 72, 5, 8, 700008, 3, '质检异常样例', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_quality_file
(id, quality_no, inbound_id, sku_id, quality_result, file_asset_id, remark, create_time, update_time, is_deleted)
VALUES
(807130, 'QUAL_H5_RECEIVE_PASS_001', 807103, 806030, 1, 710204, 'H5收货合格凭证', NOW(), NOW(), 0),
(807131, 'QUAL_H5_RECEIVE_EXCEPTION_001', 807104, 806031, 3, 710204, 'H5质检异常凭证', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_putaway_task
(id, putaway_no, inbound_id, sku_id, warehouse_id, from_zone_id, target_zone_id, target_location_id, putaway_qty, actual_qty, status, assign_account_id, complete_time, create_time, update_time, is_deleted)
VALUES
(807140, 'PUT_H5_WAIT_001', 807103, 806030, 50001, 51002, 51003, 780003, 120, 0, 10, 807001, NULL, NOW(), NOW(), 0),
(807141, 'PUT_H5_DOING_001', 807103, 806030, 50001, 51002, 51003, 780003, 58, 30, 20, 807001, NULL, NOW(), NOW(), 0),
(807142, 'PUT_H5_DONE_001', 807104, 806031, 50001, 51002, 51003, 780003, 72, 72, 30, 807001, NOW(), NOW(), NOW(), 0),
(807143, 'PUT_H5_CLOSED_001', 807104, 806031, 50001, 51002, 51003, 780003, 8, 0, 40, 807001, NULL, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_inventory
(id, warehouse_id, zone_id, location_id, sku_id, supplier_id, batch_no, production_date, shelf_life_days, in_stock_qty, locked_qty, available_qty, frozen_qty, inventory_status, version, create_time, update_time, is_deleted)
VALUES
(807150, 50001, 51002, 780002, 806030, 610001, 'BATCH_H5_INV_WAIT_PUT_001', CURDATE(), 10, 120, 0, 0, 0, 10, 1, NOW(), NOW(), 0),
(807151, 50001, 51003, 780003, 806030, 610001, 'BATCH_H5_INV_AVAILABLE_001', CURDATE(), 10, 260, 60, 200, 0, 20, 1, NOW(), NOW(), 0),
(807152, 50001, 51004, 780004, 806030, 610001, 'BATCH_H5_INV_PICK_TEMP_001', CURDATE(), 10, 48, 18, 30, 0, 30, 1, NOW(), NOW(), 0),
(807153, 50001, 51006, 780006, 806031, 610001, 'BATCH_H5_INV_RETURN_001', CURDATE(), 5, 12, 0, 12, 0, 40, 1, NOW(), NOW(), 0),
(807154, 50001, 51001, 780001, 806031, 610001, 'BATCH_H5_INV_CONFIRM_001', CURDATE(), 5, 18, 0, 0, 18, 50, 1, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_inventory_lock
(id, lock_no, warehouse_id, inventory_id, sku_id, biz_type, biz_no, lock_qty, release_qty, lock_status, create_time, update_time, is_deleted)
VALUES
(807160, 'LOCK_H5_PICK_001', 50001, 807151, 806030, 2, 'WAVE_H5_PICK_WAIT_001', 40, 0, 10, NOW(), NOW(), 0),
(807161, 'LOCK_H5_RELEASE_001', 50001, 807151, 806030, 1, 'ORD_H5_CANCEL_001', 10, 10, 20, NOW(), NOW(), 0),
(807162, 'LOCK_H5_OUTBOUND_001', 50001, 807151, 806030, 2, 'WAVE_H5_PICK_DONE_001', 20, 0, 30, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_inventory_log
(id, log_no, warehouse_id, zone_id, location_id, sku_id, batch_no, change_type, before_qty, change_qty, after_qty, biz_type, biz_no, operator_account_id, remark, create_time, update_time, is_deleted)
VALUES
(807170, 'INV_LOG_H5_INBOUND_001', 50001, 51002, 780002, 806030, 'BATCH_H5_INV_WAIT_PUT_001', 1, 0, 120, 120, 'inbound', 'INB_H5_RECEIVED_PUTAWAY_001', 700008, 'H5收货入库流水', NOW(), NOW(), 0),
(807171, 'INV_LOG_H5_PUTAWAY_001', 50001, 51003, 780003, 806030, 'BATCH_H5_INV_AVAILABLE_001', 2, 120, 140, 260, 'putaway', 'PUT_H5_DONE_001', 807001, 'H5上架流水', NOW(), NOW(), 0),
(807172, 'INV_LOG_H5_LOCK_001', 50001, 51003, 780003, 806030, 'BATCH_H5_INV_AVAILABLE_001', 3, 260, -40, 220, 'lock', 'LOCK_H5_PICK_001', 700009, 'H5拣货锁定流水', NOW(), NOW(), 0),
(807173, 'INV_LOG_H5_OUTBOUND_001', 50001, 51003, 780003, 806030, 'BATCH_H5_INV_AVAILABLE_001', 5, 220, -20, 200, 'outbound', 'OUT_H5_DONE_001', 700010, 'H5出库扣减流水', NOW(), NOW(), 0),
(807174, 'INV_LOG_H5_RETURN_001', 50001, 51006, 780006, 806031, 'BATCH_H5_INV_RETURN_001', 6, 0, 12, 12, 'return', 'RET_H5_WAIT_BACK_001', 700011, 'H5退货回仓流水', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_wave_batch
(id, wave_no, period_id, warehouse_id, line_id, delivery_date, required_qty, locked_qty, status, create_account_id, create_time, update_time, is_deleted)
VALUES
(807180, 'WAVE_H5_PICK_WAIT_001', 806041, 50001, 780201, CURDATE(), 88, 88, 20, 807001, NOW(), NOW(), 0),
(807181, 'WAVE_H5_PICK_DOING_001', 806041, 50001, 780201, CURDATE(), 96, 96, 30, 807001, NOW(), NOW(), 0),
(807182, 'WAVE_H5_PICK_DONE_001', 806041, 50001, 780201, CURDATE(), 104, 104, 40, 807001, NOW(), NOW(), 0),
(807183, 'WAVE_H5_PICK_DIFF_001', 806041, 50001, 780201, CURDATE(), 112, 112, 50, 807001, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_pick_task
(id, pick_no, wave_id, warehouse_id, line_id, assign_account_id, required_qty, actual_qty, shortage_qty, status, complete_time, create_time, update_time, is_deleted)
VALUES
(807190, 'PICK_H5_WAIT_001', 807180, 50001, 780201, 700009, 88, 0, 0, 10, NULL, NOW(), NOW(), 0),
(807191, 'PICK_H5_DOING_001', 807181, 50001, 780201, 700009, 96, 48, 0, 20, NULL, NOW(), NOW(), 0),
(807192, 'PICK_H5_DONE_001', 807182, 50001, 780201, 700009, 104, 104, 0, 30, NOW(), NOW(), NOW(), 0),
(807193, 'PICK_H5_DIFF_001', 807183, 50001, 780201, 700009, 112, 106, 6, 40, NOW(), NOW(), NOW(), 0);

INSERT IGNORE INTO wms_pick_item
(id, pick_id, sku_id, inventory_id, location_id, required_qty, actual_qty, shortage_qty, diff_reason, create_time, update_time, is_deleted)
VALUES
(807200, 807190, 806030, 807151, 780003, 88, 0, 0, NULL, NOW(), NOW(), 0),
(807201, 807191, 806030, 807151, 780003, 96, 48, 0, NULL, NOW(), NOW(), 0),
(807202, 807192, 806030, 807151, 780003, 104, 104, 0, NULL, NOW(), NOW(), 0),
(807203, 807193, 806030, 807151, 780003, 112, 106, 6, '履约缺货6件，按自提点记录', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_loading_order
(id, loading_no, wave_id, warehouse_id, line_id, driver_account_id, delivery_date, required_qty, actual_qty, status, load_account_id, load_time, outbound_account_id, outbound_time, create_time, update_time, is_deleted)
VALUES
(807210, 'LOAD_H5_WAIT_001', 807180, 50001, 780201, 700011, CURDATE(), 88, 0, 10, NULL, NULL, NULL, NULL, NOW(), NOW(), 0),
(807211, 'LOAD_H5_LOADED_WAIT_OUT_001', 807181, 50001, 780201, 700011, CURDATE(), 96, 96, 20, 700010, DATE_SUB(NOW(), INTERVAL 40 MINUTE), NULL, NULL, NOW(), NOW(), 0),
(807212, 'LOAD_H5_OUTBOUND_001', 807182, 50001, 780201, 700011, CURDATE(), 104, 104, 30, 700010, DATE_SUB(NOW(), INTERVAL 90 MINUTE), 807001, DATE_SUB(NOW(), INTERVAL 70 MINUTE), NOW(), NOW(), 0);

INSERT IGNORE INTO wms_loading_item
(id, loading_id, station_id, sku_id, required_qty, actual_qty, outbound_qty, create_time, update_time, is_deleted)
VALUES
(807220, 807210, 720001, 806030, 44, 0, 0, NOW(), NOW(), 0),
(807221, 807210, 720002, 806031, 44, 0, 0, NOW(), NOW(), 0),
(807222, 807211, 720001, 806030, 60, 60, 0, NOW(), NOW(), 0),
(807223, 807212, 720001, 806030, 64, 64, 64, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_outbound_order
(id, outbound_no, loading_id, warehouse_id, line_id, outbound_qty, status, outbound_account_id, outbound_time, idempotent_key, create_time, update_time, is_deleted)
VALUES
(807230, 'OUT_H5_WAIT_001', 807211, 50001, 780201, 0, 10, 807001, NOW(), 'idem_h5_out_wait_001', NOW(), NOW(), 0),
(807231, 'OUT_H5_DONE_001', 807212, 50001, 780201, 104, 20, 807001, DATE_SUB(NOW(), INTERVAL 70 MINUTE), 'idem_h5_out_done_001', NOW(), NOW(), 0),
(807232, 'OUT_H5_CANCEL_001', 807210, 50001, 780201, 0, 30, 807001, NOW(), 'idem_h5_out_cancel_001', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_delivery_order
(id, delivery_no, outbound_id, loading_id, warehouse_id, line_id, driver_account_id, delivery_date, status, start_time, complete_time, create_time, update_time, is_deleted)
VALUES
(807240, 'DLV_H5_WAIT_START_001', 807230, 807211, 50001, 780201, 700011, CURDATE(), 10, NULL, NULL, NOW(), NOW(), 0),
(807241, 'DLV_H5_RUNNING_001', 807231, 807212, 50001, 780201, 700011, CURDATE(), 20, DATE_SUB(NOW(), INTERVAL 50 MINUTE), NULL, NOW(), NOW(), 0),
(807242, 'DLV_H5_DONE_001', 807231, 807212, 50001, 780201, 700011, CURDATE(), 30, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 45 MINUTE), NOW(), NOW(), 0),
(807243, 'DLV_H5_FAILED_001', 807231, 807212, 50001, 780201, 700011, CURDATE(), 40, DATE_SUB(NOW(), INTERVAL 2 HOUR), NULL, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_delivery_station
(id, delivery_id, station_id, delivery_sort, status, arrive_time, complete_time, remark, create_time, update_time, is_deleted)
VALUES
(807250, 807240, 720001, 1, 10, NULL, NULL, 'H5待发车配送点', NOW(), NOW(), 0),
(807251, 807241, 720001, 1, 20, DATE_SUB(NOW(), INTERVAL 25 MINUTE), NULL, 'H5已到达待交接', NOW(), NOW(), 0),
(807252, 807242, 720001, 1, 30, DATE_SUB(NOW(), INTERVAL 90 MINUTE), DATE_SUB(NOW(), INTERVAL 45 MINUTE), 'H5完成配送', NOW(), NOW(), 0),
(807253, 807243, 720002, 2, 40, NULL, NULL, 'H5无法送达，待回仓', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_driver_sign_record
(id, sign_no, delivery_id, station_id, driver_account_id, sign_type, longitude, latitude, remark, create_time, update_time, is_deleted)
VALUES
(807260, 'SIGN_H5_ARRIVE_001', 807241, 720001, 700011, 1, 113.320800, 23.120800, 'H5司机到达自提点', NOW(), NOW(), 0),
(807261, 'SIGN_H5_DONE_001', 807242, 720001, 700011, 2, 113.320900, 23.120900, 'H5司机完成配送', NOW(), NOW(), 0),
(807262, 'SIGN_H5_FAIL_001', 807243, 720002, 700011, 3, 113.331000, 23.131000, 'H5无法送达', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_return_handover
(id, return_no, delivery_id, station_id, sku_id, warehouse_id, return_qty, reason_type, driver_account_id, supervisor_account_id, status, confirm_time, remark, create_time, update_time, is_deleted)
VALUES
(807270, 'RET_H5_WAIT_BACK_001', 807243, 720002, 806030, 50001, 4, 1, 700011, NULL, 10, NULL, 'H5待回仓确认', NOW(), NOW(), 0),
(807271, 'RET_H5_CONFIRMED_001', 807242, 720001, 806031, 50001, 2, 2, 700011, 807001, 20, NOW(), 'H5已回仓确认', NOW(), NOW(), 0),
(807272, 'RET_H5_MANAGER_TODO_001', 807243, 720002, 806030, 50001, 1, 3, 700011, NULL, 30, NULL, 'H5待主管处理', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_stocktake_order
(id, stocktake_no, warehouse_id, sku_id, book_qty, actual_qty, diff_qty, status, create_account_id, audit_account_id, create_time, update_time, is_deleted)
VALUES
(807280, 'STK_H5_DRAFT_001', 50001, 806030, 260, 0, 0, 10, 807001, NULL, NOW(), NOW(), 0),
(807281, 'STK_H5_WAIT_AUDIT_001', 50001, 806030, 260, 258, -2, 20, 807001, NULL, NOW(), NOW(), 0),
(807282, 'STK_H5_ADJUSTED_001', 50001, 806031, 90, 90, 0, 30, 807001, 807001, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_operation_log
(id, log_no, warehouse_id, account_id, role_code, module_code, action_code, biz_type, biz_no, before_status, after_status, reason, result_status, create_time, update_time, is_deleted)
VALUES
(807290, 'WMS_LOG_H5_RECEIVE_001', 50001, 700008, 'wms_receiver', 'wms_inbound', 'receive', 'inbound', 'INB_H5_RECEIVED_PUTAWAY_001', 20, 40, 'H5确认收货', 1, NOW(), NOW(), 0),
(807291, 'WMS_LOG_H5_PICK_DIFF_001', 50001, 700009, 'wms_picker', 'wms_picking', 'pick_shortage', 'pick', 'PICK_H5_DIFF_001', 20, 40, 'H5记录履约缺货', 1, NOW(), NOW(), 0),
(807292, 'WMS_LOG_H5_LOAD_001', 50001, 700010, 'wms_loader', 'wms_loading', 'load', 'loading', 'LOAD_H5_LOADED_WAIT_OUT_001', 10, 20, 'H5装车完成', 1, NOW(), NOW(), 0),
(807293, 'WMS_LOG_H5_DRIVER_FAIL_001', 50001, 700011, 'wms_driver', 'wms_delivery', 'delivery_failed', 'delivery', 'DLV_H5_FAILED_001', 20, 40, 'H5无法送达', 2, NOW(), NOW(), 0),
(807294, 'WMS_LOG_H5_MANAGER_STOCK_001', 50001, 807001, 'wms_supervisor', 'wms_inventory', 'stocktake', 'stocktake', 'STK_H5_WAIT_AUDIT_001', 10, 20, 'H5主管盘点待确认', 1, NOW(), NOW(), 0);

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Warehouse H5 extra list data: ensure each WMS H5 page has at least 6 rows
-- Date: 2026-06-30
-- ----------------------------
SET FOREIGN_KEY_CHECKS = 0;

INSERT IGNORE INTO wms_warehouse
(id, warehouse_code, warehouse_name, city_id, warehouse_type, manager_account_id, status, create_time, update_time, is_deleted)
VALUES
(808001, 'WH_H5_MORE_EAST', '广州东区前置仓', 440100, 2, 807001, 1, NOW(), NOW(), 0),
(808002, 'WH_H5_MORE_WEST', '广州西区前置仓', 440100, 2, 807001, 1, NOW(), NOW(), 0),
(808003, 'WH_H5_MORE_COLD', '广州冷链仓', 440100, 1, 807001, 1, NOW(), NOW(), 0),
(808004, 'WH_H5_MORE_RETURN', '广州退货整理仓', 440100, 2, 807001, 1, NOW(), NOW(), 0),
(808005, 'WH_H5_MORE_BACKUP', '广州备用仓', 440100, 1, 807001, 2, NOW(), NOW(), 0),
(808006, 'WH_H5_MORE_NORTH', '广州北区前置仓', 440100, 2, 807001, 1, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_zone
(id, warehouse_id, zone_code, zone_name, zone_type, status, create_time, update_time, is_deleted)
VALUES
(808010, 808001, 'H5_MORE_RECEIVE', 'H5补充收货区', 1, 1, NOW(), NOW(), 0),
(808011, 808002, 'H5_MORE_PUTAWAY', 'H5补充待上架区', 2, 1, NOW(), NOW(), 0),
(808012, 808003, 'H5_MORE_COLD_SALE', 'H5补充冷链可售区', 3, 1, NOW(), NOW(), 0),
(808013, 808004, 'H5_MORE_RETURN', 'H5补充回仓区', 6, 1, NOW(), NOW(), 0),
(808014, 808005, 'H5_MORE_BACKUP', 'H5补充备用区', 3, 2, NOW(), NOW(), 0),
(808015, 808006, 'H5_MORE_NORTH_PICK', 'H5补充北区拣货区', 4, 1, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_location
(id, warehouse_id, zone_id, location_code, location_name, capacity_qty, status, create_time, update_time, is_deleted)
VALUES
(808020, 808001, 808010, 'H5-RCV-B-01', 'H5补充收货B01', 800, 1, NOW(), NOW(), 0),
(808021, 808002, 808011, 'H5-PUT-B-01', 'H5补充待上架B01', 800, 1, NOW(), NOW(), 0),
(808022, 808003, 808012, 'H5-COLD-B-01', 'H5补充冷链B01', 1200, 1, NOW(), NOW(), 0),
(808023, 808004, 808013, 'H5-RET-B-01', 'H5补充回仓B01', 600, 1, NOW(), NOW(), 0),
(808024, 808005, 808014, 'H5-BACK-B-01', 'H5补充备用B01', 500, 2, NOW(), NOW(), 0),
(808025, 808006, 808015, 'H5-PICK-B-01', 'H5补充北区拣货B01', 700, 1, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_driver
(id, driver_no, account_id, driver_name, driver_mobile, vehicle_no, status, create_time, update_time, is_deleted)
VALUES
(808030, 'DRV_H5_MORE_001', 700011, 'H5补充司机甲', '13800008101', '粤A-H501', 1, NOW(), NOW(), 0),
(808031, 'DRV_H5_MORE_002', NULL, 'H5补充司机乙', '13800008102', '粤A-H502', 1, NOW(), NOW(), 0),
(808032, 'DRV_H5_MORE_003', NULL, 'H5补充司机丙', '13800008103', '粤A-H503', 1, NOW(), NOW(), 0),
(808033, 'DRV_H5_MORE_004', NULL, 'H5补充司机丁', '13800008104', '粤A-H504', 1, NOW(), NOW(), 0),
(808034, 'DRV_H5_MORE_005', NULL, 'H5补充司机戊', '13800008105', '粤A-H505', 1, NOW(), NOW(), 0),
(808035, 'DRV_H5_MORE_006', NULL, 'H5补充司机己', '13800008106', '粤A-H506', 2, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_line
(id, warehouse_id, line_code, line_name, default_driver_account_id, status, create_time, update_time, is_deleted)
VALUES
(808040, 50001, 'LINE_H5_MORE_A', 'H5补充广州A线', 700011, 1, NOW(), NOW(), 0),
(808041, 50001, 'LINE_H5_MORE_B', 'H5补充广州B线', 700011, 1, NOW(), NOW(), 0),
(808042, 50001, 'LINE_H5_MORE_C', 'H5补充广州C线', 700011, 1, NOW(), NOW(), 0),
(808043, 50001, 'LINE_H5_MORE_D', 'H5补充广州D线', 700011, 1, NOW(), NOW(), 0),
(808044, 50001, 'LINE_H5_MORE_E', 'H5补充广州E线', 700011, 1, NOW(), NOW(), 0),
(808045, 50001, 'LINE_H5_MORE_F', 'H5补充广州F线', 700011, 2, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_line_station
(id, line_id, station_id, delivery_sort, status, create_time, update_time, is_deleted)
VALUES
(808050, 808040, 720001, 1, 1, NOW(), NOW(), 0),
(808051, 808040, 720002, 2, 1, NOW(), NOW(), 0),
(808052, 808041, 720001, 1, 1, NOW(), NOW(), 0),
(808053, 808042, 720002, 1, 1, NOW(), NOW(), 0),
(808054, 808043, 720003, 1, 1, NOW(), NOW(), 0),
(808055, 808044, 720003, 1, 1, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_inbound_order
(id, inbound_no, source_type, source_no, purchase_id, delivery_id, supplier_id, warehouse_id, status, receive_account_id, receive_time, create_time, update_time, is_deleted)
VALUES
(808100, 'INB_H5_MORE_WAIT_001', 1, 'PDEL_H5_MORE_WAIT_001', 806302, 806320, 610001, 50001, 10, NULL, NULL, NOW(), NOW(), 0),
(808101, 'INB_H5_MORE_RECEIVE_001', 1, 'PDEL_H5_MORE_RECEIVE_001', 806303, 806321, 610001, 50001, 20, 700008, NULL, NOW(), NOW(), 0),
(808102, 'INB_H5_MORE_DOING_001', 1, 'PDEL_H5_MORE_DOING_001', 806303, 806322, 610001, 50001, 30, 700008, NULL, NOW(), NOW(), 0),
(808103, 'INB_H5_MORE_QC_001', 1, 'PDEL_H5_MORE_QC_001', 806304, 806323, 610001, 50001, 50, 700008, DATE_SUB(NOW(), INTERVAL 35 MINUTE), NOW(), NOW(), 0);

INSERT IGNORE INTO wms_inbound_item
(id, inbound_id, sku_id, supplier_id, expected_qty, received_qty, rejected_qty, damaged_qty, batch_no, production_date, shelf_life_days, diff_reason, create_time, update_time, is_deleted)
VALUES
(808110, 808100, 806030, 610001, 66, 0, 0, 0, 'BATCH_H5_MORE_INB_001', CURDATE(), 10, NULL, NOW(), NOW(), 0),
(808111, 808101, 806031, 610001, 72, 0, 0, 0, 'BATCH_H5_MORE_INB_002', CURDATE(), 5, NULL, NOW(), NOW(), 0),
(808112, 808102, 806030, 610001, 84, 50, 0, 0, 'BATCH_H5_MORE_INB_003', CURDATE(), 10, '收货中待复核数量', NOW(), NOW(), 0),
(808113, 808103, 806031, 610001, 96, 88, 4, 4, 'BATCH_H5_MORE_INB_004', CURDATE(), 5, '质检破损待确认', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_receive_record
(id, receive_no, inbound_id, inbound_item_id, sku_id, receive_qty, reject_qty, diff_qty, receive_account_id, quality_status, remark, create_time, update_time, is_deleted)
VALUES
(808120, 'RCV_H5_MORE_WAIT_001', 808101, 808111, 806031, 0, 0, 72, 700008, 4, '待收货补充样例', NOW(), NOW(), 0),
(808121, 'RCV_H5_MORE_DOING_001', 808102, 808112, 806030, 50, 0, 34, 700008, 4, '收货中补充样例', NOW(), NOW(), 0),
(808122, 'RCV_H5_MORE_QC_001', 808103, 808113, 806031, 88, 4, 8, 700008, 3, '质检异常补充样例', NOW(), NOW(), 0),
(808123, 'RCV_H5_MORE_PASS_001', 807103, 807113, 806030, 120, 0, 0, 700008, 1, '合格收货补充样例', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_quality_file
(id, quality_no, inbound_id, sku_id, quality_result, file_asset_id, remark, create_time, update_time, is_deleted)
VALUES
(808130, 'QUAL_H5_MORE_PASS_001', 808101, 806031, 1, 710204, 'H5补充合格凭证1', NOW(), NOW(), 0),
(808131, 'QUAL_H5_MORE_WAIT_001', 808102, 806030, 2, 710204, 'H5补充待确认凭证', NOW(), NOW(), 0),
(808132, 'QUAL_H5_MORE_REJECT_001', 808103, 806031, 3, 710204, 'H5补充拒收凭证', NOW(), NOW(), 0),
(808133, 'QUAL_H5_MORE_PASS_002', 807102, 806030, 1, 710204, 'H5补充合格凭证2', NOW(), NOW(), 0),
(808134, 'QUAL_H5_MORE_EXCEPTION_002', 807104, 806031, 3, 710204, 'H5补充异常凭证2', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_putaway_task
(id, putaway_no, inbound_id, sku_id, warehouse_id, from_zone_id, target_zone_id, target_location_id, putaway_qty, actual_qty, status, assign_account_id, complete_time, create_time, update_time, is_deleted)
VALUES
(808140, 'PUT_H5_MORE_WAIT_001', 808102, 806030, 50001, 51002, 51003, 780003, 84, 0, 10, 807001, NULL, NOW(), NOW(), 0),
(808141, 'PUT_H5_MORE_DOING_001', 808103, 806031, 50001, 51002, 51003, 780003, 88, 32, 20, 807001, NULL, NOW(), NOW(), 0),
(808142, 'PUT_H5_MORE_DONE_001', 807103, 806030, 50001, 51002, 51003, 780003, 64, 64, 30, 807001, NOW(), NOW(), NOW(), 0),
(808143, 'PUT_H5_MORE_CLOSE_001', 807104, 806031, 50001, 51002, 51003, 780003, 12, 0, 40, 807001, NULL, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_inventory
(id, warehouse_id, zone_id, location_id, sku_id, supplier_id, batch_no, production_date, shelf_life_days, in_stock_qty, locked_qty, available_qty, frozen_qty, inventory_status, version, create_time, update_time, is_deleted)
VALUES
(808150, 50001, 51003, 780003, 806030, 610001, 'BATCH_H5_MORE_INV_001', CURDATE(), 10, 180, 20, 160, 0, 20, 1, NOW(), NOW(), 0),
(808151, 50001, 51003, 780003, 806031, 610001, 'BATCH_H5_MORE_INV_002', CURDATE(), 5, 96, 16, 80, 0, 20, 1, NOW(), NOW(), 0),
(808152, 50001, 51004, 780004, 806030, 610001, 'BATCH_H5_MORE_INV_003', CURDATE(), 10, 54, 20, 34, 0, 30, 1, NOW(), NOW(), 0),
(808153, 50001, 51001, 780001, 806031, 610001, 'BATCH_H5_MORE_INV_004', CURDATE(), 5, 22, 0, 0, 22, 50, 1, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_inventory_lock
(id, lock_no, warehouse_id, inventory_id, sku_id, biz_type, biz_no, lock_qty, release_qty, lock_status, create_time, update_time, is_deleted)
VALUES
(808160, 'LOCK_H5_MORE_PICK_001', 50001, 808150, 806030, 2, 'WAVE_H5_MORE_WAIT_001', 24, 0, 10, NOW(), NOW(), 0),
(808161, 'LOCK_H5_MORE_PICK_002', 50001, 808151, 806031, 2, 'WAVE_H5_MORE_DOING_001', 16, 0, 10, NOW(), NOW(), 0),
(808162, 'LOCK_H5_MORE_RELEASE_001', 50001, 808150, 806030, 1, 'ORD_H5_MORE_CANCEL_001', 8, 8, 20, NOW(), NOW(), 0),
(808163, 'LOCK_H5_MORE_OUT_001', 50001, 808152, 806030, 2, 'WAVE_H5_MORE_DONE_001', 20, 0, 30, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_inventory_log
(id, log_no, warehouse_id, zone_id, location_id, sku_id, batch_no, change_type, before_qty, change_qty, after_qty, biz_type, biz_no, operator_account_id, remark, create_time, update_time, is_deleted)
VALUES
(808170, 'INV_LOG_H5_MORE_IN_001', 50001, 51002, 780002, 806030, 'BATCH_H5_MORE_INB_003', 1, 0, 84, 84, 'inbound', 'INB_H5_MORE_DOING_001', 700008, 'H5补充入库流水', NOW(), NOW(), 0),
(808171, 'INV_LOG_H5_MORE_PUT_001', 50001, 51003, 780003, 806030, 'BATCH_H5_MORE_INV_001', 2, 84, 96, 180, 'putaway', 'PUT_H5_MORE_DONE_001', 807001, 'H5补充上架流水', NOW(), NOW(), 0),
(808172, 'INV_LOG_H5_MORE_LOCK_001', 50001, 51003, 780003, 806031, 'BATCH_H5_MORE_INV_002', 3, 96, -16, 80, 'lock', 'LOCK_H5_MORE_PICK_002', 700009, 'H5补充锁定流水', NOW(), NOW(), 0),
(808173, 'INV_LOG_H5_MORE_CONFIRM_001', 50001, 51001, 780001, 806031, 'BATCH_H5_MORE_INV_004', 6, 0, 22, 22, 'return', 'RET_H5_MORE_WAIT_001', 700011, 'H5补充待确认库存流水', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_wave_batch
(id, wave_no, period_id, warehouse_id, line_id, delivery_date, required_qty, locked_qty, status, create_account_id, create_time, update_time, is_deleted)
VALUES
(808180, 'WAVE_H5_MORE_WAIT_001', 806041, 50001, 808040, CURDATE(), 76, 76, 20, 807001, NOW(), NOW(), 0),
(808181, 'WAVE_H5_MORE_DOING_001', 806041, 50001, 808041, CURDATE(), 88, 88, 30, 807001, NOW(), NOW(), 0),
(808182, 'WAVE_H5_MORE_DONE_001', 806041, 50001, 808042, CURDATE(), 92, 92, 40, 807001, NOW(), NOW(), 0),
(808183, 'WAVE_H5_MORE_DIFF_001', 806041, 50001, 808043, CURDATE(), 108, 108, 50, 807001, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_pick_task
(id, pick_no, wave_id, warehouse_id, line_id, assign_account_id, required_qty, actual_qty, shortage_qty, status, complete_time, create_time, update_time, is_deleted)
VALUES
(808190, 'PICK_H5_MORE_WAIT_001', 808180, 50001, 808040, 700009, 76, 0, 0, 10, NULL, NOW(), NOW(), 0),
(808191, 'PICK_H5_MORE_DOING_001', 808181, 50001, 808041, 700009, 88, 40, 0, 20, NULL, NOW(), NOW(), 0),
(808192, 'PICK_H5_MORE_DONE_001', 808182, 50001, 808042, 700009, 92, 92, 0, 30, NOW(), NOW(), NOW(), 0),
(808193, 'PICK_H5_MORE_DIFF_001', 808183, 50001, 808043, 700009, 108, 100, 8, 40, NOW(), NOW(), NOW(), 0);

INSERT IGNORE INTO wms_pick_item
(id, pick_id, sku_id, inventory_id, location_id, required_qty, actual_qty, shortage_qty, diff_reason, create_time, update_time, is_deleted)
VALUES
(808200, 808190, 806030, 808150, 780003, 76, 0, 0, NULL, NOW(), NOW(), 0),
(808201, 808191, 806031, 808151, 780003, 88, 40, 0, NULL, NOW(), NOW(), 0),
(808202, 808192, 806030, 808152, 780004, 92, 92, 0, NULL, NOW(), NOW(), 0),
(808203, 808193, 806031, 808151, 780003, 108, 100, 8, 'H5补充履约缺货8件', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_loading_order
(id, loading_no, wave_id, warehouse_id, line_id, driver_account_id, delivery_date, required_qty, actual_qty, status, load_account_id, load_time, outbound_account_id, outbound_time, create_time, update_time, is_deleted)
VALUES
(808210, 'LOAD_H5_MORE_WAIT_001', 808180, 50001, 808040, 700011, CURDATE(), 76, 0, 10, NULL, NULL, NULL, NULL, NOW(), NOW(), 0),
(808211, 'LOAD_H5_MORE_READY_001', 808181, 50001, 808041, 700011, CURDATE(), 88, 88, 20, 700010, DATE_SUB(NOW(), INTERVAL 20 MINUTE), NULL, NULL, NOW(), NOW(), 0),
(808212, 'LOAD_H5_MORE_OUT_001', 808182, 50001, 808042, 700011, CURDATE(), 92, 92, 30, 700010, DATE_SUB(NOW(), INTERVAL 60 MINUTE), 807001, DATE_SUB(NOW(), INTERVAL 45 MINUTE), NOW(), NOW(), 0),
(808213, 'LOAD_H5_MORE_DIFF_001', 808183, 50001, 808043, 700011, CURDATE(), 108, 100, 20, 700010, DATE_SUB(NOW(), INTERVAL 35 MINUTE), NULL, NULL, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_loading_item
(id, loading_id, station_id, sku_id, required_qty, actual_qty, outbound_qty, create_time, update_time, is_deleted)
VALUES
(808220, 808210, 720001, 806030, 38, 0, 0, NOW(), NOW(), 0),
(808221, 808211, 720002, 806031, 44, 44, 0, NOW(), NOW(), 0),
(808222, 808212, 720001, 806030, 46, 46, 46, NOW(), NOW(), 0),
(808223, 808213, 720003, 806031, 54, 50, 0, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_outbound_order
(id, outbound_no, loading_id, warehouse_id, line_id, outbound_qty, status, outbound_account_id, outbound_time, idempotent_key, create_time, update_time, is_deleted)
VALUES
(808230, 'OUT_H5_MORE_WAIT_001', 808211, 50001, 808041, 0, 10, 807001, NOW(), 'idem_h5_more_out_wait_001', NOW(), NOW(), 0),
(808231, 'OUT_H5_MORE_DONE_001', 808212, 50001, 808042, 92, 20, 807001, DATE_SUB(NOW(), INTERVAL 45 MINUTE), 'idem_h5_more_out_done_001', NOW(), NOW(), 0),
(808232, 'OUT_H5_MORE_CANCEL_001', 808210, 50001, 808040, 0, 30, 807001, NOW(), 'idem_h5_more_out_cancel_001', NOW(), NOW(), 0),
(808233, 'OUT_H5_MORE_READY_001', 808213, 50001, 808043, 0, 10, 807001, NOW(), 'idem_h5_more_out_ready_001', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_delivery_order
(id, delivery_no, outbound_id, loading_id, warehouse_id, line_id, driver_account_id, delivery_date, status, start_time, complete_time, create_time, update_time, is_deleted)
VALUES
(808240, 'DLV_H5_MORE_WAIT_001', 808230, 808211, 50001, 808041, 700011, CURDATE(), 10, NULL, NULL, NOW(), NOW(), 0),
(808241, 'DLV_H5_MORE_RUN_001', 808231, 808212, 50001, 808042, 700011, CURDATE(), 20, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NULL, NOW(), NOW(), 0),
(808242, 'DLV_H5_MORE_DONE_001', 808231, 808212, 50001, 808042, 700011, CURDATE(), 30, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW(), 0),
(808243, 'DLV_H5_MORE_FAIL_001', 808233, 808213, 50001, 808043, 700011, CURDATE(), 40, DATE_SUB(NOW(), INTERVAL 80 MINUTE), NULL, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_delivery_station
(id, delivery_id, station_id, delivery_sort, status, arrive_time, complete_time, remark, create_time, update_time, is_deleted)
VALUES
(808250, 808240, 720001, 1, 10, NULL, NULL, 'H5补充待发车配送点', NOW(), NOW(), 0),
(808251, 808241, 720001, 1, 20, DATE_SUB(NOW(), INTERVAL 15 MINUTE), NULL, 'H5补充已到达配送点', NOW(), NOW(), 0),
(808252, 808242, 720002, 2, 30, DATE_SUB(NOW(), INTERVAL 70 MINUTE), DATE_SUB(NOW(), INTERVAL 30 MINUTE), 'H5补充完成配送点', NOW(), NOW(), 0),
(808253, 808243, 720003, 1, 40, DATE_SUB(NOW(), INTERVAL 50 MINUTE), NULL, 'H5补充无法送达配送点', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_driver_sign_record
(id, sign_no, delivery_id, station_id, driver_account_id, sign_type, longitude, latitude, remark, create_time, update_time, is_deleted)
VALUES
(808260, 'SIGN_H5_MORE_WAIT_001', 808241, 720001, 700011, 1, 113.322100, 23.121100, 'H5补充司机到达', NOW(), NOW(), 0),
(808261, 'SIGN_H5_MORE_DONE_001', 808242, 720002, 700011, 2, 113.322200, 23.121200, 'H5补充司机完成', NOW(), NOW(), 0),
(808262, 'SIGN_H5_MORE_FAIL_001', 808243, 720003, 700011, 3, 113.322300, 23.121300, 'H5补充无法送达', NOW(), NOW(), 0),
(808263, 'SIGN_H5_MORE_BACK_001', 807243, 720002, 700011, 3, 113.322400, 23.121400, 'H5补充回仓签到', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_return_handover
(id, return_no, delivery_id, station_id, sku_id, warehouse_id, return_qty, reason_type, driver_account_id, supervisor_account_id, status, confirm_time, remark, create_time, update_time, is_deleted)
VALUES
(808270, 'RET_H5_MORE_WAIT_001', 808243, 720003, 806031, 50001, 3, 1, 700011, NULL, 10, NULL, 'H5补充待回仓确认', NOW(), NOW(), 0),
(808271, 'RET_H5_MORE_DONE_001', 808242, 720002, 806030, 50001, 2, 2, 700011, 807001, 20, NOW(), 'H5补充已回仓确认', NOW(), NOW(), 0),
(808272, 'RET_H5_MORE_MANAGER_001', 808243, 720003, 806031, 50001, 5, 3, 700011, NULL, 30, NULL, 'H5补充待主管处理', NOW(), NOW(), 0),
(808273, 'RET_H5_MORE_CLOSE_001', 807242, 720001, 806030, 50001, 1, 1, 700011, 807001, 40, NOW(), 'H5补充关闭回仓单', NOW(), NOW(), 0);

INSERT IGNORE INTO wms_stocktake_order
(id, stocktake_no, warehouse_id, sku_id, book_qty, actual_qty, diff_qty, status, create_account_id, audit_account_id, create_time, update_time, is_deleted)
VALUES
(808280, 'STK_H5_MORE_DRAFT_001', 50001, 806030, 180, 0, 0, 10, 807001, NULL, NOW(), NOW(), 0),
(808281, 'STK_H5_MORE_WAIT_001', 50001, 806031, 96, 94, -2, 20, 807001, NULL, NOW(), NOW(), 0),
(808282, 'STK_H5_MORE_DONE_001', 50001, 806030, 180, 180, 0, 30, 807001, 807001, NOW(), NOW(), 0),
(808283, 'STK_H5_MORE_CLOSE_001', 50001, 806031, 96, 0, 0, 40, 807001, 807001, NOW(), NOW(), 0);

INSERT IGNORE INTO wms_operation_log
(id, log_no, warehouse_id, account_id, role_code, module_code, action_code, biz_type, biz_no, before_status, after_status, reason, result_status, create_time, update_time, is_deleted)
VALUES
(808290, 'WMS_LOG_H5_MORE_RECEIVE_001', 50001, 700008, 'wms_receiver', 'wms_inbound', 'receive', 'inbound', 'INB_H5_MORE_DOING_001', 20, 30, 'H5补充收货操作', 1, NOW(), NOW(), 0),
(808291, 'WMS_LOG_H5_MORE_PUT_001', 50001, 807001, 'wms_supervisor', 'wms_putaway', 'putaway', 'putaway', 'PUT_H5_MORE_DONE_001', 20, 30, 'H5补充上架操作', 1, NOW(), NOW(), 0),
(808292, 'WMS_LOG_H5_MORE_PICK_001', 50001, 700009, 'wms_picker', 'wms_picking', 'pick_shortage', 'pick', 'PICK_H5_MORE_DIFF_001', 20, 40, 'H5补充拣货差异', 1, NOW(), NOW(), 0),
(808293, 'WMS_LOG_H5_MORE_DRIVER_001', 50001, 700011, 'wms_driver', 'wms_delivery', 'delivery_failed', 'delivery', 'DLV_H5_MORE_FAIL_001', 20, 40, 'H5补充无法送达', 2, NOW(), NOW(), 0);

SET FOREIGN_KEY_CHECKS = 1;
