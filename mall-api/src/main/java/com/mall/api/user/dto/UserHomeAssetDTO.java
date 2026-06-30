package com.mall.api.user.dto;

import java.util.ArrayList;
import java.util.List;

public class UserHomeAssetDTO {

    private List<UserHomeCategoryAssetDTO> categories = new ArrayList<>();
    private List<UserHomeBannerDTO> banners = new ArrayList<>();
    private List<UserHomePromoDTO> promos = new ArrayList<>();

    public List<UserHomeCategoryAssetDTO> getCategories() {
        return categories;
    }

    public void setCategories(final List<UserHomeCategoryAssetDTO> categories) {
        this.categories = categories;
    }

    public List<UserHomeBannerDTO> getBanners() {
        return banners;
    }

    public void setBanners(final List<UserHomeBannerDTO> banners) {
        this.banners = banners;
    }

    public List<UserHomePromoDTO> getPromos() {
        return promos;
    }

    public void setPromos(final List<UserHomePromoDTO> promos) {
        this.promos = promos;
    }
}
