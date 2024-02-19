package com.armada.storeapp.ui.home.warehouse_transactions.stock_adjustment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.StockAdjustmentAddRequest
import com.armada.storeapp.data.model.response.AddStockAdjustmentResponse
import com.armada.storeapp.data.model.response.CheckStockAdjustmentResponse
import com.armada.storeapp.data.model.response.StockAdjustmentScanModelResponse
import com.armada.storeapp.data.repositories.InStoreRepository
import com.armada.storeapp.data.repositories.WarehouseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockAdjustmentViewModel @Inject constructor
    (
    private val repository: WarehouseRepository,
    private val instoreRepository: InStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    val scanStockAdjustmentResponse: MutableLiveData<Resource<StockAdjustmentScanModelResponse>> =
        MutableLiveData()
    val addStockAdjustmentResponse: MutableLiveData<Resource<AddStockAdjustmentResponse>> =
        MutableLiveData()
    val checkStockAdjustmentResponse: MutableLiveData<Resource<CheckStockAdjustmentResponse>> =
        MutableLiveData()

    fun scanModelStockAdjustment(
        userCode: String,
        locationCode: String,
        modelCode: String,
        sessionToken: String
    ) {
        scanStockAdjustmentResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.scanModelStockAdjustment(userCode, locationCode, modelCode, sessionToken)
                .collect { values ->
                    scanStockAdjustmentResponse.postValue(values)
                }
        }

    }


    fun addStockAdjustment(
        userCode: String,
        locationCode: String,
        modelCode: String,
        totalQty: String,
        totalCount: String,
        totalDiffQty: String,
        sessionToken: String,
        addStockAdjustmentAddRequest: StockAdjustmentAddRequest
    ) {
        addStockAdjustmentResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.addStockAdjustment(
                userCode,
                locationCode,
                modelCode,
                totalQty,
                totalCount,
                totalDiffQty,
                sessionToken,
                addStockAdjustmentAddRequest
            )
                .collect { values ->
                    addStockAdjustmentResponse.postValue(values)
                }
        }

    }

    fun checkSkuForAdjustment(
        storeCode: String,
        styleCode: String
    ) {
        checkStockAdjustmentResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            instoreRepository.checkSkuForAdjustment(storeCode, styleCode)
                .collect { values ->
                    checkStockAdjustmentResponse.postValue(values)
                }
        }

    }
}