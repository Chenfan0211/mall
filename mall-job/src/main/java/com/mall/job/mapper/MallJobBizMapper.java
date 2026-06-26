package com.mall.job.mapper;

public interface MallJobBizMapper {

    int closeTimeoutOrders();

    int releaseTimeoutOrderLocks();

    int createPayReminderMessages();

    int generateVirtualPurchaseOrders();

    int generateVirtualPurchaseItems();

    int generateWarehouseWaves();

    int generatePickTasks();

    int generateLoadingOrders();

    int closeDuePublishedPeriods();

    int retryFailedMessages();

    int retryFailedRefunds();

    int retryFailedSplits();

    int createReconciliationBatches();

    int refreshReportSnapshots();

    int markOverdueTodos();

    int createQualificationExpiryTodos();

    int createContractExpiryTodos();

    int detectLowInventory();

    int syncSearchSuggestWords();
}
