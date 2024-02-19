package com.armada.storeapp.ui.omni_orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.armada.storeapp.R
import com.armada.storeapp.databinding.FragmentMyOmniOrdersBinding
import com.armada.storeapp.databinding.FragmentOmniOrderPlaceBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.OrderPlaceViewModel
import com.armada.storeapp.ui.utils.SharedpreferenceHandler

class MyOmniOrdersFragment : Fragment() {

    lateinit var binding: FragmentMyOmniOrdersBinding
    lateinit var mainActivity:MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyOmniOrdersBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        init()
        return binding.root
    }

    fun init(){
        binding.cvStorePickupOrders.setOnClickListener {
            mainActivity.navController.navigate(R.id.navigation_omni_store_pickup_orders)
        }
    }
}