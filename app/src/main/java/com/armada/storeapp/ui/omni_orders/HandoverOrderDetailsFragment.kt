package com.armada.storeapp.ui.omni_orders

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import com.armada.storeapp.data.model.request.DeliverOmniOrderRequest
import com.armada.storeapp.data.model.request.OMNIOrderDetailXX
import com.armada.storeapp.data.model.request.OMNIOrderDetailXXX
import com.armada.storeapp.data.model.request.SaveOmniOrderRequest
import com.armada.storeapp.data.model.response.OmnIOrderDetailX
import com.armada.storeapp.data.model.response.OmniOrderDetailsResponse
import com.armada.storeapp.databinding.FragmentHandoverOrderDetailsBinding
import com.armada.storeapp.databinding.FragmentPendingOrderDetailsBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.omni_orders.adapter.OrderItemAdapter
import com.armada.storeapp.ui.utils.ConnectionDetector
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.google.gson.Gson
import org.json.JSONObject

class HandoverOrderDetailsFragment : Fragment() {


    lateinit var binding: FragmentHandoverOrderDetailsBinding
    lateinit var mainActivity: MainActivity
    private var TAG = HandoverOrderDetailsFragment::class.java.simpleName
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
        binding = FragmentHandoverOrderDetailsBinding.inflate(inflater)
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

        binding.btnDeliver.setOnClickListener {
            deliverOrder()
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

            if(it.paymentStatus.equals("Pending")){
                binding.btnDeliver.visibility=View.GONE
            }
            binding.tvOrderId.text = it.orderNo.toString()
            it.customerMaster?.get(0)?.let {
                binding.tvCustomerName.text = it.customerName
                binding.tvMobile.text = it.phoneNumber
            }

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

    fun deliverOrder(

    ) {
        var deliverOmniOrderRequest: DeliverOmniOrderRequest? = null
        orderDetailsResponse?.omnI_OrderHeaderList?.get(0)?.let {
            val itemList = ArrayList<OMNIOrderDetailXXX>()
            orderItemList?.forEach {
                itemList.add(
                    OMNIOrderDetailXXX(
                        it.bC_Price,
                        it.id,
                        it.scannedQty,
                        1,
                        it.skuCode,
                    )
                )
            }
            deliverOmniOrderRequest = DeliverOmniOrderRequest(
                it.createBy!!,
                it.id.toString(),
                itemList,
                it.orderNo!!,
                it.fromStoreCode!!,
                it.toStoreCode
            )
        }

        omniOrdersViewModel.deliverStorePickupOrder(deliverOmniOrderRequest!!)
        omniOrdersViewModel.responseDeliverStorePickupOrder.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    if (it.statusCode == 1) {
                        Toast.makeText(
                            mainActivity,
                            it.data?.displayMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        val bundle = Bundle()
                        bundle.putInt("tab_position", 2)
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

}