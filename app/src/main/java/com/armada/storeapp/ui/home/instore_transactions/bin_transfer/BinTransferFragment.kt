package com.armada.storeapp.ui.home.instore_transactions.bin_transfer

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
import com.armada.storeapp.data.model.request.PicklistTransferRequest
import com.armada.storeapp.databinding.FragmentBinTransferBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.utils.ConnectionDetector
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class BinTransferFragment : Fragment() {
    private var userId = ""
    private var hasEnabledDefaultSourceBin: Boolean = false
    private var hasEnabledDefaultDestinationBin: Boolean = false
    private var TAG = BinTransferFragment::class.java.simpleName
    var cd: ConnectionDetector? = null
    private var fragmentBinTransferBinding: FragmentBinTransferBinding? = null
    private var mainActivity: MainActivity? = null
    private val binding get() = fragmentBinTransferBinding!!
    lateinit var binTransferViewModel: BinTransferViewModel
    var storeCode = ""
    var currentItemcode = ""
    var isScanningSourcebin = false
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    var isUsingScanningDevice = true
    private var currentSourceBinEditTextLength = 0
    private var currentDestinationBinEditTextLength = 0
    private var currentItemEditTextLength = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentBinTransferBinding =
            FragmentBinTransferBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initializeData()

        return root
    }


    private fun initializeData() {

        mainActivity = activity as MainActivity
        binTransferViewModel =
            ViewModelProvider(this).get(BinTransferViewModel::class.java)

        cd = ConnectionDetector(activity)
        sharedpreferenceHandler = SharedpreferenceHandler(requireContext())
        storeCode = sharedpreferenceHandler?.getData(
            SharedpreferenceHandler.STORE_CODE,
            ""
        )!!
        userId =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.LOGIN_USER_ID, 0)!!?.toString()
        fragmentBinTransferBinding?.imageButtonSourceBinSubmit?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            isScanningSourcebin = true
            fragmentBinTransferBinding?.edtSourceBin?.clearFocus()
            val sourceBin = fragmentBinTransferBinding?.edtSourceBin?.text?.toString()
            if (sourceBin!!.isEmpty())
                Toast.makeText(requireContext(), "Please enter Source bin", Toast.LENGTH_SHORT)
                    .show()
            else
                validateBin(storeCode, sourceBin)
        }

        fragmentBinTransferBinding?.imageButtonItemSubmit?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            fragmentBinTransferBinding?.edtItemCode?.clearFocus()
            val itemcode = fragmentBinTransferBinding?.edtItemCode?.text?.toString()
            if (itemcode!!.isEmpty())
                Toast.makeText(requireContext(), "Please enter Item code", Toast.LENGTH_SHORT)
                    .show()
            else
                checkbinInventory(storeCode, itemcode)
        }

        fragmentBinTransferBinding?.imageButtonDestinationSubmit?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            isScanningSourcebin = false
            fragmentBinTransferBinding?.edtDestinationBin?.clearFocus()
            val destinationBin = fragmentBinTransferBinding?.edtDestinationBin?.text?.toString()
            if (destinationBin!!.isEmpty())
                Toast.makeText(requireContext(), "Please enter Destination bin", Toast.LENGTH_SHORT)
                    .show()
            else
                validateBin(storeCode, destinationBin)
        }


        fragmentBinTransferBinding?.btnTransfer?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            try {
                val sourceBin = fragmentBinTransferBinding?.edtSourceBin?.text?.toString()
                val destinationBin = fragmentBinTransferBinding?.edtDestinationBin?.text?.toString()
                if (currentItemcode.equals("") || sourceBin.equals("") || destinationBin.equals("")) {
                    Toast.makeText(
                        requireContext(),
                        "Please enter all fields to transfer",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    var storeCode = SharedpreferenceHandler(requireContext()).getData(
                        SharedpreferenceHandler.STORE_CODE,
                        ""
                    )
                    val binLogList = ArrayList<PicklistTransferRequest.BinLog>()
                    val binLog = PicklistTransferRequest.BinLog(
                        destinationBin,
                        sourceBin, destinationBin, sourceBin, 0,
                        0,
                        "1",
                        "BinTransfer",
                        currentItemcode,
                        0
                    )
                    binLogList.add(binLog)
                    var date = ""
                    try {
                        date = mainActivity?.getCurrentDate()!!
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    val picklistTransferRequest =
                        PicklistTransferRequest(binLogList, date, storeCode, userId)
                    binTransferApi(picklistTransferRequest)
                }


            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        mainActivity?.BackPressed(this)
        setEditTextListeners()
        setCheckboxListeners()
    }

    private fun setCheckboxListeners() {
        fragmentBinTransferBinding?.checkBoxSourceBin?.setOnCheckedChangeListener { buttonView, isChecked ->
            hasEnabledDefaultSourceBin = isChecked
        }
        fragmentBinTransferBinding?.checkBoxDestinationBin?.setOnCheckedChangeListener { buttonView, isChecked ->
            hasEnabledDefaultDestinationBin = isChecked
        }
    }

    private fun setEditTextListeners() {
        fragmentBinTransferBinding?.edtSourceBin?.setSelectAllOnFocus(true)
        fragmentBinTransferBinding?.edtItemCode?.setSelectAllOnFocus(true)
        fragmentBinTransferBinding?.edtDestinationBin?.setSelectAllOnFocus(true)
        fragmentBinTransferBinding?.edtSourceBin?.clearFocus()
        fragmentBinTransferBinding?.edtSourceBin?.requestFocus()


        fragmentBinTransferBinding?.edtSourceBin?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                isUsingScanningDevice = true
            }

        }
        fragmentBinTransferBinding?.edtItemCode?.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus) {
                isUsingScanningDevice = true
            }
        }
        fragmentBinTransferBinding?.edtDestinationBin?.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus) {
                isUsingScanningDevice = true
            }
        }
        fragmentBinTransferBinding?.edtSourceBin?.addTextChangedListener(object : TextWatcher {
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
                        isScanningSourcebin = true
                        validateBin(storeCode, text.toString())
                    }
                } else
                    isUsingScanningDevice = true
            }

        })

        fragmentBinTransferBinding?.edtItemCode?.addTextChangedListener(object : TextWatcher {
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
                        checkbinInventory(storeCode, text?.toString())
                    }
                } else
                    isUsingScanningDevice = true
            }

        })

        fragmentBinTransferBinding?.edtDestinationBin?.addTextChangedListener(object : TextWatcher {
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
                if (text?.toString()?.equals("") == false) {
                    if (text?.length == 1) {
                        isUsingScanningDevice = false
                    }
                    if (isUsingScanningDevice) {
                        isScanningSourcebin = false
                        validateBin(storeCode, text.toString())
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
//                    fragmentBinTransferBinding?.edtItemCode?.setText(barcode)
//                    checkbinInventory(storeCode, barcode)
//                } else if (isSourceBinScan) {
//                    fragmentBinTransferBinding?.edtSourceBin?.setText(barcode)
//                    validateBin(storeCode, barcode)
//                } else if (isDestinationBinScan) {
//                    fragmentBinTransferBinding?.edtDestinationBin?.setText(barcode)
//                    validateBin(storeCode, barcode)
//                }
//
//            }
//        }
//
//    fun launchScanner() {
//        val i = Intent(mainActivity, CamActivity::class.java)
//        getContent.launch(i)
//    }

    fun binTransferApi(
        picklistTransferRequest: PicklistTransferRequest
    ) {
        binTransferViewModel.bintransfer(picklistTransferRequest)
        binTransferViewModel.bintransferResponse.observe(viewLifecycleOwner) {
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
                            Toast.makeText(
                                requireContext(),
                                response.displayMessage!!,
                                Toast.LENGTH_SHORT
                            ).show()
                            if (hasEnabledDefaultSourceBin) {
                                fragmentBinTransferBinding?.edtItemCode?.setText("")
                                fragmentBinTransferBinding?.edtItemCode?.clearFocus()
                                fragmentBinTransferBinding?.edtItemCode?.requestFocus()
                            } else {
                                fragmentBinTransferBinding?.edtSourceBin?.setText("")
                                fragmentBinTransferBinding?.edtSourceBin?.clearFocus()
                                fragmentBinTransferBinding?.edtSourceBin?.requestFocus()
                                fragmentBinTransferBinding?.edtItemCode?.setText("")
                            }
                            if (!hasEnabledDefaultDestinationBin) {
                                fragmentBinTransferBinding?.edtDestinationBin?.setText("")
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Following Item Transfer  Failed :$notTransferredMessage",
                                Toast.LENGTH_LONG
                            ).show()
                        }


                    }
                    binTransferViewModel.bintransferResponse.value = null
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
                    binTransferViewModel.bintransferResponse.value = null
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
        binTransferViewModel.validateBin(storeCode, bincode)
        binTransferViewModel.validateBinResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.statusCode == 1) {
                            if (isScanningSourcebin) {
                                fragmentBinTransferBinding?.edtItemCode?.setText("")
                                fragmentBinTransferBinding?.edtItemCode?.clearFocus()
                                fragmentBinTransferBinding?.edtItemCode?.requestFocus()
                            } else {
                                fragmentBinTransferBinding?.btnTransfer?.performClick()
                            }
                        }
                        binTransferViewModel.validateBinResponse.value = null
                    }
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    if (isScanningSourcebin) {
                        fragmentBinTransferBinding?.edtSourceBin?.clearFocus()
                        fragmentBinTransferBinding?.edtSourceBin?.requestFocus()
                    } else {
                        fragmentBinTransferBinding?.edtDestinationBin?.clearFocus()
                        fragmentBinTransferBinding?.edtDestinationBin?.requestFocus()
                    }

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
                    binTransferViewModel.validateBinResponse.value = null
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
        mainActivity?.hideSoftKeyboard()
        binTransferViewModel.checkbinInventory(storeCode, itemcode)
        binTransferViewModel.checkbinInventoryResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.statusCode == 1) {
                            try {
                                currentItemcode = response.skuCode!!
                                val bincode =
                                    fragmentBinTransferBinding?.edtSourceBin?.text?.toString()
                                checkItemInBin(storeCode, bincode!!, currentItemcode)

                            } catch (exception: Exception) {
                                exception.printStackTrace()
                            }
                        }

                    }
                    binTransferViewModel.checkbinInventoryResponse.value = null
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    fragmentBinTransferBinding?.edtItemCode?.clearFocus()
                    fragmentBinTransferBinding?.edtItemCode?.requestFocus()
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
                    binTransferViewModel.checkbinInventoryResponse.value = null
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
        binTransferViewModel.checkIteminBin(
            storeCode, bincode, itemCode
        )
        binTransferViewModel.checkIteminBinResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.statusCode == 1) {
                            val binList = response.binDetailsList
                            var qty = 0
                            for (binItem in binList!!) {
                                qty = binItem.quantity!!
                            }
                            if (qty!! <= 0) {
                                Toast.makeText(
                                    requireContext(),
                                    "Item not in stock in the current source bin",
                                    Toast.LENGTH_SHORT
                                ).show()
                                fragmentBinTransferBinding?.edtItemCode?.clearFocus()
                                fragmentBinTransferBinding?.edtItemCode?.requestFocus()
                            } else {
                                if (hasEnabledDefaultDestinationBin) {
                                    fragmentBinTransferBinding?.btnTransfer?.performClick()
                                } else {
                                    fragmentBinTransferBinding?.edtDestinationBin?.setText("")
                                    fragmentBinTransferBinding?.edtDestinationBin?.clearFocus()
                                    fragmentBinTransferBinding?.edtDestinationBin?.requestFocus()
                                }
                            }

                        } else {
                            fragmentBinTransferBinding?.edtItemCode?.clearFocus()
                            fragmentBinTransferBinding?.edtItemCode?.requestFocus()
                        }
                    }
                    binTransferViewModel.checkIteminBinResponse.value = null
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    fragmentBinTransferBinding?.edtItemCode?.clearFocus()
                    fragmentBinTransferBinding?.edtItemCode?.requestFocus()
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
                    binTransferViewModel.checkIteminBinResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

}