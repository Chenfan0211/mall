package com.mall.system.service;

import com.mall.api.system.dto.DictItemDTO;
import com.mall.api.system.vo.DictItemQueryVO;
import java.util.List;

public interface DictService {

    List<DictItemDTO> listItems(DictItemQueryVO query);
}
