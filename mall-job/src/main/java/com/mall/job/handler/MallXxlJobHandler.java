package com.mall.job.handler;

import com.mall.job.service.MallScheduledJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

@Component
public class MallXxlJobHandler {

    private final MallScheduledJobService mallScheduledJobService;

    public MallXxlJobHandler(final MallScheduledJobService mallScheduledJobService) {
        this.mallScheduledJobService = mallScheduledJobService;
    }

    @XxlJob("orderCloseTimeoutJob")
    public void orderCloseTimeoutJob() {
        mallScheduledJobService.closeTimeoutOrders();
    }

    @XxlJob("payRemindLastFiveMinutesJob")
    public void payRemindLastFiveMinutesJob() {
        mallScheduledJobService.createPayReminderMessages();
    }

    @XxlJob("salePeriodCutoffJob")
    public void salePeriodCutoffJob() {
        mallScheduledJobService.closeDuePublishedPeriods();
    }

    @XxlJob("messageSendRetryJob")
    public void messageSendRetryJob() {
        mallScheduledJobService.retryFailedMessages();
    }

    @XxlJob("refundRetryJob")
    public void refundRetryJob() {
        mallScheduledJobService.retryFailedRefunds();
    }

    @XxlJob("splitRetryJob")
    public void splitRetryJob() {
        mallScheduledJobService.retryFailedSplits();
    }

    @XxlJob("reconciliationBatchCreateJob")
    public void reconciliationBatchCreateJob() {
        mallScheduledJobService.createReconciliationBatches();
    }

    @XxlJob("reportSnapshotRefreshJob")
    public void reportSnapshotRefreshJob() {
        mallScheduledJobService.refreshReportSnapshots();
    }

    @XxlJob("todoSlaOverdueJob")
    public void todoSlaOverdueJob() {
        mallScheduledJobService.markOverdueTodos();
    }

    @XxlJob("supplierExpiryTodoJob")
    public void supplierExpiryTodoJob() {
        mallScheduledJobService.createSupplierExpiryTodos();
    }

    @XxlJob("lowInventoryCheckJob")
    public void lowInventoryCheckJob() {
        mallScheduledJobService.detectLowInventory();
    }

    @XxlJob("searchSuggestSyncJob")
    public void searchSuggestSyncJob() {
        mallScheduledJobService.syncSearchSuggestWords();
    }
}
