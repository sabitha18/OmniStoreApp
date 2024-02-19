package com.armada.storeapp.data.model

import com.armada.storeapp.ui.home.riva.riva_look_book.search.Stores.StoreData

/**
 * Created by PC1 on 05-03-2018.
 */

data class SelectStoreData(
    val status: Int,
    val message: String?,
    val `data`: ArrayList<StoreData>?)

data class StoreData(
        val website_id: String,
        val code: String,
        val name: String,
        val flag: String,
        val name_ar: String,
        var is_default: String,
        val store_code_en: String,
        val currency_code_en: String,
        val store_code_ar: String,
        val currency_code_ar: String,
		val currency_iso3:String,
        val currency_code: String? = null,
        val currency_symbol_ar: String? = null,
        val currency_symbol_en: String? = null
)
