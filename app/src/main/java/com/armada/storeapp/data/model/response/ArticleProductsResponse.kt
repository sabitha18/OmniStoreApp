package com.armada.storeapp.data.model.response

data class ArticleProductsResponse(
    val businessDate: String,
    val colorWiseStockList: Any,
    val displayMessage: Any,
    val documentNo: Any,
    val exceptionMessage: Any,
    val grandToatl: Double,
    val iDs: Any,
    val orderID: Any,
    val orderNo: Any,
    val recordCount: Int,
    val responseDynamicData: Any,
    val scaleWiseStockList: Any,
    val stackTrace: Any,
    val statusCode: Int,
    val stockDataSet: Any,
    val stockList: ArrayList<Stock>,
    val wmsiDs: Any
)