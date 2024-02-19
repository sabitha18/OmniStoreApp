package com.armada.storeapp.ui.home.warehouse_transactions.stock_receive

import android.app.DatePickerDialog
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
import com.armada.storeapp.data.model.response.FromLocationListResponse
import com.armada.storeapp.data.model.response.StockReceiptDocumentResponseModel
import com.armada.storeapp.data.model.response.TransferTypeResponseModel
import com.armada.storeapp.databinding.FragmentSelectStockLocationBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.warehouse_transactions.stock_receive.adapter.StockReceiptRecyclerviewAdapter
import com.armada.storeapp.ui.home.warehouse_transactions.stock_receive.adapter.TransferTypeSpinnerAdapter
import com.armada.storeapp.ui.utils.ConnectionDetector
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SelectStockLocationFragment : Fragment() {
    private var TAG = SelectStockLocationFragment::class.java.simpleName
    var cd: ConnectionDetector? = null
    private var fragmentSelectStockLocationBinding: FragmentSelectStockLocationBinding? = null
    private var mainActivity: MainActivity? = null
    var sessionToken = ""
    var userCode = ""
    var locationCode = ""
    var fromLocation = ""
    var sharedBundle = Bundle()

    //    var selectedFromLocation = ""
    var transferTypeList = ArrayList<TransferTypeResponseModel.TransferTypeResponseModelItem>()
    var fromLocationList = ArrayList<FromLocationListResponse.FromLocationListResponseItem>()
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    private val binding get() = fragmentSelectStockLocationBinding!!
    lateinit var stockreceiveViewModel: StockReceiveViewModel
    var stockReceiptAdapter: StockReceiptRecyclerviewAdapter? = null

    var isUsingScanningDevice = true
    private var currentDocumentNoLength = 0

    var totalPages = 0
    var currentPage = 0
    var startIndex = 0
    var endIndex = 0
    val noOfItemsToShow = 10
    var currentPageList =
        ArrayList<StockReceiptDocumentResponseModel.StockReceiptDocumentResponseModelItem>()
    var currentStockReceiveList =
        ArrayList<StockReceiptDocumentResponseModel.StockReceiptDocumentResponseModelItem>()
    var pageItemsHashmap =
        HashMap<Int, ArrayList<StockReceiptDocumentResponseModel.StockReceiptDocumentResponseModelItem>>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentSelectStockLocationBinding =
            FragmentSelectStockLocationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initializeData()

        return root
    }


    private fun initializeData() {

        mainActivity = activity as MainActivity
        stockreceiveViewModel =
            ViewModelProvider(this).get(StockReceiveViewModel::class.java)

        cd = ConnectionDetector(activity)
        sharedpreferenceHandler = SharedpreferenceHandler(requireContext())
        sessionToken = "Bearer " + sharedpreferenceHandler.getData(
            SharedpreferenceHandler.WAREHOUSE_TOKEN,
            ""
        )!!
        userCode = sharedpreferenceHandler.getData(
            SharedpreferenceHandler.WAREHOUSE_USER_ID,
            ""
        )!!
        fromLocation = sharedpreferenceHandler.getData(
            SharedpreferenceHandler.WAREHOUSE_FROM_LOCATION_CODE,
            ""
        )!!
        locationCode = sharedpreferenceHandler.getData(
            SharedpreferenceHandler.WAREHOUSE_TO_LOCATION_CODE,
            ""
        )!!

        fragmentSelectStockLocationBinding?.edtToLocation?.setText(locationCode)
        fragmentSelectStockLocationBinding?.edtFromLocation?.setText(fromLocation)

        if (cd?.isConnectingToInternet!!) {
            getTransferTypes(sessionToken)
//            getFromLocation(userCode, "KWT", sessionToken)
        }

        fragmentSelectStockLocationBinding?.imageButtonCalendar?.setOnClickListener {
            showDatePickerDialog()
        }

//        fragmentSelectStockLocationBinding?.btnSubmit?.setOnClickListener {
////            val selectedFromLocation =
////                fragmentSelectStockLocationBinding?.spinnerFromLocation?.selectedItem as FromLocationListResponse.FromLocationListResponseItem
//            val selectedTransferType =
//                fragmentSelectStockLocationBinding?.spinnerTransferType?.selectedItem as TransferTypeResponseModel.TransferTypeResponseModelItem
//            getDocuments()
//        }

        mainActivity?.BackPressed(this)
        setCurrentDate()

        fragmentSelectStockLocationBinding?.edtDocumentNo?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                isUsingScanningDevice = true
            }
        }
        fragmentSelectStockLocationBinding?.imageViewDocSubmit?.setOnClickListener {
            getDocuments()
        }

        fragmentSelectStockLocationBinding?.edtDocumentNo?.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                val length = text?.length!!
                if (currentDocumentNoLength - length == 1)
                    isUsingScanningDevice = false
                else if (length - currentDocumentNoLength == 1)
                    isUsingScanningDevice = false
                else if (length == 0)
                    isUsingScanningDevice = true
            }

            override fun afterTextChanged(text: Editable?) {
                currentDocumentNoLength = text?.length!!
                if (text?.toString()?.equals("") == false) {
                    if (text?.length == 1) {
                        isUsingScanningDevice = false
                    }
                    if (isUsingScanningDevice) {
                        getDocuments()
                    }
                } else {
                    isUsingScanningDevice = true
                    getDocuments()
                }


            }

        })

        fragmentSelectStockLocationBinding?.btnPageNext?.setOnClickListener {
            if (currentPage < totalPages)
                forwardPAges()
            fragmentSelectStockLocationBinding?.horizontalScrollView?.scrollX = 0

        }
        fragmentSelectStockLocationBinding?.btnPageBack?.setOnClickListener {
            if (currentPage > 1)
                backwardPages()
            fragmentSelectStockLocationBinding?.horizontalScrollView?.scrollX = 0
        }


    }

    override fun onResume() {
        super.onResume()
        try {
            getDocuments()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun getDocuments() {
        mainActivity?.hideSoftKeyboard()
        val docDate = fragmentSelectStockLocationBinding?.edtDate?.text?.toString()
        val documNo = fragmentSelectStockLocationBinding?.edtDocumentNo?.text?.toString()
        getStockReceiptDocuments(
            locationCode,
            userCode,
            docDate!!,
            fromLocation,
            documNo!!,
            sessionToken
        )
    }


    fun getTransferTypes(
        sessionToken: String
    ) {
        stockreceiveViewModel.getTransferTypes(sessionToken)
        stockreceiveViewModel.transferTypeListResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        transferTypeList = response
                        binding.spinnerTransferType?.setAdapter(
                            TransferTypeSpinnerAdapter(
                                requireContext(), response
                            )
                        )
                    }
                    stockreceiveViewModel.transferTypeListResponse.value = null
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
                    stockreceiveViewModel.transferTypeListResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    fun getFromLocation(
        userCode: String,
        countryCode: String,
        sessionToken: String
    ) {
        stockreceiveViewModel.getFromLocationList(userCode, countryCode, sessionToken)
        stockreceiveViewModel.fromLocationListResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        fromLocationList = response
//                        binding.spinnerFromLocation?.setAdapter(
//                            FromLocationSpinnerAdapter(
//                                requireContext(), response
//                            )
//                        )
                    }
                    stockreceiveViewModel.fromLocationListResponse.value = null
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
                    stockreceiveViewModel.fromLocationListResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    fun getStockReceiptDocuments(
        locationCode: String,
        userCode: String,
        docDate: String,
        fromLocation: String,
        docNumber: String,
        sessionToken: String
    ) {
        stockreceiveViewModel.getStockReceiptDocumentList(
            locationCode,
            userCode,
            docDate,
            fromLocation,
            docNumber,
            sessionToken
        )
        currentStockReceiveList = ArrayList()
        stockreceiveViewModel.stockReceiptDocumentResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)

                    it.data?.let { response ->
                        if (response.size > 0) {
                            currentStockReceiveList = response
                            currentStockReceiveList.sortByDescending { it.ID }
                        } else {
                            Toast.makeText(requireContext(), "No Record Found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    initialhandlePages()
                    stockreceiveViewModel.stockReceiptDocumentResponse.value = null
                }
                is Resource.Error -> {

                    mainActivity?.showProgressBar(false)
                    currentStockReceiveList.clear()
                    settingRecyclerview(currentStockReceiveList)
                    if (it.statusCode == 401) {
                        Toast.makeText(
                            requireContext(),
                            "Your session token expired. Please logout and login again",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else
                        it.message?.let { message ->
                            Log.e(TAG, "Error: $message")
                            if (message?.contains("No Records Found To Show"))
                                Toast.makeText(
                                    requireContext(),
                                    "No Records Found To Show...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            else
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }

                    stockreceiveViewModel.stockReceiptDocumentResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    fun setCurrentDate() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        fragmentSelectStockLocationBinding?.edtDate?.setText("$year-$month-$day")
    }

    fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            mainActivity!!,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val selectedMonth = monthOfYear + 1
                fragmentSelectStockLocationBinding?.edtDate?.setText("$year-$selectedMonth-$dayOfMonth")
                getDocuments()

            },
            year,
            month,
            day
        )


        datePickerDialog.show()
    }

    private fun settingRecyclerview(list: java.util.ArrayList<StockReceiptDocumentResponseModel.StockReceiptDocumentResponseModelItem>) {

        if (list == null || list.size == 0) {
            fragmentSelectStockLocationBinding?.btnPageNext?.visibility = View.GONE
            fragmentSelectStockLocationBinding?.btnPageBack?.visibility = View.GONE
            fragmentSelectStockLocationBinding?.tvPage?.visibility = View.GONE
        } else {
            fragmentSelectStockLocationBinding?.btnPageNext?.visibility = View.VISIBLE
            fragmentSelectStockLocationBinding?.btnPageBack?.visibility = View.VISIBLE
            fragmentSelectStockLocationBinding?.tvPage?.visibility = View.VISIBLE
        }

        stockReceiptAdapter = StockReceiptRecyclerviewAdapter(list!!, requireContext())

        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragmentSelectStockLocationBinding?.recyclerView?.layoutManager = manager
        fragmentSelectStockLocationBinding?.recyclerView?.adapter = stockReceiptAdapter

        stockReceiptAdapter?.onReceiveClick = { stockReceipt ->
            openDocument(
                stockReceipt?.ID!!.toString(), stockReceipt?.TRNSNO!!, stockReceipt?.REMARKS!!,
                userCode, stockReceipt?.FRMLOC!!, stockReceipt?.TOLOC!!, sessionToken
            )
        }


    }

    fun openDocument(
        id: String,
        transcationNo: String,
        remarks: String,
        userCode: String,
        fromLocation: String,
        toLocation: String,
        sessionToken: String
    ) {
        stockreceiveViewModel.openStockReceiptDocument(
            id, transcationNo, remarks, userCode, fromLocation, toLocation, sessionToken
        )
        stockreceiveViewModel.openStockReceiptDocumentResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.size > 0) {
                            //navigate to next screen
                            val gson = Gson()
                            val listString = gson.toJson(response)
                            sharedBundle.putString(Constants.FROM_LOCATION, fromLocation)
                            sharedBundle.putString(Constants.TO_LOCATION, toLocation)
                            sharedBundle.putString(Constants.DOCUMENT_NO, transcationNo)
                            sharedBundle.putString(Constants.OPEN_DOCUMENT, listString)
                            mainActivity?.navController?.navigate(
                                R.id.navigation_add_stock_receipt,
                                sharedBundle
                            )

                        } else {
                            Toast.makeText(requireContext(), "No Record Found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    stockreceiveViewModel.openStockReceiptDocumentResponse.value = null
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
                    stockreceiveViewModel.openStockReceiptDocumentResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }


    fun initialhandlePages() {
        pageItemsHashmap = HashMap()

        val totalPicklistItems = currentStockReceiveList.size
        currentPage = 1
        currentPageList = ArrayList()
        if (totalPicklistItems <= noOfItemsToShow) {
            currentPageList = currentStockReceiveList
            totalPages = 1
        } else {
            if (totalPicklistItems % noOfItemsToShow == 0)
                totalPages = totalPicklistItems / noOfItemsToShow
            else
                totalPages = totalPicklistItems / noOfItemsToShow + 1

            startIndex = 0
            endIndex = 0 + (noOfItemsToShow - 1)
            for ((index, value) in currentStockReceiveList.withIndex()) {
                if (index >= startIndex && index <= endIndex)
                    currentPageList.add(value)
            }
            pageItemsHashmap.put(currentPage, currentPageList)
        }

        fragmentSelectStockLocationBinding?.tvPage?.text = "Page $currentPage out of $totalPages"
        settingRecyclerview(currentPageList)

    }

    fun forwardPAges() {
        currentPage += 1
        currentPageList = ArrayList()
        if (pageItemsHashmap.containsKey(currentPage)) {
            currentPageList = pageItemsHashmap.get(currentPage)!!
        } else {
            startIndex += noOfItemsToShow
            endIndex += noOfItemsToShow


            for ((index, value) in currentStockReceiveList.withIndex()) {
                if (index >= startIndex && index <= endIndex) {
                    currentPageList.add(value)
                }

            }

            pageItemsHashmap.put(currentPage, currentPageList)
        }
        fragmentSelectStockLocationBinding?.tvPage?.text = "Page $currentPage out of $totalPages"
        settingRecyclerview(currentPageList)
    }

    fun backwardPages() {
        currentPageList = ArrayList()
        currentPage -= 1
        currentPageList = pageItemsHashmap.get(currentPage)!!
        fragmentSelectStockLocationBinding?.tvPage?.text = "Page $currentPage out of $totalPages"
        settingRecyclerview(currentPageList)

    }

}