package com.armada.storeapp.ui.home.instore_transactions.picklist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.PicklistResponseModel
import com.armada.storeapp.databinding.FragmentPicklistBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.instore_transactions.picklist.adapter.PicklistRecyclerviewAdapter
import com.armada.storeapp.ui.utils.ConnectionDetector
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class PicklistFragment : Fragment(), TabLayout.OnTabSelectedListener {

    private var TAG = PicklistFragment::class.java.simpleName
    var cd: ConnectionDetector? = null
    private var fragmentPicklistBinding: FragmentPicklistBinding? = null
    private var mainActivity: MainActivity? = null
    private val binding get() = fragmentPicklistBinding!!
    lateinit var picklistViewModel: PicklistViewModel

    //    var sessionToken = ""
    lateinit var picklistRecyclerviewAdapter: PicklistRecyclerviewAdapter
    var storeCode = ""
    var picklistStatus = "0"
    var searchString = ""
    var isSelectedTransfer = false
    var searchtypp = "Manual"

    //    val picklistHashMap = HashMap<String, PicklistResponseModel.PickHeader>()
//    var currentPicklist = ArrayList<PicklistResponseModel.PickHeader>()
    var totalPages = 0
    var currentPage = 1
    var startIndex = 0
    var endIndex = 0
    val noOfItemsToShow = 10
    var currentPageList = ArrayList<PicklistResponseModel.PickHeader>()
    var pageItemsHashmap = HashMap<Int, ArrayList<PicklistResponseModel.PickHeader>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentPicklistBinding =
            FragmentPicklistBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initializeData()

        return root
    }


    private fun initializeData() {

        mainActivity = activity as MainActivity
        picklistViewModel =
            ViewModelProvider(this).get(PicklistViewModel::class.java)

        cd = ConnectionDetector(activity)
        storeCode = SharedpreferenceHandler(requireContext()).getData(
            SharedpreferenceHandler.STORE_CODE,
            ""
        )!!

        if (cd?.isConnectingToInternet!!) {
            fragmentPicklistBinding?.tvPage?.text = "Page $currentPage"
            getPickListApi(
                picklistStatus,
                storeCode,
                searchString,
                noOfItemsToShow.toString(),
                currentPage.toString(),
                searchtypp
            )
        }


//                binding.manualPendingRg.children.forEach { radioButton ->
//            (radioButton as RadioButton).setOnCheckedChangeListener { button, isChecked ->
//                if (isChecked) {
//                    binding.manualPendingRg.children.forEach { bBtn ->
//                        if (bBtn.id != button.id) {
//                            (bBtn as RadioButton).isChecked = false
//                        }
//                    }
//                }
//            }
//        }

//        binding.manualPendingRg.setOnCheckedChangeListener { _, checkedId ->
//            val radio: RadioButton = binding.root.findViewById(checkedId)
//            when (radio) {
//                radio0 -> {
//                    Toast.makeText(context, "manual", Toast.LENGTH_SHORT).show()
//                }
//                radio1 -> {
//                    Toast.makeText(context, "Auto", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

        binding.manualPendingRg.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio0 -> {
                    searchtypp = "Manual"
                    getPickListApi(
                        picklistStatus,
                        storeCode,
                        searchString,
                        noOfItemsToShow.toString(),
                        currentPage.toString(),
                        searchtypp
                    )
                }
                R.id.radio1 -> {
                    searchtypp = "Sales"
                    getPickListApi(
                        picklistStatus,
                        storeCode,
                        searchString,
                        noOfItemsToShow.toString(),
                        currentPage.toString(),
                        searchtypp
                    )
                }
                else -> {
                    // Code to be executed when none of the radio buttons are checked
                }

            }
        })

        binding.tabLayout.addOnTabSelectedListener(this)
        mainActivity?.BackPressed(this)

        fragmentPicklistBinding?.btnPageNext?.setOnClickListener {
            forwardPAges()

        }
        fragmentPicklistBinding?.btnPageBack?.setOnClickListener {
            if (currentPage > 1)
                backwardPages()
        }

    }

    override fun onResume() {
        super.onResume()
//        getPickListApi(picklistStatus, storeCode, searchString)
    }

    private fun settingRecyclerview(
        list: ArrayList<PicklistResponseModel.PickHeader>,
        isTransferred: Boolean
    ) {

//        if (list == null || list.size == 0) {
//            fragmentPicklistBinding?.btnPageNext?.visibility = View.GONE
//            fragmentPicklistBinding?.btnPageBack?.visibility = View.GONE
//            fragmentPicklistBinding?.tvPage?.visibility = View.GONE
//        } else {
//            fragmentPicklistBinding?.btnPageNext?.visibility = View.VISIBLE
//            fragmentPicklistBinding?.btnPageBack?.visibility = View.VISIBLE
//            fragmentPicklistBinding?.tvPage?.visibility = View.VISIBLE
//        }

       

        picklistRecyclerviewAdapter =
            PicklistRecyclerviewAdapter(list!!, requireContext(), isTransferred)

        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragmentPicklistBinding?.recyclerView?.layoutManager = manager
        fragmentPicklistBinding?.recyclerView?.adapter = picklistRecyclerviewAdapter

        picklistRecyclerviewAdapter?.onOpenClick = { picklist ->
            val bundle = Bundle()
            bundle.putString(Constants.STORE_CODE, storeCode)
            bundle.putString(Constants.ID, picklist.id?.toString())
            bundle.putString(Constants.PICKLIST_NO, picklist.documentNumber)
            mainActivity?.navController?.navigate(R.id.navigation_picklist_details, bundle)
        }
    }


    fun getPickListApi(
        isStatus: String,
        storeCode: String,
        searchString: String,
        itemCount: String,
        pageNo: String,
        type: String
    ) {
        currentPageList = ArrayList()

        Log.e("picklist req", isStatus + "   "+ storeCode+ "   "+ searchString+ "   "+itemCount+ "   "+pageNo+ "   "+searchtypp)


        picklistViewModel.getPicklistByPages(isStatus, storeCode, searchString, itemCount, pageNo, searchtypp)
        picklistViewModel.picklistResponse.observe(viewLifecycleOwner) {
            if (isSelectedTransfer)
                fragmentPicklistBinding?.tabLayout?.getTabAt(1)?.select()
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->

                        try {
                            if (response.pickListHeader == null) {
                                fragmentPicklistBinding?.lvNoRecord?.visibility = View.VISIBLE
                                currentPageList.clear()
                                if (picklistStatus.equals("0"))
                                    settingRecyclerview(currentPageList, false)
                                else
                                    settingRecyclerview(currentPageList, true)
                            } else {
                                fragmentPicklistBinding?.lvNoRecord?.visibility = View.GONE

                                try {
                                    for (picklistHeader in response.pickListHeader!!) {
                                        if (picklistHeader.pickListDetails == null)
                                        else
                                            if (picklistHeader.pickListDetails != null || picklistHeader?.pickListDetails?.size!! > 0) {
                                                currentPageList.add(picklistHeader)
                                            }
                                    }
                                    if (currentPageList.size == 0) {
                                        fragmentPicklistBinding?.lvNoRecord?.visibility =
                                            View.VISIBLE
                                        currentPageList.clear()
                                        if (picklistStatus.equals("0"))
                                            settingRecyclerview(currentPageList, false)
                                        else
                                            settingRecyclerview(currentPageList, true)
                                    } else {
                                        fragmentPicklistBinding?.tvPage?.text = "Page $currentPage"
                                        fragmentPicklistBinding?.lvNoRecord?.visibility = View.GONE
                                        currentPageList.sortBy { it.id }
                                        if (picklistStatus.equals("0"))
                                            settingRecyclerview(currentPageList, false)
                                        else
                                            settingRecyclerview(currentPageList, true)
                                        pageItemsHashmap.put(currentPage, currentPageList)
                                    }

                                } catch (exception: Exception) {
                                    exception.printStackTrace()
                                }

                            }

                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }
                    }
                    picklistViewModel.picklistResponse.value = null
                }
                is Resource.Error -> {

                    mainActivity?.showProgressBar(false)
                    fragmentPicklistBinding?.lvNoRecord?.visibility =
                        View.VISIBLE
                    currentPageList.clear()
                    if (picklistStatus.equals("0"))
                        settingRecyclerview(currentPageList, false)
                    else
                        settingRecyclerview(currentPageList, true)
                    try {
                        it.message?.let { message ->
                            Log.e(TAG, "Error: $message")
                            if (message.contains("message")) {
                                val jsonObj = JSONObject(message)
                                mainActivity?.showMessage(jsonObj.get("message").toString())
                            } else
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    picklistViewModel.picklistResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        val position = tab!!.position
        when (position) {
            0 -> {
                isSelectedTransfer = false
                picklistStatus = "0"
//                fragmentPicklistBinding?.txtOpen?.visibility = View.INVISIBLE

            }
            1 -> {
                isSelectedTransfer = true
                picklistStatus = "1"
//                fragmentPicklistBinding?.txtOpen?.visibility = View.GONE
//                getPickListApi(picklistStatus, storeCode, searchString)
            }
        }
        getPickListApi(
            picklistStatus,
            storeCode,
            searchString,
            noOfItemsToShow.toString(),
            currentPage.toString(),
            searchtypp
        )
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    fun initialhandlePages(isTransferred: Boolean) {
        pageItemsHashmap = HashMap()

//        val totalPicklistItems = currentPicklist.size
//        currentPage = 1
//        currentPageList = ArrayList()
//        if (totalPicklistItems <= noOfItemsToShow) {
//            currentPageList = currentPicklist
//            totalPages = 1
//        } else {
//            if (totalPicklistItems % noOfItemsToShow == 0)
//                totalPages = totalPicklistItems / noOfItemsToShow
//            else
//                totalPages = totalPicklistItems / noOfItemsToShow + 1
//
//            startIndex = 0
//            endIndex = 0 + (noOfItemsToShow - 1)
//            for ((index, value) in currentPicklist.withIndex()) {
//                if (index >= startIndex && index <= endIndex)
//                    currentPageList.add(value)
//            }
//            pageItemsHashmap.put(currentPage, currentPageList)
//        }

//        fragmentPicklistBinding?.tvPage?.text = "Page $currentPage out of $totalPages"
//        settingRecyclerview(currentPageList, isTransferred)

    }

    fun forwardPAges() {
        currentPage += 1
        if (pageItemsHashmap.containsKey(currentPage)) {
            currentPageList = pageItemsHashmap.get(currentPage)!!
            fragmentPicklistBinding?.tvPage?.text = "Page $currentPage"
            if (picklistStatus.equals("0"))
                settingRecyclerview(currentPageList, false)
            else
                settingRecyclerview(currentPageList, true)
        } else {
            getPickListApi(
                picklistStatus,
                storeCode,
                searchString,
                noOfItemsToShow.toString(),
                currentPage.toString(),
                searchtypp
            )
        }
//        currentPageList = ArrayList()
//        if (pageItemsHashmap.containsKey(currentPage)) {
//            currentPageList = pageItemsHashmap.get(currentPage)!!
//        } else {
//            startIndex += noOfItemsToShow
//            endIndex += noOfItemsToShow
//
//
//            for ((index, value) in currentPicklist.withIndex()) {
//                if (index >= startIndex && index <= endIndex) {
//                    currentPageList.add(value)
//                }
//
//            }
//
//            pageItemsHashmap.put(currentPage, currentPageList)
//        }




        }

        fun backwardPages() {
            currentPageList = ArrayList()
            currentPage -= 1
            currentPageList = pageItemsHashmap.get(currentPage)!!
            fragmentPicklistBinding?.tvPage?.text = "Page $currentPage"
            if (picklistStatus.equals("0"))
                settingRecyclerview(currentPageList, false)
            else
                settingRecyclerview(currentPageList, true)

        }

    }