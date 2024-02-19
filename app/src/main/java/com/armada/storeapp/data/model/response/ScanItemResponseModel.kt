package com.armada.storeapp.data.model.response

class ScanItemResponseModel : ArrayList<ScanItemResponseModel.ScanItemResponseModelItem>() {
    data class ScanItemResponseModelItem(
        val ITEMCODE: String?
    )
}