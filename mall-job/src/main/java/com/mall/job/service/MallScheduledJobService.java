package com.mall.job.service;

public interface MallScheduledJobService {

    long closeTimeoutOrders();

    long createPayReminderMessages();

    long closeDuePublishedPeriods();

    long retryFailedMessages();

    long retryFailedRefunds();

    long retryFailedSplits();

    long createReconciliationBatches();

    long refreshReportSnapshots();

    long markOverdueTodos();

    long createSupplierExpiryTodos();

    long detectLowInventory();

    long syncSearchSuggestWords();
}
