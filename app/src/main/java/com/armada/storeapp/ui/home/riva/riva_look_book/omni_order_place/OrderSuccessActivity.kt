package com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.armada.storeapp.databinding.FragmentOmniOrderPlaceBinding
import com.armada.storeapp.databinding.FragmentOrderPlaceSuccessBinding
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderSuccessActivity : BaseActivity() {

    lateinit var binding: FragmentOrderPlaceSuccessBinding
    var orderId = ""
    var selectedDeliveryMethod = ""
    var pickupDate = ""
    var pickupTime = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentOrderPlaceSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            orderId = intent.getStringExtra("order_id")!!
            selectedDeliveryMethod = intent.getStringExtra("delivery_method")!!
            pickupDate = intent.getStringExtra("pickup_date")!!
            pickupTime = intent.getStringExtra("pickup_time")!!
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

            val intent = Intent(this, RivaLookBookActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        binding.button3.setOnClickListener {

            val intent = Intent(this, RivaLookBookActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }
}