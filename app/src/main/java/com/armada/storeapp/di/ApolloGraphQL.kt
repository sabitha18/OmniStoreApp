package com.armada.storeapp.di

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.network.okHttpClient
import com.armada.storeapp.BarcodeSearchQuery
import com.armada.storeapp.CategoryQuery
import com.armada.storeapp.SearchQuery
import com.armada.storeapp.type.ProductAttributeFilterInput
import com.armada.storeapp.type.ProductAttributeSortInput
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApolloGraphQL {
    private val apiDomain = "https://www.rivafashion.com"

    private class AuthorizationInterceptor(val context: Context) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var selectedLanguage = "en"
            var selectedCurrency = "USD"
            try {
                selectedLanguage = SharedpreferenceHandler(context).getData(
                    SharedpreferenceHandler.RIVA_SELECTED_COUNTRY,
                    "en"
                )!!
                selectedCurrency = SharedpreferenceHandler(context).getData(
                    SharedpreferenceHandler.RIVA_SELECTED_CURRENCY,
                    "USD"
                )!!
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            val request = chain.request().newBuilder()
                .addHeader("store", selectedLanguage)
                .addHeader("Content-Currency", selectedCurrency)
                .build()

            return chain.proceed(request)
        }
    }

    @Singleton
    @Provides
    fun provideGraphQL(context: Context): ApolloClient {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(context))
            .build()
        val apolloClient = ApolloClient.Builder()
            .serverUrl(apiDomain + "/graphql")
            .okHttpClient(okHttpClient)
            .build()
        return apolloClient
    }


    suspend fun getProducts(
        context: Context,
        pageSize: Int, currentPage: Int, filter: ProductAttributeFilterInput,
        sort: Optional<ProductAttributeSortInput?>, search: String
    ): ApolloResponse<CategoryQuery.Data> =
        provideGraphQL(context).query(CategoryQuery(pageSize, currentPage, filter, sort, search))
            .execute()

    suspend fun getScannedProduct(
        context: Context,
        barcode: String
    ): ApolloResponse<BarcodeSearchQuery.Data> =
        provideGraphQL(context).query(
            BarcodeSearchQuery(
                barcode
            )
        ).execute()

    suspend fun searchQuery(
        context: Context,
        searchQuery: String
    ): ApolloResponse<SearchQuery.Data> =
        provideGraphQL(context).query(
            SearchQuery(
                searchQuery
            )
        ).execute()


}