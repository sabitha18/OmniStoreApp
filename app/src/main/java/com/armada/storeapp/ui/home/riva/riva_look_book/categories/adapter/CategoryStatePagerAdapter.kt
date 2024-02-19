package com.armada.storeapp.ui.home.riva.riva_look_book.categories.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.armada.storeapp.data.model.response.ParentCategoryResponse
import com.armada.storeapp.ui.home.riva.riva_look_book.categories.SubCateogryFragment

class CategoryStatePagerAdapter(
    fragmentActivity: FragmentActivity,
    val parentCategories: ArrayList<ParentCategoryResponse.Data>
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return parentCategories.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = SubCateogryFragment()
        val args = Bundle()
        args.putInt("parent_category_id", parentCategories.get(position).parent_category_id!!)
        fragment.arguments = args
        return fragment
    }
}
