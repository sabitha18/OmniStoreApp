package com.armada.storeapp.ui.home.riva.riva_look_book.search.Stores

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.DirectApiCallService
import com.armada.storeapp.data.DirectUrlApi
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.GetIPAddressResponse
import com.armada.storeapp.data.model.response.ParentCategoryResponse
import com.armada.storeapp.data.repositories.RivaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreListViewModel @Inject constructor
    (
    private val repository: RivaRepository,
    application: Application
) : AndroidViewModel(application) {

    private val storeListResponse: MutableLiveData<Resource<StoreDataResponseModel>> =
        MutableLiveData()
    val responseStoreList: LiveData<Resource<StoreDataResponseModel>> = storeListResponse

    private val locationResponse: MutableLiveData<GetIPAddressResponse> =
        MutableLiveData()
    val responseLocation: LiveData<GetIPAddressResponse> = locationResponse

    fun getStoreList(language:String) {
        storeListResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getStoreList(language)
                .collect { values ->
                    storeListResponse.postValue(values)
                }
        }
    }

    fun getLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = DirectUrlApi.setUpRetrofit().getIPAddressDetails()
            if (response.isSuccessful) {
                locationResponse.postValue(response.body())
            }
        }
    }
}