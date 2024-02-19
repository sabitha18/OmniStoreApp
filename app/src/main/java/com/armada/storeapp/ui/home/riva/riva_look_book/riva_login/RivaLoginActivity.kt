package com.armada.storeapp.ui.home.riva.riva_look_book.riva_login

import android.app.Dialog
import android.app.KeyguardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.Device
import com.armada.storeapp.data.model.request.RivaLoginRequest
import com.armada.storeapp.data.model.request.RivaRegisterUserRequest
import com.armada.storeapp.data.model.response.RivaRegisterUserResponse
import com.armada.storeapp.databinding.ActivityRivaLoginBinding
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsViewModel
import com.armada.storeapp.ui.utils.ConnectionDetector
import com.armada.storeapp.ui.utils.LoadingView
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.armada.storeapp.ui.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import kotlin.collections.ArrayList

@AndroidEntryPoint
class RivaLoginActivity : BaseActivity() {

    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    lateinit var activityRivaLoginBinding: ActivityRivaLoginBinding
    private var mToolbar: Toolbar? = null
    private var strEmail = ""
    private var strPass = ""
    private var cd: ConnectionDetector? = null
    private var loading: Dialog? = null
    lateinit var rivaLoginViewModel: RivaLoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRivaLoginBinding = ActivityRivaLoginBinding.inflate(layoutInflater)
        setContentView(activityRivaLoginBinding.root)
        rivaLoginViewModel =
            ViewModelProvider(this).get(RivaLoginViewModel::class.java)
        sharedpreferenceHandler= SharedpreferenceHandler(this)
        initToolbar()
        init()
        setOnclickListener()
    }

    ///
    private fun initToolbar() {
        mToolbar = activityRivaLoginBinding.toolbarActionbar
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val upArrow: Drawable? =
            ContextCompat.getDrawable(this@RivaLoginActivity, R.drawable.ic_baseline_arrow_back_24)

        upArrow?.setColorFilter(
            ContextCompat.getColor(this@RivaLoginActivity, R.color.black),
            PorterDuff.Mode.SRC_ATOP
        )
        upArrow?.setVisible(true, true)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    ///
    private fun init() {
        cd = ConnectionDetector(this)

    }

    ///
    private fun setOnclickListener() {

        activityRivaLoginBinding.btnSignIn.setOnClickListener {

            strEmail = activityRivaLoginBinding.edtEmail.text.toString()
            strPass = activityRivaLoginBinding.edtPassword.text.toString()

            if (Patterns.EMAIL_ADDRESS.matcher(strEmail).matches() && strPass.length >= 6) {
                if (cd?.isConnectingToInternet!!) {
                    userLogin()
                } else {
                    Utils.showSnackbar(
                        activityRivaLoginBinding.root,
                        resources.getString(R.string.plz_chk_internet)
                    )
                }
            } else if (strEmail.isEmpty())
                Utils.showSnackbar(
                    activityRivaLoginBinding.root,
                    resources.getString(R.string.enter_your_email)
                )
            else if (strPass.isEmpty())
                Utils.showSnackbar(
                    activityRivaLoginBinding.root,
                    resources.getString(R.string.add_pass)
                )
            else if (!Patterns.EMAIL_ADDRESS.matcher(activityRivaLoginBinding.edtEmail.text.toString())
                    .matches()
            ) {
                Utils.showSnackbar(
                    activityRivaLoginBinding.root,
                    resources.getString(R.string.please_enter_a_valid_e_mail_address_e_g_name_email_com)
                )
            }
//            else if (activityRivaLoginBinding.edtPassword.text.toString().length <= 6) {
//                Utils.showSnackbar(
//                    activityRivaLoginBinding.root,
//                    resources.getString(R.string.pass_length)
//                )
//            }
            else
                Utils.showSnackbar(
                    activityRivaLoginBinding.root,
                    resources.getString(R.string.all_field_required)
                )
        }
    }


    fun userLogin() {
        val deviceModel = Device(
            "", Utils.device_type,
            Build.MODEL, Build.VERSION.RELEASE, Utils.api_version
        )
        val userModel = RivaLoginRequest(strEmail, strPass, deviceModel)
        val selectedLanguage=sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY,"en")!!
        val selectedCurrency=sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_CURRENCY,"USD")!!
        rivaLoginViewModel.rivaUserLogin(selectedLanguage,selectedCurrency,userModel)
        rivaLoginViewModel.responseLoginUser.observe(this) {
            when (it) {
                is Resource.Success -> {
                    handleLoginRepsonse(it.data!!)
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

    fun handleLoginRepsonse(loginResponse: RivaRegisterUserResponse) {
        val sharedpreferenceHandler = SharedpreferenceHandler(this)
        if (loginResponse.status == 200) {
            loginResponse?.data?.let {

                sharedpreferenceHandler.saveData(SharedpreferenceHandler.RIVA_USER_ID, it.id)
                sharedpreferenceHandler.saveData(SharedpreferenceHandler.RIVA_USER_LOGGED_IN, true)
                val intent = Intent(this, RivaLookBookActivity::class.java)
                startActivity(intent)
                this.finish()
            }
        } else
            Toast.makeText(this, loginResponse?.message, Toast.LENGTH_SHORT).show()
    }

    ///loading dialog
    private fun showProgress() {

        if (!this@RivaLoginActivity.isFinishing) {
            if (loading == null) {
                loading = Dialog(this@RivaLoginActivity, R.style.TranslucentDialog)
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}