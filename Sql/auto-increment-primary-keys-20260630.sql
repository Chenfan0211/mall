-- Mall business schema primary key AUTO_INCREMENT migration
-- Generated for test environment on 2026-06-30.
-- Scope: mall schema single-column numeric primary keys only.
-- Backup required before execution.

SET SESSION sql_log_bin = 0;
SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE `mall`.`afs_after_sale` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`afs_after_sale`);
SET @sql := CONCAT('ALTER TABLE `mall`.`afs_after_sale` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`afs_after_sale_item` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`afs_after_sale_item`);
SET @sql := CONCAT('ALTER TABLE `mall`.`afs_after_sale_item` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`afs_audit_log` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`afs_audit_log`);
SET @sql := CONCAT('ALTER TABLE `mall`.`afs_audit_log` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`afs_return_record` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`afs_return_record`);
SET @sql := CONCAT('ALTER TABLE `mall`.`afs_return_record` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`fin_account` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`fin_account`);
SET @sql := CONCAT('ALTER TABLE `mall`.`fin_account` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`fin_account_flow` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`fin_account_flow`);
SET @sql := CONCAT('ALTER TABLE `mall`.`fin_account_flow` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`fin_commission_detail` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`fin_commission_detail`);
SET @sql := CONCAT('ALTER TABLE `mall`.`fin_commission_detail` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`fin_deposit_record` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`fin_deposit_record`);
SET @sql := CONCAT('ALTER TABLE `mall`.`fin_deposit_record` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`fin_reconciliation_batch` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`fin_reconciliation_batch`);
SET @sql := CONCAT('ALTER TABLE `mall`.`fin_reconciliation_batch` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`fin_split_flow` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`fin_split_flow`);
SET @sql := CONCAT('ALTER TABLE `mall`.`fin_split_flow` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`fin_supplier_settlement` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`fin_supplier_settlement`);
SET @sql := CONCAT('ALTER TABLE `mall`.`fin_supplier_settlement` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`fin_withdraw_apply` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`fin_withdraw_apply`);
SET @sql := CONCAT('ALTER TABLE `mall`.`fin_withdraw_apply` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`msg_record` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`msg_record`);
SET @sql := CONCAT('ALTER TABLE `mall`.`msg_record` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`msg_template` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`msg_template`);
SET @sql := CONCAT('ALTER TABLE `mall`.`msg_template` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`op_exception_record` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`op_exception_record`);
SET @sql := CONCAT('ALTER TABLE `mall`.`op_exception_record` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`op_todo` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`op_todo`);
SET @sql := CONCAT('ALTER TABLE `mall`.`op_todo` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`ord_cart` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`ord_cart`);
SET @sql := CONCAT('ALTER TABLE `mall`.`ord_cart` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`ord_fulfillment_track` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`ord_fulfillment_track`);
SET @sql := CONCAT('ALTER TABLE `mall`.`ord_fulfillment_track` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`ord_order` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`ord_order`);
SET @sql := CONCAT('ALTER TABLE `mall`.`ord_order` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`ord_order_item` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`ord_order_item`);
SET @sql := CONCAT('ALTER TABLE `mall`.`ord_order_item` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`ord_order_status_log` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`ord_order_status_log`);
SET @sql := CONCAT('ALTER TABLE `mall`.`ord_order_status_log` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`ord_replenish_order` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`ord_replenish_order`);
SET @sql := CONCAT('ALTER TABLE `mall`.`ord_replenish_order` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`ord_stockout_record` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`ord_stockout_record`);
SET @sql := CONCAT('ALTER TABLE `mall`.`ord_stockout_record` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`pay_fund_flow` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`pay_fund_flow`);
SET @sql := CONCAT('ALTER TABLE `mall`.`pay_fund_flow` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`pay_refund_trade` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`pay_refund_trade`);
SET @sql := CONCAT('ALTER TABLE `mall`.`pay_refund_trade` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`pay_trade` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`pay_trade`);
SET @sql := CONCAT('ALTER TABLE `mall`.`pay_trade` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`prd_category` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`prd_category`);
SET @sql := CONCAT('ALTER TABLE `mall`.`prd_category` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`prd_product` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`prd_product`);
SET @sql := CONCAT('ALTER TABLE `mall`.`prd_product` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`prd_rank_tag` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`prd_rank_tag`);
SET @sql := CONCAT('ALTER TABLE `mall`.`prd_rank_tag` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`prd_sku` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`prd_sku`);
SET @sql := CONCAT('ALTER TABLE `mall`.`prd_sku` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`prd_sku_stock_spec` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`prd_sku_stock_spec`);
SET @sql := CONCAT('ALTER TABLE `mall`.`prd_sku_stock_spec` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`prd_stock_spec` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`prd_stock_spec`);
SET @sql := CONCAT('ALTER TABLE `mall`.`prd_stock_spec` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`pur_delivery_item` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`pur_delivery_item`);
SET @sql := CONCAT('ALTER TABLE `mall`.`pur_delivery_item` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`pur_delivery_order` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`pur_delivery_order`);
SET @sql := CONCAT('ALTER TABLE `mall`.`pur_delivery_order` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`pur_purchase_audit_log` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`pur_purchase_audit_log`);
SET @sql := CONCAT('ALTER TABLE `mall`.`pur_purchase_audit_log` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`pur_purchase_item` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`pur_purchase_item`);
SET @sql := CONCAT('ALTER TABLE `mall`.`pur_purchase_item` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`pur_purchase_order` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`pur_purchase_order`);
SET @sql := CONCAT('ALTER TABLE `mall`.`pur_purchase_order` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`pur_purchase_source_log` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`pur_purchase_source_log`);
SET @sql := CONCAT('ALTER TABLE `mall`.`pur_purchase_source_log` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`rpt_export_task` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`rpt_export_task`);
SET @sql := CONCAT('ALTER TABLE `mall`.`rpt_export_task` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`rpt_snapshot` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`rpt_snapshot`);
SET @sql := CONCAT('ALTER TABLE `mall`.`rpt_snapshot` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sale_period_snapshot` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sale_period_snapshot`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sale_period_snapshot` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sale_publish_period` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sale_publish_period`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sale_publish_period` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sale_publish_scope` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sale_publish_scope`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sale_publish_scope` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sale_publish_sku` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sale_publish_sku`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sale_publish_sku` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`search_suggest_word` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`search_suggest_word`);
SET @sql := CONCAT('ALTER TABLE `mall`.`search_suggest_word` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sup_contract` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sup_contract`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sup_contract` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sup_qualification` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sup_qualification`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sup_qualification` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sup_supplier` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sup_supplier`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sup_supplier` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sup_supplier_contact` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sup_supplier_contact`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sup_supplier_contact` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_account` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_account`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_account` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_account_data_scope` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_account_data_scope`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_account_data_scope` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_account_role` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_account_role`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_account_role` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_account_subject` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_account_subject`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_account_subject` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_city` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_city`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_city` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_config` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_config`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_config` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_data_scope` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_data_scope`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_data_scope` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_dict_item` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_dict_item`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_dict_item` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_dict_type` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_dict_type`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_dict_type` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_export_log` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_export_log`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_export_log` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_file_asset` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_file_asset`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_file_asset` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_login_log` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_login_log`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_login_log` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_menu` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_menu`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_menu` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_operation_log` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_operation_log`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_operation_log` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_region` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_region`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_region` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_role` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_role`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_role` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_role_button` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_role_button`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_role_button` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_role_menu` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_role_menu`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_role_menu` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`sys_task` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`sys_task`);
SET @sql := CONCAT('ALTER TABLE `mall`.`sys_task` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`usr_browse_history` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`usr_browse_history`);
SET @sql := CONCAT('ALTER TABLE `mall`.`usr_browse_history` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`usr_comment` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`usr_comment`);
SET @sql := CONCAT('ALTER TABLE `mall`.`usr_comment` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`usr_leader` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`usr_leader`);
SET @sql := CONCAT('ALTER TABLE `mall`.`usr_leader` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`usr_pickup_person` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`usr_pickup_person`);
SET @sql := CONCAT('ALTER TABLE `mall`.`usr_pickup_person` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`usr_station` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`usr_station`);
SET @sql := CONCAT('ALTER TABLE `mall`.`usr_station` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`usr_station_leader` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`usr_station_leader`);
SET @sql := CONCAT('ALTER TABLE `mall`.`usr_station_leader` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`usr_station_leave` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`usr_station_leave`);
SET @sql := CONCAT('ALTER TABLE `mall`.`usr_station_leave` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`usr_user` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`usr_user`);
SET @sql := CONCAT('ALTER TABLE `mall`.`usr_user` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`usr_user_favorite` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`usr_user_favorite`);
SET @sql := CONCAT('ALTER TABLE `mall`.`usr_user_favorite` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_delivery_order` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_delivery_order`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_delivery_order` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_delivery_station` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_delivery_station`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_delivery_station` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_driver` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_driver`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_driver` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_driver_sign_record` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_driver_sign_record`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_driver_sign_record` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_inbound_item` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_inbound_item`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_inbound_item` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_inbound_order` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_inbound_order`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_inbound_order` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_inventory` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_inventory`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_inventory` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_inventory_lock` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_inventory_lock`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_inventory_lock` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_inventory_log` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_inventory_log`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_inventory_log` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_line` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_line`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_line` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_line_station` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_line_station`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_line_station` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_loading_item` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_loading_item`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_loading_item` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_loading_order` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_loading_order`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_loading_order` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_location` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_location`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_location` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_operation_log` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_operation_log`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_operation_log` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_outbound_order` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_outbound_order`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_outbound_order` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_pick_item` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_pick_item`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_pick_item` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_pick_task` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_pick_task`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_pick_task` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_putaway_task` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_putaway_task`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_putaway_task` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_quality_file` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_quality_file`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_quality_file` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_receive_record` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_receive_record`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_receive_record` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_return_handover` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_return_handover`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_return_handover` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_stocktake_order` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_stocktake_order`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_stocktake_order` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_warehouse` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_warehouse`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_warehouse` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_wave_batch` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_wave_batch`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_wave_batch` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE `mall`.`wms_zone` MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??';
SET @next_auto_increment := (SELECT COALESCE(MAX(`id`), 0) + 1 FROM `mall`.`wms_zone`);
SET @sql := CONCAT('ALTER TABLE `mall`.`wms_zone` AUTO_INCREMENT = ', @next_auto_increment);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;


SET FOREIGN_KEY_CHECKS = 1;
