package com.armada.storeapp.data

import com.armada.storeapp.data.model.response.AuthorizeResponseModel
import com.armada.storeapp.data.model.response.GetIPAddressResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET

interface DirectApiCallService {

    @GET("")
    suspend fun getIPAddressDetails(
    ): Response<GetIPAddressResponse>
}