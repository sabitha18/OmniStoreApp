package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_filter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.PicklistResponseModel
import com.armada.storeapp.databinding.LayoutItemSortBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.product_listing.ProductFilterActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.model.ProductListMSortingModel


class SortAdapter(
    val activity: ProductFilterActivity,
    private val arrListSortData: ArrayList<ProductListMSortingModel>
) : RecyclerView.Adapter<SortAdapter.SortViewHolder>() {

    private var selectedPosition = -1
    var onSortSelected: ((ProductListMSortingModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortViewHolder {
        return SortViewHolder(
            LayoutItemSortBinding.inflate(LayoutInflater.from(activity), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrListSortData.size
    }

    override fun onBindViewHolder(holder: SortViewHolder, position: Int) {
        val sort = arrListSortData.get(position)

        if (sort?.isSelected!!) {
            holder.binding.root.setBackgroundColor(activity.resources.getColor(com.armada.storeapp.R.color.black))
            holder.binding.txtSort.setTextColor(activity.resources.getColor(com.armada.storeapp.R.color.white))
            holder.itemView.isSelected = true
            selectedPosition = holder.adapterPosition
        } else {
            holder.itemView.isSelected = false
            holder.binding.root.background =
                activity.resources.getDrawable(com.armada.storeapp.R.drawable.rectangle_black)
            holder.binding.txtSort.setTextColor(activity.resources.getColor(com.armada.storeapp.R.color.black))
        }

        if (selectedPosition === position) {
            holder.itemView.isSelected = true //using selector drawable
            holder.binding.root.setBackgroundColor(activity.resources.getColor(com.armada.storeapp.R.color.black))
            holder.binding.txtSort.setTextColor(activity.resources.getColor(com.armada.storeapp.R.color.white))
        } else {
            holder.itemView.isSelected = false
            holder.binding.root.background =
                activity.resources.getDrawable(com.armada.storeapp.R.drawable.rectangle_black)
            holder.binding.txtSort.setTextColor(activity.resources.getColor(com.armada.storeapp.R.color.black))
        }


        holder.itemView.setOnClickListener { v ->
            if (selectedPosition >= 0) notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            sort.isSelected = !sort.isSelected!!
            arrListSortData.forEach {
                if (sort.isSelected!! && sort.sort_name.equals(it.sort_name)){

                }else{
                    it.isSelected=false
                }
            }
            notifyDataSetChanged()
            if (sort.isSelected!!)
                onSortSelected?.invoke(sort)
        }
        holder.binding.txtSort.text = sort.sort_name

    }

    class SortViewHolder(val binding: LayoutItemSortBinding) :
        RecyclerView.ViewHolder(binding.root)


}