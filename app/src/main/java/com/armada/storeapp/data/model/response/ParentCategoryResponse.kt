package com.armada.storeapp.data.model.response

data class ParentCategoryResponse(
    val `data`: ArrayList<Data>?,
    val domain: String?,
    val message: String?,
    val status: Int?,
    val success: Boolean?
) {
    data class Data(
        val icon: String?,
        val name_en: String?,
        val parent_category_id: Int?
    )
}