package com.armada.storeapp.data.model.response

import java.io.Serializable

data class HomeDataModel(
    val `data`: Data?,
    val message: String?,
    val status: Int?,
    val success: Boolean?
) {

    data class Data(
        val collectionGroups: ArrayList<CollectionGroupsItemModel?>? = null,
        var popular_searches: ArrayList<PopularSearches>? = null,
        val get_all_category: String,
        val sale: Sale,
        var popular_banners: ArrayList<PopularBanner>? = null,
        var popular_top_scroll: ArrayList<PopularTopScroll>? = null,
        var page_banners: ArrayList<PageBanner>? = null,
        var social_links: SocialLinks? = null,
        var contact_us_details: ContactUs? = null,
        val empty_wishlist: EmptyPages? = null,
        val empty_bag: EmptyPages? = null,
        val empty_product: EmptyPages? = null,
        val empty_order: EmptyPages? = null,
        val empty_promotion: EmptyPages? = null,
        val empty_address: EmptyPages? = null,
        val empty_notification: EmptyPages? = null,
        val empty_store: EmptyPages? = null,
        val notification_sub_text: String? = null
    ) : Serializable

    data class PopularSearches(
        val id: Int,
        val type: String,
        val type_id: Int,
        val name: String,
        val path: String,
        val lvl: String
    ) : Serializable

    data class Sale(
        val sale_title: String,
        val sale_image: String,
        val sale_path: String,
        val sale_category_id: Int,
        val sale_category_level: Int,
        val sale_category_path: String,
        val sale_image_height: Int,
        val sale_start_date: String,
        val sale_end_date: String
    ) : Serializable

    data class PopularTopScroll(
        val id: Int? = 0,
        val type: String? = null,
        val type_id: Int? = 0,
        val name: String? = null,
        val image: String? = null,
        val path: String? = null,
        val lvl: String? = null
    ) : Serializable

    data class PopularBanner(
        val id: Int,
        val type: String,
        val type_id: Int,
        val name: String,
        val image: String,
        val path: String,
        val lvl: String
    ) : Serializable

    data class PageBanner(
        val type: String? = null,
        val banner_image: String? = null,
        val banner_height: Int,
        val is_active: Int
    ) : Serializable


    data class SocialLinks(
        val facebook: String? = null,
        val instagram: String? = null,
        val linkedin: String? = null,
        val twitter: String? = null,
        val whatsapp: String? = null
    ) : Serializable

    data class ContactUs(
        val available_time: String? = null,
        val contact_phone: String? = null,
        val support_email: String? = null,
        val support_phone: String? = null
    ) : Serializable

    data class EmptyPages(
        val title: String? = null,
        val subtitle: String? = null,
        val icon: String? = null
    ) : Serializable
}