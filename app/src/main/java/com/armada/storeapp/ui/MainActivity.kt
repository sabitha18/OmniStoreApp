package com.armada.storeapp.ui

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.armada.storeapp.R
import com.armada.storeapp.databinding.ActivityMainBinding
import com.armada.storeapp.databinding.LayoutAlertCustomBinding
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.instore_transactions.bin_transfer.BinTransferFragment
import com.armada.storeapp.ui.home.instore_transactions.inventory.InventoryFragment
import com.armada.storeapp.ui.home.instore_transactions.picklist.CreateManualPicklistFragment
import com.armada.storeapp.ui.home.instore_transactions.picklist.PicklistFragment
import com.armada.storeapp.ui.home.instore_transactions.picklist.PicklistTransferFragment
import com.armada.storeapp.ui.home.others.item_search.ItemSearchFragment
import com.armada.storeapp.ui.home.others.online_requests.item_scan.ItemScanFragment
import com.armada.storeapp.ui.home.search_enquiry.OmniChannelBagFragment
import com.armada.storeapp.ui.home.search_enquiry.OmniOrderPlaceFragment
import com.armada.storeapp.ui.home.warehouse_transactions.stock_adjustment.StockAdjustmentFragment
import com.armada.storeapp.ui.home.warehouse_transactions.stock_receive.AddStockReceiptFragment
import com.armada.storeapp.ui.home.warehouse_transactions.stock_receive.SelectStockLocationFragment
import com.armada.storeapp.ui.home.warehouse_transactions.stock_return.StockReturnFragment
import com.armada.storeapp.ui.login.LoginActivity
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private var logoutDialog: Dialog? = null
    private var binding: ActivityMainBinding? = null
    lateinit var navController: NavController
    private val appBarConfiguration by lazy {
        AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.navigation_home,
                R.id.navigation_notifications,
                R.id.navigation_profile,
                R.id.navigation_orders
            ),
            binding?.drawerLayout,
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val PERMISSION_REQUESTS = 1

        private val REQUIRED_RUNTIME_PERMISSIONS =
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //Setting actionbar
//        setSupportActionBar(binding?.toolbar)
//        binding?.toolbar?.setTitleTextColor(resources.getColor(R.color.white, resources.newTheme()))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
//        binding?.toolbar?.setTitle("Sunena Riva-Assima")
//        binding?.collapsingToolbarLayout?.setTitle("Sunena Riva-Assima")
        supportActionBar?.setTitle("Sunena Riva-Assima")


        val navView: BottomNavigationView = binding?.navView!!

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_home) {
                supportActionBar?.setTitle("Sunena Riva-Assima")
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }
//        val appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        setDrawer()


        //Bottom bar item selected listener
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController?.navigate(R.id.navigation_home)
                }
                R.id.navigation_notifications -> {
                    navController?.navigate(R.id.navigation_notifications)
                }
                R.id.navigation_profile -> {
                    navController?.navigate(R.id.navigation_profile)
                }
                R.id.navigation_orders -> {
//                    navController?.navigate(R.id.navigation_orders)
                }
            }
            true
        }

        if (!allRuntimePermissionsGranted()) {
            getRuntimePermissions()
        }

        setProfileDetails()
    }

    private fun setProfileDetails() {
        val sharedpreferenceHandler = SharedpreferenceHandler(this)
        val employeeName =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.EMPLOYEE_NAME, "")
        val storeName = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_NAME, "")
        binding?.included?.navHeader?.tvProfileName?.text = "Hi, $employeeName"
        binding?.included?.navHeader?.tvStoreName?.text = storeName
    }

    private fun setDrawer() {
        val toggle = ActionBarDrawerToggle(
            this,
            binding?.drawerLayout,
            null,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding?.drawerLayout?.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        //set drawer custom item click listeners
        binding?.included?.lvNotifications?.setOnClickListener {
            navController?.navigate(R.id.navigation_notifications)
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }
        binding?.included?.lvHome?.setOnClickListener {
            navController?.navigate(R.id.navigation_home)
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }
        binding?.included?.lvLogout?.setOnClickListener {
            displayLogoutDialog()
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }
        binding?.included?.lvProfile?.setOnClickListener {
            navController?.navigate(R.id.navigation_profile)
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }
        binding?.included?.lvMyOrders?.setOnClickListener {
//            navController?.navigate(R.id.navigation_orders)
            Toast.makeText(this, "The feature not done", Toast.LENGTH_SHORT).show()
        }
    }


    fun showProgressBar(show: Boolean) {
        if (show)
            binding?.cvProgressbar?.visibility = View.VISIBLE
        else
            binding?.cvProgressbar?.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun allRuntimePermissionsGranted(): Boolean {
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission?.let {
                if (!isPermissionGranted(this, it)) {
                    return false
                }
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.test, menu)
        return true
    }

    private fun getRuntimePermissions() {
        val permissionsToRequest = ArrayList<String>()
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission?.let {
                if (!isPermissionGranted(this, it)) {
                    permissionsToRequest.add(permission)
                }
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUESTS
            )
        }
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "Permission granted: $permission")
            return true
        }
        Log.i(TAG, "Permission NOT granted: $permission")
        return false
    }

    fun displayLogoutDialog() {

        try {
            if (logoutDialog == null) {
                val alertCustomBinding: LayoutAlertCustomBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(this),
                    R.layout.layout_alert_custom,
                    null,
                    false
                )

                val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                    .setView(alertCustomBinding.root)
                logoutDialog = builder.show()
                logoutDialog?.setCancelable(false)


                alertCustomBinding.btnNo.setOnClickListener() {
                    logoutDialog?.dismiss()
                }

                alertCustomBinding.btnYes.setOnClickListener() {
                    logoutDialog?.dismiss()
                    val sharedpreferenceHandler = SharedpreferenceHandler(this)
                    sharedpreferenceHandler.saveData(SharedpreferenceHandler.LOGIN_STATUS, false)
                    sharedpreferenceHandler.saveData(SharedpreferenceHandler.LOGIN_USERNAME, "")
                    sharedpreferenceHandler.saveData(SharedpreferenceHandler.LOGIN_USER_ID, "")
                    sharedpreferenceHandler.saveData(SharedpreferenceHandler.ACCESS_TOKEN, "")
                    sharedpreferenceHandler.saveData(SharedpreferenceHandler.STORE_CODE, "")
                    sharedpreferenceHandler.saveData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY, "")
                    sharedpreferenceHandler.saveData(
                        SharedpreferenceHandler.WAREHOUSE_LOGIN_STATUS,
                        false
                    )
                    sharedpreferenceHandler.saveData(SharedpreferenceHandler.WAREHOUSE_USER_ID, "")
                    sharedpreferenceHandler.saveData(SharedpreferenceHandler.WAREHOUSE_TOKEN, "")
                    sharedpreferenceHandler.saveData(
                        SharedpreferenceHandler.WAREHOUSE_TO_LOCATION_CODE,
                        ""
                    )

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    this.finish()
                }
            } else {
                if (logoutDialog != null && logoutDialog?.isShowing == false) {
                    logoutDialog?.show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

//    fun getCurrentDate(): String {
//        val calender = Calendar.getInstance()
//
//        val year = calender.get(Calendar.YEAR)
//        val month = calender.get(Calendar.MONTH) + 1
//        val day = calender.get(Calendar.DAY_OF_MONTH)
//        val date = "$year-$month-$day"
//        return date
//    }

    fun BackPressed(fragment: Fragment) {
        this.onBackPressedDispatcher.addCallback(this) {
            val bundle = Bundle()

            if (fragment is SelectStockLocationFragment) {
//                bundle.putInt("select_tab", 1)
                navController?.navigate(R.id.navigation_home, bundle)
            } else if (fragment is StockReturnFragment) {
//                bundle.putInt("select_tab", 1)
                navController?.navigate(R.id.navigation_home, bundle)
            } else if (fragment is StockAdjustmentFragment) {
//                bundle.putInt("select_tab", 1)
                navController?.navigate(R.id.navigation_home, bundle)
            } else if (fragment is AddStockReceiptFragment) {
                navController?.popBackStack()
            } else if (fragment is PicklistFragment) {
//                bundle.putInt("select_tab", 0)
                navController?.navigate(R.id.navigation_home, bundle)
            } else if (fragment is BinTransferFragment) {
//                bundle.putInt("select_tab", 0)
                navController?.navigate(R.id.navigation_home, bundle)
            } else if (fragment is InventoryFragment) {
//                bundle.putInt("select_tab", 0)
                navController?.navigate(R.id.navigation_home, bundle)
            } else if (fragment is PicklistTransferFragment) {
                navController?.popBackStack()
            } else if (fragment is ItemScanFragment) {
//                bundle.putInt("select_tab", 2)
                navController?.navigate(R.id.navigation_home, bundle)
            } else if (fragment is ItemSearchFragment) {
//                bundle.putInt("select_tab", 0)
                navController?.navigate(R.id.navigation_home, bundle)
            } else if (fragment is CreateManualPicklistFragment) {
//                bundle.putInt("select_tab", 0)
                navController?.navigate(R.id.navigation_home, bundle)
            }else if(fragment is OmniOrderPlaceFragment){
                navController?.navigate(R.id.navigation_omni_bag, bundle)
            }else if(fragment is OmniChannelBagFragment){
                navController?.navigate(R.id.navigation_home, bundle)
            }
        }
    }



}