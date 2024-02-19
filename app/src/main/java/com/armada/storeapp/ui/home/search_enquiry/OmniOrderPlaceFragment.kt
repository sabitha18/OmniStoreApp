package com.armada.storeapp.ui.home.search_enquiry

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.*
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.databinding.FragmentOmniOrderPlaceBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.OrderPlaceViewModel
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.adapter.EmployeeSpinnerAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.adapter.OmniItemAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.adapter.PickupTimeSpinnerAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.select_store.SelectStoreActivity
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.armada.storeapp.ui.utils.Utils
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_omni_order_place.checkBox_Enable_Payment
import kotlinx.android.synthetic.main.fragment_omni_order_place.edtApprovalNo
import kotlinx.android.synthetic.main.fragment_omni_order_place.edtCardAmount
import kotlinx.android.synthetic.main.fragment_omni_order_place.edtCashAmount
import kotlinx.android.synthetic.main.fragment_omni_order_place.lin_approvalno
import kotlinx.android.synthetic.main.fragment_omni_order_place.view.*
import org.json.JSONObject
import java.util.*


@AndroidEntryPoint
class OmniOrderPlaceFragment : Fragment() {

    private var loading: Dialog? = null
    lateinit var binding: FragmentOmniOrderPlaceBinding
    lateinit var orderPlaceViewModel: OrderPlaceViewModel
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    var selectedEmployee: EmployeeMaster? = null
    var selectedDeliveryMethod = ""
    var itemList = ArrayList<SkuMasterTypes>()
    var totalItems = 0
    var accessToken = ""
    var totalQty = 0
    var totalPrice = 0.0
    private var mainActivity: MainActivity? = null
    val skuQtyMap = HashMap<String, Int>()
    val skuList = ArrayList<String>()
    var selectedStore: OmniStoreStockCheckResponse? = null
    var customeCode = ""
    var addCustomerFragment: OmniAddCustomerFragment? = null
    var storeCode = ""
    var storeId = 0
    var selectedCurrency = ""
    var omniOrderInvoice: OmniInvoiceResponse? = null
    var pickupTimeList = ArrayList<TimeSlot>()
    var storeEmployessList = ArrayList<EmployeeMaster>()
    var shipmentType = ""
    var pickupDate = ""
    var pickupTime = ""
    var selectedCustomer: CustomerMasterData? = null
    var sendPaymentLink = false
    lateinit var omniItemAdapter : OmniItemAdapter

    var cashdouble = 0.0
    var carddouble = 0.0
    var approvalno = ""


