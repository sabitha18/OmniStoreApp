package com.armada.storeapp.ui.home.riva.riva_look_book.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.EditorialResponse
import com.armada.storeapp.data.model.response.HomeDataModel
import com.armada.storeapp.data.model.response.ParentCategoryResponse
import com.armada.storeapp.data.model.response.SubCategoryResponse
import com.armada.storeapp.data.repositories.RivaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor
    (
    private val repository: RivaRepository,
    application: Application
) : AndroidViewModel(application) {

    private val parentCategoryResponse: MutableLiveData<Resource<ParentCategoryResponse>> =
        MutableLiveData()
    val responeParentCategory: LiveData<Resource<ParentCategoryResponse>> = parentCategoryResponse

    private val bannerCollectionResponse: MutableLiveData<Resource<HomeDataModel>> =
        MutableLiveData()
    val responseBannerCollection: LiveData<Resource<HomeDataModel>> =
        bannerCollectionResponse

    private val categorySubcollectionResponse: MutableLiveData<Resource<SubCategoryResponse>> =
        MutableLiveData()
    val responseCategorySubcollection: LiveData<Resource<SubCategoryResponse>> =
        categorySubcollectionResponse

    private val editorialResponse: MutableLiveData<Resource<EditorialResponse>> = MutableLiveData()
    val responseEditorial: LiveData<Resource<EditorialResponse>> = editorialResponse


    fun getParentCategories() {
        parentCategoryResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getParentCategories()
                .collect { values ->
                    parentCategoryResponse.postValue(values)
                }
        }
    }

    fun getBannerCollections(language: String,parentCategoryId: String, collectionId: String) {
        bannerCollectionResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getBannerCollections(language,parentCategoryId, collectionId)
                .collect { values ->
                    bannerCollectionResponse.postValue(values)
                }
        }
    }




    fun getCategorySubCollection(parentCategoryId: String, collectionId: String) {
        categorySubcollectionResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getSubCategories(parentCategoryId, collectionId)
                .collect { values ->
                    categorySubcollectionResponse.postValue(values)
                }
        }
    }

    fun getEditorials(language:String,
        categoryId: String
    ) {
        editorialResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getEditorials(language,categoryId)
                .collect { values ->
                    editorialResponse.postValue(values)
                }
        }
    }

}