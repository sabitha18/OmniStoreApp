package com.armada.storeapp.data.model.response

class ShopPickOrdersResponseModel :
    ArrayList<ShopPickOrdersResponseModel.ShopPickOrdersResponseModelItem>() {
    data class ShopPickOrdersResponseModelItem(
        val ALLOCATEDBOX: String?,
        val DRIVER_NAME: Any?,
        val FROM_LOCATION: String?,
        val INTIME: String?,
        val ITEM_CODE: String?,
        val LOCATION_CODE: String?,
        val ORDER_QTY: Int?,
        val ORDER_REFNO: String?,
        val ORDER_STAT: String?,
        val POS_REF_NO: Any?,
        val SHORT_NAME: String?,
        val WH_CODE: String?
    )
}