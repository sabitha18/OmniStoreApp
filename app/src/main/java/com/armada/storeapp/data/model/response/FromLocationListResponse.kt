package com.armada.storeapp.data.model.response

class FromLocationListResponse :
    ArrayList<FromLocationListResponse.FromLocationListResponseItem>() {
    data class FromLocationListResponseItem(
        val LOCCODE: String?,
        val LOCNAME: String?
    )
}