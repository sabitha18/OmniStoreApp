package com.armada.storeapp.ui.home.riva.riva_look_book.bag

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.*
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.databinding.FragmentBagBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.bag.adapter.BagItemAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.checkout.CheckoutActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_item_scan.OmniItemScannerActivity
import com.armada.storeapp.ui.utils.*
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlin.Boolean
import kotlin.Exception
import kotlin.String
import kotlin.collections.ArrayList

@AndroidEntryPoint
class BagFragment : Fragment() {

    private var selectedLanguage: String = "en"
    private var selectedCurrency = "USD"
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    lateinit var binding: FragmentBagBinding
    lateinit var bagViewModel: BagViewModel
    var qtyBottomSheetDialog: BottomSheetDialog? = null
    var sizeBottomSheerDialog: BottomSheetDialog? = null
    private var param1: String? = null
    private var param2: String? = null
    private var cd: ConnectionDetector? = null
    var strUserId = ""
    var isLoggedIn = false
    private var isFromRefresh: Boolean? = false
    private var strParentMultiple_Out = ""
    private var strItemMultiple_Out = ""
    private var isFromOutStck = false
    private var shipping_price = "0"
    private var isNavigateToLogin = false
    private var strMultiple_ItemId: String = ""
    private var strMultiple_ProductId: String = ""
    private var strMultiple_Quantity: String = ""
    private var strProductId: String = ""
    private var strFinal_Price: String = ""
    private var strProductName: String = ""
    private var tax = "0"
    private var strStoreId = ""
    private var arrListGlobal: ArrayList<CartItemsResponse.CartItemModel>? = null
    private var loading: Dialog? = null
    private var strSubtotal = "0"
    private var totalAmount = 0.0

    //    private var loadingView: LoadingView? = null
    private var adapter: BagItemAdapter? = null
    private var strItemId: String = ""
    private var strEntityId: String = ""
    private var strQuantity: String = ""
    private var isFromWish = false
    private var modelStockRequest: StockRequestModel? = null
    private var strSelctedSize = ""
    var layoutManager: NpaGridLayoutManager? = null
    private var isCartApiCancelled = false

