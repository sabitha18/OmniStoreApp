package com.armada.storeapp.data.model.response

data class GetTimeSlotResponse(
    val businessDate: String,
    val displayMessage: Any,
    val documentNo: Any,
    val exceptionMessage: Any,
    val grandToatl: Double,
    val iDs: Any,
    val orderID: Any,
    val orderNo: Any,
    val recordCount: Int,
    val responseDynamicData: List<ResponseDynamicDataX>,
    val stackTrace: Any,
    val statusCode: Int,
    val timeSlotList: ArrayList<TimeSlot>,
    val wmsiDs: Any
)