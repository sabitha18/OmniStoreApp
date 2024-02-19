package com.armada.storeapp.ui.home.instore_transactions.picklist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.ManualPicklistRequest
import com.armada.storeapp.data.model.response.CreatePicklistSkuResponse
import com.armada.storeapp.data.model.response.GetDestinationBinResponse
import com.armada.storeapp.databinding.FragmentCreateManualPicklistBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.instore_transactions.picklist.adapter.CreatePicklistSkuRecyclerviewAdapter
import com.armada.storeapp.ui.home.instore_transactions.picklist.listener.DestinationBinSelectedListener
import com.armada.storeapp.ui.home.instore_transactions.picklist.listener.ItemQuantityListener
import com.armada.storeapp.ui.utils.ConnectionDetector
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class CreateManualPicklistFragment : Fragment(), ItemQuantityListener,
    DestinationBinSelectedListener {

    private var hasEnabledDefaultSourceBin: Boolean = false
    private var hasEnabledDefaultDestinationBin: Boolean = false
    private var TAG = CreateManualPicklistFragment::class.java.simpleName
    var cd: ConnectionDetector? = null
    private var fragmentCreateManualPicklistBinding: FragmentCreateManualPicklistBinding? = null
    private var mainActivity: MainActivity? = null
    lateinit var picklistViewModel: PicklistViewModel
    var storeCode = ""
    var userId = 0
    var storeId = 0
    var currentItemcode = ""
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    var destinationBinList = ArrayList<GetDestinationBinResponse.BinSubLevel>()
    var itemList = java.util.ArrayList<CreatePicklistSkuResponse.PickDetails>()

    var isUsingScanningDevice = true
    private var currentSourceBinEditTextLength = 0
    private var currentDestinationBinEditTextLength = 0
    private var currentItemEditTextLength = 0

    private var itemHashMap = HashMap<String, ManualPicklistRequest.PickDetails>()

    lateinit var createPicklistSkuAdapter: CreatePicklistSkuRecyclerviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentCreateManualPicklistBinding =
            FragmentCreateManualPicklistBinding.inflate(inflater, container, false)
        val root: View = fragmentCreateManualPicklistBinding!!.root
        initializeData()

        return root
    }


    private fun initializeData() {

        mainActivity = activity as MainActivity
        picklistViewModel =
            ViewModelProvider(this).get(PicklistViewModel::class.java)

        cd = ConnectionDetector(activity)
        sharedpreferenceHandler = SharedpreferenceHandler(requireContext())
        storeCode = sharedpreferenceHandler.getData(
            SharedpreferenceHandler.STORE_CODE,
            ""
        )!!
        userId = sharedpreferenceHandler.getData(SharedpreferenceHandler.LOGIN_USER_ID, 0)
        storeId = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_ID, 0)
        setListeners()
        setEditTextListeners()
        mainActivity?.BackPressed(this)
        getDestinationBinList(storeId?.toString())
    }

    private fun setListeners() {

        fragmentCreateManualPicklistBinding?.imageButtonModelSubmit?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            val itemcode = fragmentCreateManualPicklistBinding?.edtModel?.text?.toString()
            if (itemcode!!.isEmpty())
                Toast.makeText(requireContext(), "Please enter Item code", Toast.LENGTH_SHORT)
                    .show()
            else
                getSkus(storeCode, itemcode)
        }

        fragmentCreateManualPicklistBinding?.btnCreatePicklist?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            try {
                val picklistItems = ArrayList<ManualPicklistRequest.PickDetails>()
                for (item in itemList) {
                    item.moveQty?.let {
                        if (item.moveQty!! > 0) {
                            picklistItems.add(
                                ManualPicklistRequest.PickDetails(
                                    item.moveQty.toString(),
                                    item.destinationBin, item.item, item.sourceBin
                                )
                            )
                        }
                    }
                }
                val manualPicklistRequest =
                    ManualPicklistRequest(picklistItems, storeCode, userId)
                createManualPicklist(manualPicklistRequest)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    private fun setEditTextListeners() {
        fragmentCreateManualPicklistBinding?.edtModel?.setSelectAllOnFocus(true)
        fragmentCreateManualPicklistBinding?.edtModel?.requestFocus()
        fragmentCreateManualPicklistBinding?.edtModel?.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus) {
                isUsingScanningDevice = true
            }
        }

        fragmentCreateManualPicklistBinding?.edtModel?.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                val length = text?.length!!
                if (currentItemEditTextLength - length == 1)
                    isUsingScanningDevice = false
                else if (length - currentItemEditTextLength == 1)
                    isUsingScanningDevice = false
                else if (length == 0)
                    isUsingScanningDevice = true
            }

            override fun afterTextChanged(text: Editable?) {
                currentItemEditTextLength = text?.length!!
                if (text?.toString()?.equals("") == false) {
                    if (text?.length == 1) {
                        isUsingScanningDevice = false
                    }
                    if (isUsingScanningDevice) {
                        getSkus(storeCode, text?.toString())
                    }
                } else
                    isUsingScanningDevice = true
            }

        })
    }


    private fun settingRecyclerview(
        list: ArrayList<CreatePicklistSkuResponse.PickDetails>,
        destinationBinList: ArrayList<GetDestinationBinResponse.BinSubLevel>
    ) {
        fragmentCreateManualPicklistBinding?.horizontalScrollView6?.visibility = View.VISIBLE
        fragmentCreateManualPicklistBinding?.btnCreatePicklist?.visibility = View.VISIBLE
        createPicklistSkuAdapter =
            CreatePicklistSkuRecyclerviewAdapter(
                list,
                destinationBinList,
                requireContext(),
                this,
                this
            )
        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragmentCreateManualPicklistBinding?.recyclerView?.layoutManager = manager
        fragmentCreateManualPicklistBinding?.recyclerView?.adapter = createPicklistSkuAdapter
    }


    fun getSkus(
        storeCode: String,
        model: String,
    ) {
        itemList?.clear()
        picklistViewModel.getCreatePicklistSkus(
            storeCode, model
        )
        picklistViewModel.createPicklistSkuResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    try {
                        if (it?.data?.pickListDetails?.size!! > 0) {
                            itemList = it?.data?.pickListDetails
                            settingRecyclerview(itemList, destinationBinList!!)
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    picklistViewModel.createPicklistSkuResponse.value = null
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    try {
                        it.message?.let { message ->
                            Log.e(TAG, "Error: $message")
                            if (message.contains("message")) {
                                val jsonObj = JSONObject(message)
                                mainActivity?.showMessage(jsonObj.get("message").toString())
                            } else
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
                                    .show()
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    picklistViewModel.createPicklistSkuResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }


    fun getDestinationBinList(
        storeId: String
    ) {
        picklistViewModel.getDestinationBinList(
            storeId
        )
        picklistViewModel.getDestinationBinResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    try {
                        if (it?.data?.binSubLevelList?.size!! > 0) {
                            destinationBinList = it.data.binSubLevelList!!
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    picklistViewModel.getDestinationBinResponse.value = null
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    try {
                        it.message?.let { message ->
                            Log.e(TAG, "Error: $message")
                            if (message.contains("message")) {
                                val jsonObj = JSONObject(message)
                                mainActivity?.showMessage(jsonObj.get("message").toString())
                            } else
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
                                    .show()
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    picklistViewModel.getDestinationBinResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }


    fun createManualPicklist(
        manualPicklistRequest: ManualPicklistRequest
    ) {
        picklistViewModel.createManualPicklist(manualPicklistRequest)
        picklistViewModel.manualPicklistResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    Toast.makeText(requireContext(), it?.data?.displayMessage, Toast.LENGTH_SHORT)
                        .show()
                    fragmentCreateManualPicklistBinding?.horizontalScrollView6?.visibility =
                        View.GONE
                    fragmentCreateManualPicklistBinding?.edtModel?.setText("")
                    fragmentCreateManualPicklistBinding?.edtModel?.requestFocus()
                    fragmentCreateManualPicklistBinding?.btnCreatePicklist?.visibility = View.GONE
                    picklistViewModel?.manualPicklistResponse?.value = null
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
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
                    picklistViewModel?.manualPicklistResponse?.value = null

                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    override fun onItemQtyChanged(item: CreatePicklistSkuResponse.PickDetails, position: Int) {
        itemList.set(position, item)
    }

    override fun onDestinationBinSelected(
        item: CreatePicklistSkuResponse.PickDetails,
        position: Int
    ) {
        itemList.set(position, item)
    }


}