package com.armada.riva.Category.Algolia.ExpandableRecyler.Model

import com.armada.storeapp.data.model.response.CollectionListItemModel
import java.io.Serializable

/**
 * Created by User999 on 7/14/2018.
 */
data class SubCatModel(
    val category_id: String,
    val name: String,
    val path: String,
    val level: Int,
    val thumbnail: String,
    var children: ArrayList<SubCatModel>,
    var model: CollectionListItemModel
) : Serializable

