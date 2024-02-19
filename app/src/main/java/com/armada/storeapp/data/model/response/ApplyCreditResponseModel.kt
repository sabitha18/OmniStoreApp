package com.armada.storeapp.data.model.response

data class ApplyCreditResponseModel(
    val status: Int? = null,
    val message: String? = null,
    val totals: ApplyCreditTotalsModel? = null
)

data class ApplyCreditTotalsModel(
    val discount_amount: String? = null,
    val is_coupon_added: Int? = null,
    val coupon_code: String? = null,
    val shipping_amount: String? = null,
    val tax_original: String? = null,
    val tax: String? = null,
    val cod_fees: String? = null,
    val grand_total: Double? = null,
    val subtotal: String? = null,
    val store_credit: ApplyCreditBalanceModel? = null
)

data class ApplyCreditBalanceModel(
    val total_balance: String? = null,
    val balance_remaining: String? = null,
    val balance_used: String? = null
)
