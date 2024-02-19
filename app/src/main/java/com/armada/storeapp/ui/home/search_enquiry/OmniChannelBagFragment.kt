package com.armada.storeapp.ui.home.search_enquiry

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.OMNIOrderDetailX
import com.armada.storeapp.data.model.request.OrderInvoiceRequest
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.databinding.FragmentOmnichannelSearchBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.bag.adapter.BagItemAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.bag.adapter.ModelItemAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.bag.adapter.SkuItemAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.OrderPlaceViewModel
import com.armada.storeapp.ui.home.riva.riva_look_book.search.BarcodeScanner.BarcodeScannerActivity
import com.armada.storeapp.ui.utils.*
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class OmniChannelBagFragment : Fragment() {

    var omniScannedItem: ScannedItemDetailsResponse? = null
    private var selectedLanguage: String = "en"
    private var selectedCurrency = ""
    lateinit var orderPlaceViewModel: OrderPlaceViewModel
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    lateinit var binding: FragmentOmnichannelSearchBinding
    lateinit var enquiryViewModel: EnquiryViewModel

    //    var itemList: ArrayList<SkuMasterTypes>? = null
    var itemStockMap = HashMap<String, OmniStockResponse>()
    private var cd: ConnectionDetector? = null
    var selectedDeliveryMethod = ""

    var itemList = ArrayList<SkuMasterTypes>()

    //    var strUserId = ""
    var isLoggedIn = false
    private var isFromRefresh: Boolean? = false

    private var loading: Dialog? = null
    var storeCode = ""
    var storeId = 0
    private var strSubtotal = "0"
    var omniOrderInvoice: OmniInvoiceResponse? = null
    private var totalAmount = 0.0
    var origmon = 0.0
    var accessToken = ""

    private var adapter: BagItemAdapter? = null
    private var adapterSku: SkuItemAdapter? = null
    var layoutManager: NpaGridLayoutManager? = null
    private var mainActivity: MainActivity? = null
    var currentCheckingSkuCode = ""
    lateinit var modelItemAdapter: ModelItemAdapter

    var localItemList = ArrayList<SkuMasterTypes>()

    var startForResult: ActivityResultLauncher<Intent>? = null
    var currentSearchEditTextLength = 0
    private var isUsingScanningDevice: Boolean = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result
                        .data
                    val barcode = intent?.getStringExtra("barcode")
                    println(" working 5---------")
                    omniScanItem(barcode!!)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOmnichannelSearchBinding.inflate(inflater)
        enquiryViewModel =
            ViewModelProvider(this).get(EnquiryViewModel::class.java)
        orderPlaceViewModel =
            ViewModelProvider(this).get(OrderPlaceViewModel::class.java)
        mainActivity = (activity as MainActivity)
//        setEmptyPage()
        init()
        setOnClickListener()
        return binding.root
    }

    private fun init() {

        cd = ConnectionDetector(requireContext())
        sharedpreferenceHandler = SharedpreferenceHandler(requireContext())
        isLoggedIn =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_USER_LOGGED_IN, false)
        selectedLanguage =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY, "en")!!
        selectedCurrency =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_CURRENCY, "USD")!!
        sharedpreferenceHandler.saveData(SharedpreferenceHandler.PAYMENT_STATUS, "")

        layoutManager = NpaGridLayoutManager(
            activity,
            1
        )
        binding.rcyCartList?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        binding.rcyCartList?.layoutManager = layoutManager

        adapter = BagItemAdapter(
            mainActivity!!,
            selectedCurrency,
            localItemList
        )
        adapter?.onQtyChanged = { cartItem ->
            var totalQty = 0
            localItemList?.forEach {
                if (it.skuCode.equals(cartItem.skuCode)) {
                    it.quantity = cartItem.quantity
                }
                totalQty += it.quantity!!
            }
            setTotalAmt(localItemList!!)
            binding.tvQty.text = totalQty.toString()
        }
        adapter?.onDeleteClicked = { cartItem ->
            println(" working 6---------")
            showDeleteProductDialog(cartItem?.skuCode!!)


        }
        adapterSku?.onDeleteClicked = { cartItem ->
            omniScanItem(cartItem?.skuCode!!)


        }
        binding.rcyCartList?.adapter = adapter
        mainActivity?.BackPressed(this)
    }


    fun setAdapter() {
        adapter?.setAdapter(localItemList)
    }


    private fun setOnClickListener() {

        binding.radioBtnStorePickup.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked)
                selectedDeliveryMethod = "STOREPICKUP"
            else
                selectedDeliveryMethod = "HOME DELIVERY"
        }

        binding.radioBtnHomeDelivery.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked)
                selectedDeliveryMethod = "HOME DELIVERY"
            else
                selectedDeliveryMethod = "STOREPICKUP"
        }

        binding.btnEnquire.setOnClickListener {
            saveItemsToSharedPrefs()
            if (selectedDeliveryMethod.isNullOrEmpty())
                Utils.showSnackbar(binding.root, "Please select any delivery method to proceed")
            else {
                navigateToOrderPlace()
            }

        }
        binding.imageViewBarcodeScan.setOnClickListener {
            saveItemsToSharedPrefs()
//            val intent = Intent(mainActivity, OmniItemScannerActivity::class.java)
//            startActivityForResult(intent, 303)
            val intent = Intent(mainActivity, BarcodeScannerActivity::class.java)
            startForResult?.launch(intent)
        }

        binding.editText?.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                isUsingScanningDevice = true
            }
        })

        binding?.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {

                val length = text?.length!!
                if (currentSearchEditTextLength - length == 1)
                    isUsingScanningDevice = false
                else if (length - currentSearchEditTextLength == 1)
                    isUsingScanningDevice = false
                else if (length == 0)
                    isUsingScanningDevice = true
                if (isUsingScanningDevice)
                {
                    //searchModelStyle(text.toString())
                    println(" working 1---------")
                    if(text.toString() != "") {
                        if (text.toString().length == 12){
                            //call api style code
                            mainActivity?.showProgressBar(true)
                            searchModelStyle(text.toString())
                            println("call api ---------------------  ")
                        }else{
                            omniScanItem(text.toString())
                        }

                    }
                } else

                    // new code
                    if (text.toString().length == 12){
                        //call api style code
                        mainActivity?.showProgressBar(true)
                        searchModelStyle(text.toString())
                        println("call api ---------------------  ")
                    }else{
                        if (text.toString().length == 20) {
                            omniScanItem(text.toString())
                        }
                      // searchModelStyle(text.toString())
//351101-18039
                    }

//                binding.imageViewSubmit.visibility = View.VISIBLE
                binding.imageViewBarcodeScan.visibility = View.GONE

                if (text?.length!! == 0) {
                    binding.recyclerViewModel.visibility = View.GONE
                }
            }

            override fun afterTextChanged(text: Editable?) {

                if (text?.length!! > 0) {
//                    binding.imageViewSubmit.visibility = View.VISIBLE
                    binding.imageViewBarcodeScan.visibility = View.GONE
                } else {
                    binding.recyclerViewModel.visibility = View.GONE
//                    binding.imageViewSubmit.visibility = View.GONE
                    binding.imageViewBarcodeScan.visibility = View.VISIBLE
                }

                currentSearchEditTextLength = text?.length!!
                if (text?.toString()?.equals("") == false) {
                    if (text?.length == 1) {
                        isUsingScanningDevice = false
                    }
//                    if (isUsingScanningDevice && text.toString()
//                            .isDigitsOnly() && (text.toString().length!! == 13 || text.toString().length!! == 6)
//                    ) {
//                        omniScanItem(text.toString())
//                    } else
//                        searchModel(text.toString())
                } else
                    isUsingScanningDevice = true
            }

        })
        binding.imageViewSubmit.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            val sku = binding.editText.text.toString()
            println(" working 2---------")
            omniScanItem(sku)
        }
    }

    override fun onResume() {
        super.onResume()
    }

