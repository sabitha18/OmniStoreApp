package com.armada.storeapp.ui.home.instore_transactions.inventory

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
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.CommonBinTransferRequest
import com.armada.storeapp.databinding.FragmentInventoryBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.utils.ConnectionDetector
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class InventoryFragment : Fragment() {
    private var totalScannedQty: Int = 0
    private var TAG = InventoryFragment::class.java.simpleName
    var cd: ConnectionDetector? = null
    private var fragmentInventoryBinding: FragmentInventoryBinding? = null
    private var mainActivity: MainActivity? = null
    private val binding get() = fragmentInventoryBinding!!
    lateinit var inventoryViewModel: InventoryViewModel
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler

    //    var sessionToken = ""
    var userId = 0
    var storeCode = ""
    var currentBincode = ""
    var hasVaildatedBin = false
    var hasValidatedItem = false
    var scanItemList = ArrayList<CommonBinTransferRequest.BinLog>()
    var itemHashmap = HashMap<String, ArrayList<CommonBinTransferRequest.BinLog>>()
    var itemQtyHashmap = HashMap<String, Int>()
    var isUsingScanningDevice = true
    private var currentBincodeEdittextLength = 0
    private var currentItemEditTextLength = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentInventoryBinding =
            FragmentInventoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initializeData()

        return root
    }


    private fun initializeData() {

        mainActivity = activity as MainActivity
        inventoryViewModel =
            ViewModelProvider(this).get(InventoryViewModel::class.java)

        cd = ConnectionDetector(activity)
//        sessionToken = "Bearer " + SharedpreferenceHandler(requireContext()).getData(
//            SharedpreferenceHandler.ACCESS_TOKEN,
//            ""
//        )!!
        sharedpreferenceHandler = SharedpreferenceHandler(requireContext())
        userId = sharedpreferenceHandler.getData(SharedpreferenceHandler.LOGIN_USER_ID, 0)!!
        storeCode = sharedpreferenceHandler.getData(
            SharedpreferenceHandler.STORE_CODE,
            ""
        )!!

        mainActivity?.BackPressed(this)

        fragmentInventoryBinding?.imageButtonBinSubmit?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            currentBincode = fragmentInventoryBinding?.edtBin?.text?.toString()!!
            if (currentBincode.equals(""))
                Toast.makeText(requireContext(), "Please enter bincode", Toast.LENGTH_SHORT).show()
            else {
                hasVaildatedBin = true
                validateBin(storeCode, currentBincode)
            }

        }

        fragmentInventoryBinding?.imageButtonItemSubmit?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            val itemcode = fragmentInventoryBinding?.edtItemCode?.text?.toString()
            if (itemcode.equals(""))
                Toast.makeText(requireContext(), "Please enter Itemcode", Toast.LENGTH_SHORT).show()
            else {
                hasValidatedItem = true
                checkbinInventory(storeCode, itemcode!!)
            }

        }

        fragmentInventoryBinding?.btnFinish?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            val totalScannedQty = fragmentInventoryBinding?.tvScannedQty?.text?.toString()?.toInt()
            if (totalScannedQty == 0 || !hasValidatedItem) {
                Toast.makeText(
                    requireContext(),
                    "Please scan or validate item to proceed",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val binLogList = ArrayList<CommonBinTransferRequest.BinLog>()
                itemHashmap.forEach { (key, value) ->
                    val itemList = value
                    for (item in itemList) {
                        item.binCode = key
                    }
                    binLogList.addAll(itemList)
                }
                try {
                    val commonBinTransferRequest =
                        CommonBinTransferRequest(binLogList, userId, storeCode)
                    binTransferApi(commonBinTransferRequest)

                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }

        }

        setEditTextListeners()
    }

    private fun setEditTextListeners() {
        fragmentInventoryBinding?.edtBin?.setSelectAllOnFocus(true)
        fragmentInventoryBinding?.edtItemCode?.setSelectAllOnFocus(true)
        fragmentInventoryBinding?.edtBin?.requestFocus()

        fragmentInventoryBinding?.edtBin?.addTextChangedListener(object : TextWatcher {
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
                if (text?.toString()?.equals("") == false) {
                    hasVaildatedBin = false
                    if (text?.length == 1) {
                        isUsingScanningDevice = false
                    }
                    if (isUsingScanningDevice) {
                        hasVaildatedBin = true
                        validateBin(storeCode, text.toString())
                    }
                } else
                    isUsingScanningDevice = true


            }

        })

        fragmentInventoryBinding?.edtItemCode?.addTextChangedListener(object : TextWatcher {
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
                    hasValidatedItem = false
                    if (!hasVaildatedBin) {
                        fragmentInventoryBinding?.edtItemCode?.setText("")
                        fragmentInventoryBinding?.edtBin?.requestFocus()
                    }
                    if (text?.length == 1) {
                        isUsingScanningDevice = false
                    }
                    if (isUsingScanningDevice) {
                        hasValidatedItem = true
                        checkbinInventory(storeCode, text.toString())
                    }
                } else
                    isUsingScanningDevice = true

            }

        })

        fragmentInventoryBinding?.edtBin?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                isUsingScanningDevice = true
            } else {
                val bincode = fragmentInventoryBinding?.edtBin?.text?.toString()
                if (!hasVaildatedBin && !bincode?.isEmpty()!!)
                    Toast.makeText(requireContext(), "Please validate the bin", Toast.LENGTH_SHORT)
                        .show()
            }

        }
        fragmentInventoryBinding?.edtItemCode?.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus) {
                isUsingScanningDevice = true
            } else {
                val itemcode = fragmentInventoryBinding?.edtItemCode?.text?.toString()
                if (!hasValidatedItem && !itemcode?.isEmpty()!!)
                    Toast.makeText(requireContext(), "Please validate the Item", Toast.LENGTH_SHORT)
                        .show()
            }
        }

    }

    fun binTransferApi(
        commonBinTransferRequest: CommonBinTransferRequest
    ) {
        inventoryViewModel.commonBinTransfer(commonBinTransferRequest)
        inventoryViewModel.commonBintransferResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.statusCode == 1) {
                            Toast.makeText(
                                requireContext(),
                                response.displayMessage!!,
                                Toast.LENGTH_SHORT
                            ).show()
//                            mainActivity?.showMessage(response.displayMessage!!)
                            fragmentInventoryBinding?.edtBin?.setText("")
                            fragmentInventoryBinding?.edtItemCode?.setText("")
                            fragmentInventoryBinding?.tvScannedQty?.setText("0")
                            totalScannedQty = 0
                            fragmentInventoryBinding?.edtBin?.requestFocus()
                        }
                    }
                    inventoryViewModel.commonBintransferResponse.value = null
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
                    inventoryViewModel.commonBintransferResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    fun validateBin(
        storeCode: String, bincode: String
    ) {
        mainActivity?.hideSoftKeyboard()
        inventoryViewModel.validateBin(storeCode, bincode)
        inventoryViewModel.validateBinResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.statusCode == 1) {
                            fragmentInventoryBinding?.edtItemCode?.requestFocus()
                            scanItemList = ArrayList()
                            hasVaildatedBin = true
                            currentBincode =
                                fragmentInventoryBinding?.edtBin?.text?.toString()!!
                        }
                        inventoryViewModel.validateBinResponse.value = null
                    }
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    fragmentInventoryBinding?.edtBin?.clearFocus()
                    fragmentInventoryBinding?.edtBin?.requestFocus()
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
                    inventoryViewModel.validateBinResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    fun checkbinInventory(
        storeCode: String, itemcode: String
    ) {
        inventoryViewModel.checkbinInventory(storeCode, itemcode)
        inventoryViewModel.checkbinInventoryResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.statusCode == 1) {
                            try {

                                if (response.transferQuantity!! > 0) {
                                    if (itemQtyHashmap.containsKey(response.skuCode)) {
                                        //Updating existing item
                                        var itemQty = itemQtyHashmap.get(response.skuCode)
                                        if (itemQty!! < response.transferQuantity!!) {
                                            for (item in scanItemList) {
                                                if (item.ItemCode.equals(response.skuCode)) {
                                                    try {
                                                        item.Quantity = item.Quantity!! + 1
                                                        itemQtyHashmap.put(
                                                            response.skuCode!!,
                                                            item.Quantity!!
                                                        )
                                                    } catch (exception: Exception) {
                                                        exception.printStackTrace()
                                                    }
                                                }
                                            }
                                            totalScannedQty++
                                            fragmentInventoryBinding?.edtItemCode?.setText("")
                                            fragmentInventoryBinding?.edtItemCode?.requestFocus()
                                        } else {
                                            Toast.makeText(
                                                requireContext(),
                                                "Item transfer quantity limit reached",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            fragmentInventoryBinding?.edtItemCode?.clearFocus()
                                            fragmentInventoryBinding?.edtItemCode?.requestFocus()
                                        }
                                    } else {
                                        //Adding new item
                                        scanItemList.add(
                                            CommonBinTransferRequest.BinLog(
                                                response.skuCode,
                                                1,
                                                currentBincode
                                            )
                                        )
                                        itemQtyHashmap.put(response.skuCode!!, 1)
                                        totalScannedQty++
                                        fragmentInventoryBinding?.edtItemCode?.setText("")
                                        fragmentInventoryBinding?.edtItemCode?.requestFocus()
                                    }

                                } else {
                                    val code =
                                        fragmentInventoryBinding?.edtItemCode?.text?.toString()
                                    Toast.makeText(
                                        requireContext(),
                                        "$code no stock",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    fragmentInventoryBinding?.edtItemCode?.clearFocus()
                                    fragmentInventoryBinding?.edtItemCode?.requestFocus()
                                }

                                hasValidatedItem = true
                                itemHashmap.put(currentBincode, scanItemList)
                                fragmentInventoryBinding?.tvScannedQty?.text =
                                    "$totalScannedQty"

                            } catch (exception: Exception) {
                                exception.printStackTrace()
                            }
                        }

                    }
                    inventoryViewModel.checkbinInventoryResponse.value = null
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    fragmentInventoryBinding?.edtItemCode?.clearFocus()
                    fragmentInventoryBinding?.edtItemCode?.requestFocus()
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
                    inventoryViewModel.checkbinInventoryResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

}