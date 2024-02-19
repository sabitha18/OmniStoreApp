package com.armada.storeapp.ui.home.riva.riva_look_book.search.BarcodeScanner

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.armada.storeapp.R
import com.armada.storeapp.databinding.ActivityScanHistoryBinding
import com.armada.storeapp.databinding.ItemScanHistoryBinding
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.armada.storeapp.ui.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.toolbar_default.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ScanHistoryActivity : BaseActivity() {

    lateinit var activityScanHistoryBinding: ActivityScanHistoryBinding
    private var mToolbar: Toolbar? = null
    var adapter: HistoryAdapter? = null
    var arrListCode: ArrayList<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        activityScanHistoryBinding = ActivityScanHistoryBinding.inflate(layoutInflater)
        setContentView(activityScanHistoryBinding.root)

        initToolbar()
        init()
        setOnClickListener()
    }

    ///
    private fun initToolbar() {
        mToolbar = activityScanHistoryBinding.toolbarActionbar?.root
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val upArrow: Drawable =
            resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)
        upArrow.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
        upArrow.setVisible(true, true)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        activityScanHistoryBinding.toolbarActionbar.txtHead.text =
            resources.getString(R.string.scan_history)
    }

    ///
    private fun init() {

        activityScanHistoryBinding?.toolbarActionbar?.txtApply.text =
            resources.getString(R.string.clear)
        txtApply.setTextColor(resources.getColor(R.color.black))
        activityScanHistoryBinding.imgNoResult.setColorFilter(resources.getColor(R.color.black))
        val sharedpreferenceHandler =SharedpreferenceHandler(this)
        val scanHistory=sharedpreferenceHandler.getData("scan_history","")

        val list=Utils.getArrayList(this,"scan_history")
        if (list != null && list.size > 0) {
            txtApply.visibility = View.VISIBLE
            arrListCode = ArrayList()
            arrListCode?.addAll(list as ArrayList<String>)
            arrListCode?.reverse()
            adapter = HistoryAdapter(this@ScanHistoryActivity, arrListCode!!)
            activityScanHistoryBinding.rcyScanHistory.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(this@ScanHistoryActivity)
            activityScanHistoryBinding.rcyScanHistory.adapter = adapter

        } else {

            activityScanHistoryBinding.rcyScanHistory.visibility = View.GONE
            activityScanHistoryBinding.relNoResult.visibility = View.VISIBLE
        }
    }

    private fun setOnClickListener() {
        activityScanHistoryBinding.toolbarActionbar.txtApply.setOnClickListener {
            arrListCode?.clear()
            adapter?.notifyDataSetChanged()
            Utils.clearArrayList(this,"scan_history")
            activityScanHistoryBinding.rcyScanHistory.visibility = View.GONE
            activityScanHistoryBinding.relNoResult.visibility = View.VISIBLE
            activityScanHistoryBinding.toolbarActionbar.txtApply.visibility = View.GONE
        }
    }


    ///
    inner class HistoryAdapter(
        private val mContext: Context,
        private val arrListHistory: ArrayList<String>
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MyViewHolder {
            return MyViewHolder(
                ItemScanHistoryBinding.inflate(
                    LayoutInflater.from(this@ScanHistoryActivity),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val product = arrListHistory[position].split("$")

            holder.binding.txtBarcode.text = product[0].replace("$", "")

            holder.binding.root.setOnClickListener {
                try {
                    val strEntityId = product[1].replace("$", "")
                    val intent =
                        Intent(this@ScanHistoryActivity, OmniProductDetailsActivity::class.java)
                    intent.putExtra("id", strEntityId)
                    intent.putExtra("cat_id", "-1")
                    intent.putExtra("name", "")
                    startActivity(intent)
                } catch (e: Exception) {

                }

            }
        }

        override fun getItemCount(): Int {
            return arrListHistory.size
        }

        inner class MyViewHolder(val binding: ItemScanHistoryBinding) :
            RecyclerView.ViewHolder(binding.root) {
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
