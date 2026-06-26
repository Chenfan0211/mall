package com.mall.system.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mall.api.system.dto.MenuDTO;
import com.mall.system.convert.SystemConvert;
import com.mall.system.entity.SysMenu;
import com.mall.system.mapper.SysMenuMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {

    @Mock
    private SysMenuMapper menuMapper;

    @Mock
    private SystemConvert systemConvert;

    @InjectMocks
    private MenuServiceImpl menuService;

    @Test
    void listVisibleMenus_success() {
        final SysMenu menu = new SysMenu();
        menu.setId(810001L);
        menu.setMenuName("运营驾驶舱");
        final MenuDTO menuDTO = new MenuDTO();
        menuDTO.setId(810001L);
        menuDTO.setMenuName("运营驾驶舱");
        when(menuMapper.selectList(any())).thenReturn(List.of(menu));
        when(systemConvert.toMenuDTOList(List.of(menu))).thenReturn(List.of(menuDTO));

        final List<MenuDTO> result = menuService.listVisibleMenus("admin");

        assertEquals(1, result.size());
        assertEquals("运营驾驶舱", result.get(0).getMenuName());
    }
}
