package com.armada.storeapp.data.model.response

import java.io.Serializable

data class RecentlyViewModel(
    val entity_id: String,
    val item_id: String,
    val name: String,
    val image_url: String,
    var regular_price: String,
    val final_price: String,
    val is_saleable: String,
    var is_wishlist: Boolean
) : Serializable