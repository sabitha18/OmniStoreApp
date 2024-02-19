package com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.OmniOrderPlaceRequest
import com.armada.storeapp.data.model.request.OrderInvoiceRequest
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.data.repositories.OmniRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderPlaceViewModel @Inject constructor
    (
    private val omniRepository: OmniRepository,
    application: Application
) : AndroidViewModel(application) {


    var omniOrderInvoice: OmniInvoiceResponse? = null

    private val searchCustomerByIdResponse: MutableLiveData<Resource<SearchCustomerResponse>> =
        MutableLiveData()
    val responseSearchCustomerById: LiveData<Resource<SearchCustomerResponse>> =
        searchCustomerByIdResponse

    private val omniInvoiceResponse: MutableLiveData<Resource<OmniInvoiceResponse>> =
        MutableLiveData()
    val responseOmniInvoice: LiveData<Resource<OmniInvoiceResponse>> =
        omniInvoiceResponse

    private val omniOrderPlaceResponse: MutableLiveData<Resource<OmniOrderPlaceResponse>> =
        MutableLiveData()
    val responseOmniOrderPlace: LiveData<Resource<OmniOrderPlaceResponse>> =
        omniOrderPlaceResponse

    private val getStoreEmployeeResponse: MutableLiveData<Resource<GetStoreEmployeeResponse>> =
        MutableLiveData()
    val responseGetStoreEmployee: LiveData<Resource<GetStoreEmployeeResponse>> =
        getStoreEmployeeResponse

    private val getTimeSlotResponse: MutableLiveData<Resource<GetTimeSlotResponse>> =
        MutableLiveData()
    val responseGetTimeSlot: LiveData<Resource<GetTimeSlotResponse>> =
        getTimeSlotResponse

    fun searchCustomerById(
        token: String,
        customerId: String
    ) {
        searchCustomerByIdResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.searchCustomerById(
                token,
                customerId
            )
                .collect { values ->
                    searchCustomerByIdResponse.postValue(values)
                }
        }
    }


    fun omniInvoice(sessionToken: String,
        omniInvoiceRequest: OrderInvoiceRequest
    ) {
        omniInvoiceResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.omniInvoice(sessionToken,omniInvoiceRequest)
                .collect { values ->
                    omniInvoiceResponse.postValue(values)
                }
        }
    }


    fun omniOrderPlace(sessionToken: String,
        omniOrderPlaceRequest: OmniOrderPlaceRequest
    ){
        omniOrderPlaceResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.omniOrderPlace(sessionToken,omniOrderPlaceRequest)
                .collect { values ->
                    omniOrderPlaceResponse.postValue(values)
                }
        }
    }






    fun getStoreEmployees(
        storeId: String
    ){
        getStoreEmployeeResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.getStoreEmployees(storeId)
                .collect { values ->
                    getStoreEmployeeResponse.postValue(values)
                }
        }
    }

    fun getTimeSlot(
    ){
        getTimeSlotResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.getTimeSlot()
                .collect { values ->
                    getTimeSlotResponse.postValue(values)
                }
        }
    }

    fun deleteAllProductsFromBag(){
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.deleteAllProductsFromBag()
        }
    }
}