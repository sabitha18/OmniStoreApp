package com.armada.storeapp.ui.home.others.item_search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.ItemBinSearchResponse
import com.armada.storeapp.databinding.FragmentItemSearchBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.others.item_search.adapter.ItemBincodeRecyclerviewAdapter
import com.armada.storeapp.ui.utils.ConnectionDetector
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class ItemSearchFragment : Fragment() {
    private var TAG = ItemSearchFragment::class.java.simpleName
    var cd: ConnectionDetector? = null
    private var fragmentItemSearchBinding: FragmentItemSearchBinding? = null
    private var mainActivity: MainActivity? = null
    private val binding get() = fragmentItemSearchBinding!!
    lateinit var itemSearchViewModel: ItemSearchViewModel
    var storeCode = ""
    lateinit var itemBincodeRecyclerviewAdapter: ItemBincodeRecyclerviewAdapter
    var isUsingScanningDevice = true
    private var currentItemEditTextLength = 0
    var isItemSearch = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentItemSearchBinding =
            FragmentItemSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initializeData()

        return root
    }


    private fun initializeData() {

        mainActivity = activity as MainActivity
        itemSearchViewModel =
            ViewModelProvider(this).get(ItemSearchViewModel::class.java)

        cd = ConnectionDetector(activity)
        storeCode = SharedpreferenceHandler(requireContext()).getData(
            SharedpreferenceHandler.STORE_CODE,
            ""
        )!!

        fragmentItemSearchBinding?.btnSearch?.setOnClickListener {
            mainActivity?.hideSoftKeyboard()
            callSearchApi()
        }
        mainActivity?.BackPressed(this)
        setEditTextListeners()
    }

    private fun callSearchApi() {

        try {
            val barcode = fragmentItemSearchBinding?.edtBarcode?.text?.toString()
            if (barcode.equals("")) {
                Toast.makeText(
                    requireContext(),
                    "Please enter barcode",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (isItemSearch)
                    itemBinSearch(storeCode, barcode!!, "")
                else
                    itemBinSearch(storeCode, "", barcode!!)
            }


        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun setEditTextListeners() {
        fragmentItemSearchBinding?.edtBarcode?.setSelectAllOnFocus(true)
        fragmentItemSearchBinding?.edtBarcode?.requestFocus()
        fragmentItemSearchBinding?.radioGroup?.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            if (checkedId.equals(fragmentItemSearchBinding?.radioItemCode?.id)) {
                isItemSearch = true
            } else if (checkedId.equals(fragmentItemSearchBinding?.radioBincode?.id)) {
                isItemSearch = false
            }
        })
        fragmentItemSearchBinding?.edtBarcode?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                isUsingScanningDevice = true
            }
        }

        fragmentItemSearchBinding?.edtBarcode?.addTextChangedListener(object : TextWatcher {
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
                        callSearchApi()
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
//                fragmentItemSearchBinding?.edtBarcode?.setText(barcode)
//                checkbinInventory(storeCode, barcode)
//            }
//        }

//    fun launchScanner() {
//        val i = Intent(mainActivity, CamActivity::class.java)
//        getContent.launch(i)
//    }

    fun itemBinSearch(
        storeCode: String, itemcode: String, bincode: String
    ) {
        mainActivity?.hideSoftKeyboard()
        itemSearchViewModel.itemOrBinSearch("0", "0", "1", itemcode, bincode, storeCode)
        itemSearchViewModel.itemBinSearchResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    mainActivity?.showProgressBar(false)
                    it.data?.let { response ->
                        if (response.statusCode == 1) {
                            if (response.binLogDetailsList == null) {
                                Toast.makeText(
                                    requireContext(),
                                    "This item does not belong to any bin location",
                                    Toast.LENGTH_LONG
                                ).show()
                                fragmentItemSearchBinding?.edtBarcode?.setText("")
                                fragmentItemSearchBinding?.edtBarcode?.requestFocus()
                                fragmentItemSearchBinding?.cvBinlist?.visibility = View.GONE
                            } else {
                                try {
                                    response.binLogDetailsList.removeIf { it.quantity?.toInt()!! <= 0 || it.fromBinCode!!.isEmpty() }
                                    settingRecyclerview(response.binLogDetailsList, isItemSearch)
                                    fragmentItemSearchBinding?.edtBarcode?.setText("")
                                    fragmentItemSearchBinding?.edtBarcode?.requestFocus()
                                    var totalQty = 0
                                    response.binLogDetailsList.forEach {
                                        totalQty = totalQty + it.quantity!!
                                    }
                                    fragmentItemSearchBinding?.tvTotalQty?.text = "$totalQty"
                                } catch (exception: Exception) {
                                    exception.printStackTrace()
                                    fragmentItemSearchBinding?.cvBinlist?.visibility = View.GONE
                                }
                            }


                        } else {
                            fragmentItemSearchBinding?.cvBinlist?.visibility = View.GONE
                        }

                    }
                    itemSearchViewModel.itemBinSearchResponse.value = null
                }
                is Resource.Error -> {
                    mainActivity?.showProgressBar(false)
                    fragmentItemSearchBinding?.edtBarcode?.clearFocus()
                    fragmentItemSearchBinding?.edtBarcode?.requestFocus()
                    val bincodelist = ArrayList<ItemBinSearchResponse.BinLogDetails>()
                    settingRecyclerview(bincodelist, isItemSearch)
                    fragmentItemSearchBinding?.cvBinlist?.visibility = View.GONE
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
                    itemSearchViewModel.itemBinSearchResponse.value = null
                }
                is Resource.Loading -> {
                    mainActivity?.showProgressBar(true)
                }
            }
        }
    }

    private fun settingRecyclerview(
        list: ArrayList<ItemBinSearchResponse.BinLogDetails>,
        isItemSearch: Boolean
    ) {
        fragmentItemSearchBinding?.cvBinlist?.visibility = View.VISIBLE
        itemBincodeRecyclerviewAdapter =
            ItemBincodeRecyclerviewAdapter(list!!, requireContext(), isItemSearch)

        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragmentItemSearchBinding?.recyclerView?.layoutManager = manager
        fragmentItemSearchBinding?.recyclerView?.adapter = itemBincodeRecyclerviewAdapter
    }
}