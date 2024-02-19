package com.armada.storeapp.ui.home.riva.riva_look_book

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.ParentCategoryResponse
import com.armada.storeapp.data.model.response.SkuMasterTypes
import com.armada.storeapp.data.repositories.OmniRepository
import com.armada.storeapp.data.repositories.RivaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RivaLookbookViewModel @Inject constructor
    (
    private val repository: RivaRepository,
    private val omniRepository: OmniRepository,
    application: Application
) : AndroidViewModel(application) {

    private val parentCategoryResponse: MutableLiveData<Resource<ParentCategoryResponse>> =
        MutableLiveData()
    val responeParentCategory: LiveData<Resource<ParentCategoryResponse>> = parentCategoryResponse


    fun getParentCategories() {
        parentCategoryResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getParentCategories()
                .collect { values ->
                    parentCategoryResponse.postValue(values)
                }
        }
    }

}