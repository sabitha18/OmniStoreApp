package com.armada.storeapp.ui.home.riva.riva_look_book.checkout.address

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.*
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.AddAddressRequestModel
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.ui.utils.Utils
import com.armada.storeapp.databinding.ActivityAddAddressBinding
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.checkout.address.adapter.CitySpinnerAdapter
import dagger.hilt.android.AndroidEntryPoint
import com.armada.storeapp.ui.home.riva.riva_look_book.checkout.address.adapter.SpinnerAdapter
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import kotlinx.android.synthetic.main.custom_alert.*
import kotlinx.android.synthetic.main.toolbar_default.*
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddAddressActivity : BaseActivity() {


    lateinit var sharedpreferenceHandler:SharedpreferenceHandler
    lateinit var binding: ActivityAddAddressBinding
    lateinit var addAddressViewModel: AddAddressViewModel
    private var strUserId: String = ""
    private var selectedLanguage="en"
    private var selectedCurrency="USD"
    private var strFirstName: String = ""
    private var strLastName = ""
    private var strEmail: String = ""
    private var strApartmentNumber = ""
    private var strCompany: String = ""
    private var strCity: String = ""
    private var strCityId = ""
    private var strAddressLine: String = ""
    private var strTelPhone: String = ""
    private var strStreet: String = ""
    private var strPostcode: String = ""
    private var strLocation: String = ""
    private var strBlock: String = ""

    private var strHouse: String = ""
    private var strRegion: String = ""
    private var strNotes: String = ""
    private var strIsDefault: String = "0"
    private var strCountry = ""
    private var strArea = ""
    private var strFloor = ""
    private var strFlat = ""

    private var strCountryId = ""
    private var strRegionId = ""
    private var strMobileNumber: String = ""
    private var strFax: String = ""
    private var addressId: String = ""
    private var isEdit: Boolean = false
    private var strFrom: String = "Add"
    private var loading: Dialog? = null
    private var zipRequired = false
    private val mapRequestCode = 201
    private var modelAddress: BaseModel? = null
    private var isStoreKuwait = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addAddressViewModel =
            ViewModelProvider(this).get(AddAddressViewModel::class.java)
        initToolbar()
        init()
    }


    private fun initToolbar() {
        setSupportActionBar(binding.toolbarActionbar.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val upArrow: Drawable? =
            ContextCompat.getDrawable(this@AddAddressActivity, R.drawable.ic_baseline_arrow_back_24)

        upArrow?.setColorFilter(
            ContextCompat.getColor(this@AddAddressActivity, R.color.black),
            PorterDuff.Mode.SRC_ATOP
        )
        upArrow?.setVisible(true, true)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        txtHead.text = resources.getString(R.string.add_address)
    }

    ///initializing required
    private fun init() {

        sharedpreferenceHandler = SharedpreferenceHandler(this)
        strUserId = sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_USER_ID, "")!!
        selectedLanguage=sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY,"en")!!
        selectedCurrency=sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_CURRENCY,"USD")!!
        isStoreKuwait = true
        binding.txtArea.text = resources.getString(R.string.area)
        binding.txtSelectedCountry.setText("")


        binding.edtMobile.filters += InputFilter.LengthFilter(10)

        ///Asked to not to show data if not matched with google and riva governorate
//        binding.edtState.visibility = View.GONE
//        binding.relState.visibility = View.VISIBLE

        binding.edtPhoneCode.isEnabled = false
//        binding.edtPhoneCodeLand.isEnabled = false

