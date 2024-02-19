package com.armada.storeapp.data.model.request

class WishlistRequest {
    data class AddToWishList(
        val customer_id: String,
        val product_id: String
    )

    data class RemoveFromWishList(
        val customer_id: String,
        val wishlist_item_id: String,
    )


    data class MoveToWishlist(
        val customer_id: String,
        val cart_item_id: String,
        val product_id: String
    )
}