package com.armada.storeapp.data

import com.armada.storeapp.data.model.response.EditorialResponse
import com.armada.storeapp.data.model.response.HomeDataModel
import com.armada.storeapp.data.model.response.ParentCategoryResponse
import com.armada.storeapp.data.model.response.SubCategoryResponse
import com.armada.storeapp.ui.home.riva.riva_look_book.search.Stores.StoreDataResponseModel
import com.armada.storeapp.ui.home.riva.riva_look_book.search.Stores.model.StoreResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RivaSecondaryApiService {

    /*** RIVA APIs ***/

    @GET("parent-category-list")
    suspend fun getParentCategoryList(
        @Query("lang") language: String = "en",
    ): Response<ParentCategoryResponse>


    @GET("category-collections")
    suspend fun getSubCategoryList(
        @Query("lang") language: String = "en",
        @Query("parent_category_id") parentCategoryId: String,
        @Query("collection_id") collectionId: String
    ): Response<SubCategoryResponse>

    @GET("collections")
    suspend fun getBannerCollections(
        @Query("lang") language: String,
        @Query("parent_category_id") parentCategoryId: String,
        @Query("collection_id") collectionId: String
    ): Response<HomeDataModel>

    @GET("editorials")
    suspend fun getEditorials(
        @Query("lang") language: String,
        @Query("category_id") categoryId: String
    ): Response<EditorialResponse>

    @GET("store-details")
    suspend fun getStoreList(
        @Query("code") code: String,
        @Query("lang") language: String
    ): Response<StoreDataResponseModel>

}