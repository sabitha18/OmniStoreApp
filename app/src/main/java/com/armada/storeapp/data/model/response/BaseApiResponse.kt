package com.armada.storeapp.data.model.response

import android.util.Log
import com.armada.storeapp.data.Resource
import org.json.JSONObject
import retrofit2.Response

abstract class BaseApiResponse {

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Resource<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
//                Log.i("response", body.toString())
                longLog(body.toString())

                body?.let {
                    return Resource.Success(response.code(), body)
                }
            }
            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
            return error(
                response.code(),
                "${jsonObj.toString()}"
            )
        } catch (e: Exception) {
            Log.e("error res", e.message ?: e.toString())
            return error(0, e.message ?: e.toString())
        }
    }


    open fun longLog(str: String) {
        if (str.length > 4000) {
            Log.d("response", str.substring(0, 4000))
            longLog(str.substring(4000))
        } else Log.d("responsebig", str)
    }

    private fun <T> error(statusCode: Int, errorMessage: String): Resource<T> =
//    ${statusCode} Api call failed
        Resource.Error(statusCode, "$errorMessage")
}