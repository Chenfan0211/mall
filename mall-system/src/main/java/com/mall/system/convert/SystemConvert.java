package com.mall.system.convert;

import com.mall.api.system.dto.AccountDTO;
import com.mall.api.system.dto.DataScopeDTO;
import com.mall.api.system.dto.DictItemDTO;
import com.mall.api.system.dto.MenuDTO;
import com.mall.api.system.dto.RoleDTO;
import com.mall.system.entity.SysAccount;
import com.mall.system.entity.SysDataScope;
import com.mall.system.entity.SysDictItem;
import com.mall.system.entity.SysMenu;
import com.mall.system.entity.SysRole;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SystemConvert {

    AccountDTO toAccountDTO(SysAccount entity);

    RoleDTO toRoleDTO(SysRole entity);

    MenuDTO toMenuDTO(SysMenu entity);

    DictItemDTO toDictItemDTO(SysDictItem entity);

    DataScopeDTO toDataScopeDTO(SysDataScope entity);

    List<RoleDTO> toRoleDTOList(List<SysRole> entities);

    List<MenuDTO> toMenuDTOList(List<SysMenu> entities);

    List<DictItemDTO> toDictItemDTOList(List<SysDictItem> entities);

    List<DataScopeDTO> toDataScopeDTOList(List<SysDataScope> entities);
}
