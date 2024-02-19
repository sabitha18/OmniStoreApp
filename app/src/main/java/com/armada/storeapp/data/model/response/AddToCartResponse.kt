package com.armada.storeapp.data.model.response

import java.io.Serializable

data class AddToCartResponse(
    val status: String,
    val success: String,
    val message: String,
    val items: ArrayList<Item>
):Serializable{
    data class Item(
        val item_id: String,
        val entity_id: String,
        val short_description: String,
        val name: String,
        val image_url: String,
        var regular_price: String,
        val final_price: String,
        val quantity: Int,
        val qty_available:Int,
        val subtotal: String,
        val is_saleable: Boolean,
        var cnfgDBOPtion: String,
        val configurable_option: ArrayList<AddToCartConfigurableOption>,
        val parent_id: String

    ): Serializable{
        data class AddToCartConfigurableOption(
            val type: String,
            val attribute_id: String,
            val attribute_code: String,
            val attributes: AddToCartAttributes
        ) {
            data class AddToCartAttributes(
                val value: String,
                val option_id: String,
                val image_url: String
            )
        }
    }
}