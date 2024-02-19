package com.armada.storeapp.data

import com.armada.storeapp.data.model.request.AddStockReceiptRequest
import com.armada.storeapp.data.model.request.AddStockReturnRequest
import com.armada.storeapp.data.model.request.CompletePickRequest
import com.armada.storeapp.data.model.request.StockAdjustmentAddRequest
import com.armada.storeapp.data.model.response.*
import retrofit2.Response
import retrofit2.http.*

interface WarehouseApiService {

    @POST("token")
    @FormUrlEncoded
    suspend fun tokenGeneration(
        @FieldMap map: Map<String, String>
    ): Response<GenerateTokenResponse>

    @POST("api/UserLogin")
    suspend fun userLogin(
        @Query("id") userId: String,
        @Query("pas") password: String,
        @Header("Authorization") sessionToken: String
    ): Response<WarehouseLoginResponse>

    @POST("Shops_api/List_Pending_Orders")
    suspend fun pendingOrdersList(
        @Query("sHOP_location_code") shopLocationCode: String,
        @Header("Authorization") sessionToken: String
    ): Response<ShopPickOrdersResponseModel>

    @GET("Shops_api/Get_Reasons")
    suspend fun getReasons(@Header("Authorization") sessionToken: String): Response<ShopPickReasonResponseModel>

    @POST("Shops_api/List_Pending_Orders_DOC_dATE")
    suspend fun pendingOrdersListByDocumentNumberAndDate(
        @Query("sHOP_location_code") shopLocationCode: String,
        @Query("Doc_num") documentNumber: String,
        @Query("frmDate") fromDate: String,
        @Query("date") date: String,
        @Header("Authorization") sessionToken: String
    ): Response<ShopPickOrdersResponseModel>

    @POST("Shops_api/Doc_Process_Open")
    suspend fun openProcess(
        @Query("sHOP_location_code") shopLocationCode: String,
        @Query("Doc_num") documentNumber: String,
        @Query("to_location") toLocation: String,
        @Query("SHORT_NAME") shortName: String,
        @Header("Authorization") sessionToken: String
    ): Response<OpenDocumentResponseModel>

    @POST("Shops_api/ScanItems")
    suspend fun scanItem(
        @Query("barcode") barcode: String,
        @Header("Authorization") sessionToken: String
    ): Response<ScanItemResponseModel>

    @Headers("Content-Type: application/json")
    @POST("Shops_api/CompletePick")
    suspend fun completePick(
        @Body completePickRequest: CompletePickRequest,
        @Header("Authorization") sessionToken: String
    )
            : Response<CompletePickResponseModel>

//    @POST("Shops_api/CompletePick")
//    suspend fun completePick(@Query("Remark")remark:String,@Query("RefNo") refNo:String,@Query("From_Location") fromLocation:String,
//                             @Query("Id")id:String,@Query("Location_code")locationCode:String,@Query("Qty")qty:String,@Header("Authorization") sessionToken: String)
//            :Response<CompletePickResponseModel>


    /* Stock Receive Apis */

    @POST("api/SR_FromLocation")
    suspend fun getFromLocationList(
        @Query("User_code") userCode: String,
        @Query("Country_code") countryCode: String,
        @Header("Authorization") sessionToken: String
    ): Response<FromLocationListResponse>

    @POST("Shops_api/List_Documents")
    suspend fun getStockReceiptDocumentList(
        @Query("location_code") locationCode: String,
        @Query("User_code") userCode: String,
        @Query("DocDate") docDate: String,
        @Query("From_Location") fromLocation: String,
        @Query("DocNum") docNumber: String,
        @Header("Authorization") sessionToken: String
    ): Response<StockReceiptDocumentResponseModel>

    @POST("api/SR_TransferType")
    suspend fun getTransferTypes(
        @Header("Authorization") sessionToken: String
    ): Response<TransferTypeResponseModel>

    @POST("api/SR_Open_doc")
    suspend fun openStockReceiptDocument(
        @Query("ID") id: String,
        @Query("trans_no") transcationNo: String,
        @Query("Remarks") remarks: String,
        @Query("User_code") userCode: String,
        @Query("FromLoc") fromLocation: String,
        @Query("ToLoc") toLocation: String,
        @Header("Authorization") sessionToken: String
    ): Response<OpenStockReceiptDocumentResponse>

