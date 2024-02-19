package com.armada.storeapp.ui.home.riva.riva_look_book.home.Algolia.ExpandableRecyler.Model


/**
 * Created by User999 on 7/14/2018.
 */
class MainCatModel(
    val id: String,
    val name: String,
    val path: String,
    var level: String,
    val image: String,
    val has_subcat: Boolean,
    val childList: ArrayList<CategoriesListMDataModel?>?
) :
    Parent<CategoriesListMDataModel?> {


    override fun isInitiallyExpanded(): Boolean {
        return false
    }

    override fun getChildList(): MutableList<CategoriesListMDataModel?> {
        return childList!!
    }
}
