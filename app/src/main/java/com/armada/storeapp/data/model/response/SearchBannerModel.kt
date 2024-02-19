package com.armada.storeapp.data.model.response

import java.io.Serializable

data class SearchBannerModel(
        var popular_searches : ArrayList<HomeDataModel.PopularSearches>?=null,
        var popular_banners : ArrayList<HomeDataModel.PopularBanner>?=null,
        var popular_top_scroll: ArrayList<HomeDataModel.PopularTopScroll>? = null
):Serializable