package com.armada.storeapp.ui.home.riva.riva_look_book.bag

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.databinding.ActivityOmniBagBinding
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.bag.adapter.BagItemAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.bag.adapter.ModelItemAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_item_scan.OmniItemScannerActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.OrderPlaceActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.search.BarcodeScanner.BarcodeScannerActivity
import com.armada.storeapp.ui.utils.*
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.detail_toobar.view.*
import org.json.JSONObject
import kotlin.Boolean
import kotlin.Exception
import kotlin.String
import kotlin.collections.ArrayList

@AndroidEntryPoint
class OmniBagActivity : BaseActivity() {

    var omniScannedItem: ScannedItemDetailsResponse? = null
    private var selectedLanguage: String = "en"
    private var selectedCurrency = "USD"
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    lateinit var binding: ActivityOmniBagBinding
    lateinit var bagViewModel: BagViewModel
    var itemList: ArrayList<SkuMasterTypes>? = null
    var itemStockMap = HashMap<String, OmniStockResponse>()
    private var cd: ConnectionDetector? = null
    var selectedDeliveryMethod = ""

    //    var strUserId = ""
    var isLoggedIn = false
    private var isFromRefresh: Boolean? = false

    private var loading: Dialog? = null
    private var strSubtotal = "0"
    private var totalAmount = 0.0
    var currentCheckingSkuCode = ""
    private var adapter: BagItemAdapter? = null
    var layoutManager: NpaGridLayoutManager? = null
    lateinit var modelItemAdapter: ModelItemAdapter

    var startForResult: ActivityResultLauncher<Intent>? = null

    var currentSearchEditTextLength = 0
    private var isUsingScanningDevice: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOmniBagBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bagViewModel =
            ViewModelProvider(this).get(BagViewModel::class.java)
        setEmptyPage()
//        initToolbar()
        init()
        setOnClickListener()

    }

    //    private fun initToolbar() {
//        setSupportActionBar(binding.toolbarActionbar.root)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        val upArrow: Drawable =
//            resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)
//
//        upArrow.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
//        upArrow.setVisible(true, true)
//        supportActionBar!!.setHomeAsUpIndicator(upArrow)
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
//        binding.toolbarActionbar.root.title = resources.getString(R.string.shopping_bag)
//        binding.toolbarActionbar.root.txtHead.text="BAG"
//        binding.toolbarActionbar.relCartImage.visibility=View.GONE
////        binding.toolbarActionbarimgHelp.visibility=View.VISIBLE
//
//    }
    private fun init() {

        cd = ConnectionDetector(this)
        sharedpreferenceHandler = SharedpreferenceHandler(this)
        isLoggedIn =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_USER_LOGGED_IN, false)
        selectedLanguage =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY, "en")!!
        selectedCurrency =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_CURRENCY, "USD")!!
        sharedpreferenceHandler.saveData(SharedpreferenceHandler.PAYMENT_STATUS, "")

        layoutManager = NpaGridLayoutManager(
            this,
            1
        )
        binding.rcyCartList?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        binding.rcyCartList?.layoutManager = layoutManager

        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    val barcode = intent?.getStringExtra("barcode")
                    omniScanItem(barcode!!)
                }
            }

        if (cd!!.isConnectingToInternet) {
            getCartItems()
        } else {
            binding.lnrNoItems.visibility = View.VISIBLE
            binding.rcyCartList.visibility = View.GONE
            binding.linBottom.visibility = View.GONE
        }




        adapter = BagItemAdapter(
            this,
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


    private fun setOnClickListener() {

        binding.txtContinue.setOnClickListener {
            this.finish()
        }

        binding.imageView10.setOnClickListener {
            this.finish()
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
//            val intent = Intent(this, OmniItemScannerActivity::class.java)
//            startActivityForResult(intent, 303)
            val intent = Intent(this, BarcodeScannerActivity::class.java)
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
        binding.imageViewSubmit.setOnClickListener {
            val sku = binding.editText.text.toString()
            omniScanItem(sku)
        }
    }


    private fun setEmptyPage() {
        if (!Utils.emptyCartPage.icon.isNullOrEmpty())
            Glide.with(this).load(Utils.emptyCartPage.icon)
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
        bagViewModel.getAllOmniProduct(this).let {
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
////                    var qty = 0
////                    entry.value.forEach {
////                        qty += it.quantity!!
////                    }
//                    var skuitem = entry?.value?.get(0)
//                    skuitem.quantity = entry.value.size
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
        bagViewModel.responseOmniStock.observe(this) {
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
        hideSoftKeyboard()
        val priceListId = sharedpreferenceHandler.getData(SharedpreferenceHandler.PRICE_LIST_ID, 0)
        val storeId = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_ID, 0)
        bagViewModel.omniScanItem(
            skuCode,
            storeId!!.toString(),
            priceListId!!.toString()
        )
        binding.editText.setText("")
        binding.editText.clearFocus()
        bagViewModel.responseScanItemOmni.observe(this) {
            when (it) {
                is Resource.Success -> {
                    dismissProgress()
                    if (it.data?.statusCode == 1) {
                        omniScannedItem = it.data
                        omniScannedItem?.skuMasterTypesList?.get(0)?.fromRiva = false
                        omniScannedItem?.skuMasterTypesList?.get(0)?.productId =
                            ""
                        bagViewModel.addOmniProductToPrefs(
                            omniScannedItem?.skuMasterTypesList?.get(
                                0
                            )!!, this
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
                        Utils.showSnackbar(binding.root, it.message.toString())
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
        bagViewModel.responseSearchModel.observe(this) {
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

    fun setModelRecyclerview(modelList: ArrayList<SearchEngineData>) {
        binding.recyclerViewModel.visibility = View.VISIBLE
        modelItemAdapter = ModelItemAdapter(this, modelList)
        modelItemAdapter?.onItemClicked = { model ->
            hideSoftKeyboard()
            omniScanItem(model.code)
        }
        binding.recyclerViewModel.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewModel?.adapter = modelItemAdapter
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
        val intent = Intent(this, OrderPlaceActivity::class.java)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun showProgress() {
        try {
            if (loading == null) {
                loading = Dialog(
                    this,
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
        updateBadgeCart()

    }

    fun updateBadgeCart() {
//        val count = sharedpreferenceHandler.getData(SharedpreferenceHandler.CART_COUNT, 0)
//        if (count > 0) {
//            binding.toolbarActionbar.txtCartCount.text = count.toString()
//            binding.toolbarActionbar.txtCartCount.visibility = View.VISIBLE
//        } else
//            binding.toolbarActionbar.txtCartCount.visibility = View.INVISIBLE

    }

    fun showDeleteProductDialog(skuCode: String) {
        val alertDialog = AlertDialog.Builder(this)

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
            saveItemsToSharedPrefs()
            sharedpreferenceHandler.saveData(SharedpreferenceHandler.CART_COUNT, itemList?.size!!)
            itemStockMap.remove(skuCode)
            setAdapter()
            setTotalAmt(itemList!!)
            updateBadgeCart()
            if (itemList?.size == 0) {
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
            }

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