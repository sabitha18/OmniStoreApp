package com.armada.wmshandler.utils

sealed class NetworkResult<T>(
    val statusCode: Int = 0,
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(statusCode: Int, data: T) : NetworkResult<T>(statusCode, data)
    class Error<T>(statusCode: Int, message: String, data: T? = null) :
        NetworkResult<T>(statusCode, data, message)

    class Loading<T> : NetworkResult<T>()
}