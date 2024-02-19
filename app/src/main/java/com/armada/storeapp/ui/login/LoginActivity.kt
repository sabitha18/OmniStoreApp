package com.armada.storeapp.ui.login

import android.content.Intent
import android.net.Uri

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.ArrayMap
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.armada.storeapp.data.Resource
import com.armada.storeapp.databinding.ActivityLoginBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.select_country.SelectCountryActivity
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.armada.storeapp.ui.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.net.URLEncoder


@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private var TAG = LoginActivity::class.java.simpleName
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    var currentUsername = ""
    var currentPassword = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedpreferenceHandler = SharedpreferenceHandler(this)

        val hasAlreadyLoggedIn =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.LOGIN_STATUS, false)

        if (hasAlreadyLoggedIn) {
            val selectedCountry =
                sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY, "")
            if (
                selectedCountry.isNullOrEmpty()
            ) {
                val intent = Intent(this, SelectCountryActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }



        val username = binding.username
        val password = binding.password
        val login = binding.login
//        val loading = binding.loading

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })




        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
//                    EditorInfo.IME_ACTION_DONE ->
//                        callLoginApi()
                }
                false
            }

            login.setOnClickListener {
//                loading.visibility = View.VISIBLE
                currentUsername = binding?.username?.text?.toString()?.trim()!!
                currentPassword = binding?.password?.text?.toString()?.trim()!!
                hideSoftKeyboard()
                callLoginApi(currentUsername, currentPassword)
            }
        }


