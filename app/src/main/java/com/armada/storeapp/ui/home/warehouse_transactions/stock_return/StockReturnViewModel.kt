package com.armada.storeapp.ui.home.warehouse_transactions.stock_return

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.AddStockReturnRequest
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.data.repositories.InStoreRepository
import com.armada.storeapp.data.repositories.WarehouseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockReturnViewModel @Inject constructor
    (
    private val repository: WarehouseRepository,
    private val picklistRepository: InStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    val toLocationListResponse: MutableLiveData<Resource<ToLocationListResponse>> =
        MutableLiveData()
    val transferTypeListResponse: MutableLiveData<Resource<TransferTypeResponseModel>> =
        MutableLiveData()
    val priorityListResponse: MutableLiveData<Resource<PriorityListResponse>> =
        MutableLiveData()
    val defaultToLocationResponse: MutableLiveData<Resource<DefaultToLocationResponse>> =
        MutableLiveData()
    val stockReturnItemScanResponse: MutableLiveData<Resource<StockReturnItemScanResponse>> =
        MutableLiveData()
    val addStockReturnResponse: MutableLiveData<Resource<AddStockReturnResponse>> =
        MutableLiveData()
    val checkIteminBinResponse: MutableLiveData<Resource<ValidateBinResponse>> = MutableLiveData()
    val validateBinResponse: MutableLiveData<Resource<ValidateBinResponse>> = MutableLiveData()

    fun getToLocationList(
        userCode: String,
        countryCode: String,
        sessionToken: String
    ) {
        toLocationListResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getToLocationList(userCode, countryCode, sessionToken)
                .collect { values ->
                    toLocationListResponse.postValue(values)
                }
        }

    }


    fun getPriorityList(
        userCode: String,
        countryCode: String,
        sessionToken: String
    ) {
        priorityListResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPriorityList(userCode, countryCode, sessionToken)
                .collect { values ->
                    priorityListResponse.postValue(values)
                }
        }

    }


    fun getTransferList(
        userCode: String,
        countryCode: String,
        sessionToken: String
    ) {
        transferTypeListResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTransferList(userCode, countryCode, sessionToken)
                .collect { values ->
                    transferTypeListResponse.postValue(values)
                }
        }

    }

    fun scanStockReturnItem(
        userCode: String,
        locationCode: String,
        barcode: String,
        returnType: String,
        sessionToken: String
    ) {
        stockReturnItemScanResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.scanStockReturnItem(
                userCode,
                locationCode,
                barcode,
                returnType,
                sessionToken
            )
                .collect { values ->
                    stockReturnItemScanResponse.postValue(values)
                }
        }

    }


    fun getDefaultToLocation(
        userCode: String,
        sessionToken: String
    ) {
        defaultToLocationResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getDefaultToLocation(userCode, sessionToken)
                .collect { values ->
                    defaultToLocationResponse.postValue(values)
                }
        }

    }


    fun addStockReturn(
        userCode: String,
        fromLocation: String,
        toLocation: String,
        totalQty: String,
        priority: String,
        typeOfTransfer: String,
        userRemarks: String,
        sessionToken: String,
        addStockReturnRequest: AddStockReturnRequest
    ) {
        addStockReturnResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.addStockReturn(
                userCode,
                fromLocation,
                toLocation,
                totalQty,
                priority,
                typeOfTransfer,
                userRemarks,
                sessionToken,
                addStockReturnRequest
            )
                .collect { values ->
                    addStockReturnResponse.postValue(values)
                }
        }

    }

    fun checkIteminBin(
        storeCode: String,
        bincode: String,
        itemCode: String
    ) {
        Log.e("api pushed", "check item in bin")
        checkIteminBinResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            picklistRepository.checkIteminBin(storeCode, bincode, itemCode)
                .collect { values ->
                    checkIteminBinResponse.postValue(values)
                }
        }

    }

    fun checkIteminBinvalid(
        storeCode: String,
        bincode: String,
        itemCode: String
    ) {
        checkIteminBinResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            picklistRepository.checkIteminBin(storeCode, bincode, itemCode)
                .collect { values ->
                    checkIteminBinResponse.postValue(values)
                }
        }

    }

    fun validateBin(
        storeCode: String,
        bincode: String
    ) {
        validateBinResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            picklistRepository.validateBin(storeCode, bincode)
                .collect { values ->
                    validateBinResponse.postValue(values)
                }
        }

    }


}