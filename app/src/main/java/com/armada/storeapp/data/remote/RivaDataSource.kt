package com.armada.storeapp.data.remote

import com.armada.storeapp.data.RivaApiService
import com.armada.storeapp.data.RivaSecondaryApiService
import com.armada.storeapp.data.local.dao.RecentProductDao
import com.armada.storeapp.data.local.dao.ShoppingCartDao
import com.armada.storeapp.data.local.dao.WishlistDao
import com.armada.storeapp.data.local.model.RecentProduct
import com.armada.storeapp.data.local.model.RecentSearch
import com.armada.storeapp.data.local.model.ShoppingCart
import com.armada.storeapp.data.local.model.Wishlist
import com.armada.storeapp.data.model.StoreData
import com.armada.storeapp.data.model.request.*
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.ui.home.riva.riva_look_book.search.Stores.StoreDataResponseModel
import retrofit2.Response
import retrofit2.http.*
import javax.inject.Inject

class RivaDataSource @Inject constructor(
    private val apiService: RivaSecondaryApiService,
    private val rivaApiService: RivaApiService,
    private val shoppingCartDao: ShoppingCartDao,
    private val recentProductDao: RecentProductDao,
    private val wishlistDao: WishlistDao
) {

    suspend fun getCountryStores(
    ) = rivaApiService.getCountryStores("en")

    suspend fun registerRivaUser(language:String,currency:String,
        rivaRegisterUserRequest: RivaRegisterUserRequest
    ) = rivaApiService.registerRivaUser(language,currency, rivaRegisterUserRequest)

    suspend fun rivaUserLogin(language:String,currency:String,
        rivaLoginRequest: RivaLoginRequest
    ) = rivaApiService.rivaUserLogin(language,currency, rivaLoginRequest)

    suspend fun addAddress(language:String,
        addAddressRequestModel: AddAddressRequestModel
    ) = rivaApiService.addAddress(language, addAddressRequestModel)

    suspend fun editAddress(language:String,
        addressId: String,
        addAddressRequestModel: AddAddressRequestModel
    ) = rivaApiService.editAddress(addressId, language, addAddressRequestModel)

    suspend fun deleteAddress(language:String,
        addressId: String,
    ) = rivaApiService.deleteAddress(addressId, language)

    suspend fun getCountryList(language:String,
    ) = rivaApiService.getCountryList(language, "1")

    suspend fun getParentCategories() = apiService.getParentCategoryList("en")
    suspend fun getSubCategories(parentCategoryId: String, collectionId: String) =
        apiService.getSubCategoryList("en", parentCategoryId, collectionId)

    suspend fun getBannerCollections(language:String,parentCategoryId: String, collectionId: String) =
        apiService.getBannerCollections(language, parentCategoryId, collectionId)

    suspend fun getEditorials(language:String,
        categoryId: String
    ) = apiService.getEditorials(language, categoryId)

    suspend fun getAssociateProdcuts(productIds: String) =
        rivaApiService.getAssociateProducts(productIds)

    suspend fun getProductDetails(language:String,currency: String,productId: String) =
        rivaApiService.getProductDetails(productId, language,currency)

    suspend fun changeConfig(language:String,
        configRequestModel: ConfigRequestModel
    ) = rivaApiService.changeConfig(configRequestModel, language)

    suspend fun getStoreList(language:String
    ) = apiService.getStoreList(language, "en")

    suspend fun addToCart(language:String,currency:String,
        addToCartRequest: AddToCartRequest
    ) = rivaApiService.addToCart(language,currency, addToCartRequest)

    suspend fun getCartItems(language:String,currency:String,customerId: String) =
        rivaApiService.getCartItems(customerId, language, currency)

    suspend fun checkItemStock(language:String,currency:String,
        stockRequestModel: StockRequestModel
    ) = rivaApiService.checkItemStock(language,currency, stockRequestModel)

    suspend fun changeCartItem(language:String,currency: String,
        updateSizeRequest: UpdateSizeRequest
    ) = rivaApiService.changeCartItem(language, currency,updateSizeRequest)


    suspend fun updateCart(language:String,currency: String,
        updateCartRequest: UpdateCartRequest
    ) = rivaApiService.updateCart(language,currency, updateCartRequest)

    suspend fun deleteCartItem(language:String,currency: String,
        deleteCartRequestModel: DeleteCartRequestModel
    ) = rivaApiService.deleteCartItem(language,currency, deleteCartRequestModel)

    suspend fun addToWishlist(addToWishList: WishlistRequest.AddToWishList) =
        rivaApiService.addToWishlist(addToWishList)

    suspend fun removeFromWishlist(removeFromWishList: WishlistRequest.RemoveFromWishList) =
        rivaApiService.removeFromWishlist(removeFromWishList)

    suspend fun storeCredit(
        value: String,language:String,currency: String,
        applyRemoveStoreCreditRequestModel: ApplyRemoveStoreCreditRequestModel
    ) = rivaApiService.storeCredit(value, language,currency, applyRemoveStoreCreditRequestModel)

    suspend fun rivaCheckout(language:String,currency: String,
        rivaOrderRequest: RivaOrderRequest
    ) = rivaApiService.rivaCheckout(language,currency, rivaOrderRequest)

    suspend fun checkItemStockNoAddress(language:String,currency: String,
        stockRequestModel: StockRequestModel
    ) = rivaApiService.checkItemStockNoAddress(language,currency, stockRequestModel)


    suspend fun cancelOrder(language:String,currency: String,
        orderCancelRequestModel: OrderCancelRequestModel
    ) = rivaApiService.cancelOrder(language,currency, orderCancelRequestModel)

    suspend fun applyCoupon(language:String,currency: String,
        couponRequestModel: CouponRequestModel
    ) = rivaApiService.applyCoupon(language,currency, couponRequestModel)


    // calling from db

    suspend fun addProductToWishlistDb(wishlist: Wishlist) =
        wishlistDao.insertProductToWishlist(wishlist)

    suspend fun removeProductFromWishlistDb(entityId: String) =
        wishlistDao.removeProductFromWishlist(entityId)

    suspend fun deleteAllWishlistItemsDb() = wishlistDao.deleteAllWishlist()

    fun getWishlistProductIdsFromDb() = wishlistDao.getWishlistProductsIds()

    suspend fun getCartCount() = shoppingCartDao.getCartCount()

    suspend fun insertItemToCartDb(cartItem: ShoppingCart) =
        shoppingCartDao.insertItemToCart(cartItem)

    suspend fun addProductToRecentDb(recentProduct: RecentProduct) =
        recentProductDao.insertProductToRecent(recentProduct)

    fun getRecentProductsFromDb() = recentProductDao.getRecentProducts()

    fun getCartItemsFromDb() = shoppingCartDao.getCartProducts()
}
