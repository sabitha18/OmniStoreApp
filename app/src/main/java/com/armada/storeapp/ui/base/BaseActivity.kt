package com.armada.storeapp.ui.base

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.armada.storeapp.R
import com.armada.storeapp.databinding.LayoutAlertSuccessBinding
import com.armada.storeapp.ui.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_alert_success.*
import java.util.*

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {


    var successDialog: Dialog? = null

    fun hideSoftKeyboard() {
        // Only runs if there is a view that is currently focused
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    fun showMessage(msg: String) {

        try {
            if (successDialog == null) {
                val alertSuccessBinding: LayoutAlertSuccessBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(this),
                    R.layout.layout_alert_success,
                    null,
                    false
                )

                alertSuccessBinding?.txtAlertMsg?.setText(msg)

                val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                    .setView(alertSuccessBinding.root)
                successDialog = builder.show()
                successDialog?.setCancelable(false)


                alertSuccessBinding.btnOK.setOnClickListener() {

                    successDialog?.dismiss()

                }
            } else if (successDialog != null && successDialog?.isShowing == false) {
                successDialog?.txtAlertMsg?.text = msg
                successDialog?.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
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

    fun requestCallPermission(mActivity: Activity) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                mActivity,
                Manifest.permission.CALL_PHONE
            )
        ) {

            ActivityCompat.requestPermissions(
                mActivity,
                arrayOf(Manifest.permission.CALL_PHONE),
                Constants.REQUEST_CALL_PHONE
            )

        } else {
            ActivityCompat.requestPermissions(
                mActivity, arrayOf(Manifest.permission.CALL_PHONE),
                Constants.REQUEST_CALL_PHONE
            )
        }
    }
}