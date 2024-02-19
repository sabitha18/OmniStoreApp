package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.match_with

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.local.model.RecentProduct
import com.armada.storeapp.data.local.model.ShoppingCart
import com.armada.storeapp.data.local.model.Wishlist
import com.armada.storeapp.data.model.request.AddToCartRequest
import com.armada.storeapp.data.model.request.ConfigRequestModel
import com.armada.storeapp.data.model.request.RivaRegisterUserRequest
import com.armada.storeapp.data.model.request.WishlistRequest
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.data.repositories.OmniRepository
import com.armada.storeapp.data.repositories.RivaRepository
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchWithViewModel @Inject constructor
    (
    private val rivaRepository: RivaRepository,
    private val omniRepository: OmniRepository,
    application: Application
) : AndroidViewModel(application) {


    private val productDetailsResponse: MutableLiveData<Resource<ProductDetailsResponse>> =
        MutableLiveData()
    val responseProductDetails: LiveData<Resource<ProductDetailsResponse>> =
        productDetailsResponse

    private val changeConfigResponse: MutableLiveData<Resource<ConfigResponseModel>> =
        MutableLiveData()
    val responseChangeConfig: LiveData<Resource<ConfigResponseModel>> =
        changeConfigResponse

    private val addToCartResponse: MutableLiveData<Resource<AddToCartResponse>> =
        MutableLiveData()
    val responseAddToCart: LiveData<Resource<AddToCartResponse>> =
        addToCartResponse

    private val matchproductDetailsResponse: MutableLiveData<Resource<ProductDetailsResponse>> =
        MutableLiveData()
    val responseMAtchProductDetails: LiveData<Resource<ProductDetailsResponse>> =
        matchproductDetailsResponse

    private val dataCartCount: MutableLiveData<Int> = MutableLiveData()
    val cartCount: LiveData<Int> = dataCartCount

    //omni
    private val omniScanItemResponse: MutableLiveData<Resource<ScannedItemDetailsResponse>> =
        MutableLiveData()
    val responseScanItemOmni: LiveData<Resource<ScannedItemDetailsResponse>> =
        omniScanItemResponse


    fun omniScanItem(skuCode: String, storeId: String, priceListId: String) {
        omniScanItemResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.getScannedItemDetails(skuCode, storeId, priceListId)
                .collect { values ->
                    omniScanItemResponse.postValue(values)
                }
        }
    }


    // Get from db

    fun addOmniProduct(product: SkuMasterTypes) {
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.addOmniProduct(product)
        }
    }


    fun getProductDetails(language:String,currency:String,productId: String) {
        productDetailsResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.getProductDetails(language,currency,productId)
                .collect { values ->
                    productDetailsResponse.postValue(values)
                }
        }
    }

    fun getMatchProductDetails(language: String,currency: String,productId: String) {
        matchproductDetailsResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.getProductDetails(language,currency,productId)
                .collect { values ->
                    matchproductDetailsResponse.postValue(values)
                }
        }
    }

    fun changeConfig(language:String,
        configRequestModel: ConfigRequestModel
    ) {
        changeConfigResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.changeConfig(language,configRequestModel)
                .collect { values ->
                    changeConfigResponse.postValue(values)
                }
        }
    }

    fun addToCart(language:String,currency:String,
        addToCartRequest: AddToCartRequest
    ){
        addToCartResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.addToCart(language,currency,addToCartRequest)
                .collect { values ->
                    addToCartResponse.postValue(values)
                }
        }
    }


    fun getAllOmniProduct(context: Context): ArrayList<SkuMasterTypes> {
        var itemList: java.util.ArrayList<SkuMasterTypes>? = null
        val itemstring =
            SharedpreferenceHandler(context).getData(SharedpreferenceHandler.CART_ITEMS, "")
        if (itemstring.isNullOrEmpty() || itemstring.equals("null"))
            itemList = ArrayList()
        else {
            val gson = Gson()
            val type = object : TypeToken<java.util.ArrayList<SkuMasterTypes>>() {

            }.type
            itemList =
                gson.fromJson<java.util.ArrayList<SkuMasterTypes>>(itemstring, type)
        }
        return itemList!!
    }

    fun addOmniProductToPrefs(skuMasterTypes: SkuMasterTypes, context: Context) {
        val itemList = getAllOmniProduct(context)
        itemList.add(skuMasterTypes)
        val gson = Gson()
        val itemString = gson.toJson(itemList)
        SharedpreferenceHandler(context).saveData(SharedpreferenceHandler.CART_ITEMS, itemString)
    }


    // Getting from Room Db

    fun getCartCount() {
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.getCartCount()
                .collect { values ->
                    dataCartCount.postValue(values)
                }
        }
    }

}
