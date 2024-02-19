package com.armada.storeapp.ui.home.riva.riva_look_book.select_country

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.local.RivaDatabase
import com.armada.storeapp.data.model.StoreData
import com.armada.storeapp.data.model.response.CountryStoreResponse
import com.armada.storeapp.databinding.ActivitySelectCountryBinding
import com.armada.storeapp.di.DatabaseModule
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.riva_login.RivaLoginActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.select_country.adapter.SelectStoreAdapter
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import kotlinx.android.synthetic.main.toolbar_default.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelectCountryActivity : BaseActivity() {

    lateinit var binding: ActivitySelectCountryBinding
    lateinit var selectCountryViewModel: SelectCountryViewModel
    var selectStoreAdapter: SelectStoreAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCountryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        selectCountryViewModel = ViewModelProvider(this).get(SelectCountryViewModel::class.java)
        initToolbar()
        init()
        getCountries()
    }


    private fun initToolbar() {
        setSupportActionBar(binding.toolbarActionbar.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val upArrow: Drawable? =
            ContextCompat.getDrawable(
                this@SelectCountryActivity,
                R.drawable.ic_baseline_arrow_back_24
            )

        upArrow?.setColorFilter(
            ContextCompat.getColor(this@SelectCountryActivity, R.color.black),
            PorterDuff.Mode.SRC_ATOP
        )
        upArrow?.setVisible(true, true)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        txtHead.text = "SELECT COUNTRY"
    }

    fun init() {

    }

    fun getCountries() {
        selectCountryViewModel.getCountryStores()
        selectCountryViewModel.responseCountryData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.statusCode == 200) {
                        val list = it.data?.data
                        selectStoreAdapter = SelectStoreAdapter(
                            this,
                            list!! /* = java.util.ArrayList<com.armada.storeapp.data.model.StoreData> */
                        )
                        binding.recyclerViewStores.layoutManager =
                            androidx.recyclerview.widget.LinearLayoutManager(this)
                        binding.recyclerViewStores.adapter = selectStoreAdapter
                    }

                }
                is Resource.Error -> {

                }
                is Resource.Loading -> {

                }
            }
        }
    }

    fun clearDatabase(){
        emptyDatabase()
    }

    fun Activity.emptyDatabase() {
        // create a scope to access the database from a thread other than the main thread
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            DatabaseModule.provideRivaDatabase(this@emptyDatabase).clearAllTables()
        }
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