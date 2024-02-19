package com.armada.riva.Category.Algolia

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.CollectionListItemModel
import com.armada.storeapp.data.model.response.SubCategoryResponse
import com.armada.storeapp.databinding.CategoriesItemBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.riva_login.RivaLoginActivity
import com.armada.storeapp.ui.utils.Utils

/**
 * Created by User999 on 7/13/2018.
 */
class CategoriesAdapter(
    private val dataList: ArrayList<CollectionListItemModel>,
    private val context: Activity?,
    private val image_Height: Int,
    private val image_Width: Int,
    private val image_Margin: Int,
    private val top_margin: Int,
    private val bottom_margin: Int,
    private val collectionTitle: String,
    private val collectionSubTitle: String,
    private val hide_Underline: String
) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //println("Here i am loading adapter  " +  dataList[0])
        val relParams =
            RelativeLayout.LayoutParams(image_Width, ViewGroup.LayoutParams.MATCH_PARENT)
        val lnrParams = LinearLayout.LayoutParams(image_Width, image_Height)

        holder.categoriesItemBinding.imgBrand.layoutParams = lnrParams


        if (position == dataList.size - 1) {
            //  small_param1_TOp.addRule(RelativeLayout.CENTER_HORIZONTAL)
            relParams.setMargins(image_Margin, top_margin, image_Margin, bottom_margin)
            holder.categoriesItemBinding.relTop.layoutParams = relParams
        } else {

            relParams.setMargins(image_Margin, top_margin, 0, bottom_margin)
            holder.categoriesItemBinding.relTop.layoutParams = relParams


        }

        if (collectionTitle == "1" || dataList[position].name.equals("")) {
            holder.categoriesItemBinding.txtTitle.visibility = View.GONE
        } else {
            holder.categoriesItemBinding.txtTitle.visibility = View.VISIBLE
            // holder.itemView.txtTitle.text = dataList[position].name
            //println("Here i am text 222 " + dataList[position].sub_title)
            holder.categoriesItemBinding.txtTitle.text =
                Utils.getDynamicStringFromApi(context as Context, dataList[position].name)
        }
        //println("CategoriesAdapter name: "+dataList[position].name)
        if (hide_Underline.equals("0")) {
            holder.categoriesItemBinding.txtTitle.paintFlags =
                holder.categoriesItemBinding.txtTitle.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            holder.categoriesItemBinding.txtSubtitle.paintFlags =
                holder.categoriesItemBinding.txtSubtitle.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }

        if (collectionSubTitle.equals("1") || dataList[position].sub_title.equals("")) {
            holder.categoriesItemBinding.txtSubtitle.visibility = View.GONE
        } else {
            //println("Here i am text 111 "  + dataList[position].sub_title)
            holder.categoriesItemBinding.txtSubtitle.text =
                Utils.getDynamicStringFromApi(context as Context, dataList[position].sub_title)
            holder.categoriesItemBinding.txtSubtitle.visibility = View.VISIBLE
        }

        Utils.loadImagesUsingCoil(
            context as Activity,
            dataList[position].image!!,
            holder.categoriesItemBinding.imgBrand
        )
        //Glide.with(context as Activity).load(dataList.get(position).image!!).into( holder.itemView.imgBrand)

        holder.categoriesItemBinding.lnrMain.setOnClickListener {

            if (dataList[position].has_collection_groups == 1) {
//                dataList[position].name?.let { it1 -> Global.itemCategrySub(it1) }

                val bundle = Bundle()
                bundle.putString("id", dataList[position].id.toString())
                bundle.putString("name", dataList[position].name)
                bundle.putBoolean("from_home", false)
                if(context is RivaLookBookActivity){
                    (context as RivaLookBookActivity)?.navController?.navigate(R.id.navigation_subcollection,bundle)
                }

            } else {
//                dataList[position].name?.let { it1 -> Global.itemCategryMain(it1) }
                dataList[position].path = dataList[position].category_path
                dataList[position].lvl = dataList[position].category_level
                dataList[position].type_id = dataList[position].category_id.toString()
                dataList[position].title = dataList[position].name
                val bundle=Bundle()
                bundle.putSerializable("model",dataList[position])
                if(context is RivaLookBookActivity){
                    (context as RivaLookBookActivity).navController?.navigate(R.id.navigation_product_listing,bundle)
                }
                /* val intent = Intent(context, ProductListingActivity::class.java)
                 intent.putExtra("banner_height", image_Height)
                 intent.putExtra("model", dataList[position])
                 context!!.startActivity(intent)*/
//                EventBus.getDefault().post(CategoryCollectionData(image_Height, dataList[position]))
        }
        }

    }

    override fun getItemCount(): Int = dataList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CategoriesItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    class ViewHolder(val categoriesItemBinding: CategoriesItemBinding) :
        RecyclerView.ViewHolder(categoriesItemBinding.root) {

//        fun bind(
//            dataList: List<BannerCollectionResponse.Data.CollectionGroup.Collection>,
//            context: Activity?,
//            image_Width: Int,
//            image_Height: Int,
//            image_Margin: Int
//        ) {
//
//        }
    }

}