package com.mall.job.service.impl;

import com.mall.job.mapper.MallJobBizMapper;
import com.mall.job.service.MallScheduledJobService;
import com.mall.job.service.SystemTaskExecutionService;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MallScheduledJobServiceImpl implements MallScheduledJobService {

    private static final DateTimeFormatter MINUTE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final MallJobBizMapper jobBizMapper;
    private final SystemTaskExecutionService systemTaskExecutionService;
    private final Clock clock;

    public MallScheduledJobServiceImpl(
            final MallJobBizMapper jobBizMapper,
            final SystemTaskExecutionService systemTaskExecutionService,
            final Clock clock) {
        this.jobBizMapper = jobBizMapper;
        this.systemTaskExecutionService = systemTaskExecutionService;
        this.clock = clock;
    }

    @Override
    public long closeTimeoutOrders() {
        return systemTaskExecutionService.execute(
                "ORDER_CLOSE_TIMEOUT",
                "未支付订单超时关闭",
                "order",
                "ALL",
                minuteKey("ORDER_CLOSE_TIMEOUT"),
                () -> jobBizMapper.closeTimeoutOrders() + jobBizMapper.releaseTimeoutOrderLocks());
    }

    @Override
    public long createPayReminderMessages() {
        return systemTaskExecutionService.execute(
                "PAY_REMIND_LAST_FIVE_MINUTES",
                "支付最后5分钟提醒",
                "message",
                "ALL",
                minuteKey("PAY_REMIND_LAST_FIVE_MINUTES"),
                jobBizMapper::createPayReminderMessages);
    }

    @Override
    public long closeDuePublishedPeriods() {
        return systemTaskExecutionService.execute(
                "SALE_PERIOD_CUTOFF",
                "团期截团处理",
                "sale_period",
                "ALL",
                minuteKey("SALE_PERIOD_CUTOFF"),
                () -> jobBizMapper.generateVirtualPurchaseOrders()
                        + jobBizMapper.generateVirtualPurchaseItems()
                        + jobBizMapper.generateWarehouseWaves()
                        + jobBizMapper.generatePickTasks()
                        + jobBizMapper.generateLoadingOrders()
                        + jobBizMapper.closeDuePublishedPeriods());
    }

    @Override
    public long retryFailedMessages() {
        return systemTaskExecutionService.execute(
                "MESSAGE_SEND_RETRY",
                "消息发送失败重试",
                "message",
                "ALL",
                minuteKey("MESSAGE_SEND_RETRY"),
                jobBizMapper::retryFailedMessages);
    }

    @Override
    public long retryFailedRefunds() {
        return systemTaskExecutionService.execute(
                "REFUND_RETRY",
                "退款失败重试",
                "refund",
                "ALL",
                minuteKey("REFUND_RETRY"),
                jobBizMapper::retryFailedRefunds);
    }

    @Override
    public long retryFailedSplits() {
        return systemTaskExecutionService.execute(
                "SPLIT_RETRY",
                "分账失败重试",
                "split",
                "ALL",
                minuteKey("SPLIT_RETRY"),
                jobBizMapper::retryFailedSplits);
    }

    @Override
    public long createReconciliationBatches() {
        return systemTaskExecutionService.execute(
                "RECONCILIATION_BATCH_CREATE",
                "资金对账批次生成",
                "finance",
                "ALL",
                dateKey("RECONCILIATION_BATCH_CREATE"),
                jobBizMapper::createReconciliationBatches);
    }

    @Override
    public long refreshReportSnapshots() {
        return systemTaskExecutionService.execute(
                "REPORT_SNAPSHOT_REFRESH",
                "报表与驾驶舱快照刷新",
                "report",
                "ALL",
                dateKey("REPORT_SNAPSHOT_REFRESH"),
                jobBizMapper::refreshReportSnapshots);
    }

    @Override
    public long markOverdueTodos() {
        return systemTaskExecutionService.execute(
                "TODO_SLA_OVERDUE",
                "待办SLA超时升级",
                "todo",
                "ALL",
                minuteKey("TODO_SLA_OVERDUE"),
                jobBizMapper::markOverdueTodos);
    }

    @Override
    public long createSupplierExpiryTodos() {
        return systemTaskExecutionService.execute(
                "SUPPLIER_EXPIRY_TODO",
                "供应商资质合同到期提醒",
                "supplier",
                "ALL",
                dateKey("SUPPLIER_EXPIRY_TODO"),
                () -> jobBizMapper.createQualificationExpiryTodos() + jobBizMapper.createContractExpiryTodos());
    }

    @Override
    public long detectLowInventory() {
        return systemTaskExecutionService.execute(
                "LOW_INVENTORY_CHECK",
                "低库存补货检测",
                "inventory",
                "ALL",
                minuteKey("LOW_INVENTORY_CHECK"),
                jobBizMapper::detectLowInventory);
    }

    @Override
    public long syncSearchSuggestWords() {
        return systemTaskExecutionService.execute(
                "SEARCH_SUGGEST_SYNC",
                "搜索联想词同步补偿",
                "search",
                "ALL",
                minuteKey("SEARCH_SUGGEST_SYNC"),
                jobBizMapper::syncSearchSuggestWords);
    }

    private String minuteKey(final String taskCode) {
        return taskCode + ":" + MINUTE_FORMATTER.format(LocalDateTime.now(clock));
    }

    private String dateKey(final String taskCode) {
        return taskCode + ":" + DATE_FORMATTER.format(LocalDate.now(clock));
    }
}
