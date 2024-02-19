package com.armada.storeapp.data.repositories

import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.AddStockReceiptRequest
import com.armada.storeapp.data.model.request.AddStockReturnRequest
import com.armada.storeapp.data.model.request.CompletePickRequest
import com.armada.storeapp.data.model.request.StockAdjustmentAddRequest
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.data.remote.WarehouseRemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class WarehouseRepository @Inject constructor(
    private val warehouseRemoteDataSource: WarehouseRemoteDataSource
) : BaseApiResponse() {

    fun userLogin(
        userId: String,
        password: String,
        token: String
    ): Flow<Resource<WarehouseLoginResponse>> {
        return flow {
            emit(safeApiCall { warehouseRemoteDataSource.userLogin(userId, password, token) })
        }.flowOn(Dispatchers.IO)
    }

    fun generateToken(map: Map<String, String>): Flow<Resource<GenerateTokenResponse>> {
        return flow<Resource<GenerateTokenResponse>> {
            emit(safeApiCall { warehouseRemoteDataSource.generateToken(map) })
        }.flowOn(Dispatchers.IO)
    }


    fun getPendingOrdersList(
        shopLocationCode: String,
        token: String
    ): Flow<Resource<ShopPickOrdersResponseModel>> {
        return flow {
            emit(safeApiCall {
                warehouseRemoteDataSource.getPendingOrdersList(
                    shopLocationCode,
                    token
                )
            })
        }
    }

    fun getPendingOrdersListByDocNoAndDate(
        shopLocationCode: String,
        documentNumber: String,
        fromDate: String,
        toDate: String,
        token: String
    ): Flow<Resource<ShopPickOrdersResponseModel>> {
        return flow {
            emit(safeApiCall {
                warehouseRemoteDataSource.getPendingOrdersListByDocumentNumberAndDate(
                    shopLocationCode,
                    documentNumber,
                    fromDate, toDate, token
                )
            })
        }
    }

    fun getReasons(token: String): Flow<Resource<ShopPickReasonResponseModel>> {
        return flow {
            emit(safeApiCall { warehouseRemoteDataSource.getReasons(token) })
        }
    }

    fun openProcess(
        shopLocationCode: String,
        documentNumber: String,
        toLocation: String,
        shortName: String,
        token: String
    ): Flow<Resource<OpenDocumentResponseModel>> {
        return flow {
            emit(safeApiCall {
                warehouseRemoteDataSource.openProcess(
                    shopLocationCode,
                    documentNumber,
                    toLocation,
                    shortName,
                    token
                )
            })
        }
    }

    fun scanItem(barcode: String, token: String): Flow<Resource<ScanItemResponseModel>> {
        return flow {
            emit(safeApiCall { warehouseRemoteDataSource.scanItem(barcode, token) })
        }
    }

    fun completePick(
        completePickRequest: CompletePickRequest,
        token: String
    ): Flow<Resource<CompletePickResponseModel>> {
        return flow {
            emit(safeApiCall { warehouseRemoteDataSource.completePick(completePickRequest, token) })
        }
    }

    fun getFromLocationList(
        userCode: String,
        countryCode: String,
        sessionToken: String
    ): Flow<Resource<FromLocationListResponse>> {
        return flow {
            emit(safeApiCall {
                warehouseRemoteDataSource.getFromLocationList(
                    userCode,
                    countryCode,
                    sessionToken
                )
            })
        }
    }


    fun getStockReceiptDocumentList(
        locationCode: String,
        userCode: String,
        docDate: String,
        fromLocation: String,
        docNumber: String,
        sessionToken: String
    ): Flow<Resource<StockReceiptDocumentResponseModel>> {
        return flow {
            emit(safeApiCall {
                warehouseRemoteDataSource.getStockReceiptDocumentList(
                    locationCode,
                    userCode,
                    docDate,
                    fromLocation,
                    docNumber,
                    sessionToken
                )
            })
        }
    }


    fun getTransferTypes(
        sessionToken: String
    ): Flow<Resource<TransferTypeResponseModel>> {
        return flow {
            emit(safeApiCall { warehouseRemoteDataSource.getTransferTypes(sessionToken) })
        }
    }


    fun openStockReceiptDocument(
        id: String,
        transcationNo: String,
        remarks: String,
        userCode: String,
        fromLocation: String,
        toLocation: String,
        sessionToken: String
    ): Flow<Resource<OpenStockReceiptDocumentResponse>> {
        return flow {
            emit(safeApiCall {
                warehouseRemoteDataSource.openStockReceiptDocument(
                    id,
                    transcationNo,
                    remarks,
                    userCode,
                    fromLocation,
                    toLocation,
                    sessionToken
                )
            })
        }
    }

    fun addStockReceiptDocument(
        transcationNo: String,
        remarks: String,
        userCode: String,
        fromLocation: String,
        toLocation: String,
        strRemarks: String,
        totalQty: String,
        totalReqQty: String,
        intransitEnabledLoc: String,
        Flocation: String,
        crossdockInType: String,
        disDate: String,
        sessionToken: String, addStockReceiptRequest: AddStockReceiptRequest
    ): Flow<Resource<AddStockReceiptResponse>> {
        return flow {
            emit(safeApiCall {
                warehouseRemoteDataSource.addStockReceiptDocument(
                    transcationNo,
                    remarks,
                    userCode,
                    fromLocation,
                    toLocation,
                    strRemarks,
                    totalQty,
                    totalReqQty,
                    intransitEnabledLoc,
                    Flocation,
                    crossdockInType,
                    disDate,
                    sessionToken, addStockReceiptRequest
                )
            })
        }
    }

    /* Stock Return Apis */

    fun getToLocationList(
        userCode: String,
        countryCode: String,
        sessionToken: String
    ): Flow<Resource<ToLocationListResponse>> {
        return flow {
            emit(safeApiCall {
                warehouseRemoteDataSource.getToLocationList(userCode, countryCode, sessionToken)
            })
        }
    }


    fun getPriorityList(
        userCode: String,
        countryCode: String,
        sessionToken: String
    ): Flow<Resource<PriorityListResponse>> {
        return flow {
            emit(safeApiCall {
                warehouseRemoteDataSource.getPriorityList(userCode, countryCode, sessionToken)
            })
        }
    }


    fun getTransferList(
        userCode: String,
        countryCode: String,
        sessionToken: String
    ): Flow<Resource<TransferTypeResponseModel>> {
        return flow {
            emit(safeApiCall {
                warehouseRemoteDataSource.getTransferList(userCode, countryCode, sessionToken)
            })
        }
    }


    fun scanStockReturnItem(
        userCode: String,
        locationCode: String,
        barcode: String,
        returnType: String,
        sessionToken: String
    ): Flow<Resource<StockReturnItemScanResponse>> {
        return flow {
            emit(safeApiCall {
                warehouseRemoteDataSource.scanStockReturnItem(
                    userCode,
                    locationCode,
                    barcode,
                    returnType,
                    sessionToken
                )
            })
        }
    }


    fun getDefaultToLocation(
        userCode: String,
        sessionToken: String
    ): Flow<Resource<DefaultToLocationResponse>> {
        return flow {
            emit(safeApiCall {
                warehouseRemoteDataSource.getDefaultToLocation(userCode, sessionToken)
            })
        }
    }


    fun addStockReturn(
        userCode: String,
        fromLocation: String,
        toLocation: String,
        totalQty: String,
        priority: String,
        typeOfTransfer: String,
        userRemarks: String,
        sessionToken: String,
        addStockReturnRequest: AddStockReturnRequest
    ): Flow<Resource<AddStockReturnResponse>> {
        return flow {
            emit(safeApiCall {
                warehouseRemoteDataSource.addStockReturn(
                    userCode,
                    fromLocation,
                    toLocation,
                    totalQty,
                    priority,
                    typeOfTransfer,
                    userRemarks,
                    sessionToken,
                    addStockReturnRequest
                )
            })
        }
    }

    /* Stock Adjustment Apis */

    fun scanModelStockAdjustment(
        userCode: String,
        locationCode: String,
        modelCode: String,
        sessionToken: String
    ): Flow<Resource<StockAdjustmentScanModelResponse>> {
        return flow {
            emit(safeApiCall {
                warehouseRemoteDataSource.scanModelStockAdjustment(
                    userCode,
                    locationCode,
                    modelCode,
                    sessionToken
                )
            })
        }
    }


    fun addStockAdjustment(
        userCode: String,
        locationCode: String,
        modelCode: String,
        totalQty: String,
        totalCount: String,
        totalDiffQty: String,
        sessionToken: String,
        addStockAdjustmentAddRequest: StockAdjustmentAddRequest
    ): Flow<Resource<AddStockAdjustmentResponse>> {
        return flow {
            emit(safeApiCall {
                warehouseRemoteDataSource.addStockAdjustment(
                    userCode,
                    locationCode,
                    modelCode,
                    totalQty,
                    totalCount,
                    totalDiffQty,
                    sessionToken,
                    addStockAdjustmentAddRequest
                )
            })
        }
    }

    suspend fun getPickItemImage(
        departmentCode: String,
        productcode: String,
        sessionToken: String
    ): Flow<Resource<GetImageResponse>> {
        return flow {
            emit(safeApiCall {
                warehouseRemoteDataSource.getPickItemImage(
                    departmentCode,
                    productcode,
                    sessionToken
                )
            })
        }
    }

}