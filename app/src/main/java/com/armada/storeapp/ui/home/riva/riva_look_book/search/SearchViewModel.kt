package com.armada.storeapp.ui.home.riva.riva_look_book.search

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.armada.storeapp.BarcodeSearchQuery
import com.armada.storeapp.CategoryQuery
import com.armada.storeapp.SearchQuery
import com.armada.storeapp.data.GraphQLResult
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.DeliverOmniOrderRequest
import com.armada.storeapp.data.model.response.ArticleProductsResponse
import com.armada.storeapp.data.model.response.AssociateProductResponse
import com.armada.storeapp.data.model.response.OmniOrdersResponse
import com.armada.storeapp.data.model.response.SubCategoryResponse
import com.armada.storeapp.data.repositories.OmniRepository
import com.armada.storeapp.data.repositories.RivaProductRepository
import com.armada.storeapp.data.repositories.RivaRepository
import com.armada.storeapp.type.ProductAttributeFilterInput
import com.armada.storeapp.type.ProductAttributeSortInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor
    (
    private val repository: RivaProductRepository,
    private val omniRepository: OmniRepository,
    application: Application
) : AndroidViewModel(application) {

    private val scannedProductResponse: MutableLiveData<GraphQLResult<BarcodeSearchQuery.Data>> =
        MutableLiveData()
    val responseScannedProduct: LiveData<GraphQLResult<BarcodeSearchQuery.Data>> =
        scannedProductResponse

    private val searchProductResponse: MutableLiveData<GraphQLResult<SearchQuery.Data>> =
        MutableLiveData()
    val responseSearchProduct: LiveData<GraphQLResult<SearchQuery.Data>> =
        searchProductResponse

    private val articleResponse: MutableLiveData<Resource<ArticleProductsResponse>> =
        MutableLiveData()
    val responseArticle: LiveData<Resource<ArticleProductsResponse>> =
        articleResponse

    fun getScannedProduct(
        context: Context,
        barcode: String
    ) {
        scannedProductResponse.postValue(GraphQLResult.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            repository.getScannedProduct(context,barcode)
                .collect { values ->
                    scannedProductResponse.postValue(values)
                }
        }
    }

    fun searchProducts(context: Context,searchQuery: String) {
        searchProductResponse.postValue(GraphQLResult.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            repository.searchProducts(context,searchQuery)
                .collect { values ->
                    searchProductResponse.postValue(values)
                }
        }
    }

    fun getProductsByArticleNumber(
        searchValue: String,
        storeId: String,
        fromFormName: String,
    )  {
        articleResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.getProductsByArticleNumber(searchValue,storeId, fromFormName)
                .collect { values ->
                    articleResponse.postValue(values)
                }
        }

    }


}
