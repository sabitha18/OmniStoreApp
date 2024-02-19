package com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.armada.storeapp.data.model.response.ParentCategoryResponse;
import com.armada.storeapp.ui.home.riva.riva_look_book.home.BannerFragment;
import com.armada.storeapp.ui.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<String> arrListTitle;
    private ArrayList<ParentCategoryResponse.Data> arrListCategories;
    private String collectionId;

    public ViewPagerAdapter( FragmentManager manager, ArrayList<ParentCategoryResponse.Data> arrListCategories) {
        super(manager);
        this.arrListCategories = arrListCategories;
        this.arrListTitle = new ArrayList<>();
        this.collectionId = collectionId;
        for (ParentCategoryResponse.Data categories : this.arrListCategories) {
            arrListTitle.add(categories.getName_en());
        }
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        BannerFragment fragment = new BannerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(
                "parent_category_id",
                arrListCategories.get(position).getParent_category_id()
        );
        fragment.setArguments(bundle);
        return fragment;

    }

    @Override
    public int getCount() {
        return arrListTitle.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return arrListTitle.get(position);
    }


}
