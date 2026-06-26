package com.mall.system.service;

import com.mall.api.system.dto.AccountDTO;
import com.mall.api.system.vo.AccountQueryVO;
import com.mall.common.page.PageResult;
import com.mall.system.entity.SysAccount;

public interface AccountService {

    PageResult<AccountDTO> pageAccounts(AccountQueryVO query);

    AccountDTO getAccount(Long id);

    SysAccount findEnabledAccountForLogin(String username, String portalCode);
}
