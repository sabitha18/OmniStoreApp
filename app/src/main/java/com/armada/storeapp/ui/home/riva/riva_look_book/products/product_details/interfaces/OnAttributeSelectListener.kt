package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.interfaces

import com.armada.storeapp.data.model.response.ProductDetailsAttribute


interface OnAttributeSelectListener {

    fun onAttributeSelected(attribute: ProductDetailsAttribute, position: Int)
}