package com.armada.storeapp.data.model.response

import java.io.Serializable

data class CheckStockResponseModel(
    val `data`: Data,
    val message: String,
    val status: Int
):Serializable{
    data class Data(
        val billing_address: List<Any>,
        val checkout_message: String,
        val checkoutcom_credentials: CheckoutcomCredentials,
        val gift_credit: Int,
        val gift_message: List<Any>,
        val items: ArrayList<Item>,
        val payment_methods: ArrayList<PaymentMethod>,
        val quote_id: String,
        val reward_points: List<Any>,
        val shipping_address: ArrayList<AddAddressDataModel>,
        val shipping_carrier_code: String,
        val shipping_method_code: String,
        val shipping_methods: List<Any>,
        val totals: Totals,
        var userSelectedAddress: AddAddressDataModel? = null,
    ):Serializable{
        data class Totals(
            val cod_fees: String,
            var coupon_code: String,
            var discount_amount: String,
            val gift_credit_used: String,
            val gift_wrap: String,
            val grand_total: String,
            var is_coupon_added: Int,
            val order_value: String,
            val reward_discount: Int,
            val shipping_amount: String,
            val special_savings: String,
            var store_credit: StoreCredit,
            var subtotal: String,
            val tax: String,
            val tax_original: String,
            val used_points: String
        ):Serializable
    }

    data class Item(
        val brand: String,
        val configurable_option: ArrayList<StockConfigurableOption?>,
        val description: String,
        val final_price: String,
        val has_options: Int,
        val id: String,
        val image: String,
        val is_salable: Boolean,
        val item_id: String,
        val name: String,
        val options: List<Any>,
        val parent_id: String,
        val price: String,
        val qty: Int,
        val remaining_qty: Int,
        val savings: Double,
        val short_description: String,
        val sku: String,
        val type: String
    ):Serializable{
        data class StockConfigurableOption(
            val attribute_code: String,
            val attribute_id: Int,
            val attribute: Attribute,
            val type: String
        ) : Serializable
    }
}