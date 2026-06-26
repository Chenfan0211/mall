package com.mall.system.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.system.dto.RoleDTO;
import com.mall.api.system.vo.RoleQueryVO;
import com.mall.common.page.PageResult;
import com.mall.system.convert.SystemConvert;
import com.mall.system.entity.SysRole;
import com.mall.system.mapper.SysRoleMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private SysRoleMapper roleMapper;

    @Mock
    private SystemConvert systemConvert;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void pageRoles_success() {
        final RoleQueryVO query = new RoleQueryVO();
        query.setPageNum(1L);
        query.setPageSize(20L);
        query.setPortalCode("admin");
        final SysRole role = new SysRole();
        role.setId(700101L);
        role.setRoleName("平台管理员");
        final RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(700101L);
        roleDTO.setRoleName("平台管理员");
        final Page<SysRole> page = Page.of(1L, 20L);
        page.setTotal(1L);
        page.setRecords(List.of(role));
        when(roleMapper.selectPage(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(page);
        when(systemConvert.toRoleDTO(role)).thenReturn(roleDTO);

        final PageResult<RoleDTO> result = roleService.pageRoles(query);

        assertEquals(1L, result.getTotal());
        assertEquals("平台管理员", result.getList().get(0).getRoleName());
    }

    @Test
    void listByAccountId_success() {
        final SysRole role = new SysRole();
        role.setId(700101L);
        role.setRoleName("平台管理员");
        final RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(700101L);
        roleDTO.setRoleName("平台管理员");
        when(roleMapper.selectByAccountId(700001L)).thenReturn(List.of(role));
        when(systemConvert.toRoleDTOList(List.of(role))).thenReturn(List.of(roleDTO));

        final List<RoleDTO> result = roleService.listByAccountId(700001L);

        assertEquals(1, result.size());
        assertEquals("平台管理员", result.get(0).getRoleName());
    }

    @Test
    void listAuthoritiesByAccountId_success() {
        when(roleMapper.selectAuthoritiesByAccountId(700001L)).thenReturn(List.of("operation:todo:view"));

        final List<String> result = roleService.listAuthoritiesByAccountId(700001L);

        assertEquals("operation:todo:view", result.get(0));
    }
}
