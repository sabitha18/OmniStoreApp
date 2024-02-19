package com.armada.storeapp.data

sealed class Resource<T>(
    val statusCode: Int = 0,
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(statusCode: Int, data: T) : Resource<T>(statusCode, data)
    class Error<T>(statusCode: Int, message: String, data: T? = null) :
        Resource<T>(statusCode, data, message)

    class Loading<T> : Resource<T>()
}