package com.armada.storeapp.ui.home.warehouse_transactions.stock_receive

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.AddStockReceiptRequest
import com.armada.storeapp.data.model.response.OpenStockReceiptDocumentResponse
import com.armada.storeapp.databinding.FragmentAddStockReceiptBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.warehouse_transactions.stock_receive.adapter.StockItemRecyclerviewAdapter
import com.armada.storeapp.ui.utils.ConnectionDetector
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddStockReceiptFragment : Fragment() {
    private var TAG = AddStockReceiptFragment::class.java.simpleName
    var cd: ConnectionDetector? = null
    private var fragmentAddStockReceiptBinding: FragmentAddStockReceiptBinding? = null
    private var mainActivity: MainActivity? = null
    private val binding get() = fragmentAddStockReceiptBinding!!
    lateinit var stockReceiveViewModel: StockReceiveViewModel
    var stockItemRecyclerviewAdapter: StockItemRecyclerviewAdapter? = null
    var sessionToken = ""
    var userCode = ""
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    var stockItemList =
        ArrayList<OpenStockReceiptDocumentResponse.OpenStockReceiptDocumentResponseItem>()
    var fromLocation = ""
    var toLocation = ""
    var documentNo = ""
    var sharedBundle = Bundle()
    var totalReceiptQty = 0
    var totalScannedQty = 0

    var isUsingScanningDevice = true
    private var currentItemEditTextLength = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentAddStockReceiptBinding =
            FragmentAddStockReceiptBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initializeData()

        return root
    }


    private fun initializeData() {

        mainActivity = activity as MainActivity
        stockReceiveViewModel =
            ViewModelProvider(this).get(StockReceiveViewModel::class.java)

        cd = ConnectionDetector(activity)
        sharedpreferenceHandler = SharedpreferenceHandler(requireContext())
        sessionToken = "Bearer " + SharedpreferenceHandler(requireContext()).getData(
            SharedpreferenceHandler.WAREHOUSE_TOKEN,
            ""
        )!!
        userCode = sharedpreferenceHandler.getData(
            SharedpreferenceHandler.WAREHOUSE_USER_ID,
            ""
        )!!

        if (arguments != null) {
            try {
                sharedBundle = arguments!!
                val documentString = arguments?.getString(Constants.OPEN_DOCUMENT)
                val gson = Gson()
                stockItemList =
                    gson.fromJson(documentString, OpenStockReceiptDocumentResponse::class.java)
                settingRecyclerview(stockItemList!!)
                for (stockitem in stockItemList) {
                    totalReceiptQty += stockitem.RQTY!!
                    fragmentAddStockReceiptBinding?.tvTotalReceiptQty?.text = "$totalReceiptQty"
                }
                fromLocation = arguments?.getString(Constants.FROM_LOCATION, "")!!
                toLocation = arguments?.getString(Constants.TO_LOCATION, "")!!
                documentNo = arguments?.getString(Constants.DOCUMENT_NO, "")!!
                fragmentAddStockReceiptBinding?.tvFromLocation?.text = fromLocation
                fragmentAddStockReceiptBinding?.tvToLocation?.text = toLocation
                fragmentAddStockReceiptBinding?.tvRefNo?.text = documentNo
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        setListeners()
        fragmentAddStockReceiptBinding?.edtItemCode?.setSelectAllOnFocus(true)
        fragmentAddStockReceiptBinding?.edtItemCode?.requestFocus()
        mainActivity?.BackPressed(this)
    }

    fun setListeners() {
        fragmentAddStockReceiptBinding?.btnAddStock?.setOnClickListener {
        fragmentAddStockReceiptBinding?.btnAddStock?.isEnabled=false
            mainActivity?.hideSoftKeyboard()
            var itemScanned = 0
            var totalQty = 0
            var totalReqQty = 0
            val receiptList = ArrayList<AddStockReceiptRequest.Item>()
            for (stockitem in stockItemList) {
                if (stockitem.QUANTITY!! > 0) {
                    itemScanned++
                }
                totalReqQty += stockitem?.RQTY!!
                totalQty += stockitem?.QUANTITY!!
                val receipt = AddStockReceiptRequest.Item(
                    stockitem.BARCODE.toString(), stockitem.QUANTITY.toString(),
                    stockitem.ITEMCODE, stockitem.ITEMNAME, stockitem.RQTY.toString()
                )
                receiptList.add(receipt)

            }
            if (totalQty == totalReqQty) {
                var userRemarks = fragmentAddStockReceiptBinding?.edtUserRemarks?.text?.toString()
                if (userRemarks?.equals("") == true)
                    userRemarks = "Stock Receive"
                val addStockReceiptRequest = AddStockReceiptRequest(receiptList)
                addStockReceipt(
                    documentNo,
                    "Local Transfer",
                    userCode,
                    fromLocation,
                    toLocation,
                    userRemarks!!,
                    totalQty?.toString(),
                    totalReqQty?.toString(),
                    "",
                    "",
                    "1",
                    "",
                    sessionToken, addStockReceiptRequest
                )


            } else {
                fragmentAddStockReceiptBinding?.btnAddStock?.isEnabled=true
                Toast.makeText(
                    requireContext(),
                    "Please scan all items to add Stock",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        fragmentAddStockReceiptBinding?.edtItemCode?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                isUsingScanningDevice = true
            }
        }

        fragmentAddStockReceiptBinding?.edtItemCode?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                val length = text?.length!!
                if (currentItemEditTextLength - length == 1)
                    isUsingScanningDevice = false
                else if (length - currentItemEditTextLength == 1)
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
                        scanItemApi(text?.toString(), sessionToken)
                    }
                } else
                    isUsingScanningDevice = true

            }

        })

        fragmentAddStockReceiptBinding?.imageButtonBinSubmit?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            val barcode = fragmentAddStockReceiptBinding?.edtItemCode?.text?.toString()
            if (barcode.equals(""))
                Toast.makeText(requireContext(), "Please enter itemcode", Toast.LENGTH_SHORT).show()
            else
                scanItemApi(barcode!!, sessionToken)

        }

    }
