package com.mall.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Set;
import net.sf.jsqlparser.schema.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class MallDataPermissionHandlerTest {

    private final MallDataPermissionHandler handler = new MallDataPermissionHandler();

    @AfterEach
    void tearDown() {
        DataScopeContext.clear();
    }

    @Test
    void getSqlSegment_allPlatform_returnNull() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setAllPlatform(true);
        DataScopeContext.set(dataScope);

        assertNull(handler.getSqlSegment(new Table("prd_product"), null, "test"));
    }

    @Test
    void getSqlSegment_productWithCityAndSupplier_returnScopeExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setCityIds(Set.of(440100L));
        dataScope.setSupplierIds(Set.of(710001L));
        DataScopeContext.set(dataScope);

        assertEquals("prd_product.city_id IN (440100) OR prd_product.supplier_id IN (710001)",
                handler.getSqlSegment(new Table("prd_product"), null, "test").toString());
    }

    @Test
    void getSqlSegment_salePeriodWithSupplier_returnExistsExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setSupplierIds(Set.of(710001L));
        DataScopeContext.set(dataScope);

        assertEquals("EXISTS (SELECT 1 FROM sale_publish_sku dps WHERE dps.period_id = sale_publish_period.id "
                        + "AND dps.supplier_id IN (710001) AND dps.is_deleted = 0)",
                handler.getSqlSegment(new Table("sale_publish_period"), null, "test").toString());
    }

    @Test
    void getSqlSegment_productWithoutScope_returnDenyAllExpression() {
        DataScopeContext.set(new DataScopeContext.DataScope());

        assertEquals("1 = 0", handler.getSqlSegment(new Table("prd_product"), null, "test").toString());
    }

    @Test
    void getSqlSegment_categoryBaseData_returnNull() {
        DataScopeContext.set(new DataScopeContext.DataScope());

        assertNull(handler.getSqlSegment(new Table("prd_category"), null, "test"));
    }

    @Test
    void getSqlSegment_stationWithStationScope_returnStationExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setStationIds(Set.of(720001L));
        DataScopeContext.set(dataScope);

        assertEquals("usr_station.id IN (720001)",
                handler.getSqlSegment(new Table("usr_station"), null, "test").toString());
    }

    @Test
    void getSqlSegment_leaderWithStationScope_returnStationLeaderExistsExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setStationIds(Set.of(720001L));
        DataScopeContext.set(dataScope);

        assertEquals("EXISTS (SELECT 1 FROM usr_station_leader dlss WHERE dlss.leader_id = "
                        + "usr_leader.id AND dlss.is_deleted = 0 AND dlss.station_id IN (720001))",
                handler.getSqlSegment(new Table("usr_leader"), null, "test").toString());
    }

    @Test
    void getSqlSegment_messageWithStationScope_returnReceiverExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setStationIds(Set.of(720001L));
        DataScopeContext.set(dataScope);

        assertEquals("(msg_record.receiver_type = 4 AND msg_record.receiver_id IN (720001))",
                handler.getSqlSegment(new Table("msg_record"), null, "test").toString());
    }

    @Test
    void getSqlSegment_orderWithStation_returnStationScopeExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setStationIds(Set.of(720001L));
        DataScopeContext.set(dataScope);

        assertEquals("ord_order.station_id IN (720001)",
                handler.getSqlSegment(new Table("ord_order"), null, "test").toString());
    }

    @Test
    void getSqlSegment_orderItemWithSupplier_returnSupplierScopeExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setSupplierIds(Set.of(710001L));
        DataScopeContext.set(dataScope);

        assertEquals("ord_order_item.supplier_id IN (710001)",
                handler.getSqlSegment(new Table("ord_order_item"), null, "test").toString());
    }

    @Test
    void getSqlSegment_orderItemWithLeader_returnOrderLeaderExistsExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setLeaderIds(Set.of(730001L));
        DataScopeContext.set(dataScope);

        assertEquals("EXISTS (SELECT 1 FROM ord_order doil_order WHERE doil_order.id = "
                        + "ord_order_item.order_id AND doil_order.leader_id IN (730001) "
                        + "AND doil_order.is_deleted = 0)",
                handler.getSqlSegment(new Table("ord_order_item"), null, "test").toString());
    }

    @Test
    void getSqlSegment_payTradeWithCity_returnOrderExistsExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setCityIds(Set.of(440100L));
        DataScopeContext.set(dataScope);

        assertEquals("EXISTS (SELECT 1 FROM ord_order dpo WHERE dpo.id = pay_trade.order_id "
                        + "AND dpo.is_deleted = 0 AND dpo.city_id IN (440100))",
                handler.getSqlSegment(new Table("pay_trade"), null, "test").toString());
    }

    @Test
    void getSqlSegment_refundTradeWithSupplier_returnOrderItemExistsExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setSupplierIds(Set.of(710001L));
        DataScopeContext.set(dataScope);

        assertEquals("EXISTS (SELECT 1 FROM ord_order_item dpoi WHERE dpoi.order_id = "
                        + "pay_refund_trade.order_id AND dpoi.is_deleted = 0 "
                        + "AND dpoi.supplier_id IN (710001))",
                handler.getSqlSegment(new Table("pay_refund_trade"), null, "test").toString());
    }

    @Test
    void getSqlSegment_afterSaleWithStation_returnStationScopeExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setStationIds(Set.of(720001L));
        DataScopeContext.set(dataScope);

        assertEquals("afs_after_sale.station_id IN (720001)",
                handler.getSqlSegment(new Table("afs_after_sale"), null, "test").toString());
    }

    @Test
    void getSqlSegment_afterSaleItemWithSupplier_returnSupplierScopeExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setSupplierIds(Set.of(710001L));
        DataScopeContext.set(dataScope);

        assertEquals("afs_after_sale_item.supplier_id IN (710001)",
                handler.getSqlSegment(new Table("afs_after_sale_item"), null, "test").toString());
    }

    @Test
    void getSqlSegment_supplierWithCityAndSupplier_returnScopeExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setCityIds(Set.of(440100L));
        dataScope.setSupplierIds(Set.of(710001L));
        DataScopeContext.set(dataScope);

        assertEquals("sup_supplier.city_id IN (440100) OR sup_supplier.id IN (710001)",
                handler.getSqlSegment(new Table("sup_supplier"), null, "test").toString());
    }

    @Test
    void getSqlSegment_purchaseOrderWithWarehouse_returnWarehouseScopeExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setWarehouseIds(Set.of(50001L));
        DataScopeContext.set(dataScope);

        assertEquals("pur_purchase_order.warehouse_id IN (50001)",
                handler.getSqlSegment(new Table("pur_purchase_order"), null, "test").toString());
    }

    @Test
    void getSqlSegment_purchaseAuditLogWithSupplier_returnPurchaseExistsExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setSupplierIds(Set.of(710001L));
        DataScopeContext.set(dataScope);

        assertEquals("EXISTS (SELECT 1 FROM pur_purchase_order dpl_po WHERE dpl_po.id = "
                        + "pur_purchase_audit_log.purchase_id AND dpl_po.is_deleted = 0 "
                        + "AND dpl_po.supplier_id IN (710001))",
                handler.getSqlSegment(new Table("pur_purchase_audit_log"), null, "test").toString());
    }

    @Test
    void getSqlSegment_deliveryItemWithCity_returnNestedPurchaseExistsExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setCityIds(Set.of(440100L));
        DataScopeContext.set(dataScope);

        assertEquals("EXISTS (SELECT 1 FROM pur_delivery_order ddi_city_do WHERE ddi_city_do.id = "
                        + "pur_delivery_item.delivery_id AND ddi_city_do.is_deleted = 0 AND EXISTS "
                        + "(SELECT 1 FROM pur_purchase_order ddi_city_po WHERE ddi_city_po.id = "
                        + "ddi_city_do.purchase_id AND ddi_city_po.city_id IN (440100) "
                        + "AND ddi_city_po.is_deleted = 0))",
                handler.getSqlSegment(new Table("pur_delivery_item"), null, "test").toString());
    }

    @Test
    void getSqlSegment_financeAccountWithSupplier_returnSupplierSubjectExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setSupplierIds(Set.of(710001L));
        DataScopeContext.set(dataScope);

        assertEquals("(fin_account.subject_type = 1 AND fin_account.subject_id IN (710001))",
                handler.getSqlSegment(new Table("fin_account"), null, "test").toString());
    }

    @Test
    void getSqlSegment_commissionDetailWithStation_returnStationScopeExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setStationIds(Set.of(720001L));
        DataScopeContext.set(dataScope);

        assertEquals("fin_commission_detail.station_id IN (720001) OR EXISTS "
                        + "(SELECT 1 FROM ord_order dfc_o WHERE dfc_o.id = fin_commission_detail.order_id "
                        + "AND dfc_o.is_deleted = 0 AND dfc_o.station_id IN (720001))",
                handler.getSqlSegment(new Table("fin_commission_detail"), null, "test").toString());
    }

    @Test
    void getSqlSegment_splitFlowWithLeader_returnLeaderCommissionSubjectExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setLeaderIds(Set.of(730001L));
        DataScopeContext.set(dataScope);

        assertEquals("(fin_split_flow.subject_type = 2 AND fin_split_flow.subject_id IN (730001)) OR EXISTS "
                        + "(SELECT 1 FROM fin_commission_detail dfs_c WHERE dfs_c.id = "
                        + "fin_split_flow.commission_id AND dfs_c.is_deleted = 0 AND "
                        + "(dfs_c.subject_type = 2 AND dfs_c.subject_id IN (730001))) OR EXISTS "
                        + "(SELECT 1 FROM ord_order dfs_o WHERE dfs_o.id = fin_split_flow.order_id "
                        + "AND dfs_o.is_deleted = 0 AND dfs_o.leader_id IN (730001))",
                handler.getSqlSegment(new Table("fin_split_flow"), null, "test").toString());
    }

    @Test
    void getSqlSegment_supplierSettlementWithSupplier_returnSupplierScopeExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setSupplierIds(Set.of(710001L));
        DataScopeContext.set(dataScope);

        assertEquals("fin_supplier_settlement.supplier_id IN (710001)",
                handler.getSqlSegment(new Table("fin_supplier_settlement"), null, "test").toString());
    }

    @Test
    void getSqlSegment_reconciliationBatchWithScopedUser_returnDenyAllExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setSupplierIds(Set.of(710001L));
        DataScopeContext.set(dataScope);

        assertEquals("1 = 0",
                handler.getSqlSegment(new Table("fin_reconciliation_batch"), null, "test").toString());
    }

    @Test
    void getSqlSegment_financeAccountWithCity_returnSubjectCityExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setCityIds(Set.of(440100L));
        DataScopeContext.set(dataScope);

        assertEquals("(fin_account.subject_type = 1 AND EXISTS "
                        + "(SELECT 1 FROM sup_supplier dfs_sup WHERE dfs_sup.id = fin_account.subject_id "
                        + "AND dfs_sup.city_id IN (440100) AND dfs_sup.is_deleted = 0)) OR "
                        + "(fin_account.subject_type = 2 AND EXISTS "
                        + "(SELECT 1 FROM usr_station dfs_st WHERE dfs_st.id = fin_account.subject_id "
                        + "AND dfs_st.city_id IN (440100) AND dfs_st.is_deleted = 0)) OR "
                        + "(fin_account.subject_type = 3 AND EXISTS "
                        + "(SELECT 1 FROM usr_station_leader dfs_sl WHERE dfs_sl.leader_id = "
                        + "fin_account.subject_id AND dfs_sl.is_deleted = 0 AND EXISTS "
                        + "(SELECT 1 FROM usr_station dfs_lst WHERE dfs_lst.id = dfs_sl.station_id "
                        + "AND dfs_lst.city_id IN (440100) AND dfs_lst.is_deleted = 0)))",
                handler.getSqlSegment(new Table("fin_account"), null, "test").toString());
    }

    @Test
    void getSqlSegment_splitFlowWithCity_returnOrderCityExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setCityIds(Set.of(440100L));
        DataScopeContext.set(dataScope);

        assertEquals("EXISTS (SELECT 1 FROM ord_order dfs_o WHERE dfs_o.id = "
                        + "fin_split_flow.order_id AND dfs_o.is_deleted = 0 "
                        + "AND dfs_o.city_id IN (440100))",
                handler.getSqlSegment(new Table("fin_split_flow"), null, "test").toString());
    }

    @Test
    void getSqlSegment_wmsWarehouseWithCity_returnCityScopeExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setCityIds(Set.of(440100L));
        DataScopeContext.set(dataScope);

        assertEquals("wms_warehouse.city_id IN (440100)",
                handler.getSqlSegment(new Table("wms_warehouse"), null, "test").toString());
    }

    @Test
    void getSqlSegment_wmsInventoryWithWarehouse_returnWarehouseScopeExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setWarehouseIds(Set.of(50001L));
        DataScopeContext.set(dataScope);

        assertEquals("wms_inventory.warehouse_id IN (50001)",
                handler.getSqlSegment(new Table("wms_inventory"), null, "test").toString());
    }

    @Test
    void getSqlSegment_wmsDeliveryWithDriver_returnDriverScopeExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setDriverIds(Set.of(700011L));
        DataScopeContext.set(dataScope);

        assertEquals("wms_delivery_order.driver_account_id IN (700011)",
                handler.getSqlSegment(new Table("wms_delivery_order"), null, "test").toString());
    }

    @Test
    void getSqlSegment_wmsDeliveryWithStation_returnDeliveryStationExistsExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setStationIds(Set.of(720001L));
        DataScopeContext.set(dataScope);

        assertEquals("EXISTS (SELECT 1 FROM wms_delivery_station dwdo_station WHERE "
                        + "dwdo_station.delivery_id = wms_delivery_order.id "
                        + "AND dwdo_station.station_id IN (720001) AND dwdo_station.is_deleted = 0)",
                handler.getSqlSegment(new Table("wms_delivery_order"), null, "test").toString());
    }

    @Test
    void getSqlSegment_wmsDeliveryStationWithLeader_returnStationLeaderExistsExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setLeaderIds(Set.of(730001L));
        DataScopeContext.set(dataScope);

        assertEquals("EXISTS (SELECT 1 FROM usr_station_leader dwsl WHERE dwsl.station_id = "
                        + "wms_delivery_station.station_id AND dwsl.leader_id IN (730001) "
                        + "AND dwsl.is_deleted = 0)",
                handler.getSqlSegment(new Table("wms_delivery_station"), null, "test").toString());
    }

    @Test
    void getSqlSegment_wmsLoadingItemWithStation_returnStationScopeExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setStationIds(Set.of(720001L));
        DataScopeContext.set(dataScope);

        assertEquals("wms_loading_item.station_id IN (720001)",
                handler.getSqlSegment(new Table("wms_loading_item"), null, "test").toString());
    }

    @Test
    void getSqlSegment_wmsPickItemWithWarehouse_returnPickTaskExistsExpression() {
        final DataScopeContext.DataScope dataScope = new DataScopeContext.DataScope();
        dataScope.setWarehouseIds(Set.of(50001L));
        DataScopeContext.set(dataScope);

        assertEquals("EXISTS (SELECT 1 FROM wms_pick_task dwpi_pick WHERE dwpi_pick.id = "
                        + "wms_pick_item.pick_id AND dwpi_pick.is_deleted = 0 "
                        + "AND dwpi_pick.warehouse_id IN (50001))",
                handler.getSqlSegment(new Table("wms_pick_item"), null, "test").toString());
    }
}
