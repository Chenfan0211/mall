package com.mall.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.api.system.dto.RoleDTO;
import com.mall.api.system.vo.RoleQueryVO;
import com.mall.common.page.PageResult;
import com.mall.system.convert.SystemConvert;
import com.mall.system.entity.SysRole;
import com.mall.system.mapper.SysRoleMapper;
import com.mall.system.service.RoleService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final SysRoleMapper roleMapper;
    private final SystemConvert systemConvert;

    public RoleServiceImpl(final SysRoleMapper roleMapper, final SystemConvert systemConvert) {
        this.roleMapper = roleMapper;
        this.systemConvert = systemConvert;
    }

    @Override
    public PageResult<RoleDTO> pageRoles(final RoleQueryVO query) {
        final RoleQueryVO safeQuery = query == null ? new RoleQueryVO() : query;
        final LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .eq(safeQuery.getStatus() != null, SysRole::getStatus, safeQuery.getStatus())
                .eq(StringUtils.hasText(safeQuery.getPortalCode()), SysRole::getPortalCode, safeQuery.getPortalCode())
                .orderByAsc(SysRole::getRoleType, SysRole::getId);
        final Page<SysRole> page = roleMapper.selectPage(
                Page.of(safeQuery.getPageNum(), safeQuery.getPageSize()), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords().stream().map(systemConvert::toRoleDTO).toList());
    }

    @Override
    public List<RoleDTO> listByAccountId(final Long accountId) {
        return systemConvert.toRoleDTOList(roleMapper.selectByAccountId(accountId));
    }

    @Override
    public List<String> listAuthoritiesByAccountId(final Long accountId) {
        return roleMapper.selectAuthoritiesByAccountId(accountId);
    }
}
