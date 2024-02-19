package com.armada.storeapp.data.model.response

import com.google.gson.annotations.SerializedName

data class GenerateTokenResponse(
    @SerializedName(".expires") val expires: String?,
    @SerializedName(".issued") val issueed: String?,
    @SerializedName("access_token") val access_token: String?,
    @SerializedName("expires_in") val expires_in: Int?,
    @SerializedName("token_type") val token_type: String?,
    @SerializedName("userName") val userName: String?
)