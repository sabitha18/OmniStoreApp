package com.armada.storeapp.ui.home.riva.riva_look_book.categories.model

import com.armada.storeapp.data.model.response.CollectionListItemModel
import com.armada.storeapp.data.model.response.SubCategoryResponse
import com.armada.storeapp.ui.utils.Constants


data class ParentData(
    val parentCategory: CollectionListItemModel? = null,
    var subList: ArrayList<ChildData>? = null,
    var type: Int = Constants.PARENT,
    var isExpanded: Boolean = false
)