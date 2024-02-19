package com.armada.storeapp.ui.home.riva.riva_look_book.checkout

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.*
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.data.model.response.payment_gatway_response.*
import com.armada.storeapp.data.model.response.payment_gatway_response.invoice_details.InvoiceDetailsResponse
import com.armada.storeapp.databinding.ActivityCheckoutBinding
import com.armada.storeapp.databinding.LayoutOrderPlacedBinding
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.bag.BagActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.checkout.adapter.CheckoutItemAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.checkout.address.AddAddressActivity
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.armada.storeapp.ui.utils.Utils
import com.bumptech.glide.util.Util
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_alert.*
import kotlinx.android.synthetic.main.layout_alert_success.txtAlert
import kotlinx.android.synthetic.main.layout_alert_success.txtAlertMsg
import kotlinx.android.synthetic.main.toolbar_default.*
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


@AndroidEntryPoint
class CheckoutActivity : BaseActivity() {


    private var selectedLanguage = "en"
    private var selectedCurrency = "USD"
    private var strEmail: String = ""
    private var strOrderId: String? = null
    lateinit var binding: ActivityCheckoutBinding
    lateinit var checkoutViewModel: CheckoutViewModel
    lateinit var checkoutItemAdapter: CheckoutItemAdapter
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    private var stockModel: CheckStockResponseModel.Data? = null
    private var shakeAnime: Animation? = null
    var strUserId = ""
    private var strCouponCode = ""
    private var loading: Dialog? = null
    private var strMobile: String? = ""
    private var isWalletVisible = false
    private var isWalletApplied = false
    private var specialPrice = 0.0
    private var modelStockRequest: StockRequestModel? = null
    private var creditAmount = ""
    private var tax: Double = 0.0
    private var hasSendPaymentLinkToCustomer = false
    private var paymentDone = false
    private var paymentStatus: String? = null
    private var selectedPaymentMethod = ""
    var invoiceResponse: InvoiceDetailsResponse? = null
    private val ADDRESS_REQ_CODE = 202
    var isDiscountViewExpanded = false
    var orderPlacedDialog: Dialog? = null
    var currentInvoiceId: String? = null
    var invoicePaymentStatusResponse: GetInvoiceDetailsResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkoutViewModel =
            ViewModelProvider(this@CheckoutActivity).get(CheckoutViewModel::class.java)
        initToolbar()
        init()
        setOnCLickListener()
        setItemAdapter()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarActionbar.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val upArrow: Drawable? =
            ContextCompat.getDrawable(this@CheckoutActivity, R.drawable.ic_baseline_arrow_back_24)

        upArrow?.setColorFilter(
            ContextCompat.getColor(this@CheckoutActivity, R.color.black),
            PorterDuff.Mode.SRC_ATOP
        )
        upArrow?.setVisible(true, true)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        txtHead.text = resources.getString(R.string.btn_checkout)
    }

    private fun init() {

        sharedpreferenceHandler = SharedpreferenceHandler(this)
        strUserId = sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_USER_ID, "")!!
        selectedLanguage =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY, "en")!!
        selectedCurrency =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_CURRENCY, "USD")!!
