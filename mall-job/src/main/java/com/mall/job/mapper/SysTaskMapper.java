package com.mall.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.job.entity.SysTask;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Param;

public interface SysTaskMapper extends BaseMapper<SysTask> {

    SysTask selectByIdempotentKey(@Param("idempotentKey") String idempotentKey);

    int markRunning(@Param("idempotentKey") String idempotentKey);

    int markSuccess(@Param("idempotentKey") String idempotentKey);

    int markFailed(
            @Param("idempotentKey") String idempotentKey,
            @Param("failReason") String failReason,
            @Param("nextRetryTime") LocalDateTime nextRetryTime);
}
