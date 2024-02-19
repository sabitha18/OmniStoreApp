package com.armada.storeapp.ui.home.others.item_search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.ItemBinSearchResponse
import com.armada.storeapp.data.repositories.InStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemSearchViewModel @Inject constructor
    (
    private val repository: InStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    val itemBinSearchResponse: MutableLiveData<Resource<ItemBinSearchResponse>> =
        MutableLiveData()


    fun itemOrBinSearch(
        limit: String, offset: String, isActive: String,
        itemCode: String,
        bincode: String,
        storeCode: String
    ) {
        itemBinSearchResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.itemOrBinSearch(limit, offset, isActive, itemCode, bincode, storeCode)
                .collect { values ->
                    itemBinSearchResponse.postValue(values)
                }
        }

    }

}