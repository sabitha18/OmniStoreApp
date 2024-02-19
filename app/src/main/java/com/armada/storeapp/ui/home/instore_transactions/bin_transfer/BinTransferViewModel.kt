package com.armada.storeapp.ui.home.instore_transactions.bin_transfer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.PicklistTransferRequest
import com.armada.storeapp.data.model.response.CheckbinInventoryResponse
import com.armada.storeapp.data.model.response.PicklistBintransferResponseModel
import com.armada.storeapp.data.model.response.ValidateBinResponse
import com.armada.storeapp.data.repositories.InStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BinTransferViewModel @Inject constructor
    (
    private val repository: InStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    val bintransferResponse: MutableLiveData<Resource<PicklistBintransferResponseModel>> =
        MutableLiveData()
    val validateBinResponse: MutableLiveData<Resource<ValidateBinResponse>> = MutableLiveData()
    val checkbinInventoryResponse: MutableLiveData<Resource<CheckbinInventoryResponse>> =
        MutableLiveData()
    val checkIteminBinResponse: MutableLiveData<Resource<ValidateBinResponse>> = MutableLiveData()


    fun validateBin(
        storeCode: String, bincode: String
    ) {
        validateBinResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.validateBin(storeCode, bincode)
                .collect { values ->
                    validateBinResponse.postValue(values)
                }
        }

    }

    fun checkbinInventory(
        storeCode: String,
        bincode: String,
    ) {
        checkbinInventoryResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkbinInventory(storeCode, bincode)
                .collect { values ->
                    checkbinInventoryResponse.postValue(values)
                }
        }

    }

    fun bintransfer(
        picklistTransferRequest: PicklistTransferRequest
    ) {
        bintransferResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.picklistBintransfer(picklistTransferRequest)
                .collect { values ->
                    bintransferResponse.postValue(values)
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