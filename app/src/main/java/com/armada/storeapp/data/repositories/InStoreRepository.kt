package com.armada.storeapp.data.repositories

import android.util.Log
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.CommonBinTransferRequest
import com.armada.storeapp.data.model.request.ManualPicklistRequest
import com.armada.storeapp.data.model.request.PicklistTransferRequest
import com.armada.storeapp.data.model.request.SkipPicklistTransferRequest
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.data.remote.InStoreDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


@ActivityRetainedScoped
class InStoreRepository @Inject constructor(
    private val picklistDataSource: InStoreDataSource
) : BaseApiResponse() {

    suspend fun getPickList(
        isStatus: String,
        storeCode: String,
        searchString: String
    ): Flow<Resource<PicklistResponseModel>> {
        return flow<Resource<PicklistResponseModel>> {
            emit(safeApiCall {
                picklistDataSource.getPicklist(
                    isStatus,
                    storeCode,
                    searchString
                )
            })
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getPicklistByPages(
        isStatus: String,
        storeCode: String,
        searchString: String,
        itemCount: String,
        pageNo: String,
        type: String,
    ) : Flow<Resource<PicklistResponseModel>> {
        return flow<Resource<PicklistResponseModel>> {
            emit(safeApiCall {
                picklistDataSource.getPicklistByPages(
                    isStatus,
                    storeCode,
                    searchString,
                    itemCount,
                    pageNo,
                    type
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPickListDetails(
        isStatus: String,
        storeCode: String,
        picklistHeaderId: String
    ): Flow<Resource<PicklistDetailsResponseModel>> {
        return flow<Resource<PicklistDetailsResponseModel>> {
            emit(safeApiCall {
                picklistDataSource.getPickListDetails(isStatus, storeCode, picklistHeaderId)
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun createManualPicklist(manualPicklistRequest: ManualPicklistRequest): Flow<Resource<ManualPicklistResponse>> {
        return flow<Resource<ManualPicklistResponse>> {
            emit(safeApiCall {
                picklistDataSource.createManualPicklist(manualPicklistRequest)
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCreatePicklistSkus(
        storeCode: String,
        styleCode: String,
    ): Flow<Resource<CreatePicklistSkuResponse>> {
        return flow<Resource<CreatePicklistSkuResponse>> {
            emit(safeApiCall {
                picklistDataSource.getCreatePicklistSkus(storeCode, styleCode)
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getDestinationBinList(
        storeId: String
    ): Flow<Resource<GetDestinationBinResponse>> {
        return flow<Resource<GetDestinationBinResponse>> {
            emit(safeApiCall {
                picklistDataSource.getDestinationBinList(storeId)
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getSkippedPicklist(
        storeCode: String,
    ): Flow<Resource<PicklistResponseModel>> {
        return flow<Resource<PicklistResponseModel>> {
            emit(safeApiCall {
                picklistDataSource.getSkippedPicklist(storeCode)
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun validateBin(
        storeCode: String,
        bincode: String
    ): Flow<Resource<ValidateBinResponse>> {
        return flow<Resource<ValidateBinResponse>> {
            emit(safeApiCall {
                picklistDataSource.scanBinPicklist(
                    storeCode, bincode
                )
            })
        }.flowOn(Dispatchers.IO)
    }


    suspend fun picklistBintransfer(
        picklistTransferRequest: PicklistTransferRequest

    ): Flow<Resource<PicklistBintransferResponseModel>> {
        return flow<Resource<PicklistBintransferResponseModel>> {
            emit(safeApiCall {
                picklistDataSource.pickListBintransfer(
                    picklistTransferRequest
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun binTransferSkippedItems(
        picklistTransferRequest: PicklistTransferRequest
    ): Flow<Resource<PicklistBintransferResponseModel>> {
        return flow<Resource<PicklistBintransferResponseModel>> {
            emit(safeApiCall {
                picklistDataSource.binTransferSkippedItems(
                    picklistTransferRequest
                )
            })
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getSkipItemReasonList(): Flow<Resource<SkipReasonListResponse>> {
        return flow<Resource<SkipReasonListResponse>> {
            emit(safeApiCall {
                picklistDataSource.getSkipItemReasonList()
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun skipPicklistTransfer(skipPicklistTransferRequest: SkipPicklistTransferRequest)
            : Flow<Resource<SkipPicklistTransferResponse>> {
        return flow<Resource<SkipPicklistTransferResponse>> {
            emit(safeApiCall {
                picklistDataSource.skipPicklistTransfer(skipPicklistTransferRequest)
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun checkIteminBin(
        storeCode: String,
        bincode: String,
        itemCode: String
    ): Flow<Resource<ValidateBinResponse>> {
        return flow<Resource<ValidateBinResponse>> {
            emit(safeApiCall {
                picklistDataSource.checkIteminBin(
                    storeCode, bincode, itemCode
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun commonBinTransfer(
        commonBinTransferRequest: CommonBinTransferRequest
    ): Flow<Resource<CommonBintransferResponse>> {
        return flow<Resource<CommonBintransferResponse>> {
            emit(safeApiCall {
                picklistDataSource.commonBinTransfer(commonBinTransferRequest)
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun checkbinInventory(
        storeCode: String,
        bincode: String,
    ): Flow<Resource<CheckbinInventoryResponse>> {
        return flow<Resource<CheckbinInventoryResponse>> {
            emit(safeApiCall {
                picklistDataSource.checkbinInventory(storeCode, bincode)
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun itemOrBinSearch(
        limit: String, offset: String, isActive: String,
        itemCode: String,
        bincode: String,
        storeCode: String
    ): Flow<Resource<ItemBinSearchResponse>> {
        return flow<Resource<ItemBinSearchResponse>> {
            emit(safeApiCall {
                picklistDataSource.itemOrBinSearch(
                    limit,
                    offset,
                    isActive,
                    itemCode,
                    bincode,
                    storeCode
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun checkSkuForAdjustment(
        storeCode: String,
        styleCode: String
    ): Flow<Resource<CheckStockAdjustmentResponse>> {
        return flow<Resource<CheckStockAdjustmentResponse>> {
            emit(safeApiCall {
                picklistDataSource.checkSkuForAdjustment(storeCode, styleCode)
            })
        }.flowOn(Dispatchers.IO)
    }

}