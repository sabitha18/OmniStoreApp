package com.armada.storeapp.ui.home.riva.riva_look_book.search.Stores

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.databinding.ActivityStoreListingBinding
import com.armada.storeapp.databinding.LvStoreItemsBinding
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookbookViewModel
import com.armada.storeapp.ui.home.riva.riva_look_book.search.Stores.MapActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.search.Stores.StoreData
import com.armada.storeapp.ui.home.riva.riva_look_book.search.Stores.StoreDataResponseModel
import com.armada.storeapp.ui.home.riva.riva_look_book.search.Stores.StoreListViewModel
import com.armada.storeapp.ui.utils.*
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.*

@AndroidEntryPoint
class StoreListingActivity : BaseActivity() {

    private var activityStoreListingBinding: ActivityStoreListingBinding? = null
    lateinit var storeListViewModel: StoreListViewModel
    private var mToolbar: Toolbar? = null
    private var strId: String? = null
    private var loadingView: LoadingView? = null
    private var cd: ConnectionDetector? = null
    private var isFromRefresh: Boolean = false
    var layoutManager: androidx.recyclerview.widget.GridLayoutManager? = null
    var latitude: Double? = 0.00
    var longitude: Double? = 0.00
    var arrListStores = ArrayList<StoreData>()
    var gps: GPSTracker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityStoreListingBinding = ActivityStoreListingBinding.inflate(layoutInflater)
        setContentView(activityStoreListingBinding!!.root)
        storeListViewModel = ViewModelProvider(this).get(StoreListViewModel::class.java)
        initToolbar()
        setEmptyPage()
        init()
        setOnClickListener()
    }

    ///
    private fun initToolbar() {
        mToolbar = activityStoreListingBinding?.toolbarActionbar?.root as Toolbar?
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val upArrow: Drawable = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)
        upArrow.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
        upArrow.setVisible(true, true)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        activityStoreListingBinding?.toolbarActionbar?.txtHead?.text =
            resources.getString(R.string.our_stores_header)
    }

    private fun setEmptyPage() {
        val emptyPage = Utils.empty_store
        if (emptyPage != null) {
            if (!emptyPage.icon.isNullOrEmpty()) {
                Utils.loadImagesUsingCoil(
                    this@StoreListingActivity,
                    emptyPage.icon,
                    activityStoreListingBinding?.imgEmptyStore!!
                )
                //Glide.with(this@StoreListingActivity).load(emptyPage.icon).into(imgEmptyStore)
                activityStoreListingBinding?.imgEmptyStore?.visibility = View.VISIBLE
            } else {
                activityStoreListingBinding?.imgEmptyStore?.visibility = View.GONE

            }
            if (!emptyPage.title.isNullOrEmpty()) {
                activityStoreListingBinding?.txtEmpty?.text = emptyPage.title
                activityStoreListingBinding?.txtEmpty?.visibility = View.VISIBLE
            } else {
                activityStoreListingBinding?.txtEmpty?.visibility = View.GONE

            }
            if (!emptyPage.subtitle.isNullOrEmpty()) {
                activityStoreListingBinding?.txtNote?.text = emptyPage.subtitle
                activityStoreListingBinding?.txtNote?.visibility = View.VISIBLE
            } else {
                activityStoreListingBinding?.txtNote?.visibility = View.GONE
            }
        }
    }

    ///
    private fun init() {
        cd = ConnectionDetector(this)
        gps = GPSTracker(this@StoreListingActivity)

        if (intent.hasExtra("id")) {
            strId = intent.getStringExtra("id")!!
        }

        if (gps!!.canGetLocation()) {

            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val countryCode = tm.networkCountryIso

            latitude = gps!!.latitude
            longitude = gps!!.longitude

            if (latitude == 0.00 && longitude == 0.00) {
                storeListViewModel.getLocation()
            }
        }

        activityStoreListingBinding?.swipeRefreshLayout?.setOnRefreshListener {
            isFromRefresh = true
            activityStoreListingBinding?.swipeRefreshLayout?.isRefreshing = true
            activityStoreListingBinding?.swipeRefreshLayout?.postDelayed({
                activityStoreListingBinding?.swipeRefreshLayout?.isRefreshing = false

                if (cd!!.isConnectingToInternet) {
                    getStoreList()
                } else {
                    Utils.showSnackbar(
                        activityStoreListingBinding?.swipeRefreshLayout!!,
                        resources.getString(R.string.noInternet)
                    )
                }
            }, 1000)
        }

        if (cd!!.isConnectingToInternet)
            getStoreList()
        else Utils.showSnackbar(
            activityStoreListingBinding?.swipeRefreshLayout!!,
            resources.getString(R.string.noInternet)
        )

        storeListViewModel.responseLocation.observe(this) {
            latitude = it.lat
            longitude = it.lon
        }

    }


    private fun setOnClickListener() {

        activityStoreListingBinding?.txtContinue?.setOnClickListener {
            this.finish()
        }

        activityStoreListingBinding?.toolbarActionbar?.imgGlobe?.setOnClickListener()
        {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("arryList", arrListStores)
            startActivity(intent)
        }
    }


    fun getStoreList() {
        val sharedpreferenceHandler=SharedpreferenceHandler(this)
        val selectedLanguage=sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY,"en")!!
        storeListViewModel.getStoreList(selectedLanguage)
        storeListViewModel.responseStoreList.observe(this) {
            when (it) {
                is Resource.Success -> {
                    activityStoreListingBinding?.swipeRefreshLayout?.isRefreshing = false
                    isFromRefresh = false
                    setData(it.data!!)
                }

                is Resource.Error -> {
                    activityStoreListingBinding?.swipeRefreshLayout?.isRefreshing = false
                    isFromRefresh = false
                    loadingView?.hide()
                    Utils.showErrorSnackbar(activityStoreListingBinding?.relMain!!)
                    activityStoreListingBinding?.lnrNoItems?.visibility = View.VISIBLE
                    activityStoreListingBinding?.rcyStoreListing?.visibility = View.GONE
                    activityStoreListingBinding?.toolbarActionbar?.imgGlobe?.visibility = View.GONE
                }
                is Resource.Loading -> {
                    loadingView?.show()
                }
            }
        }
    }


    private fun setData(result: StoreDataResponseModel) {
        loadingView?.hide()
        if (!result.data.isNullOrEmpty()) {
            arrListStores.clear()
            for (i in 0 until result.data.size) {
                if (!result.data[i].store_id.isNullOrEmpty()) {
                    arrListStores.add(result.data[i])
                }
            }

            if (arrListStores.isNotEmpty()) {
                activityStoreListingBinding?.lnrNoItems?.visibility = View.GONE
                activityStoreListingBinding?.rcyStoreListing?.visibility = View.VISIBLE
                activityStoreListingBinding?.toolbarActionbar?.imgGlobe?.visibility = View.VISIBLE
            } else {
                activityStoreListingBinding?.lnrNoItems?.visibility = View.VISIBLE
                activityStoreListingBinding?.rcyStoreListing?.visibility = View.GONE
                activityStoreListingBinding?.toolbarActionbar?.imgGlobe?.visibility = View.GONE
            }

            try {
                getSortedStores(arrListStores)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val adapter = StoreListAdapter(
                this@StoreListingActivity,
                arrListStores,
                latitude!!,
                longitude!!
            )
            activityStoreListingBinding?.rcyStoreListing?.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(this@StoreListingActivity)
            adapter.notifyDataSetChanged()
            activityStoreListingBinding?.rcyStoreListing?.adapter = adapter

        } else {
            activityStoreListingBinding?.lnrNoItems?.visibility = View.VISIBLE
            activityStoreListingBinding?.rcyStoreListing?.visibility = View.GONE
            activityStoreListingBinding?.toolbarActionbar?.imgGlobe?.visibility = View.GONE
        }
    }


    private fun getSortedStores(arrList: ArrayList<StoreData>): ArrayList<StoreData> {

        for (i in 0 until arrList.size) {
            val startPoint = Location("locationA")
            latitude?.let { startPoint.latitude = it }
            longitude?.let { startPoint.longitude = it }

            val endPoint = Location("locationA")
            endPoint.latitude = arrList[i].latitude ?: 0.0
            endPoint.longitude = arrList[i].longitude ?: 0.0

            val distance = startPoint.distanceTo(endPoint) / 1000
            arrList[i].distance = distance
        }

        Collections.sort(arrList, object : Comparator<StoreData> {

            override fun compare(store1: StoreData, store2: StoreData): Int {

                return (store1.distance).compareTo(store2.distance)
            }
        })

        return arrList
    }

    //define the listener
    inner class StoreListAdapter(
        private val mContext: Context,
        private val storeList: ArrayList<StoreData>,
        var lati: Double,
        var longi: Double
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<StoreListAdapter.MyViewHolder>() {

        private var screenWidth: Int = 0
        private var screenHeight: Int = 0
        private var productWidth: Int = 0
        private var productheight: Int = 0


        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): StoreListAdapter.MyViewHolder {
            return MyViewHolder(
                LvStoreItemsBinding.inflate(
                    LayoutInflater.from(this@StoreListingActivity),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(
            holder: StoreListAdapter.MyViewHolder,
            position: Int
        ) {

            val metrics = mContext.resources.displayMetrics

            screenWidth = (metrics.widthPixels)
            productWidth = screenWidth
            productheight = ((productWidth / 1.75).toInt())
            screenHeight = productheight / 2

            val relParam =
                RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, productheight)
            relParam.addRule(RelativeLayout.CENTER_HORIZONTAL)

            holder.binding.txtStoreName.text = storeList[position].store_name
            holder.binding.txtPhone.text = storeList[position].phone
            holder.binding.txtLocation.text = storeList[position].address
            holder.binding.imgPhone.setColorFilter(mContext.resources.getColor(R.color.secondary_text))
            holder.binding.imgLocateStore.setColorFilter(mContext.resources.getColor(R.color.secondary_text))

            val startPoint = Location("locationA")
            latitude?.let { startPoint.latitude = it }
            longitude?.let { startPoint.longitude = it }

            val endPoint = Location("locationA")
            endPoint.latitude = storeList[position].latitude?.toDouble() ?: 0.0
            endPoint.longitude = storeList[position].longitude?.toDouble() ?: 0.0

            val distance = startPoint.distanceTo(endPoint)
//            holder.binding.txtDistance.text = "%.2f".format(storeList.get(position).distance)
//                .toString() + " " + resources.getString(R.string.km)

//            if (latitude == 0.00 && longitude == 0.00) {
//                holder.binding.txtDistance.visibility = View.GONE
//            } else {
//                holder.binding.txtDistance.visibility = View.VISIBLE
//            }

            if (!storeList.get(position).address.isNullOrEmpty()) {
                holder.binding.locationGroup.visibility = View.VISIBLE
            } else {
                holder.binding.locationGroup.visibility = View.GONE
            }


            holder.binding.phoneGroup.setOnClickListener {
                val alertDialogBuilder: AlertDialog.Builder? = AlertDialog.Builder(mContext)
                alertDialogBuilder!!
                    .setMessage(
                        mContext.resources.getString(R.string.call) + " " + storeList.get(
                            position
                        ).phone
                    )
                    .setCancelable(false)
                    .setPositiveButton(
                        mContext.resources.getString(R.string.yes),
                        DialogInterface.OnClickListener { dialog, which ->
                            // TODO Auto-generated method stub
                            val intent = Intent(
                                Intent.ACTION_DIAL,
                                Uri.parse("tel:" + storeList.get(position).phone)
                            )

                            if (ContextCompat.checkSelfPermission(
                                    mContext,
                                    android.Manifest.permission.CALL_PHONE
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                requestCallPermission(this@StoreListingActivity)
                            } else {
                                dialog.dismiss()
                                startActivity(intent)
                            }
                            dialog.dismiss()
                        })
                    .setNegativeButton(
                        mContext.resources.getString(R.string.no),
                        DialogInterface.OnClickListener { dialog, which ->
                            // TODO Auto-generated method stub
                            dialog.dismiss()
                        })
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
                val textView = alertDialog.findViewById<TextView>(android.R.id.message) as TextView
            }

            holder.binding.root.setOnClickListener {
                val uri: String = String.format(
                    Locale.ENGLISH,
                    "http://maps.google.com/maps?daddr=%f,%f (%s)",
                    storeList[position].latitude,
                    storeList[position].longitude,
                    storeList[position].store_name
                )
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                try {
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    try {
                        val unrestrictedIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        startActivity(unrestrictedIntent)
                    } catch (innerEx: ActivityNotFoundException) {
                        Utils.showSnackbar(
                            activityStoreListingBinding?.swipeRefreshLayout!!,
                            "Please install map application"
                        )
                    }
                }
            }

        }

        override fun getItemCount(): Int {
            return storeList.size
        }

        inner class MyViewHolder(val binding: LvStoreItemsBinding) :
            androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)
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