//
//    private val getContent =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (it.resultCode == Activity.RESULT_OK) {
//                val barcode = it?.data?.extras?.get("BarcodeResult").toString()
//                fragmentAddStockReceiptBinding?.edtItemCode?.setText(barcode)
//                scanItemApi(barcode, sessionToken)
//            }
//        }

    private fun settingRecyclerview(list: ArrayList<OpenStockReceiptDocumentResponse.OpenStockReceiptDocumentResponseItem>) {
        stockItemRecyclerviewAdapter = StockItemRecyclerviewAdapter(list!!, requireContext())

        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragmentAddStockReceiptBinding?.recyclerView?.layoutManager = manager
        fragmentAddStockReceiptBinding?.recyclerView?.adapter = stockItemRecyclerviewAdapter
    }

    fun scanItemApi(barcode: String, sessionToken: String) {
        mainActivity?.hideSoftKeyboard()
        stockReceiveViewModel.scanItem(barcode, sessionToken)
        stockReceiveViewModel.scanItemResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.size > 0) {
                            var itemcountFound = 0
                            for (stockItem in stockItemList) {
                                if (stockItem?.ITEMCODE.equals(response.get(0)?.ITEMCODE)) {
                                    if (stockItem.QUANTITY!! < stockItem.RQTY!!) {
                                        stockItem.QUANTITY = stockItem.QUANTITY!! + 1
                                        totalScannedQty++
                                        fragmentAddStockReceiptBinding?.tvTotalScannedQty?.text =
                                            "$totalScannedQty"
                                    } else if (stockItem.QUANTITY == stockItem.RQTY)
                                        Toast.makeText(
                                            requireContext(),
                                            "Receipt Qty Limit Reached",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    itemcountFound++
                                    fragmentAddStockReceiptBinding?.edtItemCode?.setText("")
                                    fragmentAddStockReceiptBinding?.edtItemCode?.requestFocus()
                                    break
                                }
                            }
                            if (itemcountFound == 0) {
                                Toast.makeText(
                                    requireContext(),
                                    "Item not Found",
                                    Toast.LENGTH_SHORT
                                ).show()
                                fragmentAddStockReceiptBinding?.edtItemCode?.clearFocus()
                                fragmentAddStockReceiptBinding?.edtItemCode?.requestFocus()
                            } else
                                settingRecyclerview(stockItemList)
                        }


                    }
                    stockReceiveViewModel.scanItemResponse.value = null
                }
                is Resource.Error -> {
                    if (it.statusCode == 401) {
                        Toast.makeText(
                            requireContext(),
                            "Your session token expired. Please logout and login again",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (it.message?.contains("No records found") == true) {
                        Toast.makeText(requireContext(), "Incorrect Item", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        it.message?.let { message ->
                            Log.e(TAG, "Error: $message")
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    mainActivity?.showProgressBar(false)
                    fragmentAddStockReceiptBinding?.edtItemCode?.clearFocus()
                    fragmentAddStockReceiptBinding?.edtItemCode?.requestFocus()

                    stockReceiveViewModel.scanItemResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    fun addStockReceipt(
        transcationNo: String,
        remarks: String,
        userCode: String,
        fromLocation: String,
        toLocation: String,
        strRemarks: String,
        totalQty: String,
        totalReqQty: String,
        intransitEnabledLoc: String,
        Flocation: String,
        crossdockInType: String,
        disDate: String,
        sessionToken: String,
        addStockReceiptRequest: AddStockReceiptRequest
    ) {
        stockReceiveViewModel.addStockReceiptDocument(
            transcationNo,
            remarks,
            userCode,
            fromLocation,
            toLocation,
            strRemarks,
            totalQty,
            totalReqQty,
            intransitEnabledLoc,
            Flocation,
            crossdockInType,
            disDate,
            sessionToken, addStockReceiptRequest
        )
        stockReceiveViewModel.addStockReceiptResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.size > 0)
                            if (response.get(0)?.Success_Message?.equals("Success") == true) {
                                Toast.makeText(
                                    requireContext(),
                                    "Stock Added Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                mainActivity?.navController?.navigate(R.id.navigation_select_stock_location)
                            }

                    }
                    stockReceiveViewModel.addStockReceiptResponse.value = null
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
                    stockReceiveViewModel.addStockReceiptResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }


}