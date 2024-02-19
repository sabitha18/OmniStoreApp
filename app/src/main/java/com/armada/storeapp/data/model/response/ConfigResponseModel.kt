package com.armada.storeapp.data.model.response

data class ConfigResponseModel(
    val `data`: List<ConfigResponseData>? = null,
    val message: String? = null,
    val status: String? = null,
    val success: String? = null
)
    data class ConfigResponseData(
        val attributes: ArrayList<ConfigResponseAttribute>? = null,
        val entity_id: String? = null,
        val quantity: Int? = null,
        val sku: String? = null,
        val type: String? = null,
        val attribute_code: String? = null,
        val regular_price: String? = null,
        val final_price: String? = null,
        val attribute_id: String? = null,
        val image:ArrayList<String>? = null
    )
        data class ConfigResponseAttribute(
            val option_id: String? = null,
            val value: String? = null,
            val images: ArrayList<String>? = null,
            val qty: String? = null
        )

