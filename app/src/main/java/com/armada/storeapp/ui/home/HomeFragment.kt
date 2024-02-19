package com.armada.storeapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.armada.storeapp.R
import com.armada.storeapp.databinding.FragmentHomeBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.riva_login.RivaLoginActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.select_country.SelectCountryActivity
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var fragmentHomeBinding: FragmentHomeBinding? = null
    lateinit var homeViewModel: ViewModel
    lateinit var mainActivity: MainActivity
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    var hasWarehouseLoggedIn = false
    var sessionToken = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        mainActivity = activity as MainActivity

        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = fragmentHomeBinding?.root!!

        sharedpreferenceHandler = SharedpreferenceHandler(mainActivity)
        sessionToken =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.WAREHOUSE_TOKEN, "")!!



        val binavailablity = sharedpreferenceHandler.getData(SharedpreferenceHandler.BIN_AVAILABLE, "")
        fragmentHomeBinding?.horizontalScrollView7?.isGone = binavailablity.equals("false")
        fragmentHomeBinding?.textView33?.isGone = binavailablity.equals("false")
        fragmentHomeBinding?.lineinstore?.isGone = binavailablity.equals("false")




        setListener()
        return root
    }

    private fun setListener() {
        fragmentHomeBinding?.lvRivaLookBook?.setOnClickListener {
            val intent = Intent(mainActivity, RivaLookBookActivity::class.java)
            startActivity(intent)
        }
        fragmentHomeBinding?.lvPicklist?.setOnClickListener {
            mainActivity?.navController?.navigate(
                R.id.navigation_picklist
            )
        }
        fragmentHomeBinding?.lvBinTransfer?.setOnClickListener {
            mainActivity?.navController?.navigate(R.id.navigation_bin_transfer)
        }
        fragmentHomeBinding?.lvInventory?.setOnClickListener {
            mainActivity?.navController?.navigate(R.id.navigation_inventory)
        }
        fragmentHomeBinding?.lvStockAdjustment?.setOnClickListener {
            mainActivity?.navController?.navigate(R.id.navigation_stock_adjustment)
        }
        fragmentHomeBinding?.lvStockReceive?.setOnClickListener {
            mainActivity?.navController?.navigate(R.id.navigation_select_stock_location)
        }
        fragmentHomeBinding?.lvStockReturn?.setOnClickListener {
            mainActivity?.navController?.navigate(R.id.navigation_stock_return)
        }
        fragmentHomeBinding?.lvItemSearch?.setOnClickListener {
            mainActivity?.navController?.navigate(R.id.navigation_item_search)
        }
        fragmentHomeBinding?.lvCreatePicklist?.setOnClickListener {
            mainActivity?.navController?.navigate(R.id.navigation_create_picklist)
        }
        fragmentHomeBinding?.lvOnlineRequests?.setOnClickListener {
            mainActivity?.navController?.navigate(R.id.navigation_warehouse_pending_orders)
        }
        fragmentHomeBinding?.lvScanProduct?.setOnClickListener {
            mainActivity?.navController?.navigate(R.id.navigation_omni_scan_product)
        }

        fragmentHomeBinding?.lvSearchProduct?.setOnClickListener {
            sharedpreferenceHandler.saveData(SharedpreferenceHandler.SEARCH_ITEMS,"")
            sharedpreferenceHandler.saveData(SharedpreferenceHandler.SEARCH_COUNT,0)
            mainActivity?.navController?.navigate(R.id.navigation_omni_bag)
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity?.showProgressBar(false)
    }
}
