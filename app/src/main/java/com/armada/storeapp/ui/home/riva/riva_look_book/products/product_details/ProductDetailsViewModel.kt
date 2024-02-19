package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details


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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor
    (
    private val rivaRepository: RivaRepository,
    private val omniRepository: OmniRepository,
    application: Application
) : AndroidViewModel(application) {

    private val registerUserResponse: MutableLiveData<Resource<RivaRegisterUserResponse>> =
        MutableLiveData()
    val reponseRegisterUser: LiveData<Resource<RivaRegisterUserResponse>> =
        registerUserResponse

    private val productDetailsResponse: MutableLiveData<Resource<ProductDetailsResponse>> =
        MutableLiveData()
    val responseProductDetails: LiveData<Resource<ProductDetailsResponse>> =
        productDetailsResponse

    private val matchproductDetailsResponse: MutableLiveData<Resource<ProductDetailsResponse>> =
        MutableLiveData()
    val responseMAtchProductDetails: LiveData<Resource<ProductDetailsResponse>> =
        matchproductDetailsResponse

    private val changeConfigResponse: MutableLiveData<Resource<ConfigResponseModel>> =
        MutableLiveData()
    val responseChangeConfig: LiveData<Resource<ConfigResponseModel>> =
        changeConfigResponse

    private val changeMatchWithConfigResponse: MutableLiveData<Resource<ConfigResponseModel>> =
        MutableLiveData()
    val responseChangeMatchWithConfig: LiveData<Resource<ConfigResponseModel>> =
        changeMatchWithConfigResponse

    private val addToWishlistResponse: MutableLiveData<Resource<WishlistResponse>> =
        MutableLiveData()
    val responseAddToWishlist: LiveData<Resource<WishlistResponse>> =
        addToWishlistResponse

    private val removeFromWishListResponse: MutableLiveData<Resource<WishlistResponse>> =
        MutableLiveData()
    val responseRemoveFromWishlist: LiveData<Resource<WishlistResponse>> =
        removeFromWishListResponse

    private val addToCartResponse: MutableLiveData<Resource<AddToCartResponse>> =
        MutableLiveData()
    val responseAddToCart: LiveData<Resource<AddToCartResponse>> =
        addToCartResponse

    private val dataCartCount: MutableLiveData<Int> = MutableLiveData()
    val cartCount: LiveData<Int> = dataCartCount

    //omni
    private val omniScanItemResponse: MutableLiveData<Resource<ScannedItemDetailsResponse>> =
        MutableLiveData()
    val responseScanItemOmni: MutableLiveData<Resource<ScannedItemDetailsResponse>> =
        omniScanItemResponse


    private val addToCartOmniScanItemResponse: MutableLiveData<Resource<ScannedItemDetailsResponse>> =
        MutableLiveData()
    val responseScanAddToCartItemOmni: MutableLiveData<Resource<ScannedItemDetailsResponse>> =
        addToCartOmniScanItemResponse

    private val omniStockResponse: MutableLiveData<Resource<OmniStockResponse>> =
        MutableLiveData()
    val responseOmniStock: LiveData<Resource<OmniStockResponse>> =
        omniStockResponse

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

    fun getProductDetails(language: String, currency: String, productId: String) {
        productDetailsResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.getProductDetails(language, currency, productId)
                .collect { values ->
                    productDetailsResponse.postValue(values)
                }
        }
    }

    fun getMatchProductDetails(language: String, currency: String, productId: String) {
        matchproductDetailsResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.getProductDetails(language, currency, productId)
                .collect { values ->
                    matchproductDetailsResponse.postValue(values)
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

    fun matchWithChangeConfig(
        language: String,
        configRequestModel: ConfigRequestModel
    ) {
        changeMatchWithConfigResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.changeConfig(language, configRequestModel)
                .collect { values ->
                    changeMatchWithConfigResponse.postValue(values)
                }
        }
    }

    fun addToCart(
        language: String, currency: String,
        addToCartRequest: AddToCartRequest
    ) {
        addToCartResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.addToCart(language, currency, addToCartRequest)
                .collect { values ->
                    addToCartResponse.postValue(values)
                }
        }
    }

    fun addToWishlist(addToWishList: WishlistRequest.AddToWishList) {
        addToWishlistResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.addToWishlist(addToWishList)
                .collect { values ->
                    addToWishlistResponse.postValue(values)
                }
        }
    }

    fun removeFromWishlist(removeFromWishList: WishlistRequest.RemoveFromWishList) {
        removeFromWishListResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.removeFromWishlist(removeFromWishList)
                .collect { values ->
                    removeFromWishListResponse.postValue(values)
                }
        }
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

    fun insertItemToCart(cartItem: ShoppingCart) {
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.insertItemToCart(cartItem)
        }
    }

    fun addProductToRecentDb(recentProduct: RecentProduct) {
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.addProductToRecentDb(recentProduct)
        }
    }

    fun getRecentProductsFromDb(): LiveData<List<RecentProduct>> {
        return rivaRepository.getRecentProductsFromDb()
    }

    fun getWishlistProductIdsFromDb(): LiveData<List<String>> {
        return rivaRepository.getWishlistProductIdsFromDb()
    }

    fun addProductToWishlistDb(wishlist: Wishlist) {
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.addProductToWishlistDb(wishlist)
        }
    }

    fun removeProductFromWishlistDb(entityId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.removeProductFromWishlistDb(entityId)
        }
    }

    fun deleteAllWishlistItemsDb() {
        viewModelScope.launch(Dispatchers.IO) {
            rivaRepository.deleteAllWishlistItemsDb()
        }
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

    fun omniScanAddToCartItem(skuCode: String, storeId: String, priceListId: String) {
        addToCartOmniScanItemResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.getScannedItemDetails(skuCode, storeId, priceListId)
                .collect { values ->
                    addToCartOmniScanItemResponse.postValue(values)
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

    // Get from db

    fun addOmniProduct(product: SkuMasterTypes) {
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.addOmniProduct(product)
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


    fun updateOmniProduct(skuCode: String, qty: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.updateOmniProduct(skuCode, qty)
        }
    }

    fun removeOmniProduct(skuCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.removeOmniProduct(skuCode)
        }
    }

}