    @Headers("Content-Type: application/json")
    @POST("Shops_api/Add_funcation")
    suspend fun addStockReceiptDocument(
        @Query("trans_no") transcationNo: String,
        @Query("Remarks") remarks: String,
        @Query("User_code") userCode: String,
        @Query("FromLoc") fromLocation: String,
        @Query("ToLoc") toLocation: String,
        @Query("StrRamrks") strRemarks: String,
        @Query("TotalQty") totalQty: String,
        @Query("totalReqQty") totalReqQty: String,
        @Query("intransitEnableloc") intransitEnabledLoc: String,
        @Query("Flocation") Flocation: String,
        @Query("Crossdockintype") crossdockInType: String,
        @Query("DisDate") disDate: String,
        @Header("Authorization") sessionToken: String,
        @Body addStockReceiptRequest: AddStockReceiptRequest
    ): Response<AddStockReceiptResponse>

    /* Stock Return Apis */

    @POST("api/TR_ToLocation")
    suspend fun getToLocationList(
        @Query("User_code") userCode: String,
        @Query("Country_code") countryCode: String,
        @Header("Authorization") sessionToken: String
    ): Response<ToLocationListResponse>


    @POST("api/TR_Priority")
    suspend fun getPriorityList(
        @Query("User_code") userCode: String,
        @Query("Country_code") countryCode: String,
        @Header("Authorization") sessionToken: String
    ): Response<PriorityListResponse>


    @POST("api/TR_Type")
    suspend fun getTransferList(
        @Query("User_code") userCode: String,
        @Query("Country_code") countryCode: String,
        @Header("Authorization") sessionToken: String
    ): Response<TransferTypeResponseModel>


    @POST("api/TR_ScanBarcode")
    suspend fun scanStockReturnItem(
        @Query("User_code") userCode: String,
        @Query("Location_code") locationCode: String,
        @Query("Barcode") barcode: String,
        @Query("ReturnType") returnType: String,
        @Header("Authorization") sessionToken: String
    ): Response<StockReturnItemScanResponse>


    @POST("api/TR_Default_ToLoc")
    suspend fun getDefaultToLocation(
        @Query("User_code") userCode: String,
        @Header("Authorization") sessionToken: String
    ): Response<DefaultToLocationResponse>

    @Headers("Content-Type: application/json")
    @POST("api/TR_AddFunction")
    suspend fun addStockReturn(
        @Query("User_code") userCode: String,
        @Query("FromLocation") fromLocation: String,
        @Query("Tolocation") toLocation: String,
        @Query("TotQty") totalQty: String,
        @Query("Priority") priority: String,
        @Query("TypeOfOrder") typeOfTransfer: String,
        @Query("StrRemarks") userRemarks: String,
        @Header("Authorization") sessionToken: String,
        @Body addStockReturnRequest: AddStockReturnRequest
    ): Response<AddStockReturnResponse>


    /* Stock Adjustment Apis */
    @POST("api/Adj_Scanmodel")
    suspend fun scanModelStockAdjustment(
        @Query("User_code") userCode: String,
        @Query("Location_code") locationCode: String,
        @Query("Model_code") modelCode: String,
        @Header("Authorization") sessionToken: String
    ): Response<StockAdjustmentScanModelResponse>


    @POST("api/Adj_AddFunction")
    suspend fun addStockAdjustment(
        @Query("User_code") userCode: String,
        @Query("Location_code") locationCode: String,
        @Query("Model_code") modelCode: String,
        @Query("TotalQty") totalQty: String,
        @Query("TotalCount") totalCount: String,
        @Query("TotalDiffQty") totalDiffQty: String,
        @Header("Authorization") sessionToken: String,
        @Body addStockAdjustmentAddRequest: StockAdjustmentAddRequest
    ): Response<AddStockAdjustmentResponse>


    @GET("Shops_api/Get_Image")
    suspend fun getPickItemImage(
        @Query("Department_code") departmentCode: String,
        @Query("Product_code") productcode: String,
        @Header("Authorization") sessionToken: String
    ): Response<GetImageResponse>
}