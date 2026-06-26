package com.mall.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.api.system.dto.DictItemDTO;
import com.mall.api.system.vo.DictItemQueryVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.CommonErrorCode;
import com.mall.system.convert.SystemConvert;
import com.mall.system.entity.SysDictItem;
import com.mall.system.mapper.SysDictItemMapper;
import com.mall.system.service.DictService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class DictServiceImpl implements DictService {

    private final SysDictItemMapper dictItemMapper;
    private final SystemConvert systemConvert;

    public DictServiceImpl(final SysDictItemMapper dictItemMapper, final SystemConvert systemConvert) {
        this.dictItemMapper = dictItemMapper;
        this.systemConvert = systemConvert;
    }

    @Override
    public List<DictItemDTO> listItems(final DictItemQueryVO query) {
        if (query == null || !StringUtils.hasText(query.getDictCode())) {
            throw new BusinessException(CommonErrorCode.PARAM_ERROR, "字典编码不能为空");
        }
        final LambdaQueryWrapper<SysDictItem> wrapper = new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictCode, query.getDictCode())
                .eq(query.getStatus() != null, SysDictItem::getStatus, query.getStatus())
                .orderByAsc(SysDictItem::getSortNo, SysDictItem::getId);
        return systemConvert.toDictItemDTOList(dictItemMapper.selectList(wrapper));
    }
}
