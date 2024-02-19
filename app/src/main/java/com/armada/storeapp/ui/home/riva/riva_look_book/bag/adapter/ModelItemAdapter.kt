package com.armada.storeapp.ui.home.riva.riva_look_book.bag.adapter

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.AddToCartResponse
import com.armada.storeapp.data.model.response.SearchEngineData
import com.armada.storeapp.data.model.response.SearchModelResponse
import com.armada.storeapp.data.model.response.SkuMasterTypes
import com.armada.storeapp.databinding.LayoutModelItemBinding
import com.armada.storeapp.databinding.TestLayoutBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SwipeRevealLayout.ViewBinderHelper
import com.armada.storeapp.ui.utils.Utils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_bag_item.view.*
import java.util.*
import kotlin.collections.HashMap

class ModelItemAdapter(
    private val activity: FragmentActivity,
    var modelItemList: ArrayList<SearchEngineData>?
) : RecyclerView.Adapter<ModelItemAdapter.ItemHolder>() {

    var onItemClicked: ((SearchEngineData) -> Unit)? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemHolder {
        return ItemHolder(
            LayoutModelItemBinding.inflate(
                LayoutInflater.from(activity),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(
        holder: ItemHolder,
        position: Int
    ) {

        val model = modelItemList?.get(position)

        holder.modelItemBinding.tvCode.text = model?.code
        holder.modelItemBinding.tvName.text = model?.name
        holder.modelItemBinding.tvBarcode.text ="-" +model?.barCode

        holder.modelItemBinding.root.setOnClickListener {
            onItemClicked?.invoke(model!!)
        }
    }

    override fun getItemCount(): Int {
        return if (modelItemList == null) 0 else modelItemList!!.size
    }

    class ItemHolder(val modelItemBinding: LayoutModelItemBinding) :
        RecyclerView.ViewHolder(modelItemBinding.root)
}