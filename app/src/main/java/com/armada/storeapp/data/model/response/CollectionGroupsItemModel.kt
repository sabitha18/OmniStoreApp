package com.armada.storeapp.data.model.response

import java.io.Serializable

data class CollectionGroupsItemModel(
    val id: Int? = null,
    val title: String? = null,
    val image_width: Int? = null,
    val image_height: Int? = null,
    val image_margin: Int? = null,
    val margin_top: Int? = null,
    val margin_bottom: Int? = null,
    val hide_title: Int? = null,
    val hide_collection_title: Int? = null,
    val hide_collection_sub_title: Int? = null,
    val hide_underline: Int? = null,
    val is_timeline: String?,
    val collection_list: ArrayList<CollectionListItemModel>? = null,
    val only_editorial: String?,
    val image: String,
    var should_reverse: Int? = null,
    var saleModel: HomeDataModel.Sale? = null,
) : Serializable


//val sort_order: Int? = null,

