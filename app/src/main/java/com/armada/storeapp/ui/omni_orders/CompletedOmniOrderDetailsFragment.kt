package com.armada.storeapp.ui.omni_orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.armada.storeapp.databinding.FragmentCompletedOmniOrderDetailsBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.utils.ConnectionDetector
import com.armada.storeapp.ui.utils.SharedpreferenceHandler

class CompletedOmniOrderDetailsFragment: Fragment() {

    lateinit var binding: FragmentCompletedOmniOrderDetailsBinding
    lateinit var mainActivity: MainActivity
    private var TAG = CompletedOmniOrderDetailsFragment::class.java.simpleName
    var cd: ConnectionDetector? = null
    lateinit var omniOrdersViewModel: OmniOrdersViewModel
    var sessionToken: String? = null
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompletedOmniOrderDetailsBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        initializeData()
        return binding.root
    }

    private fun initializeData() {

    }


}