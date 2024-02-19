package com.armada.storeapp.data.model.response

class PriorityListResponse : ArrayList<PriorityListResponse.PriorityListResponseItem>() {
    data class PriorityListResponseItem(
        val NAME: String?,
        val U_PRIORITY: Int?
    )
}