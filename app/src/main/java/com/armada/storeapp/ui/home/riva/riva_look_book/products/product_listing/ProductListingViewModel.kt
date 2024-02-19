package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.armada.storeapp.CategoryQuery
import com.armada.storeapp.data.GraphQLResult
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.AssociateProductResponse
import com.armada.storeapp.data.model.response.SubCategoryResponse
import com.armada.storeapp.data.repositories.RivaProductRepository
import com.armada.storeapp.data.repositories.RivaRepository
import com.armada.storeapp.type.ProductAttributeFilterInput
import com.armada.storeapp.type.ProductAttributeSortInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListingViewModel @Inject constructor
    (
    private val repository: RivaProductRepository,
    private val rivaRepository: RivaRepository,
    application: Application
) : AndroidViewModel(application) {

    private val productResponse: MutableLiveData<GraphQLResult<CategoryQuery.Products>> =
        MutableLiveData()
    val responseProduct: LiveData<GraphQLResult<CategoryQuery.Products>> = productResponse

    private val associateProductsResponse: MutableLiveData<Resource<AssociateProductResponse>> =
        MutableLiveData()
    val responseAssociateProduct: LiveData<Resource<AssociateProductResponse>> =
        associateProductsResponse

    private val subCategoryResponse: MutableLiveData<Resource<SubCategoryResponse>> =
        MutableLiveData()
    val responseSubCategory: LiveData<Resource<SubCategoryResponse>> = subCategoryResponse


    fun getSubCategories(parentCategoryId: String, collectionId: String) {
        subCategoryResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.getSubCategories(parentCategoryId, collectionId)
                .collect { values ->
                    subCategoryResponse.postValue(values)
                }
        }
    }


//    fun getProducts(
//        pageSize: Int, currentPage: Int, filter: ProductAttributeFilterInput,
//        sort: Optional<ProductAttributeSortInput?>, search: String
//    ) {
//        productResponse.postValue(GraphQLResult.Loading)
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.getProducts(pageSize, currentPage, filter, sort, search)
//                .collect { values ->
//                    val queryData = values
//                    productResponse.postValue(values)
//                }
//        }
//    }


    fun getProducts(
        context: Context,
        pageSize: Int, currentPage: Int, filter: ProductAttributeFilterInput,
        sort: Optional<ProductAttributeSortInput?>, search: String
    ) {
        productResponse.postValue(GraphQLResult.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            repository.getProducts(context, pageSize, currentPage, filter, sort, search)
                .collect { values ->
                    productResponse.postValue(values)
                }
        }
    }

    fun getAssociateProducts(productIds: String) {
        associateProductsResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.getAssociateProducts(productIds)
                .collect { values ->
                    associateProductsResponse.postValue(values)
                }
        }
    }


}
