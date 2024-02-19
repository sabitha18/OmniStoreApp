package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details


import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import com.armada.storeapp.R
import com.armada.storeapp.databinding.ActivityImageViewerBinding
import com.armada.storeapp.databinding.SingleimageViewerBinding
import com.armada.storeapp.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ImageViewerActivity : BaseActivity() {
    var binding: ActivityImageViewerBinding? = null
    private var post: String? = null
    private var arry: ArrayList<String>? = null
    private var thumbnail: String? = null
    private var gone = true
    private var height: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        intent?.let {
            arry = ArrayList()
            arry = intent.getSerializableExtra("arraylist") as ArrayList<String>
            post = intent.getStringExtra("post")
        }

        val metrics = resources.displayMetrics
        height = metrics.heightPixels

        val small_param = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        binding?.relMain?.layoutParams = small_param
        binding?.pager?.layoutParams = small_param

        binding?.imgUpload?.setColorFilter(Color.parseColor("#ffffff"))
        binding?.imgUpload?.visibility = View.GONE

        binding?.txtDone?.setOnClickListener { finish() }

        binding?.txtImageCount?.text =
            "1 " + resources.getString(R.string.of) + " " + arry?.size.toString()
        setPager()
    }

    ///
    inner class DetailOnPageChangeListener : ViewPager.SimpleOnPageChangeListener() {

        var currentPage: Int = 0
            private set

        override fun onPageSelected(position: Int) {
            currentPage = position

            binding?.txtImageCount?.text =
                (position + 1).toString() + " " + resources.getString(R.string.of) + " " + arry?.size.toString()
        }
    }


    private inner class ImagePagerAdapter internal constructor(private val images: ArrayList<String>) :
        PagerAdapter() {
        private val inflater: LayoutInflater = layoutInflater

        override fun destroyItem(container: View, position: Int, `object`: Any) {
            (container as ViewPager).removeView(`object` as View?)
        }

        override fun finishUpdate(container: View) {}

        override fun getCount(): Int {
            return images.size
        }

        override fun instantiateItem(view: View, position: Int): Any {

            val singleimageViewerBinding = SingleimageViewerBinding.inflate(inflater)
            val relParam = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
            singleimageViewerBinding.relBanner.layoutParams = relParam
            singleimageViewerBinding.snoop.layoutParams = relParam
            thumbnail = arry!![position]

            singleimageViewerBinding.snoop.load(if (thumbnail != null && thumbnail != "") thumbnail else "https://www.fashiongonerogue.com/wp-content/uploads/2014/04/iggy-azalea-revolve-clothing-photos-2014-1.jpg") {
                crossfade(true)
                allowConversionToBitmap(true)
                bitmapConfig(Bitmap.Config.ARGB_8888)
                allowHardware(true)
                listener(object : ImageRequest.Listener {
                    override fun onSuccess(
                        request: ImageRequest,
                        metadata: ImageResult.Metadata
                    ) {
                        super.onSuccess(request, metadata)
                        singleimageViewerBinding.loading.visibility = View.GONE
                    }
                })
            }
            singleimageViewerBinding.snoop.setOnClickListener(View.OnClickListener {
                if (!gone) {
                    binding?.relTop?.visibility = View.GONE
                    binding?.relBottom?.visibility = View.GONE
                    gone = true
                } else {
                    binding?.relTop?.visibility = View.VISIBLE
                    binding?.relBottom?.visibility = View.GONE
                    gone = false

                }
            })

            (view as ViewPager).addView(singleimageViewerBinding.root)

            return singleimageViewerBinding.root
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

        override fun saveState(): Parcelable? {
            return null
        }

        override fun startUpdate(container: View) {}

    }

    private fun setPager() {
        binding?.pager?.adapter = ImagePagerAdapter(arry!!)
        val listener = DetailOnPageChangeListener()
        binding?.pager?.setOnPageChangeListener(listener)
        binding?.pager?.currentItem = Integer.parseInt(post!!)
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