package com.mall.common.security;

import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class MallDataPermissionHandler implements MultiDataPermissionHandler {

    @Override
    public Expression getSqlSegment(final Table table, final Expression where, final String mappedStatementId) {
        final DataScopeContext.DataScope dataScope = DataScopeContext.get();
        if (table == null || dataScope == null || dataScope.isAllPlatform()) {
            return null;
        }
        final String tableName = normalizeTableName(table.getName());
        final String tableAlias = resolveTableAlias(table);
        final List<String> segments = new ArrayList<>();
        appendTableSegments(tableName, tableAlias, dataScope, segments);
        if (segments.isEmpty()) {
            return null;
        }
        try {
            return CCJSqlParserUtil.parseCondExpression(String.join(" OR ", segments));
        } catch (JSQLParserException exception) {
            throw new IllegalStateException("数据权限SQL片段解析失败", exception);
        }
    }

    private void appendTableSegments(
            final String tableName,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope,
            final List<String> segments) {
        if ("prd_product".equals(tableName)) {
            appendInSegment(segments, tableAlias, "city_id", dataScope.getCityIds());
            appendInSegment(segments, tableAlias, "supplier_id", dataScope.getSupplierIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("prd_sku".equals(tableName) || "sale_publish_sku".equals(tableName)) {
            appendInSegment(segments, tableAlias, "supplier_id", dataScope.getSupplierIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("sale_publish_period".equals(tableName)) {
            appendInSegment(segments, tableAlias, "city_id", dataScope.getCityIds());
            appendInSegment(segments, tableAlias, "warehouse_id", dataScope.getWarehouseIds());
            appendSalePeriodSupplierSegment(segments, tableAlias, dataScope.getSupplierIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("op_todo".equals(tableName) || "op_exception_record".equals(tableName)) {
            appendInSegment(segments, tableAlias, "city_id", dataScope.getCityIds());
            appendInSegment(segments, tableAlias, "supplier_id", dataScope.getSupplierIds());
            appendInSegment(segments, tableAlias, "warehouse_id", dataScope.getWarehouseIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("ord_order".equals(tableName)) {
            appendInSegment(segments, tableAlias, "city_id", dataScope.getCityIds());
            appendInSegment(segments, tableAlias, "station_id", dataScope.getStationIds());
            appendInSegment(segments, tableAlias, "leader_id", dataScope.getLeaderIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("ord_order_item".equals(tableName)) {
            appendInSegment(segments, tableAlias, "city_id", dataScope.getCityIds());
            appendInSegment(segments, tableAlias, "supplier_id", dataScope.getSupplierIds());
            appendInSegment(segments, tableAlias, "station_id", dataScope.getStationIds());
            appendInSegment(segments, tableAlias, "warehouse_id", dataScope.getWarehouseIds());
            appendOrderItemLeaderScopeSegment(segments, tableAlias, dataScope.getLeaderIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("ord_fulfillment_track".equals(tableName)) {
            appendInSegment(segments, tableAlias, "city_id", dataScope.getCityIds());
            appendInSegment(segments, tableAlias, "station_id", dataScope.getStationIds());
            appendInSegment(segments, tableAlias, "warehouse_id", dataScope.getWarehouseIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("ord_stockout_record".equals(tableName)) {
            appendInSegment(segments, tableAlias, "station_id", dataScope.getStationIds());
            appendInSegment(segments, tableAlias, "warehouse_id", dataScope.getWarehouseIds());
            appendStockoutSupplierSegment(segments, tableAlias, dataScope.getSupplierIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("usr_station".equals(tableName)) {
            appendInSegment(segments, tableAlias, "city_id", dataScope.getCityIds());
            appendInSegment(segments, tableAlias, "id", dataScope.getStationIds());
            appendStationLeaderStationSegment(segments, tableAlias, dataScope.getLeaderIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("usr_leader".equals(tableName)) {
            appendInSegment(segments, tableAlias, "id", dataScope.getLeaderIds());
            appendLeaderStationScopeSegment(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("usr_station_leader".equals(tableName)) {
            appendInSegment(segments, tableAlias, "station_id", dataScope.getStationIds());
            appendInSegment(segments, tableAlias, "leader_id", dataScope.getLeaderIds());
            appendStationLeaderCitySegment(segments, tableAlias, dataScope.getCityIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("usr_station_leave".equals(tableName)) {
            appendInSegment(segments, tableAlias, "city_id", dataScope.getCityIds());
            appendInSegment(segments, tableAlias, "station_id", dataScope.getStationIds());
            appendInSegment(segments, tableAlias, "leader_id", dataScope.getLeaderIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("msg_record".equals(tableName)) {
            appendMessageReceiverSegments(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("pay_trade".equals(tableName)) {
            appendOrderScopeSegments(segments, tableAlias + ".order_id", dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("pay_refund_trade".equals(tableName)) {
            appendOrderScopeSegments(segments, tableAlias + ".order_id", dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("pay_fund_flow".equals(tableName)) {
            appendFundFlowSubjectSegments(segments, tableAlias, dataScope);
            appendFundFlowPaymentSegments(segments, tableAlias, dataScope);
            appendFundFlowRefundSegments(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("afs_after_sale".equals(tableName)) {
            appendInSegment(segments, tableAlias, "city_id", dataScope.getCityIds());
            appendInSegment(segments, tableAlias, "station_id", dataScope.getStationIds());
            appendAfterSaleSupplierSegment(segments, tableAlias, dataScope.getSupplierIds());
            appendAfterSaleWarehouseSegment(segments, tableAlias, dataScope.getWarehouseIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("afs_after_sale_item".equals(tableName)) {
            appendInSegment(segments, tableAlias, "supplier_id", dataScope.getSupplierIds());
            appendAfterSaleItemScopeSegment(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("afs_audit_log".equals(tableName)) {
            appendAfterSaleLogScopeSegment(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("afs_return_record".equals(tableName)) {
            appendInSegment(segments, tableAlias, "station_id", dataScope.getStationIds());
            appendInSegment(segments, tableAlias, "warehouse_id", dataScope.getWarehouseIds());
            appendReturnRecordSupplierSegment(segments, tableAlias, dataScope.getSupplierIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("sup_supplier".equals(tableName)) {
            appendInSegment(segments, tableAlias, "city_id", dataScope.getCityIds());
            appendInSegment(segments, tableAlias, "id", dataScope.getSupplierIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("sup_supplier_contact".equals(tableName)
                || "sup_qualification".equals(tableName)
                || "sup_contract".equals(tableName)) {
            appendSupplierChildScopeSegment(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("pur_purchase_order".equals(tableName)) {
            appendInSegment(segments, tableAlias, "city_id", dataScope.getCityIds());
            appendInSegment(segments, tableAlias, "supplier_id", dataScope.getSupplierIds());
            appendInSegment(segments, tableAlias, "warehouse_id", dataScope.getWarehouseIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("pur_purchase_item".equals(tableName)) {
            appendInSegment(segments, tableAlias, "supplier_id", dataScope.getSupplierIds());
            appendInSegment(segments, tableAlias, "warehouse_id", dataScope.getWarehouseIds());
            appendPurchaseItemOrderScopeSegment(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("pur_purchase_audit_log".equals(tableName) || "pur_purchase_source_log".equals(tableName)) {
            appendPurchaseLogScopeSegment(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("pur_delivery_order".equals(tableName)) {
            appendInSegment(segments, tableAlias, "supplier_id", dataScope.getSupplierIds());
            appendInSegment(segments, tableAlias, "warehouse_id", dataScope.getWarehouseIds());
            appendDeliveryOrderPurchaseScopeSegment(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("pur_delivery_item".equals(tableName)) {
            appendDeliveryItemScopeSegment(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("fin_account".equals(tableName)
                || "fin_account_flow".equals(tableName)
                || "fin_withdraw_apply".equals(tableName)
                || "fin_deposit_record".equals(tableName)) {
            appendFinanceSubjectSegments(segments, tableAlias, dataScope);
            appendFinanceSubjectCitySegments(segments, tableAlias, dataScope.getCityIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("fin_commission_detail".equals(tableName)) {
            appendCommissionScopeSegments(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("fin_split_flow".equals(tableName)) {
            appendSplitFlowScopeSegments(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("fin_supplier_settlement".equals(tableName)) {
            appendInSegment(segments, tableAlias, "supplier_id", dataScope.getSupplierIds());
            appendSupplierChildCityScopeSegment(segments, tableAlias, dataScope.getCityIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("fin_reconciliation_batch".equals(tableName)) {
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("wms_warehouse".equals(tableName)) {
            appendInSegment(segments, tableAlias, "city_id", dataScope.getCityIds());
            appendInSegment(segments, tableAlias, "id", dataScope.getWarehouseIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("wms_zone".equals(tableName) || "wms_location".equals(tableName)) {
            appendWmsWarehouseScopeSegments(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("wms_line".equals(tableName)) {
            appendWmsWarehouseScopeSegments(segments, tableAlias, dataScope);
            appendInSegment(segments, tableAlias, "default_driver_account_id", dataScope.getDriverIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("wms_line_station".equals(tableName)) {
            appendInSegment(segments, tableAlias, "station_id", dataScope.getStationIds());
            appendWmsLineStationScopeSegment(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("wms_driver".equals(tableName)) {
            appendInSegment(segments, tableAlias, "account_id", dataScope.getDriverIds());
            appendWmsDriverLineScopeSegment(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("wms_inbound_order".equals(tableName)
                || "wms_putaway_task".equals(tableName)
                || "wms_inventory".equals(tableName)
                || "wms_inventory_lock".equals(tableName)
                || "wms_inventory_log".equals(tableName)
                || "wms_wave_batch".equals(tableName)
                || "wms_pick_task".equals(tableName)
                || "wms_loading_order".equals(tableName)
                || "wms_outbound_order".equals(tableName)
                || "wms_delivery_order".equals(tableName)
                || "wms_return_handover".equals(tableName)
                || "wms_stocktake_order".equals(tableName)
                || "wms_operation_log".equals(tableName)) {
            appendWmsWarehouseScopeSegments(segments, tableAlias, dataScope);
            appendWmsDirectStationSegment(tableName, segments, tableAlias, dataScope.getStationIds());
            appendWmsDirectStationLeaderSegment(tableName, segments, tableAlias, dataScope.getLeaderIds());
            appendWmsDriverAccountSegments(tableName, segments, tableAlias, dataScope.getDriverIds());
            appendWmsDeliveryOrderStationSegment(tableName, segments, tableAlias, dataScope.getStationIds());
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("wms_inbound_item".equals(tableName)
                || "wms_receive_record".equals(tableName)
                || "wms_quality_file".equals(tableName)) {
            appendWmsInboundChildScopeSegment(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("wms_pick_item".equals(tableName)) {
            appendWmsPickItemScopeSegment(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("wms_loading_item".equals(tableName)) {
            appendInSegment(segments, tableAlias, "station_id", dataScope.getStationIds());
            appendWmsLoadingItemScopeSegment(segments, tableAlias, dataScope);
            appendDenyAllIfEmpty(segments);
            return;
        }
        if ("wms_delivery_station".equals(tableName)
                || "wms_driver_sign_record".equals(tableName)) {
            appendInSegment(segments, tableAlias, "station_id", dataScope.getStationIds());
            appendWmsStationLeaderSegment(segments, tableAlias, dataScope.getLeaderIds());
            appendWmsDeliveryChildScopeSegment(segments, tableAlias, dataScope);
            appendWmsDriverAccountSegments(tableName, segments, tableAlias, dataScope.getDriverIds());
            appendDenyAllIfEmpty(segments);
        }
    }

    private String normalizeTableName(final String tableName) {
        if (!StringUtils.hasText(tableName)) {
            return "";
        }
        final String simpleName = tableName.contains(".")
                ? tableName.substring(tableName.lastIndexOf('.') + 1)
                : tableName;
        return simpleName.replace("`", "").toLowerCase(Locale.ROOT);
    }

    private String resolveTableAlias(final Table table) {
        if (table.getAlias() != null && StringUtils.hasText(table.getAlias().getName())) {
            return table.getAlias().getName();
        }
        return table.getName();
    }

    private void appendInSegment(
            final List<String> segments,
            final String tableAlias,
            final String column,
            final Set<Long> values) {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        final String valueText = values.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add(tableAlias + "." + column + " IN (" + valueText + ")");
    }

    private void appendSalePeriodSupplierSegment(
            final List<String> segments,
            final String tableAlias,
            final Set<Long> supplierIds) {
        if (CollectionUtils.isEmpty(supplierIds)) {
            return;
        }
        final String valueText = supplierIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("EXISTS (SELECT 1 FROM sale_publish_sku dps WHERE dps.period_id = "
                + tableAlias + ".id AND dps.supplier_id IN (" + valueText + ") AND dps.is_deleted = 0)");
    }

    private void appendDenyAllIfEmpty(final List<String> segments) {
        if (segments.isEmpty()) {
            segments.add("1 = 0");
        }
    }

    private void appendStockoutSupplierSegment(
            final List<String> segments,
            final String tableAlias,
            final Set<Long> supplierIds) {
        if (CollectionUtils.isEmpty(supplierIds)) {
            return;
        }
        final String valueText = supplierIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("EXISTS (SELECT 1 FROM ord_order_item dsoi WHERE dsoi.id = "
                + tableAlias + ".order_item_id AND dsoi.supplier_id IN (" + valueText
                + ") AND dsoi.is_deleted = 0)");
    }

    private void appendStationLeaderStationSegment(
            final List<String> segments,
            final String tableAlias,
            final Set<Long> leaderIds) {
        if (CollectionUtils.isEmpty(leaderIds)) {
            return;
        }
        final String valueText = leaderIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("EXISTS (SELECT 1 FROM usr_station_leader dsl_station WHERE dsl_station.station_id = "
                + tableAlias + ".id AND dsl_station.leader_id IN (" + valueText
                + ") AND dsl_station.is_deleted = 0)");
    }

    private void appendLeaderStationScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        final List<String> leaderScopes = new ArrayList<>();
        appendConditionInSegment(leaderScopes, "dlss", "station_id", dataScope.getStationIds());
        appendStationLeaderCityCondition(leaderScopes, "dlss.station_id", dataScope.getCityIds());
        if (leaderScopes.isEmpty()) {
            return;
        }
        segments.add("EXISTS (SELECT 1 FROM usr_station_leader dlss WHERE dlss.leader_id = "
                + tableAlias + ".id AND dlss.is_deleted = 0 AND "
                + formatConditionGroup(leaderScopes) + ")");
    }

    private void appendStationLeaderCitySegment(
            final List<String> segments,
            final String tableAlias,
            final Set<Long> cityIds) {
        if (CollectionUtils.isEmpty(cityIds)) {
            return;
        }
        final String valueText = cityIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("EXISTS (SELECT 1 FROM usr_station dsls_city WHERE dsls_city.id = "
                + tableAlias + ".station_id AND dsls_city.city_id IN (" + valueText
                + ") AND dsls_city.is_deleted = 0)");
    }

    private void appendStationLeaderCityCondition(
            final List<String> conditions,
            final String stationIdExpression,
            final Set<Long> cityIds) {
        if (CollectionUtils.isEmpty(cityIds)) {
            return;
        }
        final String valueText = cityIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        conditions.add("EXISTS (SELECT 1 FROM usr_station dslc_station WHERE dslc_station.id = "
                + stationIdExpression + " AND dslc_station.city_id IN (" + valueText
                + ") AND dslc_station.is_deleted = 0)");
    }

    private void appendMessageReceiverSegments(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        appendReceiverSegment(segments, tableAlias, 2L, dataScope.getSupplierIds());
        appendReceiverSegment(segments, tableAlias, 4L, dataScope.getStationIds());
        appendReceiverSegment(segments, tableAlias, 5L, dataScope.getLeaderIds());
        appendMessageStationCitySegment(segments, tableAlias, dataScope.getCityIds());
    }

    private void appendReceiverSegment(
            final List<String> segments,
            final String tableAlias,
            final Long receiverType,
            final Set<Long> receiverIds) {
        if (CollectionUtils.isEmpty(receiverIds)) {
            return;
        }
        final String valueText = receiverIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("(" + tableAlias + ".receiver_type = " + receiverType + " AND "
                + tableAlias + ".receiver_id IN (" + valueText + "))");
    }

    private void appendMessageStationCitySegment(
            final List<String> segments,
            final String tableAlias,
            final Set<Long> cityIds) {
        if (CollectionUtils.isEmpty(cityIds)) {
            return;
        }
        final String valueText = cityIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("(" + tableAlias + ".receiver_type = 4 AND EXISTS "
                + "(SELECT 1 FROM usr_station dmr_station WHERE dmr_station.id = " + tableAlias
                + ".receiver_id AND dmr_station.city_id IN (" + valueText + ") AND dmr_station.is_deleted = 0))");
        segments.add("(" + tableAlias + ".receiver_type = 5 AND EXISTS "
                + "(SELECT 1 FROM usr_station_leader dmr_sl WHERE dmr_sl.leader_id = " + tableAlias
                + ".receiver_id AND dmr_sl.is_deleted = 0 AND EXISTS "
                + "(SELECT 1 FROM usr_station dmr_lst WHERE dmr_lst.id = dmr_sl.station_id "
                + "AND dmr_lst.city_id IN (" + valueText + ") AND dmr_lst.is_deleted = 0)))");
    }

    private void appendOrderScopeSegments(
            final List<String> segments,
            final String orderIdExpression,
            final DataScopeContext.DataScope dataScope) {
        final List<String> orderScopes = new ArrayList<>();
        appendConditionInSegment(orderScopes, "dpo", "city_id", dataScope.getCityIds());
        appendConditionInSegment(orderScopes, "dpo", "station_id", dataScope.getStationIds());
        appendConditionInSegment(orderScopes, "dpo", "leader_id", dataScope.getLeaderIds());
        if (!orderScopes.isEmpty()) {
            segments.add("EXISTS (SELECT 1 FROM ord_order dpo WHERE dpo.id = " + orderIdExpression
                    + " AND dpo.is_deleted = 0 AND " + formatConditionGroup(orderScopes) + ")");
        }

        final List<String> itemScopes = new ArrayList<>();
        appendConditionInSegment(itemScopes, "dpoi", "supplier_id", dataScope.getSupplierIds());
        appendConditionInSegment(itemScopes, "dpoi", "warehouse_id", dataScope.getWarehouseIds());
        if (!itemScopes.isEmpty()) {
            segments.add("EXISTS (SELECT 1 FROM ord_order_item dpoi WHERE dpoi.order_id = " + orderIdExpression
                    + " AND dpoi.is_deleted = 0 AND " + formatConditionGroup(itemScopes) + ")");
        }
    }

    private void appendOrderItemLeaderScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final Set<Long> leaderIds) {
        if (CollectionUtils.isEmpty(leaderIds)) {
            return;
        }
        final String valueText = leaderIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("EXISTS (SELECT 1 FROM ord_order doil_order WHERE doil_order.id = "
                + tableAlias + ".order_id AND doil_order.leader_id IN (" + valueText
                + ") AND doil_order.is_deleted = 0)");
    }

    private void appendFundFlowSubjectSegments(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        appendSubjectSegment(segments, tableAlias, 1L, dataScope.getSupplierIds());
        appendSubjectSegment(segments, tableAlias, 2L, dataScope.getStationIds());
        appendSubjectSegment(segments, tableAlias, 3L, dataScope.getLeaderIds());
    }

    private void appendSubjectSegment(
            final List<String> segments,
            final String tableAlias,
            final Long subjectType,
            final Set<Long> subjectIds) {
        if (CollectionUtils.isEmpty(subjectIds)) {
            return;
        }
        final String valueText = subjectIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("(" + tableAlias + ".subject_type = " + subjectType + " AND "
                + tableAlias + ".subject_id IN (" + valueText + "))");
    }

    private void appendFundFlowPaymentSegments(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        appendFundFlowOrderSegment(segments, tableAlias, dataScope, true);
        appendFundFlowOrderItemSegment(segments, tableAlias, dataScope, true);
    }

    private void appendFundFlowRefundSegments(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        appendFundFlowOrderSegment(segments, tableAlias, dataScope, false);
        appendFundFlowOrderItemSegment(segments, tableAlias, dataScope, false);
    }

    private void appendFundFlowOrderSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope,
            final boolean paymentBiz) {
        final List<String> orderScopes = new ArrayList<>();
        appendConditionInSegment(orderScopes, "dffo", "city_id", dataScope.getCityIds());
        appendConditionInSegment(orderScopes, "dffo", "station_id", dataScope.getStationIds());
        appendConditionInSegment(orderScopes, "dffo", "leader_id", dataScope.getLeaderIds());
        if (orderScopes.isEmpty()) {
            return;
        }
        final String bizTable = paymentBiz ? "pay_trade dffpt" : "pay_refund_trade dffpt";
        final String bizColumn = paymentBiz ? "dffpt.pay_no" : "dffpt.refund_no";
        segments.add("EXISTS (SELECT 1 FROM " + bizTable + " WHERE " + bizColumn + " = " + tableAlias
                + ".biz_no AND dffpt.is_deleted = 0 AND EXISTS (SELECT 1 FROM ord_order dffo WHERE dffo.id = "
                + "dffpt.order_id AND dffo.is_deleted = 0 AND " + formatConditionGroup(orderScopes) + "))");
    }

    private void appendFundFlowOrderItemSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope,
            final boolean paymentBiz) {
        final List<String> itemScopes = new ArrayList<>();
        appendConditionInSegment(itemScopes, "dffoi", "supplier_id", dataScope.getSupplierIds());
        appendConditionInSegment(itemScopes, "dffoi", "warehouse_id", dataScope.getWarehouseIds());
        if (itemScopes.isEmpty()) {
            return;
        }
        final String bizTable = paymentBiz ? "pay_trade dffpt" : "pay_refund_trade dffpt";
        final String bizColumn = paymentBiz ? "dffpt.pay_no" : "dffpt.refund_no";
        segments.add("EXISTS (SELECT 1 FROM " + bizTable + " WHERE " + bizColumn + " = " + tableAlias
                + ".biz_no AND dffpt.is_deleted = 0 AND EXISTS (SELECT 1 FROM ord_order_item dffoi WHERE "
                + "dffoi.order_id = dffpt.order_id AND dffoi.is_deleted = 0 AND "
                + formatConditionGroup(itemScopes) + "))");
    }

    private void appendConditionInSegment(
            final List<String> segments,
            final String tableAlias,
            final String column,
            final Set<Long> values) {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        final String valueText = values.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add(tableAlias + "." + column + " IN (" + valueText + ")");
    }

    private String formatConditionGroup(final List<String> conditions) {
        if (conditions.size() == 1) {
            return conditions.get(0);
        }
        return "(" + String.join(" OR ", conditions) + ")";
    }

    private void appendAfterSaleSupplierSegment(
            final List<String> segments,
            final String tableAlias,
            final Set<Long> supplierIds) {
        if (CollectionUtils.isEmpty(supplierIds)) {
            return;
        }
        final String valueText = supplierIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("EXISTS (SELECT 1 FROM afs_after_sale_item dasi WHERE dasi.after_sale_id = "
                + tableAlias + ".id AND dasi.supplier_id IN (" + valueText + ") AND dasi.is_deleted = 0)");
    }

    private void appendAfterSaleWarehouseSegment(
            final List<String> segments,
            final String tableAlias,
            final Set<Long> warehouseIds) {
        if (CollectionUtils.isEmpty(warehouseIds)) {
            return;
        }
        final String valueText = warehouseIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("EXISTS (SELECT 1 FROM afs_return_record darr WHERE darr.after_sale_id = "
                + tableAlias + ".id AND darr.warehouse_id IN (" + valueText + ") AND darr.is_deleted = 0)");
    }

    private void appendAfterSaleItemScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        final List<String> afterSaleScopes = new ArrayList<>();
        appendConditionInSegment(afterSaleScopes, "dasi_as", "city_id", dataScope.getCityIds());
        appendConditionInSegment(afterSaleScopes, "dasi_as", "station_id", dataScope.getStationIds());
        if (!afterSaleScopes.isEmpty()) {
            segments.add("EXISTS (SELECT 1 FROM afs_after_sale dasi_as WHERE dasi_as.id = "
                    + tableAlias + ".after_sale_id AND dasi_as.is_deleted = 0 AND "
                    + formatConditionGroup(afterSaleScopes) + ")");
        }
        appendAfterSaleItemWarehouseSegment(segments, tableAlias, dataScope.getWarehouseIds());
    }

    private void appendAfterSaleItemWarehouseSegment(
            final List<String> segments,
            final String tableAlias,
            final Set<Long> warehouseIds) {
        if (CollectionUtils.isEmpty(warehouseIds)) {
            return;
        }
        final String valueText = warehouseIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("EXISTS (SELECT 1 FROM ord_order_item dasi_oi WHERE dasi_oi.id = "
                + tableAlias + ".order_item_id AND dasi_oi.warehouse_id IN (" + valueText
                + ") AND dasi_oi.is_deleted = 0)");
    }

    private void appendAfterSaleLogScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        final List<String> afterSaleScopes = new ArrayList<>();
        appendConditionInSegment(afterSaleScopes, "dasl_as", "city_id", dataScope.getCityIds());
        appendConditionInSegment(afterSaleScopes, "dasl_as", "station_id", dataScope.getStationIds());
        if (!afterSaleScopes.isEmpty()) {
            segments.add("EXISTS (SELECT 1 FROM afs_after_sale dasl_as WHERE dasl_as.id = "
                    + tableAlias + ".after_sale_id AND dasl_as.is_deleted = 0 AND "
                    + formatConditionGroup(afterSaleScopes) + ")");
        }
        appendAfterSaleLogSupplierSegment(segments, tableAlias, dataScope.getSupplierIds());
        appendAfterSaleLogWarehouseSegment(segments, tableAlias, dataScope.getWarehouseIds());
    }

    private void appendAfterSaleLogSupplierSegment(
            final List<String> segments,
            final String tableAlias,
            final Set<Long> supplierIds) {
        if (CollectionUtils.isEmpty(supplierIds)) {
            return;
        }
        final String valueText = supplierIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("EXISTS (SELECT 1 FROM afs_after_sale_item dasl_item WHERE dasl_item.after_sale_id = "
                + tableAlias + ".after_sale_id AND dasl_item.supplier_id IN (" + valueText
                + ") AND dasl_item.is_deleted = 0)");
    }

    private void appendAfterSaleLogWarehouseSegment(
            final List<String> segments,
            final String tableAlias,
            final Set<Long> warehouseIds) {
        if (CollectionUtils.isEmpty(warehouseIds)) {
            return;
        }
        final String valueText = warehouseIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("EXISTS (SELECT 1 FROM afs_return_record dasl_ret WHERE dasl_ret.after_sale_id = "
                + tableAlias + ".after_sale_id AND dasl_ret.warehouse_id IN (" + valueText
                + ") AND dasl_ret.is_deleted = 0)");
    }

    private void appendReturnRecordSupplierSegment(
            final List<String> segments,
            final String tableAlias,
            final Set<Long> supplierIds) {
        if (CollectionUtils.isEmpty(supplierIds)) {
            return;
        }
        final String valueText = supplierIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("EXISTS (SELECT 1 FROM afs_after_sale_item darr_item WHERE darr_item.after_sale_id = "
                + tableAlias + ".after_sale_id AND darr_item.supplier_id IN (" + valueText
                + ") AND darr_item.is_deleted = 0)");
    }

    private void appendSupplierChildScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        appendInSegment(segments, tableAlias, "supplier_id", dataScope.getSupplierIds());
        appendSupplierChildCityScopeSegment(segments, tableAlias, dataScope.getCityIds());
    }

    private void appendSupplierChildCityScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final Set<Long> cityIds) {
        if (CollectionUtils.isEmpty(cityIds)) {
            return;
        }
        final String valueText = cityIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("EXISTS (SELECT 1 FROM sup_supplier dscp WHERE dscp.id = "
                + tableAlias + ".supplier_id AND dscp.city_id IN (" + valueText
                + ") AND dscp.is_deleted = 0)");
    }

    private void appendPurchaseItemOrderScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        final List<String> orderScopes = new ArrayList<>();
        appendConditionInSegment(orderScopes, "dpi_po", "city_id", dataScope.getCityIds());
        appendConditionInSegment(orderScopes, "dpi_po", "warehouse_id", dataScope.getWarehouseIds());
        if (orderScopes.isEmpty()) {
            return;
        }
        segments.add("EXISTS (SELECT 1 FROM pur_purchase_order dpi_po WHERE dpi_po.id = "
                + tableAlias + ".purchase_id AND dpi_po.is_deleted = 0 AND "
                + formatConditionGroup(orderScopes) + ")");
    }

    private void appendPurchaseLogScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        final List<String> purchaseScopes = new ArrayList<>();
        appendConditionInSegment(purchaseScopes, "dpl_po", "city_id", dataScope.getCityIds());
        appendConditionInSegment(purchaseScopes, "dpl_po", "supplier_id", dataScope.getSupplierIds());
        appendConditionInSegment(purchaseScopes, "dpl_po", "warehouse_id", dataScope.getWarehouseIds());
        if (purchaseScopes.isEmpty()) {
            return;
        }
        segments.add("EXISTS (SELECT 1 FROM pur_purchase_order dpl_po WHERE dpl_po.id = "
                + tableAlias + ".purchase_id AND dpl_po.is_deleted = 0 AND "
                + formatConditionGroup(purchaseScopes) + ")");
    }

    private void appendDeliveryOrderPurchaseScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        final List<String> purchaseScopes = new ArrayList<>();
        appendConditionInSegment(purchaseScopes, "ddo_po", "city_id", dataScope.getCityIds());
        if (purchaseScopes.isEmpty()) {
            return;
        }
        segments.add("EXISTS (SELECT 1 FROM pur_purchase_order ddo_po WHERE ddo_po.id = "
                + tableAlias + ".purchase_id AND ddo_po.is_deleted = 0 AND "
                + formatConditionGroup(purchaseScopes) + ")");
    }

    private void appendDeliveryItemScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        final List<String> deliveryScopes = new ArrayList<>();
        appendConditionInSegment(deliveryScopes, "ddi_do", "supplier_id", dataScope.getSupplierIds());
        appendConditionInSegment(deliveryScopes, "ddi_do", "warehouse_id", dataScope.getWarehouseIds());
        if (!deliveryScopes.isEmpty()) {
            segments.add("EXISTS (SELECT 1 FROM pur_delivery_order ddi_do WHERE ddi_do.id = "
                    + tableAlias + ".delivery_id AND ddi_do.is_deleted = 0 AND "
                    + formatConditionGroup(deliveryScopes) + ")");
        }
        appendDeliveryItemPurchaseCityScopeSegment(segments, tableAlias, dataScope.getCityIds());
    }

    private void appendDeliveryItemPurchaseCityScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final Set<Long> cityIds) {
        if (CollectionUtils.isEmpty(cityIds)) {
            return;
        }
        final String valueText = cityIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("EXISTS (SELECT 1 FROM pur_delivery_order ddi_city_do WHERE ddi_city_do.id = "
                + tableAlias + ".delivery_id AND ddi_city_do.is_deleted = 0 AND EXISTS "
                + "(SELECT 1 FROM pur_purchase_order ddi_city_po WHERE ddi_city_po.id = "
                + "ddi_city_do.purchase_id AND ddi_city_po.city_id IN (" + valueText
                + ") AND ddi_city_po.is_deleted = 0))");
    }

    private void appendFinanceSubjectSegments(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        appendSubjectSegment(segments, tableAlias, 1L, dataScope.getSupplierIds());
        appendSubjectSegment(segments, tableAlias, 2L, dataScope.getStationIds());
        appendSubjectSegment(segments, tableAlias, 3L, dataScope.getLeaderIds());
    }

    private void appendFinanceSubjectCitySegments(
            final List<String> segments,
            final String tableAlias,
            final Set<Long> cityIds) {
        if (CollectionUtils.isEmpty(cityIds)) {
            return;
        }
        final String valueText = cityIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("(" + tableAlias + ".subject_type = 1 AND EXISTS "
                + "(SELECT 1 FROM sup_supplier dfs_sup WHERE dfs_sup.id = " + tableAlias
                + ".subject_id AND dfs_sup.city_id IN (" + valueText + ") AND dfs_sup.is_deleted = 0))");
        segments.add("(" + tableAlias + ".subject_type = 2 AND EXISTS "
                + "(SELECT 1 FROM usr_station dfs_st WHERE dfs_st.id = " + tableAlias
                + ".subject_id AND dfs_st.city_id IN (" + valueText + ") AND dfs_st.is_deleted = 0))");
        segments.add("(" + tableAlias + ".subject_type = 3 AND EXISTS "
                + "(SELECT 1 FROM usr_station_leader dfs_sl WHERE dfs_sl.leader_id = " + tableAlias
                + ".subject_id AND dfs_sl.is_deleted = 0 AND EXISTS "
                + "(SELECT 1 FROM usr_station dfs_lst WHERE dfs_lst.id = dfs_sl.station_id "
                + "AND dfs_lst.city_id IN (" + valueText + ") AND dfs_lst.is_deleted = 0)))");
    }

    private void appendCommissionScopeSegments(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        appendInSegment(segments, tableAlias, "supplier_id", dataScope.getSupplierIds());
        appendInSegment(segments, tableAlias, "station_id", dataScope.getStationIds());
        appendSubjectSegment(segments, tableAlias, 2L, dataScope.getLeaderIds());
        appendCommissionOrderScopeSegment(segments, tableAlias, dataScope);
    }

    private void appendCommissionOrderScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        final List<String> orderScopes = new ArrayList<>();
        appendConditionInSegment(orderScopes, "dfc_o", "city_id", dataScope.getCityIds());
        appendConditionInSegment(orderScopes, "dfc_o", "station_id", dataScope.getStationIds());
        appendConditionInSegment(orderScopes, "dfc_o", "leader_id", dataScope.getLeaderIds());
        if (!orderScopes.isEmpty()) {
            segments.add("EXISTS (SELECT 1 FROM ord_order dfc_o WHERE dfc_o.id = "
                    + tableAlias + ".order_id AND dfc_o.is_deleted = 0 AND "
                    + formatConditionGroup(orderScopes) + ")");
        }
        final List<String> itemScopes = new ArrayList<>();
        appendConditionInSegment(itemScopes, "dfc_oi", "supplier_id", dataScope.getSupplierIds());
        appendConditionInSegment(itemScopes, "dfc_oi", "warehouse_id", dataScope.getWarehouseIds());
        if (!itemScopes.isEmpty()) {
            segments.add("EXISTS (SELECT 1 FROM ord_order_item dfc_oi WHERE dfc_oi.id = "
                    + tableAlias + ".order_item_id AND dfc_oi.is_deleted = 0 AND "
                    + formatConditionGroup(itemScopes) + ")");
        }
    }

    private void appendSplitFlowScopeSegments(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        appendSubjectSegment(segments, tableAlias, 3L, dataScope.getSupplierIds());
        appendSubjectSegment(segments, tableAlias, 1L, dataScope.getStationIds());
        appendSubjectSegment(segments, tableAlias, 2L, dataScope.getLeaderIds());
        appendSplitFlowCommissionScopeSegment(segments, tableAlias, dataScope);
        appendSplitFlowOrderScopeSegment(segments, tableAlias, dataScope);
    }

    private void appendSplitFlowCommissionScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        final List<String> commissionScopes = new ArrayList<>();
        appendConditionInSegment(commissionScopes, "dfs_c", "supplier_id", dataScope.getSupplierIds());
        appendConditionInSegment(commissionScopes, "dfs_c", "station_id", dataScope.getStationIds());
        if (CollectionUtils.isEmpty(dataScope.getLeaderIds())) {
            if (commissionScopes.isEmpty()) {
                return;
            }
        } else {
            final String leaderText = dataScope.getLeaderIds().stream()
                    .sorted()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            commissionScopes.add("(dfs_c.subject_type = 2 AND dfs_c.subject_id IN (" + leaderText + "))");
        }
        segments.add("EXISTS (SELECT 1 FROM fin_commission_detail dfs_c WHERE dfs_c.id = "
                + tableAlias + ".commission_id AND dfs_c.is_deleted = 0 AND "
                + formatConditionGroup(commissionScopes) + ")");
    }

    private void appendSplitFlowOrderScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        final List<String> orderScopes = new ArrayList<>();
        appendConditionInSegment(orderScopes, "dfs_o", "city_id", dataScope.getCityIds());
        appendConditionInSegment(orderScopes, "dfs_o", "station_id", dataScope.getStationIds());
        appendConditionInSegment(orderScopes, "dfs_o", "leader_id", dataScope.getLeaderIds());
        if (!orderScopes.isEmpty()) {
            segments.add("EXISTS (SELECT 1 FROM ord_order dfs_o WHERE dfs_o.id = "
                    + tableAlias + ".order_id AND dfs_o.is_deleted = 0 AND "
                    + formatConditionGroup(orderScopes) + ")");
        }
        final List<String> itemScopes = new ArrayList<>();
        appendConditionInSegment(itemScopes, "dfs_oi", "supplier_id", dataScope.getSupplierIds());
        appendConditionInSegment(itemScopes, "dfs_oi", "warehouse_id", dataScope.getWarehouseIds());
        if (!itemScopes.isEmpty()) {
            segments.add("EXISTS (SELECT 1 FROM ord_order_item dfs_oi WHERE dfs_oi.order_id = "
                    + tableAlias + ".order_id AND dfs_oi.is_deleted = 0 AND "
                    + formatConditionGroup(itemScopes) + ")");
        }
    }

    private void appendWmsWarehouseScopeSegments(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        appendInSegment(segments, tableAlias, "warehouse_id", dataScope.getWarehouseIds());
        appendWmsWarehouseCityScopeSegment(segments, tableAlias + ".warehouse_id", dataScope.getCityIds());
    }

    private void appendWmsWarehouseCityScopeSegment(
            final List<String> segments,
            final String warehouseIdExpression,
            final Set<Long> cityIds) {
        if (CollectionUtils.isEmpty(cityIds)) {
            return;
        }
        final String valueText = cityIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("EXISTS (SELECT 1 FROM wms_warehouse dww WHERE dww.id = "
                + warehouseIdExpression + " AND dww.city_id IN (" + valueText
                + ") AND dww.is_deleted = 0)");
    }

    private void appendWmsLineStationScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        final List<String> lineScopes = new ArrayList<>();
        appendConditionInSegment(lineScopes, "dwls_line", "warehouse_id", dataScope.getWarehouseIds());
        appendConditionInSegment(lineScopes, "dwls_line", "default_driver_account_id", dataScope.getDriverIds());
        appendWmsWarehouseCityCondition(lineScopes, "dwls_line.warehouse_id", dataScope.getCityIds());
        if (lineScopes.isEmpty()) {
            return;
        }
        segments.add("EXISTS (SELECT 1 FROM wms_line dwls_line WHERE dwls_line.id = "
                + tableAlias + ".line_id AND dwls_line.is_deleted = 0 AND "
                + formatConditionGroup(lineScopes) + ")");
    }

    private void appendWmsDriverLineScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        final List<String> lineScopes = new ArrayList<>();
        appendConditionInSegment(lineScopes, "dwdr_line", "warehouse_id", dataScope.getWarehouseIds());
        appendWmsWarehouseCityCondition(lineScopes, "dwdr_line.warehouse_id", dataScope.getCityIds());
        if (lineScopes.isEmpty()) {
            return;
        }
        segments.add("EXISTS (SELECT 1 FROM wms_line dwdr_line WHERE dwdr_line.default_driver_account_id = "
                + tableAlias + ".account_id AND dwdr_line.is_deleted = 0 AND "
                + formatConditionGroup(lineScopes) + ")");
    }

    private void appendWmsInboundChildScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        final List<String> inboundScopes = new ArrayList<>();
        appendConditionInSegment(inboundScopes, "dwic_in", "warehouse_id", dataScope.getWarehouseIds());
        appendConditionInSegment(inboundScopes, "dwic_in", "supplier_id", dataScope.getSupplierIds());
        appendWmsWarehouseCityCondition(inboundScopes, "dwic_in.warehouse_id", dataScope.getCityIds());
        if (inboundScopes.isEmpty()) {
            return;
        }
        segments.add("EXISTS (SELECT 1 FROM wms_inbound_order dwic_in WHERE dwic_in.id = "
                + tableAlias + ".inbound_id AND dwic_in.is_deleted = 0 AND "
                + formatConditionGroup(inboundScopes) + ")");
    }

    private void appendWmsPickItemScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        final List<String> pickScopes = new ArrayList<>();
        appendConditionInSegment(pickScopes, "dwpi_pick", "warehouse_id", dataScope.getWarehouseIds());
        appendConditionInSegment(pickScopes, "dwpi_pick", "assign_account_id", dataScope.getDriverIds());
        appendWmsWarehouseCityCondition(pickScopes, "dwpi_pick.warehouse_id", dataScope.getCityIds());
        if (pickScopes.isEmpty()) {
            return;
        }
        segments.add("EXISTS (SELECT 1 FROM wms_pick_task dwpi_pick WHERE dwpi_pick.id = "
                + tableAlias + ".pick_id AND dwpi_pick.is_deleted = 0 AND "
                + formatConditionGroup(pickScopes) + ")");
    }

    private void appendWmsLoadingItemScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        final List<String> loadingScopes = new ArrayList<>();
        appendConditionInSegment(loadingScopes, "dwli_load", "warehouse_id", dataScope.getWarehouseIds());
        appendConditionInSegment(loadingScopes, "dwli_load", "driver_account_id", dataScope.getDriverIds());
        appendWmsWarehouseCityCondition(loadingScopes, "dwli_load.warehouse_id", dataScope.getCityIds());
        if (loadingScopes.isEmpty()) {
            return;
        }
        segments.add("EXISTS (SELECT 1 FROM wms_loading_order dwli_load WHERE dwli_load.id = "
                + tableAlias + ".loading_id AND dwli_load.is_deleted = 0 AND "
                + formatConditionGroup(loadingScopes) + ")");
    }

    private void appendWmsDeliveryChildScopeSegment(
            final List<String> segments,
            final String tableAlias,
            final DataScopeContext.DataScope dataScope) {
        final List<String> deliveryScopes = new ArrayList<>();
        appendConditionInSegment(deliveryScopes, "dwdc_do", "warehouse_id", dataScope.getWarehouseIds());
        appendConditionInSegment(deliveryScopes, "dwdc_do", "driver_account_id", dataScope.getDriverIds());
        appendWmsWarehouseCityCondition(deliveryScopes, "dwdc_do.warehouse_id", dataScope.getCityIds());
        if (deliveryScopes.isEmpty()) {
            return;
        }
        segments.add("EXISTS (SELECT 1 FROM wms_delivery_order dwdc_do WHERE dwdc_do.id = "
                + tableAlias + ".delivery_id AND dwdc_do.is_deleted = 0 AND "
                + formatConditionGroup(deliveryScopes) + ")");
    }

    private void appendWmsDriverAccountSegments(
            final String tableName,
            final List<String> segments,
            final String tableAlias,
            final Set<Long> driverAccountIds) {
        if (CollectionUtils.isEmpty(driverAccountIds)) {
            return;
        }
        if ("wms_loading_order".equals(tableName)
                || "wms_delivery_order".equals(tableName)
                || "wms_driver_sign_record".equals(tableName)
                || "wms_return_handover".equals(tableName)) {
            appendInSegment(segments, tableAlias, "driver_account_id", driverAccountIds);
        }
    }

    private void appendWmsDeliveryOrderStationSegment(
            final String tableName,
            final List<String> segments,
            final String tableAlias,
            final Set<Long> stationIds) {
        if (CollectionUtils.isEmpty(stationIds) || !"wms_delivery_order".equals(tableName)) {
            return;
        }
        final String valueText = stationIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("EXISTS (SELECT 1 FROM wms_delivery_station dwdo_station WHERE dwdo_station.delivery_id = "
                + tableAlias + ".id AND dwdo_station.station_id IN (" + valueText
                + ") AND dwdo_station.is_deleted = 0)");
    }

    private void appendWmsStationLeaderSegment(
            final List<String> segments,
            final String tableAlias,
            final Set<Long> leaderIds) {
        if (CollectionUtils.isEmpty(leaderIds)) {
            return;
        }
        final String valueText = leaderIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        segments.add("EXISTS (SELECT 1 FROM usr_station_leader dwsl WHERE dwsl.station_id = "
                + tableAlias + ".station_id AND dwsl.leader_id IN (" + valueText
                + ") AND dwsl.is_deleted = 0)");
    }

    private void appendWmsDirectStationSegment(
            final String tableName,
            final List<String> segments,
            final String tableAlias,
            final Set<Long> stationIds) {
        if ("wms_return_handover".equals(tableName)) {
            appendInSegment(segments, tableAlias, "station_id", stationIds);
        }
    }

    private void appendWmsDirectStationLeaderSegment(
            final String tableName,
            final List<String> segments,
            final String tableAlias,
            final Set<Long> leaderIds) {
        if (!"wms_return_handover".equals(tableName)) {
            return;
        }
        appendWmsStationLeaderSegment(segments, tableAlias, leaderIds);
    }

    private void appendWmsWarehouseCityCondition(
            final List<String> conditions,
            final String warehouseIdExpression,
            final Set<Long> cityIds) {
        if (CollectionUtils.isEmpty(cityIds)) {
            return;
        }
        final String valueText = cityIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        conditions.add("EXISTS (SELECT 1 FROM wms_warehouse dwc_wh WHERE dwc_wh.id = "
                + warehouseIdExpression + " AND dwc_wh.city_id IN (" + valueText
                + ") AND dwc_wh.is_deleted = 0)");
    }
}
