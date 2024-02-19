package com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.armada.riva.HOME.ProductWebActivity
import com.armada.riva.HOME.VideoActivity
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.CollectionListItemModel
import com.armada.storeapp.databinding.HomeViewImageRowBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.home.ExtendedCategoryFragment
import com.armada.storeapp.ui.home.riva.riva_look_book.home.SubCollectionFragment
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.ProductListingFragment
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.Utils
import java.util.*

class HomeDataAdapter(
    private val selectedCurrency: String,
    private val dataList: ArrayList<CollectionListItemModel>,
    private val context: Activity?,
    private val fragment: SubCollectionFragment?,
    private val image_Height: Int,
    private val image_Width: Int,
    private val image_Margin: Int,
    private val top_margin: Int,
    private val bottom_margin: Int,
    private val collectionTitle: Int,
    private val collectionSubTitle: Int,
    private
    val is_timeline: String,
    private var group_id: Int,
    var arrlBadge: ArrayList<Int>?,
    var fromHome: Boolean,
    private val rivaLookBookActivity: RivaLookBookActivity
) : androidx.recyclerview.widget.RecyclerView.Adapter<HomeDataAdapter.ViewHolder>() {

    private var amazingPrice = 0.0
    var colorFinalPrice = 0
    var colorRegularPrice = 0
    var colorAmazingPrice = 0

    //var colorSecondary = 0
    private var tenDpMargin = 0
    private var fiveDpMargin = 5

    init {
        amazingPrice = Utils.getAmazingPrice()
        colorFinalPrice = ContextCompat.getColor(context!!, R.color.brown_text_color) ?: 0
        colorRegularPrice = ContextCompat.getColor(context, R.color.regular_price) ?: 0
        colorAmazingPrice = ContextCompat.getColor(context, R.color.final_price) ?: 0
        //colorSecondary = ContextCompat.getColor(context, R.color.secondary_text) ?: 0
        tenDpMargin = context.resources?.getDimension(R.dimen.ten_dp)!!.toInt()
        fiveDpMargin = context.resources.getDimension(R.dimen.five_dp).toInt()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //println("Here i am setting category collection 444  " + dataList[position]?.name)
        val lpRel = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        if (collectionTitle == 0 || collectionSubTitle == 0) {
            //means any one is visible - give margin bottom of text, next item will have some gap from top
            lpRel.setMargins(0, 0, 0, tenDpMargin)
            holder.homeViewImageRowBinding.relTop.layoutParams = lpRel
        }

        var imgWidth = image_Width * dataList.size
        imgWidth = if (imgWidth < 315 && imgWidth > 325) {
            320 / dataList.size
        } else {
            image_Width
        }

        val lpImage = LinearLayout.LayoutParams(imgWidth, image_Height)

        val lpText = LinearLayout.LayoutParams(
            image_Width, context?.resources?.getDimension(R.dimen.fifteen_dp)?.toInt()
                ?: 0
        )
        val lpLinearTextContainer =
            LinearLayout.LayoutParams(image_Width, LinearLayout.LayoutParams.WRAP_CONTENT)
        //lpLinearTextContainer.addRule(RelativeLayout.BELOW, R.id.imgBrand)
        holder.homeViewImageRowBinding.linText.layoutParams = lpLinearTextContainer

        holder.homeViewImageRowBinding.imgBrand.visibility = View.VISIBLE
        holder.homeViewImageRowBinding.myVideo.visibility = View.GONE

        if (dataList[position].media_type.equals("I", true)) {

            if (dataList[position].image != null && !dataList[position].image.equals("")) {
                Utils.loadImagesUsingCoil(
                    context as Activity,
                    dataList[position].image,
                    holder.homeViewImageRowBinding.imgBrand
                )
            } else {
                Utils.loadGifUsingCoil(
                    context as Activity,
                    dataList[position].media_file,
                    holder.homeViewImageRowBinding.imgBrand
                )
            }

        } else if (dataList[position].media_type.equals("V", true)) {

            Utils.loadImagesUsingCoil(
                context as Activity,
                if (dataList[position].media_thumbnail != null && dataList[position].media_thumbnail != "") dataList[position].media_thumbnail else Constants.strNoImage,
                holder.homeViewImageRowBinding.imgBrand
            )
            holder.homeViewImageRowBinding.imgBrand.visibility = View.GONE

            holder.homeViewImageRowBinding.myVideo.setBackgroundColor(
                ContextCompat.getColor(
                    context as Context,
                    R.color.white
                )
            )
            holder.homeViewImageRowBinding.myVideo.visibility = View.VISIBLE
            if (dataList[position].stopVideo == 0) {
                holder.homeViewImageRowBinding.myVideo.setVideoPath(
                    Uri.parse(dataList[position].media_file).toString()
                )
                holder.homeViewImageRowBinding.myVideo.start()
            } else {
                holder.homeViewImageRowBinding.myVideo.stopPlayback()
            }
        } else {

            if (dataList[position].media_file != null && dataList[position].media_file != "") {
                Utils.loadGifUsingCoil(
                    context as Activity,
                    dataList[position].media_file,
                    holder.homeViewImageRowBinding.imgBrand
                )
            } else {
                Utils.loadImagesUsingCoil(
                    context as Activity,
                    if (dataList[position].image != null && !dataList[position].image.equals("")) dataList[position].image else Constants.strNoImage,
                    holder.homeViewImageRowBinding.imgBrand
                )
            }
        }

        if (is_timeline == "1" && (arrlBadge != null && arrlBadge?.size ?: 0 > 0 && arrlBadge?.get(
                position
            ) == 0)
        ) {
            //println("Image width: " + image_Width)
            //println("Image height: " + image_Height)
            if (position == 0) {
                val relTextParam = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                relTextParam.addRule(RelativeLayout.ALIGN_PARENT_TOP)

                relTextParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                relTextParam.setMargins(tenDpMargin, fiveDpMargin, fiveDpMargin, fiveDpMargin)


                holder.homeViewImageRowBinding.txtNewBadge.layoutParams = relTextParam
            }
            holder.homeViewImageRowBinding.txtNewBadge.visibility = View.VISIBLE

        } else {
            holder.homeViewImageRowBinding.txtNewBadge.visibility = View.GONE
        }

        ///
        if (position == 0) {
            //this will show margin in both ends
            lpImage.setMargins(image_Margin, 0, image_Margin, 0)
            lpText.setMargins(
                image_Margin, context.resources?.getDimension(R.dimen.five_dp)?.toInt()
                    ?: 0, image_Margin, 0
            )

        } else {
            //margin only in right side, so margin in between items will not become double
            lpImage.setMargins(0, 0, image_Margin, 0)
            lpText.setMargins(
                0, context.resources?.getDimension(R.dimen.five_dp)?.toInt()
                    ?: 0, image_Margin, 0
            )
        }
        holder.homeViewImageRowBinding.myVideo.layoutParams = lpImage
        holder.homeViewImageRowBinding.imgBrand.layoutParams = lpImage
//        holder.homeViewImageRowBinding.txtTitle.layoutParams = lpText
//        holder.homeViewImageRowBinding.txtSubtitle.layoutParams = lpText

//        holder.homeViewImageRowBinding.txtTitle.setPadding(
//            context.resources?.getDimension(R.dimen.five_dp)?.toInt()
//                ?: 0, 0, context.resources?.getDimension(R.dimen.five_dp)?.toInt() ?: 0, 0
//        )
//        holder.homeViewImageRowBinding.txtSubtitle.setPadding(
//            context.resources?.getDimension(R.dimen.five_dp)?.toInt()
//                ?: 0, 0, context.resources?.getDimension(R.dimen.five_dp)?.toInt() ?: 0, 0
//        )


        if (collectionTitle == 1 || dataList[position].title.equals("")) {
            holder.homeViewImageRowBinding.txtTitle.visibility = View.GONE
        } else {
            holder.homeViewImageRowBinding.txtTitle.visibility = View.VISIBLE
            holder.homeViewImageRowBinding.txtTitle.text =
                Utils.getDynamicStringFromApi(context, dataList[position].title)
        }

        //println("Here i am title text is " + dataList.get(position).title)
        if (collectionSubTitle == 1 || dataList[position].sub_title.equals("")) {
            holder.homeViewImageRowBinding.txtSubtitle.visibility = View.GONE
            holder.homeViewImageRowBinding.lnrPrice.visibility = View.GONE

        } else {

            if (!dataList[position].regular_price.isNullOrBlank() && !dataList[position].final_price.isNullOrBlank()) {

                holder.homeViewImageRowBinding.txtRegularPrice.text =
                    Utils.getPriceFormatted(dataList[position].regular_price.toString(),selectedCurrency)
                holder.homeViewImageRowBinding.txtFinalPrice.text =
                    Utils.getPriceFormatted(dataList[position].final_price,selectedCurrency)
                holder.homeViewImageRowBinding.lnrPrice.visibility = View.VISIBLE
                holder.homeViewImageRowBinding.txtSubtitle.visibility = View.GONE


                if (!dataList[position].regular_price.equals(dataList.get(position).final_price)) {

                    if (dataList[position].final_price != null && (dataList[position].final_price?.isNotEmpty() == true) && (dataList.get(
                            position
                        ).final_price?.toDouble() ?: 0.0 <= amazingPrice)
                    ) {
                        holder.homeViewImageRowBinding.txtFinalPrice.setTextColor(colorAmazingPrice)
                    } else {
                        holder.homeViewImageRowBinding.txtFinalPrice.setTextColor(colorFinalPrice)
                    }


                    if (dataList[position].regular_price != null && (dataList.get(position).regular_price?.isNotEmpty() == true) && (dataList[position].regular_price?.toDouble() ?: 0.0 <= amazingPrice)) {
                        holder.homeViewImageRowBinding.txtRegularPrice.setTextColor(
                            colorRegularPrice
                        )
                    } else {
                        holder.homeViewImageRowBinding.txtRegularPrice.setTextColor(
                            colorRegularPrice
                        )
                    }

                    holder.homeViewImageRowBinding.txtDiscountPrice.visibility = View.VISIBLE
                    holder.homeViewImageRowBinding.txtRegularPrice.visibility = View.VISIBLE

                    val discount = 100 - Math.ceil(
                        (java.lang.Float.parseFloat(dataList[position].final_price!!) / java.lang.Float.parseFloat(
                            dataList[position].regular_price!!
                        ) * 100).toDouble()
                    )
                    holder.homeViewImageRowBinding.txtDiscountPrice.text =
                        "(" + String.format(Locale.ENGLISH, "%.0f", discount) + "%)"
                } else {

                    holder.homeViewImageRowBinding.txtRegularPrice.visibility = View.GONE
                    holder.homeViewImageRowBinding.txtDiscountPrice.visibility = View.GONE

                    if (dataList[position].final_price != null && (dataList[position].final_price?.isNotEmpty() == true) && (dataList[position].final_price?.toDouble() ?: 0.0 <= amazingPrice)) {
                        holder.homeViewImageRowBinding.txtFinalPrice.setTextColor(colorAmazingPrice)
                    } else {
                        holder.homeViewImageRowBinding.txtFinalPrice.setTextColor(colorFinalPrice)
                    }

                }

                if (dataList[position].final_price?.isEmpty() == true)
                    holder.homeViewImageRowBinding.txtFinalPrice.visibility = View.GONE

                if (dataList[position].regular_price?.isEmpty() == true)
                    holder.homeViewImageRowBinding.txtRegularPrice.visibility = View.GONE

            } else {

                holder.homeViewImageRowBinding.txtSubtitle.text =
                    Utils.getDynamicStringFromApi(context, dataList[position].sub_title)
                holder.homeViewImageRowBinding.lnrPrice.visibility = View.GONE
                holder.homeViewImageRowBinding.txtSubtitle.visibility = View.VISIBLE

            }
        }
        holder.homeViewImageRowBinding.txtRegularPrice.paintFlags =
            holder.homeViewImageRowBinding.txtRegularPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


        holder.homeViewImageRowBinding.lnrMain.setOnClickListener {
            //println("Here i am setting category collection click 111  " + dataList[position]?.name)
            if (dataList[position].has_collection_groups == 1) {
                val bundle = Bundle()
                bundle.putInt("id", dataList.get(position).id!!)
                bundle.putString("name", dataList.get(position).title)
                bundle.putBoolean("from_home", fromHome)
                val subCollectionFragment = SubCollectionFragment()
                subCollectionFragment.arguments = bundle
//                rivaLookBookActivity?.replaceFragment(subCollectionFragment)
                rivaLookBookActivity?.navController?.navigate(R.id.navigation_subcollection, bundle)
            }
//            else if (is_timeline == "1") {
//                //println("Here i am setting category collection click 333  " + dataList[position]?.name)
//                ////Adding timeline which means this timeline viewed by user
//                //helper.insertTimelines(group_id,dataList[position].id!!.toInt())
//                globalClass.insertUpdateTimeline(helper, group_id, dataList[position].id!!.toInt())
//                holder.itemView.txtNewBadge.visibility = View.GONE
//                //arrlBadge.add(position,1)
//
//                AppController.instance.arrListTmLineBadge[position] = 1
//                val intent = Intent(context, TimeStoryFragmentActivity::class.java)
//                intent.putExtra("arrList", dataList)
//                intent.putExtra("post", position)
//                intent.putExtra("group_id", group_id)
//                context.startActivity(intent)
//
//                globalClass.homeEvents(group_id.toString(), "Collection Group", collectionTitle)
//
//                globalClass.homeEvents(group_id.toString(), "Time Line", collectionTitle)
//
//            }
            else if (dataList[position].has_subcategory != null && dataList[position].has_subcategory == 1) {
                val bundle = Bundle()
                bundle.putSerializable(Constants.MODEL, dataList[position])
                rivaLookBookActivity?.navController?.navigate(
                    R.id.navigation_extended_category,
                    bundle
                )
            } else {
                //println("Here i am setting category collection click 555  " + dataList[position]?.name)
                if (dataList[position].type.equals("P", true)) {
                    //println("Here i am setting category collection click 666  " + dataList[position]?.name)
                    if (dataList[position].has_term != null && dataList[position].has_term == 1) {
                        val intent = Intent(context, ProductWebActivity::class.java)
                        intent.putExtra("banner_height", 0)
                        intent.putExtra("model", dataList.get(position))
                        context.startActivity(intent)

                    } else {
                        val intent = Intent(context, OmniProductDetailsActivity::class.java)
                        intent.putExtra("id", dataList[position].type_id)
                        intent.putExtra("name", dataList[position].title)
                        intent.putExtra("image", dataList.get(position).image)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.homeViewImageRowBinding.imgBrand.transitionName = "imageproduct"
                            val options: ActivityOptionsCompat =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    context,
                                    holder.homeViewImageRowBinding.imgBrand,
                                    "imageproduct"
                                )
                            context.startActivity(intent, options.toBundle())
                        } else {
                            context.startActivity(intent)
                        }

                    }

                } else if (dataList[position].type.equals("H", false)) {
                    //println("Here i am setting category collection click 777  " + dataList[position]?.name)
                    val intent = Intent(context, ProductWebActivity::class.java)
                    intent.putExtra("banner_height", 0)
                    intent.putExtra("model", dataList[position])
                    context.startActivity(intent)
                } else if (dataList[position].type.equals("C", true)) {


                    if (dataList[position].has_term != null && dataList.get(position).has_term == 1) {
                        val intent = Intent(context, ProductWebActivity::class.java)
                        intent.putExtra("banner_height", image_Height)
                        intent.putExtra("model", dataList[position])
                        context.startActivity(intent)

                    } else if (dataList[position].path!!.isNotEmpty()) {
                        val bundle = Bundle()
                        bundle.putInt(Constants.BANNER_HEIGHT, image_Height)
                        bundle.putSerializable(Constants.MODEL, dataList[position])
                        rivaLookBookActivity?.navController?.navigate(
                            R.id.navigation_product_listing,
                            bundle
                        )
                    }


                } else if (dataList[position].media_type != null && dataList[position].media_type.equals(
                        "V",
                        false
                    )
                ) {
                    val intent = Intent(context, VideoActivity::class.java)
                    intent.putExtra("video_path", dataList[position].media_file)
                    intent.putExtra("name", dataList[position].title)
                    context.startActivity(intent)

                } else if (dataList.get(position).type.equals("E", false)) {

                    val bundle = Bundle()
                    bundle.putString("id", dataList[position].type_id)
                    bundle.putString("name", dataList[position].title)
                    rivaLookBookActivity?.navController?.navigate(R.id.navigation_editorial, bundle)

                } else if (dataList[position].media_type != null && dataList[position].media_type.equals(
                        "I",
                        false
                    ) || dataList[position].media_type != null && dataList[position].media_type.equals(
                        "G",
                        false
                    )
                ) {
                    //println("Here i am setting category collection click bbb  " + dataList[position]?.name)
                    ////Abdul have asked to not clickable if image type is I or G
                    /// Coming from category
                    if (!fromHome) {

                        if (dataList[position].category_id.toString() != null && dataList[position].category_id.toString()
                                .isNotEmpty()
                        )
                            dataList[position].type_id = dataList[position].category_id.toString()

                        if (dataList[position].category_path != null && (dataList[position].category_path?.isEmpty() == false))
                            dataList[position].path = dataList.get(position).category_path

                        if (dataList[position].category_level != null && (dataList[position].category_level?.isEmpty() == false))
                            dataList[position].lvl = dataList.get(position).category_level

                        if (dataList[position].name != null && (dataList[position].name?.isEmpty() == false))
                            dataList[position].title = dataList[position].name

                        val bundle = Bundle()
                        bundle.putInt(Constants.BANNER_HEIGHT, image_Height)
                        bundle.putSerializable(Constants.MODEL, dataList[position])
                        rivaLookBookActivity?.navController?.navigate(
                            R.id.navigation_product_listing,
                            bundle
                        )
                    }

                    if (fragment != null) {
                        val bundle = Bundle()
                        bundle.putInt("banner_height", image_Height)
                        bundle.putSerializable("model", dataList[position])
                        rivaLookBookActivity?.navController?.navigate(
                            R.id.navigation_product_listing,
                            bundle
                        )
                    }
                } else {

                }
            }
        }

    }

    override fun getItemCount(): Int = dataList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            HomeViewImageRowBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    class ViewHolder(val homeViewImageRowBinding: HomeViewImageRowBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(homeViewImageRowBinding.root) {

    }

    fun updateBadeList(arrlBadge: ArrayList<Int>) {
        this.arrlBadge = arrlBadge
        notifyDataSetChanged()
    }


    override fun getItemId(position: Int): Long {
        return dataList[position].title.hashCode().toLong()
    }
}
