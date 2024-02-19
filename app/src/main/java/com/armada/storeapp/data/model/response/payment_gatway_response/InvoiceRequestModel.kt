package com.armada.storeapp.data.model.response.payment_gatway_response

import java.io.Serializable

data class InvoiceRequestModel(
    val charge: Charge,
    val currencies: ArrayList<String>,
    val customer: Customer,
    val description: String,
    val draft: Boolean,
    val due: Long,
    val expiry: Long,
    val metadata: Metadata,
    val mode: String,
    val note: String,
    val notifications: Notifications,
    val order: ItemOrder,
    val payment_methods: ArrayList<String>,
    val post: Post,
    val redirect: Redirect,
    val reference: Reference
) : Serializable {
    data class ItemOrder(
        val amount: Double,
        val currency: String,
        val items: ArrayList<OrderItem>
    ) : Serializable {
        data class OrderItem(
            val amount: Double,
            val currency: String,
            val description: String,
            val discount: Discount,
            val image: String,
            val name: String,
            val quantity: Int

        )
    }
}