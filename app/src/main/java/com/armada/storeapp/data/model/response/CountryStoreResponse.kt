package com.armada.storeapp.data.model.response

import java.io.Serializable

data class CountryStoreResponse(
    val `data`: ArrayList<StoreDetail>,
    val message: String,
    val status: Int
):Serializable{
    data class StoreDetail(
        val code: String,
        val currency_code: String,
        val currency_symbol_ar: String,
        val currency_symbol_en: String,
        val flag: String,
        val is_default: String,
        val name: String,
        val name_ar: String,
        val store_code_ar: String,
        val store_code_en: String,
        val website_id: String
    ):Serializable
}