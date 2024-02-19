package com.armada.storeapp.ui.home.riva.riva_look_book.riva_login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.RivaLoginRequest
import com.armada.storeapp.data.model.request.RivaRegisterUserRequest
import com.armada.storeapp.data.model.response.RivaRegisterUserResponse
import com.armada.storeapp.data.repositories.RivaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RivaLoginViewModel @Inject constructor
    (
    private val rivaRepository: RivaRepository,
    application: Application
) : AndroidViewModel(application) {

    private val loginUserRepsonse: MutableLiveData<Resource<RivaRegisterUserResponse>> =
        MutableLiveData()
    val responseLoginUser: LiveData<Resource<RivaRegisterUserResponse>> =
        loginUserRepsonse

    fun rivaUserLogin(language:String,currency:String,
        rivaLoginRequest: RivaLoginRequest
    ) {
        loginUserRepsonse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.rivaUserLogin(language,currency,rivaLoginRequest)
                .collect { values ->
                    loginUserRepsonse.postValue(values)
                }
        }
    }
}