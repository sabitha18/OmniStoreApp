package com.armada.storeapp.data.remote

import com.armada.storeapp.data.WarehouseApiService
import com.armada.storeapp.data.model.request.AddStockReceiptRequest
import com.armada.storeapp.data.model.request.AddStockReturnRequest
import com.armada.storeapp.data.model.request.CompletePickRequest
import com.armada.storeapp.data.model.request.StockAdjustmentAddRequest
import javax.inject.Inject

class WarehouseRemoteDataSource @Inject constructor(
    private val warehouseApiService: WarehouseApiService
) {

    suspend fun generateToken(map: Map<String, String>) =
        warehouseApiService.tokenGeneration(map)

    suspend fun userLogin(userid: String, password: String, token: String) =
        warehouseApiService.userLogin(userid, password, token)

    suspend fun getPendingOrdersList(shopLocationCode: String, token: String) =
        warehouseApiService.pendingOrdersList(shopLocationCode, token)

    suspend fun getReasons(token: String) =
        warehouseApiService.getReasons(token)

    suspend fun getPendingOrdersListByDocumentNumberAndDate(
        shopLocationCode: String,
        documentNo: String,
        fromDate: String,
        toDate: String,
        token: String
    ) =
        warehouseApiService.pendingOrdersListByDocumentNumberAndDate(
            shopLocationCode,
            documentNo,
            fromDate,
            toDate,
            token
        )

    suspend fun openProcess(
        shopLocationCode: String,
        documentNumber: String,
        toLocation: String,
        shortName: String,
        token: String
    ) =
        warehouseApiService.openProcess(
            shopLocationCode,
            documentNumber,
            toLocation,
            shortName,
            token
        )

    suspend fun scanItem(barcode: String, token: String) =
        warehouseApiService.scanItem(barcode, token)

    suspend fun completePick(completePickRequest: CompletePickRequest, token: String) =
        warehouseApiService.completePick(completePickRequest, token)

    suspend fun getFromLocationList(
        userCode: String,
        countryCode: String,
        sessionToken: String
    ) = warehouseApiService.getFromLocationList(userCode, countryCode, sessionToken)


    suspend fun getStockReceiptDocumentList(
        locationCode: String,
        userCode: String,
        docDate: String,
        fromLocation: String,
        docNumber: String,
        sessionToken: String
    ) = warehouseApiService.getStockReceiptDocumentList(
        locationCode,
        userCode,
        docDate,
        fromLocation,
        docNumber,
        sessionToken
    )


    suspend fun getTransferTypes(
        sessionToken: String
    ) = warehouseApiService.getTransferTypes(sessionToken)


    suspend fun openStockReceiptDocument(
        id: String,
        transcationNo: String,
        remarks: String,
        userCode: String,
        fromLocation: String,
        toLocation: String,
        sessionToken: String
    ) = warehouseApiService.openStockReceiptDocument(
        id,
        transcationNo,
        remarks,
        userCode,
        fromLocation,
        toLocation,
        sessionToken
    )

    suspend fun addStockReceiptDocument(
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
    ) = warehouseApiService.addStockReceiptDocument(
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

    /* Stock Return Apis */

    suspend fun getToLocationList(
        userCode: String,
        countryCode: String,
        sessionToken: String
    ) = warehouseApiService.getToLocationList(userCode, countryCode, sessionToken)


    suspend fun getPriorityList(
        userCode: String,
        countryCode: String,
        sessionToken: String
    ) = warehouseApiService.getPriorityList(userCode, countryCode, sessionToken)


    suspend fun getTransferList(
        userCode: String,
        countryCode: String,
        sessionToken: String
    ) = warehouseApiService.getTransferList(userCode, countryCode, sessionToken)


    suspend fun scanStockReturnItem(
        userCode: String,
        locationCode: String,
        barcode: String,
        returnType: String,
        sessionToken: String
    ) = warehouseApiService.scanStockReturnItem(
        userCode,
        locationCode,
        barcode,
        returnType,
        sessionToken
    )


    suspend fun getDefaultToLocation(
        userCode: String,
        sessionToken: String
    ) = warehouseApiService.getDefaultToLocation(userCode, sessionToken)


    suspend fun addStockReturn(
        userCode: String,
        fromLocation: String,
        toLocation: String,
        totalQty: String,
        priority: String,
        typeOfTransfer: String,
        userRemarks: String,
        sessionToken: String,
        addStockReturnRequest: AddStockReturnRequest
    ) = warehouseApiService.addStockReturn(
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


    /* Stock Adjustment Apis */

    suspend fun scanModelStockAdjustment(
        userCode: String,
        locationCode: String,
        modelCode: String,
        sessionToken: String
    ) = warehouseApiService.scanModelStockAdjustment(
        userCode,
        locationCode,
        modelCode,
        sessionToken
    )


    suspend fun addStockAdjustment(
        userCode: String,
        locationCode: String,
        modelCode: String,
        totalQty: String,
        totalCount: String,
        totalDiffQty: String,
        sessionToken: String,
        addStockAdjustmentAddRequest: StockAdjustmentAddRequest
    ) = warehouseApiService.addStockAdjustment(
        userCode,
        locationCode,
        modelCode,
        totalQty,
        totalCount,
        totalDiffQty,
        sessionToken,
        addStockAdjustmentAddRequest
    )

    suspend fun getPickItemImage(
        departmentCode: String,
        productcode: String,
        sessionToken: String
    ) = warehouseApiService.getPickItemImage(departmentCode, productcode, sessionToken)
}