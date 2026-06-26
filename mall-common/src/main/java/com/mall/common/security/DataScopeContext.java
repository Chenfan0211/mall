package com.mall.common.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class DataScopeContext {

    private static final ThreadLocal<DataScope> DATA_SCOPE = new ThreadLocal<>();

    private DataScopeContext() {
    }

    public static void set(final DataScope dataScope) {
        DATA_SCOPE.set(dataScope);
    }

    public static DataScope get() {
        return DATA_SCOPE.get();
    }

    public static void clear() {
        DATA_SCOPE.remove();
    }

    public static class DataScope {

        private Long accountId;
        private Set<Long> cityIds = new HashSet<>();
        private Set<Long> supplierIds = new HashSet<>();
        private Set<Long> stationIds = new HashSet<>();
        private Set<Long> warehouseIds = new HashSet<>();
        private Set<Long> driverIds = new HashSet<>();
        private Set<Long> leaderIds = new HashSet<>();
        private boolean allPlatform;

        public Long getAccountId() {
            return accountId;
        }

        public void setAccountId(final Long accountId) {
            this.accountId = accountId;
        }

        public Set<Long> getCityIds() {
            return Collections.unmodifiableSet(cityIds);
        }

        public void setCityIds(final Set<Long> cityIds) {
            this.cityIds = cityIds == null ? new HashSet<>() : new HashSet<>(cityIds);
        }

        public Set<Long> getSupplierIds() {
            return Collections.unmodifiableSet(supplierIds);
        }

        public void setSupplierIds(final Set<Long> supplierIds) {
            this.supplierIds = supplierIds == null ? new HashSet<>() : new HashSet<>(supplierIds);
        }

        public Set<Long> getStationIds() {
            return Collections.unmodifiableSet(stationIds);
        }

        public void setStationIds(final Set<Long> stationIds) {
            this.stationIds = stationIds == null ? new HashSet<>() : new HashSet<>(stationIds);
        }

        public Set<Long> getWarehouseIds() {
            return Collections.unmodifiableSet(warehouseIds);
        }

        public void setWarehouseIds(final Set<Long> warehouseIds) {
            this.warehouseIds = warehouseIds == null ? new HashSet<>() : new HashSet<>(warehouseIds);
        }

        public Set<Long> getDriverIds() {
            return Collections.unmodifiableSet(driverIds);
        }

        public void setDriverIds(final Set<Long> driverIds) {
            this.driverIds = driverIds == null ? new HashSet<>() : new HashSet<>(driverIds);
        }

        public Set<Long> getLeaderIds() {
            return Collections.unmodifiableSet(leaderIds);
        }

        public void setLeaderIds(final Set<Long> leaderIds) {
            this.leaderIds = leaderIds == null ? new HashSet<>() : new HashSet<>(leaderIds);
        }

        public boolean isAllPlatform() {
            return allPlatform;
        }

        public void setAllPlatform(final boolean allPlatform) {
            this.allPlatform = allPlatform;
        }
    }
}
