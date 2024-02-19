package com.armada.storeapp.ui.home.riva.riva_look_book.categories.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.data.model.response.CollectionGroupsItemModel
import com.armada.storeapp.data.model.response.CollectionListItemModel
import com.armada.storeapp.data.model.response.SubCategoryResponse
import com.armada.storeapp.databinding.ChildRowBinding
import com.armada.storeapp.databinding.ParentRowBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.categories.model.ChildData
import com.armada.storeapp.ui.home.riva.riva_look_book.categories.model.ParentData
import com.armada.storeapp.ui.utils.Constants

class ExpandRecyclerviewAdapter(var mContext: Context, val list: MutableList<ParentData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onCategoryClick: ((selectedCategory: CollectionListItemModel, isParent: Boolean) -> Unit)? =
        null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Constants.PARENT) {
            val viewBinding =
                ParentRowBinding.inflate(LayoutInflater.from(mContext), parent, false)
            GroupViewHolder(viewBinding)
        } else {
            val viewBinding =
                ChildRowBinding.inflate(LayoutInflater.from(mContext), parent, false)
            ChildViewHolder(viewBinding)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val dataList = list[position]
        if (dataList.type == Constants.PARENT) {
            holder as GroupViewHolder
            holder.apply {
                parentBinding.parentTitle?.text = dataList.parentCategory?.name?.uppercase()

//                    parentBinding.downIv?.visibility = View.VISIBLE

                //                else
//                    parentBinding.downIv?.visibility = View.GONE

                parentBinding.root?.setOnClickListener {
                    if (dataList.parentCategory?.has_collection_groups!! > 0) {
                        onCategoryClick?.invoke(dataList.parentCategory, true)
                        if (dataList.isExpanded) {
                            collapseParentRow(position)

                        } else {
                            expandParentRow(position)
                        }
                    } else
                        onCategoryClick?.invoke(dataList.parentCategory, false)
                }
            }
        } else {
            holder as ChildViewHolder

            holder.apply {
                val category = dataList.subList?.first()
                var name = category?.categoryResponse?.name?.lowercase()
                childBinding.childTitle?.text = name?.replaceFirstChar { it.uppercase() }

            }
        }
    }

    private fun expandOrCollapseParentItem(singleBoarding: ParentData, position: Int) {

        if (singleBoarding.isExpanded) {
            collapseParentRow(position)
        } else {
            expandParentRow(position)
        }
    }

    private fun expandParentRow(position: Int) {
        val currentBoardingRow = list[position]
        val services = currentBoardingRow.subList
        currentBoardingRow.isExpanded = true
        var nextPosition = position
        if (currentBoardingRow.type == Constants.PARENT) {

            services?.forEach { service ->
                val parentModel = ParentData()
                parentModel.type = Constants.CHILD
                val subList: ArrayList<ChildData> = ArrayList()
                subList.add(service)
                parentModel.subList = subList
                list.add(++nextPosition, parentModel)
            }
            notifyDataSetChanged()
        }
    }

    private fun collapseParentRow(position: Int) {
        val currentBoardingRow = list[position]
        val services = currentBoardingRow.subList
        list[position].isExpanded = false
        if (list[position].type == Constants.PARENT) {
            services?.forEach { _ ->
                list.removeAt(position + 1)
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int = list[position].type

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

//    class GroupViewHolder(row: View) : RecyclerView.ViewHolder(row) {
//        val parentTV = row.findViewById(R.id.parent_Title) as TextView?
//        val downIV  = row.findViewById(R.id.down_iv) as ImageView?
//    }
//    class ChildViewHolder(row: View) : RecyclerView.ViewHolder(row) {
//        val childTV = row.findViewById(R.id.child_Title) as TextView?
//
//    }

    class GroupViewHolder(val parentBinding: ParentRowBinding) :
        RecyclerView.ViewHolder(parentBinding.root)

    class ChildViewHolder(val childBinding: ChildRowBinding) :
        RecyclerView.ViewHolder(childBinding.root)
}