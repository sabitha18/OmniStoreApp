package com.armada.storeapp.data.model.response

import java.io.Serializable

data class StockResponseModelNoAddress(
        val quote_id: String,
        val status: String,
        val success: String,
        val message: String,
        val items: ArrayList<StockResponseModel.StockDataModel.Item>,
        val outofstock_items: List<Any>,
        val zeroprice_items: List<Any>,
        var shipping_address: ArrayList<ShippingAddres>,
       // val shipping_method: ArrayList<com.armada.riva.MyCart.StockResponseModel.ShippingMethod>,
        val payment_methods: List<StockResponseModel.StockDataModel.PaymentMethod>,
        var configurable_option: ArrayList<ConfigurableOption>?,
        var total: StockResponseModel.StockDataModel.OrderTotal,
        var free_shipping_above:Double,
        var checkout_message:String,
        var storepickup_enabled:Boolean,
        //var store_list:ArrayList<StockResponseModel.Stores>,
        var delivery_time_slots:ArrayList<String>,
        var isCivilIdRequired: Boolean,
        var civilid: String
) : Serializable
{

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