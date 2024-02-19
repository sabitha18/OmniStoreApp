package com.armada.storeapp.ui.home.search_enquiry

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.armada.storeapp.R
import com.armada.storeapp.databinding.FragmentOmniOrderPlaceBinding
import com.armada.storeapp.databinding.FragmentOrderPlaceSuccessBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.OrderPlaceViewModel
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OmniOrderSuccessFragment : Fragment() {

    var orderId = ""
    var selectedDeliveryMethod = ""
    var pickupDate = ""
    var pickupTime = ""
    lateinit var binding: FragmentOrderPlaceSuccessBinding
    private var mainActivity: MainActivity? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderPlaceSuccessBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        arguments?.let {
            orderId = it.getString("order_id")!!
            selectedDeliveryMethod = it.getString("delivery_method")!!
            pickupDate = it.getString("pickup_date")!!
            pickupTime = it.getString("pickup_time")!!
        }

        when (selectedDeliveryMethod) {
            "STOREPICKUP" -> {
                binding.cvStorePickup.visibility = View.VISIBLE
                binding.tvPickupDate.text = pickupDate
                binding.tvPickupTime.text = pickupTime
                binding.tvStorePickupMessage.text =
                    "Your order has been placed with Order ID $orderId. We are preparing your order to keep it ready for pickup"
            }
            "HOME DELIVERY" -> {
                binding.cvStorePickup.visibility = View.GONE
                binding.tvHomeDeliveryMessage.text =
                    "Your order with Order ID $orderId will be delivered soon. Thank you for using our app!"
            }
        }

        binding.button2.setOnClickListener {
            mainActivity?.navController?.navigate(R.id.navigation_home)
        }
        binding.button3.setOnClickListener {

            mainActivity?.navController?.navigate(R.id.navigation_home)
        }
        return binding.root

    }
}