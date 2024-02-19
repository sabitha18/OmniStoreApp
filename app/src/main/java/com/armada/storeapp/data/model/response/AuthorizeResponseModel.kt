package com.armada.storeapp.data.model.response

data class AuthorizeResponseModel(
    val access_token: String?,
    val expires_in: Int?,
    val token_type: String?
)