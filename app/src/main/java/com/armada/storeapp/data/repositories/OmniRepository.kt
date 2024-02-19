package com.armada.storeapp.data.repositories

import androidx.lifecycle.LiveData
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.local.model.RecentProduct
import com.armada.storeapp.data.model.request.*
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.data.remote.InStoreDataSource
import com.armada.storeapp.data.remote.OmniDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class OmniRepository @Inject constructor(
    private val omniDataSource: OmniDataSource
) : BaseApiResponse() {

    suspend fun getScannedItemDetails(
        skuCode: String,
        storeId: String,
        priceListId: String
    ): Flow<Resource<ScannedItemDetailsResponse>> {
        return flow<Resource<ScannedItemDetailsResponse>> {
            emit(safeApiCall {
                omniDataSource.getScannedItemDetails(
                    skuCode, storeId, priceListId
                )
            })
        }.flowOn(Dispatchers.IO)
    }
    suspend fun searchModelStyle(
        model: String
    ): Flow<Resource<ScannedItemDetailsResponse>> {
        return flow<Resource<ScannedItemDetailsResponse>> {
            emit(safeApiCall {
                omniDataSource.getSKUSearchDetails(
                    model, model, model
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAllStockDetails(
        skuCode: String,
        countryId: String,
        storeCode: String,
        loggedStoreCode: String,
        countryCode: String
    ): Flow<Resource<OmniStockResponse>> {
        return flow<Resource<OmniStockResponse>> {
            emit(safeApiCall {
                omniDataSource.getAllStockDetails(
                    skuCode, countryId, storeCode, loggedStoreCode, countryCode
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun omniInvoice(
        sessionToken: String,
        omniInvoiceRequest: OrderInvoiceRequest
    ): Flow<Resource<OmniInvoiceResponse>> {
        return flow<Resource<OmniInvoiceResponse>> {
            emit(safeApiCall {
                omniDataSource.omniInvoice(
                    sessionToken,
                    omniInvoiceRequest
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun omniOrderPlace(
        sessionToken: String,
        omniOrderPlaceRequest: OmniOrderPlaceRequest
    ): Flow<Resource<OmniOrderPlaceResponse>> {
        return flow<Resource<OmniOrderPlaceResponse>> {
            emit(safeApiCall {
                omniDataSource.omniOrderPlace(
                    sessionToken,
                    omniOrderPlaceRequest
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun searchModel(
        model: String
    ): Flow<Resource<SearchModelResponse>> {
        return flow<Resource<SearchModelResponse>> {
            emit(safeApiCall {
                omniDataSource.searchModel(model
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getStoreEmployees(
        storeId: String
    ): Flow<Resource<GetStoreEmployeeResponse>> {
        return flow<Resource<GetStoreEmployeeResponse>> {
            emit(safeApiCall {
                omniDataSource.getStoreEmployees(
                    storeId
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getTimeSlot(
    ): Flow<Resource<GetTimeSlotResponse>> {
        return flow<Resource<GetTimeSlotResponse>> {
            emit(safeApiCall {
                omniDataSource.getTimeSlot(
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getOmniOrders(
        startDate: String,
        endDate: String,
        storeCode: String,
        searchString: String,
        userCode: String,
        orderByStatus: String
    ): Flow<Resource<OmniOrdersResponse>> {
        return flow<Resource<OmniOrdersResponse>> {
            emit(safeApiCall {
                omniDataSource.getOmniOrders(
                    startDate, endDate, storeCode, searchString, userCode, orderByStatus
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun acceptPendingOrder(
        sessionToken: String,
        pendingOrderAcceptRequest: PendingOrderAcceptRequest
    ): Flow<Resource<PendingOrderAcceptResponse>> {
        return flow<Resource<PendingOrderAcceptResponse>> {
            emit(safeApiCall {
                omniDataSource.acceptPendingOrder(
                    sessionToken, pendingOrderAcceptRequest
                )
            })
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getOmniOrderDetails(
        id: String
    ): Flow<Resource<OmniOrderDetailsResponse>> {
        return flow<Resource<OmniOrderDetailsResponse>> {
            emit(safeApiCall {
                omniDataSource.getOmniOrderDetails(id)
            })
        }.flowOn(Dispatchers.IO)
    }


    suspend fun omniItemScan(
        skuCode: String,
        storeId: String,
        priceListId: String,
    ): Flow<Resource<ScannedItemDetailsResponse>> {
        return flow<Resource<ScannedItemDetailsResponse>> {
            emit(safeApiCall {
                omniDataSource.omniItemScan(skuCode, storeId, priceListId)
            })
        }.flowOn(Dispatchers.IO)
    }


    suspend fun saveOmniOrder(
        saveOmniOrderRequest: SaveOmniOrderRequest
    ): Flow<Resource<SaveOmniOrderResponse>> {
        return flow<Resource<SaveOmniOrderResponse>> {
            emit(safeApiCall {
                omniDataSource.saveOmniOrder(saveOmniOrderRequest)
            })
        }.flowOn(Dispatchers.IO)
    }


    suspend fun deliverStorePickupOrder(
        deliverOmniOrderRequest: DeliverOmniOrderRequest
    ): Flow<Resource<OmniOrdersResponse>> {
        return flow<Resource<OmniOrdersResponse>> {
            emit(safeApiCall {
                omniDataSource.deliverStorePickupOrder(deliverOmniOrderRequest)
            })
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getProductsByArticleNumber(
        searchValue: String,
        storeId: String,
        fromFormName: String,
    ): Flow<Resource<ArticleProductsResponse>> {
        return flow<Resource<ArticleProductsResponse>> {
            emit(safeApiCall {
                omniDataSource.getProductsByArticleNumber(searchValue, storeId, fromFormName)
            })
        }.flowOn(Dispatchers.IO)
    }

//    suspend fun createOmniEnquiry(
//        omniEnquiryRequest: OmniEnquiryRequest
//    ): Flow<Resource<OmniEnquiryResponse>> {
//        return flow<Resource<OmniEnquiryResponse>> {
//            emit(safeApiCall {
//                omniDataSource.createOmniEnquiry(
//                    omniEnquiryRequest
//                )
//            })
//        }.flowOn(Dispatchers.IO)
//    }
//
//
//    suspend fun saveOmniOrder(
//        saveOmniOrderRequest: SaveOmniOrderRequest
//    ): Flow<Resource<SaveOmniOrderResponse>> {
//        return flow<Resource<SaveOmniOrderResponse>> {
//            emit(safeApiCall {
//                omniDataSource.saveOmniOrder(
//                    saveOmniOrderRequest
//                )
//            })
//        }.flowOn(Dispatchers.IO)
//    }
//
//
//    suspend fun saveOmniEcommerceOrder(
//        omniSaveEcommerceOrderRequest: OmniSaveEcommerceOrderRequest
//    ): Flow<Resource<OmniEcommerceOrderResponse>> {
//        return flow<Resource<OmniEcommerceOrderResponse>> {
//            emit(safeApiCall {
//                omniDataSource.saveOmniEcommerceOrder(
//                    omniSaveEcommerceOrderRequest
//                )
//            })
//        }.flowOn(Dispatchers.IO)
//    }


    suspend fun createOmniCustomer(
        token: String,
        createOmniCustomerRequest: CreateOmniCustomerRequest
    ): Flow<Resource<CreateOmniCustomerResponse>> {
        return flow<Resource<CreateOmniCustomerResponse>> {
            emit(safeApiCall {
                omniDataSource.createOmniCustomer(
                    token,
                    createOmniCustomerRequest
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun editOmniCustomer(
        sessionToken: String,
        omniCustomerRequest: CreateOmniCustomerRequest
    ): Flow<Resource<CreateOmniCustomerResponse>> {
        return flow<Resource<CreateOmniCustomerResponse>> {
            emit(safeApiCall {
                omniDataSource.editOmniCustomer(
                    sessionToken,
                    omniCustomerRequest
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCustomerCode(
        sessionToken: String,
        storeId: String,
        documentTypeId: String,
        businessDate: String
    ): Flow<Resource<GetCustomerCodeResponse>> {
        return flow<Resource<GetCustomerCodeResponse>> {
            emit(safeApiCall {
                omniDataSource.getCustomerCode(
                    sessionToken, storeId, documentTypeId, businessDate
                )
            })
        }.flowOn(Dispatchers.IO)
    }


    suspend fun searchCustomerById(
        token: String,
        customerId: String
    ): Flow<Resource<SearchCustomerResponse>> {
        return flow<Resource<SearchCustomerResponse>> {
            emit(safeApiCall {
                omniDataSource.searchCustomerById(
                    token,
                    customerId
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun searchCustomerByString(
        token: String,
        searchString: String
    ): Flow<Resource<SearchCustomerResponse>> {
        return flow<Resource<SearchCustomerResponse>> {
            emit(safeApiCall {
                omniDataSource.searchCustomerByString(
                    token,
                    searchString
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun searchCustomerByEmail(
        token: String,
        email: String
    ): Flow<Resource<SearchCustomerByMailResponse>> {
        return flow<Resource<SearchCustomerByMailResponse>> {
            emit(safeApiCall {
                omniDataSource.searchCustomerByEmail(
                    token,
                    email
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCountryList(
    ): Flow<Resource<GetCountryResponse>> {
        return flow<Resource<GetCountryResponse>> {
            emit(safeApiCall {
                omniDataSource.getCountryList(
                )
            })
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getStateList(
        countryId: String
    ): Flow<Resource<GetStateResponse>> {
        return flow<Resource<GetStateResponse>> {
            emit(safeApiCall {
                omniDataSource.getStateList(
                    countryId
                )
            })
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getCityList(
        stateId: String
    ): Flow<Resource<GetCityResponse>> {
        return flow<Resource<GetCityResponse>> {
            emit(safeApiCall {
                omniDataSource.getCityList(
                    stateId
                )
            })
        }.flowOn(Dispatchers.IO)
    }


    /* fetching from db
     */

    suspend fun addOmniProduct(product: SkuMasterTypes) =
        omniDataSource.addOmniProduct(product)

    suspend fun updateOmniProduct(skuCode: String, qty: Int) =
        omniDataSource.updateOmniProduct(skuCode, qty)

    fun getAllOmniProduct(): LiveData<List<SkuMasterTypes>> {
        return omniDataSource.getOmniProductsFromDb()
    }

    suspend fun removeOmniProduct(skuCode: String) =
        omniDataSource.removeOmniProduct(skuCode)

    suspend fun updateOmniProductQuantity(skuCode: String, newQty: Int) =
        omniDataSource.updateOmniProductQuantity(skuCode, newQty)

    suspend fun deleteAllProductsFromBag() = omniDataSource.deleteAllProductsFromBag()
}