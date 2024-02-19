package com.armada.storeapp.ui.home.riva.riva_look_book

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.SkuMasterTypes
import com.armada.storeapp.databinding.ActivityRivaLookBookBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.checkout.address.DateFormatter
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.armada.storeapp.ui.utils.Utils
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class RivaLookBookActivity : BaseActivity() {

    private var parentCategory: Int = 0
    lateinit var binding: ActivityRivaLookBookBinding
    var navController: NavController? = null
    lateinit var rivaLookbookViewModel: RivaLookbookViewModel
    private var loading: Dialog? = null
    var itemViewCart: BottomNavigationItemView? = null
    lateinit var badgeCart: View
    var txtBadgeCount: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRivaLookBookBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        rivaLookbookViewModel = ViewModelProvider(this).get(RivaLookbookViewModel::class.java)
//        setSupportActionBar(null)

        val navView: BottomNavigationView = binding.navView
        val bottomNavigationMenuView =
            navView.getChildAt(0) as BottomNavigationMenuView
        itemViewCart = bottomNavigationMenuView.getChildAt(3) as BottomNavigationItemView
        badgeCart = LayoutInflater.from(this)
            .inflate(R.layout.custom_badge_layout, navView, false)
        txtBadgeCount = badgeCart.findViewById(R.id.txtBadgeCount) as TextView

        navController = findNavController(R.id.nav_host_fragment_activity_riva_look_book)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_riva_home,
                R.id.navigation_search,
                R.id.navigation_category,
                R.id.navigation_bag
            )
        )
//        setupActionBarWithNavController(navController!!, appBarConfiguration)
        navView.setupWithNavController(navController!!)
        setBagCount()

    }

//    fun showProgressBar(show: Boolean) {
//        if (show)
//            binding?.cvProgressbar?.visibility = View.VISIBLE
//        else
//            binding?.cvProgressbar?.visibility = View.GONE
//    }

    override fun onResume() {
        super.onResume()
//        binding.imageViewRivaLogo.visibility = View.VISIBLE
        setBagCount()
    }

//    fun FragmentManager.removeAllFragments(containerId: Int) {
//        beginTransaction().apply {
//            fragments.filter {
//                it.id == containerId
//            }.forEach {
//                remove(it)
//            }
//        }.commit()
//    }


    ///loading dialog
    fun showProgress() {

        if (!this@RivaLookBookActivity.isFinishing) {
            if (loading == null) {
                loading = Dialog(this@RivaLookBookActivity, R.style.TranslucentDialog)
                loading?.setContentView(R.layout.custom_loading_view)
                loading?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loading?.setCanceledOnTouchOutside(false)
                loading?.show()
            } else {
                if (!!loading?.isShowing!!) {
                    loading?.show()
                }
            }
        }
    }

    fun dismissProgress() {
        if (loading != null && loading?.isShowing == true)
            loading?.dismiss()
    }

    fun setBagCount() {

        val count = SharedpreferenceHandler(this).getData(SharedpreferenceHandler.CART_COUNT, 0)
        val bottomNavigationMenuView =
            binding.navView.getChildAt(0) as BottomNavigationMenuView
        val bottomNavigationItemView = bottomNavigationMenuView.getChildAt(3) as BottomNavigationItemView
        val badgeCart = LayoutInflater.from(this)
            .inflate(R.layout.custom_badge_layout, bottomNavigationMenuView, false)
        val txtBadgeCount: TextView = badgeCart.findViewById(R.id.txtBadgeCount) as TextView


        // Reset current badge
        bottomNavigationItemView.removeView(bottomNavigationItemView.getChildAt(2))

        // Add new badge
        if (count > 0) {
            txtBadgeCount.text = count.toString()
            bottomNavigationItemView.addView(badgeCart)
        }
//        if (itemViewCart != null && itemViewCart.childCount != 2) itemViewCart.removeViewAt(2)
//        if (count > 0) {
//            badgeCart.visibility = View.VISIBLE
//        bottomNavigationItemView.addView(badgeCart)
//        }else if(count==0){
//            badgeCart.visibility = View.INVISIBLE
//        bottomNavigationItemView.addView(badgeCart)
//        }




//        } else {
//            txtBadgeCount.visibility = View.INVISIBLE
//            txtBadgeCount.text = "0"
//            itemViewCart.removeView(badgeCart)
//            badgeCart.visibility=View.INVISIBLE
//            txtBadgeCount.visibility = View.INVISIBLE

//        }
    }

    fun setParentCategory(selectedParentCategory: Int) {
        parentCategory = selectedParentCategory
    }

    fun getParentCategory(): Int {
        return parentCategory
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        val intent= Intent(this,MainActivity::class.java)
//        startActivity(intent)
//        this.finish()
//    }

    fun hideLogo() {
       binding.imageViewRivaLogo.visibility=View.GONE
    }
}