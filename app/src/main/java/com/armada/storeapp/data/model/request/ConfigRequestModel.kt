package com.armada.storeapp.data.model.request

/**
 * Created by User999 on 3/13/2018.
 */
data class ConfigRequestModel(
        var parent_product_id: String,
        var attribute_id :String,
        var option_id : String,
        var lang : String
)