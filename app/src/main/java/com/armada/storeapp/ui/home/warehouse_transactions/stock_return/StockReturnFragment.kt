package com.armada.storeapp.ui.home.warehouse_transactions.stock_return

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.AddStockReturnRequest
import com.armada.storeapp.data.model.response.PriorityListResponse
import com.armada.storeapp.data.model.response.StockReturnItemScanResponse
import com.armada.storeapp.data.model.response.ToLocationListResponse
import com.armada.storeapp.data.model.response.TransferTypeResponseModel
import com.armada.storeapp.databinding.FragmentStockReturnBinding
import com.armada.storeapp.databinding.LayoutAlertChangeTradingItemBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.warehouse_transactions.stock_receive.adapter.TransferTypeSpinnerAdapter
import com.armada.storeapp.ui.home.warehouse_transactions.stock_return.adapter.PrioritySpinnerAdapter
import com.armada.storeapp.ui.home.warehouse_transactions.stock_return.adapter.StockReturnItemRecyclerviewAdapter
import com.armada.storeapp.ui.home.warehouse_transactions.stock_return.adapter.ToLocationSpinnerAdapter
import com.armada.storeapp.ui.utils.ConnectionDetector
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_stock_return.*
import org.json.JSONObject

@AndroidEntryPoint
class StockReturnFragment : Fragment() {
    private var TAG = StockReturnFragment::class.java.simpleName
    var cd: ConnectionDetector? = null
    private var fragmentStockReturnBinding: FragmentStockReturnBinding? = null
    private var mainActivity: MainActivity? = null
    private val binding get() = fragmentStockReturnBinding!!
    lateinit var stockReturnViewModel: StockReturnViewModel
    var sessionToken = ""
    var userCode = ""
    var fromLocationCode = ""
    var toLocationCode = ""
    var toLocationList = ArrayList<ToLocationListResponse.ToLocationListResponseItem>()
    var scannedItemList = ArrayList<StockReturnItemScanResponse>()
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    var stockReturnItemRecyclerviewAdapter: StockReturnItemRecyclerviewAdapter? = null
    var totalScannedQty = 0
    var currentScannedItem: StockReturnItemScanResponse? = null
    var isScanningItem = false
    var isScanningBin = false
    var defaultBin = ""
    var currentBincode = ""
    var currentItemcode = ""
    var hasEnabledDefaultBin = false
    var itemBinhashmap = HashMap<String, HashMap<String, StockReturnItemScanResponse>>()
    var sourceBinItemQtyHashMap = HashMap<String, HashMap<String, Int>>()
    var isUsingScanningDevice = true
    private var currentBincodeEdittextLength: Int? = 0
    private var currentItemEditTextLength: Int? = 0
    var tradingType = "TR"
    var scannedQty = 0

    var itemMap = HashMap<String, Int>()

    override
    fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentStockReturnBinding =
            FragmentStockReturnBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initializeData()

