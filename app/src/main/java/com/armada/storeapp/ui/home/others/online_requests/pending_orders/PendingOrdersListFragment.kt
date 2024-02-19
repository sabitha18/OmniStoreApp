package com.armada.storeapp.ui.home.others.online_requests.pending_orders

import android.app.DatePickerDialog
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
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.ShopPickOrdersResponseModel
import com.armada.storeapp.databinding.FragmentPendingOrdersListBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.others.online_requests.pending_orders.adapter.DocumentOpenListener
import com.armada.storeapp.ui.home.others.online_requests.pending_orders.adapter.PendingOrdersAdapter
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PendingOrdersListFragment : Fragment(), DocumentOpenListener {
    private lateinit var viewModel: PendingOrdersListViewModel
    private lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    private lateinit var fragmentPendingOrdersListBinding: FragmentPendingOrdersListBinding
    private lateinit var mainActivity: MainActivity
    private var pendingOrdersList: ArrayList<ShopPickOrdersResponseModel.ShopPickOrdersResponseModelItem>? =
        null
    private var pendingOrdersAdapter: PendingOrdersAdapter? = null
    var shopLocationCode = ""
    var token = ""
    var selectedFromDate = ""
    var selectedToDate = ""

    var isUsingScanningDevice = true
    private var currentDocEdittextLength = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel =
            ViewModelProvider(this).get(PendingOrdersListViewModel::class.java)

        fragmentPendingOrdersListBinding =
            FragmentPendingOrdersListBinding.inflate(inflater, container, false)
        val root: View = fragmentPendingOrdersListBinding.root
        mainActivity = activity as MainActivity
        pendingOrdersList = ArrayList()
        sharedpreferenceHandler = SharedpreferenceHandler(requireContext())
        shopLocationCode =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_CODE, "")!!
