package com.armada.storeapp.data.model.response.payment_gatway_response

import java.io.Serializable

data class Transaction(
    val amount: Double,
    val currency: String,
    val id: String,
    val `object`: String,
    val receipt: Receipt,
    val reference: ReferenceXX,
    val response: Response,
    val source: Source,
    val status: String,
    val transaction: InvoiceTransaction
):Serializable{
    data class InvoiceTransaction(val timezone:String,val created:String,val authorization_id:String)
}