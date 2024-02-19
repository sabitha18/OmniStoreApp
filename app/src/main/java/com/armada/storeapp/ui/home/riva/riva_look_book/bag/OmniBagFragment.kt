package com.armada.storeapp.ui.home.riva.riva_look_book.bag

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.databinding.FragmentBagBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.bag.adapter.BagItemAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.bag.adapter.ModelItemAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_item_scan.OmniItemScannerActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.OrderPlaceActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.select_store.adapter.OmniStoreAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.search.BarcodeScanner.BarcodeScannerActivity
import com.armada.storeapp.ui.utils.*
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class OmniBagFragment : Fragment() {

    var omniScannedItem: ScannedItemDetailsResponse? = null
    private var selectedLanguage: String = "en"
    private var selectedCurrency = "USD"
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    lateinit var binding: FragmentBagBinding
    lateinit var bagViewModel: BagViewModel
    var itemList: ArrayList<SkuMasterTypes>? = null
    var itemStockMap = HashMap<String, OmniStockResponse>()
    private var cd: ConnectionDetector? = null
    var selectedDeliveryMethod = ""
    var currentCheckingSkuCode = ""

    //    var strUserId = ""
    var isLoggedIn = false
    private var isFromRefresh: Boolean? = false

    private var loading: Dialog? = null
    private var strSubtotal = "0"
    private var totalAmount = 0.0


    private var adapter: BagItemAdapter? = null
    var layoutManager: NpaGridLayoutManager? = null
    private var rivaLookBookActivity: RivaLookBookActivity? = null
    lateinit var modelItemAdapter: ModelItemAdapter

    var currentSearchEditTextLength = 0
    private var isUsingScanningDevice: Boolean = true

    var startForResult: ActivityResultLauncher<Intent>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    val barcode = intent?.getStringExtra("barcode")
                    omniScanItem(barcode!!)
                }
            }
    }

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
            rivaLookBookActivity!!,
            selectedCurrency,
            itemList
        )
        adapter?.onQtyChanged = { cartItem ->
            var totalQty = 0
            itemList?.forEach {
                if (it.skuCode.equals(cartItem.skuCode)) {
                    it.quantity = cartItem.quantity
                }
                totalQty += it.quantity!!
            }

            setTotalAmt(itemList!!)
            binding.tvQty.text = totalQty.toString()
            try {
                saveItemsToSharedPrefs()
//                bagViewModel.removeOmniProduct(cartItem?.skuCode!!)
//                cartItem?.id = (0..100).shuffled().last()
//                bagViewModel.addOmniProduct(cartItem)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        adapter?.onDeleteClicked = { cartItem ->
            showDeleteProductDialog(cartItem?.skuCode!!)

        }
        binding.rcyCartList?.adapter = adapter



        if (cd!!.isConnectingToInternet) {
            getCartItems()
        } else {
            binding.lnrNoItems.visibility = View.VISIBLE
            binding.rcyCartList.visibility = View.GONE
            binding.linBottom.visibility = View.GONE
        }


        binding.swipeRefreshCart.setOnRefreshListener {
            isFromRefresh = true
            binding.swipeRefreshCart.isRefreshing = true
            if (cd!!.isConnectingToInternet)
                getCartItems()
            else
                Utils.showSnackbar(binding.root, "Please check your internet connection")

        }
    }


    fun setAdapter() {
       adapter?.setAdapter(itemList!!)
    }

    fun setModelRecyclerview(modelList: ArrayList<SearchEngineData>) {
        binding.recyclerViewModel.visibility = View.VISIBLE
        modelItemAdapter = ModelItemAdapter(rivaLookBookActivity!!, modelList)
        modelItemAdapter?.onItemClicked = { model ->
            rivaLookBookActivity?.hideSoftKeyboard()
            omniScanItem(model.code)
        }
        binding.recyclerViewModel.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewModel?.adapter = modelItemAdapter
    }


    private fun setOnClickListener() {

        binding.txtContinue.setOnClickListener {
            rivaLookBookActivity?.navController?.navigate(R.id.navigation_riva_home)
        }

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

//        binding.radioGroup.children.forEach { radioButton ->
//            (radioButton as RadioButton).setOnCheckedChangeListener { button, isChecked ->
//                if (isChecked) {
//                    binding.radioGroup.children.forEach { bBtn ->
//                        if (bBtn.id != button.id) {
//                            (bBtn as RadioButton).isChecked = false
//                        }
//                    }
//                }
//            }
//        }

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
            val intent = Intent(rivaLookBookActivity, BarcodeScannerActivity::class.java)
            startForResult?.launch(intent)
//            val intent = Intent(rivaLookBookActivity, OmniItemScannerActivity::class.java)
//            startActivityForResult(intent, 303)
        }

