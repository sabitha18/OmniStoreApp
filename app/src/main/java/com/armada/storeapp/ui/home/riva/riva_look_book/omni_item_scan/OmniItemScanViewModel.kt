package com.armada.storeapp.ui.home.riva.riva_look_book.omni_item_scan

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armada.storeapp.data.Resource
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
class OmniItemScanViewModel @Inject constructor
    (
    private val omniRepository: OmniRepository,
    application: Application
) : AndroidViewModel(application) {


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
        val itemList = getAllOmniProduct(context)
        itemList.add(skuMasterTypes)
        val gson = Gson()
        val itemString = gson.toJson(itemList)
        SharedpreferenceHandler(context).saveData(SharedpreferenceHandler.CART_ITEMS, itemString)
    }
}