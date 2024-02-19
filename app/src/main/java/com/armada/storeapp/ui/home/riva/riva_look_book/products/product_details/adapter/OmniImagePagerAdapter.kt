package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.transition.CrossfadeTransition
import com.armada.storeapp.R
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ImageViewerActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.Utils
class OmniImagePagerAdapter(
    val activity: OmniProductDetailsActivity,
    val images: ArrayList<String>
) :PagerAdapter() {

    private val inflater: LayoutInflater = activity.layoutInflater


    override fun destroyItem(container: View, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View?)
    }

    override fun finishUpdate(container: View) {}

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(view: View, position: Int): Any {

        val imageLayout = inflater.inflate(R.layout.layout_product_detail_image, null)
        val spin = imageLayout.findViewById<ProgressBar>(R.id.spin)
        val imgProduct = imageLayout.findViewById<ImageView>(R.id.imgImage)
        val width = Utils.getDeviceWidth(activity)
        val imageSize = RelativeLayout.LayoutParams(width, (width * 1.4562).toInt())
        imageSize.setMargins(0, 0, 0, 0)

        imgProduct.layoutParams = imageSize

        //println("Images: " + images[position])
        if (!activity.isFinishing) {
            if (width.toInt() > 0) {
                imgProduct.load(if (images[position] != null && images[position] != "") images[position] else Constants.strNoImage) {
                    crossfade(true)
                    crossfade(800)
                    allowConversionToBitmap(true)
                    bitmapConfig(Bitmap.Config.ARGB_8888)
                    allowHardware(true)
                    size(width, (width * 1.4562).toInt())
                    listener(object : ImageRequest.Listener {
                        override fun onSuccess(
                            request: ImageRequest,
                            metadata: ImageResult.Metadata
                        ) {
                            super.onSuccess(request, metadata)
                            spin.visibility = View.GONE
//                            imgProduct.visibility = View.GONE
                        }
                    })
                    transition(CrossfadeTransition(800, false))
                }
            }
        }

        imgProduct.setOnClickListener {
            val intent = Intent(activity, ImageViewerActivity::class.java)
            intent.putExtra("arraylist", images)
            intent.putExtra("post", position.toString())
            activity.startActivity(intent)
        }
                (view as ViewPager).addView(imageLayout)

        return imageLayout
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

