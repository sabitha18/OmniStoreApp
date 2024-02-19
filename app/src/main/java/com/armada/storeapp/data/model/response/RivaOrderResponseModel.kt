package com.armada.storeapp.data.model.response
import java.io.Serializable

/**
 * Created by User999 on 3/9/2018.
 */

data class RivaOrderResponseModel(
    val status: Int? = null,
    val message: String? = null,
    val `data`: Data? = null
) {
    data class Data(
        val payment_url: String? = null,
        val success_url: String? = null,
        val failure_url: String? = null,
        val payment_method: String? = null,
        val shipping_method: String? = "",
        var is_store_pickup: Int? = 0,
        val customer_phone: String? = "",
        val order_id: String? = null,
        var status: String? = null,
        val order_date: String? = null,
        val otp_sent: Boolean? = null,
        val payment_date: String? = null,
        val totals: Totals? = null,
        val billing_address: BillingAddress? = null,
        val shipping_address: ShippingAddress? = null,
        val items: ArrayList<Item?>? = null,
        var expected_delivery: String? = null,
        val hide_from_invoice: Int? = null
    ) : Serializable {
        data class Totals(
            val discount_amount: String? = null,
            val is_coupon_added: Int? = null,
            val coupon_code: String? = null,
            val tax: String? = null,
            val cod_price: String? = null,
            val used_points: Int? = null,
            val reward_discount: Int? = null,
            val gift_credit_used: String? = null,
            val gift_wrap: String? = null,
            val subtotal: String? = null,
            val shipping_amount: String? = null,
            val grand_total: String? = null,
            val store_credit: CreditBalanceModel? = null,
        ) : Serializable  {
            data class CreditBalanceModel(
                val total_balance: String? = null,
                val balance_remaining: String? = null,
                val balance_used: String? = null
            ): Serializable
        }

        data class BillingAddress(
            val address_id: String? = null,
            val address_type: Any? = null,
            val firstname: String? = null,
            val lastname: String? = null,
            val country: String? = null,
            val country_id: String? = null,
            val region_id: String? = null,
            val region: String? = null,
            val city: String? = null,
            val city_id: String? = null,
            val street: String? = null,
            val address_line_1: String? = null,
            val address_line_2: String? = null,
            val phone_code: String? = null,
            val telephone: String? = null,
            val postcode: String? = null,
            val floor_number: String? = null,
            val apartment_number: String? = null,
            val building_number: String? = null,
            val block: String? = null,
            val mobile_number: String? = null,
            val area: String? = null,
            val notes: String? = null,
            val is_default_billing: Int? = null,
            val is_default_shipping: Int? = null
        ) : Serializable

        data class ShippingAddress(
            val address_id: String? = null,
            val address_type: Any? = null,
            val firstname: String? = null,
            val lastname: String? = null,
            val country: String? = null,
            val country_id: String? = null,
            val region_id: String? = null,
            val region: String? = null,
            val city: String? = null,
            val city_id: String? = null,
            val street: String? = null,
            val address_line_1: String? = null,
            val address_line_2: String? = null,
            val phone_code: String? = null,
            val telephone: String? = null,
            val postcode: String? = null,
            val floor_number: String? = null,
            val apartment_number: String? = null,
            val building_number: String? = null,
            val block: String? = null,
            val mobile_number: String? = null,
            val area: String? = null,
            val notes: String? = null,
            val is_default_billing: Int? = null,
            val is_default_shipping: Int? = null
        ) : Serializable

        data class Item(
            val item_id: String? = null,
            val qty: Int? = null,
            val id: String? = null,
            val parent_id: String? = null,
            val sku: String? = null,
            val type: String? = null,
            val name: String? = null,
            val brand: String? = null,
            val price: String? = null,
            var final_price: String? = null,
            val description: String? = null,
            val short_description: String? = null,
            val image: String? = null,
            val is_salable: Boolean? = null,
            val has_options: Int? = null,
            val options: List<Any?>? = null,
            var configurable_option: ArrayList<CheckStockResponseModel.Item.StockConfigurableOption?>? = null,
            val remaining_qty: Int? = null,
            val categories: Array<String>? = null
        ) : Serializable
    }
}
//
//data class OrderResponseModel(
//        val status: Int,
//        val success: String,
//        val message: String,
//        val order_id: String,
//        val order_date: String,
//        var order_status: String,
//        val shipping_method: String,
//        val tracking_name: String,
//        val tracking_number: String,
//        val tracking_url: String,
//        var payment_method: String,
//        val billing_address: BillingAddress,
//        val shipping_address: ShippingAddress,
//        var ordered_items: ArrayList<com.armada.riva.MyCart.StockResponseModel.StockDataModel.Item>,
//        val total: Total,
//        val payment_status: String?,
//        val payment_url: String,
//        val failure: String,
//        val success_url: String,
//        var is_store_pickup: Int,
//        val delivery_time: String,
//        var customer_phone: String,
//        val pickup_address: PickUp_Address, //// order details
//        val pickup_store: PickUp_Address,   //// create order
//        val otp_required: Int?,
//        var result: String?,
//        var payid: String?,
//        var trackid: String?,
//        var ref: String?,
//        var otp: Int?,
//        var expected_delivery: String? = null,
//        var firstname: String? = null,
//        var phone_code: String? = null,
//        var mobile_number: String? = null,
//        var itemsOrdered: OrderedItem? = null
//
//        ) : Serializable {
//    data class ShippingAddress(
//            val customer_address_id: String,
//            val firstname: String,
//            val lastname: String,
//            val company: String,
//            val country_id: String,
//            val country_name: String,
//            val street: String,
//            val mobile_number: String,
//            val telephone: String,
//            val location_type: String,
//            val block: String,
//            val house: String,
//            val region: String,
//            val postcode: String,
//            val fax: String,
//            val city: String,
//            val notes: String,
//            val address_line: String,
//            var phone_code: String
//    ) : Serializable
//
//    data class Total(
//            val subtotal: String,
//            val shipping: String,
//            val codfees: Double,
//            val tax: String,
//            var discount: String,
//            val grandtotal: String,
//            val store_credit: String,
//            var is_coupon_applied: String,
//            var coupon_code: String
//    ) : Serializable
//
//
//    data class BillingAddress(
//            val customer_address_id: String,
//            val firstname: String,
//            val lastname: String,
//            val company: String,
//            val country_id: String,
//            val country_name: String,
//            val street: String,
//            val mobile_number: String,
//            val telephone: String,
//            val location_type: String,
//            val block: String,
//            val house: Any,
//            val region: String,
//            val postcode: String,
//            val fax: String,
//            val city: String,
//            val notes: Any,
//            val address_line: Any,
//            val phone_code: String
//    ) : Serializable
//
//    data class OrderedItem(
//            val item_id: String,
//            val entity_id: String,
//            val name: String,
//            val quantity: String,
//            val price: String,
//            val subtotal: String,
//            val image_url: String,
//            var configurable_option: ArrayList<com.armada.riva.ProductDetail.ProductDetailData.ConfigurableOption>
//    ) : Serializable
//
//    data class PickUp_Address(
//            val store_id: String,
//            val store_code: String,
//            val store_name: String,
//            val store_manager: String,
//            val store_email: String,
//            val store_phone: String,
//            val description: String,
//            val status: String,
//            val address: String,
//            val address_2: String,
//            val state: String,
//            val city: String,
//            val region_id: String,
//            val city_id: String,
//            val zipcode: String,
//            val state_id: String,
//            val country: String,
//            val status_order: Any
//    ) : Serializable
//}