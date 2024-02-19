package com.armada.storeapp.ui.home.riva.riva_look_book.search.Stores

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import com.armada.storeapp.R
import com.armada.storeapp.databinding.ActivityMapStoreBinding
import com.armada.storeapp.databinding.ActivityRivaLookBookBinding
import com.armada.storeapp.ui.base.BaseActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.*


/**
 * Created by User999 on 3/7/2018.
 */
@AndroidEntryPoint
class MapActivity : BaseActivity() , OnMapReadyCallback {

    lateinit var activityMapStoreBinding: ActivityMapStoreBinding
    private var mToolbar: Toolbar? = null
    var mapFragment: SupportMapFragment? = null
    var arryListProduct = ArrayList<StoreData>()
    var latitude: Double? = null
    var longitude: Double? = null
    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMapStoreBinding= ActivityMapStoreBinding.inflate(layoutInflater)
        setContentView(activityMapStoreBinding.root)
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment!!.getMapAsync(this)
        mToolbar = activityMapStoreBinding.toolbarActionbar1.root
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val upArrow: Drawable
          = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)
        upArrow.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
        upArrow.setVisible(true, true)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        activityMapStoreBinding.toolbarActionbar1.txtHead.text = resources.getString(R.string.our_stores)

        if (intent.getSerializableExtra("arryList") != null) {
            arryListProduct = intent.getSerializableExtra("arryList") as ArrayList<StoreData>
        }

        if (intent.getStringExtra("title") != null){
            latitude = intent.getDoubleExtra("latitude",0.00)
            longitude = intent.getDoubleExtra("longitude",0.00)
            title = intent.getStringExtra("title")!!
        }




    }

    override fun onMapReady(googleMap: GoogleMap) {
        val mMap: GoogleMap? = googleMap;
        googleMap!!.setMapType(GoogleMap.MAP_TYPE_NORMAL)
        val builder: LatLngBounds.Builder = LatLngBounds.Builder();

        var currentMarker: Marker? = null
        val height = 100
        val width = 100
        val bitmapdraw = resources.getDrawable(R.mipmap.ic_launcher) as BitmapDrawable
        val b = bitmapdraw.bitmap
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)

        if (intent.getSerializableExtra("arryList") != null) {
            for (i in 0 until arryListProduct.size) {


                val markerOptions = MarkerOptions()
                markerOptions.position(LatLng(arryListProduct[i].latitude ?: 0.0, arryListProduct[i].longitude ?: 0.0)).title(arryListProduct[i].store_name)
                mMap!!.addMarker(markerOptions)
                builder.include(markerOptions.position)

                val bounds: LatLngBounds = builder.build();

                // offset from edges of the map in pixels
                val cu: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);


                mMap!!.setOnMapLoadedCallback(GoogleMap.OnMapLoadedCallback {
                    //Your code where exception occurs goes here...
                    // mMap!!.moveCamera(cu);
                    mMap.animateCamera(cu);
                })


            }
        }else

            if (intent.getDoubleExtra("latitude", 0.00) != null) {
                val markerOptions = MarkerOptions()
                markerOptions.position(LatLng(latitude!!, longitude!!)).title(title)
                mMap!!.addMarker(markerOptions)
                builder.include(markerOptions.position)
                val bounds: LatLngBounds = builder.build();

                // offset from edges of the map in pixels
                val cu: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);


                mMap!!.setOnMapLoadedCallback(GoogleMap.OnMapLoadedCallback {
                    //Your code where exception occurs goes here...
                    // mMap!!.moveCamera(cu);
                    mMap.animateCamera(cu);
                })
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