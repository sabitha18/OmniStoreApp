package com.armada.storeapp.ui.home.riva.riva_look_book.product_listing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.databinding.ActivityProductFilterBinding
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_filter.adapter.FilterAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_filter.adapter.SortAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.model.ProductListMAllFilterModel
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.model.ProductListMSortingModel
import com.google.android.material.slider.RangeSlider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_product_filter.*

@AndroidEntryPoint
class ProductFilterActivity : BaseActivity() {
    lateinit var activityProductFilterBinding: ActivityProductFilterBinding
    private var arrListFilterDataApi: ArrayList<ProductListMAllFilterModel>? =
        ArrayList()
    private var arrListSortData: ArrayList<ProductListMSortingModel>? =
        ArrayList()
    private var noOfFilteredApplied = 0

    var colorView: View? = null
    var brandView: View? = null
    var sizeView: View? = null

    lateinit var filterAdapter: FilterAdapter
//    lateinit var sizeAdapter: FilterAdapter
//    lateinit var brandAdapter: FilterAdapter
    private var strPriceRange = ""
    private var minPrice = 0.00
    private var maxPrice = 0.00
    private var min_value = 0.00
    private var max_value = 0.00
    private var originalMaxPrice = 0.0

    private var filterCount = 0
    private var isExcludedChecked = false
    private var isSaleChecked = false
    private var strSort: String = ""
    private var isList = true

    private var resultCodePrice: Int = 3000
    private var resultCodeAvailability: Int = 5000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProductFilterBinding = ActivityProductFilterBinding.inflate(layoutInflater)
        setContentView(activityProductFilterBinding.root)

