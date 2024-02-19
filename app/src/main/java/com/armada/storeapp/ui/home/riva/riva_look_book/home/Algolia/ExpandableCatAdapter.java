//package com.armada.riva.Category.Algolia;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.graphics.Typeface;
//import android.os.Build;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.RotateAnimation;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//
//import com.armada.storeapp.R;
//import com.armada.storeapp.data.model.response.CollectionListItemModel;
//import com.armada.storeapp.data.model.response.PatternsProduct;
//import com.armada.storeapp.data.model.response.TimeLineModel;
//import com.armada.storeapp.ui.home.riva.riva_look_book.ui.home.Algolia.ExpandableRecyler.ChildViewHolder;
//import com.armada.storeapp.ui.home.riva.riva_look_book.ui.home.Algolia.ExpandableRecyler.ExpandableRecyclerAdapter;
//import com.armada.storeapp.ui.home.riva.riva_look_book.ui.home.Algolia.ExpandableRecyler.Model.CategoriesListMDataModel;
//import com.armada.storeapp.ui.home.riva.riva_look_book.ui.home.Algolia.ExpandableRecyler.Model.MainCatModel;
//import com.armada.storeapp.ui.home.riva.riva_look_book.ui.home.Algolia.ExpandableRecyler.ParentViewHolder;
//import com.armada.storeapp.ui.utils.Utils;
//import com.bumptech.glide.Glide;
//
//
//import java.util.ArrayList;
//
//public class ExpandableCatAdapter extends ExpandableRecyclerAdapter<MainCatModel, CategoriesListMDataModel, ExpandableCatAdapter.CatViewHolder, ExpandableCatAdapter.SubCatViewHolder> {
//
//    private static final int PARENT_NORMAL = 1;
//    private static final int CHILD_NORMAL = 2;
//    private LayoutInflater mInflater;
//    private ArrayList<MainCatModel> catArrList;
//    private Typeface typeSemibold, typeNormal;
//    private Context context;
//    private boolean extended_cat = false;
//
//    public ExpandableCatAdapter(Context context, ArrayList<MainCatModel> catArrList, Typeface typeSemiBold, Typeface typeNormal, Boolean extended) {
//        super(catArrList);
//        this.context = context;
//        this.catArrList = catArrList;
//        mInflater = LayoutInflater.from(context);
//        this.typeSemibold = typeSemiBold;
//        this.typeNormal = typeNormal;
//        this.extended_cat = extended;
//    }
//
//    @Override
//    public CatViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
//        View recipeView;
//        recipeView = mInflater.inflate(R.layout.list_cat_item, parentViewGroup, false);
//
//        return new CatViewHolder(recipeView);
//    }
//
//
//    @Override
//    public SubCatViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
//        View ingredientView;
//        ingredientView = mInflater.inflate(R.layout.list_subcat_item, childViewGroup, false);
//
//        return new SubCatViewHolder(ingredientView);
//    }
//
//    @Override
//    public void onBindParentViewHolder(@NonNull CatViewHolder catViewHolder, int parentPosition, @NonNull MainCatModel recipe) {
//        catViewHolder.bind(recipe);
//    }
//
//    @Override
//    public void onBindChildViewHolder(@NonNull SubCatViewHolder subCatViewHolder, int parentPosition, int childPosition, @NonNull CategoriesListMDataModel ingredient) {
//        subCatViewHolder.bind(ingredient);
//    }
//
//    @Override
//    public int getParentViewType(int parentPosition) {
//        return PARENT_NORMAL;
//    }
//
//    @Override
//    public int getChildViewType(int parentPosition, int childPosition) {
//        return CHILD_NORMAL;
//
//    }
//
//    @Override
//    public boolean isParentViewType(int viewType) {
//        return viewType == PARENT_NORMAL;
//    }
//
//    class SubCatViewHolder extends ChildViewHolder {
//
//        private TextView txtSubcatName;
//
//        public SubCatViewHolder(@NonNull View itemView) {
//            super(itemView);
//            txtSubcatName = itemView.findViewById(R.id.txtSubCatName);
//        }
//
//        public void bind(final CategoriesListMDataModel subCatModel) {
//            //System.out.println("Here i am dynamic string 444 " + subCatModel.getName());
//            txtSubcatName.setText(Utils.getDynamicStringFromApi(context, subCatModel.getName()));
//            txtSubcatName.setTypeface(typeNormal);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //println("Has subcategories" + "   Subcategories :: " + subCatModel);
//
//                    ArrayList arrList = new ArrayList<String>();
//                    ArrayList arrListCollection = new java.util.ArrayList<CollectionListItemModel>(); //Dummy
//                    ArrayList arListPattrn = new ArrayList<PatternsProduct>();  /// Dummy
//                    ArrayList arrListTimeline = new ArrayList<TimeLineModel>(); //Dummy
//                    String level = subCatModel.getLevel();
//
//                    CollectionListItemModel model = new CollectionListItemModel(subCatModel.getThumbnail_url(), "", "", subCatModel.getId(), subCatModel.getName(), "", subCatModel.getId(), subCatModel.getLevel(), level, "0",
//                            "", "", "", "", "", "", subCatModel.getName(), Integer.parseInt(subCatModel.getId()), "", "", 0, 0, "", arListPattrn,
//                            0, "", arrListCollection, 0, arrList, 0, arrListTimeline, 0, 0, 0);
//
//                    //System.out.println("Here i am sub categories 222");
////                    EventBus.getDefault().post(new CategoryCollectionData(0, model));//todo
//
//                }
//            });
//
//        }
//
//    }
//
//
//    class CatViewHolder extends ParentViewHolder {
//
//        private static final float INITIAL_POSITION = 0.0f;
//        private static final float ROTATED_POSITION = 180f;
//
//        private final ImageView imgCat;
//        private final ImageView imgArrow;
//        private TextView txtCatName;
//
//        public CatViewHolder(@NonNull View itemView) {
//            super(itemView);
//            txtCatName = itemView.findViewById(R.id.txtCatName);
//            imgArrow = itemView.findViewById(R.id.imgArrow);
//            imgCat = itemView.findViewById(R.id.imgCategory);
//        }
//
//        public void bind(@NonNull MainCatModel catModel) {
//            String name = "";
//            switch (catModel.getName().toLowerCase()) {
//                case "woman":
//                    name = context.getResources().getString(R.string.woman);
//                    break;
//
//                case "teens":
//                    name = context.getResources().getString(R.string.teens);
//                    break;
//
//                case "kids":
//                    name = context.getResources().getString(R.string.kids);
//                    break;
//
//                default:
//                    name = catModel.getName();
//                    break;
//            }
//            //System.out.println("Here i am dynamic string 333   " + name);
//            txtCatName.setText(Utils.getDynamicStringFromApi(context, name));
//            txtCatName.setAllCaps(true);
//            txtCatName.setTypeface(typeSemibold);
//
//
//            //Picasso.with(context).load(catModel.getImage()).into(imgCat);
//
//           /* if (catModel.getHas_subcat())
//            {
//                Picasso.with(context).load(R.drawable.ic_arrow_down).into(imgArrow);
//            } else {
//                if (appcontroller.isLangArebic())
//                Picasso.with(context).load(R.drawable.ic_left).into(imgArrow);
//                else Picasso.with(context).load(R.drawable.ic_right).into(imgArrow);
//            }*/
//
//
//            Glide.with(context).load(catModel.getImage()).into(imgCat);
//
//            if (catModel.getHas_subcat()) {
//                Glide.with(context).load(R.drawable.ic_baseline_keyboard_arrow_down_24).into(imgArrow);
//            } else {
//                 Glide.with(context).load(R.drawable.ic_baseline_keyboard_arrow_right_24).into(imgArrow);
//            }
//
//
//        }
//
//        @SuppressLint("NewApi")
//        @Override
//        public void setExpanded(boolean expanded) {
//            super.setExpanded(expanded);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                if (expanded) {
//                    imgArrow.setRotation(ROTATED_POSITION);
//                } else {
//                    imgArrow.setRotation(INITIAL_POSITION);
//                }
//            }
//        }
//
//        @Override
//        public void onExpansionToggled(boolean expanded) {
//            super.onExpansionToggled(expanded);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                RotateAnimation rotateAnimation;
//                if (expanded) { // rotate clockwise
//                    rotateAnimation = new RotateAnimation(ROTATED_POSITION,
//                            INITIAL_POSITION,
//                            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
//                            RotateAnimation.RELATIVE_TO_SELF, 0.5f);
//                } else { // rotate counterclockwise
//                    rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
//                            INITIAL_POSITION,
//                            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
//                            RotateAnimation.RELATIVE_TO_SELF, 0.5f);
//                }
//
//                rotateAnimation.setDuration(200);
//                rotateAnimation.setFillAfter(true);
//                imgArrow.startAnimation(rotateAnimation);
//            }
//        }
//    }
//
//}
