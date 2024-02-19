package com.armada.storeapp.ui.home.instore_transactions.picklist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.ManualPicklistRequest
import com.armada.storeapp.data.model.request.PicklistTransferRequest
import com.armada.storeapp.data.model.request.SkipPicklistTransferRequest
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.data.repositories.InStoreRepository
import com.armada.storeapp.data.repositories.WarehouseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PicklistViewModel @Inject constructor
    (
    private val repository: InStoreRepository,
    private val warehouseRepository: WarehouseRepository,
    application: Application
) : AndroidViewModel(application) {

    val picklistResponse: MutableLiveData<Resource<PicklistResponseModel>> =
        MutableLiveData()
    val picklistDetailsResponse: MutableLiveData<Resource<PicklistDetailsResponseModel>> =
        MutableLiveData()
    val skippedPicklistResponse: MutableLiveData<Resource<PicklistResponseModel>> =
        MutableLiveData()
    val scanBinPicklistResponse: MutableLiveData<Resource<ValidateBinResponse>> =
        MutableLiveData()
    val picklistBintransferResponse: MutableLiveData<Resource<PicklistBintransferResponseModel>> =
        MutableLiveData()
    val skipReasonListResponse: MutableLiveData<Resource<SkipReasonListResponse>> =
        MutableLiveData()
    val skipPicklistTransferResponse: MutableLiveData<Resource<SkipPicklistTransferResponse>> =
        MutableLiveData()
    val getImageResponse: MutableLiveData<Resource<GetImageResponse>> = MutableLiveData()
    val binTransferSkippedItemResponse: MutableLiveData<Resource<PicklistBintransferResponseModel>> =
        MutableLiveData()
    val manualPicklistResponse: MutableLiveData<Resource<ManualPicklistResponse>> =
        MutableLiveData()

    val checkIteminBinResponse: MutableLiveData<Resource<ValidateBinResponse>> = MutableLiveData()
    val createPicklistSkuResponse: MutableLiveData<Resource<CreatePicklistSkuResponse>> =
        MutableLiveData()
    val getDestinationBinResponse: MutableLiveData<Resource<GetDestinationBinResponse>> =
        MutableLiveData()

    fun getPicklist(
        isStatus: String,
        storeCode: String,
        searchString: String
    ) {
        picklistResponse.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.IO) {
            repository.getPickList(isStatus, storeCode, searchString)
                .collect { values ->
                    picklistResponse.postValue(values)
                }
        }

    }

    fun getPicklistByPages(
        isStatus: String,
        storeCode: String,
        searchString: String,
        itemCount: String,
        pageNo: String,
        type: String,
    ) {
        picklistResponse.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.IO) {
            repository.getPicklistByPages(isStatus, storeCode, searchString, itemCount, pageNo, type)
                .collect { values ->
                    picklistResponse.postValue(values)
                }
        }

    }

    fun getPickListDetails(
        isStatus: String,
        storeCode: String,
        picklistHeaderId: String
    ) {
        picklistDetailsResponse.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.IO) {
            repository.getPickListDetails(isStatus, storeCode, picklistHeaderId)
                .collect { values ->
                    picklistDetailsResponse.postValue(values)
                }
        }

    }

    fun createManualPicklist(manualPicklistRequest: ManualPicklistRequest) {
        manualPicklistResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.createManualPicklist(manualPicklistRequest)
                .collect { values ->
                    manualPicklistResponse.postValue(values)
                }
        }

    }

    fun getCreatePicklistSkus(
        storeCode: String,
        styleCode: String,
    ) {
        createPicklistSkuResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCreatePicklistSkus(storeCode, styleCode)
                .collect { values ->
                    createPicklistSkuResponse.postValue(values)
                }
        }

    }

    fun getDestinationBinList(
        storeId: String
    ) {
        getDestinationBinResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getDestinationBinList(storeId)
                .collect { values ->
                    getDestinationBinResponse.postValue(values)
                }
        }

    }

    fun getSkippedPicklist(
        storeCode: String,
    ) {
        skippedPicklistResponse.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.IO) {
            repository.getSkippedPicklist(storeCode)
                .collect { values ->
                    skippedPicklistResponse.postValue(values)
                }
        }

    }

    fun getSkipReasonList() {
        skipReasonListResponse.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.IO) {
            repository.getSkipItemReasonList()
                .collect { values ->
                    skipReasonListResponse.postValue(values)
                }
        }

    }

    fun skipPicklistTransfer(skipPicklistTransferRequest: SkipPicklistTransferRequest) {
        skipPicklistTransferResponse.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.IO) {
            repository.skipPicklistTransfer(skipPicklistTransferRequest)
                .collect { values ->
                    skipPicklistTransferResponse.postValue(values)
                }
        }

    }

    fun getImage(
        departmentCode: String,
        productCode: String,
        sessionToken: String
    ) {
        getImageResponse.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.IO) {
            warehouseRepository.getPickItemImage(departmentCode, productCode, sessionToken)
                .collect { values ->
                    getImageResponse.postValue(values)
                }
        }

    }

    fun scanBinPicklist(
        storeCode: String,
        bincode: String
    ) {
        scanBinPicklistResponse.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.IO) {
            repository.validateBin(storeCode, bincode)
                .collect { values ->
                    scanBinPicklistResponse.postValue(values)
                }
        }

    }


    fun picklistBintransfer(
        picklistTransferRequest: PicklistTransferRequest
    ) {
        picklistBintransferResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.picklistBintransfer(picklistTransferRequest)
                .collect { values ->
                    picklistBintransferResponse.postValue(values)
                }
        }

    }

    fun binTransferSkippedItems(
        picklistTransferRequest: PicklistTransferRequest
    ) {
        binTransferSkippedItemResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.binTransferSkippedItems(picklistTransferRequest)
                .collect { values ->
                    binTransferSkippedItemResponse.postValue(values)
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
            repository.checkIteminBin(storeCode, bincode, itemCode)
                .collect { values ->
                    checkIteminBinResponse.postValue(values)
                }
        }

    }

}