    private var strAttributeId = ""
    private var strOptionId = ""
    private var strAttributeId_Color = ""
    private var strOptionId_Color = ""
    private var strAttributeId_Size = ""
    private var strOptionId_Size = ""
    private var strParentId = ""
    private var strConfigProdId = ""
    private var strSize = ""
    private var strConfigOption: String = ""
    private var list_Position = 0
    private var mainArrListCart: ArrayList<AddToCartResponse.Item>? = null
    private var viewPagerParent: ViewPager? = null
    private var addressModel: AddAddressDataModel? = null
    private var rivaLookBookActivity: RivaLookBookActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBagBinding.inflate(inflater)
        bagViewModel =
            ViewModelProvider(this).get(BagViewModel::class.java)
        rivaLookBookActivity = (activity as RivaLookBookActivity)
        setEmptyPage()
        init()
        setOnClickListener()
        return binding.root
    }

    private fun init() {
        mainArrListCart = ArrayList()

        cd = ConnectionDetector(requireContext())
        sharedpreferenceHandler = SharedpreferenceHandler(requireContext())
        strUserId = sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_USER_ID, "")!!
        isLoggedIn =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_USER_LOGGED_IN, false)
        selectedLanguage =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY, "en")!!
        selectedCurrency =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_CURRENCY, "USD")!!
        sharedpreferenceHandler.saveData(SharedpreferenceHandler.PAYMENT_STATUS, "")
        arrListGlobal = ArrayList<CartItemsResponse.CartItemModel>()

        ///setting cart adapter
        layoutManager = NpaGridLayoutManager(
            activity,
            1
        )
        binding.rcyCartList?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        binding.rcyCartList?.layoutManager = layoutManager
        if (strUserId != "") {

            if (cd!!.isConnectingToInternet) {
                getCartItems()
            } else {
                binding.lnrNoItems.visibility = View.VISIBLE
                binding.rcyCartList.visibility = View.GONE
                binding.linBottom.visibility = View.GONE
            }

        }

        binding.swipeRefreshCart.setOnRefreshListener {
            isFromRefresh = true
            binding.swipeRefreshCart.isRefreshing = true
            if (strUserId != "") {
                if (cd!!.isConnectingToInternet)
                    getCartItems()
                else
                    Utils.showSnackbar(binding.root, "Please check your internet connection")
            } else {
                rivaLookBookActivity?.navController?.navigate(R.id.navigation_riva_login)
            }

        }
    }


    private fun setOnClickListener() {

        binding.imageViewBarcodeScan.setOnClickListener {
            val intent = Intent(rivaLookBookActivity, OmniItemScannerActivity::class.java)
            startActivity(intent)
        }

        binding.txtContinue.setOnClickListener {
            rivaLookBookActivity?.navController?.navigate(R.id.navigation_riva_home)
        }

        binding.btnEnquire.setOnClickListener {
            if (isLoggedIn) {
                if (cd!!.isConnectingToInternet)
                    checkStock()
                else Utils.showSnackbarWithAction(
                    binding.root!!,
                    resources.getString(R.string.plz_chk_internet),
                    "Action"
                )
            } else {
                if (rivaLookBookActivity != null)
                    rivaLookBookActivity?.navController?.navigate(R.id.navigation_riva_login)
            }
        }
    }

    private fun setEmptyPage() {
        if (!Utils.emptyCartPage.icon.isNullOrEmpty())
            Glide.with(activity!!).load(Utils.emptyCartPage.icon)
                .into(binding.imgEmptyCart)
        //println("Here i am cart empty "+ Global.emptyCartPage)
        if (!Utils.emptyCartPage.title.isNullOrEmpty()) {
            binding.txtEmpty.text = Utils.emptyCartPage.title
            binding.txtEmpty.visibility = View.VISIBLE
        } else {
            binding.txtEmpty.visibility = View.GONE
        }
        if (!Utils.emptyCartPage.subtitle.isNullOrEmpty()) {
            binding.txtCartNote.text = Utils.emptyCartPage.subtitle
            binding.txtCartNote.visibility = View.VISIBLE
        } else {
            binding.txtCartNote.visibility = View.GONE
        }
    }

    fun getCartItems() {
        bagViewModel.getCartItems(selectedLanguage, selectedCurrency, strUserId)
        bagViewModel.responseCartItems.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    isFromRefresh = false
                    binding.swipeRefreshCart.isRefreshing = false
                    if (it?.data?.items?.size == 0) {
                        sharedpreferenceHandler.saveData(
                            SharedpreferenceHandler.RIVA_USER_ADDRESS,
                            ""
                        )
                        sharedpreferenceHandler.saveData(SharedpreferenceHandler.CART_COUNT, 0)
                        updateBadgeCart()
                    }

                    handleCartListItems(it.data!!)
                    arrListGlobal = it.data.items
                    dismissProgress()
                }

                is Resource.Error -> {
                    binding?.lnrNoItems?.visibility = View.VISIBLE
                    isFromRefresh = false
                    sharedpreferenceHandler.saveData(SharedpreferenceHandler.CART_COUNT, 0)
                    updateBadgeCart()
                    binding.rcyCartList?.visibility = View.GONE
                    binding?.linBottom?.visibility = View.GONE
                    if (binding.swipeRefreshCart != null && binding.swipeRefreshCart.isRefreshing) {
                        binding.swipeRefreshCart.isRefreshing = false
                    }
                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    private fun handleCartListItems(cartModel: CartItemsResponse) {
        if (cartModel.status == 200) {

            if (cartModel?.items?.size > 0) {
                binding.lnrNoItems.visibility = View.GONE
                binding.linBottom.visibility = View.VISIBLE
                mainArrListCart?.clear()
                if (binding.swipeRefreshCart != null && binding.swipeRefreshCart.isRefreshing) {
                    binding.swipeRefreshCart.isRefreshing = false
                }
                isFromRefresh = false
                val arrListItems = ArrayList<AddToCartResponse.Item>()
                val arryListProduct = ArrayList<AddToCartResponse>()
                var bagCount = 0
                cartModel.items.forEach { itemCart ->
                    bagCount += itemCart?.qty!!

                    var strConDbOptions = ""
                    val arrListConfig =
                        ArrayList<AddToCartResponse.Item.AddToCartConfigurableOption>()

                    if (itemCart.configurable_option != null) {
                        for (i in 0 until itemCart.configurable_option.size) {

                            val objConOption = itemCart.configurable_option[i]
                            strConDbOptions =
                                strConDbOptions + objConOption.type + " : " + objConOption.attributes?.value + ","

                            val attrModel =
                                AddToCartResponse.Item.AddToCartConfigurableOption.AddToCartAttributes(
                                    objConOption.attributes?.value ?: "",
                                    objConOption.attributes?.option_id ?: "",
                                    objConOption.attributes?.attribute_image_url ?: ""
                                )

                            val configModel =
                                AddToCartResponse.Item.AddToCartConfigurableOption(
                                    objConOption.type ?: "",
                                    (objConOption.attribute_id ?: 0).toString(),
                                    objConOption.attribute_code ?: "",
                                    attrModel
                                )

                            arrListConfig.add(configModel)
                        }
                    }

                    val productData = AddToCartResponse.Item(
                        itemCart.item_id ?: "",
                        itemCart.id ?: "",
                        itemCart.short_description ?: "",
                        itemCart.name ?: "",
                        itemCart.image ?: "",
                        itemCart.price ?: "",
                        itemCart.final_price ?: "",
                        itemCart.qty ?: 0,
                        itemCart.remaining_qty ?: 0,
                        "",
                        itemCart.is_salable ?: false,
                        strConDbOptions.toUpperCase(),
                        arrListConfig,
                        (itemCart.parent_id ?: 0).toString()
                    )

                    // For out of stock products
                    if (itemCart.is_salable != true) {
                        if (strItemMultiple_Out.length > 0) {
                            strItemMultiple_Out =
                                strItemMultiple_Out + "," + itemCart.item_id
                            strParentMultiple_Out =
                                strParentMultiple_Out + "," + itemCart.parent_id
                        } else {
                            strItemMultiple_Out = itemCart.item_id ?: ""
                            strParentMultiple_Out = itemCart.parent_id ?: ""
                        }

                    }

                    arrListItems.add(productData)
                }

                sharedpreferenceHandler.saveData(SharedpreferenceHandler.CART_COUNT, bagCount)
                updateBadgeCart()

                if (cartModel.totals != null) {
                    tax = (cartModel.totals.tax ?: "0").toString()
                    strSubtotal = (cartModel.totals.sub_total ?: 0.0).toString()
                    shipping_price = (cartModel.totals.shipping_amount ?: 0.0).toString()
                    try {
                        totalAmount = 0.0
                        for (i in 0 until cartModel.items.size) {
                            totalAmount += ((cartModel.items[i].final_price?.toDouble()
                                ?: 0.0) * (cartModel.items[i].qty ?: 1))
                        }
                    } catch (e: Exception) {
                        totalAmount = cartModel.totals.total ?: 0.0
                    }

                }

                var items: AddToCartResponse? = null
                items = AddToCartResponse("", "", "", arrListItems)
                arryListProduct.add(items)

                if (isCartApiCancelled) {
                    dismissProgress()
                    isCartApiCancelled = false
                    return
                }
                mainArrListCart = arryListProduct[0].items
                addressModel
                //println("Here i am cart api aaa")
                binding.lnrNoItems?.visibility = View.GONE
                binding.rcyCartList?.visibility = View.VISIBLE
                binding.linBottom?.visibility = View.VISIBLE


                var totalItems = 0
                for (cartItem in mainArrListCart!!) {
                    totalItems += cartItem.quantity
                }
                //setBagCount todo
//                if (totalItems > 0) {
//                    (activity as CartActivity).setBagCount(
//                        resources.getString(
//                            R.string.your_bag,
//                            totalItems.toString()
//                        )
//                    )
                var currentActivity: FragmentActivity? = null
                adapter = BagItemAdapter(
                    rivaLookBookActivity!!,
                    selectedCurrency,
                    null
                )
//                adapter?.onSizeChanged = { cartItem ->
//
//                    strParentId = cartItem?.parent_id
//                    strItemId = cartItem?.item_id
//                    strEntityId = cartItem?.entity_id
//
//                    strAttributeId = cartItem?.configurable_option[0].attribute_id
//                    strOptionId = cartItem?.configurable_option[0].attributes.option_id
//
//                    strAttributeId_Color = cartItem?.configurable_option[0].attribute_id
//                    strOptionId_Color = cartItem?.configurable_option[0].attributes.option_id
//
//                    strConfigOption = cartItem?.cnfgDBOPtion
//                    strQuantity = cartItem?.quantity.toString()
//
////                    strSelctedSize = holder.cartListSwipeBinding.txtSize.text.toString()
//
//                    changeConfig(strParentId, strAttributeId, strOptionId, false)
//
//                }
//                adapter?.onQtyChanged = { cartItem, position ->
//                    showQtyPicker(cartItem, position)
//                }
//                adapter?.onDeleteClicked = { cartItem ->
//                    strItemId = cartItem.item_id
//                    deleteCart()
//                }
                binding.rcyCartList?.adapter = adapter

                setTotalAmt(mainArrListCart!!)
                dismissProgress()

            } else {
                binding.lnrNoItems.visibility = View.VISIBLE
                binding.linBottom.visibility = View.GONE
                sharedpreferenceHandler.saveData(SharedpreferenceHandler.INVOICE_ID, "")
                sharedpreferenceHandler.saveData(SharedpreferenceHandler.CART_COUNT, 0)
                updateBadgeCart()
            }
        } else {
            binding.lnrNoItems.visibility = View.VISIBLE
            binding.linBottom.visibility = View.GONE
            sharedpreferenceHandler.saveData(SharedpreferenceHandler.CART_COUNT, 0)

            sharedpreferenceHandler.saveData(SharedpreferenceHandler.INVOICE_ID, "")
            updateBadgeCart()
        }
    }

    private fun showQtyPicker(cartItem: AddToCartResponse.Item, position: Int) {
        val parentView =
            activity?.layoutInflater?.inflate(R.layout.layout_bottom_dialog, null)
        val wheelView = parentView?.findViewById(R.id.wheelView) as WheelView
        val txtDone = parentView?.findViewById(R.id.txtDone) as TextView
        val txtCancel = parentView?.findViewById(R.id.txtCancel) as TextView
        val arrayListData: java.util.ArrayList<String> = java.util.ArrayList()

        qtyBottomSheetDialog = null
        if (qtyBottomSheetDialog == null) {
            qtyBottomSheetDialog = BottomSheetDialog(activity!!)
            qtyBottomSheetDialog?.setContentView(parentView!!)
            qtyBottomSheetDialog?.setCanceledOnTouchOutside(false)
            qtyBottomSheetDialog?.setCancelable(false)
        }

        for (j in 1 until cartItem?.qty_available + 1) {
            arrayListData.add(j.toString())
            if (j == 5) {
                break
            }
        }

        wheelView.setItems(arrayListData)
        val index = cartItem?.quantity
        /* if (index != 0)
             wheelView.setSeletion(index - 1)
         else*/
        wheelView.setSeletion(0)

        txtCancel.setOnClickListener {
            qtyBottomSheetDialog?.dismiss()
        }
        txtDone.setOnClickListener {
            qtyBottomSheetDialog?.dismiss()
            if (arrayListData != null && arrayListData.size != 0) {
                if (wheelView.seletedIndex <= arrayListData.size) {
//                                holder.cartListSwipeBinding.txtQty.text =
//                                    arrayListData[wheelView.seletedIndex]
                    strItemId = cartItem?.item_id
                    strQuantity = arrayListData[wheelView.seletedIndex]

                    if (!strUserId.equals("") && Utils.hasInternetConnection(activity)) {
//                        list_Position =
//                            position + 1 /// Adding one becasue on refreshing we ae setting -1 position for delete no ned for update
                        updateQty()
                    } else {
                        adapter!!.notifyDataSetChanged()

                    }
                }
            }
        }
        if (qtyBottomSheetDialog != null && !qtyBottomSheetDialog?.isShowing!!)
            qtyBottomSheetDialog?.show()
    }

    //check stock api call
    private fun checkStock() {
        if (activity != null) {
            strMultiple_ProductId = ""
            strMultiple_ItemId = ""
            strMultiple_Quantity = ""

            arrListGlobal?.forEach {
                /////////////////////////////////////Product Ids /////////////////////////////////////

                strMultiple_ProductId = if (strMultiple_ProductId.isNotEmpty()) {
                    strMultiple_ProductId + "," + it?.id
                } else it?.id!!

                /////////////////////////////////////Item Ids /////////////////////////////////////

                strMultiple_ItemId = if (strMultiple_ItemId.isNotEmpty())
                    strMultiple_ItemId + "," + it?.item_id
                else it?.item_id!!

                /////////////////////////////////////Quantity /////////////////////////////////////

                strMultiple_Quantity = if (strMultiple_Quantity.isNotEmpty())
                    strMultiple_Quantity + "," + it?.qty?.toShort()
                else it?.qty.toString()
            }

            val cartModel = StockRequestModel(
                strUserId,
                "",
                "",
                "tap"
            )

            modelStockRequest =
                cartModel  ///// To get updated prices checkout page by changing address
            checkStockApi(cartModel!!)
        }
    }

    fun checkStockApi(stockRequestModel: StockRequestModel) {
        bagViewModel.checkItemStock(selectedLanguage, selectedCurrency, stockRequestModel)
        bagViewModel.responseCheckStock.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    handleStockCart(it.data!!)
                    dismissProgress()
                }

                is Resource.Error -> {
                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    private fun handleStockCart(responseModel: CheckStockResponseModel) {
        if (responseModel.status == 200 && responseModel.data!!.items!!.size > 0) {
            val stockModel = responseModel.data
            dismissProgress()

            val intent = Intent(activity, CheckoutActivity::class.java)
            intent.putExtra("model", responseModel.data)
            intent.putExtra("model_request", modelStockRequest)
            startActivity(intent)


        } else if (responseModel.status == 355) {
            dismissProgress()
            ///Out of stock
            Utils.showSnackbarWithAction(
                binding.root!!,
                responseModel.message!!,
                "Action"
            )
        } else {
            dismissProgress()

            Utils.showSnackbarWithAction(
                binding.root!!,
                responseModel.message!!,
                "Action"
            )
        }

    }

    ///set total amount
    private fun setTotalAmt(arrList: ArrayList<AddToCartResponse.Item>) {
        var totalItems = 0

        for (i in 0 until arrList.size) {
            totalItems += arrList[i].quantity
        }

//        if (totalItems == 1) {
//            binding.txtTotalItems?.text =
//                ((resources.getString(R.string.item) + " (" + totalItems.toString()) + ")")
//        } else if (totalItems > 1) {
//            binding.txtTotalItems?.text =
//                ((resources.getString(R.string.items) + " (" + totalItems.toString()) + ")")
//        }

        if (!isLoggedIn) {
            var totalAmt = 0.0
            for (i in arrList.indices) {
                val itemAmt =
                    java.lang.Double.parseDouble(arrList[i].final_price) * arrList[i].quantity // Calc final price of all items and display
                totalAmt += itemAmt
            }

//            binding.txtSubtotal?.text =
//                (Utils.getPriceFormatted(totalAmt.toString(), selectedCurrency))
//
//            binding.lnrShippingCharges?.visibility = View.GONE
            binding.txtCartTotal?.text =
                (Utils.getPriceFormatted(totalAmt.toString(), selectedCurrency))
            //  if (!myApp!!.isLangArebic) {
//            binding.txtTotalAmount?.text =
//                (Utils.getPriceFormatted(totalAmt.toString()))
        } else {
            binding.txtCartTotal?.text =
                (Utils.getPriceFormatted(totalAmount.toString(), selectedCurrency))
//            binding.txtTotalAmount?.text = (Utils.getPriceFormatted(strSubtotal))
        }

    }


    private fun showProgress() {
        try {
            if (loading == null) {
                loading = Dialog(
                    requireContext(),
                    R.style.TranslucentDialog
                )
                loading!!.setContentView(R.layout.custom_loading_view)
                loading!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loading!!.setCanceledOnTouchOutside(false)
                if (!loading!!.isShowing)
                    loading!!.show()
            }

            if (loading != null && !loading!!.isShowing)
                loading!!.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    ///dismiss loading
    private fun dismissProgress() {

        try {
            if (loading != null)
                loading!!.dismiss()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    open class SingleTapConfirm : SimpleOnGestureListener() {
        override fun onSingleTapUp(event: MotionEvent): Boolean {
            return true
        }
    }

    fun changeConfig(
        strProductId: String?,
        attributeId: String,
        optionId: String,
        isUpdateCartSize: Boolean
    ) {
        showProgress()
        val configAttr = ConfigRequestModel(
            strProductId!!,
            Utils.removeLastCharacter(attributeId),
            Utils.removeLastCharacter(optionId),
            "kuwait_en"
        )
        bagViewModel.changeConfig(selectedLanguage, configAttr)
        bagViewModel.responseChangeConfig.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    handleChangeConfigResponse(it.data!!, isUpdateCartSize)
                    dismissProgress()
                }

                is Resource.Error -> {
                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }

    }

    fun handleChangeConfigResponse(
        responseModel: ConfigResponseModel?,
        isUpdateCartSize: Boolean
    ) {
        dismissProgress()
        strConfigProdId = ""  // to reset each time when user seclect config options
        try {
            val modelCompareConfig = responseModel?.data?.get(0)
            val arrConfigOption: ArrayList<ProductDetailsConfigurableOption> =
                ArrayList()
            val arrLstAttributes = modelCompareConfig?.attributes
            this.strProductId = modelCompareConfig?.entity_id ?: ""
            if (isUpdateCartSize) {
                updateSize(strItemId, this.strProductId, strParentId)
            } else {
                if (modelCompareConfig?.type != null) {
                    val arrAttributes: ArrayList<ProductDetailsAttribute> =
                        ArrayList()
                    for (k in 0 until arrLstAttributes?.size!!) {

                        if (!arrLstAttributes[k].images.isNullOrEmpty()) {
                            val arrListImages = ArrayList<String>()
                            arrLstAttributes[k].images?.forEach { image ->
                                arrListImages.add(image)
                            }
                        }
                        val images = ArrayList<String>()
                        val modelAttribute = ProductDetailsAttribute(
                            "",
                            "",
                            images,
                            arrLstAttributes[k].option_id!!,
                            "",
                            false,
                            arrLstAttributes[k].value!!,
                            false, true
                        )
                        arrAttributes.add(modelAttribute)
                    }
                    val modelColor = ProductDetailsConfigurableOption(
                        modelCompareConfig.attribute_code!!,
                        modelCompareConfig.attribute_id!!.toInt(),
                        arrAttributes,
                        modelCompareConfig.type,
                        "No"
                    )
                    arrConfigOption.add(modelColor)

                    showBottomSheetConfigPicker(arrConfigOption, strProductId ?: "")
                } else {
                    strConfigProdId = modelCompareConfig?.entity_id ?: ""
                    strConfigOption = strConfigOption.split(",")[0].trim()
                    adapter?.notifyDataSetChanged()
                }
            }

        } catch (e: Exception) {
            dismissProgress()
            e.printStackTrace()
        }
    }

    private fun showBottomSheetConfigPicker(
        arrLstCompareConfig: ArrayList<ProductDetailsConfigurableOption>,
        parentId: String
    ) {
        val parentView = layoutInflater.inflate(R.layout.layout_bottom_dialog, null)
        val wheelView = parentView.findViewById(R.id.wheelView) as WheelView
        val txtDone = parentView.findViewById(R.id.txtDone) as TextView
        val txtCancel = parentView.findViewById(R.id.txtCancel) as TextView

        if (sizeBottomSheerDialog == null) {
            sizeBottomSheerDialog =
                BottomSheetDialog(requireContext())
            sizeBottomSheerDialog?.setContentView(parentView)
            sizeBottomSheerDialog?.setCanceledOnTouchOutside(false)
            sizeBottomSheerDialog?.setCancelable(false)
        }
        val arrListSize: ArrayList<String> = ArrayList()
        arrLstCompareConfig[0].attributes?.forEach {
            arrListSize.add(it.value.toString())
        }

        wheelView.setItems(arrListSize)

        var index = 0
        for (i in 0 until arrListSize.size) {
            if (strSelctedSize.trim().equals(arrListSize[i].trim())) {
                index = i
                break
            }
        }

        if (index <= 0) wheelView.setSeletion(0)
        else wheelView.setSeletion(index)

        txtCancel.setOnClickListener {
            sizeBottomSheerDialog?.dismiss()
        }

        txtDone.setOnClickListener {

            sizeBottomSheerDialog?.dismiss()
            val post = wheelView.seletedIndex
            strAttributeId = strAttributeId + "," + arrLstCompareConfig[0].attribute_id
            strOptionId =
                strOptionId + "," + arrLstCompareConfig[0].attributes?.get(post)?.option_id
                    ?: ""
            strSize = arrLstCompareConfig[0].attributes?.get(post)?.value ?: ""

            if (cd?.isConnectingToInternet == true) {
                if (strUserId.isNotEmpty()) {
                    changeConfig(strParentId, strAttributeId, strOptionId, true)
                } else {
                    changeConfig(strParentId, strAttributeId, strOptionId, false)
                }
            }
        }
        if (sizeBottomSheerDialog != null && !sizeBottomSheerDialog?.isShowing!!)
            sizeBottomSheerDialog?.show()
    }

    //update product size
    private fun updateSize(cartItemId: String, newProductId: String, parentId: String) {

        showProgress()
        val cartModel = UpdateSizeRequest(
            strUserId,
            cartItemId,
            newProductId,
            parentId,
            "1"
        )

        bagViewModel.changeCartItem(selectedLanguage, selectedCurrency, cartModel)
        bagViewModel.responseChangeCartItem.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    handleCart(it.data!!)
                    dismissProgress()
                }

                is Resource.Error -> {
                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    private fun handleCart(cartModel: CartItemsResponse) {
        if (cartModel.status == 200) {
            if (strUserId != "") {
                if (cd!!.isConnectingToInternet) {
                    isFromRefresh = true
                    getCartItems()
                } else {
                    dismissProgress()
                }
            } else {
                dismissProgress()
            }
        } else if (cartModel.status == 400) {
            dismissProgress()
            Utils.showSnackbar(binding.root!!, cartModel.message.toString())
        } else {
            Utils.showSnackbar(binding.root!!, cartModel.message.toString())
            dismissProgress()
            getCartItems()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {

    }

    override fun onResume() {
        super.onResume()
        strUserId = sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_USER_ID, "")!!
        getCartItems()
        updateBadgeCart()

    }

    fun updateBadgeCart() {
        rivaLookBookActivity?.setBagCount()

    }


    ///update qty api call
    private fun updateQty() {
        val cartModel = UpdateCartRequest(Cart(strUserId, strItemId, strQuantity))

        bagViewModel.updateCart(selectedLanguage, selectedCurrency, cartModel)
        bagViewModel.responseUpdateCart.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    handleCart(it.data!!)
                    dismissProgress()
                }

                is Resource.Error -> {
                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    private fun deleteCart() {
        val cartModel = DeleteCartRequestModel(
            strUserId,
            strItemId
        )
        bagViewModel.deleteCartItem(selectedLanguage, selectedCurrency, cartModel)
        bagViewModel.responseDeleteCartItem.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    deleteCart(it.data!!)
                    dismissProgress()
                }

                is Resource.Error -> {
                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    private fun deleteCart(cartModel: AddToCartResponse) {
        if (cartModel.items != null) {
            mainArrListCart!!.clear()
            mainArrListCart = cartModel.items
        }
        if (cartModel.status == "200" && mainArrListCart!!.size > 0) {
            binding.lnrNoItems.visibility = View.GONE
            if (strUserId != "") {
                if (cd!!.isConnectingToInternet) {
                    isFromRefresh = true
                    getCartItems()
                } else {
                    dismissProgress()
                }
            } else {
                dismissProgress()
            }

        } else {
            dismissProgress()
            binding.lnrNoItems.visibility = View.VISIBLE
            sharedpreferenceHandler.saveData(SharedpreferenceHandler.CART_COUNT, 0)
            updateBadgeCart()

            binding.rcyCartList.visibility = View.GONE
            binding.linBottom.visibility = View.GONE

        }
        Utils.showSnackbarWithAction(
            binding.root!!,
            cartModel.message,
            "Action"
        )

    }
}