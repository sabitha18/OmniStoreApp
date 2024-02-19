package com.armada.storeapp.ui.home.riva.riva_look_book.bag

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.armada.storeapp.R
import com.armada.storeapp.databinding.ActivityBagBinding
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.bag.BagViewModel
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import kotlinx.android.synthetic.main.detail_toobar.*

class BagActivity : BaseActivity() {

    private var mToolbar: Toolbar? = null
    lateinit var binding: ActivityBagBinding
    lateinit var bagViewModel: BagViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBagBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        init()
    }

    ///
    private fun initToolbar() {
        mToolbar = binding.toolbarActionbar.root
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val upArrow: Drawable =
            resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)

        upArrow.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
        upArrow.setVisible(true, true)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        binding.toolbarActionbar.txtHead.text = resources.getString(R.string.shopping_bag)
//        binding.toolbarActionbarimgHelp.visibility=View.VISIBLE

    }

    fun init() {
        val sharedpreferenceHandler = SharedpreferenceHandler(this)
        val cartCount = sharedpreferenceHandler.getData(SharedpreferenceHandler.CART_COUNT, 0)
        setBagCount(cartCount)
    }

    fun setBagCount(bagCount: Int) {
        if (bagCount > 0) {
            binding.toolbarActionbar.txtCartCount.text = bagCount.toString()
            binding.toolbarActionbar.txtCartCount.visibility = View.VISIBLE
        } else
            binding.toolbarActionbar.txtCartCount.visibility = View.INVISIBLE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}