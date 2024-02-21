package com.armada.storeapp.data.remote

import com.armada.storeapp.data.ApiService
import com.armada.storeapp.data.model.request.CommonBinTransferRequest
import com.armada.storeapp.data.model.request.ManualPicklistRequest
import com.armada.storeapp.data.model.request.PicklistTransferRequest
import com.armada.storeapp.data.model.request.SkipPicklistTransferRequest
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

class InStoreDataSource @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getPicklist(
        isStatus: String, storeCode: String, searchString: String
    ) = apiService.getPickList(isStatus, storeCode, searchString)

    suspend fun getPicklistByPages(
        isStatus: String,
        storeCode: String,
        searchString: String,
        itemCount: String,
        pageNo: String,
        type: String,
    ) = apiService.getManualPicklistByPages(isStatus, storeCode, searchString,itemCount,pageNo,type)

    suspend fun getPickListDetails(
        isStatus: String,
        storeCode: String,
        picklistHeaderId: String
    ) = apiService.getPickListDetails(isStatus, storeCode, picklistHeaderId)

    suspend fun createManualPicklist(manualPicklistRequest: ManualPicklistRequest) =
        apiService.createManualPicklist(manualPicklistRequest)

    suspend fun pickListBintransfer(
        picklistTransferRequest: PicklistTransferRequest
    ) = apiService.picklistBinTransfer(picklistTransferRequest)


    suspend fun getCreatePicklistSkus(
        storeCode: String,
        styleCode: String,
    ) = apiService.getCreatePicklistSkus(storeCode, styleCode)

    suspend fun getDestinationBinList(
        storeId: String
    ) = apiService.getDestinationBinList(storeId)

    suspend fun binTransferSkippedItems(
        picklistTransferRequest: PicklistTransferRequest
    ) = apiService.binTransferSkippedItems(picklistTransferRequest)

    suspend fun getSkippedPicklist(
        storeCode: String,
    ) = apiService.getSkippedPicklist(storeCode)

    suspend fun scanBinPicklist(
        storeCode: String,
        bincode: String
    ) = apiService.validateBin(storeCode, bincode)

    suspend fun checkIteminBin(
        storeCode: String,
        bincode: String,
        itemCode: String
    ) = apiService.checkIteminBin(storeCode, bincode, itemCode)

    suspend fun getSkipItemReasonList() = apiService.getSkipItemReasonList()

    suspend fun skipPicklistTransfer(skipPicklistTransferRequest: SkipPicklistTransferRequest) =
        apiService.skipPicklistTransfer(skipPicklistTransferRequest)

    suspend fun commonBinTransfer(
        commonBinTransferRequest: CommonBinTransferRequest
    ) = apiService.commonBinTransfer(commonBinTransferRequest)

    suspend fun checkbinInventory(
        storeCode: String,
        bincode: String,
    ) = apiService.checkbinInventory(storeCode, bincode)

    suspend fun itemOrBinSearch(
        limit: String, offset: String, isActive: String,
        itemCode: String,
        bincode: String,
        storeCode: String
    ) = apiService.itemOrBinSearch(limit, offset, isActive, itemCode, bincode, storeCode)
    suspend fun itemNotBinSearch(
        SearchValue: String,
        StoreID: String,
        fromFormName: String
    ) = apiService.itemNotBinSearch(SearchValue, StoreID, fromFormName)
    suspend fun checkSkuForAdjustment(
        storeCode: String,
        styleCode: String
    ) = apiService.checkSkuForAdjustment(storeCode, styleCode)

}