//        binding.chkShip.visibility = View.VISIBLE


        binding.chkShip.setOnClickListener()
        {
            strIsDefault = if (binding.chkShip.isChecked)
                "1"
            else "0"
        }

        binding.spinCountry.setSelection(0, false)
        binding.spinState.setSelection(0, false)


        binding.edtMobile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.isNotEmpty() == true) {
                    //println("Here i am mobile filter 111  " + edtMobile.text.toString())
                    if (binding.edtMobile.text.toString().startsWith("0")) {
                        binding.edtMobile.filters = arrayOf<InputFilter>(
                            InputFilter.LengthFilter(
                                Utils.getMobileNoLimitBasedOnCountryUpdated(
                                    strCountryId,
                                    binding.edtMobile.text.toString()
                                )
                            )
                        )
                    }
                }
            }

        })

        binding.txtSelectState.visibility = View.GONE
        binding.txtSelectCity.visibility = View.GONE
        binding.edtCity.isEnabled = false
        binding.edtState.isEnabled = false
        binding.edtCity.setText("")
        binding.edtState.setText("")
        binding.relState.visibility = View.VISIBLE
        binding.relCity.visibility = View.VISIBLE
        binding.spinState.visibility = View.VISIBLE
        binding.spinCity.visibility = View.VISIBLE
        binding.edtState.visibility = View.GONE
        binding.edtCity.visibility = View.GONE

        binding.txtSelectedCountry.setOnClickListener { binding.spinCountry.performClick() }
        binding.txtSelectState.setOnClickListener { binding.spinState.performClick() }
        binding.txtSelectCity.setOnClickListener { binding.spinCity.performClick() }

        binding.btnSaveAddress.setOnClickListener()
        {
            validation()
        }

        if (intent.hasExtra("from")) {
            strFrom = intent.getStringExtra("from").toString()

            if (strFrom == "edit") {
                txtHead.text = resources.getString(R.string.edit_add)
                binding.btnSaveAddress.text = resources.getString(R.string.update_address)
                isEdit = true
                val addressModel: AddAddressDataModel =
                    intent.getSerializableExtra("model") as AddAddressDataModel
                setAddress(addressModel)

                strCity = addressModel.city.toString()

            } else {
                //both not require for add address
            }
        }

        getCountryList()

    }


    fun getCountryList() {
        addAddressViewModel.getCountryList(selectedLanguage)
        addAddressViewModel.responseCoutryList.observe(this) {
            when (it) {
                is Resource.Success -> {
                    handleCountryListResponse(it.data)
                    dismissProgress()
                }

                is Resource.Error -> {
                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    private fun handleCountryListResponse(response: CountryListResponseModel?) {
        dismissProgress()
        if (response != null) {
            if (response.data != null && response.data.size > 0) {
                val arrListCountryList: ArrayList<CountryListDataModel> = ArrayList()
                val model = CountryListDataModel(
                    "",
                    resources.getString(R.string.country),
                    "",
                    "0",
                    "0",
                    "NA"
                )
                arrListCountryList.add(model)
                arrListCountryList.addAll(response.data)
                val spinAdapter = SpinnerAdapter(
                    this@AddAddressActivity,
                    R.layout.spinner_items,
                    arrListCountryList,
                    resources
                )
                binding.spinCountry.adapter = spinAdapter

                binding.spinCountry.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parentView: AdapterView<*>,
                            selectedItemView: View?, position: Int, id: Long
                        ) {
                            setCountry(position, arrListCountryList)
                        }

                        override fun onNothingSelected(arg0: AdapterView<*>) {}
                    }

                if (intent.hasExtra("address_model")) {
                    modelAddress = intent!!.getSerializableExtra("address_model") as BaseModel

                    for (i in 0 until arrListCountryList.size) {
                        //Here we are comparing country code (store code) in country list api. If not found then riva not delivering in that country
                        if (arrListCountryList[i].id == modelAddress!!.countryCode) {
                            binding.edtBlock.setText(modelAddress!!.block_no)
                            binding.edtStreet.setText(Utils.removeUnnamedRoad(modelAddress!!.street))

                            binding.txtSelectedCountry.setText(modelAddress!!.country)
                            binding.edtArea.setText(modelAddress!!.area)

                            if (isStoreKuwait) {
                                binding.edtStreet.setText(Utils.removeUnnamedRoad(modelAddress!!.street))
                            } else {

                                binding.edtAptHouseVilla.setText(modelAddress!!.building)
                            }

                            strCountry = modelAddress!!.country

                            ///comparing and showing spinner state and city
                            if (arrListCountryList[i].regions!!.size > 0) {

                                for (m in 0 until arrListCountryList[i].regions!!.size) {

                                    setState(arrListCountryList[i].regions, false)
                                    /**
                                     * changes made comparing partial string using contains operator
                                     */
                                    // println("Governorate add: "+modelAddress!!.governorate+ " :: "+arrListCountryList[i].regions[m].city_name)
                                    if (modelAddress!!.governorate.contains(arrListCountryList[i].regions!![m].name!!)) {

                                        binding.txtSelectState.text =
                                            arrListCountryList[i].regions!![m].name
                                        binding.edtState.setText(arrListCountryList[i].regions!![m].name)
                                        strRegionId =
                                            arrListCountryList[i].regions!![m].id.toString()
                                        strRegion =
                                            arrListCountryList[i].regions!![m].name.toString()

                                        //println("Here i am state name  444  " + strRegion + "   " + txtSelectState.text.toString())
                                        setCity(arrListCountryList[i].regions!![m].cities, false)
                                        /**
                                         * changes made comparing partial string using contains operator
                                         */
                                        for (c in 0 until arrListCountryList[i].regions!![m].cities!!.size) {
                                            if (modelAddress!!.area.contains(arrListCountryList[i].regions!![m].cities!![c].name.toString())) {

                                                strCityId =
                                                    arrListCountryList[i].regions!![m].cities!![c].id.toString()
                                                strCity =
                                                    arrListCountryList[i].regions!![m].cities?.get(c)?.name.toString()
                                                binding.txtSelectCity.text =
                                                    arrListCountryList[i].regions!![m].cities!![c].name
                                                binding.edtCity.setText(arrListCountryList[i].regions!![m].cities!![c].name)
                                                break
                                            }
                                        }

                                        break
                                    }
                                }

                            }
                            for (a in 0 until arrListCountryList.size) {

                                if (modelAddress!!.countryCode == arrListCountryList[a].id) {
                                    binding.edtPhoneCode.setText(
                                        Utils.addPlus(
                                            Utils.removePlus(
                                                arrListCountryList[a].dial_code ?: ""
                                            )
                                        )
                                    )
//                                    binding.edtPhoneCodeLand.setText(
//                                        Utils.addPlus(
//                                            Utils.removePlus(
//                                                arrListCountryList[a].dial_code ?: ""
//                                            )
//                                        )
//                                    )
                                    strCountryId = arrListCountryList[a].id.toString()
                                    binding.edtMobile.text.clear()
                                    //println("Here i am mobile filter 222  " + edtMobile.text.toString())
                                    binding.edtMobile.filters = arrayOf<InputFilter>(
                                        InputFilter.LengthFilter(
                                            Utils.getMobileNoLimitBasedOnCountryUpdated(
                                                arrListCountryList[a].id, ""
                                            )
                                        )
                                    ) //set max mobile number length
                                }
                            }
                            break
                        }
                    }
                } else {

                    /// Default selection for country by store wise only for new address
                    if (strCountry.isNullOrBlank()) {
                        strCountry = arrListCountryList[1].full_name.toString()
                        strCountryId = arrListCountryList[1].id.toString()
                        binding.txtSelectedCountry.setText(strCountry)
                    }

                    for (k in 0 until arrListCountryList.size) {
                        if (strCountryId == arrListCountryList[k].id) {
                            if (arrListCountryList[k].regions != null && arrListCountryList[k].regions!!.size > 0) {
                                setState(arrListCountryList[k].regions, true)
                                break
                            }
                        }
                    }
                }
            }
        }

        binding.addScroll.visibility = View.VISIBLE
    }

    private fun setCountry(position: Int, arrListCountryList: ArrayList<CountryListDataModel>) {
        if (position != 0) {
            strCountry = Utils.getDynamicStringFromApi(
                this@AddAddressActivity,
                arrListCountryList[position].full_name
            )
            strCountryId = arrListCountryList[position].id.toString()

            binding.txtSelectedCountry.setText(strCountry)
            if (arrListCountryList[position].has_state == "1") {
//                binding.relState.visibility = View.VISIBLE
//                binding.edtState.visibility = View.GONE

//                binding.relCity.visibility = View.GONE
//                binding.edtCity.visibility = View.VISIBLE

                setState(arrListCountryList[position].regions!!, true)
            } else {
                strRegion = ""
                strRegionId = ""
                binding.edtState.setText(strRegion)
                binding.txtSelectState.text = strRegion
//                binding.relState.visibility =
//                View.VISIBLE /// Was gone before did for requiremnt not show data if not matched with api
//                binding.edtState.visibility =
//                View.GONE /// Was visible before  did for requiremnt not show data if not matched with api

//                binding.relCity.visibility = View.GONE
//                binding.edtCity.visibility = View.GONE
            }

            binding.edtPhoneCode.setText(
                Utils.addPlus(
                    Utils.removePlus(
                        arrListCountryList[position].dial_code ?: ""
                    )
                )
            )
//            binding.edtPhoneCodeLand.setText(
//                Utils.addPlus(
//                    Utils.removePlus(
//                        arrListCountryList[position].dial_code ?: ""
//                    )
//                )
//            )
        }
    }

    ///validation
    private fun validation() {
        strFirstName = binding.edtFirstName.text.toString()
        strLastName = binding.edtSecondName.text.toString()
        strCompany = binding.edtCompany.text.toString()
//        strTelPhone = binding.edtLandline.text.toString()
        strApartmentNumber = binding.edtAptHouseVilla.text.toString()
        //println("Here i am mobile filter 444  " + edtMobile.text.toString())
        binding.edtMobile.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(
                Utils.getMobileNoLimitBasedOnCountryUpdated(
                    strCountryId,
                    binding.edtMobile.text.toString()
                )
            )
        )

        strMobileNumber =
            binding.edtMobile.text.toString()
        strBlock = binding.edtBlock.text.toString()

        if (isStoreKuwait) {
            strStreet = binding.edtStreet.text.toString()
        } else {
            strHouse = binding.edtAptHouseVilla.text.toString()
        }

        // strStreet = edtStreet.text.toString()
        strRegion = binding.edtState.text.toString()
        strCity = binding.edtCity.text.toString()
        strEmail = binding.edtEmail.text.toString()
        strArea = binding.edtArea.text.toString()

        if (strFirstName.isEmpty())
            Utils.showSnackbar(binding.addScroll, resources.getString(R.string.add_first_name))
        else if (strLastName.isEmpty())
            Utils.showSnackbar(binding.addScroll, resources.getString(R.string.add_last_name))
        else if (strEmail.isEmpty())
            Utils.showSnackbar(binding.addScroll, resources.getString(R.string.please_enter_email))
        else if (strCountry.isEmpty()) run {
            Utils.showSnackbar(binding.addScroll, resources.getString(R.string.add_country))
        }
        else if (strRegion.isEmpty())
            Utils.showSnackbar(binding.addScroll, resources.getString(R.string.add_region))
        else if (strCity.isEmpty())
            Utils.showSnackbar(binding.addScroll, resources.getString(R.string.add_city))
        else if (strBlock.trim()
                .isNullOrEmpty() && isStoreKuwait
        ) {
            Utils.showSnackbar(binding.addScroll, resources.getString(R.string.enter_block))
        } else if (strStreet.trim().isEmpty()) {
            if (isStoreKuwait) {
                Utils.showSnackbar(
                    binding.addScroll,
                    resources.getString(R.string.enter_valid_street)
                )
            } else {
                Utils.showSnackbar(
                    binding.addScroll,
                    resources.getString(R.string.enter_valid_delivery_address)
                )
            }
        } else if (strApartmentNumber.trim().isEmpty()) {
            Utils.showSnackbar(
                binding.addScroll,
                resources.getString(R.string.enter_apt_villa_house_building)
            )
        } else if (!Utils.isInternational(strCountryId) && binding.edtMobile.text.toString().length != Utils.getMobileNoLimitBasedOnCountryUpdated(
                strCountryId, binding.edtMobile.text.toString()
            )
        ) {
            Utils.showSnackbar(binding.addScroll, resources.getString(R.string.invalid_mobile))
        } else if (Utils.isInternational(strCountryId) && strMobileNumber.length <= 5) {
            Utils.showSnackbar(binding.addScroll, resources.getString(R.string.invalid_mobile))
        } else if (strTelPhone.isNotEmpty()) {
            if (strTelPhone.length < 5) {
                Utils.showSnackbar(
                    binding.addScroll,
                    resources.getString(R.string.invalid_telephone)
                )
            } else {
                if (!isEdit)
                    addAddress()
                else editAddress()
            }
        } else {
            if (!isEdit)
                addAddress()
            else editAddress()
        }
    }

    ///setting address to edit
    private fun setAddress(model: AddAddressDataModel) {
        //println("Here i am address dataaa  $model")
        addressId = model.address_id.toString()
        binding.edtFirstName.setText(model.firstname)
        binding.edtSecondName.setText(model.lastname)
        binding.edtBlock.setText(model.block)
        binding.edtEmail.setText(model.email)

        binding.edtCity.setText(model.city)
        binding.txtSelectCity.text = model.city

        binding.edtState.setText(model.region)

        if (isStoreKuwait) {
            binding.edtStreet.setText(Utils.removeUnnamedRoad(model.street))
        } else {
        }
        binding.edtAptHouseVilla.setText(model.apartment_number)

        binding.edtArea.setText(model.area)
        binding.txtSelectedCountry.setText(model.country)
        binding.txtSelectState.text = model.region

        binding.edtState.setText(model.region)

//        if (!model.region_id.isNullOrEmpty()) {
//            binding.relState.visibility = View.VISIBLE
//            binding.edtState.visibility = View.GONE
//        } else {
//            binding.relState.visibility =
//                View.VISIBLE // was gone before did for requirement not show data if not matched with api
//            binding.edtState.visibility =
//                View.GONE /// Was visible before did for requirement not show data if not matched with api
//        }

        strCountry = model.country.toString()
        strCountryId = model.country_id.toString()

        strRegion = model.region.toString()
        if (!model.region_id.isNullOrEmpty()) {
            strRegionId = model.region_id.toString()
        }

        binding.edtMobile.setText(model.mobile_number)
        binding.edtMobile.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(
                Utils.getMobileNoLimitBasedOnCountryUpdated(
                    strCountryId,
                    binding.edtMobile.text.toString()
                )
            )
        )
        binding.edtPhoneCode.setText(Utils.addPlus(Utils.removePlus(model.phone_code ?: "")))
//        binding.edtLandline.setText(
//            model.telephone
//        )
        binding.chkShip.text = resources.getString(R.string.set_as_default)
        binding.chkShip.isChecked = model.is_default_shipping.toString() == "1"

    }


    ///set state
    private fun setState(
        regionListResponseModel: ArrayList<CountryListRegionModel>?,
        bool_clicked: Boolean
    ) {

        if (bool_clicked && !isEdit) {
            strRegion = ""
            strRegionId = ""
            binding.edtState.setText(strRegion)
            binding.txtSelectState.text = strRegion
            strCity = ""
            strCityId = ""
            binding.edtCity.setText(strCity)
            binding.txtSelectCity.text = strCity
            binding.spinCity?.adapter = null
        }

        ///condition removed to hide edittext not matched with google api

        if (regionListResponseModel != null && regionListResponseModel.size > 0) {
            if (regionListResponseModel.get(0).id != "-1") {
                val model =
                    CountryListRegionModel(null, "", "-1", resources.getString(R.string.select))
                regionListResponseModel.add(0, model)
            }
//            binding.relState.visibility = View.VISIBLE

            val spinAdapter = StateSpinnerAdapter(
                this@AddAddressActivity,
                R.layout.spinner_items,
                regionListResponseModel,
                resources
            )
            binding.spinState.adapter = spinAdapter

            binding.spinState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View,
                    position: Int,
                    id: Long
                ) {

                    if (position != 0) {
                        strRegion = Utils.getDynamicStringFromApi(
                            this@AddAddressActivity,
                            regionListResponseModel[position].name
                        )
                        strRegionId = regionListResponseModel[position].id.toString()
                        binding.edtState.setText(strRegion)
                        binding.txtSelectState.text = strRegion
                        //println("Here i am state name  222  " + strRegion + "   " + txtSelectState.text.toString())
                        binding.spinCity?.adapter = null
//                        binding.relCity.visibility = View.GONE
//                        binding.edtCity.visibility = View.VISIBLE

                        if (regionListResponseModel[position].cities!!.size > 0)
                            setCity(regionListResponseModel[position].cities!!, true)
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {}
            }
        }
    }
    /////

    ///set city
    private fun setCity(
        cityListResponseModel: ArrayList<CountryListCityModel>?,
        bool_clicked: Boolean
    ) {

        if (bool_clicked && !isEdit) {
            strCity = ""
            strCityId = ""
            binding.edtCity.setText(strCity)
            binding.txtSelectCity.text = strCity
        }

        if (cityListResponseModel!!.size > 0) {
//            binding.relCity.visibility = View.VISIBLE
//            binding.edtCity.visibility = View.GONE

            if (cityListResponseModel[0].id != "-1") {
                val model = CountryListCityModel(null, "-1", resources.getString(R.string.select))
                cityListResponseModel.add(0, model)
            }
            val cityadapter =
                CitySpinnerAdapter(
                    this@AddAddressActivity,
                    R.layout.spinner_items,
                    cityListResponseModel,
                    resources
                )
            binding.spinCity.adapter = cityadapter

            binding.spinCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View, position: Int, id: Long
                ) {

                    if (position != 0) {
                        strCity = Utils.getDynamicStringFromApi(
                            this@AddAddressActivity,
                            cityListResponseModel[position].name
                        )
                        strCityId = cityListResponseModel[position].id.toString()
                        binding.edtCity.setText(strCity)
                        binding.txtSelectCity.text = strCity
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {}
            }
        }
    }

    ///add address api call
    private fun addAddress() {
        val shippingAddress = AddAddressRequestModel(
            customer_id = strUserId,
            firstname = strFirstName,
            lastname = strLastName,
            telephone = strMobileNumber,
            apartment_number = strApartmentNumber,
            floor_number = "",
            building_number = "",
            street = strStreet,
            block = strBlock,
            city = strCity,
            region = strRegion,
            region_id = strRegionId,
            country_id = strCountryId,
            phone_code = Utils.removePlus(binding.edtPhoneCode.text.toString()),
            is_default_billing = strIsDefault.toInt(),
            is_default_shipping = strIsDefault.toInt(),
            notes = "",
            postcode = strPostcode,
            prefix = "",
            area = strArea,
            mobile_number = strMobileNumber,
            jeddah = "",
            short_address = "",
            latitude = "",
            longitude = ""
        )
        addAddressViewModel.addAddress(selectedLanguage,shippingAddress)
        addAddressViewModel.responseAddAddress.observe(this) {
            when (it) {
                is Resource.Success -> {
                    handleAddress(it.data!!)
                    dismissProgress()
                }

                is Resource.Error -> {
                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    private fun editAddress() {

        val shippingAddress = AddAddressRequestModel(
            customer_id = strUserId,
            firstname = strFirstName,
            lastname = strLastName,
            telephone = strMobileNumber,
            apartment_number = strApartmentNumber,
            floor_number = strFloor,
            building_number = "",
            street = strStreet,
            block = strBlock,
            city = strCity,
            region = strRegion,
            region_id = strRegionId,
            country_id = strCountryId,
            phone_code = Utils.removePlus(binding.edtPhoneCode.text.toString()),
            is_default_billing = (strIsDefault ?: "0").toInt(),
            is_default_shipping = (strIsDefault ?: "0").toInt(),
            notes = strNotes,
            postcode = strPostcode,
            prefix = "",
            area = strArea,
            mobile_number = strMobileNumber,
            jeddah = "",
            short_address = strAddressLine
        )

        addAddressViewModel.editAddress(selectedLanguage,addressId, shippingAddress)
        addAddressViewModel.responseEditAddress.observe(this) {
            when (it) {
                is Resource.Success -> {
                    handleAddress(it.data!!)
                    dismissProgress()
                }

                is Resource.Error -> {
                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    private fun handleAddress(addressmodel: AddAddressResponseModel) {

        dismissProgress()
        if (addressmodel.status == 200) {
            if (strFrom == "edit") {
                if (!this@AddAddressActivity.isFinishing) {

                    val dialogView = LayoutInflater.from(this@AddAddressActivity)
                        .inflate(R.layout.custom_alert, null)
                    val builder =
                        androidx.appcompat.app.AlertDialog.Builder(this@AddAddressActivity)
                            .setView(dialogView)
                    val dialog = builder.show()
                    dialog.setCancelable(false)

                    dialog.txtAlert.text = ""
                    dialog.txtAlert.visibility = View.GONE
                    dialog.txtAlertMsg.text =
                        resources.getString(R.string.edit_address_success)
                    dialog.buttonAction.text = resources.getString(R.string.ok)
                    dialog.btnContinue.text = resources.getString(R.string.proceed)

                    dialog.btnContinue.visibility = View.GONE
                    dialog.buttonAction.setOnClickListener {
                        dialog.dismiss()

                        val returnIntent = Intent()
                        try {
                            addressmodel?.data?.get(0)?.email = strEmail
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }
                        returnIntent.putExtra("model", addressmodel)
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }
                }

            } else {

                if (!this@AddAddressActivity.isFinishing) {

                    val dialogView = LayoutInflater.from(this@AddAddressActivity)
                        .inflate(R.layout.custom_alert, null)
                    val builder =
                        androidx.appcompat.app.AlertDialog.Builder(this@AddAddressActivity)
                            .setView(dialogView)
                    val dialog = builder.show()
                    dialog.setCancelable(false)

                    dialog.txtAlert.text = ""
                    dialog.txtAlert.visibility = View.GONE
                    dialog.txtAlertMsg.text = resources.getString(R.string.add_address_success)
                    dialog.buttonAction.text = resources.getString(R.string.ok)
                    dialog.btnContinue.text = resources.getString(R.string.proceed)

                    dialog.btnContinue.visibility = View.GONE
                    dialog.buttonAction.setOnClickListener {
                        dialog.dismiss()
                        val returnIntent = Intent()
                        try {
                            addressmodel?.data?.get(0)?.email = strEmail
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }
                        returnIntent.putExtra("model", addressmodel)
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }
                }
            }
        } else {
            Utils.showSnackbar(binding.addScroll, addressmodel.message.toString())

        }
    }

    ///spinner for states
    internal class StateSpinnerAdapter
    /*************  CustomAdapter Constructor  */
        (
        private val activity: Activity,
        textViewResourceId: Int,
        private val data: java.util.ArrayList<CountryListRegionModel>,
        var res: Resources


    ) : ArrayAdapter<CountryListRegionModel>(activity, textViewResourceId, data) {
        internal var font: Typeface? = null
        private var tempValues: String? = null
        internal var inflater: LayoutInflater =
            activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        internal var locale: Locale? = null
        internal var typeSemiBold: Typeface? = null
        internal var typeNormal: Typeface? = null

        init {
            /***********  Layout inflator to call external xml layout ()  */

        }

        /********** Take passed values  */

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return getCustomView(position, convertView, parent)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return getCustomView(position, convertView, parent)
        }

        private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {


            /********** Inflate spinner_rows.xml file for each row ( Defined below )  */
            val row = inflater.inflate(R.layout.spinner_items, parent, false)
            /***** Get each Model object from Arraylist  */
            tempValues = null
            tempValues = Utils.getDynamicStringFromApi(activity, data[position].name)

            val label = row.findViewById<View>(R.id.txtItem) as TextView
            label.text = tempValues
            if (position == 0)
                label.setTextColor(ContextCompat.getColor(activity, R.color.stroke_color))
            else label.setTextColor(ContextCompat.getColor(activity, R.color.black))

            return row
        }
    }

    ///loading dialog
    private fun showProgress() {

        if (!this@AddAddressActivity.isFinishing) {
            if (loading == null) {
                loading = Dialog(this@AddAddressActivity, R.style.TranslucentDialog)
                loading?.setContentView(R.layout.custom_loading_view)
                loading?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loading?.setCanceledOnTouchOutside(false)
                loading?.show()
            }else{
                if(!!loading?.isShowing!!){
                    loading?.show()
                }
            }
        }
    }

    private fun dismissProgress() {
        if (loading != null && loading?.isShowing == true)
            loading?.dismiss()
    }

    ///back pressed
    override fun onBackPressed() {

        if (strFrom.equals("checkout", ignoreCase = true)) {
            val returnIntent = Intent()
            returnIntent.putExtra("model", "")
            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()

        } else super.onBackPressed()
    }


    ///back menu item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (strFrom.equals("checkout", ignoreCase = true)) {
                    val returnIntent = Intent()
                    returnIntent.putExtra("model", "")
                    setResult(Activity.RESULT_CANCELED, returnIntent)
                    finish()

                } else this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}
