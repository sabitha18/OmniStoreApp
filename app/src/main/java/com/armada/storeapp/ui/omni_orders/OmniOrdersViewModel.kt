package com.armada.storeapp.ui.omni_orders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.*
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.data.repositories.InStoreRepository
import com.armada.storeapp.data.repositories.OmniRepository
import com.armada.storeapp.data.repositories.WarehouseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OmniOrdersViewModel @Inject constructor
    (
    private val repository: OmniRepository,
    application: Application
) : AndroidViewModel(application) {

    private val omniOrdersResponse: MutableLiveData<Resource<OmniOrdersResponse>> =
        MutableLiveData()
    val responseOmniOrdersResponse: LiveData<Resource<OmniOrdersResponse>> =
        omniOrdersResponse

    private val orderAcceptResponse: MutableLiveData<Resource<PendingOrderAcceptResponse>> =
        MutableLiveData()
    val responsePendingOrderAccept: LiveData<Resource<PendingOrderAcceptResponse>> =
        orderAcceptResponse

    private val orderDetailsResponse: MutableLiveData<Resource<OmniOrderDetailsResponse>> =
        MutableLiveData()
    val responseOrderDetails: LiveData<Resource<OmniOrderDetailsResponse>> =
        orderDetailsResponse

    private val scannedItemResponse: MutableLiveData<Resource<ScannedItemDetailsResponse>> =
        MutableLiveData()
    val responseScannedItem: LiveData<Resource<ScannedItemDetailsResponse>> =
        scannedItemResponse

    private val saveOmniOrdersResponse: MutableLiveData<Resource<SaveOmniOrderResponse>> =
        MutableLiveData()
    val responseSaveOmniOrder: LiveData<Resource<SaveOmniOrderResponse>> =
        saveOmniOrdersResponse


    private val deliverStorePickupOrderResponse: MutableLiveData<Resource<OmniOrdersResponse>> =
        MutableLiveData()
    val responseDeliverStorePickupOrder: LiveData<Resource<OmniOrdersResponse>> =
        deliverStorePickupOrderResponse


    fun getOmniOrders(
        startDate: String,
        endDate: String,
        storeCode: String,
        searchString: String,
        userCode: String,
        orderByStatus: String
    ) {
        omniOrdersResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getOmniOrders(
                startDate,
                endDate,
                storeCode,
                searchString,
                userCode,
                orderByStatus
            )
                .collect { values ->
                    omniOrdersResponse.postValue(values)
                }
        }

    }

    fun acceptPendingOrder(
        sessionToken: String,
        pendingOrderAcceptRequest: PendingOrderAcceptRequest
    ) {
        orderAcceptResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.acceptPendingOrder(sessionToken, pendingOrderAcceptRequest)
                .collect { values ->
                    orderAcceptResponse.postValue(values)
                }
        }

    }

    fun getOmniOrderDetails(
        id: String
    ) {
        orderDetailsResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getOmniOrderDetails(id)
                .collect { values ->
                    orderDetailsResponse.postValue(values)
                }
        }

    }


    fun omniItemScan(
        skuCode: String,
        storeId: String,
        priceListId: String,
    ) {
        scannedItemResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.omniItemScan(skuCode, storeId, priceListId)
                .collect { values ->
                    scannedItemResponse.postValue(values)
                }
        }

    }


    fun saveOmniOrder(
        saveOmniOrderRequest: SaveOmniOrderRequest
    ) {
        saveOmniOrdersResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveOmniOrder(saveOmniOrderRequest)
                .collect { values ->
                    saveOmniOrdersResponse.postValue(values)
                }
        }

    }


    fun deliverStorePickupOrder(
        deliverOmniOrderRequest: DeliverOmniOrderRequest
    ) {
        deliverStorePickupOrderResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.deliverStorePickupOrder(deliverOmniOrderRequest)
                .collect { values ->
                    deliverStorePickupOrderResponse.postValue(values)
                }
        }

    }
}
