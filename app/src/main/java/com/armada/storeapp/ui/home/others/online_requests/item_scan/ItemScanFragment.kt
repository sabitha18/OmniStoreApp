package com.armada.storeapp.ui.home.others.online_requests.item_scan

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.CompletePickRequest
import com.armada.storeapp.data.model.response.OpenDocumentResponseModel
import com.armada.storeapp.data.model.response.ShopPickReasonResponseModel
import com.armada.storeapp.databinding.FragmentItemScanBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.others.online_requests.item_scan.adapter.ItemsAdapter
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ItemScanFragment : Fragment() {
    private lateinit var viewModel: ItemScanViewModel
    private lateinit var fragmentItemScanBinding: FragmentItemScanBinding
    private lateinit var mainActivity: MainActivity
    lateinit var openDocumentResponseModel: OpenDocumentResponseModel
    private var itemList: ArrayList<OpenDocumentResponseModel.OpenDocumentResponseModelItem> =
        ArrayList()
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    var token = ""
    var shopLocationCode = ""
    var remarksList = ArrayList<ShopPickReasonResponseModel.ShopPickReasonResponseModelItem>()
    lateinit var itemsAdapter: ItemsAdapter
    var wareHouseUserId = ""
    var isItemScan = false
    var isBinScan = false
    var currentBincode = ""
    var currentItemCode = ""
    var hasEnabledDefaultBin = false
    var itemBinhashmap = HashMap<String, String>()

    var isUsingScanningDevice = true
    private var currentBincodeEdittextLength = 0
    private var currentItemEditTextLength = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel =
            ViewModelProvider(this).get(ItemScanViewModel::class.java)

        fragmentItemScanBinding =
            FragmentItemScanBinding.inflate(inflater, container, false)
        val root: View = fragmentItemScanBinding.root
        init()

        return root
    }

    fun init() {
        mainActivity = activity as MainActivity
        sharedpreferenceHandler = SharedpreferenceHandler(requireContext())
        token = sharedpreferenceHandler.getData(SharedpreferenceHandler.WAREHOUSE_TOKEN, "")!!
        token = "Bearer $token"
        wareHouseUserId =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.WAREHOUSE_USER_ID, "")!!
        shopLocationCode =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_CODE, "")!!

        val binavailablity = sharedpreferenceHandler.getData(SharedpreferenceHandler.BIN_AVAILABLE, "")
        fragmentItemScanBinding?.textView22?.isInvisible = binavailablity.equals("false")
        fragmentItemScanBinding?.lvBin?.isInvisible = binavailablity.equals("false")
        fragmentItemScanBinding?.checkBox?.isInvisible = binavailablity.equals("false")

        if (arguments != null) {
            try {
                val gson = Gson()
                val responseString = arguments?.getString("open_response")
                val documentNo = arguments?.getString("document_no")
                openDocumentResponseModel =
                    gson.fromJson(responseString, OpenDocumentResponseModel::class.java)
                itemList = openDocumentResponseModel
                fragmentItemScanBinding?.tvDocumentNo?.text = documentNo

            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        mainActivity?.BackPressed(this)
        getRemarks()
        setClickListeners()
        setEditTextListeners()

    }

    private fun setClickListeners() {

        fragmentItemScanBinding?.checkBox?.setOnCheckedChangeListener { buttonView, isChecked ->
            hasEnabledDefaultBin = isChecked
        }

        fragmentItemScanBinding?.edtBin?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                isUsingScanningDevice = true
            }

        }
        fragmentItemScanBinding?.edtItemCode?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                isUsingScanningDevice = true
            }
        }

        fragmentItemScanBinding?.imageButtonBinSubmit?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            currentBincode = fragmentItemScanBinding?.edtBin?.text?.toString()!!
            if (currentBincode.equals(""))
                Toast.makeText(requireContext(), "Please enter bincode", Toast.LENGTH_SHORT).show()
            else
                checkItemInBin(shopLocationCode, currentBincode, currentItemCode)
        }

        fragmentItemScanBinding?.imageButtonItemSubmit?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            val barcode = fragmentItemScanBinding?.edtItemCode?.text.toString()
            if (barcode.equals(""))
                Toast.makeText(
                    requireContext(),
                    "Please enter or scan item barcode",
                    Toast.LENGTH_LONG
                ).show()
            else
                scanItem(barcode)
        }

