package com.mall.api.auth.dto;

import com.mall.api.system.dto.AccountDTO;
import com.mall.api.system.dto.DataScopeDTO;
import com.mall.api.system.dto.MenuDTO;
import com.mall.api.system.dto.RoleDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LoginDTO implements Serializable {

    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private AccountDTO account;
    private List<RoleDTO> roles = new ArrayList<>();
    private List<MenuDTO> menus = new ArrayList<>();
    private List<DataScopeDTO> dataScopes = new ArrayList<>();
    private List<String> authorities = new ArrayList<>();

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(final String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(final Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public AccountDTO getAccount() {
        return account;
    }

    public void setAccount(final AccountDTO account) {
        this.account = account;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(final List<RoleDTO> roles) {
        this.roles = roles == null ? new ArrayList<>() : roles;
    }

    public List<MenuDTO> getMenus() {
        return menus;
    }

    public void setMenus(final List<MenuDTO> menus) {
        this.menus = menus == null ? new ArrayList<>() : menus;
    }

    public List<DataScopeDTO> getDataScopes() {
        return dataScopes;
    }

    public void setDataScopes(final List<DataScopeDTO> dataScopes) {
        this.dataScopes = dataScopes == null ? new ArrayList<>() : dataScopes;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(final List<String> authorities) {
        this.authorities = authorities == null ? new ArrayList<>() : authorities;
    }
}
