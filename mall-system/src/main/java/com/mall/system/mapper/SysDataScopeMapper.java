package com.mall.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.system.entity.SysDataScope;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysDataScopeMapper extends BaseMapper<SysDataScope> {

    List<SysDataScope> selectByAccountId(@Param("accountId") Long accountId);
}