//        shopLocationCode = "T0001"
        token = sharedpreferenceHandler.getData(SharedpreferenceHandler.WAREHOUSE_TOKEN, "")!!
        token = "Bearer $token"
        setCurrentDate()
        callPendingOrdersApi()
        mainActivity?.BackPressed(this)
        setListeners()
        return root
    }

    private fun setListeners() {
        fragmentPendingOrdersListBinding?.edtDocumentNo?.requestFocus()
        fragmentPendingOrdersListBinding?.calendarFromDate?.setOnClickListener {
            showDatePickerDialog(true)
        }
        fragmentPendingOrdersListBinding?.calendarToDate?.setOnClickListener {
            showDatePickerDialog(false)
        }
        fragmentPendingOrdersListBinding?.edtDocumentNo?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                isUsingScanningDevice = true
            }
        }
        fragmentPendingOrdersListBinding?.imageButtonDocSubmit?.setOnClickListener {
            filterOrdersByData()
        }
        fragmentPendingOrdersListBinding?.edtDocumentNo?.addTextChangedListener(object :
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
                if (currentDocEdittextLength - length == 1)
                    isUsingScanningDevice = false
                else if (length - currentDocEdittextLength == 1)
                    isUsingScanningDevice = false
                else if (length == 0)
                    isUsingScanningDevice = true
            }

            override fun afterTextChanged(text: Editable?) {
                currentDocEdittextLength = text?.length!!
                if (text?.toString()?.equals("") == false) {
                    if (text?.length == 1) {
                        isUsingScanningDevice = false
                    }
                    if (isUsingScanningDevice) {
                        filterOrdersByData()
                    }
                } else
                    isUsingScanningDevice = true


            }

        })

        fragmentPendingOrdersListBinding?.btnSearch?.setOnClickListener {
            filterOrdersByData()
        }

    }

    override fun onResume() {
        super.onResume()
        mainActivity?.setTitle("Shop Pick")
    }

    fun filterOrdersByData() {
        mainActivity?.hideSoftKeyboard()
        val documentNo = fragmentPendingOrdersListBinding?.edtDocumentNo?.text.toString()
        val fromDate = fragmentPendingOrdersListBinding?.edtFromDate?.text.toString()
        val toDate = fragmentPendingOrdersListBinding?.edtToDate?.text.toString()
        getPendingOrdersByDateAndDocumentNo(documentNo, fromDate, toDate)
    }


    private fun getPendingOrdersByDateAndDocumentNo(
        documentNo: String, fromDate: String,
        toDate: String,
    ) {
        viewModel.getPendingOrdersByDateAndDocno(
            shopLocationCode,
            documentNo,
            fromDate,
            toDate,
            token
        )
        viewModel.filtered_pending_orders_response.observe(mainActivity!!) { response ->
            when (response) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    if (response.data?.get(0)?.ORDER_REFNO == null) {
                        showOrHideEmptyScreen(true)
                    } else {
                        response.data?.let {
                            pendingOrdersList = response?.data
                            settingRecyclerview(pendingOrdersList!!)
                            showOrHideEmptyScreen(false)
                        }
                    }
                    viewModel.filtered_pending_orders_response.value = null
                }

                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    if (response.statusCode == 401) {
                        Toast.makeText(
                            requireContext(),
                            "Your session token expired. Please logout and login again",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (response.message?.contains("No Records") == true) {
                            showOrHideEmptyScreen(true)
                        }
                    }
                    viewModel.filtered_pending_orders_response.value = null
                }

                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    private fun callPendingOrdersApi() {

        viewModel.getPendingOrders(shopLocationCode, token)
        viewModel.pending_orders_response.observe(mainActivity!!) { response ->

            when (response) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    if (response.data?.get(0)?.ORDER_REFNO == null) {
                        showOrHideEmptyScreen(true)
                    } else {
                        response.data?.let {
                            pendingOrdersList = response?.data
                            settingRecyclerview(pendingOrdersList!!)
                            showOrHideEmptyScreen(false)
                        }
                    }
                    viewModel.pending_orders_response.value = null
                }

                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    if (response.statusCode == 401) {
                        Toast.makeText(
                            requireContext(),
                            "Your session token expired. Please logout and login again",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (response.message?.contains("No Records") == true) {
                            showOrHideEmptyScreen(true)
                        }
                    }
                    viewModel.pending_orders_response.value = null

                }

                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    private fun openDocumentProcess(
        shopLocationCode: String,
        documentNo: String,
        toLocation: String,
        shortName: String
    ) {
        viewModel.openProcess(shopLocationCode, documentNo, toLocation, shortName, token)
        viewModel.open_process_response.observe(mainActivity!!) { response ->
            when (response) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    if (response.data?.get(0)?.ORDER_REFNO == null) {

                    } else {
                        val gson = Gson()
                        val responseString = gson.toJson(response.data)
                        val bundle = Bundle()
                        bundle.putString("open_response", responseString)
                        bundle.putString("document_no", documentNo)
                        mainActivity?.navController?.navigate(R.id.navigation_scan_item, bundle)
                    }
                    viewModel.open_process_response.value = null
                }

                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    if (response.statusCode == 401) {
                        Toast.makeText(
                            requireContext(),
                            "Your session token expired. Please logout and login again",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            response.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    viewModel.open_process_response.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    private fun showOrHideEmptyScreen(show: Boolean) {
        if (show)
            fragmentPendingOrdersListBinding?.cvNoData?.visibility = View.VISIBLE
        else
            fragmentPendingOrdersListBinding?.cvNoData?.visibility = View.GONE
    }

    private fun settingRecyclerview(list: ArrayList<ShopPickOrdersResponseModel.ShopPickOrdersResponseModelItem>) {
        pendingOrdersAdapter = PendingOrdersAdapter(list!!, requireContext(), this)

        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragmentPendingOrdersListBinding?.recyclerView?.layoutManager = manager
        fragmentPendingOrdersListBinding?.recyclerView?.adapter = pendingOrdersAdapter
    }


    fun setCurrentDate() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        val monthName = getMonthName(month)
        fragmentPendingOrdersListBinding?.edtFromDate?.setText("$day-$monthName-$year")
        fragmentPendingOrdersListBinding?.edtToDate?.setText("$day-$monthName-$year")
    }

    fun showDatePickerDialog(isFromDate: Boolean) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            mainActivity!!,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val selectedMonth = monthOfYear + 1
                val monthName = getMonthName(selectedMonth)
                if (isFromDate) {
                    fragmentPendingOrdersListBinding?.edtFromDate?.setText("$dayOfMonth-$monthName-$year")
                    selectedFromDate = "$dayOfMonth-$selectedMonth-$year"
                } else {
                    fragmentPendingOrdersListBinding?.edtToDate?.setText("$dayOfMonth-$monthName-$year")
                    selectedToDate = "$dayOfMonth-$selectedMonth-$year"
                }

                filterOrdersByData()

            },
            year,
            month,
            day
        )


        datePickerDialog.show()
    }

    override fun openDocument(order: ShopPickOrdersResponseModel.ShopPickOrdersResponseModelItem) {
        //call api and navigate to item scan screen
        openDocumentProcess(
            shopLocationCode,
            order.ORDER_REFNO!!,
            order.FROM_LOCATION!!,
            order.SHORT_NAME!!
        )
    }

    private fun getMonthName(month: Int): String {
        var monthName = ""
        when (month) {
            1 -> monthName = "JAN"
            2 -> monthName = "FEB"
            3 -> monthName = "MAR"
            4 -> monthName = "APR"
            5 -> monthName = "MAY"
            6 -> monthName = "JUN"
            7 -> monthName = "JUL"
            8 -> monthName = "AUG"
            9 -> monthName = "SEP"
            10 -> monthName = "OCT"
            11 -> monthName = "NOV"
            12 -> monthName = "DEC"
        }
        return monthName
    }

}