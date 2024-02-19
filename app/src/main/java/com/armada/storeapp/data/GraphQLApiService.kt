package com.armada.storeapp.data

import android.content.Context
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.armada.storeapp.BarcodeSearchQuery
import com.armada.storeapp.CategoryQuery
import com.armada.storeapp.Category_listQuery
import com.armada.storeapp.SearchQuery
import com.armada.storeapp.type.ProductAttributeFilterInput
import com.armada.storeapp.type.ProductAttributeSortInput

interface GraphQLApiService {

    //    suspend fun getProducts(
//        pageSize: Int, currentPage: Int, filter: ProductAttributeFilterInput,
//        sort: Optional<ProductAttributeSortInput?>, search: String
//    ): Flow<ApolloResponse<CategoryQuery.Data>>
    suspend fun getProducts(context:Context,
        pageSize: Int, currentPage: Int, filter: ProductAttributeFilterInput,
        sort: Optional<ProductAttributeSortInput?>, search: String
    ): ApolloResponse<CategoryQuery.Data>

    suspend fun getScannedProduct(context:Context,barcode:String):ApolloResponse<BarcodeSearchQuery.Data>

    suspend fun searchProducts(context:Context,searchQuery: String):ApolloResponse<SearchQuery.Data>
}