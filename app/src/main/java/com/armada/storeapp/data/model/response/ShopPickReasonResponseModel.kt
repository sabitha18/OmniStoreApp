package com.armada.storeapp.data.model.response

class ShopPickReasonResponseModel :
    ArrayList<ShopPickReasonResponseModel.ShopPickReasonResponseModelItem>() {
    data class ShopPickReasonResponseModelItem(
        val REASON: String?,
        val REASON_CODE: Int?
    )
}