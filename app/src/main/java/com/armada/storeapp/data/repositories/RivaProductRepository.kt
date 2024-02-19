package com.armada.storeapp.data.repositories

import android.content.Context
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.armada.storeapp.BarcodeSearchQuery
import com.armada.storeapp.CategoryQuery
import com.armada.storeapp.R
import com.armada.storeapp.SearchQuery
import com.armada.storeapp.data.DataSourceException
import com.armada.storeapp.data.GraphQLResult
import com.armada.storeapp.data.remote.RivaProductDataSource
import com.armada.storeapp.di.ApolloGraphQL
import com.armada.storeapp.type.ProductAttributeFilterInput
import com.armada.storeapp.type.ProductAttributeSortInput
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@ActivityRetainedScoped
class RivaProductRepository @Inject constructor(
    private val rivaProductDataSource: RivaProductDataSource
) {


//    suspend fun getProducts(
//        pageSize: Int, currentPage: Int, filter: ProductAttributeFilterInput,
//        sort: Optional<ProductAttributeSortInput?>, search: String
//    ): Flow<ApolloResponse<CategoryQuery.Data>> {
//        return ApolloGraphQL.getProducts(pageSize, currentPage, filter, sort, search).toFlow()
//    }

    suspend fun getProducts(context:Context,
        pageSize: Int, currentPage: Int, filter: ProductAttributeFilterInput,
        sort: Optional<ProductAttributeSortInput?>, search: String
    ): Flow<GraphQLResult<CategoryQuery.Products>> =
        flow<GraphQLResult<CategoryQuery.Products>> {
            try {
                val result =
                    rivaProductDataSource.getProducts(context,pageSize, currentPage, filter, sort, search)
                if (result.hasErrors()) {
                    emit(GraphQLResult.Error(DataSourceException.Server(result.errors?.first())))
                } else {
                    emit(GraphQLResult.Success(result.data?.products!!))
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                GraphQLResult.Error(DataSourceException.Unexpected(R.string.error_unexpected_message))
            }

        }.onStart { emit(GraphQLResult.Loading) }

    suspend fun getScannedProduct(context:Context,
        barcode: String
    ): Flow<GraphQLResult<BarcodeSearchQuery.Data>> =
        flow<GraphQLResult<BarcodeSearchQuery.Data>> {
            try {
                val result =
                    rivaProductDataSource.getScannedProduct(context,barcode)
                if (result.hasErrors()) {
                    emit(GraphQLResult.Error(DataSourceException.Server(result.errors?.first())))
                } else {
                    emit(GraphQLResult.Success(result.data!!))
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                GraphQLResult.Error(DataSourceException.Unexpected(R.string.error_unexpected_message))
            }

        }.onStart { emit(GraphQLResult.Loading) }

    suspend fun searchProducts(context:Context,
        searchQuery: String
    ): Flow<GraphQLResult<SearchQuery.Data>> =
        flow<GraphQLResult<SearchQuery.Data>> {
            try {
                val result =
                    rivaProductDataSource.searchProducts(context,searchQuery)
                if (result.hasErrors()) {
                    emit(GraphQLResult.Error(DataSourceException.Server(result.errors?.first())))
                } else {
                    emit(GraphQLResult.Success(result.data!!))
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                GraphQLResult.Error(DataSourceException.Unexpected(R.string.error_unexpected_message))
            }

        }.onStart { emit(GraphQLResult.Loading) }

}