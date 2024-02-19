package com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.armada.storeapp.data.model.response.ParentCategoryResponse
import com.armada.storeapp.ui.home.riva.riva_look_book.home.BannerFragment
import com.armada.storeapp.ui.utils.Constants

class RivaHomeStatePagerAdapter(
    fragmentActivity: FragmentActivity,
    val parentCategories: ArrayList<ParentCategoryResponse.Data>
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return parentCategories.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = BannerFragment()
        val args = Bundle()
        args.putInt(
            Constants.PARENT_CATEGORY_ID,
            parentCategories.get(position).parent_category_id!!
        )
        fragment.arguments = args
        return fragment
    }
}
