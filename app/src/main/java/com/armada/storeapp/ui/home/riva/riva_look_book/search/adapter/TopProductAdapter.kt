package com.armada.storeapp.ui.home.riva.riva_look_book.search.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.CollectionListItemModel
import com.armada.storeapp.data.model.response.HomeDataModel
import com.armada.storeapp.data.model.response.PatternsProduct
import com.armada.storeapp.data.model.response.TimeLineModel
import com.armada.storeapp.databinding.ItemSearchBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.Utils

class TopProductAdapter(
    val searchList: ArrayList<HomeDataModel.PopularTopScroll>,
    private val rivaLookBookActivity: RivaLookBookActivity
) : androidx.recyclerview.widget.RecyclerView.Adapter<TopProductAdapter.MyViewHolder>() {

    private var strSearchString: String = ""

    private var amazingPrice = 0.0
    var colorFinalPrice = 0
    var colorRegularPrice = 0
    var colrAmazingPrice = 0
    var productWidth = 0
    var productheight = 0
    var typeSemibold: Typeface? = null
    var typeNormal: Typeface? = null
    var typeMedium: Typeface? = null

    init {
        amazingPrice = Utils.getAmazingPrice()
        colorFinalPrice = ContextCompat.getColor(rivaLookBookActivity, R.color.black)
        colorRegularPrice = ContextCompat.getColor(rivaLookBookActivity, R.color.regular_price)
        colrAmazingPrice = ContextCompat.getColor(rivaLookBookActivity, R.color.final_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemSearchBinding.inflate(
                LayoutInflater.from(rivaLookBookActivity),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val searchItem = searchList.get(position)

        val small_param = RelativeLayout.LayoutParams(productWidth, productheight)
        val small_pa1 =
            RelativeLayout.LayoutParams(productWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        holder.binding.relText.visibility = View.GONE
        ////////////////////////////////////////////////////////////////////////////////////////

        if (rivaLookBookActivity != null) {
            holder.binding.imgProduct.load(if (searchItem.image != null && searchItem.image != "") searchItem.image else Constants.strNoImage) {
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
                        holder.binding.spin.visibility = View.GONE
                    }
                })
            }

            holder.binding.rltmain.layoutParams = small_pa1
            holder.binding.imgProduct.layoutParams = small_param
            holder.binding.relImage.layoutParams = small_param

            small_pa1.rightMargin = rivaLookBookActivity?.resources?.getDimension(R.dimen.ten_dp)?.toInt() ?: 0


            val param = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, rivaLookBookActivity?.resources?.getDimension(
                    R.dimen.thirty_dp
                )?.toInt() ?: 0
            )
            param.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.imgProduct)
            param.bottomMargin = rivaLookBookActivity?.resources?.getDimension(R.dimen.ten_dp)?.toInt() ?: 0

            holder.binding.relSold.setPadding(
                rivaLookBookActivity?.resources?.getDimension(R.dimen.seven_dp)?.toInt() ?: 0,
                rivaLookBookActivity?.resources?.getDimension(R.dimen.eight_dp)?.toInt() ?: 0,
                rivaLookBookActivity?.resources?.getDimension(R.dimen.eight_dp)?.toInt() ?: 0,
                rivaLookBookActivity?.resources?.getDimension(R.dimen.eight_dp)?.toInt() ?: 0
            )

            param.addRule(RelativeLayout.ALIGN_PARENT_LEFT)

            holder.binding.relSold.layoutParams = param

            holder.binding.txtProductName.text = (searchItem.name)

            holder.binding.txtRegularPrice.paintFlags =
                holder.binding.txtRegularPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            holder.binding.txtDiscount.alpha = 0.8.toFloat()

            holder.binding.txtSale.visibility = View.GONE
            holder.binding.relSold.visibility = View.GONE
            holder.binding.relSold.alpha = 0.7.toFloat()

            holder.binding.rltmain.setOnClickListener {

//                val intent = Intent(rivaLookBookActivity, OmniProductDetailsActivity::class.java)
//                intent.putExtra("id", searchItem.type_id.toString())
//                intent.putExtra("cat_id", "-1")
//                intent.putExtra("name", searchItem.name)
//                intent.putExtra("image", searchItem.image)
//                rivaLookBookActivity!!.startActivity(intent)

                if (searchItem.type.equals("C")) {

                    val arrList = ArrayList<String>()
                    val arrListCollection = java.util.ArrayList<CollectionListItemModel>() //Dummy
                    val arListPattrn = ArrayList<PatternsProduct>()  /// Dummy
                    val arrListTimeline = java.util.ArrayList<TimeLineModel>() //Dummy

                    val model = CollectionListItemModel("", "", "", searchItem.type_id, searchItem.name, "", searchItem.type_id.toString(), searchItem.path, searchItem.lvl, "0",
                        "", "", "", "", "", "", searchItem.name, searchItem.type_id, searchItem.path, searchItem.lvl, 0,
                        0, "", arListPattrn as ArrayList<PatternsProduct?>, 0, "", arrListCollection as ArrayList<CollectionListItemModel?>, 0, arrList, 0, 0)
                    val bundle = Bundle()
                    bundle.putInt("banner_height", 0)
                    bundle.putSerializable("model", model)
                    bundle.putString("strHead", searchItem.name)
                    rivaLookBookActivity.navController?.navigate(R.id.navigation_product_listing,bundle)

                } else if (searchItem.type.equals("P")) {

                    val intent = Intent(rivaLookBookActivity, OmniProductDetailsActivity::class.java)
                    intent.putExtra("id", searchItem.type_id.toString())
                    intent.putExtra("cat_id", "-1")
                    intent.putExtra("name", searchItem.name)
                    rivaLookBookActivity!!.startActivity(intent)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return if (searchList.size > 10) 10 else searchList.size
    }

    class MyViewHolder(val binding: ItemSearchBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)
}