        initializeData()
        setOnClickListener()
    }

    private fun initializeData() {
        if (intent.hasExtra("maxPrice")) {
            maxPrice = intent.getDoubleExtra("maxPrice", 0.0)
        }
        if (intent.hasExtra("minPrice")) {
            minPrice = intent.getDoubleExtra("minPrice", 0.0)
        }
        if (intent.hasExtra("originalMaxPrice")) {
            originalMaxPrice = intent.getDoubleExtra("originalMaxPrice", 0.0)
        }
        if (intent.hasExtra("range")) {
            strPriceRange = intent.getStringExtra("range").toString()
        }
        if (intent.hasExtra("isExcludedChecked")) {
            isExcludedChecked = intent.getBooleanExtra("isExcludedChecked", false)
        }
        if (intent.hasExtra("isSaleChecked")) {
            isSaleChecked = intent.getBooleanExtra("isSaleChecked", false)
        }
        if (intent.hasExtra("isSaleChecked")) {
            isSaleChecked = intent.getBooleanExtra("isSaleChecked", false)
        }
        if (intent.hasExtra("isList")) {
            isList = intent.getBooleanExtra("isList", false)
        }

        if (intent.hasExtra("filterData") && intent.getSerializableExtra("filterData") != null) {
            arrListFilterDataApi =
                intent.getSerializableExtra("filterData") as ArrayList<ProductListMAllFilterModel>?
//            setFilterData()
            setFilterOptionsData()
        }

        arrListSortData = ArrayList()

        if (intent.hasExtra("sortBy") && intent.getStringExtra("sortBy") != null) {
            strSort = intent.getStringExtra("sortBy") as String
            setSortData()
        }
        setRangeBar()

    }

    private fun setOnClickListener() {
        activityProductFilterBinding.imageViewClose.setOnClickListener {
            this.finish()
        }

        activityProductFilterBinding.btnViewResults.setOnClickListener {

            if (maxPrice != originalMaxPrice || minPrice > 0) {
                filterCount++
            }
            arrListFilterDataApi?.forEach {
                it.options?.forEach {
                    if (it?.isSelected!!)
                        filterCount++
                }
            }
            val intent = Intent()
            intent.putExtra("filterData", arrListFilterDataApi)
            intent.putExtra("filterCount", filterCount)
            intent.putExtra("maxPrice", maxPrice)
            intent.putExtra("minPrice", minPrice)
            intent.putExtra("range", strPriceRange)
            intent.putExtra("isExcludeChecked", isExcludedChecked)
            intent.putExtra("sortBy", strSort)
            intent.putExtra("applyFilter", "yes")
            setResult(Activity.RESULT_OK, intent)
            finish()
            overridePendingTransition(R.anim.nothing, R.anim.bottom_down)
        }
        activityProductFilterBinding.rangeSlider.addOnChangeListener(RangeSlider.OnChangeListener { slider, value, fromUser ->
            val values = rangeSlider.values
            strPriceRange = "${values[0]} - ${values[1]}"
            maxPrice = values[1].toDouble()
            minPrice = values[0].toDouble()
        })
        activityProductFilterBinding.tvClear.setOnClickListener {
            filterCount = 0
            strPriceRange = ""
            minPrice = 0.00
            maxPrice = originalMaxPrice
            setRangeBar()
            strSort = "2"
            setSortData()
            for (i in 0 until (arrListFilterDataApi?.size ?: 0)) {
                for (j in 0 until arrListFilterDataApi?.get(i)?.options?.size!!) {
                    arrListFilterDataApi?.get(i)?.options?.get(j)?.isSelected = false
                }
            }
            isExcludedChecked = false
            activityProductFilterBinding.checkboxOutofStock.isChecked = false
            setFilterOptionsData()
        }

        activityProductFilterBinding.checkboxOutofStock.setOnCheckedChangeListener { buttonView, isChecked ->
            isExcludedChecked = isChecked
        }
    }

    fun setAdapter(
        recyclerView: RecyclerView,
        spanCount: Int,
        selectedFilterPosition: Int,
        viewType: Int
    ) {
        try {
            recyclerView.layoutManager =
                androidx.recyclerview.widget.GridLayoutManager(this, spanCount)
            val filterAdapter = FilterAdapter(
                this,
                selectedFilterPosition,
                viewType,
                arrListFilterDataApi!!
            )
            recyclerView.adapter = filterAdapter
            filterAdapter?.notifyDataSetChanged()
        } catch (e: Exception) {
            Log.i("ColorFilterActivity", "Exception for color: " + e.printStackTrace())
        }
    }

    private fun setRangeBar() {
        val valuesList = ArrayList<Float>()
        valuesList.add(minPrice.toFloat())
        valuesList.add(originalMaxPrice.toFloat())
        activityProductFilterBinding.rangeSlider.values = valuesList
    }

    private fun setSortData() {
        arrListSortData = ArrayList()
        val sort1 = ProductListMSortingModel(
            sort_name = resources.getString(R.string.recommended),
            sort_value = "1",
            isSelected = false
        )
        val sort2 = ProductListMSortingModel(
            sort_name = resources.getString(R.string.newly_arrived),
            sort_value = "2",
            isSelected = false
        )
        val sort3 = ProductListMSortingModel(
            sort_name = resources.getString(R.string.price_high_to_low),
            sort_value = "3",
            isSelected = false
        )
        val sort4 = ProductListMSortingModel(
            sort_name = resources.getString(R.string.price_low_to_high),
            sort_value = "4",
            isSelected = false
        )

        when(strSort){
            "1"->{
                sort1.isSelected=true
            }
            "2"->{
                sort2.isSelected=true
            }
            "3"->{
                sort3.isSelected=true
            }
            "4"->{
                sort4.isSelected=true
            }
        }

        arrListSortData?.add(sort1)
        arrListSortData?.add(sort2)
        arrListSortData?.add(sort3)
        arrListSortData?.add(sort4)

        if (arrListSortData != null && arrListSortData?.isNotEmpty() == true) {
            val sortAdapter = SortAdapter(this, arrListSortData!!)
            sortAdapter?.onSortSelected = { sort ->
                strSort = sort.sort_value!!
            }
            activityProductFilterBinding.recyclerViewSort.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            activityProductFilterBinding.recyclerViewSort.adapter = sortAdapter
        }
    }

    private fun setFilterOptionsData(
    ) {

        if (arrListFilterDataApi != null && arrListFilterDataApi?.isNotEmpty() == true) {
            linFilter.removeAllViews()

            for ((index, value) in arrListFilterDataApi!!.withIndex()) {
                val inflater = LayoutInflater
                    .from(this@ProductFilterActivity)
                val v = inflater.inflate(R.layout.lv_item_filter_listing, null)
                val txtFilterTitle = v?.findViewById<TextView>(R.id.txtFilterTitle)
                val txtFilterCount = v?.findViewById<TextView>(R.id.txtFilterCount)
                val imgExpand = v?.findViewById<ImageView>(R.id.imgExpand)
                val recyclerView = v?.findViewById<RecyclerView>(R.id.rvFilterData)

                txtFilterTitle?.text = value?.name?.uppercase() ?: ""

                var viewType = 0
                var spanCount = 2
                when (value?.code) {
                    "color_swatch" -> {
                        viewType = 0
                        spanCount = 2
                    }
                    "size" -> {
                        viewType = 1
                        spanCount = 4
                    }
                    "brand" -> {
                        viewType = 2
                        spanCount = 3
                    }
                    else -> {
                        viewType = -1
                    }
                }
                recyclerView?.layoutManager =
                    androidx.recyclerview.widget.GridLayoutManager(this, spanCount)
                filterAdapter = FilterAdapter(
                    this,
                    index,
                    viewType,
                    arrListFilterDataApi!!
                )

                //setting current count
                var qty = 0
                value.options?.forEach {

                    if (it?.isSelected!!)
                        qty++

                }
                if (qty > 0)
                    txtFilterCount?.setText(qty.toString())
                else
                    txtFilterCount?.setText("")

                //changing filter count for each option
                filterAdapter?.onFilterSelected = { list, viewType ->
                    when (viewType) {
                        0 -> {
                            list.forEach {
                                if (it.code.equals("color_swatch")) {
                                    var qty = 0
                                    it.options?.forEach {
                                        if (it.isSelected!!)
                                            qty++
                                    }
                                    txtFilterCount?.text = qty.toString()
                                }

                            }
                        }
                        1 -> {
                            list.forEach {
                                if (it.code.equals("size")) {
                                    var qty = 0
                                    it.options?.forEach {
                                        if (it.isSelected!!)
                                            qty++
                                    }
                                    txtFilterCount?.text = qty.toString()
                                }

                            }
                        }
                        2 -> {
                            list.forEach {
                                if (it.code.equals("brand")) {
                                    var qty = 0
                                    it.options?.forEach {
                                        if (it.isSelected!!)
                                            qty++
                                    }
                                    txtFilterCount?.text = qty.toString()
                                }

                            }
                        }
                        else -> {

                        }
                    }
                }
                recyclerView?.adapter = filterAdapter
                filterAdapter?.notifyDataSetChanged()


                imgExpand?.setOnClickListener {
                    if (recyclerView?.getVisibility() === View.VISIBLE) {
                        TransitionManager.beginDelayedTransition(
                            v?.rootView as ViewGroup?,
                            AutoTransition()
                        )
                        recyclerView.setVisibility(View.GONE)
                        imgExpand.setImageResource(R.drawable.ic_baseline_add_24)
                    } else {
                        TransitionManager.beginDelayedTransition(
                            v?.rootView as ViewGroup,
                            AutoTransition()
                        )
                        recyclerView?.setVisibility(View.VISIBLE)
                        imgExpand.setImageResource(R.drawable.ic_baseline_horizontal_rule_24)
                    }
                }
                linFilter.addView(v)
            }

        }
    }

    fun updatedFilters(filterData: ArrayList<ProductListMAllFilterModel>) {
        arrListFilterDataApi = filterData!!
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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.nothing, R.anim.bottom_down)
    }
}