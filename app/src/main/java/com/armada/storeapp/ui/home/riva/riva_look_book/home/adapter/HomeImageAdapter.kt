package com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import com.armada.riva.HOME.ProductWebActivity
import com.armada.riva.HOME.VideoActivity
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.CollectionListItemModel
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.home.ExtendedCategoryFragment
import com.armada.storeapp.ui.home.riva.riva_look_book.home.SubCollectionFragment
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.ProductListingFragment
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.Utils
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware
import kotlinx.android.synthetic.main.home_view_image.view.*


class HomeImageAdapter internal constructor(
    var mContext: Context,
    private val image_Height: Int,
    private val top_margin: Int,
    private val bottom_margin: Int,
    private val collectionTitle: Int,
    private val collectionSubTitle: Int,
    private val images: List<CollectionListItemModel?>,
    private var fromHome: Boolean,
    private var rivaLookBookActivity: RivaLookBookActivity
) : PagerAdapter() {
    private val inflater: LayoutInflater = (mContext as Activity).layoutInflater
    private var screenWidth: Int = 0

    init {
        val metrics = mContext.resources.displayMetrics
        screenWidth = (metrics.widthPixels)
    }

    override fun destroyItem(container: View, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View?)
    }

    override fun finishUpdate(container: View) {}

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(view: View, position: Int): Any {

        val imageLayout = inflater.inflate(R.layout.home_view_image, null)

        val paramMain = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val lpImage = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, image_Height)
        lpImage.addRule(RelativeLayout.CENTER_HORIZONTAL)
        imageLayout.imgBrand.layoutParams = lpImage

        paramMain.addRule(RelativeLayout.CENTER_HORIZONTAL)
        paramMain.topMargin = top_margin
        paramMain.bottomMargin = bottom_margin
        imageLayout.relMain.layoutParams = paramMain

        when {
            images[position]?.media_type.equals("I", false) -> {
                try {
                    val imageLoader: ImageLoader? = ImageLoader.getInstance()
                    imageLoader!!.init(ImageLoaderConfiguration.createDefault(mContext.applicationContext))

                    imageLayout.progressSpinner.visibility = View.VISIBLE

                    val imageAware = ImageViewAware(imageLayout.imgBrand, false)


                    imageLayout.imgBrand.load(
                        if (images[position]?.image != null && !images[position]?.image.equals(
                                ""
                            )
                        ) images[position]?.image else Constants.strNoImage
                    ) {
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
                                imageLayout.progressSpinner.visibility = View.GONE
                            }
                        })
                    }

                } catch (e: Exception) {

                }
            }
            images[position]?.media_type.equals("G", false) -> {
                imageLayout.progressSpinner.visibility = View.GONE
                imageLayout.imgPlay.visibility = View.GONE
                Utils.loadGifUsingCoil(
                    mContext,
                    if (images[position]?.media_file != null && images[position]?.media_file != "") images[position]?.media_file else Constants.strNoImage,
                    imageLayout.imgBrand
                )
//                Glide.with(mContext).asGif().load(if (images[position].media_file != null && images[position].media_file != "") images[position].media_file else Global.strNoImage).into(imageLayout.imgBrand)
            }
            images[position]?.media_type.equals("V", false) -> {
                imageLayout.progressSpinner.visibility = View.GONE

                imageLayout.imgPlay.visibility = View.VISIBLE
                //val btmThumbnail= retriveVideoThumb(images.get(position).media_file)
                Utils.loadImagesUsingCoil(
                    mContext,
                    if (images[position]?.media_thumbnail != null && images[position]?.media_thumbnail != "") images[position]?.media_thumbnail else Constants.strNoImage,
                    imageLayout.imgBrand
                )

            }
            else -> {
                imageLayout.progressSpinner.visibility = View.GONE

                imageLayout.imgPlay.visibility = View.GONE
                // Picasso.with(mContext).load(images.get(position).image!!).into(imageLayout.imgBrand)
                Utils.loadImagesUsingCoil(
                    mContext,
                    if (images[position]?.image != null && images[position]?.image != "") images[position]?.image else Constants.strNoImage,
                    imageLayout.imgBrand
                )
            }
        }


        if (collectionSubTitle == 1) {
            imageLayout.txtSubtitle.visibility = View.GONE
        } else {
            imageLayout.txtSubtitle.visibility = View.VISIBLE
            imageLayout.txtSubtitle.text =
                Utils.getDynamicStringFromApi(mContext, images[position]?.sub_title)
        }

        if (collectionTitle == 1) {
            imageLayout.txtTitle.visibility = View.GONE
        } else {
            imageLayout.txtTitle.visibility = View.VISIBLE
            imageLayout.txtTitle.text =
                Utils.getDynamicStringFromApi(mContext, images[position]?.title)
        }

        imageLayout.relMain.setOnClickListener {

            if (images[position]?.has_collection_groups == 1) {

                if (images[position]?.id != null)
                    images[position]?.id = images[position]?.category_id

                if (images[position]?.title.isNullOrEmpty())
                    images[position]?.title = images[position]?.name

                val bundle = Bundle()
                bundle.putInt("id", images[position]?.id!!)
                bundle.putString("name", images[position]?.title)
                bundle.putBoolean("from_home", fromHome)

                val subCollectionFragment= SubCollectionFragment()
                subCollectionFragment.arguments=bundle
                rivaLookBookActivity?.navController?.navigate(R.id.navigation_subcollection,bundle)
//                rivaLookBookActivity?.replaceFragment(subCollectionFragment)

            } else if (images[position]?.has_subcategory != null && images[position]?.has_subcategory == 1) {
                val bundle = Bundle()
                bundle.putSerializable(Constants.MODEL, images[position])
                bundle.putInt(Constants.BANNER_HEIGHT, image_Height)

                rivaLookBookActivity?.navController?.navigate(R.id.navigation_extended_category,bundle)

            } else {

                if (images[position]?.media_type != null && images[position]?.media_type.equals(
                        "V",
                        false
                    )
                ) {

                    val intent = Intent(mContext, VideoActivity::class.java)
                    intent.putExtra("video_path", images.get(position)?.media_file)
                    intent.putExtra("name", images.get(position)?.title)
                    mContext.startActivity(intent)

                } else if (images[position]?.type.equals("P", false)) {

                    if (images[position]?.has_term != null && images[position]?.has_term == 1) {

                        val intent = Intent(mContext, ProductWebActivity::class.java)
                        intent.putExtra("banner_height", 0)
                        intent.putExtra("model", images[position])
                        mContext.startActivity(intent)

                    } else {
                        val intent = Intent(mContext, OmniProductDetailsActivity::class.java)
                        intent.putExtra("id", images[position]?.type_id)
                        intent.putExtra("name", images[position]?.title)
                        intent.putExtra("image", images[position]?.image)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            imageLayout.imgBrand.transitionName = "imageproduct"
                            val options: ActivityOptionsCompat =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    (mContext as Activity),
                                    imageLayout.imgBrand,
                                    "imageproduct"
                                )
                            mContext.startActivity(intent, options.toBundle())
                        } else {
                            mContext.startActivity(intent)
                        }
                    }


                } else if (images[position]?.type.equals("H", false)) {

                    val intent = Intent(mContext, ProductWebActivity::class.java)
                    intent.putExtra("banner_height", 0)
                    intent.putExtra("model", images[position])
                    mContext.startActivity(intent)


                } else if (images[position]?.type.equals("C", false)) {
                    var intent: Intent? = null
                    if (images[position]?.has_term != null && images[position]?.has_term == 1) {
                        intent = Intent(mContext, ProductWebActivity::class.java)
                        intent.putExtra("banner_height", image_Height)
                        intent.putExtra("model", images[position])
                        mContext.startActivity(intent)
                    } else {
                        val bundle = Bundle()
                        bundle.putInt(Constants.BANNER_HEIGHT, image_Height)
                        bundle.putSerializable(Constants.MODEL, images[position])
                        rivaLookBookActivity.navController?.navigate(
                            R.id.navigation_product_listing,
                            bundle
                        )

                    }


                } else if (images[position]?.type.equals("E", false)) {
                    /*val intent = Intent(mContext, EditorialsActivity::class.java)
                    intent.putExtra("id", images[position].type_id)
                    intent.putExtra("name", images[position].title)
                    mContext.startActivity(intent)*/
                    //todo
//                    EventBus.getDefault()
//                        .post(EditorialModel(images[position].type_id, images[position].title))

                }

                ///Abdul have asked to not clickable if image type is I or G
                else if (images.get(position)?.media_type.equals(
                        "I",
                        false
                    ) || images.get(position)?.media_type.equals("G", false)
                ) {
                    if (images.get(position)?.type_id.isNullOrEmpty())
                        images.get(position)?.type_id = images.get(position)?.category_id.toString()

                    if (images.get(position)?.path.isNullOrEmpty()) {
                        if (images.get(position)?.category_path != null)
                            images.get(position)?.path = images.get(position)?.category_path
                        else images.get(position)?.path = ""
                    }

                    if (images.get(position)?.lvl.isNullOrEmpty()) {
                        if (images.get(position)?.category_level != null)
                            images.get(position)?.lvl = images.get(position)?.category_level
                        else images.get(position)?.lvl = ""
                    }


                    if (images.get(position)?.title.isNullOrEmpty())
                        images.get(position)?.title = images.get(position)?.name
                    val bundle = Bundle()
                    bundle.putInt(Constants.BANNER_HEIGHT, image_Height)
                    bundle.putSerializable(Constants.MODEL, images[position])

                    val productListingFragment= ProductListingFragment()
                    productListingFragment.arguments=bundle
//                    rivaLookBookActivity?.replaceFragment(productListingFragment)
                    rivaLookBookActivity?.navController?.navigate(R.id.navigation_product_listing,bundle)


                }
            }

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