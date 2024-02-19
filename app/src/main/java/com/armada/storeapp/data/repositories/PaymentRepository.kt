package com.armada.storeapp.data.repositories

import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.AddAddressRequestModel
import com.armada.storeapp.data.model.request.RivaLoginRequest
import com.armada.storeapp.data.model.request.RivaRegisterUserRequest
import com.armada.storeapp.data.model.response.AddAddressResponseModel
import com.armada.storeapp.data.model.response.BaseApiResponse
import com.armada.storeapp.data.model.response.RivaRegisterUserResponse
import com.armada.storeapp.data.model.response.payment_gatway_response.GetInvoiceDetailsResponse
import com.armada.storeapp.data.model.response.payment_gatway_response.InvoiceRequestModel
import com.armada.storeapp.data.model.response.payment_gatway_response.InvoiceResponseModel
import com.armada.storeapp.data.model.response.payment_gatway_response.invoice_details.InvoiceDetailsResponse
import com.armada.storeapp.data.model.response.payment_gatway_response.invoice_response.InvoiceResponse
import com.armada.storeapp.data.remote.PaymentGatewayDataSource
import com.armada.storeapp.data.remote.RivaDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class PaymentRepository @Inject constructor(
    private val paymentGatewayDataSource: PaymentGatewayDataSource
) : BaseApiResponse() {

    suspend fun sendPaymentLinkToCustomer(
        invoiceRequestModel: InvoiceRequestModel
    ): Flow<Resource<InvoiceResponse>> {
        return flow<Resource<InvoiceResponse>> {
            emit(safeApiCall {
                paymentGatewayDataSource.sendPaymentLinkToCustomer(invoiceRequestModel)
            })
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getPaymentStatus(
        invoiceId: String,
    ) : Flow<Resource<InvoiceDetailsResponse>> {
        return flow<Resource<InvoiceDetailsResponse>> {
            emit(safeApiCall {
                paymentGatewayDataSource.getPaymentStatus(invoiceId)
            })
        }.flowOn(Dispatchers.IO)
    }

}