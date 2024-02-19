package com.armada.storeapp.data

import com.armada.storeapp.data.model.response.payment_gatway_response.InvoiceRequestModel
import com.armada.storeapp.data.model.request.RivaRegisterUserRequest
import com.armada.storeapp.data.model.response.RivaRegisterUserResponse
import com.armada.storeapp.data.model.response.payment_gatway_response.GetInvoiceDetailsResponse
import com.armada.storeapp.data.model.response.payment_gatway_response.InvoiceResponseModel
import com.armada.storeapp.data.model.response.payment_gatway_response.invoice_details.InvoiceDetailsResponse
import com.armada.storeapp.data.model.response.payment_gatway_response.invoice_response.InvoiceResponse
import retrofit2.Response
import retrofit2.http.*

interface PaymentGatewayApiService {

    @POST("invoices")
    suspend fun sendPaymentLinkToCustomer(
        @Header("Authorization") sessionToken: String,
        @Body invoiceRequestModel: InvoiceRequestModel
    ): Response<InvoiceResponse>


    @GET("invoices/{invoiceId}")
    suspend fun getPaymentStatus(
        @Header("Authorization") sessionToken: String,
        @Path("invoiceId") invoiceId: String,
    ): Response<InvoiceDetailsResponse>
}