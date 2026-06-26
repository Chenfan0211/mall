package com.mall.api.user.dto;

import com.mall.api.product.dto.ProductDTO;
import java.util.List;

public class UserProductDetailDTO {

    private ProductDTO product;
    private List<UserProductSkuDTO> skus;
    private Long favoriteFlag;
    private Long commentCount;

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(final ProductDTO product) {
        this.product = product;
    }

    public List<UserProductSkuDTO> getSkus() {
        return skus;
    }

    public void setSkus(final List<UserProductSkuDTO> skus) {
        this.skus = skus;
    }

    public Long getFavoriteFlag() {
        return favoriteFlag;
    }

    public void setFavoriteFlag(final Long favoriteFlag) {
        this.favoriteFlag = favoriteFlag;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(final Long commentCount) {
        this.commentCount = commentCount;
    }
}
