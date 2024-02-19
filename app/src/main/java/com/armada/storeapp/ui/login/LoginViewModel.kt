package com.armada.storeapp.ui.login

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.data.repositories.LoginRepository
import com.armada.storeapp.data.repositories.WarehouseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor
    (
    private val loginRepository: LoginRepository,
    private val warehouseRepository: WarehouseRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    val loginResponse: MutableLiveData<Resource<AuthorizeResponseModel>> =
        MutableLiveData()
    val userDetailsResponse: MutableLiveData<Resource<PosUserInfo>> = MutableLiveData()

    val wmsLoginResponse: MutableLiveData<Resource<WarehouseLoginResponse>> =
        MutableLiveData()

    val token_response: MutableLiveData<Resource<GenerateTokenResponse>> =
        MutableLiveData()

    fun wmsUserLogin(userId: String, password: String, token: String) {
        wmsLoginResponse.postValue(Resource.Loading())
        viewModelScope.launch {
            warehouseRepository.userLogin(userId, password, token).collect { values ->
                wmsLoginResponse.value = values
            }
        }
    }

    fun generateToken(jsonParams: Map<String, String>) {
        token_response.postValue(Resource.Loading())
        viewModelScope.launch {
            warehouseRepository.generateToken(jsonParams).collect { values ->
                token_response.value = values
            }
        }
    }


    fun login(username: String, password: String) {

        loginResponse.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.userLogin(username, password).collect { values ->
                loginResponse.postValue(values)
                println("values ----     "+values)
            }

        }


    }

    fun getUserDetails(accessToken: String) {
        userDetailsResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.getUserDetails(accessToken).collect { values ->
                userDetailsResponse.postValue(values)

            }
        }
    }


    fun loginDataChanged(username: String, password: String) {
//        if (!isUserNameValid(username)) {
//            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
//        } else
        if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 3
    }
}