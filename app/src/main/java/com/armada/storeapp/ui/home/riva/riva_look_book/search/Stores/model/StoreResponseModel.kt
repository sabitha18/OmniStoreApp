package com.armada.storeapp.ui.home.riva.riva_look_book.search.Stores.model

data class StoreResponseModel(
    val status: Int? = null,
    val message: String? = null,
    val `data`: ArrayList<Data>? = null
)

data class Data(
    val website_id: String? = null,
    val name: String? = null,
    val code: String? = null,
    val flag: String? = null,
    val is_default: String? = null,
    val name_ar: String? = null,
    val store_code_en: String? = null,
    val store_code_ar: String? = null,
    val currency_code: String? = null,
    val currency_symbol_en: String? = null,
    val currency_symbol_ar: String? = null
)