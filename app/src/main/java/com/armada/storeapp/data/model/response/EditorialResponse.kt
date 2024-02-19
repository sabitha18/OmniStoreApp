package com.armada.storeapp.data.model.response

data class EditorialResponse(
    val `data`: ArrayList<Data>?,
    val message: String?,
    val status: Int?,
    val success: Boolean?
) {
    data class Data(
        val category_id: String?,
        val category_level: String?,
        val category_path: String?,
        val id: Int?,
        val image: String?,
        val image_height: Int?,
        val media_file: String?,
        val media_thumbnail: String?,
        val media_type: String?,
        val name: String?,
        val product: Product?,
        val products: ArrayList<Product>?,
        val type: String?
    ) {
        data class Product(
            val final_price: String?,
            val image: String?,
            val name: String?,
            val product_id: Int?,
            val regular_price: String?,
            var products: ArrayList<Product>
        )
    }
}