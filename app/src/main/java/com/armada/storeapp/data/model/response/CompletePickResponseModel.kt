package com.armada.storeapp.data.model.response

class CompletePickResponseModel :
    ArrayList<CompletePickResponseModel.CompletePickResponseModelItem>() {
    data class CompletePickResponseModelItem(
        val Success_Message: String?
    )
}