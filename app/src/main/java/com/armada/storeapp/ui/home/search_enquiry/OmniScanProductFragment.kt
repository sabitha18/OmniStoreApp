package com.armada.storeapp.ui.home.search_enquiry

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.armada.storeapp.R
import com.armada.storeapp.databinding.FragmentOmniScanProductBinding
import com.armada.storeapp.databinding.FragmentOrderPlaceSuccessBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_item_scan.OmniItemScannerActivity

class OmniScanProductFragment : Fragment() {

    lateinit var binding: FragmentOmniScanProductBinding
    private var mainActivity: MainActivity? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOmniScanProductBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        val intent = Intent(mainActivity, OmniItemScannerActivity::class.java)
        startActivityForResult(intent, 501)
        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 501) {

            mainActivity?.navController?.navigate(R.id.navigation_omni_bag)
        }
    }
}