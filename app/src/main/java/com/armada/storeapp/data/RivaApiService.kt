package com.armada.storeapp.data

import com.armada.storeapp.data.model.StoreData
import com.armada.storeapp.data.model.request.*
import com.armada.storeapp.data.model.response.*
import org.intellij.lang.annotations.Language
import retrofit2.Response
import retrofit2.http.*

interface RivaApiService {

    @GET("stores")
    suspend fun getCountryStores(
        @Query("lang") language: String,
    ): Response<CountryStoreResponse>

    @POST("customers/register")
    suspend fun registerRivaUser(
        @Query("lang") language: String,
        @Query("store") currency: String,
        @Body rivaRegisterUserRequest: RivaRegisterUserRequest
    ): Response<RivaRegisterUserResponse>

    @POST("customers/login")
    suspend fun rivaUserLogin(
        @Query("lang") language: String,
        @Query("store") currency: String,
        @Body rivaLoginRequest: RivaLoginRequest
    ): Response<RivaRegisterUserResponse>


    @POST("customer/address")
    suspend fun addAddress(
        @Query("lang") language: String,
        @Body addAddressRequestModel: AddAddressRequestModel
    ): Response<AddAddressResponseModel>


    @PUT("customer/address/{addressId}")
    suspend fun editAddress(
        @Path("addressId") addressId: String,
        @Query("lang") language: String,
        @Body addAddressRequestModel: AddAddressRequestModel
    ): Response<AddAddressResponseModel>


    @DELETE("customer/address/{addressId}")
    suspend fun deleteAddress(
        @Path("addressId") addressId: String,
        @Query("lang") language: String
    ): Response<DeleteAddressResponseModel>

    @GET("directory/countries")
    suspend fun getCountryList(
        @Query("lang") language: String,
        @Query("all") all:String
    ):Response<CountryListResponseModel>

    @GET("basicprodinfo/{ids}")
    suspend fun getAssociateProducts(@Path("ids") ids: String): Response<AssociateProductResponse>

    @GET("productdetails/{productId}")
    suspend fun getProductDetails(
        @Path("productId") productId: String,
        @Query("lang") language: String,
        @Query("store") currency: String
    ): Response<ProductDetailsResponse>

    @POST("configurable/options")
    suspend fun changeConfig(
        @Body configRequestModel: ConfigRequestModel,
        @Query("lang") language: String
    ): Response<ConfigResponseModel>

    @POST("wishlist/add")
    suspend fun addToWishlist(@Body addToWishList: WishlistRequest.AddToWishList): Response<WishlistResponse>

    @POST("wishlist/delete")
    suspend fun removeFromWishlist(@Body removeFromWishList: WishlistRequest.RemoveFromWishList): Response<WishlistResponse>

    @POST("addtocart")
    suspend fun addToCart(
        @Query("lang") language: String,
        @Query("store") currency: String,
        @Body addToCartRequest: AddToCartRequest
    ): Response<AddToCartResponse>

    @GET("cartlist/{customerId}")
    suspend fun getCartItems(
        @Path("customerId") customerId: String,
        @Query("lang") language: String,
        @Query("store") currency: String
    ): Response<CartItemsResponse>

    @POST("checkItemStock")
    suspend fun checkItemStock(
        @Query("lang") language: String,
        @Query("store")currency: String,
        @Body stockRequestModel: StockRequestModel
    ): Response<CheckStockResponseModel>

    @POST("changeCartItem")
    suspend fun changeCartItem(
        @Query("lang") language: String,
        @Query("store")currency: String,
        @Body updateSizeRequest: UpdateSizeRequest
    ): Response<CartItemsResponse>


    @POST("updateCarts")
    suspend fun updateCart(
        @Query("lang") language: String,
        @Query("store")currency: String,
        @Body updateCartRequest: UpdateCartRequest
    ): Response<CartItemsResponse>

    @POST("deleteCartItem")
    suspend fun deleteCartItem(
        @Query("lang") language: String,
        @Query("store")currency: String,
        @Body deleteCartRequestModel: DeleteCartRequestModel
    ): Response<AddToCartResponse>

    @POST("store-credit/{value}")
    suspend fun storeCredit(
        @Path("value") value: String,
        @Query("lang") language: String,
        @Query("store")currency: String,
        @Body applyRemoveStoreCreditRequestModel: ApplyRemoveStoreCreditRequestModel
    ): Response<ApplyCreditResponseModel>

    @POST("checkout")
    suspend fun rivaCheckout(
        @Query("lang") language: String,
        @Query("store")currency: String,
        @Body rivaOrderRequest: RivaOrderRequest
    ): Response<RivaOrderResponseModel>

    @POST("checkItemStock")
    suspend fun checkItemStockNoAddress(
        @Query("lang") language: String,
        @Query("store")currency: String,
        @Body stockRequestModel: StockRequestModel
    ): Response<StockResponseModelNoAddress>


    @POST("revertOrder")
    suspend fun cancelOrder(
        @Query("lang") language: String,
        @Query("store")currency: String,
        @Body orderCancelRequestModel: OrderCancelRequestModel
    ):Response<Unit>

    @POST("redeemcoupon")
    suspend fun applyCoupon(
        @Query("lang") language: String,
        @Query("store")currency: String,
        @Body couponRequestModel: CouponRequestModel
    ):Response<StockResponseModel>
}