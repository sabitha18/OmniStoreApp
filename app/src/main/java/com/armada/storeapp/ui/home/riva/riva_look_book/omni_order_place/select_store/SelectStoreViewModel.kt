package com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.select_store

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.OmniStockResponse
import com.armada.storeapp.data.model.response.ScannedItemDetailsResponse
import com.armada.storeapp.data.repositories.OmniRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectStoreViewModel @Inject constructor
    (
    private val omniRepository: OmniRepository,
    application: Application
) : AndroidViewModel(application) {


    private val storeStockResponse: MutableLiveData<Resource<OmniStockResponse>> =
        MutableLiveData()
    val responseStoreStock: LiveData<Resource<OmniStockResponse>> =
        storeStockResponse


    fun getStoreStockResponse(skuCode: String, countryId: String, storeCode: String,loggedStoreCode:String,countryCode:String) {
        storeStockResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.getAllStockDetails(skuCode, countryId, storeCode, loggedStoreCode, countryCode)
                .collect { values ->
                    storeStockResponse.postValue(values)
                }
        }
    }
}