//    fun getCartItems() {
//        mainActivity?.showProgressBar(true)
//        enquiryViewModel.getAllOmniProduct(mainActivity!!).let {
//            if (it.isNullOrEmpty()) {
//                isFromRefresh = false
//                sharedpreferenceHandler.saveData(SharedpreferenceHandler.SEARCH_COUNT, 0)
//                binding.rcyCartList?.visibility = View.GONE
//                binding?.linBottom?.visibility = View.GONE
//                if (binding.swipeRefreshCart != null && binding.swipeRefreshCart.isRefreshing) {
//                    binding.swipeRefreshCart.isRefreshing = false
//                }
//                mainActivity?.showProgressBar(false)
//            } else {
//                itemList = it
////                itemList?.forEach {
////                    it.availableQty = 0
////                }
//                var grouped = itemList?.groupBy { it.skuCode }
//                itemList?.clear()
//                grouped?.forEach { entry ->
//                    var qty = 0
//                    entry.value.forEach {
//                        qty += it.quantity!!
//                    }
//                    var skuitem = entry?.value?.get(0)
//                    skuitem.quantity = qty
//                    skuitem.availableQty = 0
//                    skuitem.availabilityStatus = false
//                    itemList?.add(skuitem)
//                }
//                handleCartListItems(itemList!!)
//                binding.linBottom.visibility = View.VISIBLE
//                if (binding.swipeRefreshCart != null && binding.swipeRefreshCart.isRefreshing) {
//                    binding.swipeRefreshCart.isRefreshing = false
//                }
//                isFromRefresh = false
//            }
//        }
//    }

    private fun handleCartListItems(list: ArrayList<SkuMasterTypes>) {

        if (list?.size > 0) {

            isFromRefresh = false

            var bagCount = list.size
            sharedpreferenceHandler.saveData(SharedpreferenceHandler.SEARCH_COUNT, bagCount)

            binding.rcyCartList?.visibility = View.VISIBLE
            binding.linBottom?.visibility = View.VISIBLE

            setAdapter()

            setTotalAmt(list)
            mainActivity?.showProgressBar(false)

            localItemList?.forEach {
                currentCheckingSkuCode = it.skuCode!!
                checkOmniStock()
            }

        } else {
            binding.linBottom.visibility = View.GONE
            sharedpreferenceHandler.saveData(SharedpreferenceHandler.INVOICE_ID, "")
            sharedpreferenceHandler.saveData(SharedpreferenceHandler.SEARCH_COUNT, 0)
        }
    }

    override fun onStop() {
        super.onStop()
//        saveItemsToSharedPrefs()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun checkOmniStock() {
        mainActivity?.showProgressBar(false)
        val countryId = sharedpreferenceHandler.getData(SharedpreferenceHandler.COUNTRY_ID, 0)
        val countryCode = sharedpreferenceHandler.getData(SharedpreferenceHandler.COUNTRY_CODE, "")
        val storeCode = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_CODE, "")

        enquiryViewModel.checkOmniStock(
            currentCheckingSkuCode,
            countryId!!.toString(),
            storeCode!!,
            storeCode,
            countryCode!!
        )
        enquiryViewModel.responseOmniStock.observe(mainActivity!!) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    if (it.data?.statusCode == 1) {
                        try {
                            itemStockMap.put(
                                it.data.omniStoreStockCheckResponse.get(0).skuCode,
                                it.data!!
                            )

                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }

                    }
                    var noOfItemStockChecked = 0
                    itemStockMap.forEach { sku, stockCheckResponse ->
                        noOfItemStockChecked++
                    }
                    if (noOfItemStockChecked == localItemList?.size) {
                        handleOmniStockResponse()
                    }


                }

                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    try {
                        val jsonObj = JSONObject(it.message)
                        val message = jsonObj.getString("message")
                        Utils.showSnackbar(binding.root, message)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        Utils.showSnackbar(binding.root, it.message.toString())
                    }
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(false)
                }
            }
        }
    }

    fun handleOmniStockResponse() {
        mainActivity?.showProgressBar(false)
        localItemList?.forEach { item ->
            var availableQty = 0
            val stockResponse = itemStockMap.get(item.skuCode)
            stockResponse?.omniStoreStockCheckResponse?.forEach {
                availableQty += it.availableQty
            }
            item.availabilityStatus = availableQty > 0
            item.availableQty = availableQty
            if (item.availabilityStatus == false) {
                item.quantity = 1
                item.status = "Unavailable"
            } else {
                item.status = "Available"
            }
        }
        setAdapter()
        setTotalAmt(localItemList)
        omniInvoice("","")
    }


    fun omniInvoice(pickupDate: String, pickupTime: String) {

        val orderItemList = getInvoiceItemList()
        var deliveryType = selectedDeliveryMethod
            deliveryType = "E-Commerce"
        storeCode = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_CODE, "")!!
        storeId = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_ID, 0)!!
        val omniInvoiceRequest = OrderInvoiceRequest(
            true, selectedCurrency, "", "", deliveryType, storeCode,
            storeId, orderItemList, selectedCurrency, pickupTime, pickupDate, ""
        )
        val gson = Gson()
        val requestString = gson.toJson(omniInvoiceRequest)
        getAccessToken()
        orderPlaceViewModel.omniInvoice(accessToken, omniInvoiceRequest)
        orderPlaceViewModel.responseOmniInvoice.observe(mainActivity!!) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    if (it.data?.statusCode == 1) {

                        omniOrderInvoice = it.data

                        val ordertemp = it.data.omnI_OrderHeaderData.omni_InvoiceHeader.invoiceDetailList
                        Log.d("itemlist : " , localItemList.toString())
                        var tprice = 0.00
                        for (i in 0 until localItemList.size) {
                            for (j in 0 until ordertemp.size){
                                if(localItemList.get(i).skuCode.equals(ordertemp.get(j).skuCode)){
                                    localItemList.get(i).stylePrice = ordertemp.get(j).lineTotal
                                    tprice += ordertemp.get(j).lineTotal
                                }
                            }
                        }
//                        omniItemAdapter.notifyDataSetChanged()
                        setAdapter()

                        var totalQty = 0
                        var totalPrice = 0.0
                        try {
                            localItemList.forEach {
                                totalQty += it.quantity!!
                                totalPrice += it.stylePrice!! * it.quantity!!
                            }
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }

                        totalPrice = tprice
                        binding.txtCartTotal?.text =
                            (Utils.getPriceFormatted(tprice.toString(), selectedCurrency))
                        binding.tvQty.text = totalQty.toString()


//                        binding.tvTotalPrice.text =
//                            (Utils.getPriceFormatted(totalPrice.toString(), selectedCurrency))

                       Log.d("omniinvorespo", omniOrderInvoice.toString())
                    }
                }

                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    try {
                        val jsonObj = JSONObject(it.message)
                        val message = jsonObj.getString("message")
                        Utils.showSnackbar(binding.root, message)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        Utils.showSnackbar(binding.root, it.message.toString())
                    }
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    fun getAccessToken() {
        accessToken =
            "Bearer " + sharedpreferenceHandler.getData(SharedpreferenceHandler.ACCESS_TOKEN, "")!!
    }

    private fun getInvoiceItemList(
    ): ArrayList<OMNIOrderDetailX> {
        val orderItemList = ArrayList<OMNIOrderDetailX>()
        var deliveryType = selectedDeliveryMethod
            deliveryType = "E-Commerce"

        orderItemList.clear()
        try {
            for (i in 0 until localItemList.size) {
                orderItemList.add(
                    OMNIOrderDetailX(
                        selectedCurrency,
                        localItemList.get(i).stylePrice!!,
                        localItemList.get(i).brandCode!!,
                        localItemList.get(i).brandID!!,
                        deliveryType,
                        localItemList.get(i).stylePrice!!,
                        selectedCurrency,
                        localItemList.get(i).quantity!!,
                        "",
                        "",
                        localItemList.get(i).productGroupID!!,
                        localItemList.get(i).productGroupName!!,
                        localItemList.get(i).seasonID!!,
                        localItemList.get(i).seasonName!!,
                        localItemList.get(i).segamentationID!!,
                        localItemList.get(i).skuCode!!,
                        localItemList.get(i).styleCode!!,
                        localItemList.get(i).subBrandCode!!,
                        localItemList.get(i).subBrandID!!,
                        "0.5",
                        "KG",
                        localItemList.get(i).yearID!!
                    )
                )


            }
        }catch (e: NullPointerException){
            e.printStackTrace()
        }
        return orderItemList
    }
    fun omniScanItem(skuCode: String) {
        mainActivity?.hideSoftKeyboard()
        val priceListId = sharedpreferenceHandler.getData(SharedpreferenceHandler.PRICE_LIST_ID, 0)
        val storeId = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_ID, 0)
        enquiryViewModel.omniScanItem(
            skuCode,
            storeId!!.toString(),
            priceListId!!.toString()
        )

        enquiryViewModel.responseScanItemOmni.observe(mainActivity!!) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
