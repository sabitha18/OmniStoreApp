package com.armada.storeapp.ui.omni_orders.store_pickup_orders

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.PendingOrderAcceptRequest
import com.armada.storeapp.data.model.response.OmnIOrderHeader
import com.armada.storeapp.data.model.response.OmniOrderItem
import com.armada.storeapp.data.model.response.OmniOrdersResponse
import com.armada.storeapp.databinding.FragmentReceivedStorepickupOrdersBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.instore_transactions.picklist.PicklistFragment
import com.armada.storeapp.ui.omni_orders.OmniOrdersViewModel
import com.armada.storeapp.ui.omni_orders.adapter.OmniOrderAdapter
import com.armada.storeapp.ui.omni_orders.adapter.StatusSpinnerAdapter
import com.armada.storeapp.ui.utils.ConnectionDetector
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.google.android.material.tabs.TabLayout
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ReceivedStorePickupOrdersFragment : Fragment(), TabLayout.OnTabSelectedListener {

    var tabPosition =0
    lateinit var binding: FragmentReceivedStorepickupOrdersBinding
    lateinit var mainActivity: MainActivity
    private var TAG = ReceivedStorePickupOrdersFragment::class.java.simpleName
    var cd: ConnectionDetector? = null
    lateinit var omniOrdersViewModel: OmniOrdersViewModel
    lateinit var omniOrderAdapter: OmniOrderAdapter
    var currentOrderList: ArrayList<OmnIOrderHeader>? = null
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    var sessionToken = ""
    var storeCode = ""
    var storeId = 0
    var userCode = ""
    var selectedFromDate = ""
    var selectedToDate = ""
    var status = "Pending"
    var searchString = ""
    val statusList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReceivedStorepickupOrdersBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        initializeData()
        setListeners()
        mainActivity?.BackPressed(this)
        return binding.root
    }

    private fun setListeners() {
        statusList.add("Pending")
        statusList.add("Accepted")
        val statusSpinnerAdapter = StatusSpinnerAdapter(requireContext(), statusList)
        binding?.spinnerStatus?.adapter = statusSpinnerAdapter
        binding.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                try {
                    status = statusList?.get(position)
                    getOmniOrders(status)
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        setCurrentDate()
        binding.calendarFromDate.setOnClickListener {
            showDatePickerDialog(true)
        }
        binding.calendarToDate.setOnClickListener {
            showDatePickerDialog(false)
        }
    }


    private fun initializeData() {

        mainActivity = activity as MainActivity
        omniOrdersViewModel =
            ViewModelProvider(mainActivity).get(OmniOrdersViewModel::class.java)

        cd = ConnectionDetector(activity)

        sharedpreferenceHandler = SharedpreferenceHandler(mainActivity)
        storeCode = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_CODE, "")!!
        storeId = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_ID, 0)!!
        userCode = sharedpreferenceHandler.getData(SharedpreferenceHandler.LOGIN_USER_CODE, "")!!
        sessionToken = sharedpreferenceHandler.getData(SharedpreferenceHandler.ACCESS_TOKEN, "")!!



        binding.tabLayout.addOnTabSelectedListener(this)
        mainActivity?.BackPressed(this)

        if (arguments != null && arguments?.containsKey("tab_position") == true) {
            val tabPosition = arguments?.getInt("tab_position")
            binding.tabLayout.getTabAt(1)?.select()
        } else {
            if (cd?.isConnectingToInternet!!) {
                getOmniOrders(
                    status
                )
            }
        }

    }

    private fun settingRecyclerview(
        list: ArrayList<OmnIOrderHeader>
    ) {
        try {
            omniOrderAdapter =
                OmniOrderAdapter(list!!, requireContext())
            val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding?.recyclerView?.layoutManager = manager
            binding?.recyclerView?.adapter = omniOrderAdapter

            omniOrderAdapter?.acceptPendingOrder = { order, position ->
                acceptPendingOrder(order, position)
            }

            omniOrderAdapter?.tapToScan = { order ->
                val bundle = Bundle()
                bundle.putString(Constants.ID, order.id?.toString())
                when(tabPosition){
                    0->{
                        mainActivity?.navController?.navigate(
                            R.id.navigation_pending_omni_order_details,
                            bundle
                        )
                    }
                    1->{
                        mainActivity?.navController?.navigate(
                            R.id.navigation_store_handover_omni_order_details,
                            bundle
                        )
                    }
                    2->{
                        mainActivity?.navController?.navigate(
                            R.id.navigation_completed_omni_order_details,
                            bundle
                        )
                    }
                }

            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }


    fun getOmniOrders(
        omniOrderStatus: String
    ) {
        omniOrdersViewModel.getOmniOrders(
            selectedFromDate,
            selectedToDate,
            storeCode,
            searchString,
            "",
            omniOrderStatus
        )
        omniOrdersViewModel.responseOmniOrdersResponse.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)

                    it.data?.let { response ->

                        try {
                            if (response.omnI_OrderHeaderList == null) {
                                binding?.lvNoRecord?.visibility = View.VISIBLE
                            } else {
                                binding?.lvNoRecord?.visibility = View.GONE
                                currentOrderList = response.omnI_OrderHeaderList
                                settingRecyclerview(response.omnI_OrderHeaderList)
                            }

                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }
                    }
                }
                is Resource.Error -> {

                    mainActivity?.showProgressBar(false)
                    binding?.lvNoRecord?.visibility =
                        View.VISIBLE
                    try {
                        it.message?.let { message ->
                            Log.e(TAG, "Error: $message")
                            if (message.contains("message")) {
                                val jsonObj = JSONObject(message)
                                mainActivity?.showMessage(jsonObj.get("message").toString())
                            } else
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    fun acceptPendingOrder(
        omnIOrderHeader: OmnIOrderHeader, position: Int
    ) {
        val pendingOrderAcceptRequest = PendingOrderAcceptRequest(
            omnIOrderHeader.id, "Accepted", storeCode,
            storeId, omnIOrderHeader.updateBy
        )
        omniOrdersViewModel.acceptPendingOrder(sessionToken, pendingOrderAcceptRequest)
        omniOrdersViewModel.responsePendingOrderAccept.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    if (it.statusCode == 1) {
                        Toast.makeText(
                            mainActivity,
                            "Order Accepted Sucessfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        currentOrderList?.remove(omnIOrderHeader)
                        omniOrderAdapter?.notifyItemRemoved(position)

                    }


                }
                is Resource.Error -> {

                    mainActivity?.showProgressBar(false)
                    try {
                        it.message?.let { message ->
                            Log.e(TAG, "Error: $message")
                            if (message.contains("message")) {
                                val jsonObj = JSONObject(message)
                                mainActivity?.showMessage(jsonObj.get("message").toString())
                            } else
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tabPosition = tab!!.position
        when (tabPosition) {
            0 -> {
                status = "Pending"
                getOmniOrders(status)
            }
            1 -> {
                status = "Packing Completed"
                getOmniOrders(status)
            }
            2 -> {
                status = "Delivered"
                getOmniOrders(status)
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }


    fun setCurrentDate() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        val monthName = getMonthName(month)
        binding?.edtFromDate?.setText("$day-$monthName-$year")
        binding?.edtToDate?.setText("$day-$monthName-$year")
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
                    binding?.edtFromDate?.setText("$dayOfMonth-$monthName-$year")
                    selectedFromDate = "$year-$selectedMonth-$dayOfMonth"
                } else {
                    binding?.edtToDate?.setText("$dayOfMonth-$monthName-$year")
                    selectedToDate = "$year-$selectedMonth-$dayOfMonth"
                }

                getOmniOrders("Pending")
            },
            year,
            month,
            day
        )


        datePickerDialog.show()
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