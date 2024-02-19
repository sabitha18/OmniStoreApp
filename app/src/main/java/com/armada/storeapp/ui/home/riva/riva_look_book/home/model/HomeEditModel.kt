package com.armada.storeapp.ui.home.riva.riva_look_book.home.model

import com.armada.storeapp.data.model.response.CollectionListItemModel
import com.armada.storeapp.ui.home.riva.riva_look_book.home.Algolia.ExpandableRecyler.Model.Parent

class HomeEditModel(val image: String, val edits: ArrayList<CollectionListItemModel>) :
    Parent<CollectionListItemModel> {


    override fun isInitiallyExpanded(): Boolean {
        return false
    }

    override fun getChildList(): MutableList<CollectionListItemModel> {
        return edits
    }


}