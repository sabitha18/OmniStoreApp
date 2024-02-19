package com.armada.storeapp.data.remote

import android.content.Context
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.armada.storeapp.BarcodeSearchQuery
import com.armada.storeapp.CategoryQuery
import com.armada.storeapp.SearchQuery
import com.armada.storeapp.data.GraphQLApiService
import com.armada.storeapp.di.ApolloGraphQL
import com.armada.storeapp.type.ProductAttributeFilterInput
import com.armada.storeapp.type.ProductAttributeSortInput
import javax.inject.Inject

class RivaProductDataSource @Inject constructor(
) : GraphQLApiService {

//    override suspend fun getProducts(
//        pageSize: Int, currentPage: Int, filter: ProductAttributeFilterInput,
//        sort: Optional<ProductAttributeSortInput?>, search: String
//    ): Flow<ApolloResponse<CategoryQuery.Data>> {
//        return ApolloGraphQL.getProducts(pageSize, currentPage, filter, sort, search).toFlow()
//
//    }

    override suspend fun getProducts(
        context: Context,
        pageSize: Int, currentPage: Int, filter: ProductAttributeFilterInput,
        sort: Optional<ProductAttributeSortInput?>, search: String
    ): ApolloResponse<CategoryQuery.Data> {
        return ApolloGraphQL.getProducts(context, pageSize, currentPage, filter, sort, search)
    }

    override suspend fun getScannedProduct(
        context: Context,
        barcode: String
    ): ApolloResponse<BarcodeSearchQuery.Data> {
        return ApolloGraphQL.getScannedProduct(context, barcode)
    }

    override suspend fun searchProducts(
        context: Context,
        searchQuery: String
    ): ApolloResponse<SearchQuery.Data> {
        return ApolloGraphQL.searchQuery(context, searchQuery)
    }

}