package com.armada.storeapp.ui.home.riva.riva_look_book.select_country

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.StoreData
import com.armada.storeapp.data.model.response.CountryStoreResponse
import com.armada.storeapp.data.model.response.ParentCategoryResponse
import com.armada.storeapp.data.repositories.RivaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCountryViewModel @Inject constructor
    (
    private val repository: RivaRepository,
    application: Application
) : AndroidViewModel(application) {

    private val countryResponse: MutableLiveData<Resource<CountryStoreResponse>> =
        MutableLiveData()
    val responseCountryData: LiveData<Resource<CountryStoreResponse>> = countryResponse


    fun getCountryStores() {
        countryResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCountryStores()
                .collect { values ->
                    countryResponse.postValue(values)
                }
        }
    }

}