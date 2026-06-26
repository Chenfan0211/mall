package com.mall.system.controller;

import com.mall.api.system.dto.DictItemDTO;
import com.mall.api.system.vo.DictItemQueryVO;
import com.mall.common.result.Result;
import com.mall.system.service.DictService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/system/dicts")
public class DictController {

    private final DictService dictService;

    public DictController(final DictService dictService) {
        this.dictService = dictService;
    }

    @GetMapping("/items")
    @PreAuthorize("hasAuthority('system:dict:view')")
    public Result<List<DictItemDTO>> listItems(@Valid final DictItemQueryVO query) {
        return Result.success(dictService.listItems(query));
    }
}
