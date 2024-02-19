package com.armada.storeapp.ui.home.instore_transactions.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.CommonBinTransferRequest
import com.armada.storeapp.data.model.response.CheckbinInventoryResponse
import com.armada.storeapp.data.model.response.CommonBintransferResponse
import com.armada.storeapp.data.model.response.ValidateBinResponse
import com.armada.storeapp.data.repositories.InStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor
    (
    private val repository: InStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    val commonBintransferResponse: MutableLiveData<Resource<CommonBintransferResponse>> =
        MutableLiveData()
    val validateBinResponse: MutableLiveData<Resource<ValidateBinResponse>> = MutableLiveData()
    val checkbinInventoryResponse: MutableLiveData<Resource<CheckbinInventoryResponse>> =
        MutableLiveData()

    fun commonBinTransfer(
        commonBinTransferRequest: CommonBinTransferRequest
    ) {
        commonBintransferResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.commonBinTransfer(commonBinTransferRequest)
                .collect { values ->
                    commonBintransferResponse.postValue(values)
                }
        }

    }

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


}