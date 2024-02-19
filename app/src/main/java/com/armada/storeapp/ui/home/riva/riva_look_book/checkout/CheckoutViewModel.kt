package com.armada.storeapp.ui.home.riva.riva_look_book.checkout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.*
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.data.model.response.payment_gatway_response.GetInvoiceDetailsResponse
import com.armada.storeapp.data.model.response.payment_gatway_response.InvoiceRequestModel
import com.armada.storeapp.data.model.response.payment_gatway_response.InvoiceResponseModel
import com.armada.storeapp.data.model.response.payment_gatway_response.invoice_details.InvoiceDetailsResponse
import com.armada.storeapp.data.model.response.payment_gatway_response.invoice_response.InvoiceResponse
import com.armada.storeapp.data.repositories.PaymentRepository
import com.armada.storeapp.data.repositories.RivaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor
    (
    private val rivaRepository: RivaRepository,
    private val paymentRepository: PaymentRepository,
    application: Application
) : AndroidViewModel(application) {

    private val addAddressResponse: MutableLiveData<Resource<AddAddressResponseModel>> =
        MutableLiveData()
    val responseAddAddress: LiveData<Resource<AddAddressResponseModel>> =
        addAddressResponse

    private val editAddressResponse: MutableLiveData<Resource<AddAddressResponseModel>> =
        MutableLiveData()
    val responseEditAddress: LiveData<Resource<AddAddressResponseModel>> =
        addAddressResponse

    private val deleteAddressResponse: MutableLiveData<Resource<DeleteAddressResponseModel>> =
        MutableLiveData()
    val responseDeleteAddress: LiveData<Resource<DeleteAddressResponseModel>> =
        deleteAddressResponse

    private val storeCreditResponse: MutableLiveData<Resource<ApplyCreditResponseModel>> =
        MutableLiveData()
    val responseStoreCredit: LiveData<Resource<ApplyCreditResponseModel>> =
        storeCreditResponse

    private val rivaCheckoutResponse: MutableLiveData<Resource<RivaOrderResponseModel>> =
        MutableLiveData()
    val responseRivaCheckout: LiveData<Resource<RivaOrderResponseModel>> =
        rivaCheckoutResponse

    private val checkItemStockNoAddressResponse: MutableLiveData<Resource<StockResponseModelNoAddress>> =
        MutableLiveData()
    val responseCheckItemStockNoAddress: LiveData<Resource<StockResponseModelNoAddress>> =
        checkItemStockNoAddressResponse

    private val checkItemStockResponse: MutableLiveData<Resource<CheckStockResponseModel>> =
        MutableLiveData()
    val responseCheckItemStock: LiveData<Resource<CheckStockResponseModel>> =
        checkItemStockResponse

    private val cancelOrderResponse: MutableLiveData<Resource<Unit>> =
        MutableLiveData()
    val responseCancelOrder: LiveData<Resource<Unit>> =
        cancelOrderResponse

    private val applyCouponResponse: MutableLiveData<Resource<StockResponseModel>> =
        MutableLiveData()
    val responseApplyCoupon: LiveData<Resource<StockResponseModel>> =
        applyCouponResponse

    private val sendPaymentLinkToCustomer: MutableLiveData<Resource<InvoiceResponse>> =
        MutableLiveData()
    val responsePaymentLink: LiveData<Resource<InvoiceResponse>> =
        sendPaymentLinkToCustomer


    private val paymentStatusResponse: MutableLiveData<Resource<InvoiceDetailsResponse>> =
        MutableLiveData()
    val responsePaymentStatus: LiveData<Resource<InvoiceDetailsResponse>> =
        paymentStatusResponse

    private var checkPaymentStatusJob: Job? = null

    fun startTrackingPaymentStatus(invoiceId: String) {
        checkPaymentStatusJob = viewModelScope.launch {
            while (isActive) {
                delay(5000)
                getPaymentStatus(invoiceId)
            }
        }
        checkPaymentStatusJob?.start()
    }

    fun stopGettingPaymentStatus() {
        checkPaymentStatusJob?.cancel()
    }

    fun addAddress(language: String,
        addAddressRequestModel: AddAddressRequestModel
    ) {
        addAddressResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.addAddress(language,addAddressRequestModel)
                .collect { values ->
                    addAddressResponse.postValue(values)
                }
        }
    }

    fun editAddress(language: String,
        addressId: String,
        addAddressRequestModel: AddAddressRequestModel
    ) {
        editAddressResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.editAddress(language,addressId, addAddressRequestModel)
                .collect { values ->
                    editAddressResponse.postValue(values)
                }
        }
    }

    fun deleteAddress(language: String,
        addressId: String
    ) {
        deleteAddressResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.deleteAddress(language,addressId)
                .collect { values ->
                    deleteAddressResponse.postValue(values)
                }
        }
    }

    fun storeCredit(
        value: String,language:String,currency:String,
        applyRemoveStoreCreditRequestModel: ApplyRemoveStoreCreditRequestModel
    ) {
        storeCreditResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.storeCredit(value,language,currency, applyRemoveStoreCreditRequestModel)
                .collect { values ->
                    storeCreditResponse.postValue(values)
                }
        }
    }

    fun rivaCheckout(language:String,currency:String,
        rivaOrderRequest: RivaOrderRequest
    ) {
        rivaCheckoutResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.rivaCheckout(language,currency,rivaOrderRequest)
                .collect { values ->
                    rivaCheckoutResponse.postValue(values)
                }
        }
    }

    fun checkItemStockNoAddress(language:String,currency:String,
        stockRequestModel: StockRequestModel
    ) {
        checkItemStockNoAddressResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.checkItemStockNoAddress(language,currency,stockRequestModel)
                .collect { values ->
                    checkItemStockNoAddressResponse.postValue(values)
                }
        }
    }

    fun checkItemStock(language:String,currency:String,
        stockRequestModel: StockRequestModel
    ) {
        checkItemStockResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.checkItemStock(language,currency,stockRequestModel)
                .collect { values ->
                    checkItemStockResponse.postValue(values)
                }
        }
    }


    fun cancelOrder(language:String,currency:String,
        orderCancelRequestModel: OrderCancelRequestModel
    ) {
        cancelOrderResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.cancelOrder(language,currency,orderCancelRequestModel)
                .collect { values ->
                    cancelOrderResponse.postValue(values)
                }
        }
    }

    fun applyCoupon(language:String,currency:String,
        couponRequestModel: CouponRequestModel
    ) {
        applyCouponResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.applyCoupon(language,currency,couponRequestModel)
                .collect { values ->
                    applyCouponResponse.postValue(values)
                }
        }
    }


    fun sendPaymentLinkToCustomer(
        invoiceRequestModel: InvoiceRequestModel
    ) {
        sendPaymentLinkToCustomer.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            paymentRepository.sendPaymentLinkToCustomer(invoiceRequestModel)
                .collect { values ->
                    sendPaymentLinkToCustomer.postValue(values)
                }
        }
    }


    fun getPaymentStatus(
        invoiceId: String,
    ) {
        paymentStatusResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            paymentRepository.getPaymentStatus(invoiceId)
                .collect { values ->
                    paymentStatusResponse.postValue(values)
                }
        }
    }


}