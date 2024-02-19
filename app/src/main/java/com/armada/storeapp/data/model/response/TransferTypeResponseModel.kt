package com.armada.storeapp.data.model.response

class TransferTypeResponseModel :
    ArrayList<TransferTypeResponseModel.TransferTypeResponseModelItem>() {
    data class TransferTypeResponseModelItem(
        val CODE: String?,
        val NAME: String?
    )
}