package com.armada.storeapp.ui.omni_orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.armada.storeapp.data.model.response.OmnIOrderActivityX
import com.armada.storeapp.data.model.response.OmniOrderDetailsResponse
import com.armada.storeapp.data.model.response.SkuMasterTypes
import com.armada.storeapp.databinding.FragmentOrderHistoryBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.utils.Utils
import com.bumptech.glide.util.Util
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class OrderStatusHistoryFragment : Fragment() {
    lateinit var binding: FragmentOrderHistoryBinding
    lateinit var mainActivity: MainActivity
    private var TAG = OrderStatusHistoryFragment::class.java.simpleName
    var orderDetailsResponse: OmniOrderDetailsResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderHistoryBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        initializeData()
        return binding.root
    }


    private fun initializeData() {

        mainActivity = activity as MainActivity
        arguments?.let {
            val itemString = arguments?.getString("order_activity")
            val gson = Gson()
            val type = object : TypeToken<ArrayList<OmnIOrderActivityX>>() {

            }.type
            val orderActivityList =
                gson.fromJson<java.util.ArrayList<OmnIOrderActivityX>>(itemString, type)
            val inputFormat = "yyyy-MM-dd hh:mm:ss"
            val outputFormatDate = "dd-MM-yyyy"
            val outputFormatTime = "h:mm aaa"
            binding.deliveredGroup.visibility = View.GONE
            binding.packedGroup.visibility = View.GONE
            for ((index, value) in orderActivityList.withIndex()) {
                when (value.activity) {
                    "Created" -> {
                        binding.tvCreatedDate.text =
                            Utils.getFormattedDateTime(
                                inputFormat,
                                outputFormatDate,
                                value.act_Time
                            )
                        binding.tvCreatedTime.text =
                            Utils.getFormattedDateTime(
                                inputFormat,
                                outputFormatTime,
                                value.act_Time
                            )
                        binding.tvStatus1.text="Order was created to the store"
                    }
                    "Accepted" -> {
                        binding.tvAcceptedDate.text =
                            Utils.getFormattedDateTime(
                                inputFormat,
                                outputFormatDate,
                                value.act_Time
                            )
                        binding.tvAcceptedTime.text =
                            Utils.getFormattedDateTime(
                                inputFormat,
                                outputFormatTime,
                                value.act_Time
                            )
                        binding.tvStatus2.text="Order was accepted by the store"
                        binding.deliveredGroup.visibility = View.GONE
                        binding.packedGroup.visibility = View.GONE
                    }
                    "Packing Completed" -> {
                        binding.tvPackedDate.text =
                            Utils.getFormattedDateTime(
                                inputFormat,
                                outputFormatDate,
                                value.act_Time
                            )
                        binding.tvPackedTime.text =
                            Utils.getFormattedDateTime(
                                inputFormat,
                                outputFormatTime,
                                value.act_Time
                            )
                        binding.tvStatus3.text="Completed Packing and moved to store pickup"
                        binding.deliveredGroup.visibility = View.GONE
                        binding.packedGroup.visibility = View.VISIBLE
                    }
                    "Delivered" -> {
                        binding.tvDeliveredDate.text =
                            Utils.getFormattedDateTime(
                                inputFormat,
                                outputFormatDate,
                                value.act_Time
                            )
                        binding.tvDeliveredTime.text =
                            Utils.getFormattedDateTime(
                                inputFormat,
                                outputFormatTime,
                                value.act_Time
                            )
                        binding.tvStatus4.text="Delivered"
                        binding.deliveredGroup.visibility = View.VISIBLE
                        binding.packedGroup.visibility = View.VISIBLE
                    }
                }
            }
        }
        mainActivity?.BackPressed(this)

    }
}