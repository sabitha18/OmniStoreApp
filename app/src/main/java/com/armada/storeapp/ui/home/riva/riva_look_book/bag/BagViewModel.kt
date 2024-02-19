package com.armada.storeapp.ui.home.riva.riva_look_book.bag

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.*
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.data.repositories.OmniRepository
import com.armada.storeapp.data.repositories.RivaRepository
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.select_store.adapter.SkuStockAdapter
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BagViewModel @Inject constructor
    (
    private val rivaRepository: RivaRepository,
    private val omniRepository: OmniRepository,
    application: Application
) : AndroidViewModel(application) {

    private val registerUserResponse: MutableLiveData<Resource<RivaRegisterUserResponse>> =
        MutableLiveData()
    val reponseRegisterUser: LiveData<Resource<RivaRegisterUserResponse>> =
        registerUserResponse

    private val cartItemsResponse: MutableLiveData<Resource<CartItemsResponse>> =
        MutableLiveData()
    val responseCartItems: LiveData<Resource<CartItemsResponse>> =
        cartItemsResponse

    private val checkStockResponse: MutableLiveData<Resource<CheckStockResponseModel>> =
        MutableLiveData()
    val responseCheckStock: LiveData<Resource<CheckStockResponseModel>> =
        checkStockResponse

    private val changeCartItemsResponse: MutableLiveData<Resource<CartItemsResponse>> =
        MutableLiveData()
    val responseChangeCartItem: LiveData<Resource<CartItemsResponse>> =
        changeCartItemsResponse

    private val updateCartItemsResponse: MutableLiveData<Resource<CartItemsResponse>> =
        MutableLiveData()
    val responseUpdateCart: LiveData<Resource<CartItemsResponse>> =
        updateCartItemsResponse

    private val changeConfigResponse: MutableLiveData<Resource<ConfigResponseModel>> =
        MutableLiveData()
    val responseChangeConfig: LiveData<Resource<ConfigResponseModel>> =
        changeConfigResponse

    private val deleteCartItemResponse: MutableLiveData<Resource<AddToCartResponse>> =
        MutableLiveData()
    val responseDeleteCartItem: LiveData<Resource<AddToCartResponse>> =
        deleteCartItemResponse

    private val omniStockResponse: MutableLiveData<Resource<OmniStockResponse>> =
        MutableLiveData()
    val responseOmniStock: LiveData<Resource<OmniStockResponse>> =
        omniStockResponse

    private val omniScanItemResponse: MutableLiveData<Resource<ScannedItemDetailsResponse>> =
        MutableLiveData()
    val responseScanItemOmni: MutableLiveData<Resource<ScannedItemDetailsResponse>> =
        omniScanItemResponse

    private val searchModelResponse: MutableLiveData<Resource<SearchModelResponse>> =
        MutableLiveData()
    val responseSearchModel: LiveData<Resource<SearchModelResponse>> =
        searchModelResponse

    fun registerRivaUser(
        language: String, currency: String,
        rivaRegisterUserRequest: RivaRegisterUserRequest
    ) {
        registerUserResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.registerRivaUser(language, currency, rivaRegisterUserRequest)
                .collect { values ->
                    registerUserResponse.postValue(values)
                }
        }
    }


    fun getCartItems(language: String, currency: String, customerId: String) {
        cartItemsResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.getCartItems(language, currency, customerId)
                .collect { values ->
                    cartItemsResponse.postValue(values)
                }
        }
    }

    fun checkItemStock(
        language: String, currency: String,
        stockRequestModel: StockRequestModel
    ) {
        checkStockResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.checkItemStock(language, currency, stockRequestModel)
                .collect { values ->
                    checkStockResponse.postValue(values)
                }
        }
    }

    fun changeCartItem(
        language: String, currency: String,
        updateSizeRequest: UpdateSizeRequest
    ) {
        changeCartItemsResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.changeCartItem(language, currency, updateSizeRequest)
                .collect { values ->
                    changeCartItemsResponse.postValue(values)
                }
        }
    }


    fun updateCart(
        language: String, currency: String,
        updateCartRequest: UpdateCartRequest
    ) {
        updateCartItemsResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.updateCart(language, currency, updateCartRequest)
                .collect { values ->
                    updateCartItemsResponse.postValue(values)
                }
        }
    }

    fun deleteCartItem(
        language: String, currency: String,
        deleteCartRequestModel: DeleteCartRequestModel
    ) {
        deleteCartItemResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.deleteCartItem(language, currency, deleteCartRequestModel)
                .collect { values ->
                    deleteCartItemResponse.postValue(values)
                }
        }
    }

    fun changeConfig(
        language: String,
        configRequestModel: ConfigRequestModel
    ) {
        changeConfigResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.changeConfig(language, configRequestModel)
                .collect { values ->
                    changeConfigResponse.postValue(values)
                }
        }
    }

