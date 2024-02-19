package com.armada.storeapp.data

import com.apollographql.apollo3.api.Error

sealed class DataSourceException(
    val messageResource: Any?
) : RuntimeException() {
    class Unexpected(messageResource: Int) : DataSourceException(messageResource)
    class Server(error: Error?) : DataSourceException(error)
}