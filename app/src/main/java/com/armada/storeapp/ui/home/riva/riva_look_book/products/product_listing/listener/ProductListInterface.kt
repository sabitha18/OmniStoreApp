package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.listener

import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.model.ProductListMDataModel

interface ProductListInterface {
    fun onProductClickEvent(product: ProductListMDataModel)
    fun toggleWishList(
        type: String?,
        wishlistId: String,
        position: Int,
        list: ArrayList<ProductListMDataModel>?
    )
    fun onSeeTheLook(product: ProductListMDataModel)
}