package com.mall.system.service;

import com.mall.api.system.dto.MenuDTO;
import java.util.List;

public interface MenuService {

    List<MenuDTO> listVisibleMenus(String portalCode);

    List<MenuDTO> listByAccountIdAndPortal(Long accountId, String portalCode);
}
