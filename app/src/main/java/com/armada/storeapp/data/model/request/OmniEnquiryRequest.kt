package com.armada.storeapp.data.model.request

data class OmniEnquiryRequest(
    val active: Boolean,
    val oMNIEnquiryDetail: List<OMNIEnquiryDetail>,
    val storeCode: String,
    val storeid: Int
)