    var createdbyloc = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOmniOrderPlaceBinding.inflate(inflater)
        orderPlaceViewModel =
            ViewModelProvider(this).get(OrderPlaceViewModel::class.java)
        mainActivity = activity as MainActivity
        sharedpreferenceHandler = SharedpreferenceHandler(mainActivity!!)
        storeCode = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_CODE, "")!!
        storeId = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_ID, 0)!!
        selectedCurrency =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_CURRENCY, "USD")!!
        init()

        Log.d(
            "openpage,",
            "Page fragment"
        )
        setClickListeners()
        return binding.root
    }

    fun init() {
        arguments?.let {
            if (arguments!!.containsKey("delivery_method"))
                selectedDeliveryMethod = arguments!!.getString("delivery_method")!!
            if (arguments!!.containsKey("items")) {
                itemList =
                    arguments!!.getSerializable("items") as ArrayList<SkuMasterTypes> /* = java.util.ArrayList<com.armada.storeapp.data.model.response.SkuMasterTypes> */
            }
        }

        pickupDate = getCurrentDate()
        processData()
        getSalesEmployees()
        mainActivity?.BackPressed(this)
    }

    fun processData() {
        when (selectedDeliveryMethod) {
            "STOREPICKUP" -> {
                binding.txtStorePickup.visibility = View.VISIBLE
                binding.txtHomeDelivery.visibility = View.GONE
                getTimeSlot()

            }
            "HOME DELIVERY" -> {
                binding.txtStorePickup.visibility = View.GONE
                binding.txtHomeDelivery.visibility = View.VISIBLE
            }
        }
        if (!itemList.isNullOrEmpty())
            totalItems = itemList.size
        itemList?.forEach {
            totalQty += it.quantity!!
            totalPrice += it.stylePrice!! * it.quantity!!
            skuList.add(it.skuCode!!)
            skuQtyMap.put(it.skuCode, it.quantity!!)
        }

        binding.tvTotalItems.text = totalItems.toString()
        binding.tvTotalQty.text = totalQty.toString()
        binding.tvTotalPrice.text =
            (Utils.getPriceFormatted(totalPrice.toString(), selectedCurrency))
        binding.recyclerViewOmniItems.layoutManager =
            LinearLayoutManager(mainActivity!!, LinearLayoutManager.VERTICAL, false)
        omniItemAdapter = OmniItemAdapter(mainActivity!!, selectedCurrency, itemList)
        binding.recyclerViewOmniItems.adapter = omniItemAdapter
    }

    fun getAccessToken() {
        accessToken =
            "Bearer " + sharedpreferenceHandler.getData(SharedpreferenceHandler.ACCESS_TOKEN, "")!!
    }

    fun setClickListeners() {

        binding.radioBtnShippingNormal.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked)
                shipmentType = "Normal"
            else
                shipmentType = "Express"
        }

        binding.radioBtnShippingExpress.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked)
                shipmentType = "Express"
            else
                shipmentType = "Normal"
        }
        binding.imageViewCalendar.setOnClickListener {
            showDatePickerDialog()
        }
        binding.cvSelectStores.setOnClickListener {
            val intent = Intent(mainActivity!!, SelectStoreActivity::class.java)
            intent.putStringArrayListExtra("skus", skuList)
            intent.putExtra("sku_map", skuQtyMap)
            intent.putExtra("delivery_method", selectedDeliveryMethod)
            startActivityForResult(intent, 304)
        }
        binding.btnViewAllItems.setOnClickListener {
            if (binding.recyclerViewOmniItems.visibility == View.VISIBLE) {
                binding.recyclerViewOmniItems.visibility = View.GONE
                binding.imageViewExpandCollapse.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_baseline_add_24,
                        resources.newTheme()
                    )
                )
            } else {
                binding.recyclerViewOmniItems.visibility = View.VISIBLE
                binding.imageViewExpandCollapse.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_baseline_minimize_24,
                        resources.newTheme()
                    )
                )
            }
        }
        binding.btnPlaceOrder.setOnClickListener {
            placeOrderApi()
        }
        binding.btnNext.setOnClickListener {

            if (selectedStore == null && binding.cvUserSelectedStore.visibility == View.GONE) {
                Utils.showSnackbar(binding.root, "Please select store to proceed")
            } else if (binding.tvSelectedStoreAvailability.text.equals("Unavailable")) {
                Utils.showSnackbar(binding.root, "Stock is unavailable for the selected stock")
            } else if (binding.tvSelectedStoreAvailability.text.equals("Available")) {
                when (selectedDeliveryMethod) {
                    "STOREPICKUP" -> {
                        pickupDate = binding.cvPickupDetails.edtPickupDate.text.toString()
                        pickupTime = ""
                        try {
                            pickupTime =
                                (binding.spinnerPickupTime.selectedItem as TimeSlot).timeSlotName
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }
                        selectedEmployee = null
                        try {
                            selectedEmployee =
                                binding.spinnerEmployee.selectedItem as EmployeeMaster
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }
                        if (selectedCustomer == null) {
                            setFragment()
                            binding.checkBoxSendPaymentLink.visibility = View.VISIBLE
                            binding.checkBoxSendPaymentLink.isChecked = true
                            if(sharedpreferenceHandler.getData(SharedpreferenceHandler.PAYMENTENABLED, "false").equals("false")) {
                                binding.checkBoxEnablePayment.visibility = View.GONE
                            }else{
                                if (selectedStore?.storeName?.equals("E-Commerce") == false){
                                    binding.checkBoxEnablePayment.visibility = View.VISIBLE
                                } else {
                                    binding.checkBoxEnablePayment.visibility = View.GONE
                                }
                            }
                            binding.lvStores.visibility = View.GONE
                            binding.cvPickupDetails.visibility = View.VISIBLE
                        } else if ((pickupDate.isNullOrEmpty() || pickupDate.contains("yyyy") || pickupTime.isNullOrEmpty()) && binding.lvStores.visibility == View.GONE) {
                            Utils.showSnackbar(
                                binding.root,
                                "Please select pickup date and time to proceed"
                            )
                        } else if (selectedEmployee != null && selectedEmployee?.empcodename.equals(
                                "Select"
                            )
                        ) {
                            Utils.showSnackbar(
                                binding.root,
                                "Please select any salesman to proceed"
                            )
                        } else {

                              if (binding.checkBoxEnablePayment.isChecked) {

                                if(binding.edtCashAmount.text.toString().isNullOrEmpty()){
                                    if(binding.edtCardAmount.text.toString().isNullOrEmpty()){
                                        Utils.showSnackbar(
                                            binding.root,
                                            "Please enter card or cash amount to proceed"
                                        )
                                    } else if(binding.edtCardAmount.length()>=0){
                                        if(binding.edtApprovalNo.text.toString().isNullOrEmpty()) {
                                            Utils.showSnackbar(
                                                binding.root,
                                                "Please enter approval number to proceed"
                                            )
                                        } else {
                                            if(binding.edtCashAmount.length()>=0){
                                                if(binding.edtCashAmount.text.isNullOrEmpty()){
                                                    cashdouble = 0.0
                                                } else{
                                                    val stricash = binding.edtCashAmount.text.toString()
                                                    cashdouble = stricash.toDouble()
                                                }

                                            }

                                            if(binding.edtCardAmount.length()>=0){
                                                if(binding.edtCardAmount.text.isNullOrEmpty()){
                                                    carddouble = 0.0
                                                } else{
                                                    val  stricard = binding.edtCardAmount.text.toString()
                                                    carddouble = stricard.toDouble()
                                                }

                                            }


                                            if(binding.edtApprovalNo.length()>=0){
                                                approvalno = binding.edtApprovalNo.text.toString()
                                            }else {
                                                approvalno = ""
                                            }

                                            val sum = cashdouble+carddouble

                                            if(sum != totalPrice){

                                                Utils.showSnackbar(
                                                    binding.root,
                                                    "Total Price Mismatch!"
                                                )


                                            } else {
                                                sendPaymentLink = binding.checkBoxSendPaymentLink.isChecked
                                                omniInvoice(pickupDate, pickupTime)
                                            }

                                        }
                                    }
                                } else {
                                    if(binding.edtCashAmount.length()>=0){
                                        if(binding.edtCashAmount.text.isNullOrEmpty()){
                                            cashdouble = 0.0
                                        } else{
                                            val stricash = binding.edtCashAmount.text.toString()
                                            cashdouble = stricash.toDouble()
                                        }

                                    }

                                    if(binding.edtCardAmount.length()>=0){
                                        if(binding.edtCardAmount.text.isNullOrEmpty()){
                                            carddouble = 0.0
                                        } else{
                                            val  stricard = binding.edtCardAmount.text.toString()
                                            carddouble = stricard.toDouble()
                                        }
                                    }

                                    if(binding.edtApprovalNo.length()>=0){
                                        approvalno = binding.edtApprovalNo.text.toString()
                                    } else {
                                        approvalno = ""
                                    }

                                    val sum = cashdouble+carddouble

                                    if(sum != totalPrice){

                                        Utils.showSnackbar(
                                            binding.root,
                                            "Total Price Mismatch!"
                                        )


                                    } else {
                                        sendPaymentLink = binding.checkBoxSendPaymentLink.isChecked
                                        omniInvoice(pickupDate, pickupTime)
                                    }
                                }


                            } else {

                                sendPaymentLink = binding.checkBoxSendPaymentLink.isChecked
                                omniInvoice(pickupDate, pickupTime)

                            }



                        }

                    }
                    "HOME DELIVERY" -> {
                        selectedEmployee = binding.spinnerEmployee.selectedItem as EmployeeMaster
                        if (selectedCustomer == null) {
                            setFragment()
                            binding.checkBoxSendPaymentLink.visibility = View.VISIBLE
                            binding.checkBoxSendPaymentLink.isChecked = true
                            if(sharedpreferenceHandler.getData(SharedpreferenceHandler.PAYMENTENABLED, "false").equals("false")) {
                                binding.checkBoxEnablePayment.visibility = View.GONE
                            }else{
                                if (selectedStore?.storeName?.equals("E-Commerce") == false){
                                    binding.checkBoxEnablePayment.visibility = View.VISIBLE
                                } else {
                                    binding.checkBoxEnablePayment.visibility = View.GONE
                                }

                            }


                            binding.lvStores.visibility = View.GONE
                            binding.txtShipmentType.visibility = View.VISIBLE
                            binding.radioGroupShipping.visibility = View.VISIBLE
                        } else if (shipmentType.isNullOrEmpty()) {
                            Utils.showSnackbar(
                                binding.root,
                                "Please select any shipment type to proceed"
                            )
                        } else if (selectedEmployee != null && selectedEmployee?.empcodename.equals("Select")) {
                            Utils.showSnackbar(
                                binding.root,
                                "Please select any salesman to proceed"
                            )
                        }
                        else {

                            if (binding.checkBoxEnablePayment.isChecked) {

                                if(binding.edtCashAmount.text.toString().isNullOrEmpty()){
                                    if(binding.edtCardAmount.text.toString().isNullOrEmpty()){
                                        Utils.showSnackbar(
                                            binding.root,
                                            "Please enter card or cash amount to proceed"
                                        )
                                    } else if(binding.edtCardAmount.length()>=0){
                                        if(binding.edtApprovalNo.text.toString().isNullOrEmpty()) {
                                            Utils.showSnackbar(
                                                binding.root,
                                                "Please enter approval number to proceed"
                                            )
                                        } else {
                                            if(binding.edtCashAmount.length()>=0){
                                                if(binding.edtCashAmount.text.isNullOrEmpty()){
                                                    cashdouble = 0.0
                                                } else{
                                                    val stricash = binding.edtCashAmount.text.toString()
                                                    cashdouble = stricash.toDouble()
                                                }

                                            }

                                            if(binding.edtCardAmount.length()>=0){
                                                if(binding.edtCardAmount.text.isNullOrEmpty()){
                                                    carddouble = 0.0
                                                } else{
                                                    val  stricard = binding.edtCardAmount.text.toString()
                                                    carddouble = stricard.toDouble()
                                                }

                                            }


                                            if(binding.edtApprovalNo.length()>=0){
                                                approvalno = binding.edtApprovalNo.text.toString()
                                            }else {
                                                approvalno = ""
                                            }

                                            val sum = cashdouble+carddouble

                                            if(sum != totalPrice){

                                                Utils.showSnackbar(
                                                    binding.root,
                                                    "Total Price Mismatch!"
                                                )


                                            } else {
                                                sendPaymentLink = binding.checkBoxSendPaymentLink.isChecked
                                                omniInvoice(getCurrentDate(), "")
                                            }

                                        }
                                    }
                                } else {
                                    if(binding.edtCashAmount.length()>=0){
                                        if(binding.edtCashAmount.text.isNullOrEmpty()){
                                            cashdouble = 0.0
                                        } else{
                                            val stricash = binding.edtCashAmount.text.toString()
                                            cashdouble = stricash.toDouble()
                                        }

                                    }

                                    if(binding.edtCardAmount.length()>=0){
                                        if(binding.edtCardAmount.text.isNullOrEmpty()){
                                            carddouble = 0.0
                                        } else{
                                            val  stricard = binding.edtCardAmount.text.toString()
                                            carddouble = stricard.toDouble()
                                        }

                                    }

                                    if(binding.edtApprovalNo.length()>=0){
                                        approvalno = binding.edtApprovalNo.text.toString()
                                    }else {
                                        approvalno = ""
                                    }

                                    val sum = cashdouble+carddouble

                                    if(sum != totalPrice){

                                        Utils.showSnackbar(
                                            binding.root,
                                            "Total Price Mismatch!"
                                        )


                                    } else {
                                        sendPaymentLink = binding.checkBoxSendPaymentLink.isChecked
                                        omniInvoice(getCurrentDate(), "")
                                    }
                                }


                            } else {

                                    sendPaymentLink = binding.checkBoxSendPaymentLink.isChecked
                                    omniInvoice(getCurrentDate(), "")

                            }


                        }
                    }
                }
            }
        }


        binding.checkBoxSendPaymentLink.setOnCheckedChangeListener{ buttonView, isChecked ->

            if(sharedpreferenceHandler.getData(SharedpreferenceHandler.PAYMENTENABLED, "false").equals("false")){


                binding.checkBoxSendPaymentLink.isChecked = true
                binding.checkBoxEnablePayment.isChecked = false
                binding.checkBoxEnablePayment.isVisible = false
                binding.llEnablePaymentContainer.isVisible = false
                binding.edtCardAmount.text.clear()
                binding.edtCashAmount.text.clear()
                binding.edtApprovalNo.text.clear()
                Utils.showSnackbar(
                    binding.root,
                    "Only One Payment Mode Enabled"
                )
            } else {
                if (selectedStore?.storeName?.equals("E-Commerce") == false){
                    binding.checkBoxEnablePayment.isChecked = !isChecked
                    binding.llEnablePaymentContainer.isVisible = !isChecked

                    binding.edtCardAmount.text.clear()
                    binding.edtCashAmount.text.clear()
                    binding.edtApprovalNo.text.clear()
                } else {
                    binding.checkBoxSendPaymentLink.isChecked = true
                    binding.checkBoxEnablePayment.isChecked = false
                    binding.checkBoxEnablePayment.isVisible = false
                    binding.llEnablePaymentContainer.isVisible = false
                    binding.edtCardAmount.text.clear()
                    binding.edtCashAmount.text.clear()
                    binding.edtApprovalNo.text.clear()

                }

            }


        }


        binding.checkBoxEnablePayment.setOnCheckedChangeListener{ buttonView, isChecked ->

            binding.checkBoxSendPaymentLink.isChecked = !isChecked
            binding.llEnablePaymentContainer.isVisible = isChecked

        }

        binding.edtCardAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                binding.linApprovalno.isVisible = count >= 1
            }
        })





        binding.imageViewBack.setOnClickListener {
            if (binding.lvStores.visibility == View.VISIBLE) {
//                mainActivity?.navController?.navigate(R.id.navigation_omni_bag)
            } else {
                when (selectedDeliveryMethod) {
                    "STOREPICKUP" -> {
                        if (binding.fragmentContainerView.visibility == View.VISIBLE) {
                            binding.fragmentContainerView.visibility = View.GONE
                            binding.checkBoxSendPaymentLink.visibility = View.GONE
                            binding.checkBoxEnablePayment.visibility = View.GONE
                            binding.lvStores.visibility = View.VISIBLE
                            binding.cvSelectStores.visibility = View.VISIBLE
                            binding.cvUserSelectedStore.visibility = View.VISIBLE
                            binding.cvPickupDetails.visibility = View.GONE
                        } else if (binding.btnPlaceOrder.visibility == View.VISIBLE
                            && binding.savedCards.visibility == View.VISIBLE
                        ) {
                            binding.savedCards.visibility = View.GONE
                            binding.fragmentContainerView.visibility = View.VISIBLE
                            binding.checkBoxSendPaymentLink.visibility = View.VISIBLE
                            binding.checkBoxSendPaymentLink.isChecked = true
                            if(sharedpreferenceHandler.getData(SharedpreferenceHandler.PAYMENTENABLED, "false").equals("false")) {
                                binding.checkBoxEnablePayment.visibility = View.GONE
                            }else{
                                if (selectedStore?.storeName?.equals("E-Commerce") == false){
                                    binding.checkBoxEnablePayment.visibility = View.VISIBLE
                                } else {
                                    binding.checkBoxEnablePayment.visibility = View.GONE
                                }
                            }
                            binding.cvPickupDetails.visibility = View.VISIBLE
                            binding.btnPlaceOrder.visibility = View.GONE
                            binding.btnNext.visibility = View.VISIBLE
                        }
                    }

                    "HOME DELIVERY" -> {
                        if (binding.fragmentContainerView.visibility == View.VISIBLE) {
                            binding.checkBoxSendPaymentLink.visibility = View.GONE
                            binding.checkBoxEnablePayment.visibility = View.GONE

                            binding.lvStores.visibility = View.VISIBLE
                            binding.cvSelectStores.visibility = View.VISIBLE
                            binding.cvUserSelectedStore.visibility = View.VISIBLE
                            binding.txtShipmentType.visibility = View.GONE
                            binding.radioGroupShipping.visibility = View.GONE
                            binding.fragmentContainerView.visibility = View.GONE
                        } else if (binding.btnPlaceOrder.visibility == View.VISIBLE
                            && binding.savedCards.visibility == View.VISIBLE
                        ) {
                            binding.savedCards.visibility = View.GONE
                            binding.fragmentContainerView.visibility = View.VISIBLE
                            binding.checkBoxSendPaymentLink.visibility = View.VISIBLE
                            binding.checkBoxSendPaymentLink.isChecked = true
                            if(sharedpreferenceHandler.getData(SharedpreferenceHandler.PAYMENTENABLED, "false").equals("false")) {
                                binding.checkBoxEnablePayment.visibility = View.GONE
                            }else{
                                if (selectedStore?.storeName?.equals("E-Commerce") == false){
                                    binding.checkBoxEnablePayment.visibility = View.VISIBLE
                                } else {
                                    binding.checkBoxEnablePayment.visibility = View.GONE
                                }
                            }
                            binding.txtShipmentType.visibility = View.VISIBLE
                            binding.radioGroupShipping.visibility = View.VISIBLE
                            binding.btnPlaceOrder.visibility = View.GONE
                            binding.btnNext.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun setFragment() {
        binding.fragmentContainerView.visibility = View.VISIBLE
        addCustomerFragment = OmniAddCustomerFragment()
        val bundle = Bundle()
        bundle.putString("delivery_method", selectedDeliveryMethod)
        addCustomerFragment?.arguments = bundle
        addCustomerFragment?.omniOrderPlaceFragment = this
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, addCustomerFragment!!)
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 304 && data != null && data.hasExtra(
                "selected_store"
            )
        ) {
            val gson = Gson()
            val storeString = data.getStringExtra("selected_store")
            selectedStore = gson.fromJson(storeString, OmniStoreStockCheckResponse::class.java)
            binding.cvUserSelectedStore.visibility = View.VISIBLE
            selectedStore?.let {

                if (it.enableWarehouseFullFillment == 1) {
                    binding.tvSelectedStoreName.text = it.warehouseName
                    binding.tvSelectedStoreCode.text = it.warehouseCode
                } else {
                    binding.tvSelectedStoreName.text = it.storeName
                    binding.tvSelectedStoreCode.text = it.storeCode
                }

                //warehouse code commented on 20.01.24 - joshua

//                if (it.enableWarehouseFullFillment == 1) {
//                    binding.tvSelectedStoreName.text = it.warehouseName
//                    binding.tvSelectedStoreCode.text = it.warehouseCode
//                } else {
//                    binding.tvSelectedStoreName.text = it.storeName
//                    binding.tvSelectedStoreCode.text = it.storeCode
//                }


//                if (it.availableQty > 0) {
//                    binding.tvSelectedStoreAvailability.text = "Available"
//                    binding.tvSelectedStoreAvailability.setTextColor(resources.getColor(R.color.green))
//                    binding.tvSelectedQty.text = it.availableQty.toString()
//                } else {
//                    binding.tvSelectedStoreAvailability.text = "Unavailable"
//                    binding.tvSelectedStoreAvailability.setTextColor(resources.getColor(R.color.red))
//                    binding.tvSelectedQty.text = "0"
//                }
            }
            if (data.hasExtra(

                    "status")) {
                val status = data.getStringExtra("status")
                if (status.equals("Available")) {
                    binding.tvSelectedStoreAvailability.setTextColor(resources.getColor(R.color.green))
                } else {
                    binding.tvSelectedStoreAvailability.setTextColor(resources.getColor(R.color.red))
                }
                binding.tvSelectedStoreAvailability.text = status
            }

            if (data.hasExtra("storeCode")) {
                val fromstorecode = data.getStringExtra("storeCode")
                sharedpreferenceHandler.saveData(SharedpreferenceHandler.FROMSTORECODEORDER, fromstorecode)
            }

        }
    }

    fun setDisplayAddress(customerId: String) {

//        orderPlaceViewModel.searchCustomerById(customerId!!)
//        orderPlaceViewModel.responseSearchCustomerById.observe(this) {
//            when (it) {
//                is Resource.Success -> {
//                   dismissProgress()
//                    if (it.data?.statusCode == 1) {
//                        binding.
//                    }
//                }
//
//                is Resource.Error -> {
//                    dismissProgress()
//                }
//                is Resource.Loading -> {
//                    showProgress()
//                }
//            }
//        }

    }


//    ///loading dialog
//    fun showProgress() {
//
//        if (!mainActivity?.isFinishing!!) {
//            if (loading == null) {
//                loading = Dialog(mainActivity!!, R.style.TranslucentDialog)
//                loading?.setContentView(R.layout.custom_loading_view)
//                loading?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                loading?.setCanceledOnTouchOutside(false)
//                loading?.show()
//            } else {
//                if (loading?.isShowing == true) {
//
//                } else {
//                    loading?.show()
//                }
//            }
//        }
//    }
//
//    fun dismissProgress() {
//        if (loading != null && loading?.isShowing == true)
//            loading?.dismiss()
//    }

    fun omniInvoice(pickupDate: String, pickupTime: String) {
        val orderItemList = getInvoiceItemList()
        var deliveryType = selectedDeliveryMethod
        if (selectedStore?.storeName?.equals("E-Commerce") == true)
            deliveryType = "E-Commerce"


        val omniInvoiceRequest = OrderInvoiceRequest(
            true, selectedCurrency, "", "", deliveryType, storeCode,
            storeId, orderItemList, selectedCurrency, pickupTime, pickupDate, ""
        )

        Log.e("emodeted", deliveryType)
        val gson = Gson()
        val requestString = gson.toJson(omniInvoiceRequest)
        getAccessToken()
        orderPlaceViewModel.omniInvoice(accessToken, omniInvoiceRequest)
        orderPlaceViewModel.responseOmniInvoice.observe(mainActivity!!) {
            when (it) {
                is Resource.Success -> {
                    showProgressBar(false)
                    if (it.data?.statusCode == 1) {
                        omniOrderInvoice = it.data

//                        for (i in 0 until itemList.size) {
//                            itemList.get(i).stylePrice = it.data.omnI_OrderHeaderData.omni_InvoiceHeader.invoiceDetailList.get(i).sellingPrice
//                        }

//                        val ordertemp = it.data.omnI_OrderHeaderData.omni_InvoiceHeader.invoiceDetailList
//                        Log.d("itemlist : " , itemList.toString())
//                        var tprice = 0.00
//                        for (i in 0 until itemList.size) {
//                            for (j in 0 until ordertemp.size){
//                                if(itemList.get(i).skuCode.equals(ordertemp.get(j).skuCode)){
//                                    itemList.get(i).stylePrice = ordertemp.get(j).sellingPrice
//                                    tprice += ordertemp.get(j).sellingPrice
//                                }
//                            }
//                        }
//                        omniItemAdapter.notifyDataSetChanged()
//                        totalPrice = tprice
//
//                        binding.tvTotalPrice.text =
//                            (Utils.getPriceFormatted(totalPrice.toString(), selectedCurrency))

                        createdbyloc = it.data.omnI_OrderHeaderData.createBy

                        orderPlaceViewModel?.omniOrderInvoice = omniOrderInvoice
                        setSavedCards()
                        binding.fragmentContainerView.visibility = View.GONE
                        binding.checkBoxSendPaymentLink.visibility = View.GONE
                        binding.btnPlaceOrder.visibility = View.VISIBLE
                        binding.btnNext.visibility = View.GONE
                        binding.savedCards.visibility = View.VISIBLE
                        binding.cvCards.txtCustomerDetails.visibility = View.VISIBLE
                        binding.cvCards.cvCustomerDetails.visibility = View.VISIBLE
                        binding.cvCards.txtOrderDetails.visibility = View.VISIBLE
                        binding.cvCards.cvOrderDetails.visibility = View.VISIBLE
                        when (selectedDeliveryMethod) {
                            "STOREPICKUP" -> {
                                binding.cvPickupDetails.visibility = View.GONE
                                binding.cvCards.txtStorePickupDetails.visibility = View.VISIBLE
                                binding.cvCards.cvStorePickupDetails.visibility = View.VISIBLE
                                binding.cvCards.cvShippingDetails.visibility = View.GONE
                                binding.cvCards.txtShippingDetails.visibility = View.GONE
                            }

                            "HOME DELIVERY" -> {
                                binding.txtShipmentType.visibility = View.GONE
                                binding.radioGroupShipping.visibility = View.GONE
                                binding.cvCards.txtStorePickupDetails.visibility = View.GONE
                                binding.cvCards.cvStorePickupDetails.visibility = View.GONE
                                binding.cvCards.cvShippingDetails.visibility = View.VISIBLE
                                binding.cvCards.txtShippingDetails.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    showProgressBar(false)
                    try {
                        val jsonObj = JSONObject(it.message)
                        val message = jsonObj.getString("message")
                        Utils.showSnackbar(binding.root, message)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        Utils.showSnackbar(binding.root, it.message.toString())
                    }
                }
                is Resource.Loading -> {
                    showProgressBar(true)
                }
            }
        }
    }

    private fun setSavedCards() {
        //setting custome details
        val fromStoreName = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_NAME, "")
        binding.cvCards.tvFromLocation.text = fromStoreName
        binding.cvCards.tvOrderedQty.text = totalQty.toString()
        binding.cvCards.tvOrderTotal.text =
            (Utils.getPriceFormatted(totalPrice.toString(), selectedCurrency))
        selectedCustomer?.let {

            binding.cvCards.tvCustomerId.text = it.id.toString()
            binding.cvCards.tvCustomerCode.text = it.customerCode
            binding.cvCards.tvCustomerName.text = it.customerName
            binding.cvCards.tvEmail.text = it.email
            binding.cvCards.tvMobile.text = it.isoCode + " " + it.phoneNumber
        }
        selectedStore?.let {
            if (it.enableWarehouseFullFillment == 1) {
                binding.cvCards.tvStoreName.text = it.warehouseName
            } else {
                binding.cvCards.tvStoreName.text = it.storeName
            }

            binding.cvCards.tvStatus.text = "PENDING"
        }
        if (selectedDeliveryMethod.equals("STOREPICKUP")) {
            binding.cvCards.tvPickupDate.text = pickupDate
            binding.cvCards.tvPickupTime.text = pickupTime
        } else if (selectedDeliveryMethod.equals("HOME DELIVERY")) {
            selectedCustomer?.let {
                var countryList=addCustomerFragment?.countryList
                countryList?.forEach { country->
                    if(it.countryID == country.id ) {
                        binding.cvCards.tvCountry.text = country.countryName
                    }
                }
//                if (it.countryName == null || it.countryName.isEmpty())
//                    binding.cvCards.tvCountry.text = it.countryName
//                else
//                    binding.cvCards.tvCountry.text = it.countryName
                binding.cvCards.tvCity.text = it.city
                binding.cvCards.tvState.text = it.stateName
                binding.cvCards.tvBlockStreet.text = it.billingBlock
                binding.cvCards.tvApartmentNo.text = it.billingArea
            }
        }
    }

    private fun getOrderItemDetailsList(
    ): ArrayList<OMNIOrderDetail> {
        val orderItemList = ArrayList<OMNIOrderDetail>()
        var deliveryType = selectedDeliveryMethod
        if (selectedStore?.storeName?.equals("E-Commerce") == true)
            deliveryType = "E-Commerce"
        itemList.forEach {
            orderItemList.add(
                OMNIOrderDetail(
                    selectedCurrency,
                    it.stylePrice!!,
                    it.brandCode!!,
                    it.brandID!!,
                    deliveryType,
                    1,
                    it.stylePrice!!,
                    selectedCurrency,
                    it.quantity!!,
                    pickupDate,
                    pickupTime,
                    it.productGroupID!!,
                    it.productGroupName!!,
                    it.seasonID!!,
                    it.seasonName!!,
                    it.segamentationID!!,
                    it.afSegamationName!!,
                    it.skuCode!!,
                    it.styleCode!!,
                    it.subBrandCode!!,
                    it.subBrandID!!,
                    selectedStore?.storeCode!!,
                    selectedStore?.id!!,
                    "0.5",
                    "KG",
                    it.yearID!!,
                    it.year!!,
                    0,
                    0
                )
            )
        }
        return orderItemList
    }


    private fun getInvoiceItemList(
    ): ArrayList<OMNIOrderDetailX> {
        val orderItemList = ArrayList<OMNIOrderDetailX>()
        var deliveryType = selectedDeliveryMethod
        if (selectedStore?.storeName?.equals("E-Commerce") == true)
            deliveryType = "E-Commerce"
        itemList.forEach {
            orderItemList.add(
                OMNIOrderDetailX(
                    selectedCurrency,
                    it.orig_price!!,
                    it.brandCode!!,
                    it.brandID!!,
                    deliveryType,
                    it.orig_price!!,
                    selectedCurrency,
                    it.quantity!!,
                    pickupDate,
                    pickupTime,
                    it.productGroupID!!,
                    it.productGroupName!!,
                    it.seasonID!!,
                    it.seasonName!!,
                    it.segamentationID!!,
                    it.skuCode!!,
                    it.styleCode!!,
                    it.subBrandCode!!,
                    it.subBrandID!!,
                    "0.5",
                    "KG",
                    it.yearID!!
                )
            )
        }
        return orderItemList
    }


    fun placeOrderApi() {
        val userCode = sharedpreferenceHandler.getData(SharedpreferenceHandler.LOGIN_USER_CODE, "")
        var omniOrderPlaceRequest: OmniOrderPlaceRequest? = null
        if (selectedStore != null) {
            var deliveryType = selectedDeliveryMethod
            if (selectedStore?.storeName?.equals("E-Commerce") == true)
                deliveryType = "E-Commerce"
            var toStoreCode = ""
            var toStoreId = 0
            var isfromwareehouse = 0
            var placeordawithpayment = "false"
            if (selectedStore?.enableWarehouseFullFillment == 1) {

                toStoreCode = storeCode
                toStoreId = storeId!!
                isfromwareehouse = 1
            } else {
                toStoreCode = selectedStore?.storeCode!!
                toStoreId = selectedStore?.id!!
                isfromwareehouse = 0
            }
            var enablePaymentSend = ""
            if (sendPaymentLink == true) {
                enablePaymentSend = "Enable Payment Link"
                placeordawithpayment = "false"
            }else{
                enablePaymentSend = ""
            placeordawithpayment = "true"
            }


            var salesEmployee = binding.spinnerEmployee.selectedItem as EmployeeMaster

            val transferReqList = ArrayList<TransferReq>()
            skuQtyMap.forEach { (key, value) ->
                transferReqList.add(TransferReq(key, value))
            }
            var orderItemList = ArrayList<OMNIOrderDetail>()
            try {
                orderItemList = getOrderItemDetailsList()
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            var wareHouseCode = ""
            var warehouseId = 0

            try {
                if (selectedStore?.enableWarehouseFullFillment == 1) {
                    wareHouseCode = selectedStore?.warehouseCode!!
                    warehouseId = selectedStore?.warehouseID!!
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            //warehouse code commented by joshua 20.01.24
//            try {
//                if (selectedStore?.enableWarehouseFullFillment == 1) {
//                    wareHouseCode = selectedStore?.warehouseCode!!
//                    warehouseId = selectedStore?.warehouseID!!
//                }
//            } catch (exception: Exception) {
//                exception.printStackTrace()
//            }

            omniOrderInvoice?.omnI_OrderHeaderData?.omni_InvoiceHeader?.let {
                it.customerID = selectedCustomer?.id!!
                it.customerCode = selectedCustomer?.customerCode!!
                it.customerName = selectedCustomer?.customerName!!
                it.customerMobileNo = selectedCustomer?.phoneNumber!!
                it.salesEmployeeID = selectedEmployee?.id!!
                it.salesEmployeeCode = selectedEmployee?.employeeCode!!
                it.salesEmployeeName = selectedEmployee?.employeeName!!
                it.fromStoreID = storeId
                it.fromStoreCode = storeCode
                it.fromStoreCode = createdbyloc
                it.receivedAmount = totalPrice
            }

            omniOrderPlaceRequest = OmniOrderPlaceRequest(
                true,
                "",
                omniOrderInvoice?.omnI_OrderHeaderData?.baseCurrency!!,
                selectedCustomer?.city!!,
                selectedCustomer?.countryID?.toString()!!,
                selectedCustomer?.customerCode!!,
                deliveryType,
                enablePaymentSend,
                storeCode,
                storeId,
                orderItemList,
                omniOrderInvoice?.omnI_OrderHeaderData?.omni_InvoiceHeader!!,
                omniOrderInvoice?.omnI_OrderHeaderData?.orderCurrency!!,
                pickupTime,
                pickupDate,
                salesEmployee?.employeeCode!!,
                salesEmployee?.id!!,
                shipmentType,
                selectedCustomer?.stateID?.toString()!!,
                selectedStore?.storeName!!,
                toStoreCode,
                toStoreId,
                transferReqList,
                userCode!!,
                wareHouseCode,
                warehouseId,

                isfromwareehouse,
                placeordawithpayment,
                cashdouble,
                carddouble,
                approvalno
            )
        }

        val gson = Gson()
        val requestString = gson.toJson(omniOrderPlaceRequest)
        Log.e("jsonreqplaceorder", omniOrderPlaceRequest!!.toString())

        longLog(omniOrderPlaceRequest!!.toString())



        getAccessToken()
        orderPlaceViewModel.omniOrderPlace(accessToken, omniOrderPlaceRequest!!)
        orderPlaceViewModel.responseOmniOrderPlace.observe(mainActivity!!) {
            when (it) {
                is Resource.Success -> {
                    showProgressBar(false)
                    if (it.data?.statusCode == 1) {
                        sharedpreferenceHandler.saveData(SharedpreferenceHandler.SEARCH_ITEMS, "")
                        sharedpreferenceHandler.saveData(SharedpreferenceHandler.SEARCH_COUNT, 0)
                        orderPlaceViewModel.deleteAllProductsFromBag()
                        val bundle = Bundle()
                        bundle.putString("order_id", it.data.orderNo)
                        bundle.putString("delivery_method", selectedDeliveryMethod)
                        bundle.putString("pickup_date", pickupDate)
                        bundle.putString("pickup_time", pickupTime)
                        mainActivity?.navController?.navigate(
                            R.id.navigation_omni_order_success,
                            bundle
                        )
                    }
                }

                is Resource.Error -> {
                    showProgressBar(false)
                    try {
                        val jsonObj = JSONObject(it.message)
                        val message = jsonObj.getString("message")
                        Utils.showSnackbar(binding.root, message)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        Utils.showSnackbar(binding.root, it.message.toString())
                    }
                }
                is Resource.Loading -> {
                    showProgressBar(true)
                }
            }
        }
    }


    open fun longLog(str: String) {
        if (str.length > 4000) {
            Log.d("request place", str.substring(0, 4000))
            longLog(str.substring(4000))
        } else Log.d("request place big", str)
    }

    fun getTimeSlot() {
        orderPlaceViewModel.getTimeSlot()
        orderPlaceViewModel.responseGetTimeSlot.observe(mainActivity!!) {
            when (it) {
                is Resource.Success -> {
                    showProgressBar(false)
                    if (it.data?.statusCode == 1) {
                        pickupTimeList = it.data.timeSlotList
                        binding.spinnerPickupTime.adapter =
                            PickupTimeSpinnerAdapter(mainActivity!!, pickupTimeList)
                    }
                }

                is Resource.Error -> {
                    showProgressBar(false)
                    try {
                        val jsonObj = JSONObject(it.message)
                        val message = jsonObj.getString("message")
                        Utils.showSnackbar(binding.root, message)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        Utils.showSnackbar(binding.root, it.message.toString())
                    }
                }
                is Resource.Loading -> {
                    showProgressBar(true)
                }
            }
        }
    }



    fun showProgressBar(show: Boolean) {
        if (show)
            binding?.cvProgressbar?.visibility = View.VISIBLE
        else
            binding?.cvProgressbar?.visibility = View.GONE
    }

    fun getSalesEmployees() {
        orderPlaceViewModel.getStoreEmployees(storeId!!.toString())
        orderPlaceViewModel.responseGetStoreEmployee.observe(mainActivity!!) {
            when (it) {
                is Resource.Success -> {
                    if (it.data?.statusCode == 1) {
                        storeEmployessList.add(
                            EmployeeMaster(
                                true, null,
                                null, 0, null, null, 0, null, null, null,
                                null, "", null, "Select", "", "", "Select",
                                0, false, false, false, false, null, null, 0, "",
                                0, null, null, 0, null, null, 0, null, null
                            )
                        )
                        storeEmployessList.addAll(it.data.employeeMasterList)
                        binding.spinnerEmployee.adapter =
                            EmployeeSpinnerAdapter(mainActivity!!, storeEmployessList)
                        showProgressBar(false)
                    }
                }
                is Resource.Error -> {
                    showProgressBar(false)
                    try {
                        val jsonObj = JSONObject(it.message)
                        val message = jsonObj.getString("message")
                        Utils.showSnackbar(binding.root, message)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        Utils.showSnackbar(binding.root, it.message.toString())
                    }
                }
                is Resource.Loading -> {
                    showProgressBar(true)
                }
            }
        }
    }

    fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            mainActivity!!,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val selectedMonth = monthOfYear + 1
                val selectedDate = "$year-$selectedMonth-$dayOfMonth"
                binding.edtPickupDate.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.minDate = c.getTimeInMillis()
        datePickerDialog.show()
    }


    override fun onResume() {
        super.onResume()
        try {
            getSalesEmployees()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }


    fun getCurrentDate(): String {
        val calender = Calendar.getInstance()

        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH) + 1
        val day = calender.get(Calendar.DAY_OF_MONTH)
        val date = "$year-$month-$day" //todo remove
        return date
    }

}