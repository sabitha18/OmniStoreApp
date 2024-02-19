package com.armada.storeapp.data.remote

import com.armada.storeapp.data.ApiService
import com.armada.storeapp.data.PaymentGatewayApiService
import com.armada.storeapp.data.model.response.AuthorizeResponseModel
import com.armada.storeapp.data.model.response.payment_gatway_response.GetInvoiceDetailsResponse
import com.armada.storeapp.data.model.response.payment_gatway_response.InvoiceRequestModel
import com.armada.storeapp.data.model.response.payment_gatway_response.InvoiceResponseModel
import com.armada.storeapp.ui.utils.Constants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import javax.inject.Inject

class PaymentGatewayDataSource @Inject constructor(
    private val apiService: PaymentGatewayApiService
) {
    suspend fun sendPaymentLinkToCustomer(
        invoiceRequestModel: InvoiceRequestModel
    ) = apiService.sendPaymentLinkToCustomer(Constants.PAYMENT_GATEWAY_TEST_TOKEN, invoiceRequestModel)


    suspend fun getPaymentStatus(
        invoiceId: String,
    ) = apiService.getPaymentStatus(Constants.PAYMENT_GATEWAY_TEST_TOKEN, invoiceId)
}