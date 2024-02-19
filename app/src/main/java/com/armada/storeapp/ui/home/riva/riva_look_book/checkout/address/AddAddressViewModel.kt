package com.armada.storeapp.ui.home.riva.riva_look_book.checkout.address

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.AddAddressRequestModel
import com.armada.storeapp.data.model.request.RivaLoginRequest
import com.armada.storeapp.data.model.response.AddAddressResponseModel
import com.armada.storeapp.data.model.response.CountryListResponseModel
import com.armada.storeapp.data.model.response.RivaRegisterUserResponse
import com.armada.storeapp.data.repositories.RivaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddAddressViewModel @Inject constructor
    (
    private val rivaRepository: RivaRepository,
    application: Application
) : AndroidViewModel(application) {

    private val addAddressResponse: MutableLiveData<Resource<AddAddressResponseModel>> =
        MutableLiveData()
    val responseAddAddress: LiveData<Resource<AddAddressResponseModel>> =
        addAddressResponse

    private val editAddressResponse: MutableLiveData<Resource<AddAddressResponseModel>> =
        MutableLiveData()
    val responseEditAddress: LiveData<Resource<AddAddressResponseModel>> =
        editAddressResponse

    private val countryListResponse: MutableLiveData<Resource<CountryListResponseModel>> =
        MutableLiveData()
    val responseCoutryList: LiveData<Resource<CountryListResponseModel>> =
        countryListResponse

    fun addAddress(language:String,
        addAddressRequestModel: AddAddressRequestModel
    ) {
        addAddressResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.addAddress(language,addAddressRequestModel)
                .collect { values ->
                    addAddressResponse.postValue(values)
                }
        }
    }

    fun editAddress(language:String,
        addressId: String,
        addAddressRequestModel: AddAddressRequestModel
    ) {
        editAddressResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.editAddress(language,addressId, addAddressRequestModel)
                .collect { values ->
                    editAddressResponse.postValue(values)
                }
        }
    }

    fun getCountryList(language:String
    ){
        countryListResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.getCountryList(language)
                .collect { values ->
                    countryListResponse.postValue(values)
                }
        }
    }
}