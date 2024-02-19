package com.armada.storeapp.data

sealed class GraphQLResult<out T> {
    data class Success<out T>(val data: T) : GraphQLResult<T>()
    data class Error(val exception: DataSourceException) : GraphQLResult<Nothing>()
    object Loading : GraphQLResult<Nothing>()
}

inline fun <T : Any> GraphQLResult<T>.onSuccess(action: (T) -> Unit): GraphQLResult<T> {
    if (this is GraphQLResult.Success) action(data)
    return this
}

inline fun <T : Any> GraphQLResult<T>.onError(action: (DataSourceException) -> Unit): GraphQLResult<T> {
    if (this is GraphQLResult.Error) action(exception)
    return this
}

inline fun <T : Any> GraphQLResult<T>.onLoading(action: () -> Unit): GraphQLResult<T> {
    if (this is GraphQLResult.Loading) action()
    return this
}