package com.armada.storeapp.ui.home.riva.riva_look_book.home.model

import com.armada.storeapp.data.model.response.EditorialResponse
import com.armada.storeapp.ui.home.riva.riva_look_book.home.Algolia.ExpandableRecyler.Model.Parent

/**
 * Created by User999 on 7/16/2018.
 */
class EditorialModel(
    val id: Int,
    val image_height: Int,
    val image: String,
    val media_file: String,
    var media_type: String,
    var media_thumbnail: String,
    var type: String,
    var category_id: String,
    var name: String,
    var category_path: String,
    var category_level: String,
    var product: EditorialResponse.Data.Product,
    val products: ArrayList<EditorialResponse.Data.Product>
) :
    Parent<EditorialResponse.Data.Product> {


    override fun isInitiallyExpanded(): Boolean {
        return false
    }

    override fun getChildList(): MutableList<EditorialResponse.Data.Product> {
        return products
    }


}