binding.recyclerSku.visibility = View.GONE
                    binding.recyclerViewModel.visibility = View.VISIBLE
                    binding.rcyCartList.visibility = View.VISIBLE
                    mainActivity?.showProgressBar(false)
                    if (it.data?.statusCode == 1) {
                        binding.editText.setText("")
                        binding.editText.clearFocus()
                        omniScannedItem = it.data
                        omniScannedItem?.skuMasterTypesList?.get(0)?.fromRiva = false
                        omniScannedItem?.skuMasterTypesList?.get(0)?.productId =  ""
                        origmon = omniScannedItem?.skuMasterTypesList?.get(0)!!.stylePrice!!
                        addProductsToLocal(omniScannedItem?.skuMasterTypesList?.get(0)!!, origmon)
//                        enquiryViewModel.addOmniProductToPrefs(
//                            omniScannedItem?.skuMasterTypesList?.get(
//                                0
//                            )!!, mainActivity!!
//                        )
//                        getCartItems()
                    } else {
//                        Utils.showSnackbar(binding.root, it.data?.displayMessage!!)
                        enquiryViewModel.responseScanItemOmni.value = null
                    }
                    enquiryViewModel.responseScanItemOmni.value = null


                }

                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    try {
                        val jsonObj = JSONObject(it.message)
                        val message = jsonObj.getString("message")
                        Utils.showSnackbar(binding.root, message)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        Utils.showSnackbar(binding.root, it.message.toString())
                    }
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(false)
                }
            }
        }
    }

    private fun addProductsToLocal(product: SkuMasterTypes, double: Double) {
        product.quantity = 1
        product.availableQty = 0
        product.orig_price = double
        if (localItemList.size == 0)
            localItemList.add(product)

        else {
            var foundItem = 0
            localItemList.forEach {
                if (it.skuCode.equals(product.skuCode)) {
                    it.quantity = it.quantity!! + product.quantity!!
                    foundItem++
                }
            }
            if (foundItem == 0)
                localItemList.add(product)
        }

        setAdapter()
        binding.linBottom.visibility = View.VISIBLE
        setTotalAmt(localItemList)
        localItemList?.forEach {
            currentCheckingSkuCode = it.skuCode!!
            checkOmniStock()
        }

    }
    fun searchModel(model: String) {
        enquiryViewModel.searchModel(model)
        enquiryViewModel.responseSearchModel.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.data?.searchEngineDataList != null) {

                        if (binding.editText.text.toString().equals(""))
                            binding.recyclerViewModel.visibility = View.GONE
                        else {
//                            if (it.data.searchEngineDataList.size == 1) {
////                                omniScanItem(it.data.searchEngineDataList.get(0).code)
//                            } else
                            setModelRecyclerview(it.data.searchEngineDataList)
                        }

                    } else {
                        binding.recyclerViewModel.visibility = View.GONE
                        Utils.showSnackbar(binding.root, "No Records Found")
                    }


                }

                is Resource.Error -> {
                    try {
                        val jsonObj = JSONObject(it.message)
                        val message = jsonObj.getString("message")
                        Utils.showSnackbar(binding.root, message)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }
                is Resource.Loading -> {
                }
            }
        }
    }

    fun searchModelStyle(model: String) {
        mainActivity?.showProgressBar(false)
        enquiryViewModel.searchModelStyle(model)
        enquiryViewModel.responseScanItemOmni.observe(this) {
            when (it) {
                is Resource.Success -> {
                    println(" success ---------  "+it.data?.skuMasterTypesList)

                    layoutManager = NpaGridLayoutManager(
                        activity,
                        1
                    )
                    binding.recyclerViewModel.visibility = View.GONE
                    binding.rcyCartList.visibility = View.GONE
                    binding.recyclerSku.visibility = View.VISIBLE
                    binding.recyclerSku?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                    binding.recyclerSku?.layoutManager = layoutManager

                    adapterSku = SkuItemAdapter(
                        mainActivity!!,
                        selectedCurrency,
                        it.data?.skuMasterTypesList
                    )
                    adapterSku?.onDeleteClicked = { cartItem ->
                        omniScanItem(cartItem?.skuCode!!)


                    }
                    binding.recyclerSku?.adapter = adapterSku
                }

                is Resource.Error -> {
                    try {
                        val jsonObj = JSONObject(it.message)
                        val message = jsonObj.getString("message")
                        Utils.showSnackbar(binding.root, message)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }
                is Resource.Loading -> {
                }
            }
        }
    }


    fun setModelRecyclerview(modelList: ArrayList<SearchEngineData>) {
        binding.recyclerViewModel.visibility = View.VISIBLE
        modelItemAdapter = ModelItemAdapter(mainActivity!!, modelList)
        modelItemAdapter?.onItemClicked = { model ->
            mainActivity?.hideSoftKeyboard()
            println(" working 4---------")
            omniScanItem(model.code)
        }
        binding.recyclerViewModel.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewModel?.adapter = modelItemAdapter
    }


    private fun saveItemsToSharedPrefs() {
        val gson = Gson()
        val itemString = gson.toJson(localItemList)
        sharedpreferenceHandler.saveData(SharedpreferenceHandler.SEARCH_ITEMS, itemString)
    }

    private fun navigateToOrderPlace() {
        val bundle = Bundle()
        bundle.putString("delivery_method", selectedDeliveryMethod)
        bundle.putSerializable("items", localItemList)
        mainActivity?.navController?.navigate(R.id.navigation_omni_order_place, bundle)
    }

    ///set total amount
    private fun setTotalAmt(arrList: ArrayList<SkuMasterTypes>) {
        var totalQty = 0
        var totalPrice = 0.0
        try {
            arrList?.forEach {
                totalQty += it.quantity!!
                totalPrice += it.stylePrice!! * it.quantity!!
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        binding.txtCartTotal?.text =
            (Utils.getPriceFormatted(totalPrice.toString(), selectedCurrency))
        binding.tvQty.text = totalQty.toString()
    }

    fun showDeleteProductDialog(skuCode: String) {
        val alertDialog = AlertDialog.Builder(mainActivity!!)

        // Setting Dialog Title
        alertDialog.setTitle("Confirm")

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to delete the product?")

        // On pressing Settings button
        alertDialog.setPositiveButton(
            "YES"
        ) { dialog, which ->
//            deleteProductFromDb(skuCode)
            localItemList?.removeIf { it.skuCode.equals(skuCode) }

            sharedpreferenceHandler.saveData(
                SharedpreferenceHandler.SEARCH_COUNT,
                localItemList?.size!!
            )
            itemStockMap.remove(skuCode)
            setAdapter()
            setTotalAmt(localItemList!!)
            saveItemsToSharedPrefs()
            Utils.showSnackbar(binding.root, "Item removed successfully")

        }

        // on pressing cancel button
        alertDialog.setNegativeButton(
            "NO"
        ) { dialog, which -> dialog.cancel() }

        // Showing Alert Message
        alertDialog.show()
    }
}