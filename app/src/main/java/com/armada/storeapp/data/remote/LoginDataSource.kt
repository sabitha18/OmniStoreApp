package com.armada.storeapp.data.remote

import android.util.Log
import com.armada.storeapp.data.ApiService
import com.armada.storeapp.data.model.response.AuthorizeResponseModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject

class LoginDataSource @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun userLogin(
        username: String,
        password: String
    ): Response<AuthorizeResponseModel> {
        val paramText = "grant_type=password&username=$username&password=$password"
        val body: RequestBody = paramText.toRequestBody(
            "text/plain".toMediaTypeOrNull()
        )

        Log.d("eifjijef", paramText.toString())

        Log.d("eifjijef", body.contentLength().toString())

        return apiService.authorizeUser(body)
    }

    suspend fun getUserDetails(accessToken: String) = apiService.getUserDetails(accessToken)
}