//        fragmentItemScanBinding?.imageButtonItemScan?.setOnClickListener {
//            val i = Intent(mainActivity, CamActivity::class.java)
//            i.putExtra("title", "Verify Your Shipment")
//            i.putExtra("msg", "Scan Barcode")
//            getContent.launch(i)
//        }

        fragmentItemScanBinding?.btnFinish?.setOnClickListener {
            var callApiDisabled = false
            val scanneditemList = java.util.ArrayList<CompletePickRequest.Item>()
            for (item in itemList) {
                if (item.PICK_QTY == 0 && item.USER_REMARKS == "FOUND AND SCANNED") {
                    Toast.makeText(
                        requireContext(),
                        "Please scan the item or change the remark for ${item.ITEM_CODE}",
                        Toast.LENGTH_LONG
                    ).show()
                    callApiDisabled = true
                } else {
                    var bincodeValue = itemBinhashmap.get(item.ITEM_CODE)
                    if(bincodeValue==null){
                        bincodeValue = ""
                    }
                    val pickItem = CompletePickRequest.Item(
                        item.FROM_LOCATION!!,
                        item.ID.toString(),
                        item.LOCATION_CODE!!,
                        item.PICK_QTY.toString(),
                        item.ORDER_REFNO!!,
                        item.USER_REMARKS!!,
                        wareHouseUserId,
                        item.WH_CODE!!,
                        item.ITEM_CODE!!,
                        bincodeValue
                    )
                    scanneditemList.add(pickItem)
                }
            }
            if (!callApiDisabled) {
                val completePickRequest = CompletePickRequest(scanneditemList)
                completePick(completePickRequest, token)
            }
        }
    }

    private fun setEditTextListeners() {
        fragmentItemScanBinding?.edtBin?.setSelectAllOnFocus(true)
        fragmentItemScanBinding?.edtItemCode?.setSelectAllOnFocus(true)
        fragmentItemScanBinding?.edtItemCode?.requestFocus()
        fragmentItemScanBinding?.edtBin?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                val length = text?.length!!
                if (currentBincodeEdittextLength - length == 1)
                    isUsingScanningDevice = false
                else if (length - currentBincodeEdittextLength == 1)
                    isUsingScanningDevice = false
                else if (length == 0)
                    isUsingScanningDevice = true
            }

            override fun afterTextChanged(text: Editable?) {
                currentBincodeEdittextLength = text?.length!!
                currentBincode = fragmentItemScanBinding?.edtBin?.text?.toString()!!
                if (text?.toString()?.equals("") == false) {
                    if (text?.length == 1) {
                        isUsingScanningDevice = false
                    }
                    if (isUsingScanningDevice) {
                        checkItemInBin(shopLocationCode, currentBincode, currentItemCode)
                    }
                } else
                    isUsingScanningDevice = true


            }

        })

        fragmentItemScanBinding?.edtItemCode?.addTextChangedListener(object : TextWatcher {
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
                        scanItem(text.toString())
                    }
                } else
                    isUsingScanningDevice = true

            }

        })

    }

