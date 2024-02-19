package com.armada.storeapp.data.model.response

data class InvoiceDetail(
    val active: Boolean,
    val appVersion: Any,
    val appliedCustomerSpecialPricesID: Any,
    val appliedPriceListID: Int,
    val appliedPromotionID: String,
    val arabicDetails: Any,
    val barcode: Any,
    val billNo: Any,
    val brandID: Int,
    val category: Int,
    val comboGroupID: Int,
    val countryCode: String,
    val countryID: Int,
    val countryServerSyncTime: String,
    val createBy: Any,
    val createOn: Any,
    val createdByUserName: Any,
    val customerCode: Any,
    val customerName: Any,
    val discountAmount: Double,
    val discountRemarks: String,
    val discountType: String,
    val dummyPrice: Double,
    val dummyQty: Int,
    val employeeDiscountAmount: Double,
    val employeeDiscountID: Int,
    val exchangeQty: Int,
    val exchangeRefID: Int,
    val exchangeRemarks: Any,
    val exchangedSKU: Any,
    val familyDiscountAmount: Double,
    val fromCentralUnit: Boolean,
    val id: Int,
    val invoiceDate: String,
    val invoiceDetailID: Int,
    val invoiceHeaderID: Int,
    val invoiceNo: Any,
    val invoiceType: String,
    val isCombo: Boolean,
    val isCountrySync: Boolean,
    val isDataSyncToCountryServer: Boolean,
    val isDataSyncToMainServer: Boolean,
    val isExchanged: Boolean,
    val isFreeItem: Boolean,
    val isGift: Boolean,
    val isHeader: Boolean,
    val isPromoExcludeItem: Boolean,
    val isRecordVisible: Boolean,
    val isReturned: Boolean,
    val isServerSync: Boolean,
    val isStoreSync: Boolean,
    val lineTotal: Double,
    val linkedSrlNo: Int,
    val mainServerSyncTime: String,
    val modifiedSalesEmployee: Any,
    val modifiedSalesManager: Any,
    val netAmount: Double,
    val oldExchangeQty: Int,
    val oldReturnQty: Int,
    val paymentList: Any,
    val posCode: String,
    val price: Double,
    val productGroupID: Int,
    val productGroupName: String,
    val productSubGroupID: Int,
    val promoGroupID: Int,
    val promotionAmount: Double,
    val promotionName: String,
    val promtionApplied: Boolean,
    val qty: Int,
    val returnAmount: Double,
    val returnQty: Int,
    val returnRefID: Int,
    val returnRemarks: Any,
    val returnedSKU: Any,
    val salesReturnID: Int,
    val salesStatus: Boolean,
    val scn: Any,
    val seasonID: Int,
    val segamentationID: Int,
    val sellingLineTotal: Double,
    val sellingPrice: Double,
    val serialNo: Int,
    val singleDiscountAmount: Double,
    val skuCode: String,
    val skuImage: Any,
    val skuImageUrl: Any,
    val skuid: Int,
    val specialDiscountType: Any,
    val specialPromoDiscount: Double,
    val specialPromoDiscountPercentage: Int,
    val specialPromoDiscountType: String,
    val stockQty: Int,
    val storeCode: String,
    val storeID: Int,
    val styleCode: String,
    val styleID: Int,
    val subBrandID: Int,
    val subBrandName: String,
    val syncFailedReason: String,
    val tag_Id: Any,
    val taxAmount: Double,
    val taxID: Int,
    val updateBy: Any,
    val updateOn: Any,
    val updatedByUserName: Any,
    val yearID: Int
)