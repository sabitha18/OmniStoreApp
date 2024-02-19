package com.armada.storeapp.ui.home.warehouse_transactions.stock_receive

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.AddStockReceiptRequest
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.data.repositories.WarehouseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockReceiveViewModel @Inject constructor
    (
    private val repository: WarehouseRepository,
    application: Application
) : AndroidViewModel(application) {

    val fromLocationListResponse: MutableLiveData<Resource<FromLocationListResponse>> =
        MutableLiveData()
    val stockReceiptDocumentResponse: MutableLiveData<Resource<StockReceiptDocumentResponseModel>> =
        MutableLiveData()
    val transferTypeListResponse: MutableLiveData<Resource<TransferTypeResponseModel>> =
        MutableLiveData()
    val openStockReceiptDocumentResponse: MutableLiveData<Resource<OpenStockReceiptDocumentResponse>> =
        MutableLiveData()
    val addStockReceiptResponse: MutableLiveData<Resource<AddStockReceiptResponse>> =
        MutableLiveData()
    val scanItemResponse: MutableLiveData<Resource<ScanItemResponseModel>> = MutableLiveData()

    fun getFromLocationList(
        userCode: String,
        countryCode: String,
        sessionToken: String
    ) {
        fromLocationListResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFromLocationList(userCode, countryCode, sessionToken)
                .collect { values ->
                    fromLocationListResponse.postValue(values)
                }
        }

    }


    fun getStockReceiptDocumentList(
        locationCode: String,
        userCode: String,
        docDate: String,
        fromLocation: String,
        docNumber: String,
        sessionToken: String
    ) {
        stockReceiptDocumentResponse.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.IO) {
            repository.getStockReceiptDocumentList(
                locationCode,
                userCode,
                docDate,
                fromLocation,
                docNumber,
                sessionToken
            )
                .collect { values ->
                    stockReceiptDocumentResponse.postValue(values)
                }
        }

    }


    fun getTransferTypes(
        sessionToken: String
    ) {
        transferTypeListResponse.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.IO) {
            repository.getTransferTypes(sessionToken)
                .collect { values ->
                    transferTypeListResponse.postValue(values)
                }
        }

    }

    fun openStockReceiptDocument(
        id: String,
        transcationNo: String,
        remarks: String,
        userCode: String,
        fromLocation: String,
        toLocation: String,
        sessionToken: String
    ) {
        openStockReceiptDocumentResponse.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.IO) {
            repository.openStockReceiptDocument(
                id,
                transcationNo,
                remarks,
                userCode,
                fromLocation,
                toLocation,
                sessionToken
            )
                .collect { values ->
                    openStockReceiptDocumentResponse.postValue(values)
                }
        }

    }


    fun addStockReceiptDocument(
        transcationNo: String,
        remarks: String,
        userCode: String,
        fromLocation: String,
        toLocation: String,
        strRemarks: String,
        totalQty: String,
        totalReqQty: String,
        intransitEnabledLoc: String,
        Flocation: String,
        crossdockInType: String,
        disDate: String,
        sessionToken: String, addStockReceiptRequest: AddStockReceiptRequest
    ) {
        addStockReceiptResponse.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.IO) {
            repository.addStockReceiptDocument(
                transcationNo,
                remarks,
                userCode,
                fromLocation,
                toLocation,
                strRemarks,
                totalQty,
                totalReqQty,
                intransitEnabledLoc,
                Flocation,
                crossdockInType,
                disDate,
                sessionToken, addStockReceiptRequest
            )
                .collect { values ->
                    addStockReceiptResponse.postValue(values)
                }
        }

    }

    fun scanItem(barcode: String, sessionToken: String) {
        scanItemResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.scanItem(barcode, sessionToken).collect { values ->
                scanItemResponse.postValue(values)
            }
        }
    }


}