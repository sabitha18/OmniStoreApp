package com.armada.storeapp.data.repositories

import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.AuthorizeResponseModel
import com.armada.storeapp.data.model.response.BaseApiResponse
import com.armada.storeapp.data.model.response.PosUserDetailsResponse
import com.armada.storeapp.data.model.response.PosUserInfo
import com.armada.storeapp.data.remote.LoginDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class LoginRepository @Inject constructor(
    private val loginDataSource: LoginDataSource
) : BaseApiResponse() {

    fun userLogin(
        username: String, password: String
    ): Flow<Resource<AuthorizeResponseModel>> {
        return flow<Resource<AuthorizeResponseModel>> {
            emit(safeApiCall { loginDataSource.userLogin(username, password) })
        }.flowOn(Dispatchers.IO)
    }

    fun getUserDetails(accessToken: String)
            : Flow<Resource<PosUserInfo>> {
        return flow {
            emit(safeApiCall { loginDataSource.getUserDetails(accessToken) })
        }
    }
}