//    fun getAllOmniProduct(): LiveData<List<SkuMasterTypes>> {
//        return omniRepository.getAllOmniProduct()
//    }

    fun getAllOmniProduct(context: Context): ArrayList<SkuMasterTypes> {
        var itemList: java.util.ArrayList<SkuMasterTypes>? = null
        val itemstring =
            SharedpreferenceHandler(context).getData(SharedpreferenceHandler.CART_ITEMS, "")
        if (itemstring.isNullOrEmpty() || itemstring.equals("null"))
            itemList = ArrayList()
        else {
            val gson = Gson()
            val type = object : TypeToken<ArrayList<SkuMasterTypes>>() {

            }.type
            itemList =
                gson.fromJson<ArrayList<SkuMasterTypes>>(itemstring, type)
        }
        return itemList!!
    }

    fun addOmniProductToPrefs(skuMasterTypes: SkuMasterTypes, context: Context) {
        skuMasterTypes.quantity=1
        val itemList = getAllOmniProduct(context)
        if (itemList.size == 0)
            itemList.add(skuMasterTypes)
        else{
            var foundItem=0
            itemList.forEach {
                if (it.skuCode.equals(skuMasterTypes.skuCode)) {
                    it.quantity = it.quantity!! + skuMasterTypes.quantity!!
                    foundItem++
                }
            }
            if(foundItem==0)
                itemList.add(skuMasterTypes)
        }


        val gson = Gson()
        val itemString = gson.toJson(itemList)
        SharedpreferenceHandler(context).saveData(SharedpreferenceHandler.CART_ITEMS, itemString)
    }

    fun omniScanItem(skuCode: String, storeId: String, priceListId: String) {
        omniScanItemResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.getScannedItemDetails(skuCode, storeId, priceListId)
                .collect { values ->
                    omniScanItemResponse.postValue(values)
                }
        }
    }

    fun searchModel(
        model: String
    ) {
        searchModelResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.searchModel(model)
                .collect { values ->
                    searchModelResponse.postValue(values)
                }
        }
    }


    fun checkOmniStock(
        skuCode: String,
        countryId: String,
        storeCode: String,
        loggedStoreCode: String,
        countryCode: String
    ) {
        omniStockResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.getAllStockDetails(
                skuCode,
                countryId,
                storeCode,
                loggedStoreCode,
                countryCode
            )
                .collect { values ->
                    omniStockResponse.postValue(values)
                }
        }
    }

    fun addOmniProduct(product: SkuMasterTypes) {
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.addOmniProduct(product)
        }
    }

    fun removeOmniProduct(skuCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.removeOmniProduct(skuCode)
        }
    }

    fun updateOmniProductQuantity(skuCode: String, newQty: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.updateOmniProductQuantity(skuCode, newQty)
        }
    }

    fun deleteAllProductsFromBag() {
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.deleteAllProductsFromBag()
        }
    }


}