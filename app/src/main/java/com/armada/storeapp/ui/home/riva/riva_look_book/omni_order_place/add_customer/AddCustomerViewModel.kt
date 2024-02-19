package com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.add_customer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.CreateOmniCustomerRequest
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.data.repositories.LoginRepository
import com.armada.storeapp.data.repositories.OmniRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCustomerViewModel @Inject constructor
    (
    private val loginRepository: LoginRepository,
    private val omniRepository: OmniRepository,
    application: Application
) : AndroidViewModel(application) {


    private val getCustomerCodeResponse: MutableLiveData<Resource<GetCustomerCodeResponse>> =
        MutableLiveData()
    val responseCustomerCode: LiveData<Resource<GetCustomerCodeResponse>> =
        getCustomerCodeResponse

    private val createCustomerResponse: MutableLiveData<Resource<CreateOmniCustomerResponse>> =
        MutableLiveData()
    val responseCreateCustomer: LiveData<Resource<CreateOmniCustomerResponse>> =
        createCustomerResponse

    private val editCustomerResponse: MutableLiveData<Resource<CreateOmniCustomerResponse>> =
        MutableLiveData()
    val responseEditCustomer: LiveData<Resource<CreateOmniCustomerResponse>> =
        editCustomerResponse

    private val searchCustomerByIdResponse: MutableLiveData<Resource<SearchCustomerResponse>> =
        MutableLiveData()
    val responseSearchCustomerById: LiveData<Resource<SearchCustomerResponse>> =
        searchCustomerByIdResponse

    private val searchCustomerByString: MutableLiveData<Resource<SearchCustomerResponse>> =
        MutableLiveData()
    val responseSearchCustomerByString: LiveData<Resource<SearchCustomerResponse>> =
        searchCustomerByString

    private val searchCustomerByEmail: MutableLiveData<Resource<SearchCustomerByMailResponse>> =
        MutableLiveData()
    val responseSearchCustomerByEmail: LiveData<Resource<SearchCustomerByMailResponse>> =
        searchCustomerByEmail

    private val getCountryResponse: MutableLiveData<Resource<GetCountryResponse>> =
        MutableLiveData()
    val responseGetcountry: LiveData<Resource<GetCountryResponse>> =
        getCountryResponse

    private val getStateResponse: MutableLiveData<Resource<GetStateResponse>> =
        MutableLiveData()
    val responseGetState: LiveData<Resource<GetStateResponse>> =
        getStateResponse

    private val getCityResponse: MutableLiveData<Resource<GetCityResponse>> =
        MutableLiveData()
    val responseGetCity: LiveData<Resource<GetCityResponse>> =
        getCityResponse

    var selectedCustomer: CustomerMasterData? = null

    val loginResponse: MutableLiveData<Resource<AuthorizeResponseModel>> =
        MutableLiveData()
    val responseLogin: LiveData<Resource<AuthorizeResponseModel>> =
        loginResponse

    fun updateToken(username: String, password: String) {
        loginResponse.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.userLogin(username, password).collect { values ->
                loginResponse.postValue(values)
            }

        }


    }

    fun createOmniCustomer(
        token: String,
        createOmniCustomerRequest: CreateOmniCustomerRequest
    ) {
        createCustomerResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.createOmniCustomer(token, createOmniCustomerRequest)
                .collect { values ->
                    createCustomerResponse.postValue(values)
                }
        }
    }

    fun editCustomer(
        token: String,
        omniCustomerRequest: CreateOmniCustomerRequest
    ) {
        editCustomerResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.editOmniCustomer(token, omniCustomerRequest)
                .collect { values ->
                    editCustomerResponse.postValue(values)
                }
        }
    }

    fun getCustomerCode(
        sessionToken: String,
        storeId: String,
        documentTypeId: String,
        businessDate: String
    ) {
        getCustomerCodeResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.getCustomerCode(sessionToken, storeId, documentTypeId, businessDate)
                .collect { values ->
                    getCustomerCodeResponse.postValue(values)
                }
        }
    }


    fun searchCustomerById(
        token: String,
        customerId: String
    ) {
        searchCustomerByIdResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.searchCustomerById(token, customerId)
                .collect { values ->
                    searchCustomerByIdResponse.postValue(values)
                }
        }
    }

    fun searchCustomerByString(
        token: String,
        searchString: String
    ) {
        searchCustomerByString.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.searchCustomerByString(token, searchString)
                .collect { values ->
                    searchCustomerByString.postValue(values)
                }
        }
    }

    fun searchCustomerByEmail(
        token: String,
        email: String
    ) {
        searchCustomerByEmail.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.searchCustomerByEmail(token, email)
                .collect { values ->
                    searchCustomerByEmail.postValue(values)
                }
        }
    }


    fun getCountryList(
    ) {
        getCountryResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.getCountryList()
                .collect { values ->
                    getCountryResponse.postValue(values)
                }
        }
    }


    fun getStateList(
        countryId: String
    ) {
        getStateResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.getStateList(countryId)
                .collect { values ->
                    getStateResponse.postValue(values)
                }
        }
    }


    fun getCityList(
        stateId: String
    ) {
        getCityResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.getCityList(stateId)
                .collect { values ->
                    getCityResponse.postValue(values)
                }
        }
    }

}