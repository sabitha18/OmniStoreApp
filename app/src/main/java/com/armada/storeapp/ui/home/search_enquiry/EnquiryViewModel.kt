package com.armada.storeapp.ui.home.search_enquiry

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
class EnquiryViewModel @Inject constructor
    (
    private val rivaRepository: RivaRepository,
    private val omniRepository: OmniRepository,
    application: Application
) : AndroidViewModel(application) {

    private val omniStockResponse: MutableLiveData<Resource<OmniStockResponse>> =
        MutableLiveData()
    val responseOmniStock: LiveData<Resource<OmniStockResponse>> =
        omniStockResponse

    private val omniScanItemResponse: MutableLiveData<Resource<ScannedItemDetailsResponse>> =
        MutableLiveData()
    val responseScanItemOmni: MutableLiveData<Resource<ScannedItemDetailsResponse>> =
        omniScanItemResponse

    private val skuItemResponse: MutableLiveData<Resource<ScannedItemDetailsResponse>> =
        MutableLiveData()
    val responseSkuItemOmni: MutableLiveData<Resource<ScannedItemDetailsResponse>> =
        skuItemResponse

    private val searchModelResponse: MutableLiveData<Resource<SearchModelResponse>> =
        MutableLiveData()
    val responseSearchModel: LiveData<Resource<SearchModelResponse>> =
        searchModelResponse


    fun getAllOmniProduct(context: Context): ArrayList<SkuMasterTypes> {
        var itemList: java.util.ArrayList<SkuMasterTypes>? = null
        val itemstring =
            SharedpreferenceHandler(context).getData(SharedpreferenceHandler.SEARCH_ITEMS, "")
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
//        if (itemList.size == 0)
            itemList.add(skuMasterTypes)
//        else{
//            var shouldAdd=false
//            itemList.forEach {
//                if (it.skuCode.equals(skuMasterTypes.skuCode)) {
//                    it.quantity = it.quantity!! + skuMasterTypes.quantity!!
//                } else {
//                    shouldAdd=true
//                }
//            }
//            if(shouldAdd)
//                itemList.add(skuMasterTypes)
//        }
        val gson = Gson()
        val itemString = gson.toJson(itemList)
        SharedpreferenceHandler(context).saveData(SharedpreferenceHandler.SEARCH_ITEMS, itemString)
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
    ){
        searchModelResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.searchModel(model)
                .collect { values ->
                    searchModelResponse.postValue(values)
                }
        }
    }

    fun searchModelStyle(
        model: String
    ){
        skuItemResponse.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            omniRepository.searchModelStyle(model)
                .collect { values ->
                    skuItemResponse.postValue(values)
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

}