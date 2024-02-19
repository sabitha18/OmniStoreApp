package com.armada.storeapp.data.model.response

data class Attributes(
    val attribute_image_url: String,
    val images: List<Any>,
    val option_id: String,
    val price: String,
    val value: String
)