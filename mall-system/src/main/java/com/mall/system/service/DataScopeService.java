package com.mall.system.service;

import com.mall.api.system.dto.DataScopeDTO;
import com.mall.common.security.DataScopeContext;
import java.util.List;

public interface DataScopeService {

    List<DataScopeDTO> listByAccountId(Long accountId);

    DataScopeContext.DataScope buildContextByAccountId(Long accountId);
}
