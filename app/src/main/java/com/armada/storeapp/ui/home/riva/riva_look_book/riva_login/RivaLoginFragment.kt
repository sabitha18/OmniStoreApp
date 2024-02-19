package com.armada.storeapp.ui.home.riva.riva_look_book.riva_login

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.Device
import com.armada.storeapp.data.model.request.RivaLoginRequest
import com.armada.storeapp.data.model.response.RivaRegisterUserResponse
import com.armada.storeapp.databinding.ActivityRivaLoginBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.utils.ConnectionDetector
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.armada.storeapp.ui.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_riva_login.*

@AndroidEntryPoint
class RivaLoginFragment : Fragment() {
    lateinit var sharedpreferenceHandler:SharedpreferenceHandler
    lateinit var activityRivaLoginBinding: ActivityRivaLoginBinding
    private var mToolbar: Toolbar? = null
    private var strEmail = ""
    private var strPass = ""
    private var rivaLookBookActivity: RivaLookBookActivity? = null
    private var cd: ConnectionDetector? = null
    private var loading: Dialog? = null
    lateinit var rivaLoginViewModel: RivaLoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        activityRivaLoginBinding = ActivityRivaLoginBinding.inflate(layoutInflater)
        rivaLoginViewModel =
            ViewModelProvider(this).get(RivaLoginViewModel::class.java)
        cd = ConnectionDetector(requireContext())
        rivaLookBookActivity = activity as RivaLookBookActivity
        sharedpreferenceHandler=SharedpreferenceHandler(rivaLookBookActivity!!)
        activityRivaLoginBinding.toolbarActionbar.visibility = View.GONE
        setOnclickListener()
        return activityRivaLoginBinding.root
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
            } else
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
        rivaLoginViewModel.responseLoginUser.observe(rivaLookBookActivity!!) {
            when (it) {
                is Resource.Success -> {
                    handleLoginRepsonse(it.data!!)
                    rivaLookBookActivity?.dismissProgress()
                }

                is Resource.Error -> {
                    rivaLookBookActivity?.dismissProgress()
                }
                is Resource.Loading -> {
                    rivaLookBookActivity?.showProgress()
                }
            }
        }
    }

    fun handleLoginRepsonse(loginResponse: RivaRegisterUserResponse) {
        val sharedpreferenceHandler = SharedpreferenceHandler(requireContext())
        if (loginResponse.status == 200) {
            loginResponse?.data?.let {

                sharedpreferenceHandler.saveData(SharedpreferenceHandler.RIVA_USER_ID, it.id)
                sharedpreferenceHandler.saveData(SharedpreferenceHandler.RIVA_USER_LOGGED_IN, true)
                rivaLookBookActivity?.navController?.navigate(R.id.navigation_riva_home)
            }
        } else
            Toast.makeText(requireContext(), loginResponse?.message, Toast.LENGTH_SHORT).show()
    }
}