package com.mall.auth.service.impl;

import com.mall.api.auth.dto.LoginDTO;
import com.mall.api.auth.vo.PasswordLoginVO;
import com.mall.api.system.dto.AccountDTO;
import com.mall.api.system.dto.DataScopeDTO;
import com.mall.auth.service.AuthService;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.CommonErrorCode;
import com.mall.common.security.DataScopeType;
import com.mall.common.security.LoginTokenCodec;
import com.mall.common.security.LoginTokenDTO;
import com.mall.system.convert.SystemConvert;
import com.mall.system.entity.SysAccount;
import com.mall.system.service.AccountService;
import com.mall.system.service.DataScopeService;
import com.mall.system.service.MenuService;
import com.mall.system.service.RoleService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private static final Long TOKEN_EXPIRES_SECONDS = 7200L;

    private final AccountService accountService;
    private final RoleService roleService;
    private final MenuService menuService;
    private final DataScopeService dataScopeService;
    private final SystemConvert systemConvert;
    private final PasswordEncoder passwordEncoder;
    private final LoginTokenCodec loginTokenCodec;

    public AuthServiceImpl(
            final AccountService accountService,
            final RoleService roleService,
            final MenuService menuService,
            final DataScopeService dataScopeService,
            final SystemConvert systemConvert,
            final PasswordEncoder passwordEncoder,
            final LoginTokenCodec loginTokenCodec) {
        this.accountService = accountService;
        this.roleService = roleService;
        this.menuService = menuService;
        this.dataScopeService = dataScopeService;
        this.systemConvert = systemConvert;
        this.passwordEncoder = passwordEncoder;
        this.loginTokenCodec = loginTokenCodec;
    }

    @Override
    public LoginDTO passwordLogin(final PasswordLoginVO loginVO) {
        final SysAccount account = accountService.findEnabledAccountForLogin(
                loginVO.getUsername(), loginVO.getPortalCode());
        if ("TEST_HASH_CHANGE_ME".equals(account.getPasswordHash())) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED, "测试账号密码未初始化，请执行 Sql/mall.sql 末尾密码回填SQL");
        }
        if (!passwordEncoder.matches(loginVO.getPassword(), account.getPasswordHash())) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED, "账号或密码错误");
        }
        final AccountDTO accountDTO = systemConvert.toAccountDTO(account);
        final List<String> authorities = roleService.listAuthoritiesByAccountId(account.getId());
        final List<DataScopeDTO> dataScopes = dataScopeService.listByAccountId(account.getId());
        final LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccessToken(loginTokenCodec.encode(buildLoginToken(account, loginVO.getPortalCode(), authorities, dataScopes)));
        loginDTO.setTokenType("Bearer");
        loginDTO.setExpiresIn(TOKEN_EXPIRES_SECONDS);
        loginDTO.setAccount(accountDTO);
        loginDTO.setRoles(roleService.listByAccountId(account.getId()));
        loginDTO.setMenus(menuService.listByAccountIdAndPortal(account.getId(), loginVO.getPortalCode()));
        loginDTO.setDataScopes(dataScopes);
        loginDTO.setAuthorities(authorities);
        return loginDTO;
    }

    private LoginTokenDTO buildLoginToken(
            final SysAccount account,
            final String portalCode,
            final List<String> authorities,
            final List<DataScopeDTO> dataScopes) {
        final LoginTokenDTO tokenDTO = new LoginTokenDTO();
        tokenDTO.setAccountId(account.getId());
        tokenDTO.setUsername(account.getUsername());
        tokenDTO.setPortalCode(portalCode);
        tokenDTO.setAuthorities(authorities);
        appendDataScopes(tokenDTO, dataScopes);
        return tokenDTO;
    }

    private void appendDataScopes(final LoginTokenDTO tokenDTO, final List<DataScopeDTO> dataScopes) {
        final List<Long> cityIds = new ArrayList<>();
        final List<Long> supplierIds = new ArrayList<>();
        final List<Long> stationIds = new ArrayList<>();
        final List<Long> warehouseIds = new ArrayList<>();
        final List<Long> driverIds = new ArrayList<>();
        final List<Long> leaderIds = new ArrayList<>();
        for (final DataScopeDTO dataScope : dataScopes == null ? List.<DataScopeDTO>of() : dataScopes) {
            if (dataScope == null) {
                continue;
            }
            if (DataScopeType.ALL_PLATFORM.getCode().equals(dataScope.getScopeType())) {
                tokenDTO.setAllPlatform(true);
            } else if (DataScopeType.CITY.getCode().equals(dataScope.getScopeType())) {
                cityIds.add(dataScope.getScopeObjectId());
            } else if (DataScopeType.SUPPLIER.getCode().equals(dataScope.getScopeType())) {
                supplierIds.add(dataScope.getScopeObjectId());
            } else if (DataScopeType.STATION.getCode().equals(dataScope.getScopeType())) {
                stationIds.add(dataScope.getScopeObjectId());
            } else if (DataScopeType.WAREHOUSE.getCode().equals(dataScope.getScopeType())) {
                warehouseIds.add(dataScope.getScopeObjectId());
            } else if (DataScopeType.DRIVER.getCode().equals(dataScope.getScopeType())) {
                driverIds.add(dataScope.getScopeObjectId());
            } else if (DataScopeType.LEADER.getCode().equals(dataScope.getScopeType())) {
                leaderIds.add(dataScope.getScopeObjectId());
            }
        }
        tokenDTO.setCityIds(cityIds);
        tokenDTO.setSupplierIds(supplierIds);
        tokenDTO.setStationIds(stationIds);
        tokenDTO.setWarehouseIds(warehouseIds);
        tokenDTO.setDriverIds(driverIds);
        tokenDTO.setLeaderIds(leaderIds);
    }
}
