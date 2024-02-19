package com.armada.storeapp.ui.home.warehouse_transactions.stock_adjustment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.StockAdjustmentAddRequest
import com.armada.storeapp.data.model.response.StockAdjustmentScanModelResponse
import com.armada.storeapp.databinding.FragmentStockAdjustmentBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.warehouse_transactions.stock_adjustment.adapter.StockAdjustmentItemRecyclerviewAdapter
import com.armada.storeapp.ui.utils.ConnectionDetector
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class StockAdjustmentFragment : Fragment() {
    private var TAG = StockAdjustmentFragment::class.java.simpleName
    var cd: ConnectionDetector? = null
    private var fragmentStockAdjustmentBinding: FragmentStockAdjustmentBinding? = null
    private var mainActivity: MainActivity? = null
    private val binding get() = fragmentStockAdjustmentBinding!!
    lateinit var stockAdjustmentViewModel: StockAdjustmentViewModel
    var sessionToken = ""
    var locationCode = ""
    var userCode = ""
    var totalStock = 0
    var totalCount = 0
    var totalDiff = 0
    var modelSkuList =
        ArrayList<StockAdjustmentScanModelResponse.StockAdjustmentScanModelResponseItem>()
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    var stockAdjustmentItemsAdpater: StockAdjustmentItemRecyclerviewAdapter? = null

    var isUsingScanningDevice = true
    private var currentModelEdittextLength = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentStockAdjustmentBinding =
            FragmentStockAdjustmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initializeData()

        return root
    }


    private fun initializeData() {

        mainActivity = activity as MainActivity
        stockAdjustmentViewModel =
            ViewModelProvider(this).get(StockAdjustmentViewModel::class.java)

        cd = ConnectionDetector(activity)
        sharedpreferenceHandler = SharedpreferenceHandler(requireContext())
        sessionToken = "Bearer " + sharedpreferenceHandler.getData(
            SharedpreferenceHandler.WAREHOUSE_TOKEN,
            ""
        )!!
        locationCode =
            sharedpreferenceHandler.getData(
                SharedpreferenceHandler.WAREHOUSE_TO_LOCATION_CODE,
                ""
            )!!
        userCode = sharedpreferenceHandler.getData(
            SharedpreferenceHandler.WAREHOUSE_USER_ID,
            ""
        )!!
        setListeners()
        mainActivity?.BackPressed(this)

    }

    private fun setListeners() {
        fragmentStockAdjustmentBinding?.edtModel?.requestFocus()
        fragmentStockAdjustmentBinding?.edtModel?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                isUsingScanningDevice = true
            }
        }
        fragmentStockAdjustmentBinding?.edtModel?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                val length = text?.length!!
                if (currentModelEdittextLength - length == 1)
                    isUsingScanningDevice = false
                else if (length - currentModelEdittextLength == 1)
                    isUsingScanningDevice = false
                else if (length == 0)
                    isUsingScanningDevice = true
            }

            override fun afterTextChanged(text: Editable?) {
                currentModelEdittextLength = text?.length!!
                if (text?.toString()?.equals("") == false) {
                    if (text?.length == 1) {
                        isUsingScanningDevice = false
                    }
                    if (isUsingScanningDevice) {
                        scanProductModel(userCode, locationCode, text?.toString(), sessionToken)
                    }
                } else
                    isUsingScanningDevice = true

            }

        })


        fragmentStockAdjustmentBinding?.imageButtonModelSubmit?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            val modelcode = fragmentStockAdjustmentBinding?.edtModel?.text?.toString()
            scanProductModel(userCode, locationCode, modelcode!!, sessionToken)
        }

        fragmentStockAdjustmentBinding?.btnAdd?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            if (modelSkuList?.size > 0) {
                val modelCode = fragmentStockAdjustmentBinding?.edtModel?.text?.toString()
                val itemList = ArrayList<StockAdjustmentAddRequest.adjust>()
                for (model in modelSkuList) {
                    val modelItem = StockAdjustmentAddRequest.adjust(
                        model.COUNT.toString(), model.DIFF.toString(),
                        model.INSTOCK.toString(), model.ITEMCODE
                    )
                    itemList.add(modelItem)
                }
                val addStockAdjustmentAddRequest = StockAdjustmentAddRequest(itemList)
                addAdjustment(
                    userCode,
                    locationCode,
                    modelCode!!,
                    totalStock.toString(),
                    totalCount.toString(),
                    totalDiff.toString(),
                    sessionToken,
                    addStockAdjustmentAddRequest
                )
            } else {
                Toast.makeText(requireContext(), "Item list are empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    private val getContent =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (it.resultCode == Activity.RESULT_OK) {
//                val barcode = it?.data?.extras?.get("BarcodeResult").toString()
//                scanProductModel(userCode, locationCode, barcode!!, sessionToken)
//            }
//        }

    private fun settingRecyclerview(list: ArrayList<StockAdjustmentScanModelResponse.StockAdjustmentScanModelResponseItem>) {
        if (list.size > 0)
            fragmentStockAdjustmentBinding?.horizontalScrollView4?.visibility = View.VISIBLE
        else
            fragmentStockAdjustmentBinding?.horizontalScrollView4?.visibility = View.GONE
        stockAdjustmentItemsAdpater =
            StockAdjustmentItemRecyclerviewAdapter(list!!, requireContext())

        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragmentStockAdjustmentBinding?.recyclerView?.layoutManager = manager
        fragmentStockAdjustmentBinding?.recyclerView?.adapter = stockAdjustmentItemsAdpater

        stockAdjustmentItemsAdpater?.onCountChange = { currentItem ->
            totalDiff = 0
            totalCount = 0
            totalStock = 0
            for (skuItem in modelSkuList) {
                if (skuItem.equals(currentItem.ITEMCODE)) {
                    skuItem.DIFF = currentItem.DIFF
                    skuItem.COUNT = currentItem.COUNT
                }
                totalDiff += skuItem.DIFF!!
                totalCount += skuItem.COUNT!!
                totalStock += skuItem.INSTOCK!!
            }

            fragmentStockAdjustmentBinding?.tvTotalStock?.text = "$totalStock"
            fragmentStockAdjustmentBinding?.tvTotalCount?.text = "$totalCount"
            fragmentStockAdjustmentBinding?.tvTotalDiff?.text = "$totalDiff"
        }
    }

    fun scanProductModel(
        userCode: String,
        locationCode: String,
        modelCode: String,
        sessionToken: String
    ) {
        mainActivity?.hideSoftKeyboard()
        stockAdjustmentViewModel.scanModelStockAdjustment(
            userCode, locationCode, modelCode, sessionToken
        )
        stockAdjustmentViewModel.scanStockAdjustmentResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        totalStock = 0
                        totalCount = 0
                        totalDiff = 0
                        if (response.size > 0) {
                            modelSkuList?.clear()
                            modelSkuList.addAll(response)
                            for (skuItem in modelSkuList) {
//                                skuItem.INSTOCK = 0
//                                skuItem.COUNT = 0
//                                skuItem.DIFF = 0
                                totalStock += skuItem.INSTOCK!!
                                totalCount += skuItem.COUNT!!
                                totalDiff += skuItem.DIFF!!
                            }
                            fragmentStockAdjustmentBinding?.tvTotalStock?.text = "$totalStock"
                            fragmentStockAdjustmentBinding?.tvTotalCount?.text = "$totalCount"
                            fragmentStockAdjustmentBinding?.tvTotalDiff?.text = "$totalDiff"
                            settingRecyclerview(modelSkuList)
                        } else {
                            Toast.makeText(requireContext(), "No Record Found", Toast.LENGTH_SHORT)
                                .show()
                        }
                        checkForStockAdjustment(locationCode, modelCode)
                    }
                    stockAdjustmentViewModel.scanStockAdjustmentResponse.value = null
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    if (it.statusCode == 401) {
                        Toast.makeText(
                            requireContext(),
                            "Your session token expired. Please logout and login again",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else
                        it.message?.let { message ->
                            if (message.contains("[") && message.contains("Failed_Message")) {
                                try {
                                    var msg = message?.substringAfter("[")
                                    msg = msg.substringBefore("]")
//                                    val jsonArray = JSONArray(msg)
                                    val jsonObj = JSONObject(msg)
                                    mainActivity?.showMessage(
                                        jsonObj.get("Failed_Message").toString()
                                    )
                                } catch (exception: Exception) {
                                    exception.printStackTrace()
                                }
                            } else
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
                                    .show()
                        }
                    stockAdjustmentViewModel.scanStockAdjustmentResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    fun checkForStockAdjustment(
        storeCode: String, styleCode: String
    ) {
        stockAdjustmentViewModel.checkSkuForAdjustment(storeCode, styleCode)
        stockAdjustmentViewModel.checkStockAdjustmentResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (modelSkuList.size > 0) {
                            modelSkuList.forEach {
                                for (item in response.pickListDetails!!) {
                                    if (item?.item.equals(it.ITEMCODE)) {
                                        it.INSTOCK = item?.quantity
                                        it.COUNT = item?.quantity
                                    }
                                }
                            }
                        }
                        settingRecyclerview(modelSkuList)
                    }
                    stockAdjustmentViewModel.checkStockAdjustmentResponse.value = null
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    it.message?.let { message ->
                        if (message.contains("Failed_Response")) {
                            try {
                                val jsonObj = JSONObject(message)
                                mainActivity?.showMessage(
                                    jsonObj.get("Failed_Response").toString()
                                )
                            } catch (exception: Exception) {
                                exception.printStackTrace()
                            }
                        } else
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                    stockAdjustmentViewModel.checkStockAdjustmentResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    fun addAdjustment(
        userCode: String,
        locationCode: String,
        modelCode: String,
        totalQty: String,
        totalCount: String,
        totalDiffQty: String,
        sessionToken: String,
        addStockAdjustmentAddRequest: StockAdjustmentAddRequest
    ) {
        stockAdjustmentViewModel.addStockAdjustment(
            userCode,
            locationCode,
            modelCode,
            totalQty,
            totalCount,
            totalDiffQty,
            sessionToken,
            addStockAdjustmentAddRequest
        )
        stockAdjustmentViewModel.addStockAdjustmentResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.size > 0) {
                            if (response.get(0)?.Success_Message?.contains("Success") == true) {
                                Toast.makeText(
                                    requireContext(),
                                    "Stock Adjustment Done Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                fragmentStockAdjustmentBinding?.edtModel?.setText("")
                                fragmentStockAdjustmentBinding?.edtModel?.requestFocus()
                                modelSkuList = ArrayList()
                                settingRecyclerview(modelSkuList)
                            }
                        }
                        stockAdjustmentViewModel.addStockAdjustmentResponse.value = null
                    }
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    if (it.statusCode == 401) {
                        Toast.makeText(
                            requireContext(),
                            "Your session token expired. Please logout and login again",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else
                        it.message?.let { message ->
                            if (message.contains("Failed_Response")) {
                                try {
                                    val jsonObj = JSONObject(message)
                                    mainActivity?.showMessage(
                                        jsonObj.get("Failed_Response").toString()
                                    )
                                } catch (exception: Exception) {
                                    exception.printStackTrace()
                                }
                            } else
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                    stockAdjustmentViewModel.addStockAdjustmentResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

}