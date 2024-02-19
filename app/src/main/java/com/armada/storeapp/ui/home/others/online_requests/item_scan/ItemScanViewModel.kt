package com.armada.storeapp.ui.home.others.online_requests.item_scan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.CompletePickRequest
import com.armada.storeapp.data.model.response.CompletePickResponseModel
import com.armada.storeapp.data.model.response.ScanItemResponseModel
import com.armada.storeapp.data.model.response.ShopPickReasonResponseModel
import com.armada.storeapp.data.model.response.ValidateBinResponse
import com.armada.storeapp.data.repositories.InStoreRepository
import com.armada.storeapp.data.repositories.WarehouseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemScanViewModel @Inject constructor
    (
    private val warehouseRepository: WarehouseRepository,
    private val picklistRepository: InStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    val scan_item_response: MutableLiveData<Resource<ScanItemResponseModel>> = MutableLiveData()

    val remarks_reasons: MutableLiveData<Resource<ShopPickReasonResponseModel>> = MutableLiveData()

    val pick_response: MutableLiveData<Resource<CompletePickResponseModel>> = MutableLiveData()

    val checkIteminBinResponse: MutableLiveData<Resource<ValidateBinResponse>> = MutableLiveData()

    fun scanItem(barcode: String, token: String) {
        scan_item_response.postValue(Resource.Loading())
        viewModelScope.launch {
            warehouseRepository.scanItem(barcode, token).collect { values ->
                scan_item_response.postValue(values)
            }
        }
    }


    fun getRemarks(token: String) {
        remarks_reasons.postValue(Resource.Loading())
        viewModelScope.launch {
            warehouseRepository.getReasons(token).collect { values ->
                remarks_reasons.postValue(values)
            }
        }
    }


    fun completePick(completePickRequest: CompletePickRequest, token: String) {
        pick_response.postValue(Resource.Loading())
        viewModelScope.launch {
            warehouseRepository.completePick(completePickRequest, token).collect { values ->
                pick_response.postValue(values)
            }
        }
    }


    fun checkIteminBin(
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
}