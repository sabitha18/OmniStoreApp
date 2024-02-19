package com.armada.storeapp.ui.omni_orders

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
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
import com.armada.storeapp.data.model.request.OMNIOrderDetailXX
import com.armada.storeapp.data.model.request.PendingOrderAcceptRequest
import com.armada.storeapp.data.model.request.SaveOmniOrderRequest
import com.armada.storeapp.data.model.response.OmnIOrderDetailX
import com.armada.storeapp.data.model.response.OmnIOrderHeader
import com.armada.storeapp.data.model.response.OmniOrderDetailsResponse
import com.armada.storeapp.data.model.response.OmniOrdersResponse
import com.armada.storeapp.databinding.FragmentPendingOrderDetailsBinding
import com.armada.storeapp.databinding.FragmentReceivedStorepickupOrdersBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.instore_transactions.picklist.PicklistFragment
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_item_scan.OmniItemScannerActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.adapter.OmniItemAdapter
import com.armada.storeapp.ui.omni_orders.adapter.OmniOrderAdapter
import com.armada.storeapp.ui.omni_orders.adapter.OrderItemAdapter
import com.armada.storeapp.ui.omni_orders.adapter.StatusSpinnerAdapter
import com.armada.storeapp.ui.utils.ConnectionDetector
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.test_details.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class PendingOrderDetailsFragment : Fragment() {

    lateinit var binding: FragmentPendingOrderDetailsBinding
    lateinit var mainActivity: MainActivity
    private var TAG = PendingOrderDetailsFragment::class.java.simpleName
    var cd: ConnectionDetector? = null
    lateinit var omniOrdersViewModel: OmniOrdersViewModel

    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    var sessionToken = ""
    var storeCode = ""
    var storeId = 0
    var userCode = ""
    var status = "Accepted"
    var searchString = ""
    var orderId = ""
    var priceListId = 0
    var selectedCurrency = "KWD"
    var orderDetailsResponse: OmniOrderDetailsResponse? = null
    var orderItemList: ArrayList<OmnIOrderDetailX>? = null
    var hasSaveButtonEnabled = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPendingOrderDetailsBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        initializeData()
        setListeners()
        return binding.root
    }

    private fun setListeners() {
        binding.btnViewAllItems.setOnClickListener {
            if (binding.recyclerViewOmniItems.visibility == View.VISIBLE) {
                binding.recyclerViewOmniItems.visibility = View.GONE
                binding.imageViewExpandCollapse.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_baseline_add_24,
                        resources.newTheme()
                    )
                )
            } else {
                binding.recyclerViewOmniItems.visibility = View.VISIBLE
                binding.imageViewExpandCollapse.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_baseline_minimize_24,
                        resources.newTheme()
                    )
                )
            }
        }

        binding.tvOrderHistory.setOnClickListener {
            val bundle = Bundle()
            val gson = Gson()
            bundle.putString(
                "order_activity",
                gson.toJson(orderDetailsResponse?.omnI_OrderHeaderList?.get(0)?.omnI_OrderActivity)
            )
            mainActivity?.navController?.navigate(R.id.navigation_order_status_history, bundle)
        }

        binding.cvScan.setOnClickListener {
            if (hasSaveButtonEnabled) {
                Toast.makeText(mainActivity, "All items scanned", Toast.LENGTH_SHORT).show()
                binding.btnSave.visibility = View.VISIBLE
                binding.btnCancel.visibility = View.VISIBLE
            } else {
                val intent = Intent(mainActivity, OrderItemScannerActivity::class.java)
                startActivityForResult(intent, 401)
            }
        }

        binding.btnCancel.setOnClickListener {
            mainActivity.navController.navigate(R.id.navigation_omni_store_pickup_orders)
        }
        binding.btnSave.setOnClickListener {
            saveOrder()
        }
    }


    override fun onResume() {
        super.onResume()
        if (hasSaveButtonEnabled) {
            binding.btnSave.visibility = View.VISIBLE
            binding.btnCancel.visibility = View.VISIBLE
        } else {
            binding.btnSave.visibility = View.GONE
            binding.btnCancel.visibility = View.GONE
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
        priceListId = sharedpreferenceHandler.getData(SharedpreferenceHandler.PRICE_LIST_ID, 0)!!
        userCode = sharedpreferenceHandler.getData(SharedpreferenceHandler.LOGIN_USER_CODE, "")!!
        sessionToken = sharedpreferenceHandler.getData(SharedpreferenceHandler.ACCESS_TOKEN, "")!!

        arguments?.let {
            orderId = arguments?.getString(Constants.ID)!!

        }

        if (cd?.isConnectingToInternet!!) {
            getOmniOrderDetails(orderId)
        }
        mainActivity?.BackPressed(this)

    }

    fun getOmniOrderDetails(
        id: String
    ) {
        omniOrdersViewModel.getOmniOrderDetails(id)
        omniOrdersViewModel.responseOrderDetails.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let {
                        setData(it)
                        orderDetailsResponse = it
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

    private fun setData(omniOrderDetailsResponse: OmniOrderDetailsResponse) {

        omniOrderDetailsResponse.omnI_OrderHeaderList?.get(0)?.let {
            binding.tvOrderId.text = it.orderNo.toString()
            it.customerMaster?.get(0)?.let {
                binding.tvCustomerName.text = it.customerName
                binding.tvMobile.text = it.phoneNumber
            }
            binding.tvPickupDate.text = it.pickupDate
            binding.tvPickupTime.text = it.pickupTimeSlot

            binding.tvTotalItems.text = it.omnI_OrderDetail.size.toString()
            binding.tvTotalQty.text = it.totalQty.toString()
            binding.tvTotalPrice.text = it.orderCurrencyGrandTotal.toString() + " KWD" //todo change

            it.storeDetail?.get(0)?.let {
                binding.tvStoreName.text = it.storeName
                binding.tvLocation.text = it.location
            }

            orderItemList = it.omnI_OrderDetail

            binding.recyclerViewOmniItems.layoutManager =
                LinearLayoutManager(mainActivity!!, LinearLayoutManager.VERTICAL, false)
            val omniItemAdapter =
                OrderItemAdapter(mainActivity!!, selectedCurrency, it.omnI_OrderDetail)
            binding.recyclerViewOmniItems.adapter = omniItemAdapter

        }

    }

    fun saveOrder(

    ) {
        var saveOmniOrderRequest: SaveOmniOrderRequest? = null
        orderDetailsResponse?.omnI_OrderHeaderList?.get(0)?.let {
            val itemList = ArrayList<OMNIOrderDetailXX>()
            orderItemList?.forEach {
                itemList.add(
                    OMNIOrderDetailXX(
                        it.bC_Price,
                        it.baseCurrency,
                        it.id,
                        it.oC_Price,
                        it.orderCurrency,
                        it.orderID.toString(),
                        it.orderQty,
                        it.scannedQty,
                        "true",
                        it.skuCode,
                        "Packing Completed",
                        it.weight,
                        it.weightUnit
                    )
                )
            }
            saveOmniOrderRequest = SaveOmniOrderRequest(
                it.businessDate,
                it.deliveryType,
                it.id.toString(),
                itemList,
                "Packing Completed",
                it.fromStoreCode,
                it.fromStoreID,
                it.storeDetail.get(0).storeName,
                it.updateBy
            )
        }
        val requestString = Gson().toJson(saveOmniOrderRequest)
        omniOrdersViewModel.saveOmniOrder(saveOmniOrderRequest!!)
        omniOrdersViewModel.responseSaveOmniOrder.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    if (it.statusCode == 1) {
                        Toast.makeText(
                            mainActivity,
                            "Order Saved Sucessfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        val bundle = Bundle()
                        bundle.putInt("tab_position", 1)
                        mainActivity?.navController?.navigate(
                            R.id.navigation_omni_store_pickup_orders,
                            bundle
                        )
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 401 && data != null && data.hasExtra(
                "item_code"
            )
        ) {
            val scannedSkuCode = data.getStringExtra("item_code")
            var totalScannedItems = 0
            orderItemList?.forEach {
                if (it.skuCode.equals(scannedSkuCode)) {
                    var scannedQty = it.scannedQty + 1
                    it.scannedQty = scannedQty

                    if (it.scannedQty == it.orderQty)
                        totalScannedItems++
                }
            }

            if (orderItemList?.size == totalScannedItems) {
                hasSaveButtonEnabled = true
                binding.btnSave.visibility = View.VISIBLE
                binding.btnCancel.visibility = View.VISIBLE
            }

            Toast.makeText(mainActivity, "$scannedSkuCode scanned successfully", Toast.LENGTH_SHORT)
                .show()
        }

    }

}