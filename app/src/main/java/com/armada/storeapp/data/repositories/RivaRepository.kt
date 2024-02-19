package com.armada.storeapp.data.repositories

import androidx.lifecycle.LiveData
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.local.model.RecentProduct
import com.armada.storeapp.data.local.model.ShoppingCart
import com.armada.storeapp.data.local.model.Wishlist
import com.armada.storeapp.data.model.StoreData
import com.armada.storeapp.data.model.request.*
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.data.remote.RivaDataSource
import com.armada.storeapp.ui.home.riva.riva_look_book.search.Stores.StoreDataResponseModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.http.Body
import java.util.*
import javax.inject.Inject

@ActivityRetainedScoped
class RivaRepository @Inject constructor(
    private val rivaDataSource: RivaDataSource
) : BaseApiResponse() {

    suspend fun getCountryStores(
    ): Flow<Resource<CountryStoreResponse>> {
        return flow<Resource<CountryStoreResponse>> {
            emit(safeApiCall {
                rivaDataSource.getCountryStores()
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun registerRivaUser(language:String,currency: String,
        rivaRegisterUserRequest: RivaRegisterUserRequest
    ): Flow<Resource<RivaRegisterUserResponse>> {
        return flow<Resource<RivaRegisterUserResponse>> {
            emit(safeApiCall {
                rivaDataSource.registerRivaUser(language,currency,rivaRegisterUserRequest)
            })
        }.flowOn(Dispatchers.IO)
    }



    suspend fun rivaUserLogin(language:String,currency: String,
        rivaLoginRequest: RivaLoginRequest
    ): Flow<Resource<RivaRegisterUserResponse>> {
        return flow<Resource<RivaRegisterUserResponse>> {
            emit(safeApiCall {
                rivaDataSource.rivaUserLogin(language,currency,rivaLoginRequest)
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addAddress(language:String,
        addAddressRequestModel: AddAddressRequestModel
    ): Flow<Resource<AddAddressResponseModel>> {
        return flow<Resource<AddAddressResponseModel>> {
            emit(safeApiCall {
                rivaDataSource.addAddress(language,addAddressRequestModel)
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun editAddress(language:String,
        addressId: String,
        addAddressRequestModel: AddAddressRequestModel
    ): Flow<Resource<AddAddressResponseModel>> {
        return flow<Resource<AddAddressResponseModel>> {
            emit(safeApiCall {
                rivaDataSource.editAddress(language,addressId, addAddressRequestModel)
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteAddress(language:String,
        addressId: String,
    ): Flow<Resource<DeleteAddressResponseModel>> {
        return flow<Resource<DeleteAddressResponseModel>> {
            emit(safeApiCall {
                rivaDataSource.deleteAddress(language,addressId)
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCountryList(language:String
    ): Flow<Resource<CountryListResponseModel>> {
        return flow<Resource<CountryListResponseModel>> {
            emit(safeApiCall {
                rivaDataSource.getCountryList(language)
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getParentCategories(
    ): Flow<Resource<ParentCategoryResponse>> {
        return flow<Resource<ParentCategoryResponse>> {
            emit(safeApiCall {
                rivaDataSource.getParentCategories()
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getSubCategories(
        parentCategoryId: String,
        collectionId: String
    ): Flow<Resource<SubCategoryResponse>> {
        return flow {
            emit(safeApiCall { rivaDataSource.getSubCategories(parentCategoryId, collectionId) })
        }
    }

    suspend fun getBannerCollections(
        language: String,
        parentCategoryId: String,
        collectionId: String
    ): Flow<Resource<HomeDataModel>> {
        return flow {
            emit(safeApiCall {
                rivaDataSource.getBannerCollections(language,
                    parentCategoryId,
                    collectionId
                )
            })
        }
    }

    suspend fun getEditorials(
        language: String,
        categoryId: String
    ): Flow<Resource<EditorialResponse>> {
        return flow {
            emit(safeApiCall { rivaDataSource.getEditorials(language,categoryId) })
        }
    }

    suspend fun getAssociateProducts(productIds: String): Flow<Resource<AssociateProductResponse>> {
        return flow {
            emit(safeApiCall { rivaDataSource.getAssociateProdcuts(productIds) })
        }
    }

    suspend fun getProductDetails(language:String,currency: String,productId: String): Flow<Resource<ProductDetailsResponse>> {
        return flow {
            emit(safeApiCall { rivaDataSource.getProductDetails(language,currency,productId) })
        }
    }

    suspend fun changeConfig(language:String,
        configRequestModel: ConfigRequestModel
    ): Flow<Resource<ConfigResponseModel>> {
        return flow {
            emit(safeApiCall { rivaDataSource.changeConfig(language,configRequestModel) })
        }
    }

    suspend fun getStoreList(language:String
    ): Flow<Resource<StoreDataResponseModel>> {
        return flow {
            emit(safeApiCall { rivaDataSource.getStoreList(language) })
        }
    }

    suspend fun addToCart(language:String,currency: String,
        addToCartRequest: AddToCartRequest
    ): Flow<Resource<AddToCartResponse>> {
        return flow {
            emit(safeApiCall { rivaDataSource.addToCart(language,currency,addToCartRequest) })
        }
    }


    suspend fun getCartItems(language:String,currency: String,customerId: String): Flow<Resource<CartItemsResponse>> {
        return flow {
            emit(safeApiCall { rivaDataSource.getCartItems(language,currency,customerId) })
        }
    }

    suspend fun checkItemStock(language:String,currency: String,
        stockRequestModel: StockRequestModel
    ): Flow<Resource<CheckStockResponseModel>> {
        return flow {
            emit(safeApiCall { rivaDataSource.checkItemStock(language,currency,stockRequestModel) })
        }
    }

    suspend fun changeCartItem(language:String,currency: String,
        updateSizeRequest: UpdateSizeRequest
    ): Flow<Resource<CartItemsResponse>> {
        return flow {
            emit(safeApiCall { rivaDataSource.changeCartItem(language,currency,updateSizeRequest) })
        }
    }


    suspend fun updateCart(language:String,currency: String,
        updateCartRequest: UpdateCartRequest
    ): Flow<Resource<CartItemsResponse>> {
        return flow {
            emit(safeApiCall { rivaDataSource.updateCart(language,currency,updateCartRequest) })
        }
    }

    suspend fun deleteCartItem(language:String,currency: String,
        deleteCartRequestModel: DeleteCartRequestModel
    ): Flow<Resource<AddToCartResponse>> {
        return flow {
            emit(safeApiCall { rivaDataSource.deleteCartItem(language,currency,deleteCartRequestModel) })
        }
    }


    suspend fun addToWishlist(addToWishList: WishlistRequest.AddToWishList): Flow<Resource<WishlistResponse>> {
        return flow {
            emit(safeApiCall { rivaDataSource.addToWishlist(addToWishList) })
        }
    }

    suspend fun removeFromWishlist(removeFromWishList: WishlistRequest.RemoveFromWishList): Flow<Resource<WishlistResponse>> {
        return flow {
            emit(safeApiCall { rivaDataSource.removeFromWishlist(removeFromWishList) })
        }
    }

    suspend fun storeCredit(
        value: String,language:String,currency: String,
        applyRemoveStoreCreditRequestModel: ApplyRemoveStoreCreditRequestModel
    ): Flow<Resource<ApplyCreditResponseModel>> {
        return flow {
            emit(safeApiCall { rivaDataSource.storeCredit(value,language,currency,applyRemoveStoreCreditRequestModel) })
        }
    }

    suspend fun rivaCheckout(language:String,currency: String,
        rivaOrderRequest: RivaOrderRequest
    ): Flow<Resource<RivaOrderResponseModel>> {
        return flow {
            emit(safeApiCall { rivaDataSource.rivaCheckout(language,currency,rivaOrderRequest) })
        }
    }

    suspend fun checkItemStockNoAddress(language:String,currency: String,
        stockRequestModel: StockRequestModel
    ): Flow<Resource<StockResponseModelNoAddress>> {
        return flow {
            emit(safeApiCall { rivaDataSource.checkItemStockNoAddress(language,currency,stockRequestModel) })
        }
    }


    suspend fun cancelOrder(language:String,currency: String,
        orderCancelRequestModel: OrderCancelRequestModel
    ): Flow<Resource<Unit>> {
        return flow {
            emit(safeApiCall { rivaDataSource.cancelOrder(language,currency,orderCancelRequestModel) })
        }
    }

    suspend fun applyCoupon(language:String,currency: String,
        couponRequestModel: CouponRequestModel
    ): Flow<Resource<StockResponseModel>> {
        return flow {
            emit(safeApiCall { rivaDataSource.applyCoupon(language,currency,couponRequestModel) })
        }
    }

    // fetching from db

    suspend fun getCartCount(): Flow<Int> {
        return flow {
            emit(rivaDataSource.getCartCount())
        }
    }

    suspend fun insertItemToCart(cartItem: ShoppingCart) {
        return rivaDataSource.insertItemToCartDb(cartItem)
    }

    fun getWishlistProductIdsFromDb(): LiveData<List<String>> {
        return rivaDataSource.getWishlistProductIdsFromDb()
    }

    suspend fun addProductToWishlistDb(wishlist: Wishlist) {
        rivaDataSource.addProductToWishlistDb(wishlist)
    }

    suspend fun removeProductFromWishlistDb(entityId: String) {
        rivaDataSource.removeProductFromWishlistDb(entityId)
    }

    suspend fun deleteAllWishlistItemsDb() {
        rivaDataSource.deleteAllWishlistItemsDb()
    }

    suspend fun addProductToRecentDb(recentProduct: RecentProduct) =
        rivaDataSource.addProductToRecentDb(recentProduct)

    fun getRecentProductsFromDb(): LiveData<List<RecentProduct>> {
        return rivaDataSource.getRecentProductsFromDb()
    }

    fun getCartItemsFromDb(): LiveData<List<ShoppingCart>> {
        return rivaDataSource.getCartItemsFromDb()
    }
}