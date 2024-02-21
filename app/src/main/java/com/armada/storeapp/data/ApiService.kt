package com.armada.storeapp.data

import com.armada.storeapp.data.model.request.*
import com.armada.storeapp.data.model.response.*
import com.google.android.datatransport.cct.StringMerger
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    /** Login APIs **/

    @POST("auth")
    suspend fun authorizeUser(
        @Body params: RequestBody,
    ): Response<AuthorizeResponseModel>

    @GET("api/PickList")
    suspend fun getPickList(
        @Query("isStatus") isStatus: String,
        @Query("StoreCode") storeCode: String,
        @Query("SearchString") searchString: String
    ): Response<PicklistResponseModel>


    @GET("api/PickList")
    suspend fun getPicklistByPages(
        @Query("isStatus") isStatus: String,
        @Query("StoreCode") storeCode: String,
        @Query("SearchString") searchString: String,
        @Query("limit") itemCount: String,
        @Query("offset") pageNo: String,
    ): Response<PicklistResponseModel>


    @GET("api/GetPicklist")
    suspend fun getManualPicklistByPages(
        @Query("isStatus") isStatus: String,
        @Query("StoreCode") storeCode: String,
        @Query("SearchString") searchString: String,
        @Query("limit") itemCount: String,
        @Query("offset") pageNo: String,
        @Query("Type") Type: String,
    ): Response<PicklistResponseModel>

    @GET("api/PicklistDetails")
    suspend fun getPickListDetails(
        @Query("isStatus") isStatus: String,
        @Query("StoreCode") storeCode: String,
        @Query("ID") picklistHeaderId: String
    ): Response<PicklistDetailsResponseModel>

    @GET("api/SkippedPickList")
    suspend fun getSkippedPicklist(
        @Query("StoreCode") storeCode: String,
    ): Response<PicklistResponseModel>

    @GET("api/CheckBin")
    suspend fun validateBin(
        @Query("StoreCode") storeCode: String,
        @Query("BinCode") bincode: String
    ): Response<ValidateBinResponse>

    @Headers("Content-Type: application/json")
    @POST("api/APIBinTransfer")
    suspend fun picklistBinTransfer(@Body picklistTransferRequest: PicklistTransferRequest): Response<PicklistBintransferResponseModel>

    @Headers("Content-Type: application/json")
    @POST("api/BinTransferSkippedItem")
    suspend fun binTransferSkippedItems(@Body picklistTransferRequest: PicklistTransferRequest): Response<PicklistBintransferResponseModel>


    @GET("api/User")
    suspend fun getUserDetails(
        @Header("Authorization") sessionToken: String
    ): Response<PosUserInfo>

    @GET("api/BinSkipReason")
    suspend fun getSkipItemReasonList(): Response<SkipReasonListResponse>

    @POST("api/SkipPickListTransfer")
    suspend fun skipPicklistTransfer(@Body skipPicklistTransferRequest: SkipPicklistTransferRequest):
            Response<SkipPicklistTransferResponse>


    @GET("api/CheckBin")
    suspend fun checkIteminBin(
        @Query("StoreCode") storeCode: String,
        @Query("BinCode") bincode: String,
        @Query("ItemCode") itemCode: String
    ): Response<ValidateBinResponse>


    @Headers("Content-Type: application/json")
    @POST("api/CommonBinTransfer")
    suspend fun commonBinTransfer(
        @Body commonBinTransferRequest: CommonBinTransferRequest
    ): Response<CommonBintransferResponse>

    @GET("api/CheckBinInventory")
    suspend fun checkbinInventory(
        @Query("storeCode") storeCode: String,
        @Query("itemCode") bincode: String,
    ): Response<CheckbinInventoryResponse>


    @GET("api/BinwiseReport")
    suspend fun itemOrBinSearch(
        @Query("limit") limit: String,
        @Query("offset") offset: String,
        @Query("isActive") isActive: String,
        @Query("itemCode") itemCode: String,
        @Query("binCode") bincode: String,
        @Query("StoreCode") storeCode: String
    ): Response<ItemBinSearchResponse>
    @GET("api/stock")
    suspend fun itemNotBinSearch(
        @Query("SearchValue") SearchValue: String,
        @Query("StoreID") StoreID: String,
        @Query("fromFormName") v: String
    ): Response<ItemNotBinSearchResponse>
    @Headers("Content-Type: application/json")
    @POST("api/PickList")
    suspend fun createManualPicklist(@Body manualPicklistRequest: ManualPicklistRequest)
            : Response<ManualPicklistResponse>

    @GET("api/PickList")
    suspend fun getCreatePicklistSkus(
        @Query("StoreCode") storeCode: String,
        @Query("styleCode") styleCode: String,
    ): Response<CreatePicklistSkuResponse>

    @GET("api/SalesDestinationBin")
    suspend fun getDestinationBinList(
        @Query("id") storeId: String
    ): Response<GetDestinationBinResponse>

    @GET("api/CheckSkuForAdjustmentAPI")
    suspend fun checkSkuForAdjustment(
        @Query("StoreCode") storeCode: String,
        @Query("styleCode") styleCode: String
    ): Response<CheckStockAdjustmentResponse>

    /* Omni channel APIS */

    @GET("api/OMNI_SkuSearchForSales")
    suspend fun getScannedItemDetails(
        @Query("SKUCode") skuCode: String,
        @Query("storeid") storeId: String,
        @Query("PriceListID") priceListId: String
    ): Response<ScannedItemDetailsResponse>


    @GET("api/OMNI_GetStockDetails")
    suspend fun getAllStockDetails(
        @Query("SKUCode") skuCode: String,
        @Query("CountryID") countryId: String,
        @Query("StoreCode") storeCode: String,
        @Query("LoggedStoreCode") loggedStoreCode: String,
        @Query("CountryCode") countryCode: String
    ): Response<OmniStockResponse>


    @POST("api/OMNI_Invoice")
    suspend fun omniInvoice(
        @Header("Authorization") sessionToken: String,
        @Body omniInvoiceRequest: OrderInvoiceRequest
    ): Response<OmniInvoiceResponse>

