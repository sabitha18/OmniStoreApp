package com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.select_store

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatDelegate
//import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.OmniStockResponse
import com.armada.storeapp.data.model.response.OmniStoreStockCheckResponse
import com.armada.storeapp.databinding.FragmentSelectStoreBinding
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.RecyclerTouchListener
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.RecyclerTouchListener.ClickListener
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.select_store.adapter.OmniStoreAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.select_country.adapter.SelectStoreAdapter
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.armada.storeapp.ui.utils.Utils
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class SelectStoreActivity
    : BaseActivity() {

    private var adapter: OmniStoreAdapter? = null
    private var loading: Dialog? = null
    val storeList = ArrayList<OmniStoreStockCheckResponse>()

    var itemStockMap = HashMap<String, OmniStockResponse>()

    lateinit var binding: FragmentSelectStoreBinding
    lateinit var storeViewModel: SelectStoreViewModel
    var skuList: ArrayList<String>? = null
    var storeNameList = ArrayList<String>()
    var storeSkuStockMap = HashMap<String, ArrayList<OmniStoreStockCheckResponse>>()
    var selectedDeliveryMethod = ""
    var skuQtyMap = HashMap<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSelectStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storeViewModel = ViewModelProvider(this).get(SelectStoreViewModel::class.java)
        initToolbar()
        init()
        setListeners()
    }

    fun init() {
        intent?.let {
            if (intent.hasExtra("skus")) {
                skuList = intent.getStringArrayListExtra("skus")
            }
            if (intent.hasExtra("sku_map"))
                skuQtyMap = intent.extras?.get("sku_map") as HashMap<String, Int>
            if (intent.hasExtra("delivery_method"))
                selectedDeliveryMethod = intent.getStringExtra("delivery_method")!!
        }

        skuList?.forEach {
            checkOmniStock(it)
        }
    }

    private fun initToolbar() {
//        setSupportActionBar(binding.toolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        val upArrow: Drawable =
//            resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)
//
//        upArrow.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
//        upArrow.setVisible(true, true)
//        supportActionBar!!.setHomeAsUpIndicator(upArrow)
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
//        binding.toolbar.title = "SELECT STORE"
////        binding.toolbarActionbarimgHelp.visibility=View.VISIBLE

    }

    fun setListeners() {
        binding.searchViewStore.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val filterList = ArrayList<OmniStoreStockCheckResponse>()
                storeList.forEach {
                    if (it.storeName.lowercase().contains(query!!)) {
                        filterList.add(it)
                    }
                    if (it.enableWarehouseFullFillment == 1 && it.warehouseName.lowercase()
                            .contains(query!!)
                    )
                        filterList.add(it)
                }
                adapter?.filterList(filterList)
//                adapter?.filterList(filterList)
//                if (storeList.contains(query)) {
//
//                } else {
//                   Utils.showSnackbar(binding.root,"No store found")
//                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filterList = ArrayList<OmniStoreStockCheckResponse>()
                storeList.forEach {
                    if (it.storeName.lowercase().contains(newText!!)) {
                        filterList.add(it)
                    }
                    if (it.enableWarehouseFullFillment == 1 && it.warehouseName.lowercase()
                            .contains(newText!!)
                    )
                        filterList.add(it)
                }
                adapter?.filterList(filterList)
                return false
            }
        })

        binding.imageView10.setOnClickListener {
            val returnIntent = Intent()
            setResult(Activity.RESULT_CANCELED, returnIntent)
            this.finish()
        }
    }


    fun checkOmniStock(skuCode: String) {
        val sharedpreferenceHandler = SharedpreferenceHandler(this)
        val countryId = sharedpreferenceHandler.getData(SharedpreferenceHandler.COUNTRY_ID, 0)
        val countryCode = sharedpreferenceHandler.getData(SharedpreferenceHandler.COUNTRY_CODE, "")
        val storeCode = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_CODE, "")

        storeViewModel.getStoreStockResponse(
            skuCode,
            countryId!!.toString(),
            storeCode!!,
            storeCode,
            countryCode!!
        )
        storeViewModel.responseStoreStock.observe(this) {
            when (it) {
                is Resource.Success -> {
                    dismissProgress()
                    if (it.statusCode == 200) {

                        try {
                            itemStockMap.put(
                                it.data?.omniStoreStockCheckResponse?.get(0)?.skuCode!!,
                                it.data!!
                            )

                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }

                        var noOfItemStockChecked = itemStockMap.keys.size
                        if (noOfItemStockChecked == skuList?.size) {
                            setStoreRecyclerView()
                        }
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

    fun processStockResponse(data: OmniStockResponse) {
        if (data.statusCode == 1) {
            data.omniStoreStockCheckResponse.forEach {


                val storeName= if(it.enableWarehouseFullFillment==1) it.warehouseName else it.storeName

//                if(it.availableQty > 0){
//                    it.status = "Available"
//                } else {
//                    it.status = "Unavailable"
//                }

                if (storeSkuStockMap.containsKey(storeName)) {

                    //getting existing sku data stock
                    val stockList = storeSkuStockMap.get(storeName)
                    if (stockList?.contains(it) == true) {

                    } else {
                        when (selectedDeliveryMethod) {
                            "STOREPICKUP" -> {
                                if (storeName.equals("E-Commerce") || it.enableWarehouseFullFillment == 1) {
                                    // for store pickup E - commerce and warehouse does not need to be listed
                                } else {
                                    val qty = skuQtyMap?.get(it.skuCode)
                                    it.addedQty = qty!!
                                    stockList?.add(it)
                                    storeSkuStockMap.put(storeName, stockList!!)
                                }
                            }
                            "HOME DELIVERY" -> {
                                //setting customer added qty for each sku
                                val qty = skuQtyMap.get(it.skuCode)
                                it.addedQty = qty!!
                                stockList?.add(it)
                                // setting values to map. put warehouse as seperate store
//                                if (it.enableWarehouseFullFillment == 1)
//                                    storeSkuStockMap.put(it.warehouseName, stockList!!)
//                                else
                                    storeSkuStockMap.put(storeName, stockList!!)
                            }
                        }


                    }

                } else {
                    //store with different skus
                    val storeName= if(it.enableWarehouseFullFillment==1) it.warehouseName else it.storeName
                    val stockList = ArrayList<OmniStoreStockCheckResponse>()
                    when (selectedDeliveryMethod) {
                        "STOREPICKUP" -> {
                            if (storeName.equals("E-Commerce") || it.enableWarehouseFullFillment == 1) {
                                // for store pickup E - commerce and warehouse does not need to be listed
                            } else {
                                val qty = skuQtyMap?.get(it.skuCode)
                                it.addedQty = qty!!
                                stockList.add(it)
                                storeSkuStockMap.put(storeName, stockList)
                            }
                        }
                        "HOME DELIVERY" -> {
                            //setting customer added qty for each sku
                            val qty = skuQtyMap.get(it.skuCode)
                            it.addedQty = qty!!
                            stockList.add(it)
//                            if (it.enableWarehouseFullFillment == 1)
//                                storeSkuStockMap.put(it.warehouseName, stockList)
//                            else
                                storeSkuStockMap.put(storeName, stockList)

                        }
                    }


                }
            }


        }
    }

    fun setStoreRecyclerView() {


        itemStockMap.forEach { sku, stockCheckResponse ->
            processStockResponse(stockCheckResponse)
        }

        storeNameList.clear()
        storeNameList = ArrayList(storeSkuStockMap.keys)
        storeList.clear()
        storeNameList.forEach {
            val storeItemList = storeSkuStockMap?.get(it)
            var totalAddedQty = 0
            var totalAvailableQty = 0

            var idx = 0

            storeItemList?.forEach {

                it.idx = idx

                totalAddedQty += it.addedQty
                if (it.availableQty - it.addedQty >= 0) {
                    totalAvailableQty += it.availableQty - it.addedQty
                } else
                    totalAvailableQty = 0

                var status = ""

                if (totalAvailableQty >= totalAddedQty) {
                    status = "Available"
                } else {
                    status = "Unavailable"
                }

                try {
                    it.status = status
                    storeList.add(it!!)
                } catch (exception: Exception) {
                    Log.e("exception", itemStockMap.size.toString() )
                    exception.printStackTrace()
                }

                idx += 1
            }



//            var status = ""
//            if (totalAvailableQty == totalAddedQty) {
//                status = "Available"
//            } else {
//                status = "Unavailable"
//            }
//
//            try {
//                storeItemList?.get(0)?.status = status
//                storeList.add(storeItemList?.get(0)!!)
//            } catch (exception: Exception) {
//                Log.e("exception", itemStockMap.size.toString() )
//                exception.printStackTrace()
//            }
        }

        storeList.sortBy { it.status }

        Log.e("adapter list size", storeList.size.toString())
       adapter = OmniStoreAdapter(this, storeList)
        adapter?.onStoreClicked = { store, status ->
            var storeItem: OmniStoreStockCheckResponse? = null
            val storeName = if(store.enableWarehouseFullFillment==1) store.warehouseName else store.storeName
            val fromstore = store.storeCode

            val warehousecode = store.warehouseCode
            val warehousename = store.warehouseName


            var idx1 = store.idx
            storeItem = storeSkuStockMap?.get(storeName)?.get(idx1)
            val gson = Gson()
            val returnIntent = Intent()
            returnIntent.putExtra("selected_store", gson.toJson(storeItem))
            returnIntent.putExtra("status", status)
            returnIntent.putExtra("fromstorecode", fromstore)
            returnIntent.putExtra("warehousecode", warehousecode)
            returnIntent.putExtra("warehousename", warehousename)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView?.adapter = adapter

    }

    ///loading dialog
    private fun showProgress() {

        if (!this@SelectStoreActivity.isFinishing) {
            if (loading == null) {
                loading = Dialog(this@SelectStoreActivity, R.style.TranslucentDialog)
                loading?.setContentView(R.layout.custom_loading_view)
                loading?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loading?.setCanceledOnTouchOutside(false)
                loading?.show()
            } else {
                if (loading?.isShowing == true) {

                } else {
                    loading?.show()
                }
            }
        }
    }

    private fun dismissProgress() {
        if (loading != null && loading?.isShowing == true)
            loading?.dismiss()
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

}