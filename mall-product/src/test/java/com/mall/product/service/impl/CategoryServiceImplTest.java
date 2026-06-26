package com.mall.product.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mall.api.product.dto.CategoryDTO;
import com.mall.api.product.vo.CategoryQueryVO;
import com.mall.product.convert.ProductConvert;
import com.mall.product.entity.PrdCategory;
import com.mall.product.mapper.PrdCategoryMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private PrdCategoryMapper categoryMapper;

    @Mock
    private ProductConvert productConvert;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void listCategories_success() {
        final CategoryQueryVO query = new CategoryQueryVO();
        query.setCategoryType(1L);
        final PrdCategory category = new PrdCategory();
        category.setId(750001L);
        category.setCategoryName("水果鲜食");
        final CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(750001L);
        categoryDTO.setCategoryName("水果鲜食");
        when(categoryMapper.selectList(any())).thenReturn(List.of(category));
        when(productConvert.toCategoryDTO(category)).thenReturn(categoryDTO);

        final List<CategoryDTO> result = categoryService.listCategories(query);

        assertEquals(1, result.size());
        assertEquals("水果鲜食", result.get(0).getCategoryName());
    }
}
