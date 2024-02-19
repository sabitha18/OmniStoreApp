package com.armada.storeapp.ui.home.others.online_requests.pending_orders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.OpenDocumentResponseModel
import com.armada.storeapp.data.model.response.ShopPickOrdersResponseModel
import com.armada.storeapp.data.repositories.WarehouseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendingOrdersListViewModel @Inject constructor
    (
    private val warehouseRepository: WarehouseRepository,
    application: Application
) : AndroidViewModel(application) {

    val open_process_response: MutableLiveData<Resource<OpenDocumentResponseModel>> =
        MutableLiveData()

    val pending_orders_response: MutableLiveData<Resource<ShopPickOrdersResponseModel>> =
        MutableLiveData()

    val filtered_pending_orders_response: MutableLiveData<Resource<ShopPickOrdersResponseModel>> =
        MutableLiveData()

    fun getPendingOrders(shopLocationCode: String, token: String) {
        pending_orders_response.postValue(Resource.Loading())
        viewModelScope.launch {
            warehouseRepository.getPendingOrdersList(shopLocationCode, token).collect { values ->
                pending_orders_response.postValue(values)
            }
        }
    }


    fun getPendingOrdersByDateAndDocno(
        shopLocationCode: String,
        documentNo: String,
        fromDate: String,
        toDate: String,
        token: String
    ) {
        filtered_pending_orders_response.postValue(Resource.Loading())
        viewModelScope.launch {
            warehouseRepository.getPendingOrdersListByDocNoAndDate(
                shopLocationCode,
                documentNo,
                fromDate, toDate,
                token
            ).collect { values ->
                filtered_pending_orders_response.postValue(values)
            }
        }

    }


    fun openProcess(
        shopLocationCode: String,
        documentNo: String,
        toLocation: String,
        shortName: String,
        token: String
    ) {
        open_process_response.postValue(Resource.Loading())
        viewModelScope.launch {
            warehouseRepository.openProcess(
                shopLocationCode,
                documentNo,
                toLocation,
                shortName,
                token
            ).collect { values ->
                open_process_response.postValue(values)
            }
        }
    }

}