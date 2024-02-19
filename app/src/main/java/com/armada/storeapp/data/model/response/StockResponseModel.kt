package com.armada.storeapp.data.model.response

import java.io.Serializable

/**
 * Created by User999 on 3/9/2018.
 */

data class StockResponseModel(
    val `data`: StockDataModel? = null,
    val message: String? = null,
    val status: Int? = null
) : Serializable {


    data class StockDataModel(
        val billing_address: List<Any>? = null,
        val gift_message: List<Any>? = null,
        val gift_option: String? = null,
        val gift_credit: ArrayList<Int>,
        val items: ArrayList<Item>? = null,
        var payment_methods: ArrayList<PaymentMethod>? = null,
        val quote_id: String? = null,
        val reward_points: Any? = null,
        var shipping_address: ArrayList<AddAddressDataModel>? = null,
        val shipping_carrier_code: String? = null,
        val shipping_method_code: String? = null,
        val shipping_methods: List<Any>,
        val checkout_message: String? = null,
        val totals: OrderTotal? = null,
        var userSelectedAddress: AddAddressDataModel? = null,
        var checkoutcom_credentials: CheckoutComCredentialsModel? = null
    ) : Serializable {
        data class PaymentMethod(
            val code: String? = null,
            val title: String? = null,
            val cod_price: Double? = null,
            val gateways: String? = null,
            val icon: String? = null,
            val is_selected: Int? = null,
            var isCheckoutSelected: Boolean? = false
        ) : Serializable

        data class ShippingMethod(
            val code: String? = null,
            val title: String? = null,
            val shipping_price: String? = null
        ) : Serializable

        data class CheckoutComCredentialsModel(
            val public_key: String? = null,
            val secret_key: String? = null,
            val success_url: String? = null,
            val fail_url: String? = null,
            val checkout_host: String? = null
        ) : Serializable

//        internal data class ConfigurableOption(
//            val attribute_id: Int? = null,
//            val type: String? = null,
//            val attribute_code: String? = null,
//            val attributes: Attributes? = null
//        )

//        internal data class Attributes(
//            val value: String? = null,
//            val option_id: String? = null,
//            val attribute_image_url: String? = null,
//            val price: String? = null,
//            val images: List<Any>? = null
//        )

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
            val options: List<Any>? = null,
            val savings: Int,
            var configurable_option: ArrayList<CheckStockResponseModel.Item.StockConfigurableOption>? = null,
            val remaining_qty: Int? = null,
            val categories: Array<String>? = null
        ) : Serializable
//        var configurable_option: ArrayList<ConfigurableOption?>? = null,
//        data class Stores(
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
//            val zipcode: Any,
//            val state_id: String,
//            val country: String,
//            val status_order: String,
//            val store_name_ar: String,
//            val address_ar: String,
//            val state_ar: String,
//            val city_ar: String
//        ) : Serializable


        data class OrderTotal(
            val cod_fees: String? = null,
            var coupon_code: String? = null,
            var discount_amount: String? = null,
            val gift_credit_used: String? = null,
            val gift_wrap: String? = null,
            val grand_total: String? = null,
            var is_coupon_added: String? = null,
            val order_value: String,
            val reward_discount: Int? = null,
            val shipping_amount: String? = null,
            var subtotal: String? = null,
            val tax: String? = null,
            val tax_original: String,
            val used_points: String? = null,
            var special_savings: String? = null,
            var store_credit: StoreCredit? = null,
        ) : Serializable {

        }
/*	data class ShippingAddress(
			val customer_address_id: String,
			val firstname: String,
			val lastname: String,
			val company: String,
			val mobile_number: String,
			val telephone: String,
			val location_type: String,
			val block: String,
			val house: String,
			val region: String,
			val address_line: String,
			val street: String,
			val postcode: String,
			val fax: String,
			val notes: String,
			val is_default_shipping: String,
			val city: String,
			val country_id: String,
			val country_name: String
	):Serializable*/
    }
}