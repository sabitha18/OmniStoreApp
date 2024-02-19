package com.armada.storeapp.data.model.request

data class StockAdjustmentAddRequest(
    val Adjlst: ArrayList<adjust>?
) {
    data class adjust(
        val CountQty: String?,
        val DiffQty: String?,
        val Quantity: String?,
        val itemcode: String?
    )
}