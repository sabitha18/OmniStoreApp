package com.armada.storeapp.ui.home.riva.riva_look_book.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.ParentCategoryResponse
import com.armada.storeapp.data.model.response.SubCategoryResponse
import com.armada.storeapp.data.repositories.RivaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor
    (
    private val repository: RivaRepository,
    application: Application
) : AndroidViewModel(application) {

    private val parentCategoryResponse: MutableLiveData<Resource<ParentCategoryResponse>> =
        MutableLiveData()
    val responeParentCategory: LiveData<Resource<ParentCategoryResponse>> = parentCategoryResponse

    private val subCategoryResponse: MutableLiveData<Resource<SubCategoryResponse>> =
        MutableLiveData()
    val responseSubCategory: LiveData<Resource<SubCategoryResponse>> = subCategoryResponse


    fun getParentCategories() {
        parentCategoryResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getParentCategories()
                .collect { values ->
                    parentCategoryResponse.postValue(values)
                }
        }
    }


    fun getSubCategories(parentCategoryId: String, collectionId: String) {
        subCategoryResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getSubCategories(parentCategoryId, collectionId)
                .collect { values ->
                    subCategoryResponse.postValue(values)
                }
        }
    }
}
