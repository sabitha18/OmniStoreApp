package com.armada.storeapp.data.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WarehouseLoginResponse(
    @SerializedName("MESSAGE") val message: String,
    @SerializedName("USER_LOCATION") val userLocation: String,
    @SerializedName("USER_ID") val userId: String,
    @SerializedName("USERNAME") val userName: String,
    @SerializedName("DEFFROMLOC") val fromLocation: String
) : Serializable
