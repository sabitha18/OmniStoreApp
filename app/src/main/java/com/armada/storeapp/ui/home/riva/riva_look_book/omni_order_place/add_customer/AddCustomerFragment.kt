package com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.add_customer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.IsoCode
import com.armada.storeapp.data.model.request.CreateOmniCustomerRequest
import com.armada.storeapp.data.model.response.CityMaster
import com.armada.storeapp.data.model.response.CountryMaster
import com.armada.storeapp.data.model.response.CustomerMasterData
import com.armada.storeapp.data.model.response.StateMaster
import com.armada.storeapp.databinding.FragmentAddCustomerBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.checkout.address.adapter.CitySpinnerAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_item_scan.OmniItemScannerActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.OrderPlaceActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.RecyclerTouchListener
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.add_customer.adapter.CountrySpinnerAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.add_customer.adapter.IsoCodeSpinnerAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.add_customer.adapter.SearchAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.add_customer.adapter.StateSpinnerAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.select_store.SelectStoreViewModel
import com.armada.storeapp.ui.home.riva.riva_look_book.search.BarcodeScanner.BarcodeScannerActivity
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.armada.storeapp.ui.utils.Utils
import com.bumptech.glide.util.Util
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_omni_order_place.*
import org.json.JSONObject
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class AddCustomerFragment : Fragment() {
    lateinit var binding: FragmentAddCustomerBinding
    var orderPlaceActivity: OrderPlaceActivity? = null
    lateinit var addCustomerViewModel: AddCustomerViewModel
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    var selectedDeliveryMethod = ""
    var customerCode = ""
    var countryList: ArrayList<CountryMaster>? = null
    var stateList: ArrayList<StateMaster>? = null
    var cityList: ArrayList<CityMaster>? = null
    var isoCodeList = ArrayList<IsoCode>()
    var isEditCustomer = false
    var customerId = 0
    lateinit var listAdapter: SearchAdapter
    var customerList = ArrayList<CustomerMasterData>()
    var accessToken = ""
    var editCountryId: String? = null
    var editStateId: String? = null
    var editCityId: String? = null
    var startForResult: ActivityResultLauncher<Intent>? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    val barcode = intent?.getStringExtra("barcode")

                    if (barcode != null) {
                        val decodedBytes = Base64.decode(barcode, Base64.DEFAULT)
                        val decodedString = decodedBytes.toString(Charset.defaultCharset())

                        println("Decoded string: $decodedString")

                        searchCustomerByEmail(decodedString)
                    } else {
                        // Handle the case where 'barcode' is null
                    }
                }
            }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCustomerBinding.inflate(inflater)

        addCustomerViewModel = ViewModelProvider(this).get(AddCustomerViewModel::class.java)

        orderPlaceActivity = (activity as OrderPlaceActivity)
        sharedpreferenceHandler = SharedpreferenceHandler(orderPlaceActivity!!)
        updateToken()
        init()
        setOnClickListener()
        println("check ------------------   6666")
        return binding.root
    }

    fun init() {
        binding.lvIcons.visibility = View.VISIBLE
        arguments?.let {
            if (arguments?.containsKey("delivery_method") == true) {
                selectedDeliveryMethod = arguments?.getString("delivery_method")!!
            }
            if (arguments?.containsKey("is_edit_customer") == true)
                isEditCustomer = arguments?.getBoolean("is_edit_customer")!!
            if (arguments?.containsKey("customer_id") == true)
                customerId = arguments?.getInt("customer_id")!!
        }

        isoCodeList.add(IsoCode("KWT", "+965"))
        isoCodeList.add(IsoCode("UAE", "+971"))
        isoCodeList.add(IsoCode("QAT", "+974"))
        isoCodeList.add(IsoCode("BAH", "+973"))
        isoCodeList.add(IsoCode("KSA", "+966"))
        isoCodeList.add(IsoCode("OMN", "+968"))

        binding.spinnerIsoCodes.adapter = IsoCodeSpinnerAdapter(orderPlaceActivity!!, isoCodeList)

        when (selectedDeliveryMethod) {
            "STOREPICKUP" -> {
                binding.cvShippingDetails.visibility = View.GONE
                binding.lvCustomerCountry.visibility = View.VISIBLE
            }

            "HOME DELIVERY" -> {
                binding.lvCustomerCountry.visibility = View.GONE
                binding.cvShippingDetails.visibility = View.VISIBLE
            }
        }
        listAdapter = SearchAdapter(
            orderPlaceActivity!!,
            customerList
        )
        binding.listSearchedData.layoutManager = LinearLayoutManager(
            orderPlaceActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.listSearchedData.adapter = listAdapter
        binding.btnAddCustomer.text = "ADD CUSTOMER"
        getCountry()
    }


    fun setOnClickListener() {


            binding.lvIcons.setOnClickListener {

                val intent = Intent(activity, BarcodeScannerActivity::class.java)
                startForResult?.launch(intent)
            }

        binding.btnAddCustomer.setOnClickListener {
            println("check -------------22 ")
            if (isEditCustomer)
                createCustomerApi(addCustomerViewModel?.selectedCustomer?.id!!,true)
            else
                createCustomerApi(0,false)
        }

        binding.listSearchedData.addOnItemTouchListener(
            RecyclerTouchListener(
                orderPlaceActivity!!,
                binding.listSearchedData,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        val searchItem = customerList.get(position)
                        setDisplayAddress(searchItem)
                    }

                    override fun onLongClick(view: View?, position: Int) {}
                })
        )

        binding.imageButtonEditCustomer.setOnClickListener {
            isEditCustomer = true
            addCustomerViewModel.selectedCustomer?.let {

//                try {
//                    editCountryId = it.countryID.toString()
//                    editStateId = it.stateID.toString()
//                    editCityId = it.billingCityID.toString()
//                } catch (exception: Exception) {
//                    exception.printStackTrace()
//                }
                binding.btnAddCustomer.text = "EDIT CUSTOMER"
                binding.edtCustomerCode.setText(it.customerCode)
                binding.edtCustomerName.setText(it.customerName)
                binding.edtCustomerEmail.setText(it.email)
                binding.edtCustomerMobileNumber.setText(it.phoneNumber)
                binding.edtBlockStreet.setText(it.billingStreet)
                binding.edtApartmentNo.setText(it.billingArea)
                binding.edtCustomerEmail.isEnabled = false
                binding.edtCustomerCode.isEnabled = false
                binding.edtCustomerMobileNumber.isEnabled = false
                binding.spinnerIsoCodes.isEnabled=false
                binding.spinnerIsoCodes.isClickable=false
                binding.lvCustomerCountry.visibility = View.VISIBLE
                for ((index, value) in isoCodeList.withIndex()) {
                    if (value.isoCode.equals(it.isoCode)) {
                        binding.spinnerIsoCodes.setSelection(index)
                    }
                }
                if (selectedDeliveryMethod.equals("HOME DELIVERY")) {
                    try {
                        for ((index, value) in countryList!!.withIndex()) {
                            if (value.countryCode.equals(it.countryCode)) {
                                binding.spinnerCountry.setSelection(index)
//                                getStates(it.countryID.toString())
                            }
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }

                    try {
                        for ((index, value) in stateList!!.withIndex()) {
                            if (value.stateCode.equals(it.stateCode)) {
                                binding.spinnerState.setSelection(index)
                            }
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }

                    try {
                        for ((index, value) in cityList!!.withIndex()) {
                            if (value.cityCode.equals(it.shippingCityCode)) {
                                binding.spinnerCity.setSelection(index)
                            }
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    binding.cvSearchedCustomer.visibility = View.GONE
                    binding.lvCreateCustomer.visibility = View.VISIBLE
                    binding.cvSearchview.visibility = View.VISIBLE
                    binding.listSearchedData.visibility = View.VISIBLE
                    binding.cvEnterCustomerDetails.visibility = View.VISIBLE
                    binding.lvCustomerCountry.visibility = View.GONE

//                    binding.lvCreateCustomer.performClick()
                } else if (selectedDeliveryMethod.equals("STOREPICKUP")) {
                    binding.cvSearchedCustomer.visibility = View.GONE
                    binding.lvCreateCustomer.visibility = View.VISIBLE
                    binding.cvSearchview.visibility = View.VISIBLE
                    binding.listSearchedData.visibility = View.VISIBLE
                    binding.cvEnterCustomerDetails.visibility = View.VISIBLE
                    binding.cvShippingDetails.visibility = View.GONE
                    binding.lvCustomerCountry.visibility = View.VISIBLE
                }
            }
        }

        binding.lvCreateCustomer.setOnClickListener {
            if (binding.cvEnterCustomerDetails.visibility == View.VISIBLE) {
                binding.cvEnterCustomerDetails.visibility = View.GONE
                binding.imageViewExpandCollapse.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_baseline_add_24,
                        resources.newTheme()
                    )
                )
            } else {
                binding.cvEnterCustomerDetails.visibility = View.VISIBLE
                if (selectedDeliveryMethod.equals("STOREPICKUP"))
                    binding.lvCustomerCountry.visibility = View.VISIBLE
                else
                    binding.lvCustomerCountry.visibility = View.GONE
                binding.imageViewExpandCollapse.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_baseline_minimize_24,
                        resources.newTheme()
                    )
                )
            }
        }

        binding.spinnerCountry.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                try {
                    getStates(countryList?.get(position)?.id?.toString()!!)
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        binding.spinnerState.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                try {
                    getCities(stateList?.get(position)?.id?.toString()!!)
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        binding.searchViewCustomer.inputType= InputType.TYPE_CLASS_TEXT
        binding.searchViewCustomer.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query?.isDigitsOnly() == true) {
                    searchCustomerByString(query)
                } else {
                    if (query?.contains("@") == true)
                        searchCustomerByEmail(query!!)
                    else
                        searchCustomerByString(query!!)
                }
//                // on below line we are checking
//                // if query exist or not.
//                if (programmingLanguagesList.contains(query)) {
//                    // if query exist within list we
//                    // are filtering our list adapter.
//                    listAdapter.filter.filter(query)
//                } else {
//                    // if query is not present we are displaying
//                    // a toast message as no  data found..
//                    Toast.makeText(this@MainActivity, "No Language found..", Toast.LENGTH_LONG)
//                        .show()
//                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                if (newText?.isEmpty() == true) {
//                    customerList = ArrayList()
//                    listAdapter = SearchAdapter(orderPlaceActivity!!, customerList)
//                    binding.listSearchedData.adapter = listAdapter
//                } else {
//                    if (newText?.isDigitsOnly() == true) {
////                        searchCustomerById(newText!!)
//                    } else {
//                        searchCustomerByString(newText!!)
//                    }
//                }
                return false
            }
        })

    }


    fun editCustomer(customerId: Int) {
        val storeId = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_ID, 0)
        val isoCode = binding.spinnerIsoCodes.selectedItem as IsoCode
        val customeName = binding.edtCustomerName.text.toString()
        val phoneNumber = binding.edtCustomerMobileNumber.text.toString()
        val email = binding.edtCustomerEmail.text.toString()

        if (selectedDeliveryMethod.equals("STOREPICKUP")) {
            if (customeName.isNullOrEmpty())
                Utils.showSnackbar(binding.root, "Please enter customer name")
            else if (phoneNumber.isNullOrEmpty())
                Utils.showSnackbar(binding.root, "Please enter phone number")
            else if (email.isNullOrEmpty())
                Utils.showSnackbar(binding.root, "Please enter mail id")

        } else if (selectedDeliveryMethod.equals("HOME DELIVERY")) {


        }


        var customerRequest: CreateOmniCustomerRequest? = null
        if (selectedDeliveryMethod.equals("STOREPICKUP")) {
            val countryCode =
                (binding.spinnerCustomerCountry.selectedItem as CountryMaster).countryCode
            customerRequest = CreateOmniCustomerRequest(
                true,
                isoCode.isoCode,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                phoneNumber,
                "",
                "",
                "",
                "",
                0,
                "",
                customerCode,
                "",
                countryCode,
                "WalkIn",
                1,
                customeName,
                true,
                "",
                1596,
                15,
                email,
                "",
                customerId,
                true,
                true,
                isoCode.isoCode,
                "",
                "",
                0,
                false,
                "",
                phoneNumber,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                isoCode.isoCode,
                customeName,
                phoneNumber,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                storeId,
                storeId,
                "",
                ""
            )

        } else if (selectedDeliveryMethod.equals("HOME DELIVERY")) {
            var selectedCityMaster: CityMaster? = null
            val selectedCountry = binding.spinnerCountry.selectedItem as CountryMaster
            val selectedState = binding.spinnerState.selectedItem as StateMaster
            try {
                selectedCityMaster = binding.spinnerCity.selectedItem as CityMaster
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            val blockStreet = binding.edtBlockStreet.text.toString()
            val apartmentNo = binding.edtApartmentNo.text.toString()

            var selectedCityName = ""
            var selectedCityId = 0
            var selectedCityCode = ""

            if (selectedCityMaster != null) {
                selectedCityName = selectedCityMaster?.cityName
                selectedCityId = selectedCityMaster?.id
                selectedCityCode = selectedCityMaster?.cityCode
            } else {
                selectedCityName = binding.edtCity.text.toString()
            }

            customerRequest = CreateOmniCustomerRequest(
                true,
                isoCode.isoCode,
                "",
                "",
                "",
                apartmentNo,
                blockStreet,
                selectedCityCode,
                selectedCityId.toString(),
                phoneNumber,
                blockStreet,
                apartmentNo,
                selectedCityName,
                selectedCountry.id.toString(),
                0,
                "",
                customerCode,
                blockStreet,
                selectedCountry.countryCode,
                "WalkIn",
                1,
                customeName,
                true,
                "",
                1596,
                15,
                email,
                "",
                customerId,
                true,
                true,
                isoCode.isoCode,
                "",
                "",
                0,
                false,
                "",
                phoneNumber,
                "",
                "",
                "",
                "",
                "",
                apartmentNo,
                selectedCityName,
                selectedCityCode,
                selectedCityId.toString(),
                selectedCountry.countryCode.toString(),
                selectedCountry.id.toString(),
                isoCode.isoCode,
                customeName,
                phoneNumber,
                "",
                selectedState.stateCode,
                selectedState.id.toString(),
                selectedState.stateName,
                blockStreet,
                selectedState.stateCode,
                selectedState.id.toString(),
                selectedState.stateName,
                storeId,
                storeId,
                "",
                ""
            )
        }

        val gson = Gson()
        val editCustomerString = gson.toJson(customerRequest)
        getAccessToken()
        addCustomerViewModel.editCustomer(accessToken!!, customerRequest!!)
        addCustomerViewModel.responseEditCustomer.observe(orderPlaceActivity!!) {
            when (it) {
                is Resource.Success -> {
                    orderPlaceActivity?.dismissProgress()
                    if (it.data?.statusCode == 1) {
                        binding.lvCreateCustomer.performClick()
                        searchCustomerById(customerId.toString())
                        isEditCustomer = false
                    }
                }

                is Resource.Error -> {
                    orderPlaceActivity?.dismissProgress()
                    if (it.statusCode == 401) {
                        updateToken()
                    } else {
                        try {
                            val jsonObj = JSONObject(it.message)
                            val message = jsonObj.getString("message")
                            Utils.showSnackbar(binding.root, message)
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                            Utils.showSnackbar(binding.root, it.message.toString())
                        }
                    }
                }
                is Resource.Loading -> {
                    orderPlaceActivity?.showProgress()
                }
            }
        }
    }

    fun createCustomerApi(
        customerId: Int, isEdit: Boolean
    ) {
        val storeId = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_ID, 0)
        val isoCode = binding.spinnerIsoCodes.selectedItem as IsoCode
        val customeName = binding.edtCustomerName.text.toString()
        val phoneNumber = binding.edtCustomerMobileNumber.text.toString()
        val email = binding.edtCustomerEmail.text.toString()
        var customerRequest: CreateOmniCustomerRequest? = null
        var currentCustomerCode=""
        if(isEdit)
            currentCustomerCode=addCustomerViewModel?.selectedCustomer?.customerCode!!
        else
            currentCustomerCode=binding.edtCustomerCode.text.toString()
        if (selectedDeliveryMethod.equals("STOREPICKUP")) {

            val countryCode =
                (binding.spinnerCustomerCountry.selectedItem as CountryMaster).countryCode
            val countryId = (binding.spinnerCustomerCountry.selectedItem as CountryMaster).id

            if (countryCode.isNullOrEmpty())
                Utils.showSnackbar(binding.root, "Please select country")
            else if (customeName.isNullOrEmpty())
                Utils.showSnackbar(binding.root, "Please enter Name")
            else if (phoneNumber.isNullOrEmpty())
                Utils.showSnackbar(binding.root, "Please enter Phone number")
            else if (email.isNullOrEmpty())
                Utils.showSnackbar(binding.root, "Please enter Email Id")
            else {
                if (isEdit)
                    customerRequest = CreateOmniCustomerRequest(
                        true,
                        isoCode.isoCode,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        phoneNumber,
                        "",
                        "",
                        "",
                        countryId.toString(),
                        0,
                        "",
                      currentCustomerCode ,
                        "", countryCode,
                        "WalkIn",
                        1,
                        customeName,
                        true,
                        "",
                        1596,
                        15,
                        email,
                        "",
                        customerId,
                        true,
                        true,
                        isoCode.isoCode,
                        "",
                        "",
                        0,
                        false,
                        "",
                        phoneNumber,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        countryCode,
                        countryId.toString(),
                        isoCode.isoCode,
                        customeName,
                        phoneNumber,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        storeId,
                        storeId,
                        "",
                        ""
                    ) else
                    customerRequest = CreateOmniCustomerRequest(
                        true,
                        isoCode.isoCode,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        phoneNumber,
                        "",
                        "",
                        "",
                        countryId.toString(),
                        0,
                        "",
                        currentCustomerCode,
                        "", countryCode,
                        "WalkIn",
                        1,
                        customeName,
                        true,
                        "",
                        1596,
                        15,
                        email,
                        "",
                        0,
                        true,
                        true,
                        isoCode.isoCode,
                        "",
                        "",
                        0,
                        false,
                        "",
                        phoneNumber,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        countryCode,
                        countryId.toString(),
                        isoCode.isoCode,
                        customeName,
                        phoneNumber,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        storeId,
                        storeId,
                        "",
                        ""
                    )
                getAccessToken()
                val gson=Gson()
                val requestString=gson.toJson(customerRequest)
                if (isEdit)
                    addCustomerViewModel.editCustomer(accessToken!!, customerRequest!!)
                else
                    addCustomerViewModel.createOmniCustomer(accessToken!!, customerRequest!!)
            }


        } else if (selectedDeliveryMethod.equals("HOME DELIVERY")) {
            var selectedCity: CityMaster? = null
            val selectedCountry = binding.spinnerCountry.selectedItem as CountryMaster
            val selectedState = binding.spinnerState.selectedItem as StateMaster
            val blockStreet = binding.edtBlockStreet.text.toString()
            val apartmentNo = binding.edtApartmentNo.text.toString()

            try {
                selectedCity = binding.spinnerCity.selectedItem as CityMaster
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            var selectedCityName = ""
            var selectedCityId = 0
            var selectedCityCode = ""

            if (selectedCity != null) {
                selectedCityName = selectedCity?.cityName
                selectedCityId = selectedCity?.id
                selectedCityCode = selectedCity?.cityCode
            } else {
                selectedCityName = binding.edtCity.text.toString()
            }

            if (selectedCountry == null)
                Utils.showSnackbar(binding.root, "Please select country")
            else if (selectedState == null)
                Utils.showSnackbar(binding.root, "Please select state")
            else if (selectedCityName.isNullOrEmpty())
                Utils.showSnackbar(binding.root, "Please select or enter city")
            else if (blockStreet.isNullOrEmpty())
                Utils.showSnackbar(binding.root, "Please enter street and block")
            else if (apartmentNo.isNullOrEmpty())
                Utils.showSnackbar(binding.root, "Please enter apartment no")
            else if (customeName.isNullOrEmpty())
                Utils.showSnackbar(binding.root, "Please enter Name")
            else if (phoneNumber.isNullOrEmpty())
                Utils.showSnackbar(binding.root, "Please enter Phone number")
            else if (email.isNullOrEmpty())
                Utils.showSnackbar(binding.root, "Please enter Email Id")
            else {
                if (isEdit)
                    customerRequest = CreateOmniCustomerRequest(
                        true,
                        isoCode.isoCode,
                        "",
                        "",
                        "",
                        apartmentNo,
                        blockStreet,
                        selectedCityCode,
                        selectedCityId.toString(),
                        phoneNumber,
                        blockStreet,
                        apartmentNo,
                        selectedCityName,
                        selectedCountry.id.toString(),
                        0,
                        "",
                        currentCustomerCode,
                        blockStreet,
                        selectedCountry.countryCode,
                        "WalkIn",
                        1,
                        customeName,
                        true,
                        "",
                        1596,
                        15,
                        email,
                        "",
                        customerId,
                        true,
                        true,
                        isoCode.isoCode,
                        "",
                        "",
                        0,
                        false,
                        "",
                        phoneNumber,
                        "",
                        "",
                        "",
                        "",
                        "",
                        apartmentNo,
                        selectedCityName,
                        selectedCityCode,
                        selectedCityId.toString(),
                        selectedCountry.countryCode.toString(),
                        selectedCountry.id.toString(),
                        isoCode.isoCode,
                        customeName,
                        phoneNumber,
                        "",
                        selectedState.stateCode,
                        selectedState.id.toString(),
                        selectedState.stateName,
                        blockStreet,
                        selectedState.stateCode,
                        selectedState.id.toString(),
                        selectedState.stateName,
                        storeId,
                        storeId,
                        "",
                        ""
                    )
                else
                    customerRequest = CreateOmniCustomerRequest(
                        true,
                        isoCode.isoCode,
                        "",
                        "",
                        "",
                        apartmentNo,
                        blockStreet,
                        selectedCityCode,
                        selectedCityId.toString(),
                        phoneNumber,
                        blockStreet,
                        apartmentNo,
                        selectedCityName,
                        selectedCountry.id.toString(),
                        0,
                        "",
                        currentCustomerCode,
                        blockStreet,
                        selectedCountry.countryCode,
                        "WalkIn",
                        1,
                        customeName,
                        true,
                        "",
                        1596,
                        15,
                        email,
                        "",
                        0,
                        true,
                        true,
                        isoCode.isoCode,
                        "",
                        "",
                        0,
                        false,
                        "",
                        phoneNumber,
                        "",
                        "",
                        "",
                        "",
                        "",
                        apartmentNo,
                        selectedCityName,
                        selectedCityCode,
                        selectedCityId.toString(),
                        selectedCountry.countryCode.toString(),
                        selectedCountry.id.toString(),
                        isoCode.isoCode,
                        customeName,
                        phoneNumber,
                        "",
                        selectedState.stateCode,
                        selectedState.id.toString(),
                        selectedState.stateName,
                        blockStreet,
                        selectedState.stateCode,
                        selectedState.id.toString(),
                        selectedState.stateName,
                        storeId,
                        storeId,
                        "",
                        ""
                    )
                val gson=Gson()
                val requestString=gson.toJson(customerRequest)
                getAccessToken()
                if (isEdit)
                    addCustomerViewModel.editCustomer(accessToken!!, customerRequest!!)
                else
                    addCustomerViewModel.createOmniCustomer(accessToken!!, customerRequest!!)
            }
        }

        addCustomerViewModel.responseCreateCustomer.observe(orderPlaceActivity!!) {
            when (it) {
                is Resource.Success -> {
                    orderPlaceActivity?.dismissProgress()
                    if (it.data?.statusCode == 1) {
                        binding.lvCreateCustomer.performClick()
                        searchCustomerById(it.data.iDs)
                    }
                }

                is Resource.Error -> {

                    orderPlaceActivity?.dismissProgress()
                    if (it.statusCode == 401) {
                        updateToken()
                    } else {
                        try {
                            val jsonObj = JSONObject(it.message)
                            val message = jsonObj.getString("message")
                            Utils.showSnackbar(binding.root, message)
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                            Utils.showSnackbar(binding.root, it.message.toString())
                        }
                    }

                }
                is Resource.Loading -> {
                    orderPlaceActivity?.showProgress()
                }
            }
        }

        // edit customer response
        addCustomerViewModel.responseEditCustomer.observe(orderPlaceActivity!!) {
            when (it) {
                is Resource.Success -> {
                    orderPlaceActivity?.dismissProgress()
                    if (it.data?.statusCode == 1) {
                        binding.lvCreateCustomer.performClick()
                        searchCustomerById(customerId.toString())
                        isEditCustomer = false
                    }
                }

                is Resource.Error -> {
                    orderPlaceActivity?.dismissProgress()
                    if (it.statusCode == 401) {
                        updateToken()
                    } else {
                        try {
                            val jsonObj = JSONObject(it.message)
                            val message = jsonObj.getString("message")
                            Utils.showSnackbar(binding.root, message)
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }
                    }
                }
                is Resource.Loading -> {
                    orderPlaceActivity?.showProgress()
                }
            }
        }

    }

    fun getCustomerCode() {
        val storeId = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_ID, 0)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDate = LocalDateTime.now().format(formatter)
        getAccessToken()
        addCustomerViewModel.getCustomerCode(accessToken!!, storeId!!.toString(), "15", currentDate)
        addCustomerViewModel.responseCustomerCode.observe(orderPlaceActivity!!) {
            when (it) {
                is Resource.Success -> {
                    orderPlaceActivity?.dismissProgress()
                    if (it.data?.statusCode == 1) {
                        customerCode = it?.data?.documentNo
                        binding.edtCustomerCode.setText(customerCode)
                    }

                }

                is Resource.Error -> {
                    if (it.statusCode == 401) {
                        updateToken()
                    }
                    orderPlaceActivity?.dismissProgress()
                }
                is Resource.Loading -> {
                    orderPlaceActivity?.showProgress()
                }
            }
        }
    }

    fun updateToken() {
        val username = sharedpreferenceHandler.getData(SharedpreferenceHandler.LOGIN_USERNAME, "")
        val password = sharedpreferenceHandler.getData(SharedpreferenceHandler.LOGIN_PASSWORD, "")
        val encryptedPassword = Utils.encrypt(password!!)?.trimEnd()
        addCustomerViewModel.updateToken(username!!, encryptedPassword!!)
        addCustomerViewModel.responseLogin.observe(orderPlaceActivity!!) {
            when (it) {
                is Resource.Success -> {
                    orderPlaceActivity?.dismissProgress()
                    if (it.statusCode == 200) {
                        sharedpreferenceHandler.saveData(
                            SharedpreferenceHandler.ACCESS_TOKEN,
                            it.data?.access_token
                        )
                        getCustomerCode()
                    }

                }

                is Resource.Error -> {
                    if (it.statusCode == 401) {
                        Utils.showSnackbar(binding.root, "Please logout and login again")
                    }
                    orderPlaceActivity?.dismissProgress()
                }
                is Resource.Loading -> {
                    orderPlaceActivity?.showProgress()
                }
            }
        }
    }

    fun searchCustomerById(id: String) {
        customerList.clear()
        listAdapter.notifyDataSetChanged()
        getAccessToken()
        addCustomerViewModel.searchCustomerById(accessToken!!, id)
        addCustomerViewModel.responseSearchCustomerById.observe(orderPlaceActivity!!) {
            when (it) {
                is Resource.Success -> {
                    orderPlaceActivity?.dismissProgress()
                    if (it.data?.statusCode == 1) {
                        if (it.data?.customerMasterData?.size > 0) {
                            customerList = it.data?.customerMasterData
//                            val customerNames = ArrayList<String>()
//                            customerList.forEach {
//                                customerNames.add(it.customerName)
//                            }
//                            listAdapter = ArrayAdapter(
//                                orderPlaceActivity!!,
//                                android.R.layout.simple_list_item_1,
//                                customerNames
//                            )
//                            binding.listSearchedData.adapter = listAdapter
                            addCustomerViewModel.selectedCustomer = customerList.get(0)
                            setDisplayAddress(customerList.get(0))

                        } else {
//                            Utils.showSnackbar(binding.root, "No customer found")
                        }
                    } else {
                        Utils.showSnackbar(binding.root, it.data?.displayMessage!!)
                    }
                }

                is Resource.Error -> {
                    orderPlaceActivity?.dismissProgress()
                    if (it.statusCode == 401) {
                        updateToken()
                    } else {
                        try {
                            val jsonObj = JSONObject(it.message)
                            val message = jsonObj.getString("message")
                            Utils.showSnackbar(binding.root, message)
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }
                    }
                }
                is Resource.Loading -> {
                    orderPlaceActivity?.showProgress()
                }
            }
        }
    }

    fun setDisplayAddress(address: CustomerMasterData) {
        address?.let {
            binding.tvCustomerId.text = it.id.toString()
            binding.tvCustomerName.text = it.customerName
            binding.tvCustomerCode.text = it.customerCode
            binding.tvEmail.text = it.email
            binding.tvMobile.text =
                it.isoCode + " " + it.phoneNumber
            binding.cvSearchedCustomer.visibility = View.VISIBLE

            binding.lvCreateCustomer.visibility = View.GONE
            binding.cvSearchview.visibility = View.GONE
            binding.listSearchedData.visibility = View.GONE
            binding.cvEnterCustomerDetails.visibility = View.GONE
            orderPlaceActivity?.selectedCustomer = address
        }
    }

    fun searchCustomerByEmail(email: String) {
        customerList.clear()
        listAdapter.notifyDataSetChanged()
        getAccessToken()
        addCustomerViewModel.searchCustomerByEmail(accessToken!!, email)
        addCustomerViewModel.responseSearchCustomerByEmail.observe(orderPlaceActivity!!) {
            when (it) {
                is Resource.Success -> {
                    orderPlaceActivity?.dismissProgress()
                    if (it.data?.statusCode == 1) {
                        if (it.data?.omniCustomerMasterType?.size > 0) {
//                            binding.listSearchedData.visibility = View.VISIBLE
                            customerList = it.data?.omniCustomerMasterType
//                            listAdapter = SearchAdapter(
//                                orderPlaceActivity!!,
//                                customerList
//                            )
//                            binding.listSearchedData.layoutManager = LinearLayoutManager(
//                                orderPlaceActivity,
//                                LinearLayoutManager.VERTICAL,
//                                false
//                            )
//                            binding.listSearchedData.adapter = listAdapter
                            addCustomerViewModel.selectedCustomer = customerList.get(0)
                            searchCustomerById(customerList?.get(0)?.id?.toString())
//                            setDisplayAddress(customerList.get(0))
                        } else {
//                            Utils.showSnackbar(binding.root, "No customer found")
                        }
                    } else {
                        Utils.showSnackbar(binding.root, it.data?.displayMessage!!)
                    }
                }

                is Resource.Error -> {
                    orderPlaceActivity?.dismissProgress()
                    if (it.statusCode == 401) {
                        updateToken()
                    } else {
                        try {
                            val jsonObj = JSONObject(it.message)
                            val message = jsonObj.getString("message")
                            Utils.showSnackbar(binding.root, message)
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }
                    }
                }
                is Resource.Loading -> {
                    orderPlaceActivity?.showProgress()
                }
            }
        }
    }


    fun searchCustomerByString(searchString: String) {
        getAccessToken()
        addCustomerViewModel.searchCustomerByString(accessToken!!, searchString)
        addCustomerViewModel.responseSearchCustomerByString.observe(orderPlaceActivity!!) {
            when (it) {
                is Resource.Success -> {
                    orderPlaceActivity?.dismissProgress()
                    if (it.data?.statusCode == 1) {
                        if (it.data?.customerMasterData?.size > 0) {
                            customerList = it.data?.customerMasterData
//                            listAdapter = SearchAdapter(
//                                orderPlaceActivity!!,
//                                customerList
//                            )
//                            binding.listSearchedData.layoutManager = LinearLayoutManager(
//                                orderPlaceActivity,
//                                LinearLayoutManager.VERTICAL,
//                                false
//                            )
//                            binding.listSearchedData.adapter = listAdapter
                            addCustomerViewModel.selectedCustomer = customerList.get(0)
                            searchCustomerById(customerList?.get(0)?.id?.toString())
                        } else {
                            Utils.showSnackbar(binding.root, "No customer found")
                        }
                    } else {
                        Utils.showSnackbar(binding.root, it.data?.displayMessage!!)
                        searchCustomerById(searchString)
                    }
                }

                is Resource.Error -> {
                    orderPlaceActivity?.dismissProgress()
                    if (it.statusCode == 401) {
                        updateToken()
                    } else {
                        try {
                            val jsonObj = JSONObject(it.message)
                            val message = jsonObj.getString("message")
                            Utils.showSnackbar(binding.root, message)
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }
                    }
                    searchCustomerById(searchString)
                }
                is Resource.Loading -> {
                    orderPlaceActivity?.showProgress()
                }
            }
        }
    }

    private fun getAccessToken() {
        accessToken =
            "Bearer " + sharedpreferenceHandler.getData(SharedpreferenceHandler.ACCESS_TOKEN, "")!!
    }

    fun getCountry() {
        addCustomerViewModel.getCountryList()
        addCustomerViewModel.responseGetcountry.observe(orderPlaceActivity!!) {
            when (it) {
                is Resource.Success -> {
                    orderPlaceActivity?.dismissProgress()
                    if (!it.data?.countryMasterList.isNullOrEmpty()) {
                        countryList = it.data?.countryMasterList
                        binding.spinnerCountry.adapter =
                            CountrySpinnerAdapter(orderPlaceActivity!!, countryList!!)
                        binding.spinnerCustomerCountry.adapter =
                            CountrySpinnerAdapter(orderPlaceActivity!!, countryList!!)
                    }
                }

                is Resource.Error -> {
                    orderPlaceActivity?.dismissProgress()
                    try {
                        val jsonObj = JSONObject(it.message)
                        val message = jsonObj.getString("message")
                        Utils.showSnackbar(binding.root, message)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }
                is Resource.Loading -> {
                    orderPlaceActivity?.showProgress()
                }
            }
        }
    }

    fun getStates(countryId: String) {
        addCustomerViewModel.getStateList(countryId)
        addCustomerViewModel.responseGetState.observe(orderPlaceActivity!!) {
            when (it) {
                is Resource.Success -> {
                    orderPlaceActivity?.dismissProgress()
                    if (!it.data?.stateMasterList.isNullOrEmpty()) {
                        stateList = it.data?.stateMasterList
                        binding.spinnerState.adapter =
                            StateSpinnerAdapter(orderPlaceActivity!!, stateList!!)
                    }
                }

                is Resource.Error -> {
                    orderPlaceActivity?.dismissProgress()
                    try {
                        val jsonObj = JSONObject(it.message)
                        val message = jsonObj.getString("message")
                        Utils.showSnackbar(binding.root, message)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }
                is Resource.Loading -> {
                    orderPlaceActivity?.showProgress()
                }
            }
        }
    }

    fun getCities(stateId: String) {
        addCustomerViewModel.getCityList(stateId)
        addCustomerViewModel.responseGetCity.observe(orderPlaceActivity!!) {
            when (it) {
                is Resource.Success -> {
                    orderPlaceActivity?.dismissProgress()
                    if (it.data?.statusCode == 1) {
                        if (!it.data?.cityMasterList.isNullOrEmpty()) {
                            cityList = it.data?.cityMasterList
                            binding.spinnerCity.adapter =
                                com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.add_customer.adapter.CitySpinnerAdapter(
                                    orderPlaceActivity!!,
                                    cityList!!
                                )
                            binding.spinnerCity.visibility = View.VISIBLE
                            binding.edtCity.visibility = View.GONE
                        }
                    } else {
                        cityList = ArrayList<CityMaster>()
                        binding.spinnerCity.adapter =
                            com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.add_customer.adapter.CitySpinnerAdapter(
                                orderPlaceActivity!!,
                                cityList!!
                            )
                        binding.spinnerCity.visibility = View.GONE
                        binding.edtCity.visibility = View.VISIBLE
                    }
                }

                is Resource.Error -> {
                    orderPlaceActivity?.dismissProgress()
                    try {
                        val jsonObj = JSONObject(it.message)
                        val message = jsonObj.getString("message")
                        Utils.showSnackbar(binding.root, message)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }
                is Resource.Loading -> {
                    orderPlaceActivity?.showProgress()
                }
            }
        }
    }


}