//    private val getContent =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (it.resultCode == Activity.RESULT_OK) {
//                val barcode = it?.data?.extras?.get("BarcodeResult").toString()
//                if (isItemScan) {
//                    fragmentItemScanBinding?.edtItemCode?.setText(barcode)
//                    scanItem(barcode)
//                } else if (isBinScan) {
//                    fragmentItemScanBinding?.edtBincode?.setText(barcode)
//                    checkItemInBin(shopLocationCode, currentBincode, currentItemCode)
//                }
//
//            }
//        }
//
//    fun launchScanner() {
//        val i = Intent(mainActivity, CamActivity::class.java)
//        getContent.launch(i)
//    }

    private fun settingRecyclerview(
        list: ArrayList<OpenDocumentResponseModel.OpenDocumentResponseModelItem>,
        remarks: ArrayList<ShopPickReasonResponseModel.ShopPickReasonResponseModelItem>
    ) {
        itemsAdapter = ItemsAdapter(list!!, requireContext(), this, remarks)
        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragmentItemScanBinding?.recyclerviewItems?.layoutManager = manager
        fragmentItemScanBinding?.recyclerviewItems?.adapter = itemsAdapter
    }

    private fun getRemarks() {
        viewModel.getRemarks(token)
        viewModel.remarks_reasons.observe(mainActivity!!) { response ->
            when (response) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    if (response.data == null) {
                        Toast.makeText(
                            requireContext(),
                            "Something went wrong !",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        remarksList = response?.data
                        settingRecyclerview(itemList, remarksList)
                    }
                    viewModel.remarks_reasons.value = null
                }

                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    if (response.statusCode == 401) {
                        Toast.makeText(
                            requireContext(),
                            "Your session token expired. Please logout and login again",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else
                        Toast.makeText(
                            requireContext(),
                            response.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    viewModel.remarks_reasons.value = null
                }

                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    private fun scanItem(barcode: String) {
        viewModel.scanItem(barcode, token)
        viewModel.scan_item_response.observe(mainActivity!!) { response ->

            when (response) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    if (response.data == null) {
                        Toast.makeText(
                            requireContext(),
                            "Wrong Itemcode",
                            Toast.LENGTH_LONG
                        ).show()
                        fragmentItemScanBinding?.edtItemCode?.setText("")
                        fragmentItemScanBinding?.edtItemCode?.requestFocus()
                    } else {

                        if (response?.data?.get(0)?.ITEMCODE!!.contains("No records found")) {
                            Toast.makeText(requireContext(), "Item not found", Toast.LENGTH_LONG)
                                .show()
                            fragmentItemScanBinding?.edtItemCode?.setText("")
                            fragmentItemScanBinding?.edtItemCode?.requestFocus()
                        } else {
                            try {
                                currentItemCode = response.data?.get(0).ITEMCODE!!
                                for (item in itemList) {
                                    if (item.ITEM_CODE == response.data?.get(0).ITEMCODE) {
                                        if (item.PICK_QTY == 1) {
                                            Toast.makeText(
                                                requireContext(),
                                                "This item is already scanned !",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        break
                                    }
                                }

                                val binavailablity = sharedpreferenceHandler.getData(SharedpreferenceHandler.BIN_AVAILABLE, "")

                                if(binavailablity.equals("true")){
                                    if (currentBincode.equals(""))
                                        fragmentItemScanBinding?.edtBin?.requestFocus()
                                } else {
                                    checkItemInBin(shopLocationCode, "", currentItemCode)
                                }


                            } catch (exception: Exception) {
                                exception.printStackTrace()
                            }

                        }

                    }
                    viewModel.scan_item_response.value = null
                }

                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    if (response.statusCode == 401) {
                        Toast.makeText(
                            requireContext(),
                            "Your session token expired. Please logout and login again",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else
                        if (response.message?.contains("No records") == true) {
                            mainActivity?.showMessage("Item Not Found")
                            fragmentItemScanBinding?.edtItemCode?.clearFocus()
                            fragmentItemScanBinding?.edtItemCode?.requestFocus()
                        }
                    viewModel.scan_item_response?.value = null
                }

                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    fun checkItemInBin(
        storeCode: String,
        bincode: String,
        itemCode: String
    ) {
        viewModel.checkIteminBin(
            storeCode, bincode, itemCode
        )
        viewModel.checkIteminBinResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.statusCode == 1) {
                            for (item in itemList) {
                                if (item.ITEM_CODE == currentItemCode) {
                                    if (item.PICK_QTY == 0) {
                                        item.PICK_QTY = 1
                                    }
                                    break
                                }
                            }
                            settingRecyclerview(itemList, remarksList)
                            itemBinhashmap.put(currentItemCode, currentBincode)
                            fragmentItemScanBinding?.edtItemCode?.setText("")
                            fragmentItemScanBinding?.edtItemCode?.requestFocus()
                            if (!hasEnabledDefaultBin)
                                fragmentItemScanBinding?.edtBin?.setText("")
                        }
                    }
                    viewModel.checkIteminBinResponse.value = null
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    fragmentItemScanBinding?.edtBin?.clearFocus()
                    fragmentItemScanBinding?.edtBin?.requestFocus()
                    try {
                        it.message?.let { message ->
                            if (message.contains("message")) {
                                val jsonObj = JSONObject(message)
                                mainActivity?.showMessage(jsonObj.get("message").toString())
                            } else
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    viewModel.checkIteminBinResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        mainActivity?.setTitle("Shop Pick")
    }

    private fun completePick(
        completePickRequest: CompletePickRequest,
        token: String
    ) {

        viewModel.completePick(completePickRequest, token)
        viewModel.pick_response.observe(mainActivity!!) { response ->

            when (response) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    if (response.data == null) {
                        Toast.makeText(
                            requireContext(),
                            "Something went wrong !",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (response?.data?.get(0)?.Success_Message?.contains("Success") == true) {
                            Toast.makeText(requireContext(), "Pick Completed", Toast.LENGTH_LONG)
                                .show()
//                            mainActivity?.replaceFragment(PendingOrdersListFragment())//Todo
                        }
                    }
                    viewModel.pick_response.value = null
                }

                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    if (response.statusCode == 401) {
                        Toast.makeText(
                            requireContext(),
                            "Your session token expired. Please logout and login again",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else
                        Toast.makeText(
                            requireContext(),
                            response.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    viewModel.pick_response.value = null
                }

                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

//    override fun onItemPicked(
//        item: OpenDocumentResponseModel.OpenDocumentResponseModelItem,
//        remark: ShopPickReasonResponseModel.ShopPickReasonResponseModelItem
//    ) {
//        var qty = 0
//        if (remark?.REASON?.contains("FOUND AND SCANNED") == true) {
//            qty = 1
//        }
//        completePick(
//            item,
//            remark,
//            item?.ORDER_REFNO!!,
//            shopLocationCode,
//            item?.ID.toString(),
//            item?.LOCATION_CODE!!,
//            "$qty",
//            token
//        )
//    }

    fun updateRemarks(list: ArrayList<OpenDocumentResponseModel.OpenDocumentResponseModelItem>) {
//        for ((index, value) in itemList.withIndex()) {
//            val item = itemList.get(index)
//            if (item.ITEM_CODE == updatedItem.ITEM_CODE) {
//                itemList[index] = updatedItem
//            }
//        }
        this.itemList = list
    }

}