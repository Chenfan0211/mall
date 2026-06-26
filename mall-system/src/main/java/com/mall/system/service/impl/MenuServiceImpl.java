package com.mall.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.api.system.dto.MenuDTO;
import com.mall.system.convert.SystemConvert;
import com.mall.system.entity.SysMenu;
import com.mall.system.mapper.SysMenuMapper;
import com.mall.system.service.MenuService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class MenuServiceImpl implements MenuService {

    private static final Long STATUS_ENABLED = 1L;
    private static final Long VISIBLE = 1L;

    private final SysMenuMapper menuMapper;
    private final SystemConvert systemConvert;

    public MenuServiceImpl(final SysMenuMapper menuMapper, final SystemConvert systemConvert) {
        this.menuMapper = menuMapper;
        this.systemConvert = systemConvert;
    }

    @Override
    public List<MenuDTO> listVisibleMenus(final String portalCode) {
        final LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<SysMenu>()
                .eq(StringUtils.hasText(portalCode), SysMenu::getPortalCode, portalCode)
                .eq(SysMenu::getVisibleStatus, VISIBLE)
                .eq(SysMenu::getStatus, STATUS_ENABLED)
                .orderByAsc(SysMenu::getParentId, SysMenu::getSortNo, SysMenu::getId);
        return systemConvert.toMenuDTOList(menuMapper.selectList(wrapper));
    }

    @Override
    public List<MenuDTO> listByAccountIdAndPortal(final Long accountId, final String portalCode) {
        return systemConvert.toMenuDTOList(menuMapper.selectByAccountIdAndPortal(accountId, portalCode));
    }
}
