package com.armada.storeapp.ui.home.riva.riva_look_book.search.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.CollectionListItemModel
import com.armada.storeapp.data.model.response.HomeDataModel
import com.armada.storeapp.data.model.response.PatternsProduct
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.utils.Constants

class SearchImagePagerAdapter(
    private val images: java.util.ArrayList<HomeDataModel.PopularBanner>,
    val rivaLookBookActivity: RivaLookBookActivity,
    var screenWidth: Int,
    layoutInflater: LayoutInflater
) : PagerAdapter() {
    private val inflater: LayoutInflater
    var viewPgHeight: Double = 0.0
    var viewPgWidth: Double = 0.0
    val constant = screenWidth / 320

    init {
        inflater = layoutInflater
        viewPgWidth = (this.constant * 300).toDouble()
        viewPgHeight = (this.constant * 150).toDouble()

    }

    override fun destroyItem(container: View, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View?)
    }

    override fun finishUpdate(container: View) {}

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(view: View, position: Int): Any {

        val imageLayout = inflater.inflate(R.layout.item_image_view, null)
        val spin = imageLayout.findViewById<ProgressBar>(R.id.spin)
        val imgProduct = imageLayout.findViewById<ImageView>(R.id.imgImage)
        if (viewPgWidth == 0.0) {
            val screenWidth =
                (rivaLookBookActivity.resources.getDimension(R.dimen.hundred_sixty_dp) + rivaLookBookActivity.resources.getDimension(
                    R.dimen.hundred_sixty_dp
                )).toInt()
            val constant = screenWidth / 320
            viewPgWidth = (constant * 300).toDouble()
            viewPgHeight = (constant * 150).toDouble()
        }

        if (viewPgWidth > 0 && viewPgHeight > 0) {
            imgProduct.load(if (images[position].image != null && images[position].image != "") images[position].image else Constants.strNoImage) {
                crossfade(true)
                allowConversionToBitmap(true)
                size(viewPgWidth.toInt(), viewPgHeight.toInt())
                bitmapConfig(Bitmap.Config.ARGB_8888)
                allowHardware(true)
                listener(object : ImageRequest.Listener {
                    override fun onSuccess(
                        request: ImageRequest,
                        metadata: ImageResult.Metadata
                    ) {
                        super.onSuccess(request, metadata)
                        spin.visibility = View.GONE
                    }
                })
            }
        }

        imgProduct.setOnClickListener(View.OnClickListener {

            when {
                images[position].type.equals("P", false) -> {

                    val bundle = Bundle()
                    bundle.putString("from_search", "1")
                    bundle.putString("name", images[position].name)
                    rivaLookBookActivity?.navController?.navigate(
                        R.id.navigation_product_listing,
                        bundle
                    )

                }
                images[position].type.equals("C", false) -> {

                    val arrListBanner = ArrayList<String>()
                    val arrListCollection = java.util.ArrayList<CollectionListItemModel>() //Dummy
                    val arrList = ArrayList<PatternsProduct>()

                    val model = CollectionListItemModel(
                        images.get(position).image,
                        "",
                        "",
                        images.get(position).type_id,
                        images.get(position).name,
                        images.get(position).type,
                        images.get(position).type_id.toString(),
                        images.get(position).path,
                        images.get(position).lvl,
                        "no",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        images.get(position).name,
                        images.get(position).type_id,
                        images.get(position).path,
                        images.get(position).lvl,
                        0,
                        0,
                        "",
                        arrList as ArrayList<PatternsProduct?>,
                        0,
                        "",
                        arrListCollection as ArrayList<CollectionListItemModel?>,
                        0,
                        arrListBanner,
                        0,
                        0
                    )

                    val bundle = Bundle()
                    bundle.putInt("banner_height", 0)
                    bundle.putSerializable("model", model)
                    rivaLookBookActivity?.navController?.navigate(
                        R.id.navigation_product_listing,
                        bundle
                    )

                }
                images.get(position).type.equals("E", false) -> {
                    val bundle = Bundle()
                    bundle.putInt("id", images.get(position).type_id)
                    bundle.putString("name", images.get(position).name)
                    rivaLookBookActivity?.navController?.navigate(R.id.navigation_editorial, bundle)
                }
                else -> {

                    val intent = Intent(rivaLookBookActivity, OmniProductDetailsActivity::class.java)
                    intent.putExtra("id", images.get(position).type_id.toString())
                    intent.putExtra("name", images.get(position).name)
                    intent.putExtra("image", images.get(position).image)
                    rivaLookBookActivity.startActivity(intent)


                }
            }
        })

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