//        binding?.editText?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(
//                text: CharSequence?,
//                start: Int,
//                count: Int,
//                after: Int
//            ) {
//            }
//
//            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
//                binding.imageViewSubmit.visibility = View.VISIBLE
//                binding.imageViewBarcodeScan.visibility = View.GONE
//                if (text?.length!! == 0) {
//                    binding.recyclerViewModel.visibility = View.GONE
//                } else {
////                    searchModel(text.toString())
//                    if (text.toString()
//                            .isDigitsOnly() && (text.toString().length!! == 13 || text.toString().length!! == 6)
//                    )
//                        omniScanItem(text.toString())
//                    else
//                        searchModel(text.toString())
//                }
//            }
//
//            override fun afterTextChanged(text: Editable?) {
//                if (text?.length!! > 0) {
////                    searchModel(text.toString())
//                    if (text.toString()
//                            .isDigitsOnly() && (text.toString().length!! == 13 || text.toString().length!! == 6)
//                    )
//                        omniScanItem(text.toString())
//                    else
//                        searchModel(text.toString())
//                    binding.imageViewSubmit.visibility = View.VISIBLE
//                    binding.imageViewBarcodeScan.visibility = View.GONE
//                } else {
//                    binding.recyclerViewModel.visibility = View.GONE
//                    binding.imageViewSubmit.visibility = View.GONE
//                    binding.imageViewBarcodeScan.visibility = View.VISIBLE
//                }
//            }
//
//        })

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
                    omniScanItem(text.toString())
                } else
                    searchModel(text.toString())
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
            val sku = binding.editText.text.toString()
            omniScanItem(sku)
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
        showProgress()
        bagViewModel.getAllOmniProduct(rivaLookBookActivity!!).let {
            if (it.isNullOrEmpty()) {
                itemList?.clear()
                itemStockMap?.clear()
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
            } else {
                itemList = it
                itemList?.forEach {
                    it.availableQty=0
                }
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
                handleCartListItems(itemList!!)
                binding.lnrNoItems.visibility = View.GONE
                binding.linBottom.visibility = View.VISIBLE
                if (binding.swipeRefreshCart != null && binding.swipeRefreshCart.isRefreshing) {
                    binding.swipeRefreshCart.isRefreshing = false
                }
                isFromRefresh = false
            }
        }
