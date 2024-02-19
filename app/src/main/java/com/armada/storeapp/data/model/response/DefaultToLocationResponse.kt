package com.armada.storeapp.data.model.response

class DefaultToLocationResponse :
    ArrayList<DefaultToLocationResponse.DefaultToLocationResponseItem>() {
    data class DefaultToLocationResponseItem(
        val DefLoc: String?
    )
}