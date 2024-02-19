package com.armada.storeapp.data.model.response

class ToLocationListResponse : ArrayList<ToLocationListResponse.ToLocationListResponseItem>() {
    data class ToLocationListResponseItem(
        val LOCCODE: String?,
        val LOCNAME: String?
    )
}