        return root
    }


    private fun initializeData() {

        mainActivity = activity as MainActivity
        stockReturnViewModel =
            ViewModelProvider(this).get(StockReturnViewModel::class.java)

        cd = ConnectionDetector(activity)
        sharedpreferenceHandler = SharedpreferenceHandler(requireContext())
        sessionToken = "Bearer " + sharedpreferenceHandler.getData(
            SharedpreferenceHandler.WAREHOUSE_TOKEN,
            ""
        )!!

        val binavailablity = sharedpreferenceHandler.getData(SharedpreferenceHandler.BIN_AVAILABLE, "")
        fragmentStockReturnBinding?.lvBin?.isInvisible = binavailablity.equals("false")
        fragmentStockReturnBinding?.textView53?.isInvisible = binavailablity.equals("false")
        fragmentStockReturnBinding?.checkBox?.isInvisible = binavailablity.equals("false")



        userCode = sharedpreferenceHandler.getData(
            SharedpreferenceHandler.WAREHOUSE_USER_ID,
            ""
        )!!
        fromLocationCode =
            sharedpreferenceHandler.getData(
                SharedpreferenceHandler.WAREHOUSE_TO_LOCATION_CODE,
                ""
            )!!
        toLocationCode = sharedpreferenceHandler.getData(
            SharedpreferenceHandler.WAREHOUSE_FROM_LOCATION_CODE,
            ""
        )!!
        if (cd?.isConnectingToInternet!!) {
            getDefaultToLocation(userCode, sessionToken)
            getTransferList(userCode, Constants.country_code, sessionToken)
            getPriorityList(userCode, Constants.country_code, sessionToken)
        } else
            Toast.makeText(
                requireContext(),
                "Please check your network connection",
                Toast.LENGTH_SHORT
            ).show()

        fragmentStockReturnBinding?.edtItemCode?.setSelectAllOnFocus(true)
        fragmentStockReturnBinding?.edtBin?.setSelectAllOnFocus(true)
        fragmentStockReturnBinding?.edtItemCode?.requestFocus()

        mainActivity?.BackPressed(this)

        fragmentStockReturnBinding?.checkBox?.setOnCheckedChangeListener { buttonView, isChecked ->
            hasEnabledDefaultBin = isChecked
        }


        fragmentStockReturnBinding?.imageButtonItemSubmit?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            val barcode = fragmentStockReturnBinding?.edtItemCode?.text?.toString()
            if (barcode.equals(""))
                Toast.makeText(requireContext(), "Please enter itemcode", Toast.LENGTH_SHORT).show()
            else
                scanBarcode(userCode, fromLocationCode, barcode!!, tradingType, sessionToken)
        }


        fragmentStockReturnBinding?.imageButtonBinSubmit?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            val barcode = fragmentStockReturnBinding?.edtBin?.text?.toString()
            val binavailablity = sharedpreferenceHandler.getData(SharedpreferenceHandler.BIN_AVAILABLE, "")
            if (binavailablity.equals("true")){
                if (barcode.equals(""))
                    Toast.makeText(requireContext(), "Please enter bincode", Toast.LENGTH_SHORT).show()
                else {
                    if (currentScannedItem != null) {
//                        getvalidskucode(fromLocationCode, barcode!!, currentScannedItem?.ItemCode!!)
                        checkItemInBin(fromLocationCode, barcode!!, currentScannedItem?.ItemCode!!)
                    } else
                        Toast.makeText(requireContext(), "Please scan item", Toast.LENGTH_SHORT).show()
                }
            } else {
//                getvalidskucode(fromLocationCode, "", currentScannedItem?.ItemCode!!)
                checkItemInBin(fromLocationCode, "", currentScannedItem?.ItemCode!!)
            }
        }

        fragmentStockReturnBinding?.edtBin?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                isUsingScanningDevice = true
            }
        }
        fragmentStockReturnBinding?.edtItemCode?.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus) {
                isUsingScanningDevice = true
            }
        }

        fragmentStockReturnBinding?.edtItemCode?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                val length = text?.length!!
                if (currentItemEditTextLength!! - length == 1)
                    isUsingScanningDevice = false
                else if (length - currentItemEditTextLength!! == 1)
                    isUsingScanningDevice = false
                else if (length == 0)
                    isUsingScanningDevice = true
            }

            override fun afterTextChanged(text: Editable?) {
                currentItemEditTextLength = text?.length!!
                if (text?.toString()?.equals("") == false) {
                    if (text?.length == 1) {
                        isUsingScanningDevice = false
                    }
                    if (isUsingScanningDevice) {
                        scanBarcode(
                            userCode,
                            fromLocationCode,
                            text?.toString(),
                            tradingType,
                            sessionToken
                        )
                    }
                } else
                    isUsingScanningDevice = true
            }
        })

        fragmentStockReturnBinding?.edtBin?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                val length = text?.length!!
                if (currentBincodeEdittextLength!! - length == 1)
                    isUsingScanningDevice = false
                else if (length - currentBincodeEdittextLength!! == 1)
                    isUsingScanningDevice = false
                else if (length == 0)
                    isUsingScanningDevice = true
            }

            override fun afterTextChanged(text: Editable?) {
                try {
                    currentBincodeEdittextLength = text?.length
                    if (text?.toString()?.equals("") == false) {
                        if (text?.length == 1) {
                            isUsingScanningDevice = false
                        }
                        if (isUsingScanningDevice) {
//                            getvalidskucode(fromLocationCode,
//                                text?.toString(),
//                                currentScannedItem?.ItemCode!!)
                            checkItemInBin(
                                fromLocationCode,
                                text?.toString(),
                                currentScannedItem?.ItemCode!!
                            )
                        }
                    } else
                        isUsingScanningDevice = true
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }

        })

        fragmentStockReturnBinding?.spinnerToLocation?.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (toLocationList?.size > 0) {
                    fragmentStockReturnBinding?.edtToLocation?.setText(toLocationList?.get(position)?.LOCNAME)
                    toLocationCode = toLocationList?.get(position)?.LOCCODE!!
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        fragmentStockReturnBinding?.spinnerTransferType?.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                try {
                    val tradeItem =
                        spinnerTransferType.selectedItem as TransferTypeResponseModel.TransferTypeResponseModelItem
                    if (!tradingType.equals(tradeItem.CODE)) {
                        if (scannedItemList.size > 0) {
                            val message =
                                "You are changing transfer type to ${tradeItem.NAME} and this will delete all the items you are scanned earlier. Are you sure to change transfer type?"
                            displayTradeDialog(message, tradeItem.CODE!!)
                            tradingType = tradeItem.CODE.toString();
//                            Toast.makeText(context, tradeItem.CODE, Toast.LENGTH_SHORT).show()
                        } else {
                            tradingType = tradeItem.CODE.toString();

//                            Toast.makeText(context, tradeItem.CODE, Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        fragmentStockReturnBinding?.btnAddStockReturn?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            var groupedByItemCodeMap = scannedItemList.groupBy { (it.ItemCode) }
            var totalQty = fragmentStockReturnBinding?.tvTotalScannedQty?.text?.toString()?.toInt()
            val returnList = ArrayList<AddStockReturnRequest.Transfer>()
            for ((itemCode, scannedItems) in groupedByItemCodeMap) {
                val groupByBincode = scannedItems.groupBy { it.binCode }
                for ((bincode, items) in groupByBincode) {
                    var qty = 0



                    items.forEach {
                        qty = qty + it.Qty?.toInt()!!
                        val returnItem = AddStockReturnRequest.Transfer(
                            it.strBarCode,
                            qty?.toString(),
                            it.ItemCode,
                            it.itemname,
                            it.binCode
                        )
                        returnList.add(returnItem)
                    }
                }
            }

            if (returnList?.size > 0) {
                val priority =
                    fragmentStockReturnBinding?.spinnerPriority?.selectedItem as PriorityListResponse.PriorityListResponseItem
                val transfer =
                    fragmentStockReturnBinding?.spinnerTransferType?.selectedItem as TransferTypeResponseModel.TransferTypeResponseModelItem
                var userRemarks = fragmentStockReturnBinding?.edtRemarks?.text?.toString()
                if (priority.U_PRIORITY != 3) {
                    Toast.makeText(
                        requireContext(),
                        "Please select priority Store Allocation",
                        Toast.LENGTH_SHORT
                    ).show()
                }
//                else if (transfer.CODE.equals("NTR")) {
//                    Toast.makeText(
//                        requireContext(),
//                        "Please select transfer type Trading Items",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
                else {
                    if (userRemarks?.equals("") == true)
                        userRemarks = "Stock Return"
                    val addStockReturnRequest = AddStockReturnRequest(returnList)
                    addStockReturn(
                        userCode,
                        fromLocationCode,
                        toLocationCode,
                        totalQty.toString(),
                        priority.U_PRIORITY.toString(),
                        transfer.CODE!!,
                        userRemarks!!,
                        sessionToken,
                        addStockReturnRequest
                    )
                }

            } else {
                Toast.makeText(
                    requireContext(),
                    "Please scan all items to add Stock",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun displayTradeDialog(message: String, tradeCode: String) {

        try {
            val alertChangeTradeBinding: LayoutAlertChangeTradingItemBinding =
                DataBindingUtil.inflate(
                    LayoutInflater.from(requireContext()),
                    R.layout.layout_alert_change_trading_item,
                    null,
                    false
                )
            alertChangeTradeBinding.txtAlertMsg?.text = message

            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(alertChangeTradeBinding.root)
            val tradeDialog = builder.show()
            tradeDialog?.setCancelable(false)


            alertChangeTradeBinding.btnNo.setOnClickListener() {
                tradeDialog?.dismiss()
            }

            alertChangeTradeBinding.btnYes.setOnClickListener() {
                tradeDialog?.dismiss()
                scannedItemList.clear()
                Log.e("settingRecyclerview", "displayTradeDialog: 417" )
                settingRecyclerview(scannedItemList)
                totalScannedQty = 0
                fragmentStockReturnBinding?.tvTotalScannedQty?.text = "$totalScannedQty"
                tradingType = tradeCode
            }
            tradeDialog?.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun settingRecyclerview(list: ArrayList<StockReturnItemScanResponse>) {
        stockReturnItemRecyclerviewAdapter =
            StockReturnItemRecyclerviewAdapter(list!!, requireContext())

        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragmentStockReturnBinding?.recyclerView?.layoutManager = manager
        fragmentStockReturnBinding?.recyclerView?.adapter = stockReturnItemRecyclerviewAdapter

        stockReturnItemRecyclerviewAdapter?.onDeleteClick = { stockReturn, position ->
            if (scannedItemList?.size > 0) {
                scannedItemList?.removeAt(position)
                stockReturnItemRecyclerviewAdapter?.notifyItemRemoved(position)
                totalScannedQty -= stockReturn.Qty?.toInt()!!
                fragmentStockReturnBinding?.tvTotalScannedQty?.text = "$totalScannedQty"
            }

        }

    }


    fun getDefaultToLocation(
        userCode: String,
        sessionToken: String
    ) {
        stockReturnViewModel.getDefaultToLocation(userCode, sessionToken)
        stockReturnViewModel.defaultToLocationResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.size > 0) {
                            if (response.get(0).DefLoc == null || response.get(0).DefLoc.equals("Not Assigned")) {
                                getToLocationList(userCode, Constants.country_code, sessionToken)
                            } else {
                                fragmentStockReturnBinding?.edtToLocation?.setText(response.get(0).DefLoc)
//                                fragmentStockReturnBinding?.lvToLocation?.visibility = View.GONE
                            }
                        }
                        stockReturnViewModel.defaultToLocationResponse.value = null
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
                            Log.e(TAG, "Error: $message")
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                    stockReturnViewModel.defaultToLocationResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    fun getToLocationList(
        userCode: String,
        countryCode: String,
        sessionToken: String
    ) {
        stockReturnViewModel.getToLocationList(userCode, countryCode, sessionToken)
        stockReturnViewModel.toLocationListResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.size > 0) {
                            fragmentStockReturnBinding?.spinnerToLocation?.adapter =
                                ToLocationSpinnerAdapter(
                                    requireContext(),
                                    response
                                )
                            toLocationList = response
                            for ((index, value) in toLocationList.withIndex()) {
                                if (toLocationCode.equals(value.LOCCODE)) {
                                    fragmentStockReturnBinding?.spinnerToLocation?.setSelection(
                                        index
                                    )
                                }
                            }
                            fragmentStockReturnBinding?.lvToLocation?.visibility = View.VISIBLE
                        }
                    }
                    stockReturnViewModel.toLocationListResponse.value = null
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
                            Log.e(TAG, "Error: $message")
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                    stockReturnViewModel.toLocationListResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }


    fun getTransferList(
        userCode: String,
        countryCode: String,
        sessionToken: String
    ) {
        stockReturnViewModel.getTransferList(userCode, countryCode, sessionToken)
        stockReturnViewModel.transferTypeListResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.size > 0) {
                            fragmentStockReturnBinding?.spinnerTransferType?.adapter =
                                TransferTypeSpinnerAdapter(
                                    requireContext(),
                                    response
                                )
                        }
                    }
                    stockReturnViewModel.transferTypeListResponse.value = null
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
                            Log.e(TAG, "Error: $message")
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                    stockReturnViewModel.transferTypeListResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }


    fun getPriorityList(
        userCode: String,
        countryCode: String,
        sessionToken: String
    ) {
        stockReturnViewModel.getPriorityList(userCode, countryCode, sessionToken)
        stockReturnViewModel.priorityListResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.size > 0) {
                            fragmentStockReturnBinding?.spinnerPriority?.adapter =
                                PrioritySpinnerAdapter(
                                    requireContext(),
                                    response
                                )

                            var index = 0
                            for (stock in response) {
                                index++
                                if (stock.NAME.equals("Store Allocation")) {
                                    fragmentStockReturnBinding?.spinnerPriority?.setSelection(index - 1)
                                }

                            }
                        }
                    }
                    stockReturnViewModel.priorityListResponse.value = null
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
                            Log.e(TAG, "Error: $message")
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                    stockReturnViewModel.priorityListResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    fun scanBarcode(
        userCode: String,
        locationCode: String,
        barcode: String,
        returnType: String,
        sessionToken: String
    ) {
        mainActivity?.hideSoftKeyboard()
        stockReturnViewModel.scanStockReturnItem(
            userCode,
            locationCode,
            barcode,
            returnType,
            sessionToken
        )
        stockReturnViewModel.stockReturnItemScanResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        try {
                            currentItemcode = response.ItemCode!!
                            currentScannedItem = response

//                            Log.e("Current scanned itm", currentItemcode + "           "+currentScannedItem.toString())
                            val binavailablity = sharedpreferenceHandler.getData(SharedpreferenceHandler.BIN_AVAILABLE, "")
                            if (binavailablity.equals("true")) {
                                if (hasEnabledDefaultBin) {
//                                    getvalidskucode(fromLocationCode, defaultBin, currentItemcode)
                                    checkItemInBin(fromLocationCode, defaultBin, currentItemcode)
                                } else {
                                    fragmentStockReturnBinding?.edtBin?.requestFocus()
                                }
                            } else {
//                                getvalidskucode(fromLocationCode, "", currentItemcode)
                                checkItemInBin(fromLocationCode, "", currentItemcode)
                            }


                        } catch (exception: Exception) {
                            exception.printStackTrace()
                            fragmentStockReturnBinding?.edtItemCode?.clearFocus()
                            fragmentStockReturnBinding?.edtItemCode?.requestFocus()
                        }
                    }
                    stockReturnViewModel.stockReturnItemScanResponse.value = null
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    fragmentStockReturnBinding?.edtItemCode?.clearFocus()
                    fragmentStockReturnBinding?.edtItemCode?.requestFocus()
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
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
                                    .show()
                        }
                    stockReturnViewModel.stockReturnItemScanResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    private fun updateScannedItem(currentItem: StockReturnItemScanResponse) {


        totalScannedQty++
        fragmentStockReturnBinding?.tvTotalScannedQty?.text =
            "$totalScannedQty"

        if (hasEnabledDefaultBin)
            currentItem.binCode = defaultBin
        else
            currentItem.binCode = currentBincode

        currentItem?.Qty = "1"

        if(scannedItemList.size==0||scannedItemList==null){

            scannedItemList.add(currentItem)
            //settingRecyclerview(scannedItemList)
        } else {
            val startingIndex = 0
            for (i in 0 until scannedItemList.size) {
                if (scannedItemList.get(i).ItemCode.equals(currentItem.ItemCode)) {
//                    scannedItemList.removeAt(i)
                    //if(i<scannedItemList.size-1){

                    currentItem.strBarCode = scannedItemList.get(i).strBarCode

                    currentItem.Qty = scannedQty.toString()
//                    currentItem.Qty = scannedItemList.get(i).Qty

                    scannedItemList.set(i, currentItem)
                    break
                    //}

                } else {
                    if(i==scannedItemList.size-1){
                        scannedItemList.add(currentItem)
                    }
                    Log.e("no scanned item", scannedItemList.toString())
                }
            }
        }
        Log.e("Scanneditemlist-after", scannedItemList.toString())
        settingRecyclerview(scannedItemList)

        fragmentStockReturnBinding?.edtItemCode?.setText("")
        fragmentStockReturnBinding?.edtItemCode?.requestFocus()
        currentScannedItem = null
        currentItemcode = ""
    }



    fun getvalidskucode(
        storeCode: String,
        bincode: String,
        itemCode: String
    ) {
        stockReturnViewModel.checkIteminBin(
            storeCode?.trim(), bincode?.trim(), itemCode?.trim()
        )
        stockReturnViewModel.checkIteminBinResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { response ->
                        mainActivity?.showProgressBar(false)
                        currentItemcode = it.data.binDetailsList?.get(0)?.skuCode.toString()


                    }
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)

//                    if (it.statusCode == 401) {
//                        Toast.makeText(
//                            requireContext(),
//                            "Your session token expired. Please logout and login again",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    } else
                    try {
                        var msg = ""
                        it.message?.let { message ->
                            Log.e(TAG, "Error: $message")
                            if (message.contains("message")) {
                                val jsonObj = JSONObject(message)
                                msg = jsonObj.get("message").toString()
                                mainActivity?.showMessage(msg)
                            } else
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                        if (msg.isEmpty()) {
                            fragmentStockReturnBinding?.edtBin?.clearFocus()
                            fragmentStockReturnBinding?.edtBin?.requestFocus()
                        } else if (msg.equals("Item not Available")) {
                            fragmentStockReturnBinding?.edtItemCode?.clearFocus()
                            fragmentStockReturnBinding?.edtItemCode?.requestFocus()
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    stockReturnViewModel.checkIteminBinResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    fun checkItemInBin(
        storeCode: String,
        bincode: String,
        itemCode: String
    ) {


        stockReturnViewModel.checkIteminBin(
            storeCode?.trim(), bincode?.trim(), itemCode?.trim()
        )
        stockReturnViewModel.checkIteminBinResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.statusCode == 1) {

//                            currentItemcode =
//                                fragmentStockReturnBinding?.edtItemCode?.text?.toString()!!

                            Log.e("Scanneditemlist-before", scannedItemList.toString())


                            currentItemcode = it.data.binDetailsList?.get(0)?.skuCode.toString()

                            try {
                                response.binDetailsList?.forEach {
                                    currentBincode = it?.binCode!!
                                    if (hasEnabledDefaultBin)
                                        defaultBin = currentBincode

                                    if (sourceBinItemQtyHashMap.containsKey(currentBincode)) {
                                        itemMap = sourceBinItemQtyHashMap.get(currentBincode)!!
//                                        if (itemMap?.containsKey(currentItemcode) == true) {

                                        if (itemMap.containsKey(currentItemcode) == true) {
                                            val qty = itemMap.get(currentItemcode)

                                            if (it.quantity!! - qty!! > 0) {
                                                val updatedItem = HashMap<String, Int>()
                                                //Log.e("before", "scannedQty: $scannedQty  qty:$qty " )

                                                val tQty = qty+1;
                                                scannedItemList = scannedItemList.map { item ->
                                                    if (item.ItemCode == currentItemcode) {
                                                        scannedQty = (item.Qty?.toIntOrNull() ?: 0) + 1
                                                        item.copy(Qty = tQty.toString())
                                                    } else {
                                                        item
                                                    }
                                                }.toCollection(ArrayList())
//                                              scannedQty = scannedQty + 1
                                                itemMap.set(currentItemcode, tQty)
//                                              updatedItem?.put(currentItemcode, tQty)
                                                sourceBinItemQtyHashMap.put(
                                                    currentBincode,
                                                    itemMap
                                                )
                                                updateScannedItem(currentScannedItem!!)
                                            } else {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Item out of stock in the bin $currentBincode",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                Log.d("else hitted" , "")
                                            }
                                            Log.d("source 1 hitted" , sourceBinItemQtyHashMap.toString())

                                        } else {

                                            itemMap.set(currentItemcode, 1)
                                            sourceBinItemQtyHashMap.put(currentBincode, itemMap)
                                            updateScannedItem(currentScannedItem!!)
                                            Log.d("first hit" , "$itemMap $scannedQty")
                                            Log.d("source 2 hitted" , sourceBinItemQtyHashMap.toString())

                                        }

                                    } else {
                                        Log.d("source 3 hitted" , sourceBinItemQtyHashMap.toString())
                                        Log.d("bincode new hit" , scannedQty.toString())
                                        val itemQtyMap = HashMap<String, Int>()
                                        itemQtyMap.put(currentItemcode, 1)
                                        sourceBinItemQtyHashMap.put(currentBincode, itemQtyMap)
                                        updateScannedItem(currentScannedItem!!)
                                    }
                                }
                            } catch (exception: Exception) {
                                exception.printStackTrace()
                                Log.d("exceptionn" , exception.printStackTrace().toString())
                                fragmentStockReturnBinding?.edtBin?.clearFocus()
                                fragmentStockReturnBinding?.edtBin?.requestFocus()
                            }

                            if (!hasEnabledDefaultBin)
                                fragmentStockReturnBinding?.edtBin?.setText("")
                        }
                    }
                    stockReturnViewModel.checkIteminBinResponse.value = null
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)

//                    if (it.statusCode == 401) {
//                        Toast.makeText(
//                            requireContext(),
//                            "Your session token expired. Please logout and login again",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    } else
                    try {
                        var msg = ""
                        it.message?.let { message ->
                            Log.e(TAG, "Error: $message")
                            if (message.contains("message")) {
                                val jsonObj = JSONObject(message)
                                msg = jsonObj.get("message").toString()
                                mainActivity?.showMessage(msg)
                            } else
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                        if (msg.isEmpty()) {
                            fragmentStockReturnBinding?.edtBin?.clearFocus()
                            fragmentStockReturnBinding?.edtBin?.requestFocus()
                        } else if (msg.equals("Item not Available")) {
                            fragmentStockReturnBinding?.edtItemCode?.clearFocus()
                            fragmentStockReturnBinding?.edtItemCode?.requestFocus()
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    stockReturnViewModel.checkIteminBinResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }


    fun addStockReturn(
        userCode: String,
        fromLocation: String,
        toLocation: String,
        totalQty: String,
        priority: String,
        typeOfTransfer: String,
        userRemarks: String,
        sessionToken: String,
        addStockReturnRequest: AddStockReturnRequest
    ) {
        stockReturnViewModel.addStockReturn(
            userCode,
            fromLocation,
            toLocation,
            totalQty,
            priority,
            typeOfTransfer,
            userRemarks,
            sessionToken,
            addStockReturnRequest
        )
        stockReturnViewModel.addStockReturnResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.Response?.contains("Success") == true) {
                            mainActivity?.showMessage("Stock Returned Successfully")
                            fragmentStockReturnBinding?.edtBin?.setText("")
                            fragmentStockReturnBinding?.edtItemCode?.setText("")
                            fragmentStockReturnBinding?.edtItemCode?.clearFocus()
                            fragmentStockReturnBinding?.edtBin?.clearFocus()
                            fragmentStockReturnBinding?.edtRemarks?.setText("")
                            fragmentStockReturnBinding?.edtRemarks?.clearFocus()
                            scannedItemList?.clear()
                            Log.e("settingRecyclerview", "displayTradeDialog: 1011" )
                            settingRecyclerview(scannedItemList)
                            fragmentStockReturnBinding?.edtItemCode?.clearFocus()
                            fragmentStockReturnBinding?.edtItemCode?.requestFocus()
                            fragmentStockReturnBinding?.edtBin?.setText("")
                            fragmentStockReturnBinding?.tvTotalScannedQty?.text = "0"
                            totalScannedQty = 0
                            currentItemcode = ""
                            currentBincode = ""
                            currentScannedItem = null
                            fragmentStockReturnBinding?.edtRemarks?.setText("")

                        }
                    }
                    stockReturnViewModel.priorityListResponse.value = null
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
                            Log.e(TAG, "Error: $message")
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                    stockReturnViewModel.priorityListResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

}

