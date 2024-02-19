package com.armada.storeapp.ui.home.instore_transactions.picklist

import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.PicklistTransferRequest
import com.armada.storeapp.data.model.request.SkipPicklistTransferRequest
import com.armada.storeapp.data.model.response.PicklistDetailsResponseModel
import com.armada.storeapp.data.model.response.SkipReasonListResponse
import com.armada.storeapp.databinding.FragmentPicklistTransferBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.instore_transactions.picklist.adapter.PicklistItemRecyclerviewAdapter
import com.armada.storeapp.ui.home.instore_transactions.picklist.dialog.ReasonInterface
import com.armada.storeapp.ui.home.instore_transactions.picklist.dialog.SelectReasonDialog
import com.armada.storeapp.ui.utils.ConnectionDetector
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class PicklistTransferFragment : Fragment(), ReasonInterface {
    private var TAG = PicklistTransferFragment::class.java.simpleName
    var cd: ConnectionDetector? = null
    private var fragmentPicklistTransferBinding: FragmentPicklistTransferBinding? = null
    private var mainActivity: MainActivity? = null
    private val binding get() = fragmentPicklistTransferBinding!!
    lateinit var picklistViewModel: PicklistViewModel
    var sessionToken = ""
    var shopLocationCode = ""
    var userId = ""
    lateinit var selectedPicklist: ArrayList<PicklistDetailsResponseModel.PickHeader.PickDetails>
    var scannedItemsList = ArrayList<PicklistDetailsResponseModel.PickHeader.PickDetails>()
    lateinit var picklistItemRecyclerviewAdapter: PicklistItemRecyclerviewAdapter
    var isScanningSourceBin = false
    var isScanningItem = false
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    var isScanningDestinationBin = false
    var currentScannedItem: PicklistDetailsResponseModel.PickHeader.PickDetails? = null
    var currentItemPosition = -1
    var selectReasonDialog: SelectReasonDialog? = null
    var reasonList = ArrayList<SkipReasonListResponse.SkipReasons>()
    var skippedQty = 0
    var totalScannedQty = 0

    //    var transferEnabled = false
    var picklistDocumentNo: String? = ""
    var isUsingScanningDevice = true
    private var currentSourceBinEditTextLength = 0
    private var currentDestinationBinEditTextLength = 0
    private var currentItemEditTextLength = 0

    var itemIdScanHashMap = HashMap<Int, Boolean>()
    var sourceBinitemQtyHashMap = HashMap<String, ArrayList<ScannedItem>>()

    var currentDefaultSourceBin: String? = ""
    var currentDefaultDestinationBin: String? = ""
    var currentItemCode: String? = ""

    data class ScannedItem(val skuCode: String, var scannedQty: Int)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentPicklistTransferBinding =
            FragmentPicklistTransferBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initializeData()

        return root
    }

    private fun initializeData() {

        mainActivity = activity as MainActivity
        picklistViewModel =
            ViewModelProvider(this).get(PicklistViewModel::class.java)

        cd = ConnectionDetector(activity)
        sharedpreferenceHandler = SharedpreferenceHandler(requireContext())
        sessionToken = "Bearer " + sharedpreferenceHandler.getData(
            SharedpreferenceHandler.WAREHOUSE_TOKEN,
            ""
        )!!
        shopLocationCode = sharedpreferenceHandler.getData(
            SharedpreferenceHandler.STORE_CODE,
            ""
        )!!

        userId =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.LOGIN_USER_ID, 0)!!?.toString()

        arguments?.let {
            val storeCode: String = it.get(Constants.STORE_CODE).toString()
            val picklistHeaderId: String = it.get(Constants.ID).toString()
            picklistDocumentNo = it.get(Constants.PICKLIST_NO).toString()
            getPicklistDetails("0", storeCode!!, picklistHeaderId!!)
        }
        mainActivity?.BackPressed(this)
        setClickListeners()
        fragmentPicklistTransferBinding?.edtSourceBin?.setSelectAllOnFocus(true)
        fragmentPicklistTransferBinding?.edtItemCode?.setSelectAllOnFocus(true)
        fragmentPicklistTransferBinding?.edtDestinationBin?.setSelectAllOnFocus(true)
        fragmentPicklistTransferBinding?.edtItemCode?.clearFocus()
        fragmentPicklistTransferBinding?.edtItemCode?.requestFocus()
        getReasonList()
    }

    fun setClickListeners() {
        fragmentPicklistTransferBinding?.edtSourceBin?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                isUsingScanningDevice = true
            }

        }
        fragmentPicklistTransferBinding?.edtItemCode?.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus) {
                isUsingScanningDevice = true
            }
        }
        fragmentPicklistTransferBinding?.edtDestinationBin?.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus) {
                isUsingScanningDevice = true
            }
        }
        fragmentPicklistTransferBinding?.edtSourceBin?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                val length = text?.length!!
                if (currentSourceBinEditTextLength - length == 1)
                    isUsingScanningDevice = false
                else if (length - currentSourceBinEditTextLength == 1)
                    isUsingScanningDevice = false
                else if (length == 0)
                    isUsingScanningDevice = true
            }

            override fun afterTextChanged(text: Editable?) {
                currentSourceBinEditTextLength = text?.length!!
                if (text?.toString()?.equals("") == false) {
                    if (text?.length == 1) {
                        isUsingScanningDevice = false
                    }
                    if (isUsingScanningDevice) {
                        isScanningSourceBin = true
                        isScanningItem = false
                        isScanningDestinationBin = false
                        mainActivity?.hideSoftKeyboard()
                        scanBinApi(text.toString(), shopLocationCode)
                    }
                } else
                    isUsingScanningDevice = true
            }

        })

        fragmentPicklistTransferBinding?.edtItemCode?.addTextChangedListener(object : TextWatcher {
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
                if (text?.length == 0) {
                    isUsingScanningDevice = true
                } else {
                    if (text?.length == 1) {
                        isUsingScanningDevice = false
                    }
                    if (isUsingScanningDevice) {
                        if (checkIfAllItemsScanned()) {
                            Toast.makeText(
                                requireContext(),
                                "All items were scanned",
                                Toast.LENGTH_SHORT
                            ).show()
                            fragmentPicklistTransferBinding?.edtItemCode?.setText("")
                        } else {
                            scanItem()
                        }
//                            isScanningSourceBin = false
//                            isScanningItem = true
//                            isScanningDestinationBin = false
//                            val sourceBin =
//                                fragmentPicklistTransferBinding?.edtSourceBin?.text?.toString()
//                            val itemcode = text?.toString()
//                            mainActivity?.hideSoftKeyboard()
//                            if (currentScannedItem?.itemBarcode.equals(itemcode) ||
//                                currentScannedItem?.item.equals(itemcode)
//                            ) {
//                                if (itemScanHashMap.containsKey(currentScannedItem?.id))
//                                    Toast.makeText(
//                                        requireContext(),
//                                        "This item is already scanned",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                else
//                                    checkItemInBin(shopLocationCode, sourceBin!!, itemcode)
//                            } else
//                                Toast.makeText(requireContext(), "Incorrect Item", Toast.LENGTH_SHORT)
//                                    .show()


                    }
                }

            }

        })

        fragmentPicklistTransferBinding?.edtDestinationBin?.addTextChangedListener(object :
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
                if (currentDestinationBinEditTextLength - length == 1)
                    isUsingScanningDevice = false
                else if (length - currentDestinationBinEditTextLength == 1)
                    isUsingScanningDevice = false
                else if (length == 0)
                    isUsingScanningDevice = true
            }

            override fun afterTextChanged(text: Editable?) {
                currentDestinationBinEditTextLength = text?.length!!
                if (text?.length == 0) {
                    isUsingScanningDevice = true
                } else {
                    if (text?.length == 1) {
                        isUsingScanningDevice = false
                    }
                    if (isUsingScanningDevice) {
                        isScanningSourceBin = false
                        isScanningItem = false
                        isScanningDestinationBin = true
                        mainActivity?.hideSoftKeyboard()
                        scanBinApi(text.toString(), shopLocationCode)
                    }
                }

            }

        })
        fragmentPicklistTransferBinding?.imageButtonSourceBinSubmit?.setOnClickListener {
            isScanningSourceBin = true
            isScanningItem = false
            isScanningDestinationBin = false
            mainActivity?.hideSoftKeyboard()
            val barcode = fragmentPicklistTransferBinding?.edtSourceBin?.text?.toString()
            scanBinApi(barcode!!, shopLocationCode)
        }

        fragmentPicklistTransferBinding?.imageButtonItemSubmit?.setOnClickListener {
            if (checkIfAllItemsScanned()) {
                Toast.makeText(
                    requireContext(),
                    "All items were scanned",
                    Toast.LENGTH_SHORT
                ).show()
                fragmentPicklistTransferBinding?.edtItemCode?.setText("")
            } else {
                scanItem()
            }
        }

        fragmentPicklistTransferBinding?.imageButtonDestinationSubmit?.setOnClickListener {
            isScanningSourceBin = false
            isScanningItem = false
            isScanningDestinationBin = true
            mainActivity?.hideSoftKeyboard()
            val barcode = fragmentPicklistTransferBinding?.edtDestinationBin?.text?.toString()
            scanBinApi(barcode!!, shopLocationCode)

        }

        fragmentPicklistTransferBinding?.btnSkip?.setOnClickListener {

            if (checkIfAllItemsScanned()) {
                Toast.makeText(
                    requireContext(),
                    "All items were scanned",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showReasonDialog(reasonList)
            }
        }


        fragmentPicklistTransferBinding?.btnTransfer?.setOnClickListener {
            if (scannedItemsList.size > 0)
                try {
                    val binLogList = ArrayList<PicklistTransferRequest.BinLog>()
                    for (item in scannedItemsList!!) {
                        if (item.skippedQty!! > 0)
                            item.isSkipped = 1
                        val binLog = PicklistTransferRequest.BinLog(
                            item.newDestinationBin!!,
                            item.newSourceBin!!,
                            item.sourceBin!!,
                            item.destinationBin!!,
                            item.id!!,
                            item.isSkipped,
                            "1",
                            picklistDocumentNo,
                            item.item!!,
                            item.skippedQty!!
                        )
                        binLogList.add(binLog)
                    }
                    var date = ""
                    try {
                        date = mainActivity?.getCurrentDate()!!
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    val picklistTransferRequest =
                        PicklistTransferRequest(binLogList, date, shopLocationCode, userId)
                    picklistTransferAPi(picklistTransferRequest)

                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            else
                Toast.makeText(
                    requireContext(),
                    "Please scan atleast one item to make transfer",
                    Toast.LENGTH_SHORT
                )
                    .show()
        }
    }

    fun scanItem() {
        isScanningSourceBin = false
        isScanningItem = true
        isScanningDestinationBin = false
        mainActivity?.hideSoftKeyboard()
        val barcode = fragmentPicklistTransferBinding?.edtItemCode?.text?.toString()?.trim()
        val sourceBin = fragmentPicklistTransferBinding?.edtSourceBin?.text?.toString()?.trim()

        if (currentScannedItem?.itemBarcode?.trim().equals(barcode) ||
            currentScannedItem?.item?.trim().equals(barcode)
        ) {
            if (itemIdScanHashMap.containsKey(currentScannedItem?.id))
                Toast.makeText(
                    requireContext(),
                    "This item is already scanned",
                    Toast.LENGTH_SHORT
                ).show()
            else
                checkItemInBin(shopLocationCode, sourceBin!!, barcode!!)
        } else
            Toast.makeText(requireContext(), "Incorrect Itemdd", Toast.LENGTH_SHORT)
                .show()
    }

    fun showReasonDialog(reasonList: ArrayList<SkipReasonListResponse.SkipReasons>) {
        selectReasonDialog = SelectReasonDialog(this, reasonList)
        if (selectReasonDialog?.isVisible == false)
            selectReasonDialog?.show(childFragmentManager, TAG)
    }

    fun getNextItem() {
        try {
            selectedPicklist?.let {
                if (selectedPicklist.size > 0) {
                    if (currentItemPosition < selectedPicklist?.size - 1) {
                        currentItemPosition++
                        currentScannedItem = selectedPicklist?.get(currentItemPosition)
                        setUIValues()
                    }

//                if (selectedPicklist.size - skippedQty == scannedItemsList.size) {
//                    transferEnabled = true
//                }
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun setUIValues() {
        currentScannedItem?.let {
            currentDefaultSourceBin = currentScannedItem?.sourceBin
            currentDefaultDestinationBin = currentScannedItem?.destinationBin
            currentItemCode = currentScannedItem?.item
            fragmentPicklistTransferBinding?.tvSourceBin?.setText(currentDefaultSourceBin)
            fragmentPicklistTransferBinding?.edtSourceBin?.setText(currentDefaultSourceBin)
            fragmentPicklistTransferBinding?.tvDestinationBin?.setText(
                currentDefaultDestinationBin
            )
            fragmentPicklistTransferBinding?.edtDestinationBin?.setText(
                currentDefaultDestinationBin
            )
            fragmentPicklistTransferBinding?.tvItemCode?.setText(currentItemCode)
            fragmentPicklistTransferBinding?.edtItemCode?.setText("")
            fragmentPicklistTransferBinding?.edtItemCode?.requestFocus()
            setImage()
        }
    }

    private fun scanBinApi(barcode: String, storeCode: String) {
        picklistViewModel.scanBinPicklist(storeCode, barcode)
        picklistViewModel.scanBinPicklistResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.displayMessage != null) {

                            if (isScanningSourceBin) {
                                val binbarcode =
                                    fragmentPicklistTransferBinding?.edtSourceBin?.text?.toString()
                                currentScannedItem?.newSourceBin = binbarcode
                                fragmentPicklistTransferBinding?.tvSourceBin?.text = binbarcode
                                fragmentPicklistTransferBinding?.edtItemCode?.clearFocus()
                                fragmentPicklistTransferBinding?.edtItemCode?.requestFocus()
                            } else if (isScanningDestinationBin) {
//                                movingToNextItemUpdateValues()
                                val binbarcode =
                                    fragmentPicklistTransferBinding?.edtDestinationBin?.text?.toString()
                                currentScannedItem?.newDestinationBin = binbarcode
                                fragmentPicklistTransferBinding?.tvDestinationBin?.text = binbarcode
                                fragmentPicklistTransferBinding?.edtItemCode?.clearFocus()
                                fragmentPicklistTransferBinding?.edtItemCode?.requestFocus()
                            }
                        }
                    }

                    picklistViewModel?.scanBinPicklistResponse?.value = null
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
                    picklistViewModel?.scanBinPicklistResponse?.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }


    private fun processScannedItem() {
        try {
            val enteredItemcode = fragmentPicklistTransferBinding?.edtItemCode?.text?.toString()?.trim()
            var isItemScanned = false
            for (item in selectedPicklist) {
                if (enteredItemcode == item?.itemBarcode
                    || enteredItemcode == item?.item
                ) {
                    isItemScanned = true
                    break
                }
            }
            if (!isItemScanned) {
                Toast.makeText(requireContext(), "Item could not verified", Toast.LENGTH_SHORT)
                    .show()
                fragmentPicklistTransferBinding?.horizontalScrollView?.scrollX = 0
                fragmentPicklistTransferBinding?.edtItemCode?.clearFocus()
                fragmentPicklistTransferBinding?.edtItemCode?.requestFocus()
            } else {
                totalScannedQty++
                fragmentPicklistTransferBinding?.tvTotalScannedQty?.text =
                    "$totalScannedQty"
                currentScannedItem?.scannedQty = 1
                itemIdScanHashMap.put(currentScannedItem?.id!!, true)
                scannedItemsList.add(currentScannedItem!!)
                settingRecyclerview(scannedItemsList)
                fragmentPicklistTransferBinding?.edtItemCode?.setText("")
                getNextItem()
            }

            checkIfAllItemsScanned()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun settingRecyclerview(list: ArrayList<PicklistDetailsResponseModel.PickHeader.PickDetails>) {
        list.removeIf { it.stockAvailInSourceBin!! <= 0 && it.isSkipped == 0 }

        picklistItemRecyclerviewAdapter = PicklistItemRecyclerviewAdapter(list, requireContext())

        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragmentPicklistTransferBinding?.recyclerView?.layoutManager = manager
        fragmentPicklistTransferBinding?.recyclerView?.adapter = picklistItemRecyclerviewAdapter

//        picklistItemRecyclerviewAdapter?.onDeleteClick = { item, position ->
//            if (scannedItemsList?.size > 0) {
//                scannedItemsList?.removeAt(position)
//                picklistItemRecyclerviewAdapter?.notifyDataSetChanged()
//                totalScannedQty -= 1
//                fragmentPicklistTransferBinding?.tvTotalScannedQty?.text = "$totalScannedQty"
//            }
//
//        }
    }


    fun picklistTransferAPi(
        picklistTransferRequest: PicklistTransferRequest
    ) {
        picklistViewModel.picklistBintransfer(picklistTransferRequest)
        picklistViewModel.picklistBintransferResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        var itemTransferred = 0
                        var notTransferredMessage = ""
                        if (response.binLogList?.size!! > 0) {
                            for (binLog in response.binLogList) {
                                if (binLog.status.equals("Success"))
                                    itemTransferred++
                                else
                                    notTransferredMessage =
                                        "Item ${binLog.skuCode} From ${binLog.fromBinCode} To ${binLog.toBinCode}\n"

                            }
                        }
                        if (itemTransferred == response.binLogList?.size) {
                            mainActivity?.showMessage(response.displayMessage!!)
                            mainActivity?.navController?.navigate(R.id.navigation_picklist)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Following Item Transfer  Failed :$notTransferredMessage",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }
                    picklistViewModel?.picklistBintransferResponse?.value = null
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
                    picklistViewModel?.picklistBintransferResponse?.value = null

                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }


    fun getReasonList() {
        picklistViewModel.getSkipReasonList()
        picklistViewModel.skipReasonListResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        reasonList =
                            response.skipReasonsList as ArrayList<SkipReasonListResponse.SkipReasons>
                    }
                    picklistViewModel?.skipReasonListResponse?.value = null
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)

                    it.message?.let { message ->
                        Log.e(TAG, "Error: $message")
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                    picklistViewModel?.skipReasonListResponse?.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    fun skipItemPicklistTransfer(
        skipPicklistTransferRequest: SkipPicklistTransferRequest
    ) {
        picklistViewModel.skipPicklistTransfer(skipPicklistTransferRequest)
        picklistViewModel.skipPicklistTransferResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.statusCode == 1) {
                            if (response.sourceBin == null || response.sourceBin.isEmpty()) {
                                Toast.makeText(
                                    requireContext(),
                                    response.displayMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                                skippedQty++
                                fragmentPicklistTransferBinding?.tvTotalSkippedQty?.text =
                                    "$skippedQty"
                                // Moving to next item
                                try {
                                    getNextItem()
//                                selectedPicklist?.removeAt(currentItemPosition)
//                                    currentItemPosition++
//                                    if (currentItemPosition <= selectedPicklist?.size!! - 1)
//                                        currentScannedItem =
//                                            selectedPicklist?.get(currentItemPosition)
//                                    fragmentPicklistTransferBinding?.tvSourceBin?.text =
//                                        currentScannedItem?.sourceBin
//                                    fragmentPicklistTransferBinding?.edtSourceBin?.setText(
//                                        currentScannedItem?.sourceBin
//                                    )
//                                    fragmentPicklistTransferBinding?.edtDestinationBin?.setText(
//                                        currentScannedItem?.destinationBin
//                                    )
//                                    fragmentPicklistTransferBinding?.tvDestinationBin?.text =
//                                        currentScannedItem?.destinationBin
//                                    fragmentPicklistTransferBinding?.tvItemCode?.text =
//                                        currentScannedItem?.item
//                                    fragmentPicklistTransferBinding?.edtItemCode?.setText("")
//                                    fragmentPicklistTransferBinding?.edtItemCode?.requestFocus()
//                                    setImage()
                                } catch (exception: Exception) {
                                    exception.printStackTrace()
                                }
                            } else {
//                                Toast.makeText(
//                                    requireContext(),
//                                    "Please scan the updated source bin",
//                                    Toast.LENGTH_SHORT
//                                ).show()
                                fragmentPicklistTransferBinding?.edtSourceBin?.setText(response.sourceBin)
                                currentScannedItem?.newSourceBin = response.sourceBin
                                fragmentPicklistTransferBinding?.tvSourceBin?.setText(response.sourceBin)
                            }

                        }

                    }
                    checkIfAllItemsScanned()
                    picklistViewModel?.skipPicklistTransferResponse?.value = null
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
                    picklistViewModel?.skipPicklistTransferResponse?.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    private fun setImage() {
        try {
            var itemcode = currentScannedItem?.item
            val departmentCode = itemcode?.substringBefore("-")?.trim()
            itemcode = itemcode?.replace(departmentCode + "-", "")?.trim()
            val productCode = itemcode?.substringBefore("-")?.trim()
            getImage(departmentCode!!, productCode!!, sessionToken)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun getImage(
        departmentCode: String, productCode: String, sessionToken: String
    ) {
        picklistViewModel.getImage(departmentCode, productCode, sessionToken)
        picklistViewModel.getImageResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        viewItemImage(response.Image_String_base_64)
                    }
                    picklistViewModel?.getImageResponse?.value = null
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    if (it.statusCode == 401) {
                        Toast.makeText(
                            requireContext(),
                            "Your session token expired. Please logout and login again",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else
                        it.message?.let { message ->
                            Log.e(TAG, "Error: $message")
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                    picklistViewModel?.getImageResponse?.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    private fun viewItemImage(imageStringBase64: String?) {
        var base64String = imageStringBase64
        if (base64String!!.contains("data:image/jpeg;base64")) {
            base64String = base64String.replace("data:image/jpeg;base64,", "")
        }
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        binding.imageButton?.visibility = View.VISIBLE
        binding.imageButton?.setImageBitmap(bitmap)
    }

    override fun OnReasonSelected(selectedReason: SkipReasonListResponse.SkipReasons) {
        if (selectedReason.skipReasonCode.equals("AVD")) {
            skippedQty++
            fragmentPicklistTransferBinding?.tvTotalSkippedQty?.text = "$skippedQty"
            getNextItem()
            checkIfAllItemsScanned()
        } else {
            val skipPicklistTransferRequest = SkipPicklistTransferRequest(
                currentScannedItem?.newDestinationBin,
                currentScannedItem?.item,
                "",
                currentScannedItem?.id,
                selectedReason.skipReasonCode,
                userId?.toInt(),
                currentScannedItem?.newSourceBin
            )
            skipItemPicklistTransfer(skipPicklistTransferRequest!!)
        }

    }


    fun checkItemInBin(
        storeCode: String,
        bincode: String,
        itemCode: String
    ) {
        picklistViewModel.checkIteminBin(
            storeCode.trim(), bincode.trim(), itemCode.trim()
        )
        picklistViewModel.checkIteminBinResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.statusCode == 1) {
                            var skuCode = ""
                            val binList = response.binDetailsList
                            var qty = 0
                            for (binItem in binList!!) {
                                qty = binItem.quantity!!
                                skuCode = binItem.skuCode!!
                            }

                            val currentBinCode =
                                fragmentPicklistTransferBinding?.edtSourceBin?.text?.toString()
                            var inStockQty = 0
                            var scannedSkuQty = 0
                            // Getting the instock qty by checking scanned qty in app side and total qty coming from server
                            if (sourceBinitemQtyHashMap.containsKey(currentBinCode)) {
                                val itemList =
                                    sourceBinitemQtyHashMap.get(currentBinCode) as ArrayList<ScannedItem>
                                var hasSkuCodeFound = false
                                for (item in itemList) {
                                    if (skuCode.equals(item.skuCode)) {
                                        inStockQty = qty - item.scannedQty
                                        hasSkuCodeFound = true
                                        scannedSkuQty = item.scannedQty
                                        break
                                    }
                                }
                                if (!hasSkuCodeFound) {
                                    inStockQty = qty
                                }
                            } else
                                inStockQty = qty

                            if (inStockQty < 0)
                                inStockQty = 0

                            if (inStockQty!! <= 0) {
                                Toast.makeText(
                                    requireContext(),
                                    "Item not in stock in the current source bin",
                                    Toast.LENGTH_SHORT
                                ).show()

                                fragmentPicklistTransferBinding?.edtItemCode?.setText("")
                                fragmentPicklistTransferBinding?.edtSourceBin?.clearFocus()
                                fragmentPicklistTransferBinding?.edtSourceBin?.requestFocus()
                            } else {
                                currentScannedItem?.stockAvailInSourceBin = inStockQty
                                //Updating item qty at binlevel to Hashmap
                                try {
                                    var itemList = ArrayList<ScannedItem>()
                                    if (sourceBinitemQtyHashMap.containsKey(currentBinCode)) {
                                        val updatedScannedQty = scannedSkuQty + 1
                                        itemList =
                                            sourceBinitemQtyHashMap.get(currentBinCode) as ArrayList<ScannedItem>
                                        for (item in itemList) {
                                            if (skuCode.equals(item.skuCode)) {
                                                item.scannedQty = updatedScannedQty
                                            }
                                        }

                                    } else
                                        itemList.add(ScannedItem(skuCode, 1))
                                    sourceBinitemQtyHashMap.put(currentBinCode!!, itemList)
                                } catch (exception: Exception) {
                                    exception.printStackTrace()
                                }
                                processScannedItem()
                            }


                        }
                    }
                    picklistViewModel.checkIteminBinResponse.value = null
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    fragmentPicklistTransferBinding?.edtItemCode?.clearFocus()
                    fragmentPicklistTransferBinding?.edtItemCode?.requestFocus()
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
                    picklistViewModel.checkIteminBinResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    fun getPicklistDetails(
        isStatus: String,
        storeCode: String,
        picklistHeaderId: String
    ) {
        picklistViewModel.getPickListDetails(
            isStatus, storeCode, picklistHeaderId
        )
        picklistViewModel.picklistDetailsResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    selectedPicklist = it?.data?.pickListHeader?.get(0)?.pickListDetails!!
                    selectedPicklist.removeIf {
                        it.sourceBin == null || it.sourceBin.isEmpty() || it.sourceBin.equals(
                            null
                        )
                    }
                    selectedPicklist.sortBy { it?.visualOrder }
                    for (item in selectedPicklist) {
                        item.newSourceBin = item.sourceBin
                        item.newDestinationBin = item.destinationBin
                    }
                    fragmentPicklistTransferBinding?.tvTotalQty?.text = "${selectedPicklist.size}"
                    getNextItem()
                    picklistViewModel.picklistDetailsResponse.value = null
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
                    picklistViewModel.picklistDetailsResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    fun checkIfAllItemsScanned(): Boolean {
        if (selectedPicklist.size - skippedQty == scannedItemsList.size) {
            fragmentPicklistTransferBinding?.edtSourceBin?.setText("")
            fragmentPicklistTransferBinding?.edtDestinationBin?.setText("")
            fragmentPicklistTransferBinding?.edtItemCode?.setText("")
            fragmentPicklistTransferBinding?.tvSourceBin?.setText("")
            fragmentPicklistTransferBinding?.tvDestinationBin?.setText("")
            fragmentPicklistTransferBinding?.tvItemCode?.setText("")
            return true
        }

        return false
    }
}