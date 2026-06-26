package com.mall.system.service;

import com.mall.api.system.dto.RoleDTO;
import com.mall.api.system.vo.RoleQueryVO;
import com.mall.common.page.PageResult;
import java.util.List;

public interface RoleService {

    PageResult<RoleDTO> pageRoles(RoleQueryVO query);

    List<RoleDTO> listByAccountId(Long accountId);

    List<String> listAuthoritiesByAccountId(Long accountId);
}
