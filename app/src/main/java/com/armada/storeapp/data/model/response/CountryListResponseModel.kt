package com.armada.storeapp.data.model.response

/**
 * Created by User999 on 3/13/2018.
 */
data class CountryListResponseModel(
    val `data`: ArrayList<CountryListDataModel>? = null,
    val message: String? = null,
    val status: Int? = null
)

data class CountryListDataModel(
    val dial_code: String? = null,
    val full_name: String? = null,
    val full_name_english: String? = null,
    val has_state: String? = null,
    val id: String? = null,
    val iso2_code: String? = null,
    val regions: ArrayList<CountryListRegionModel>? = null
)

data class CountryListRegionModel(
    val cities: ArrayList<CountryListCityModel>? = null,
    val code: String? = null,
    val id: String? = null,
    val name: String? = null
)

data class CountryListCityModel(
    val blocks: List<Any>? = null,
    val id: String? = null,
    val name: String? = null
)