//        bagViewModel.getAllOmniProduct().observe(rivaLookBookActivity!!) {
//            val list = it as ArrayList<SkuMasterTypes>
//            var sharedPreferenceList: java.util.ArrayList<SkuMasterTypes>? = null
//            val itemString =
//                sharedpreferenceHandler.getData(SharedpreferenceHandler.CART_ITEMS, "")
//            if (!itemString.isNullOrEmpty()) {
//                val gson = Gson()
//                val type = object : TypeToken<java.util.ArrayList<SkuMasterTypes>>() {
//
//                }.type
//                sharedPreferenceList =
//                    gson.fromJson<java.util.ArrayList<SkuMasterTypes>>(itemString, type)
//            }
//
//            if (list.isNullOrEmpty()) {
//                if (sharedPreferenceList != null && sharedPreferenceList.size > 0) {
//                    itemList = sharedPreferenceList
//                    setAdapter()
//                } else {
//                    binding?.lnrNoItems?.visibility = View.VISIBLE
//                    isFromRefresh = false
//                    sharedpreferenceHandler.saveData(SharedpreferenceHandler.CART_COUNT, 0)
//                    updateBadgeCart()
//                    binding.rcyCartList?.visibility = View.GONE
//                    binding?.linBottom?.visibility = View.GONE
//                    if (binding.swipeRefreshCart != null && binding.swipeRefreshCart.isRefreshing) {
//                        binding.swipeRefreshCart.isRefreshing = false
//                    }
//                }
//
//            } else {
//                if (sharedPreferenceList != null && sharedPreferenceList.size > 0 && sharedPreferenceList.size != list.size)
//                    itemList = sharedPreferenceList
//                else
//                    itemList = list
//                var grouped = itemList?.groupBy { it.skuCode }
//                itemList?.clear()
//                grouped?.forEach { entry ->
//                    var qty = 0
//                    entry.value.forEach {
//                        qty += it.quantity!!
//                    }
//                    var skuitem = entry?.value?.get(0)
////                    if (qty > skuitem.stock!!) {
////                        skuitem?.quantity = skuitem?.stock
////                        Utils.showSnackbar(
////                            binding.root,
////                            "Maximum quantity for this item has reached"
////                        )
////                        qty = skuitem.stock!!
////                    }
//                    skuitem.quantity = qty
//                    skuitem.availableQty = 0
//                    skuitem.availabilityStatus = false
//                    itemList?.add(skuitem)
//                }
//                handleCartListItems(itemList!!)
//                binding.lnrNoItems.visibility = View.GONE
//                binding.linBottom.visibility = View.VISIBLE
//                if (binding.swipeRefreshCart != null && binding.swipeRefreshCart.isRefreshing) {
//                    binding.swipeRefreshCart.isRefreshing = false
//                }
//                isFromRefresh = false
//            }
//        }
    }

    private fun handleCartListItems(list: ArrayList<SkuMasterTypes>) {

        if (list?.size > 0) {

            if (binding.swipeRefreshCart != null && binding.swipeRefreshCart.isRefreshing) {
                binding.swipeRefreshCart.isRefreshing = false
            }
            isFromRefresh = false

            var bagCount = list.size
            sharedpreferenceHandler.saveData(SharedpreferenceHandler.CART_COUNT, bagCount)
            updateBadgeCart()


            binding.lnrNoItems?.visibility = View.GONE
            binding.rcyCartList?.visibility = View.VISIBLE
            binding.linBottom?.visibility = View.VISIBLE

            setAdapter()

            setTotalAmt(list)
            dismissProgress()

            itemList?.forEach {
                currentCheckingSkuCode = it.skuCode!!
                checkOmniStock()
            }

        } else {
            binding.lnrNoItems.visibility = View.VISIBLE
            binding.linBottom.visibility = View.GONE
            sharedpreferenceHandler.saveData(SharedpreferenceHandler.INVOICE_ID, "")
            sharedpreferenceHandler.saveData(SharedpreferenceHandler.CART_COUNT, 0)
            updateBadgeCart()
        }
    }

    override fun onStop() {
        super.onStop()
//        saveItemsToSharedPrefs()
    }

    fun checkOmniStock() {
        val countryId = sharedpreferenceHandler.getData(SharedpreferenceHandler.COUNTRY_ID, 0)
        val countryCode = sharedpreferenceHandler.getData(SharedpreferenceHandler.COUNTRY_CODE, "")
        val storeCode = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_CODE, "")

        bagViewModel.checkOmniStock(
            currentCheckingSkuCode,
            countryId!!.toString(),
            storeCode!!,
            storeCode,
            countryCode!!
        )
        bagViewModel.responseOmniStock.observe(rivaLookBookActivity!!) {
            when (it) {
                is Resource.Success -> {
                    dismissProgress()
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
                    if (noOfItemStockChecked == itemList?.size) {
                        handleOmniStockResponse()
                    }


                }

                is Resource.Error -> {
                    dismissProgress()
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
                    showProgress()
                }
            }
        }
    }

    fun handleOmniStockResponse() {
        itemList?.forEach { item ->
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
        setTotalAmt(itemList!!)
    }

    fun omniScanItem(skuCode: String) {
        rivaLookBookActivity?.hideSoftKeyboard()
        val priceListId = sharedpreferenceHandler.getData(SharedpreferenceHandler.PRICE_LIST_ID, 0)
        val storeId = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_ID, 0)
        bagViewModel.omniScanItem(
            skuCode,
            storeId!!.toString(),
            priceListId!!.toString()
        )

        bagViewModel.responseScanItemOmni.observe(rivaLookBookActivity!!) {
            when (it) {
                is Resource.Success -> {
                    dismissProgress()
                    if (it.data?.statusCode == 1) {
                        binding.recyclerViewModel.visibility = View.GONE
                        binding.editText.setText("")
                        binding.editText.clearFocus()
                        omniScannedItem = it.data
                        omniScannedItem?.skuMasterTypesList?.get(0)?.fromRiva = false
                        omniScannedItem?.skuMasterTypesList?.get(0)?.productId =
                            ""
                        bagViewModel.addOmniProductToPrefs(
                            omniScannedItem?.skuMasterTypesList?.get(
                                0
                            )!!, rivaLookBookActivity!!
                        )
                        getCartItems()
                    } else {
//                        Utils.showSnackbar(binding.root, it.data?.displayMessage!!)

                    }

                    bagViewModel.responseScanItemOmni.value=null
                }

                is Resource.Error -> {
                    dismissProgress()
                    try {
                        val jsonObj = JSONObject(it.message)
                        val message = jsonObj.getString("message")
                        Utils.showSnackbar(binding.root, message)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    fun searchModel(model: String) {
        bagViewModel.searchModel(model)
        bagViewModel.responseSearchModel.observe(rivaLookBookActivity!!) {
            when (it) {
                is Resource.Success -> {
                    if (it.data?.searchEngineDataList != null) {
                        if (binding.editText.text.toString().equals(""))
                            binding.recyclerViewModel.visibility = View.GONE
                        else {
//                            if (it.data.searchEngineDataList.size == 1) {
//                                omniScanItem(it.data.searchEngineDataList.get(0).code)
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

    private fun addProductToDb() {
        try {
            omniScannedItem?.skuMasterTypesList?.get(0)?.quantity = 1
            omniScannedItem?.skuMasterTypesList?.get(0)?.id = (0..100).shuffled().last()
            bagViewModel.addOmniProduct(omniScannedItem?.skuMasterTypesList?.get(0)!!)

            itemList?.add(omniScannedItem?.skuMasterTypesList?.get(0)!!)
            adapter?.notifyDataSetChanged()


        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun allProductsToDb() {
        try {
            saveItemsToSharedPrefs()
            bagViewModel.deleteAllProductsFromBag()
            var id = 0
            itemList?.forEach {
                id = id++
                it.id = id
                bagViewModel.addOmniProduct(it)
            }
            navigateToOrderPlace()
        } catch (exception: Exception) {
            exception.printStackTrace()
            navigateToOrderPlace()
        }
    }

    private fun saveItemsToSharedPrefs() {
        val gson = Gson()
        val itemString = gson.toJson(itemList)
        sharedpreferenceHandler.saveData(SharedpreferenceHandler.CART_ITEMS, itemString)
    }

    private fun navigateToOrderPlace() {
        val intent = Intent(rivaLookBookActivity, OrderPlaceActivity::class.java)
        intent.putExtra("delivery_method", selectedDeliveryMethod)
        val gson = Gson()
        intent.putExtra("items", gson.toJson(itemList!!))
        startActivity(intent)
    }

    private fun deleteProductFromDb(skuCode: String) {
        try {
            bagViewModel.removeOmniProduct(skuCode)
            itemList?.removeIf { (it.skuCode.equals(skuCode)) }
            saveItemsToSharedPrefs()
            sharedpreferenceHandler.saveData(SharedpreferenceHandler.CART_COUNT, itemList?.size!!)
            itemStockMap.remove(skuCode)

            updateBadgeCart()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
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

    override fun onResume() {
        super.onResume()
        getCartItems()
        rivaLookBookActivity?.binding?.imageViewRivaLogo?.visibility = View.GONE
        updateBadgeCart()

    }

    fun updateBadgeCart() {
        rivaLookBookActivity?.setBagCount()
    }

    fun showDeleteProductDialog(skuCode: String) {
        val alertDialog = AlertDialog.Builder(rivaLookBookActivity)

        // Setting Dialog Title
        alertDialog.setTitle("Confirm")

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to delete the product?")

        // On pressing Settings button
        alertDialog.setPositiveButton(
            "YES"
        ) { dialog, which ->
//            deleteProductFromDb(skuCode)
            itemList?.removeIf { it.skuCode.equals(skuCode) }

            sharedpreferenceHandler.saveData(SharedpreferenceHandler.CART_COUNT, itemList?.size!!)
            itemStockMap.remove(skuCode)
            setAdapter()
            setTotalAmt(itemList!!)
            updateBadgeCart()
            if (itemList?.size == 0) {
                binding?.lnrNoItems?.visibility = View.VISIBLE
                isFromRefresh = false
                sharedpreferenceHandler.saveData(SharedpreferenceHandler.CART_COUNT, 0)
                updateBadgeCart()
                binding.rcyCartList?.visibility = View.GONE
                binding?.linBottom?.visibility = View.GONE
                if (binding.swipeRefreshCart != null && binding.swipeRefreshCart.isRefreshing) {
                    binding.swipeRefreshCart.isRefreshing = false
                }
            }
            saveItemsToSharedPrefs()
            Utils.showSnackbar(binding.root, "Item removed from bag successfully")

        }

        // on pressing cancel button
        alertDialog.setNegativeButton(
            "NO"
        ) { dialog, which -> dialog.cancel() }

        // Showing Alert Message
        alertDialog.show()
    }

}