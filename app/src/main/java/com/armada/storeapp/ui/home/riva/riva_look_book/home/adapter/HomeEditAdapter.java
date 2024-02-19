package com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.armada.storeapp.R;
import com.armada.storeapp.data.model.response.CollectionGroupsItemModel;
import com.armada.storeapp.data.model.response.CollectionListItemModel;
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity;
import com.armada.storeapp.ui.home.riva.riva_look_book.home.Algolia.ExpandableRecyler.ChildViewHolder;
import com.armada.storeapp.ui.home.riva.riva_look_book.home.Algolia.ExpandableRecyler.ExpandableRecyclerAdapter;
import com.armada.storeapp.ui.home.riva.riva_look_book.home.Algolia.ExpandableRecyler.ParentViewHolder;
import com.armada.storeapp.ui.home.riva.riva_look_book.home.EditorialFragment;
import com.armada.storeapp.ui.home.riva.riva_look_book.home.model.EditorialModel;
import com.armada.storeapp.ui.home.riva.riva_look_book.home.model.HomeEditModel;
import com.armada.storeapp.ui.utils.Utils;

import java.util.ArrayList;

import okhttp3.internal.Util;


public class HomeEditAdapter extends ExpandableRecyclerAdapter<HomeEditModel, CollectionListItemModel, HomeEditAdapter.CatViewHolder, HomeEditAdapter.SubCatViewHolder> {

    private static final int PARENT_NORMAL = 1;
    private static final int CHILD_NORMAL = 2;
    private LayoutInflater mInflater;
    private RivaLookBookActivity rivaLookBookActivity;
    private ArrayList<HomeEditModel> catArrList;
    private Typeface typeSemibold, typeNormal;
    private Context context;
    private CollectionGroupsItemModel collection;
    private int image_Height, top_margin, bottom_margin, banner_height;

    public HomeEditAdapter(RivaLookBookActivity activity, ArrayList<HomeEditModel> catArrList, int image_Height, int top_margin, int bottom_margin, int banner_height, CollectionGroupsItemModel collection) {
        super(catArrList);
        this.context = context;
        this.catArrList = catArrList;
        mInflater = LayoutInflater.from(context);
        this.top_margin = top_margin;
        this.image_Height = image_Height;
        this.bottom_margin = bottom_margin;
        this.banner_height = banner_height;
        this.collection = collection;
        this.rivaLookBookActivity = activity;
    }

    @Override
    public CatViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View recipeView;
        recipeView = mInflater.inflate(R.layout.list_edit_item, parentViewGroup, false);

        return new CatViewHolder(recipeView);
    }


    @Override
    public SubCatViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View ingredientView;
        ingredientView = mInflater.inflate(R.layout.edit_list_item, childViewGroup, false);

        return new SubCatViewHolder(ingredientView);
    }

    @Override
    public void onBindParentViewHolder(@NonNull CatViewHolder catViewHolder, int parentPosition, @NonNull HomeEditModel recipe) {
        catViewHolder.bind(recipe);
    }

    @Override
    public void onBindChildViewHolder(@NonNull SubCatViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull CollectionListItemModel child) {
        childViewHolder.bind(child);

    }

    @Override
    public int getParentViewType(int parentPosition) {
        return PARENT_NORMAL;
    }

    @Override
    public int getChildViewType(int parentPosition, int childPosition) {
        return CHILD_NORMAL;

    }

    @Override
    public boolean isParentViewType(int viewType) {
        return viewType == PARENT_NORMAL;
    }

    class SubCatViewHolder extends ChildViewHolder {

        private TextView txtName;

        public SubCatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
        }

        public void bind(final CollectionListItemModel subCatModel) {

            txtName.setTypeface(typeNormal);
            txtName.setText(subCatModel.getTitle());

            txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", subCatModel.getType_id());
                    bundle.putString("name", subCatModel.getTitle());
                    rivaLookBookActivity.getNavController().navigate(R.id.navigation_editorial,bundle);
                }
            });

        }
    }

    class CatViewHolder extends ParentViewHolder {
        private final ImageView imgBanner;
        private ProgressBar spinProgress;

        public CatViewHolder(@NonNull View itemView) {
            super(itemView);

            imgBanner = itemView.findViewById(R.id.imgBanner);
            spinProgress = itemView.findViewById(R.id.spin);
        }

        public void bind(@NonNull final HomeEditModel catModel) {

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, image_Height);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.topMargin = top_margin;
            params.bottomMargin = bottom_margin;
            imgBanner.setLayoutParams(params);

            Utils.loadImagesUsingCoil(context, (catModel.getImage() != null && !catModel.getImage().equals("")) ? catModel.getImage() : "http://venanimation.com/archivo/ver/94735/yudelmi.png?ancho=300&largo=400", imgBanner);

        }
    }

}
