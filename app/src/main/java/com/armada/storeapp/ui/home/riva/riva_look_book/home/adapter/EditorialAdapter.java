package com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.armada.storeapp.data.model.response.EditorialResponse;
import com.armada.storeapp.databinding.ListEditItemBinding;
import com.armada.storeapp.databinding.ListEditSubitemBinding;
import com.armada.storeapp.ui.home.riva.riva_look_book.home.Algolia.ExpandableRecyler.ChildViewHolder;
import com.armada.storeapp.ui.home.riva.riva_look_book.home.Algolia.ExpandableRecyler.ExpandableRecyclerAdapter;
import com.armada.storeapp.ui.home.riva.riva_look_book.home.Algolia.ExpandableRecyler.ParentViewHolder;
import com.armada.storeapp.ui.home.riva.riva_look_book.home.model.EditorialModel;
import com.armada.storeapp.ui.utils.Utils;

import java.util.ArrayList;

public class EditorialAdapter extends ExpandableRecyclerAdapter<EditorialModel, EditorialResponse.Data.Product, EditorialAdapter.CatViewHolder, EditorialAdapter.SubCatViewHolder> {

    private static final int PARENT_NORMAL = 1;
    private static final int CHILD_NORMAL = 2;
    private LayoutInflater mInflater;
    private ArrayList<EditorialModel> catArrList;
    private Context context;
    private String selectedCurrency="";

    public EditorialAdapter(Context context, ArrayList<EditorialModel> catArrList,String currency) {
        super(catArrList);
        this.context = context;
        this.catArrList = catArrList;
        mInflater = LayoutInflater.from(context);
        selectedCurrency=currency;
    }

    @Override
    public CatViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        ListEditItemBinding listEditItemBinding = ListEditItemBinding.inflate(LayoutInflater.from(context), parentViewGroup, false);
        return new CatViewHolder(listEditItemBinding);
    }


    @Override
    public SubCatViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        ListEditSubitemBinding listEditSubitemBinding = ListEditSubitemBinding.inflate(LayoutInflater.from(context), childViewGroup, false);
        return new SubCatViewHolder(listEditSubitemBinding);
    }

    @Override
    public void onBindParentViewHolder(@NonNull CatViewHolder catViewHolder, int parentPosition, @NonNull EditorialModel model) {
        catViewHolder.bind(model);
    }

    @Override
    public void onBindChildViewHolder(@NonNull SubCatViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull EditorialResponse.Data.Product child) {
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
        private ListEditSubitemBinding binding;

        public SubCatViewHolder(@NonNull ListEditSubitemBinding listEditSubitemBinding) {
            super(listEditSubitemBinding.getRoot());
            binding = listEditSubitemBinding;
        }

        public void bind(EditorialResponse.Data.Product subCatModel) {

            EditProductAdapter adapter = new EditProductAdapter(context,selectedCurrency, subCatModel.getProducts());
            LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            binding.rcyProducts.setLayoutManager(horizontalLayoutManagaer);
            binding.rcyProducts.setAdapter(adapter);

        }

    }


    class CatViewHolder extends ParentViewHolder {
        private ListEditItemBinding binding;


        public CatViewHolder(@NonNull ListEditItemBinding listEditItemBinding) {
            super(listEditItemBinding.getRoot());
            binding = listEditItemBinding;
        }

        public void bind(@NonNull final EditorialModel catModel) {

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, catModel.getImage_height());
            binding.imgBanner.setLayoutParams(params);

            binding.spin.setVisibility(View.VISIBLE);

            if (catModel.getMedia_type().equals("I")) {
                binding.spin.setVisibility(View.GONE);
                binding.imgPlay.setVisibility(View.GONE);
                Utils.loadImagesUsingCoil(context, (catModel.getImage() != null && !catModel.getImage().equals("")) ? catModel.getImage() : "http://venanimation.com/archivo/ver/94735/yudelmi.png?ancho=300&largo=400", binding.imgBanner);
            } else if (catModel.getMedia_type().equals("G")) {
                binding.spin.setVisibility(View.GONE);
                binding.imgPlay.setVisibility(View.GONE);
                Utils.loadGifUsingCoil(context, (catModel.getMedia_file() != null && !catModel.getMedia_file().equals("")) ? catModel.getMedia_file() : "http://venanimation.com/archivo/ver/94735/yudelmi.png?ancho=300&largo=400", binding.imgBanner);
            } else if (catModel.getMedia_type().equals("V")) {
                try {
                    binding.imgPlay.setVisibility(View.VISIBLE);
                    binding.spin.setVisibility(View.GONE);
                    Utils.loadGifUsingCoil(context, (catModel.getMedia_thumbnail() != null && !catModel.getMedia_thumbnail().equals("")) ? catModel.getMedia_thumbnail() : "http://venanimation.com/archivo/ver/94735/yudelmi.png?ancho=300&largo=400", binding.imgBanner);
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } else {
                binding.spin.setVisibility(View.GONE);
                binding.imgPlay.setVisibility(View.GONE);
                Utils.loadGifUsingCoil(context, (catModel.getImage() != null && !catModel.getImage().equals("")) ? catModel.getImage() : "http://venanimation.com/archivo/ver/94735/yudelmi.png?ancho=300&largo=400", binding.imgBanner);
            }
        }


    }

}
