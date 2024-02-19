package com.armada.storeapp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object DirectUrlApi {

    fun setUpRetrofit(): DirectApiCallService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pro.ip-api.com/json/?key=8IZOgbbv8bgOWqQ")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(DirectApiCallService::class.java)
        return service
    }
}