////        todo remove
//        sharedpreferenceHandler.saveData(
//            SharedpreferenceHandler.WAREHOUSE_LOGIN_STATUS,
//            true
//        )
//        sharedpreferenceHandler.saveData(
//            SharedpreferenceHandler.LOGIN_STATUS,
//            true
//        )
//        sharedpreferenceHandler.saveData(
//            SharedpreferenceHandler.RIVA_USER_LOGGED_IN,
//            true
//        )
//        sharedpreferenceHandler.saveData(
//            SharedpreferenceHandler.RIVA_USER_ID,
//            646884
//        )
    }

    fun callLoginApi(username: String, password: String) {
        try {
            val encryptedPassword = Utils.encrypt(password)?.trimEnd()

            val query = URLEncoder.encode(encryptedPassword, "utf-8");

            loginViewModel.login(username, query!!)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        loginViewModel.loginResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    binding?.cvProgressbar?.visibility = View.GONE
                    it.data?.let { response ->
                        val sharedpreferenceHandler = SharedpreferenceHandler(this)
                        sharedpreferenceHandler.saveData(
                            SharedpreferenceHandler.ACCESS_TOKEN,
                            response.access_token
                        )
                        sharedpreferenceHandler.saveData(
                            SharedpreferenceHandler.LOGIN_USERNAME,
                            username
                        )
                        sharedpreferenceHandler.saveData(
                            SharedpreferenceHandler.LOGIN_PASSWORD,
                            password
                        )
                        getUserDetails("Bearer ${response.access_token!!}")
                    }
                    loginViewModel.loginResponse.value = null
                }
                is Resource.Error -> {
                    binding?.cvProgressbar?.visibility = View.GONE
                    it.message?.let { message ->
                        Log.e(TAG, "Error: $message")
                        if (message.contains("error_description")) {
                            val jsonObj = JSONObject(message)
                            showMessage(jsonObj.get("error_description").toString())
                        } else
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                    }
                    loginViewModel.loginResponse.value = null
                }
                is Resource.Loading -> {
                    binding?.cvProgressbar?.visibility = View.VISIBLE
                }
            }
        }
    }


    fun getUserDetails(accessToken: String) {
        loginViewModel.getUserDetails(accessToken)
        loginViewModel.userDetailsResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    binding?.cvProgressbar?.visibility = View.GONE
                    it.data?.let { response ->
                        val sharedpreferenceHandler = SharedpreferenceHandler(this)
                        sharedpreferenceHandler.saveData(SharedpreferenceHandler.LOGIN_STATUS, true)
                        sharedpreferenceHandler.saveData(
                            SharedpreferenceHandler.STORE_CODE,
                            response.userInfoRecord?.storeCode
                        )
                        sharedpreferenceHandler.saveData(
                            SharedpreferenceHandler.STORE_ID,
                            response.userInfoRecord?.storeID!!
                        )
                        sharedpreferenceHandler.saveData(
                            SharedpreferenceHandler.COUNTRY_CODE,
                            response.userInfoRecord?.countryCode
                        )
                        sharedpreferenceHandler.saveData(
                            SharedpreferenceHandler.COUNTRY_ID,
                            response.userInfoRecord?.countryID!!
                        )
                        sharedpreferenceHandler.saveData(
                            SharedpreferenceHandler.PRICE_LIST_ID,
                            response.userInfoRecord?.priceListID!!
                        )
                        sharedpreferenceHandler.saveData(
                            SharedpreferenceHandler.LOGIN_USER_ID,
                            response.userInfoRecord?.id!!
                        )
                        sharedpreferenceHandler.saveData(
                            SharedpreferenceHandler.USER_TYPE,
                            response.userInfoRecord.userType
                        )
                        sharedpreferenceHandler.saveData(
                            SharedpreferenceHandler.LOGIN_USER_CODE,
                            response.userInfoRecord?.userCode
                        )
                        sharedpreferenceHandler.saveData(
                            SharedpreferenceHandler.EMPLOYEE_NAME,
                            response.userInfoRecord?.employeeName
                        )
                        sharedpreferenceHandler.saveData(
                            SharedpreferenceHandler.STORE_NAME,
                            response.userInfoRecord?.storeName
                        )
                        if(response.userInfoRecord?.binEnabled==1){
                            sharedpreferenceHandler.saveData(
                                SharedpreferenceHandler.BIN_AVAILABLE,
                                "true"
                            )
                        } else {
                            sharedpreferenceHandler.saveData(
                                SharedpreferenceHandler.BIN_AVAILABLE,
                                "false"
                            )
                        }

                        if(response.userInfoRecord.withPaymentEnabled) {
                            sharedpreferenceHandler.saveData(
                                SharedpreferenceHandler.PAYMENTENABLED, "true"
                            )

                        } else {
                            sharedpreferenceHandler.saveData(
                                SharedpreferenceHandler.PAYMENTENABLED, "false"
                            )
                            Log.e("payena", response.userInfoRecord.withPaymentEnabled.toString())
                        }


//                      Toast.makeText(this, response.userInfoRecord?.binEnabled.toString(), Toast.LENGTH_SHORT).show()
                        generateTokenApi()
                    }
                    loginViewModel.userDetailsResponse.value = null
                }
                is Resource.Error -> {
                    binding?.cvProgressbar?.visibility = View.GONE
                    it.message?.let { message ->
                        Log.e(TAG, "Error userdetails: $message")
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                    loginViewModel.userDetailsResponse.value = null
                }
                is Resource.Loading -> {
                    binding?.cvProgressbar?.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun generateTokenApi() {
        Toast.makeText(this, "Logging to WMS", Toast.LENGTH_LONG).show()
        var jsonParams: MutableMap<String, String> = ArrayMap()
        jsonParams["username"] = "Api_Develop@gmail.com"
        jsonParams["password"] = "Api_Develop@724272"
        jsonParams["grant_type"] = "password"
        loginViewModel.generateToken(jsonParams)
        loginViewModel.token_response.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    binding?.cvProgressbar?.visibility = View.GONE
                    response.data?.let {
                        if (!it?.access_token?.equals("")!!) {
                            sharedpreferenceHandler.saveData(
                                SharedpreferenceHandler.WAREHOUSE_TOKEN,
                                it?.access_token
                            )
                            wmsLogin(currentUsername, currentPassword, "Bearer " + it?.access_token)
                        }
                    }
                    loginViewModel.token_response.value = null
                }

                is Resource.Error -> {
                    binding?.cvProgressbar?.visibility = View.GONE
                    Toast.makeText(
                        this,
                        response.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    loginViewModel.token_response.value = null
                }

                is Resource.Loading -> {
                    binding?.cvProgressbar?.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun wmsLogin(userId: String, password: String, token: String) {

        loginViewModel.wmsUserLogin(userId, password, token)
        loginViewModel.wmsLoginResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    binding?.cvProgressbar?.visibility = View.GONE
                    response.data?.message?.let {
                        if (it.contains("Success")) {
                            sharedpreferenceHandler.saveData(
                                SharedpreferenceHandler.WAREHOUSE_LOGIN_STATUS,
                                true
                            )
                            sharedpreferenceHandler.saveData(
                                SharedpreferenceHandler.WAREHOUSE_USER_ID,
                                response.data?.userId,
                            )
                            sharedpreferenceHandler.saveData(
                                SharedpreferenceHandler.WAREHOUSE_TO_LOCATION_CODE,
                                response.data?.userLocation
                            )
                            sharedpreferenceHandler.saveData(
                                SharedpreferenceHandler.WAREHOUSE_FROM_LOCATION_CODE,
                                response.data?.fromLocation
                            )
                            sharedpreferenceHandler.saveData(
                                SharedpreferenceHandler.WAREHOUSE_USER_NAME,
                                response.data?.userName
                            )
                            sharedpreferenceHandler.saveData(
                                SharedpreferenceHandler.LOGIN_STATUS,
                                true
                            )

                            val hasAlreadyLoggedIn =
                                sharedpreferenceHandler.getData(SharedpreferenceHandler.LOGIN_STATUS, false)
                            if (hasAlreadyLoggedIn) {
                                val selectedCountry =
                                    sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY, "")
                                if (
                                    selectedCountry.isNullOrEmpty()
                                ) {
                                    val intent = Intent(this, SelectCountryActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }

//                            val intent = Intent(this, MainActivity::class.java)
//                            startActivity(intent)
//                            finish()
                        } else {
                            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                        }
                    }
                    loginViewModel.wmsLoginResponse.value = null
                }

                is Resource.Error -> {
                    Toast.makeText(this, "WMS User not found", Toast.LENGTH_SHORT).show()
                    binding?.cvProgressbar?.visibility = View.GONE
                    Toast.makeText(
                        this,
                        response.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    loginViewModel.wmsLoginResponse.value = null
                }

                is Resource.Loading -> {
                    binding?.cvProgressbar?.visibility = View.VISIBLE
                }
            }
        }
    }

}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