//        currentInvoiceId = sharedpreferenceHandler.getData(SharedpreferenceHandler.INVOICE_ID, "")
        shakeAnime = AnimationUtils.loadAnimation(this, R.anim.shake_anim)

        intent?.let {
            if (it?.hasExtra("model") && it?.getSerializableExtra("model") != null) {

                stockModel = intent.getSerializableExtra("model") as CheckStockResponseModel.Data
                modelStockRequest =
                    intent.getSerializableExtra("model_request") as StockRequestModel
                binding.edtCoupon.imeOptions = EditorInfo.IME_ACTION_DONE
                setTotal()
            }
        }

        try {
            val address =
                sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_USER_ADDRESS, "")
            if (!address.isNullOrEmpty()) {
                val gson = Gson()
                val userAddress = gson.fromJson(
                    address,
                    AddAddressResponseModel::class.java
                )
                setDisplayAddress(userAddress?.data?.get(0)!!)
                stockModel?.userSelectedAddress = userAddress?.data?.get(0)
                binding.tvEdit.visibility = View.VISIBLE
                binding.tvCustomerAddress.visibility = View.VISIBLE
                binding.imageViewDeleteAddress.visibility = View.VISIBLE
                binding.tvAddAddress.visibility = View.GONE

            } else {
                binding.tvEdit.visibility = View.GONE
                binding.imageViewDeleteAddress.visibility = View.GONE
                binding.tvAddAddress.visibility = View.VISIBLE
                binding.tvCustomerAddress.visibility = View.GONE
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        stockModel?.items.let {
            binding.tvNoOfItems.text = it?.size?.toString() + " ITEMS"
            binding.tvItemCount.text = it?.size?.toString()
        }

//
//        if (currentInvoiceId.isNullOrEmpty()) {
//            setPaymentView()
//        } else {
//            checkoutViewModel.getPaymentStatus(currentInvoiceId!!)
//        }

    }

    fun setPaymentView() {
        val paymentStatus =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.PAYMENT_STATUS, "")
        when (paymentStatus) {
            "CREATED" -> {
                binding.tvPaymentLinkSent.visibility = View.VISIBLE
                binding.tvResendLink.visibility = View.VISIBLE
                binding.tvPaymentSuccess.visibility = View.GONE
                binding.tvSendPaymentLink.visibility = View.GONE
            }
            "PAID" -> {
                binding.tvEdit.visibility = View.GONE
                binding.imageViewDeleteAddress.visibility = View.GONE
                binding.tvPaymentLinkSent.visibility = View.GONE
                binding.tvPaymentSuccess.visibility = View.VISIBLE
                binding.tvResendLink.visibility = View.GONE
                binding.tvSendPaymentLink.visibility = View.GONE
            }
            "EXPIRED" -> {
                binding.tvPaymentLinkSent.visibility = View.GONE
                binding.tvPaymentSuccess.visibility = View.GONE
                binding.tvSendPaymentLink.visibility = View.VISIBLE
                binding.tvResendLink.visibility = View.GONE
            }
            " " -> {
                binding.tvPaymentLinkSent.visibility = View.GONE
                binding.tvPaymentSuccess.visibility = View.GONE
                binding.tvSendPaymentLink.visibility = View.VISIBLE
                binding.tvResendLink.visibility = View.GONE
            }
        }
    }

    private fun setOnCLickListener() {

        binding.btnApplyCoupon.setOnClickListener()
        {
            strCouponCode = binding.edtCoupon.text.toString()
            if (binding.edtCoupon.text.toString().isNotEmpty()) {
                applyCoupon()
            } else {
                binding.edtCoupon.startAnimation(shakeAnime)
            }
        }

        binding.btnRemoveCoupon.setOnClickListener {
            strCouponCode = ""
            applyCoupon()
        }

        binding.tvEdit.setOnClickListener()
        {
            val intent = Intent(this, AddAddressActivity::class.java)
            if (stockModel?.userSelectedAddress != null && !stockModel?.userSelectedAddress?.address_id.isNullOrEmpty()) {
                intent.putExtra("model", stockModel?.userSelectedAddress)
                intent.putExtra("from", "edit")
            }
            startActivityForResult(intent, ADDRESS_REQ_CODE)
        }

        binding.tvAddAddress.setOnClickListener()
        {
            val intent = Intent(this, AddAddressActivity::class.java)
            startActivityForResult(intent, ADDRESS_REQ_CODE)
        }


        binding.btnConfirmOrder.setOnClickListener {
            strMobile =
                "+965" + stockModel?.userSelectedAddress?.mobile_number
            if (!paymentDone) {
                Utils.showSnackbar(binding.cvBottom, resources.getString(R.string.complete_payment))
            } else if ((stockModel?.userSelectedAddress?.address_id.isNullOrEmpty())) {
                Utils.showSnackbar(binding.cvBottom, resources.getString(R.string.error_address))
            } else if (!binding.ckhWhatsapp.isChecked) {
                Utils.showSnackbar(binding.cvBottom, getString(R.string.whatsapp_condition_error))
            } else {
                confirmOrder()
            }
        }

        binding.tvSendPaymentLink.setOnClickListener {
            if (!hasSendPaymentLinkToCustomer) {
                if (stockModel?.userSelectedAddress != null) {
                    sendPaymentLinkToCustomerApi()
                } else {
                    Utils.showSnackbar(binding.cvBottom, "Please add address")
                }

            } else
                Utils.showSnackbar(binding.root, "Already sent payment link to customer")

        }

        binding.tvResendLink.setOnClickListener {
            if (hasSendPaymentLinkToCustomer) {
                sendPaymentLinkToCustomerApi()
            }
        }

        binding.imageViewExpandCollapse.setOnClickListener {
            if (isDiscountViewExpanded) {
                isDiscountViewExpanded = false
                binding.cvCouponView.visibility = View.GONE
                binding.imageViewExpandCollapse.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_baseline_add_24,
                        resources.newTheme()
                    )
                )
            } else {
                isDiscountViewExpanded = true
                binding.cvCouponView.visibility = View.VISIBLE
                binding.imageViewExpandCollapse.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_baseline_minimize_24,
                        resources.newTheme()
                    )
                )
            }
        }

        binding.imageViewDeleteAddress.setOnClickListener {

            val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_alert, null)
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
            val dialog = builder.show()
            dialog.setCancelable(false)

            dialog.txtAlert.text = resources.getString(R.string.are_you_sure)
            dialog.txtAlertMsg.text = resources.getString(R.string.delete_address)
            dialog.buttonAction.text = resources.getString(R.string.no)
            dialog.btnContinue.text = resources.getString(R.string.yes)

            dialog.btnContinue.setOnClickListener {
                try {
                    deleteAddressApi()
                    dialog.dismiss()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                dialog.dismiss()

            }
            dialog.buttonAction.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun setItemAdapter() {
        binding.recyclerView2.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        binding.recyclerView2.layoutManager =
            LinearLayoutManager(
                this@CheckoutActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        checkoutItemAdapter = CheckoutItemAdapter(
            this@CheckoutActivity,
            stockModel?.items
        )
        binding.recyclerView2.adapter = checkoutItemAdapter
    }

    private fun setTotal() {
        var subTotal = 0.0
        var shippingPrice = 0.0
        var tax = 0.0
        var discount = 0.0

        subTotal=stockModel?.totals?.subtotal?.toDouble()!!

        if (!stockModel?.totals?.shipping_amount.isNullOrEmpty()) {
            if ((stockModel?.totals?.shipping_amount?.toDouble() ?: 0.0) > 0) {
                shippingPrice = stockModel?.totals?.shipping_amount?.toDouble()!!
            }
        }

        if (!stockModel?.totals?.tax.isNullOrEmpty()) {
            if ((stockModel?.totals?.tax?.toDouble() ?: 0.0) > 0) {
                tax = stockModel?.totals?.tax?.toDouble()!!
            }
        }

        if (stockModel?.totals?.discount_amount != null && stockModel!!.totals.discount_amount.isNotEmpty() && stockModel!!.totals!!.discount_amount!!.toDouble() > 0) {
            discount = stockModel!!.totals!!.discount_amount!!.toDouble()
        }

        val orderTotal = subTotal + shippingPrice + tax - discount


        //-------------------------------------------------------------------------
        // Handling views

        binding.discountCollapseGroup.visibility = View.GONE
        binding.taxCollapse.visibility = View.GONE

        if (stockModel!!.totals!!.is_coupon_added != null && stockModel!!.totals!!.is_coupon_added.equals(
                "1"
            )
        ) {
            binding.edtCoupon.setText(stockModel!!.totals!!.coupon_code)
            binding.edtCoupon.isEnabled = false
            binding.btnApplyCoupon.text = resources.getString(R.string.remove)

            binding.btnApplyCoupon.visibility = View.GONE
            binding.btnRemoveCoupon.visibility = View.VISIBLE

        } else {
            binding.edtCoupon.isEnabled = true
            binding.btnApplyCoupon.text = resources.getString(R.string.apply)

            binding.btnApplyCoupon.visibility = View.VISIBLE
            binding.btnRemoveCoupon.visibility = View.GONE
        }

        binding.tvSubTotal.text = (Utils.getPriceFormatted(subTotal.toString(), selectedCurrency))

        binding.tvOrderTotal.text =
            (Utils.getPriceFormatted(orderTotal.toString(), selectedCurrency))

//        if (!stockModel?.totals?.special_savings.isNullOrEmpty()) {
//            specialPrice = stockModel?.totals?.special_savings?.toDouble() as Double
//        }

        if (shippingPrice > 0) {
            binding.tvShippingCharge.text =
                (Utils.getPriceFormatted(shippingPrice.toString(), selectedCurrency))
        } else {
            binding.tvShippingCharge.text = (resources.getString(R.string.free))
        }

        if (discount > 0) {
            binding.tvDiscountAmount.text =
                String.format("-" + Utils.getPriceFormatted(discount.toString(),
                        selectedCurrency))
            binding.discountCollapseGroup.visibility = View.VISIBLE

        } else {
            binding.discountCollapseGroup.visibility = View.GONE
        }

        if (tax > 0) {
            binding.tvTax.text =
               Utils.getPriceFormatted(tax.toString(),
                    selectedCurrency)
            binding.taxCollapse.visibility = View.VISIBLE

        } else {
            binding.taxCollapse.visibility = View.GONE
        }

        binding.tvOrderTotal.text =
            (Utils.getPriceFormatted(
               orderTotal.toString(),
                selectedCurrency
            ))
        binding.tvTotalAmount.text =
            Utils.getPriceFormatted(stockModel!!.totals!!.grand_total, selectedCurrency)


        val grandTotalWithDiscount = stockModel?.totals?.grand_total?.toDouble() ?: 0.0
        if (grandTotalWithDiscount <= 0.0) {
            selectedPaymentMethod = "free"
        }

        if (stockModel?.totals?.coupon_code?.isNullOrEmpty() == false) {
            binding.edtCoupon.setText(stockModel?.totals?.coupon_code)
            binding.imageViewExpandCollapse.performClick()
            binding.btnRemoveCoupon.visibility = View.VISIBLE
            binding.btnApplyCoupon.visibility = View.GONE
        }

    }

    // Apply - Remove Coupon -----------------------------------------------------------------------
    private fun applyCoupon() {
        val reqModel =
            CouponRequestModel(quote_id = stockModel!!.quote_id!!, coupon_code = strCouponCode)
        checkoutViewModel.applyCoupon(selectedLanguage, selectedCurrency, reqModel)
        checkoutViewModel.responseApplyCoupon.observe(this) {
            when (it) {
                is Resource.Success -> {
                    handleApplyCouponResponse(it.data!!)
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


    private fun handleApplyCouponResponse(response: StockResponseModel) {
        val orderModel = response.data
        if (response.status == 200) {
            if (strCouponCode != "") {

                binding.btnApplyCoupon.visibility = View.GONE
                binding.btnRemoveCoupon.visibility = View.VISIBLE
                binding.edtCoupon.isEnabled = false

                stockModel?.totals?.is_coupon_added = 1
                stockModel?.totals?.coupon_code = strCouponCode
                stockModel?.totals?.discount_amount =
                    orderModel?.totals?.discount_amount.toString()
                stockModel?.totals?.subtotal = orderModel?.totals?.subtotal.toString()

                binding.tvSubTotal.text =
                    (Utils.getPriceFormatted(
                        (orderModel?.totals?.subtotal).toString(),
                        selectedCurrency
                    ))
                binding.tvOrderTotal.text =
                    (Utils.getPriceFormatted(
                        (orderModel?.totals?.grand_total).toString(),
                        selectedCurrency
                    ))
                binding.tvTotalAmount.text =
                    (Utils.getPriceFormatted(
                        (orderModel?.totals?.grand_total).toString(),
                        selectedCurrency
                    ))


                if (orderModel?.totals?.discount_amount != null && orderModel.totals.discount_amount != "" && orderModel.totals.discount_amount!!.toDouble() > 0.0) {
                    binding.discountCollapseGroup.visibility = View.VISIBLE
                    binding.tvDiscountAmount.text =
                        ("-" + Utils.getPriceFormatted(
                            (orderModel.totals.discount_amount).toString(),
                            selectedCurrency
                        ))
                } else binding.discountCollapseGroup.visibility = View.GONE


                ///////////////////////////////////////////////////////////////////////////////////

                if (orderModel?.totals?.shipping_amount != null && orderModel.totals.shipping_amount != "" && orderModel.totals.shipping_amount.toDouble() > 0.0) {

                    binding.tvShippingCharge.text =
                        (Utils.getPriceFormatted(
                            orderModel.totals.shipping_amount.toString(),
                            selectedCurrency
                        ))
                } else {
                    binding.tvShippingCharge.text = (resources.getString(R.string.free))
                }

                //////For 100% Discount//////////////////////////////////////////////////////////////////////////////////////

                if (stockModel!!.totals!!.is_coupon_added != null && stockModel!!.totals!!.is_coupon_added.equals(
                        "1"
                    )
                ) {

                    if (orderModel?.totals?.grand_total != null && orderModel.totals.grand_total.toDouble() <= 0) {
//                        binding.relStoreCredit.visibility = View.GONE
                        selectedPaymentMethod = "free"
                        binding.tvOrderTotal.text =
                            (Utils.getPriceFormatted(
                                (orderModel.totals.grand_total).toString(),
                                selectedCurrency
                            ))
                        binding.tvTotalAmount.text =
                            (Utils.getPriceFormatted(
                                (orderModel.totals.grand_total).toString(),
                                selectedCurrency
                            ))
                    }
                } else {

                }

            } else {
                binding.edtCoupon.setText("")

                binding.btnApplyCoupon.visibility = View.VISIBLE
                binding.btnRemoveCoupon.visibility = View.GONE

                //checking if wallet value was visible before applying coupon, if visible make it visible , if not visible (no credit in wallet) hide it
//                if (isWalletVisible) {
//                    binding.relStoreCredit.visibility = View.VISIBLE
//                }

                stockModel!!.totals!!.is_coupon_added = 1
                stockModel!!.totals!!.coupon_code = ""
                stockModel!!.totals!!.discount_amount =
                    orderModel?.totals?.discount_amount.toString()
                stockModel!!.totals!!.subtotal = orderModel?.totals?.subtotal.toString()
                binding.edtCoupon.isEnabled = true
                binding.btnApplyCoupon.text = (resources.getString(R.string.apply))
                binding.tvOrderTotal.text =
                    (Utils.getPriceFormatted(
                        (orderModel?.totals?.grand_total).toString(),
                        selectedCurrency
                    ))
                binding.tvTotalAmount.text =
                    (Utils.getPriceFormatted(
                        (orderModel?.totals?.grand_total).toString(),
                        selectedCurrency
                    ))

                if (orderModel?.totals?.discount_amount != null && orderModel.totals.discount_amount != "" && orderModel.totals.discount_amount!!.toDouble() > 0) {
                    binding.tvDiscountAmount.text =
                        ("-" + Utils.getPriceFormatted(
                            (orderModel.totals.discount_amount).toString(),
                            selectedCurrency
                        ))
                    binding.discountCollapseGroup.visibility = View.VISIBLE
                } else binding.discountCollapseGroup.visibility = View.GONE

                ///////////////////////////////////////////////////////////////////////////////////

                if (orderModel?.totals?.shipping_amount != null && orderModel.totals.shipping_amount.toDouble() > 0.0) {

                    binding.tvShippingCharge.text =
                        (Utils.getPriceFormatted(
                            orderModel.totals.shipping_amount.toString(),
                            selectedCurrency
                        ))

                } else {
                    binding.tvShippingCharge.text = (resources.getString(R.string.free))
                }


            }


            if (orderModel?.totals?.grand_total != null && orderModel?.totals?.grand_total?.toDouble() <= 0) {
                binding.tvOrderTotal.text =
                    (Utils.getPriceFormatted(
                        (orderModel.totals.grand_total).toString(),
                        selectedCurrency
                    ))
                binding.tvTotalAmount.text =
                    (Utils.getPriceFormatted(
                        (orderModel.totals.grand_total).toString(),
                        selectedCurrency
                    ))
            }

            if (response.message != null)
                Utils.showSnackbar(binding.cvBottom, response.message)
        } else {
            Utils.showSnackbar(binding.cvBottom, resources.getString(R.string.invalid_coupon_code))
        }
    }

    //----------------------------------------------------------------------------------------------

    // Check Stock Api -----------------------------------------------------------------------------

    private fun checkStock(strAddressId: String?, showProgress: Boolean) {

        val cartModel = modelStockRequest
        cartModel?.shipping_address_id = if (!strAddressId.isNullOrBlank()) strAddressId else ""
        cartModel?.billing_address_id = if (!strAddressId.isNullOrBlank()) strAddressId else ""
        cartModel?.payment_method = selectedPaymentMethod

        checkoutViewModel.checkItemStock(selectedLanguage, selectedCurrency, cartModel!!)
        checkoutViewModel.responseCheckItemStock.observe(this) {
            when (it) {
                is Resource.Success -> {
                    stockCart(it.data!!)
                    dismissProgress()
                }

                is Resource.Error -> {
                    checkStockForNoAddress()
                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    private fun stockCart(model: CheckStockResponseModel) {

        dismissProgress()
        //println("Response  : " + stockModel)
        if (model.status == 200 && model.data!!.items!!.size > 0) {
            stockModel = model.data

            if (stockModel!!.totals!!.store_credit == null) {
                val creditModel =
                    StoreCredit("0.0", "0.0", "0.0", "0.0", "0.0")
                stockModel!!.totals!!.store_credit = creditModel
            }

            if (!model.data.shipping_address.isNullOrEmpty()) {
                stockModel!!.userSelectedAddress = model.data.shipping_address!![0]
            }
            setTotal()

        } else if (model.status == 355) {

            //Out of stock
            Utils.showSnackbar(binding.cvBottom, model.message!!)
        } else {
            Utils.showSnackbar(binding.cvBottom, model.message!!)
        }
    }

    //----------------------------------------------------------------------------------------------

    // check stock for no address-------------------------------------------------------------------

    private fun checkStockForNoAddress() {
        val cartModel = modelStockRequest
        cartModel!!.shipping_address_id = ""
        cartModel.billing_address_id = ""
        modelStockRequest = cartModel
        checkoutViewModel.checkItemStockNoAddress(selectedLanguage, selectedCurrency, cartModel)
        checkoutViewModel.responseCheckItemStockNoAddress.observe(this) {
            when (it) {
                is Resource.Success -> {
                    stockCartNoAddress(it.data!!)
                    dismissProgress()
                }

                is Resource.Error -> {
                    checkStockForNoAddress()
                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    private fun stockCartNoAddress(emptyAddStockModel: StockResponseModelNoAddress) {

        if (emptyAddStockModel.status == "200" && emptyAddStockModel.items.size > 0) {

            if (emptyAddStockModel.configurable_option == null) {
                val arrayList = ArrayList<ConfigurableOption>()
                emptyAddStockModel.configurable_option = arrayList
            }
            setTotal()

        } else if (emptyAddStockModel.status == "355") {

            //Out of stock
            Utils.showSnackbar(binding.cvBottom, emptyAddStockModel.message)
        }
    }
    //----------------------------------------------------------------------------------------------

    // Confirm order and cancel order --------------------------------------------------------------

    private fun confirmOrder() {
        showProgress()
        val formatter = DateTimeFormatter.ofPattern("yyyy MMM dd")
        val currentDate = LocalDateTime.now().format(formatter)

        val paymentInfo = OrderRequestPaymentModel(
            id = invoiceResponse?.transactions?.get(0)?.id,
            amount = invoiceResponse?.order?.amount.toString(),
            date = currentDate,
            type = "cart_payment",
            info = "",
            status = "SUCCESS"
        )

        val reqModel = RivaOrderRequest(
            customer_id = strUserId,
            shipping_address_id = stockModel?.userSelectedAddress?.address_id ?: "",
            paymentMethod = "checkoutsdk",
            gateway = "",
            hide_from_invoice = "0",
            payment_details = paymentInfo
        )

        checkoutViewModel.rivaCheckout(selectedLanguage, selectedCurrency, reqModel)
        checkoutViewModel.responseRivaCheckout.observe(this) {
            when (it) {
                is Resource.Success -> {
                    handleCheckoutResponse(it.data!!)
                    sharedpreferenceHandler.saveData(SharedpreferenceHandler.RIVA_USER_ADDRESS, "")
                    dismissProgress()
                }

                is Resource.Error -> {
                    Utils.showErrorSnackbar(binding.cvBottom)
                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleCheckoutResponse(responseModel: RivaOrderResponseModel) {
        dismissProgress()
        //println("Response Create Order: " + orderModel.toString())
        if (responseModel.status == 200) {
            val orderModel = responseModel.data
            strOrderId = orderModel?.order_id.toString()
            //println("Stock model: " + stockModel)
            // orderModel.ordered_items = stockModel!!.items //old code not passing categories
            if (orderModel?.items!!.size == stockModel?.items!!.size) {
                for (i in orderModel.items.indices) {
                    orderModel.items[i]?.final_price =
                        stockModel?.items?.get(i)?.final_price.toString()
                    orderModel.items[i]?.configurable_option =
                        stockModel?.items?.get(i)?.configurable_option
                }
            }

            if (orderModel.items.size == stockModel?.items!!.size) {
                for (i in orderModel.items.indices) {
                    orderModel.items[i]!!.final_price =
                        stockModel?.items?.get(i)?.final_price.toString()
                    orderModel.items[i]!!.configurable_option =
                        stockModel?.items?.get(i)?.configurable_option
                }
            }

            sharedpreferenceHandler.saveData(SharedpreferenceHandler.INVOICE_ID, "")
            showOrderPlacedDialog(
                orderModel.order_id!!,
                orderModel.totals?.grand_total?.toDouble()!!
            )

        } else {
            Utils.showSnackbar(binding.cvBottom, responseModel.message!!)
        }
    }

    private fun cancelOrder() {
        val orderCancelModel = OrderCancelRequestModel(strUserId, strOrderId!!)
        checkoutViewModel.cancelOrder(selectedLanguage, selectedCurrency, orderCancelModel)
        checkoutViewModel.responseCancelOrder.observe(this) {
            when (it) {
                is Resource.Success -> {
//                    stockCartNoAddress(it.data!!)
                    dismissProgress()
                }

                is Resource.Error -> {
                    checkStockForNoAddress()
                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // Payment Apis---------------------------------------------------------------------------------

    fun sendPaymentLinkToCustomerApi() {
        val charge = Charge(Receipt(true, "", true), "payment link")
        val currencies = ArrayList<String>()
        currencies.add(selectedCurrency)
        val customer = Customer(
            strEmail,
            stockModel?.userSelectedAddress?.firstname!!,
            "",
            stockModel?.userSelectedAddress?.lastname!!,
            "",
            Phone(
                stockModel?.userSelectedAddress?.phone_code!!,
                stockModel?.userSelectedAddress?.mobile_number!!
            )
        )
        val metadata = Metadata("1", "2", "3")
        val channels = ArrayList<String>()
        channels.add("SMS")
        channels.add("EMAIL")
        val notifications = Notifications(channels, true)

        val items = ArrayList<InvoiceRequestModel.ItemOrder.OrderItem>()
        var shippingPrice = 0.0
        var discountAmount = 0.0
        try {

            shippingPrice =
                stockModel?.totals?.shipping_amount?.toDouble()!!
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        try {
            discountAmount =
                stockModel?.totals?.discount_amount?.toDouble()!!
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        for ((index, value) in stockModel?.items!!.withIndex()) {
            if (index == 0) {
                items.add(
                    InvoiceRequestModel.ItemOrder.OrderItem(
                        Utils.roundOffDecimal((value?.final_price?.toDouble() * value?.qty) + shippingPrice)!!,
                        selectedCurrency,
                        value?.short_description,
                        Discount("P", discountAmount),
                        value?.image,
                        value?.name,
                        value?.qty
                    )
                )
            } else {
                items.add(
                    InvoiceRequestModel.ItemOrder.OrderItem(
                        Utils.roundOffDecimal(value?.final_price?.toDouble() * value?.qty)!!,
                        selectedCurrency,
                        value?.short_description,
                        Discount("P", 0.0),
                        value?.image,
                        value?.name,
                        value?.qty
                    )
                )
            }
        }
        stockModel?.items?.forEach {

        }
        val order = InvoiceRequestModel.ItemOrder(
            Utils.roundOffDecimal(stockModel?.totals?.grand_total?.toDouble()!!)!!,
            selectedCurrency,
            items
        )
        val paymentMethod = ArrayList<String>()
        paymentMethod.add("KNET")
        val unixTimeStamp = getEpoch()
//        val date = getDate(unixTimeStamp, "dd/MM/yyyy hh:mm:ss.SSS")
        val invoiceReuestModel = InvoiceRequestModel(
            charge, currencies, customer, "Payment link", false,
            unixTimeStamp, unixTimeStamp, metadata,
            "INVOICE", "Riva Payment", notifications, order, paymentMethod,
            Post(""),
            Redirect(""), Reference("", "")
        )
        val json = Gson().toJson(invoiceReuestModel)
        Log.d("invoice_request", json)
        checkoutViewModel.sendPaymentLinkToCustomer(invoiceReuestModel)
        checkoutViewModel.responsePaymentLink.observe(this@CheckoutActivity) {
            when (it) {
                is Resource.Success -> {
                    hasSendPaymentLinkToCustomer = true
                    currentInvoiceId = it?.data?.id
                    sharedpreferenceHandler.saveData(
                        SharedpreferenceHandler.INVOICE_ID,
                        currentInvoiceId
                    )
                    sharedpreferenceHandler.saveData(
                        SharedpreferenceHandler.PAYMENT_STATUS,
                        it.data?.status
                    )
                    setPaymentView()
                    Utils.showSnackbar(binding.cvBottom, "Payment link has been sent")
//                    Toast.makeText(this,"Payment link has been sent",Toast.LENGTH_SHORT).show()
                    checkoutViewModel.startTrackingPaymentStatus(currentInvoiceId!!)
                    getPaymentStatus()
                    dismissProgress()
                }

                is Resource.Error -> {
                    try {
                        val message = it.message
                        val jsonObj = JSONObject(message)
                        val jsonArray = jsonObj.getJSONArray("errors")
                        val jsonErrorObj = jsonArray.get(0) as JSONObject
                        val errorMessage = jsonErrorObj.getString("description")
                        Utils.showSnackbar(binding.cvBottom, errorMessage!!)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    fun getEpoch(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MILLISECOND, -calendar.timeZone.getOffset(calendar.timeInMillis))
        val date: Date = calendar.time
        return date.time * 1000
    }


    fun getPaymentStatus() {
        checkoutViewModel.responsePaymentStatus.observe(this) {
            when (it) {
                is Resource.Success -> {
                    sharedpreferenceHandler.saveData(
                        SharedpreferenceHandler.PAYMENT_STATUS,
                        it.data?.status
                    )
                    setPaymentView()
                    invoiceResponse = it.data
                    when (invoiceResponse?.status) {
                        "CREATED" -> {
                            paymentStatus = invoiceResponse?.status
                        }
                        "PAID" -> {
                            hasSendPaymentLinkToCustomer = false
                            paymentDone = true
                            paymentStatus = invoiceResponse?.status
                            selectedPaymentMethod =
                                invoiceResponse?.transactions?.get(0)?.source?.payment_method!!
                            checkoutViewModel.stopGettingPaymentStatus()
                            sharedpreferenceHandler.saveData(
                                SharedpreferenceHandler.PAYMENT_STATUS,
                                ""
                            )
                            Utils.showSnackbar(
                                binding.cvBottom,
                                "Please don't press back or close the application. Please confirm order"
                            )
                        }
                        "EXPIRED" -> {
                            hasSendPaymentLinkToCustomer = false
                            paymentStatus = invoiceResponse?.status
                            checkoutViewModel.stopGettingPaymentStatus()
                            sharedpreferenceHandler.saveData(SharedpreferenceHandler.INVOICE_ID, "")
                        }

                    }
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

    // delete address api---------------------------------------------------------------------------
    fun deleteAddressApi() {
        checkoutViewModel.deleteAddress(
            selectedLanguage,
            stockModel?.userSelectedAddress?.address_id!!
        )
        checkoutViewModel.responseDeleteAddress.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.data?.status == 200) {
                        binding.tvAddAddress.visibility = View.VISIBLE
                        binding.tvCustomerAddress.visibility = View.GONE
                        binding.tvCustomerAddress.setText("")
                        binding.tvEdit.visibility = View.GONE
                        binding.imageViewDeleteAddress.visibility = View.GONE
                        sharedpreferenceHandler.saveData(
                            SharedpreferenceHandler.RIVA_USER_ADDRESS,
                            ""
                        )
                        stockModel?.userSelectedAddress = null
                    }
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
//----------------------------------------------------------------------------------------------

    ///loading dialog
    private fun showProgress() {

        if (!this@CheckoutActivity.isFinishing) {
            if (loading == null) {
                loading = Dialog(this@CheckoutActivity, R.style.TranslucentDialog)
                loading?.setContentView(R.layout.custom_loading_view)
                loading?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loading?.setCanceledOnTouchOutside(false)
                loading?.show()
            } else {
                if (!!loading?.isShowing!!) {
                    loading?.show()
                }
            }
        }
    }

    private fun dismissProgress() {
        if (loading != null && loading?.isShowing == true)
            loading?.dismiss()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == ADDRESS_REQ_CODE && data != null && data.hasExtra(
                "model"
            )
        ) {
            val addressResponse =
                (data.getSerializableExtra("model") as AddAddressResponseModel)
            val gson = Gson()
            sharedpreferenceHandler.saveData(
                SharedpreferenceHandler.RIVA_USER_ADDRESS,
                gson.toJson(addressResponse)
            )
            try {
                stockModel?.userSelectedAddress = addressResponse.data?.get(0)

                setDisplayAddress(stockModel?.userSelectedAddress!!)

            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            binding.tvAddAddress.visibility = View.GONE
            binding.tvEdit.visibility = View.VISIBLE
            checkStock(stockModel?.userSelectedAddress!!.address_id!!, true)

        }
        if (resultCode == Activity.RESULT_OK) {
        }
    }

    private fun setDisplayAddress(userAddress: AddAddressDataModel) {
        try {
            strEmail = userAddress?.email!!
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        binding.tvCustomerAddress?.visibility = View.VISIBLE
        var address =
            userAddress?.firstname + " " + userAddress?.lastname + "\n" +
                    userAddress?.apartment_number + ", " +
                    userAddress?.block + ", "
        if (!userAddress?.floor_number.isNullOrEmpty())
            address += userAddress?.floor_number + ", "
        if (!userAddress?.building_number.isNullOrEmpty())
            address += userAddress?.building_number + "\n"
        if (!userAddress?.street.isNullOrEmpty())
            address += userAddress?.street + ", "
        if (!userAddress?.city.isNullOrEmpty())
            address += userAddress?.city + ", "
        if (!userAddress?.region.isNullOrEmpty())
            address += userAddress?.region + ", " +
                    userAddress?.country + "\n"
        address += "+965 " + userAddress?.mobile_number + "\n"
        address += userAddress?.email
        binding.tvCustomerAddress?.setText(address)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showOrderPlacedDialog(orderId: String, totalAmount: Double) {

        try {
            val alertSuccessBinding: LayoutOrderPlacedBinding = LayoutOrderPlacedBinding.inflate(
                LayoutInflater.from(this),
                null,
                false
            )
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(alertSuccessBinding.root)
            alertSuccessBinding.imageViewClose.setOnClickListener() {
                orderPlacedDialog?.dismiss()
               this.finish()

            }


            alertSuccessBinding.tvOrderMessage.text =
                "Order $orderId with $totalAmount $selectedCurrency has been placed successfully"
            if (orderPlacedDialog == null) {
                orderPlacedDialog?.setCancelable(false)
                orderPlacedDialog = builder.show()


            } else if (orderPlacedDialog != null && orderPlacedDialog?.isShowing == false) {
                orderPlacedDialog?.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }
}