//    @POST("api/OMNI_CreateEnquiry")
//    suspend fun createOmniEnquiry(
//        @Body omniEnquiryRequest: OmniEnquiryRequest
//    ): Response<OmniEnquiryResponse>
//
//
//    @POST("api/OMNI_OrderMaster")
//    suspend fun saveOmniOrder(
//        @Body saveOmniOrderRequest: SaveOmniOrderRequest
//    ): Response<SaveOmniOrderResponse>
//
//
//    @POST("api/ECommerce")
//    suspend fun saveOmniEcommerceOrder(
//        @Body omniSaveEcommerceOrderRequest: OmniSaveEcommerceOrderRequest
//    ): Response<OmniEcommerceOrderResponse>


    @POST("api/customer")
    suspend fun createOmniCustomer(
        @Header("Authorization") sessionToken: String,
        @Body createOmniCustomerRequest: CreateOmniCustomerRequest
    ): Response<CreateOmniCustomerResponse>

    @PUT("api/customer")
    suspend fun editOmniCustomer(
        @Header("Authorization") sessionToken: String,
        @Body createOmniCustomerRequest: CreateOmniCustomerRequest
    ): Response<CreateOmniCustomerResponse>

    @GET("api/documentnumbering")
    suspend fun getCustomerCode(
        @Header("Authorization") sessionToken: String,
        @Query("storeid") storeId: String,
        @Query("DocumentTypeID") documentTypeId: String,
        @Query("business_date") businessDate: String
    ): Response<GetCustomerCodeResponse>


    @GET("api/customer")
    suspend fun searchCustomerById(
        @Header("Authorization") sessionToken: String,
        @Query("id") customerId: String
    ): Response<SearchCustomerResponse>


    @GET("api/CustomerSearchPOS")
    suspend fun searchCustomerByString(
        @Header("Authorization") sessionToken: String,
        @Query("custSearchString") searchString: String
    ): Response<SearchCustomerResponse>

    @GET("api/OMNI_CreateEnquiry")
    suspend fun searchCustomerByEmail(
        @Header("Authorization") sessionToken: String,
        @Query("CustomerCode") email: String
    ): Response<SearchCustomerByMailResponse>

    @GET("api/CountryMasterLookUP")
    suspend fun getCountryList(
    ): Response<GetCountryResponse>

    @GET("api/StateMasterLookUp")
    suspend fun getStateList(
        @Query("countryid") countryId: String
    ): Response<GetStateResponse>

    @GET("api/OMNI_CityLookUp")
    suspend fun getCityList(
        @Query("StateID") stateId: String
    ): Response<GetCityResponse>

    @GET("api/OmniTimeSlot")
    suspend fun getTimeSlot(
    ): Response<GetTimeSlotResponse>

    @GET("api/salesemployee")
    suspend fun getStoreEmployees(
        @Query("storeid") storeId: String
    ): Response<GetStoreEmployeeResponse>


    @POST("api/OMNI_MobileApp")
    suspend fun omniOrderPlace(
        @Header("Authorization") sessionToken: String,
        @Body omniOrderPlaceRequest: OmniOrderPlaceRequest
    ): Response<OmniOrderPlaceResponse>

    @GET("api/SearchEngineProduct")
    suspend fun searchModel(
        @Query("CustSearchString") model: String
    ): Response<SearchModelResponse>

    @GET("api/sku")
    suspend fun searchModelStyle(
        @Query("searchString") model: String,
        @Query("limit") limit: String,
        @Query("isActive") isActive: String,
        @Query("offset") offset: String,
    ): Response<ScannedItemDetailsResponse>


    @GET("api/OMNI_OrderMaster")
    suspend fun getOmniOrders(
        @Query("StartDate") startDate: String,
        @Query("EndDate") endDate: String,
        @Query("StoreCode") storeCode: String,
        @Query("searchString") searchString: String,
        @Query("userCode")userCode:String,
        @Query("orderbyStatus") orderByStatus:String
    ): Response<OmniOrdersResponse>

    @PUT("api/OMNI_OrderMaster")
    suspend fun acceptPendingOrder(
        @Header("Authorization") sessionToken: String,
        @Body pendingOrderAcceptRequest: PendingOrderAcceptRequest
    ): Response<PendingOrderAcceptResponse>


    @GET("api/OMNI_OrderMaster")
    suspend fun getOmniOrderDetails(
        @Query("ID") id: String
    ): Response<OmniOrderDetailsResponse>


    @GET("api/SKUSearchForSales")
    suspend fun omniItemScan(
        @Query("skucode") skuCode: String,
        @Query("storeid") storeId: String,
        @Query("pricelistid") priceListId: String,
    ): Response<ScannedItemDetailsResponse>


    @POST("api/OMNI_AcceptOrders")
    suspend fun saveOmniOrder(
        @Body saveOmniOrderRequest: SaveOmniOrderRequest
    ): Response<SaveOmniOrderResponse>


    @POST("api/OMNI_AcceptOrders")
    suspend fun deliverStorePickupOrder(
        @Body deliverOmniOrderRequest: DeliverOmniOrderRequest
    ): Response<OmniOrdersResponse>

    @GET("api/Stock")
    suspend fun getProductsByArticleNumber(
        @Query("SearchValue") searchValue: String,
        @Query("StoreID") storeId: String,
        @Query("fromFormName") fromFormName: String,
    ): Response<ArticleProductsResponse>


}