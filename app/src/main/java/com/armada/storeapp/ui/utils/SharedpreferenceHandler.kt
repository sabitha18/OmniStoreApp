package com.armada.storeapp.ui.utils

import android.content.Context

class SharedpreferenceHandler(mcontext: Context) {
    companion object {

        val PAYMENT_STATUS="payment_status"
        val RECENT_SEARCH="recent_search"
        val CART_COUNT="cart_count"
        val CART_ITEMS="cart_items"
        val SEARCH_ITEMS="search_items"
        val SEARCH_COUNT="search_count"
        val RIVA_USER_ADDRESS = "riva_user_address"
        val INVOICE_ID = "riva_invoice_id"
        val LOGIN_TOKEN = "lg_tkn"
        val LOGIN_STATUS = "login_status"
        val LOGIN_USER_ID = "login_user_id"
        val LOGIN_USER_CODE = "login_user_code"
        val ACCESS_TOKEN = "access_token"
        val LOGIN_USERNAME = "username"
        val LOGIN_PASSWORD = "password"
        val USER_EMAIL = "email"
        val LOGIN_PSWD = "login_password"
        val LOCATION_ID = "location_id"
        val STORE_CODE = "store_code"
        val STORE_ID = "store_id"
        val USER_TYPE="user_type"
        val STORE_NAME = "store_name"
        val COUNTRY_CODE="country_code"
        val COUNTRY_ID="country_id"
        val PRICE_LIST_ID="price_list_id"

        val BIN_AVAILABLE = "true"

        var FROMSTORECODEORDER = "FROMSTORECODEORDER"






        val EMPLOYEE_NAME = "employee_name"
        val POS_URL = "pos_url"
        val WMS_URL = "wms_url"
        lateinit var context: Context
        var IS_FIRST_INSTALL = "is_first_install"

        val WAREHOUSE_LOGIN_STATUS = "warehouse_login_status"
        val WAREHOUSE_USER_ID = "warehouse_user_id"
        val WAREHOUSE_USER_NAME = "warehouse_username"
        val WAREHOUSE_TO_LOCATION_CODE = "warehouse_location_code"
        val WAREHOUSE_FROM_LOCATION_CODE = "warehouse_from_location_code"
        val WAREHOUSE_TOKEN = "warehouse_token"
        val STOREAPP_SHARED_PREFERENCE = "storeapp"

        val RIVA_USER_ID = "riva_user_id"
        val RIVA_USER_LOGGED_IN = "riva_logged_in"

        val RIVA_SELECTED_COUNTRY="country"
        val RIVA_SELECTED_CURRENCY="currency"
        val PAYMENTENABLED="false"


    }

    init {
        context = mcontext
    }

    fun saveData(name: String?, value: Boolean) {
        val sharedPreferences =
            context!!.getSharedPreferences(STOREAPP_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(name, value).apply()
    }

    fun saveData(name: String?, value: String?) {
        val sharedPreferences =
            context!!.getSharedPreferences(STOREAPP_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(name, value).apply()
    }

    fun saveData(name: String?, value: Int) {
        val sharedPreferences =
            context!!.getSharedPreferences(STOREAPP_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(name, value).apply()
    }

    fun getData(name: String?, defaultValue: String?): String? {
        val sharedPreferences =
            context.getSharedPreferences(STOREAPP_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        return sharedPreferences.getString(name, defaultValue)
    }

    fun getData(name: String?, defaultValue: Int): Int {
        val sharedPreferences =
            context!!.getSharedPreferences(STOREAPP_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(name, defaultValue)
    }

    fun getData(name: String?, defaultValue: Boolean): Boolean {
        return context!!.getSharedPreferences(STOREAPP_SHARED_PREFERENCE, Context.MODE_PRIVATE)
            .getBoolean(name, defaultValue)
    }

    operator fun contains(name: String?): Boolean {
        return context!!.getSharedPreferences(STOREAPP_SHARED_PREFERENCE, Context.MODE_PRIVATE)
            .contains(name)
    }

    fun deleteData(name: String?) {
        if (contains(name)) try {
            context!!.getSharedPreferences(STOREAPP_SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()
                .remove(name)
                .commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteSession() {
        try {
            context!!.getSharedPreferences(STOREAPP_SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()
                .clear()
                .commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
