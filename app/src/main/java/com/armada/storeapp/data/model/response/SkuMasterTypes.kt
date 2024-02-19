package com.armada.storeapp.data.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "omni_product")
data class SkuMasterTypes(
    val active: Boolean?,
    val afSegamationName: String?,
//    val appVersion: Any,
    val arabicSKU: String?,
//    val armadaCollectionCode: Any,
    val armadaCollectionID: Int?,
    val barCode: String?,
    val barCodeID: Int?,
    val barCodeRunningNo: Int?,
//    val baseEntry: Any,
//    val binCode: Any,
    val brandCode: String?,
    val brandID: Int?,
//    val brandName: Any,
//    val brandShortCode: Any,
//    val collectionCode: Any,
    val collectionID: Int?,
//    val collectionName: Any,
//    val colorCode: Any,
    val colorID: Int?,
    var imageUrl: String?,
    val colorName: String?,
//    val createBy: Any,
//    val createOn: Any,
//    val createdByUserName: Any,
    val defaultPrice: Int?,
//    val description: Any,
//    val designCode: Any,
    val designID: Int?,
//    val designerCode: Any,
    val designerID: Int?,
//    val divisionCode: Any,
    val divisionID: Int?,
//    val divisionName: Any,
    val exchangeRate: Int?,
    @PrimaryKey(
        autoGenerate = true
    )
    var id: Int,
    var productId:String,
    var fromRiva:Boolean?=false,

    var quantity: Int?,
//    val importExcelList: Any,
    val isComboItem: Boolean?,
    val isCountrySync: Boolean?,
    val isHeaderItem: Boolean?,
    val isNonTrading: Boolean?,
    val isServerSync: Boolean?,
    val isStoreSync: Boolean?,
//    val itemImageMasterList: Any,
    val orderID: Int?,
//    val origin: Any,
//    val priceListID: Any,
//    val productCode: Any,
//    val productGroupCode: Any,
    val productGroupID: Int?,
    val productGroupName: String?,
//    val productLineCode: Any,
    val productLineID: Int?,
//    val productSubGroupCode: Any,
    val productSubGroupID: Int?,
//    val productSubGroupName: Any,
    val promotionApplied: Boolean?,
    val purchaseCurrencyID: Int?,
    val purchasePrice: Int?,
//    val purchasePriceCurrencyCode: Any,
//    val purchasePriceListCode: Any,
    val purchasePriceListID: Int?,
//    val remarks: Any,
//    val rrpCurrencyCode: Any,
    val rrpCurrencyID: Int?,
    val rrpPrice: Int?,
    val salePriceListID: Int?,
    val salesPrice: Int?,
    val salesPriceListID: Int?,
//    val scaleCode: Any,
    val scaleDetailMasterID: Int?,
    val scaleID: Int?,
//    val scaleName: Any,
//    val scn: Any,
//    val seasonCode: Any,
    val seasonID: Int?,
    val seasonName: String?,
    val segamentationID: Int?,
//    val segmentationCode: Any,
    val serialNo: Int?,
//    val shortDescription: Any,
//    val sizeCode: Any,
    val sizeID: Int?,
    val sizeName: String?,
    val skuCode: String?,
    val skuImage: String?,
    val skuImageSource: String?,
//    val skuImageUrl: Any,
//    val skuMasterTypesRecord: Any,
//    val skuName: Any,
    val skuid: Int?,
    val stock: Int?,
    var status :String?,
//    val storeCode: Any,
    val styleCode: String?,
    val styleID: Int?,
//    val styleName: Any,
    var stylePrice: Double?,
//    val stylePricingList: Any,
//    val styleStatusCode: Any,
    val styleStatusID: Int?,
    val subBrandCode: String?,
    val subBrandID: Int?,
//    val subBrandName: Any,
    val supplierBarcode: String?,
    val tag_Id: String?,
//    val updateBy: Any,
//    val updateOn: Any,
//    val updatedByUserName: Any,
//    val useSeperator: Any,
    val year: String?,
//    val yearCode: Any,
    val yearID: Int?,
    var availableQty: Int?=0,
    var availabilityStatus: Boolean?=false,
    var orig_price: Double,
    val fromStoreQty: Int,
    val tpStoreQty: Int
) : Serializable