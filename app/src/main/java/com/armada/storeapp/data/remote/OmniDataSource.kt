package com.armada.storeapp.data.remote

import com.armada.storeapp.data.ApiService
import com.armada.storeapp.data.local.dao.OmniProductDao
import com.armada.storeapp.data.local.model.RecentProduct
import com.armada.storeapp.data.model.request.*
import com.armada.storeapp.data.model.response.*
import retrofit2.Response
import retrofit2.http.*
import javax.inject.Inject


class OmniDataSource @Inject constructor(
    private val apiService: ApiService,
    private val omniProductDao: OmniProductDao
) {

    suspend fun getScannedItemDetails(
        skuCode: String,
        storeId: String,
        priceListId: String
    ) = apiService.getScannedItemDetails(skuCode, storeId, priceListId)
    suspend fun getSKUSearchDetails(
        skuCode: String,
        storeId: String,
        priceListId: String
    ) = apiService.searchModelStyle(skuCode, "500")

    suspend fun getAllStockDetails(
        skuCode: String,
        countryId: String,
        storeCode: String,
        loggedStoreCode: String,
        countryCode: String
    ) = apiService.getAllStockDetails(skuCode, countryId, storeCode, loggedStoreCode, countryCode)

    suspend fun omniInvoice(
        sessionToken: String,
        omniInvoiceRequest: OrderInvoiceRequest
    ) = apiService.omniInvoice(sessionToken, omniInvoiceRequest)

    suspend fun omniOrderPlace(
        sessionToken: String,
        omniOrderPlaceRequest: OmniOrderPlaceRequest
    ) = apiService.omniOrderPlace(sessionToken, omniOrderPlaceRequest)

    suspend fun searchModel(
        model: String
    )=apiService.searchModel(model)

    suspend fun searchModelStyle(
        model: String
    )=apiService.searchModelStyle(model,"500")

    suspend fun getStoreEmployees(
        storeId: String
    ) = apiService.getStoreEmployees(storeId)

    suspend fun getTimeSlot(
    ) = apiService.getTimeSlot()

    suspend fun getOmniOrders(
        startDate: String,
        endDate: String,
        storeCode: String,
        searchString: String,
        userCode: String,
        orderByStatus: String
    ) = apiService.getOmniOrders(
        startDate,
        endDate,
        storeCode,
        searchString,
        userCode,
        orderByStatus
    )

    suspend fun acceptPendingOrder(
        sessionToken: String,
        pendingOrderAcceptRequest: PendingOrderAcceptRequest
    ) = apiService.acceptPendingOrder(sessionToken, pendingOrderAcceptRequest)

    suspend fun getOmniOrderDetails(
        id: String
    ) = apiService.getOmniOrderDetails(id)


    suspend fun omniItemScan(
        skuCode: String,
        storeId: String,
        priceListId: String,
    ) = apiService.omniItemScan(skuCode, storeId, priceListId)


    suspend fun saveOmniOrder(
        saveOmniOrderRequest: SaveOmniOrderRequest
    ) = apiService.saveOmniOrder(saveOmniOrderRequest)


    suspend fun deliverStorePickupOrder(
        deliverOmniOrderRequest: DeliverOmniOrderRequest
    ) = apiService.deliverStorePickupOrder(deliverOmniOrderRequest)

    suspend fun getProductsByArticleNumber(
      searchValue: String,
       storeId: String,
       fromFormName: String,
    )=apiService.getProductsByArticleNumber(searchValue, storeId, fromFormName)

//    suspend fun createOmniEnquiry(
//        omniEnquiryRequest: OmniEnquiryRequest
//    ) = apiService.createOmniEnquiry(omniEnquiryRequest)
//
//
//    suspend fun saveOmniOrder(
//        saveOmniOrderRequest: SaveOmniOrderRequest
//    ) = apiService.saveOmniOrder(saveOmniOrderRequest)
//
//
//    suspend fun saveOmniEcommerceOrder(
//        omniSaveEcommerceOrderRequest: OmniSaveEcommerceOrderRequest
//    ) = apiService.saveOmniEcommerceOrder(omniSaveEcommerceOrderRequest)


    suspend fun createOmniCustomer(
        token: String,
        createOmniCustomerRequest: CreateOmniCustomerRequest
    ) = apiService.createOmniCustomer(token, createOmniCustomerRequest)

    suspend fun editOmniCustomer(
        sessionToken: String,
        omniCustomerRequest: CreateOmniCustomerRequest
    ) = apiService.editOmniCustomer(sessionToken, omniCustomerRequest)

    suspend fun getCustomerCode(
        sessionToken: String,
        storeId: String,
        documentTypeId: String,
        businessDate: String
    ) = apiService.getCustomerCode(sessionToken, storeId, documentTypeId, businessDate)


    suspend fun searchCustomerById(
        token: String,
        customerId: String
    ) = apiService.searchCustomerById(token, customerId)

    suspend fun searchCustomerByString(
        token: String,
        searchString: String
    ) = apiService.searchCustomerByString(token, searchString)

    suspend fun searchCustomerByEmail(
        token: String,
        email: String
    ) = apiService.searchCustomerByEmail(token, email)

    suspend fun getCountryList(
    ) = apiService.getCountryList()


    suspend fun getStateList(
        countryId: String
    ) = apiService.getStateList(countryId)

    suspend fun getCityList(
        stateId: String
    ) = apiService.getCityList(stateId)


    suspend fun addOmniProduct(product: SkuMasterTypes) =
        omniProductDao.insertOmniProduct(product)

    suspend fun updateOmniProduct(skuCode: String, qty: Int) =
        omniProductDao.updateOmniProductQuantity(skuCode, qty)

    fun getOmniProductsFromDb() = omniProductDao.getAllOmniProducts()

    suspend fun deleteAllProductsFromBag() = omniProductDao.deleteAll()

    suspend fun removeOmniProduct(skuCode: String) =
        omniProductDao.removeOmniProduct(skuCode)

    suspend fun updateOmniProductQuantity(skuCode: String, newQty: Int) =
        omniProductDao.updateOmniProductQuantity(skuCode, newQty)
}