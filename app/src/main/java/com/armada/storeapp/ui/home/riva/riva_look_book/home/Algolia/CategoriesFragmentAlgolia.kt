//package com.armada.riva.Category.Algolia
//
//import android.os.Bundle
//import android.util.DisplayMetrics
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.LinearLayout
//import android.widget.RelativeLayout
//import androidx.annotation.UiThread
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.algolia.search.saas.Client
//import com.algolia.search.saas.Index
//import com.algolia.search.saas.Query
//import com.armada.riva.Category.Algolia.ExpandableRecyler.ExpandableRecyclerAdapter
//import com.armada.storeapp.ui.home.riva.riva_look_book.ui.home.Algolia.ExpandableRecyler.Model.MainCatModel
//import com.armada.riva.Category.Algolia.ExpandableRecyler.Model.SubCatModel
//import com.armada.riva.Category.adapter.CategoryParentAdapter
//import com.armada.riva.Category.fragment.CategoryDataFragment
//import com.armada.riva.Category.model.CategoryCollectionData
//import com.armada.riva.HOME.Model.*
//import com.armada.riva.homepage.HomeActivity
//import com.armada.riva.R
//import com.armada.riva.Util.AppController
//import com.armada.riva.Util.ConnectionDetector
//import com.armada.riva.Util.Global
//import com.armada.riva.Util.RequestInterfaceForCollections
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.CompositeDisposable
//import io.reactivex.schedulers.Schedulers
//import kotlinx.android.synthetic.main.categoris_fragment_algolia.*
//import kotlinx.android.synthetic.main.categoris_fragment_algolia.view.*
//import org.greenrobot.eventbus.EventBus
//import org.jetbrains.anko.below
//
///**
// * Created by User999 on 7/13/2018.
// */
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
//class CategoriesFragmentAlgolia : Fragment() {
//
//    private var param1: String? = null
//    private var param2: String? = null
//    private var mCompositeDisposable: CompositeDisposable? = null
//    private lateinit var mActivity: FragmentActivity
//    private var isFromRefresh: Boolean? = false
//    var lnrMain: LinearLayout? = null
//    private var globalClass: Global? = null
//    private var cd: ConnectionDetector? = null
//    private var appController: AppController? = null
//    private var relMainHolder: RelativeLayout? = null
//
//    /////Algolia///////////////////////////
//
//    private var client: Client? = null
//    private var index: Index? = null
//    private var query: Query? = null
//
//    /////////////////////////////////////////////////////////////
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        mActivity = activity as HomeActivity
//        mCompositeDisposable = CompositeDisposable()
//        globalClass = Global(mActivity)
//        cd = ConnectionDetector(activity)
//        appController = mActivity.applicationContext as AppController
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        val view = inflater.inflate(R.layout.categoris_fragment_algolia, container, false)
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        lnrMain = view.lnrMain
//        relMainHolder = view.relMainLayout
//
//        view.swipe_refresh_layout.setOnRefreshListener {
//            isFromRefresh = true
//            view.swipe_refresh_layout.isRefreshing = true
//            view.swipe_refresh_layout.postDelayed({
//                view.swipe_refresh_layout.isRefreshing = false
//                //Load Home page data
//                if (cd!!.isConnectingToInternet) {
//                    categoriesApi()
//                } else {
//                    Global.showSnackbar(
//                        relMainHolder!!,
//                        resources.getString(R.string.plz_chk_internet)
//                    )
//                }
//            }, 1000)
//        }
//
//        //Load Home page data
//        if (cd?.isConnectingToInternet == true) {
//            categoriesApi()
//        } else {
//            Global.showSnackbar(
//                relMainHolder!!,
//                resources.getString(R.string.plz_chk_internet)
//            )
//        }
//
//        // Init Algolia.
//        client = Client(
//            resources.getString(R.string.algolia_app_id_Live),
//            resources.getString(R.string.algolia_api_key_Live)
//        )
//        index =
//            client!!.getIndex("magento_" + AppController.instance.getStoreCode() + "_categories")
//
//        // Pre-build query.
//        query = Query()
//        query!!.filters = "level=2 OR level=3 AND include_in_menu=1"
//        query!!.hitsPerPage = 1000
//
//        getAlgoliaCategories()
//
//    }
//
//    private fun getAlgoliaCategories() {
//
//        val arrayLstLvl0Cat = ArrayList<SubCatModel>()
//        val arrayLstLvl1Cat = ArrayList<SubCatModel>()
//        val arrayLstMainCat = ArrayList<MainCatModel>()
//        val arrListPattern = ArrayList<PatternsProduct>()
//        val arrList = ArrayList<String>()
//        val arrListTimeline = ArrayList<TimeLineModel>()
//        val arrListCollection = ArrayList<CollectionListItemModel>()
//        val catModel = CollectionListItemModel(
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            0,
//            "",
//            "",
//            0,
//            0,
//            "",
//            arrListPattern as ArrayList<PatternsProduct?>,
//            0,
//            "",
//            arrListCollection as ArrayList<CollectionListItemModel?>,
//            0,
//            arrList,
//            0,
//            arrListTimeline as ArrayList<TimeLineModel?>,
//            0
//        )
//
//        val arrListImages = ArrayList<String>()
//        val array = resources.getStringArray(R.array.cat_images)
//
//        for (element in array)
//            arrListImages.add(element)
//
//
//        index!!.searchAsync(query) { content, error ->
//            if (content != null && error == null) {
//
//                try {
//                    val jHits = content.getJSONArray("hits")
//                    var post = 0
//                    for (i in 0 until jHits.length()) {
//
//                        val jsonObject = jHits.getJSONObject(i)
//                        if (!jsonObject.getString("objectID").equals("765")) {
//
//                            if (post > 11)
//                                post = 0
//
//                            if (jsonObject.getInt("level") == 2) {
//                                //println("Object Id :"+jobj.getString("objectID")+"  "+ jobj.getString("name"))
//                                val arrListChildren = ArrayList<SubCatModel>()
//                                val category = SubCatModel(
//                                    jsonObject.getString("objectID"),
//                                    jsonObject.getString("name"),
//                                    jsonObject.getString("path").replace("/", "///"),
//                                    jsonObject.getInt("level"),
//                                    "http:" + jsonObject.optString(
//                                        "icon_url",
//                                        arrListImages[post].replace("https:", "")
//                                    ),
//                                    arrListChildren,
//                                    catModel
//                                )
//
//                                arrayLstLvl0Cat.add(category)
//                                post++
//                            } else if (jsonObject.getInt("level") == 3) {
//
//                                val arrListChildren = ArrayList<SubCatModel>()
//                                val category = SubCatModel(
//                                    jsonObject.getString("objectID"),
//                                    jsonObject.getString("name"),
//                                    jsonObject.getString("path").replace("/", "///"),
//                                    jsonObject.getInt("level"),
//                                    "https://cdn3.iconfinder.com/data/icons/meanicons-base/512/meanicons_31-512.png",
//                                    arrListChildren,
//                                    catModel
//                                )
//
//                                arrayLstLvl1Cat.add(category)
//                            }
//                        }
//                    }
//
//
//                    for (j in 0 until arrayLstLvl0Cat.size) {
//                        val arrListChildren = ArrayList<SubCatModel>()
//                        if (arrayLstLvl0Cat[j].category_id != "765") {          /// Editorials
//
//                            for (k in 0 until arrayLstLvl1Cat.size) {
//                                if (arrayLstLvl1Cat[k].path.startsWith(arrayLstLvl0Cat[j].path)) {
//                                    arrListChildren.add(arrayLstLvl1Cat[k])
//                                }
//                            }
//                        }
//
////                        val categoryModel = MainCatModel(
////                            arrayLstLvl0Cat[j].category_id,
////                            arrayLstLvl0Cat[j].name,
////                            arrayLstLvl0Cat[j].path,
////                            "",
////                            arrayLstLvl0Cat[j].thumbnail,
////                            if (arrListChildren.size > 0) true else false,
////                            arrListChildren
////                        )
//                        //arrayLstMainCat.add(categoryModel)
//                        arrayLstLvl0Cat[j].children = arrListChildren
//                    }
//
//                    rcyCategories?.isNestedScrollingEnabled = false
//                    val mAdapter = ExpandableCatAdapter(
//                        mActivity,
//                        arrayLstMainCat,
//                        appController!!.semiBold,
//                        appController!!.normalFont,
//                        false
//                    )
//                    mAdapter.setExpandCollapseListener(object :
//                        ExpandableRecyclerAdapter.ExpandCollapseListener {
//                        @UiThread
//                        override fun onParentExpanded(parentPosition: Int) {
//
//                            val expandedCat = arrayLstMainCat[parentPosition]
//                            var dataChanged = false
//                            try {
//                                val database = FirebaseDatabase.getInstance()
//                                val queryRef = database.getReference("Categories").child("items")
//                                    .child(expandedCat.id).orderByChild("views").limitToLast(100)
//
//                                rcyCategories.postDelayed({
//                                    rcyCategories.parent.requestChildFocus(
//                                        rcyCategories,
//                                        rcyCategories
//                                    )
//
//                                }, 100)
//
//                                queryRef.addValueEventListener(object : ValueEventListener {
//                                    override fun onDataChange(p0: DataSnapshot) {
//                                        try {
//
//                                            if (!dataChanged) {
//
//                                                dataChanged = true
//                                                if (p0.child("views").value != null) {
//                                                    val count = p0.child("views").value
//                                                    if (count == 0) {
//                                                        //  mutableData.setValue(1);
//                                                        val fireModel = FirebaseCategoryModel(
//                                                            expandedCat.id,
//                                                            expandedCat.name,
//                                                            1
//                                                        )
//                                                        val myRef =
//                                                            database.getReference("Categories")
//                                                        myRef.child("items").child(expandedCat.id)
//                                                            .setValue(fireModel)
//
//                                                    } else {
//
//                                                        val viewCount = count
//                                                        val vi = (viewCount.toString()).toInt() + 1
//
//                                                        val myRef =
//                                                            database.getReference("Categories")
//                                                        myRef.child("items").child(expandedCat.id)
//                                                            .child("views").setValue(vi)
//                                                    }
//
//                                                } else {
//
//                                                    val fireModel = FirebaseCategoryModel(
//                                                        expandedCat.id,
//                                                        expandedCat.name,
//                                                        1
//                                                    )
//                                                    val myRef = database.getReference("Categories")
//                                                    myRef.child("items").child(expandedCat.id)
//                                                        .setValue(fireModel)
//                                                }
//                                            }
//                                        } catch (e: Exception) {
//                                            e.printStackTrace()
//                                        }
//                                    }
//
//                                    override fun onCancelled(p0: DatabaseError) {
//
//                                    }
//                                })
//                            } catch (e: Exception) {
//
//                            }
//
//                            /*  if(expandedCat.id.equals("765"))
//                            {
//                                mAdapter.collapseParent(parentPosition)
//                                val intent = Intent(context, EditorialsActivity::class.java)
//                                mActivity.startActivity(intent)
//
//                            } else*/ if (!expandedCat.has_subcat) {
//                                mAdapter.collapseParent(parentPosition)
//                                val arrListOne = ArrayList<String>()
//                                val arrListCollectionOne =
//                                    ArrayList<CollectionListItemModel>() //Dummy
//                                val arListPattern = ArrayList<PatternsProduct>()  /// Dummy
//                                val arrListTimelineOne = ArrayList<TimeLineModel>()  // Dummy
//
//                                val model = CollectionListItemModel(
//                                    expandedCat.image,
//                                    "",
//                                    "",
//                                    expandedCat.id,
//                                    expandedCat.name,
//                                    "",
//                                    expandedCat.id,
//                                    expandedCat.path,
//                                    "categories.level0:" + expandedCat.path,
//                                    "0",
//                                    "",
//                                    "",
//                                    "",
//                                    "",
//                                    "",
//                                    "",
//                                    expandedCat.name,
//                                    expandedCat.id.toInt(),
//                                    "",
//                                    "",
//                                    0,
//                                    0,
//                                    "",
//                                    arListPattern as ArrayList<PatternsProduct?>,
//                                    0,
//                                    "",
//                                    arrListCollectionOne as ArrayList<CollectionListItemModel?>,
//                                    0,
//                                    arrListOne,
//                                    0,
//                                    arrListTimelineOne as ArrayList<TimeLineModel?>,
//                                    0
//                                )
//                                /*  val intent = Intent(mActivity, ProductListingActivity::class.java)
//                                  intent.putExtra("banner_height", 0)
//                                  intent.putExtra("model", model)
//                                  mActivity.startActivity(intent)*/
//                                // (activity as HomeActivity).displayProductListInCategory(CategoryCollectionData(0,model))
//                                EventBus.getDefault().post(CategoryCollectionData(0, model))
//                            }
//                        }
//
//                        @UiThread
//                        override fun onParentCollapsed(parentPosition: Int) {
//                            val collapsedCat = arrayLstMainCat[parentPosition]
//
//                        }
//                    })
//
//                    rcyCategories.adapter = mAdapter
//                    rcyCategories.layoutManager = LinearLayoutManager(mActivity)
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    // println("error : "+e.toString())
//                }
//            }
//        }
//    }
//
//    private fun categoriesApi() {
//        if (!isFromRefresh!!) {
//            showProgressDialog()
//        }
//
//        val requestInterface = RequestInterfaceForCollections.create()
//        //Log.d("Categories",globalClass?.SECONDARY_DOMAIN + "category-collections?lang=" + AppController.instance.getStoreCode()+"&parent_category_id="+param1)
//
//        mCompositeDisposable?.add(
//            requestInterface.getCategoryCollection(
//                Global.MOB_TOKEN,
//                globalClass!!.SECONDARY_DOMAIN + "category-collections?lang=" + AppController.instance.getStoreCode() + "&parent_category_id=" + param1
//            )
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponse, this::handleError)
//        )
//    }
//
//    private fun handleResponse(homeList: HomeNewResponse) {
//
//        if (appController?.left_menu_set == false) {
//            appController?.left_menu_set = true
//        }
//
//        //println("Success ::: "+homeList)
//
//        val responseModel: HomeNewResponse = homeList
//
//        if (responseModel.data?.get_all_category != null && responseModel.data.get_all_category.equals(
//                "0"
//            )
//        )
//            rcyCategories.visibility = View.GONE
//        else rcyCategories.visibility = View.VISIBLE
//
//
//        if (responseModel.data?.collectionGroups?.isNotEmpty() == true) {
//
//            val param = RelativeLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            param.topMargin = mActivity.resources.getDimension(R.dimen.twenty_dp).toInt()
//            param.below(rcyMainCategories)
//            rcyCategories.layoutParams = param
//
//            setCategoryView(responseModel.data.collectionGroups)
//
//        } else {
//            lnrMain?.visibility = View.GONE
//        }
//
//        if (!isFromRefresh!!) {
//            dismissDialog()
//        }
//        if (parentFragment is CategoryDataFragment) {
//            (parentFragment as CategoryDataFragment).removeSkeleton()
//        }
//    }
//
//    ///
//    private fun setCategoryView(collectionData: List<CollectionGroupsItemModel?>?) {
//
//        val displayMetrics = DisplayMetrics()
//        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
//        val width: Double = displayMetrics.widthPixels.toDouble()
//        val density: Double = (width / 320)
//
//        lnrMain?.visibility = View.GONE
//
//        rcyMainCategories.isNestedScrollingEnabled = false
//        //println("Category :: "+collectionData)
//        val adapterParent = CategoryParentAdapter(
//            (collectionData as ArrayList<CollectionGroupsItemModel>?)!!,
//            mActivity,
//            globalClass!!,
//            appController!!,
//            density
//        )
//        rcyMainCategories.layoutManager =
//            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
//        rcyMainCategories.adapter = adapterParent
//    }
//
//
//    //Show progres dialog method
//    private fun showProgressDialog() {
//        /*loadingView = com.armada.riva.loadingdialog.LoadingView.Builder(mActivity, true)
//                .setProgressColorResource(R.color.black)
//                .setBackgroundColorRes(R.color.bg_color)
//                .setProgressStyle(com.armada.riva.loadingdialog.LoadingView.ProgressStyle.CYCLIC)
//                .setCustomMargins(0, 0, 0, resources.getDimension(R.dimen.fifty_dp).toInt())
//                .attachTo(mActivity)
//        loadingView!!.show()*/
//    }
//
//    //Dismiss progress dialog method
//    private fun dismissDialog() {
//        /* if (loadingView != null)
//             loadingView!!.hide()*/
//    }
//
//    /* fun enableDisableSwipeRefresh(enable: Boolean) {
//         if (swipe_refresh_layout != null) {
//             swipe_refresh_layout.setEnabled(enable)
//         }
//     }*/
//
//    //Here Error We will get
//    fun handleError(error: Throwable) {
//
//        //println("Error ::: "+error.localizedMessage)
//        //Log.d(TAG, error.localizedMessage)
//        /* if (progressBar != null)
//             progressBar.visibility = View.GONE*/
//
//        dismissDialog()
//        (activity as HomeActivity).displayErrorDialog(error)
//
//
//        //  Toast.makeText(mActivity, "Error ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
//    }
//
//    companion object {
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            CategoriesFragmentAlgolia().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        if (mCompositeDisposable != null) {
//            mCompositeDisposable?.dispose()
//
//        }
//    }
//
//}