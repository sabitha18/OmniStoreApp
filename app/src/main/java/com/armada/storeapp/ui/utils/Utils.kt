package com.armada.storeapp.ui.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.preference.PreferenceManager
import android.util.Base64
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.armada.storeapp.BuildConfig
import com.armada.storeapp.data.model.response.HomeDataModel
import com.armada.storeapp.data.model.response.SearchBannerModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.UnsupportedEncodingException
import java.math.RoundingMode
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Utils {

    val device_type = "A"
    val api_version = BuildConfig.VERSION_NAME
    var empty_product: HomeDataModel.EmptyPages = HomeDataModel.EmptyPages()
    var emptyCartPage: HomeDataModel.EmptyPages = HomeDataModel.EmptyPages()
    var emptyWishlistPage: HomeDataModel.EmptyPages = HomeDataModel.EmptyPages()
    var empty_order: HomeDataModel.EmptyPages = HomeDataModel.EmptyPages()
    var empty_promotion: HomeDataModel.EmptyPages = HomeDataModel.EmptyPages()
    var empty_address: HomeDataModel.EmptyPages = HomeDataModel.EmptyPages()
    var empty_notification: HomeDataModel.EmptyPages = HomeDataModel.EmptyPages()
    var empty_store: HomeDataModel.EmptyPages = HomeDataModel.EmptyPages()
    var tempSearchModel = SearchBannerModel()
    private var page_banners: ArrayList<HomeDataModel.PageBanner> = ArrayList()


    fun hasInternetConnection(context: Context?): Boolean {
        if (context == null)
            return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        val activeNetwork = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.activeNetwork ?: return false
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun showSnackbar(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        val snackBarView: View = snackbar.view
        snackBarView.setBackgroundColor(Color.parseColor("#b87253"))
        snackbar.show()
    }

    fun setSearchModel(searchBannerModel: SearchBannerModel) {
        tempSearchModel = searchBannerModel
    }

    fun showSnackbarWithAction(view: View, message: String, string: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction(string, null).show()
    }

    //Snackbar for error
    fun showErrorSnackbar(view: View) {
        if (view != null) {
//            val snackbar: Snackbar = if (AppController.instance.isLangArebic) {
//                Snackbar.make(
//                    view,
//                    "حدث خطأ أثناء التواصل مع ريڤا فاشن ؛ حاول مرة أخري",
//                    Snackbar.LENGTH_SHORT
//                )
//            } else {
            val snackbar = Snackbar.make(
                view,
                "There was an error communicating with RIVA server, please try again later",
                Snackbar.LENGTH_SHORT
            )
//            }
            val snackBarView: View = snackbar.view
            snackBarView.setBackgroundColor(Color.parseColor("#b87253"))
            snackbar.show()
        }
    }

    fun firstLetterCaps(activity: Activity, value: String?): String {
        return if (!value.isNullOrEmpty()) {
            try {
                value.split(' ').joinToString(" ") { it ->
                    it.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                }
            } catch (e: Exception) {
                value
            }
        } else {
            ""
        }
    }

    fun removeLastCharacter(str: String): String { //remove last char from string
        var str = str
        if (str.isNotEmpty() && str[str.length - 1] == ',') {
            str = str.substring(0, str.length - 1)
        }
        return str
    }


    fun encrypt(strToEncrypt: String, secret_key: String): String? {
//        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray

        try {
            keyBytes = secret_key.toByteArray(charset("UTF8"))
            val skey = SecretKeySpec(keyBytes, "AES")
            val input = strToEncrypt.toByteArray(charset("UTF8"))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
                cipher.init(Cipher.ENCRYPT_MODE, skey)

                val cipherText = ByteArray(cipher.getOutputSize(input.size))
                var ctLength = cipher.update(
                    input, 0, input.size,
                    cipherText, 0
                )
                ctLength += cipher.doFinal(cipherText, ctLength)
                return Base64.encodeToString(cipherText, Base64.DEFAULT)
            }
        } catch (uee: UnsupportedEncodingException) {
            uee.printStackTrace()
        } catch (ibse: IllegalBlockSizeException) {
            ibse.printStackTrace()
        } catch (bpe: BadPaddingException) {
            bpe.printStackTrace()
        } catch (ike: InvalidKeyException) {
            ike.printStackTrace()
        } catch (nspe: NoSuchPaddingException) {
            nspe.printStackTrace()
        } catch (nsae: NoSuchAlgorithmException) {
            nsae.printStackTrace()
        } catch (e: ShortBufferException) {
            e.printStackTrace()
        }

        return null
    }

    fun getDeviceWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun getDeviceHeight(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun encrypt(value: String): String? {
        val KEY_AES = "a$" + "ten*\$@#!^@2020"
        try {
            var key = KEY_AES.toByteArray(charset("UTF8"))
            var ivs = KEY_AES.toByteArray(charset("UTF8"))
//            byte[] key = KEY_AES . getBytes ("UTF-8");
//            byte[] ivs = KEY_AES . getBytes ("UTF-8");
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
//            Cipher cipher = Cipher . getInstance ("AES/CBC/PKCS7Padding");
            val secretKeySpec = SecretKeySpec(key, "AES")
//            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            val paramSpec = IvParameterSpec(ivs)
//            AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivs);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, paramSpec)

            return Base64.encodeToString(
                cipher.doFinal(value.toByteArray(charset("UTF8"))),
                Base64.DEFAULT
            )

//            return Base64.encodeToString(cipher.doFinal(value.getBytes("UTF-8")), Base64.DEFAULT);
        } catch (e: Exception) {
            e.printStackTrace();
        }
        return null
    }

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

    /////Price Formatting with currency
    fun getPriceFormatted(price: String?, selectedCurrency: String): String {

        var formattedPrice: String
        var strPrice = price
        if (strPrice != null && strPrice.isNotEmpty()) {

            strPrice = strPrice.replace("٫", ".").trim()
            strPrice = strPrice.replace(",", "").trim()
            if (strPrice.contains(" ")) {
                strPrice = strPrice.split(" ")[0]
            }
        }

        formattedPrice = if (strPrice != null && strPrice.isNotEmpty()) {
            try {
                "" + String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    java.lang.Double.parseDouble(strPrice.toString())
                )
            } catch (e: Exception) {
                e.printStackTrace()
                "" + String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    java.lang.Double.parseDouble(strPrice.toString())
                )
            }

        } else {
            "$strPrice $selectedCurrency"
        }
        return "$formattedPrice $selectedCurrency"
    }

    fun getConvertedCurrency(activity: Context, str: String): String {
        var strcurrency = ""
//        if (AppController.instance.isLangArebic) {
        when {
            str.equals("KWD", true) -> {
                strcurrency = "د.ك"
            }
            str.equals("QAR", true) -> {
                strcurrency = "ر.ق"
            }
            str.equals("SAR", true) -> {
                strcurrency = "ر.س"
            }
            str.equals("$", true) -> {
                strcurrency = "$"
            }
            str.equals("BHD", true) -> {
                strcurrency = "د.ب"
            }
            str.equals("OMR", true) -> { //oman
                strcurrency = "ر.ع"
            }
            str.equals("AED", true) -> {
                strcurrency = "د.إ"
            }
            else -> {
                strcurrency = str
            }
        }
//        } else {
//            strcurrency = str
//        }
        return strcurrency
    }

    fun dp2px(value: Float, context: Context): Int { //convert dp to px
        return (value / context.resources.displayMetrics.density).toInt()
    }

    @JvmStatic
    fun loadImagesUsingCoil(context: Context, strUrl: String?, imageView: ImageView) {
        imageView.load(strUrl) {
            crossfade(true)
            allowConversionToBitmap(true)
            bitmapConfig(Bitmap.Config.ARGB_8888)
            allowHardware(true)
            listener(object : coil.request.ImageRequest.Listener {

            })
        }
    }

    @JvmStatic
    fun loadImagesUsingCoilWithSize(
        context: Context,
        strUrl: String?,
        imageView: ImageView,
        width: Int,
        height: Int
    ) {
        if (width.toInt() > 0 && height.toInt() > 0) {
            imageView.load(strUrl) {
                crossfade(true)
                allowConversionToBitmap(true)
                bitmapConfig(Bitmap.Config.ARGB_8888)
                allowHardware(true)
                size(width, height)
                listener(object : coil.request.ImageRequest.Listener {
                })
            }
        }
    }

    @JvmStatic
    fun loadGifUsingCoilWithSize(
        context: Context,
        strUrl: String?,
        imageView: ImageView,
        width: Int,
        height: Int
    ) {
        if (width.toInt() > 0 && height.toInt() > 0) {
            val imageLoader = ImageLoader.Builder(context)
                .componentRegistry {
                    if (Build.VERSION.SDK_INT >= 28) {
                        add(ImageDecoderDecoder(context))
                    } else {
                        add(GifDecoder())
                    }
                }
                .build()

            val request = coil.request.ImageRequest.Builder(context)
                .data(strUrl)
                .target(imageView)
                .crossfade(true)
                .size(width, height)
                .allowConversionToBitmap(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .allowHardware(true)
                .build()

            imageLoader.enqueue(request)
        }
    }

    @JvmStatic
    fun loadGifUsingCoil(context: Context, strUrl: String?, imageView: ImageView) {
        val imageLoader = ImageLoader.Builder(context)
            .componentRegistry {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder(context))
                } else {
                    add(GifDecoder())
                }
            }
            .build()

        val request = coil.request.ImageRequest.Builder(context)
            .data(strUrl)
            .target(imageView)
            .crossfade(true)
            .allowConversionToBitmap(true)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .allowHardware(true)
            .build()

        imageLoader.enqueue(request)
    }

    fun setPageBanner(page_banners: ArrayList<HomeDataModel.PageBanner>) {
        this.page_banners = page_banners
    }

    fun getPageBanner(): ArrayList<HomeDataModel.PageBanner> {
        return this.page_banners
    }

    fun setProductEmptyPage(empty_product: HomeDataModel.EmptyPages) {
        this.empty_product = empty_product
    }

    fun getDynamicStringFromApi(activity: Context, value: String?): String {
        //println("""Here i am dynamic string   $value""")
        val a = value?.lowercase()?.replace(" ", "_")?.replace("-", "_")?.replace(".", "_")
            ?.replace("!", "")
            ?.replace("'", "")?.replace("&", "_")?.replace("___", "_")?.replace("(", "_")
            ?.replace(")", "_")?.replace("©", "_")?.replace("'", "_")?.replace("§", "_")
            ?.replace("´", "_")?.replace("super", "super_")?.trim()
        //println("Here i am value is $a")
        var result = ""
        result = try {
            activity.resources.getText(
                activity.resources.getIdentifier(
                    a,
                    "string",
                    "com.armada.riva"
                )
            ) as String
        } catch (e: Exception) {
            // println("Here i am value is exception 1  ${e.localizedMessage}")
            value.toString()
        } catch (e: Resources.NotFoundException) {
            //println("Here i am value is exception 2  ${e.localizedMessage}")
            value.toString()
        }
        return result
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDateTime(inputFormat: String, outputFormat: String, value: String): String {
        val curFormater = SimpleDateFormat(inputFormat, Locale.ENGLISH)
        var dateObj: Date? = null
        try {
            dateObj = curFormater.parse(value)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        var newDateStr: String
        val postFormater = SimpleDateFormat(outputFormat)
        newDateStr = postFormater.format(dateObj)
//        newDateStr = englishToArabicConvertor(newDateStr)

        return newDateStr
    }

    /////Amazing price
    fun getAmazingPrice(): Double {
        var amazingPrice = 0.0
        val strStroreCode = "en"
//            SharedPreferencesManager.getString(this, Constants.PREFS_STORE_CODE_EN, "en")

        if (strStroreCode.equals("en")) {
            amazingPrice = 25.0
        } else if (strStroreCode.equals("bahrain_en")) {
            amazingPrice = 9.0
        } else if (strStroreCode.equals("qatar_en")) {
            amazingPrice = 90.0
        } else if (strStroreCode.equals("ksa_en")) {
            amazingPrice = 90.0
        } else if (strStroreCode.equals("uae_en")) {
            amazingPrice = 90.0
        } else if (strStroreCode.equals("kuwait_en")) {
            amazingPrice = 7.5
        } else if (strStroreCode.equals("oman_en")) { //oman
            amazingPrice = 10.0
        }

        return amazingPrice
    }

    //////Price formattign without currency
    fun getPriceFormattedWithoutCurrency(price: String): String {
        val currency: Currency
        val countryCode = "KW"
//        SharedPreferencesManager.getString(this, Constants.PREFS_COUNTRY_CODE, "KW")

        var formattedPrice: String
        try {
            currency = Currency.getInstance(Locale("kuwait_en", countryCode))
            formattedPrice = String.format(
                Locale.ENGLISH,
                "%.2f",
                java.lang.Double.parseDouble(price.toString())
            )
        } catch (e: Exception) {
            e.printStackTrace()
            formattedPrice = String.format(
                Locale.ENGLISH,
                "%.2f",
                java.lang.Double.parseDouble(price.toString())
            )
        }

        return formattedPrice
    }

    ////User for local barcode search
    fun saveArrayList(context: Context, list: ArrayList<String>, key: String) {
        val prefs = SharedpreferenceHandler(context)
        val gson = Gson()
        val json = gson.toJson(list)
        prefs.saveData(key, json)
    }

    @Suppress("DEPRECATION")
    fun getArrayList(context: Context, key: String): ArrayList<String>? {
        val prefs = SharedpreferenceHandler(context)
        val gson = Gson()
        val json = prefs.getData(key, "")
        val type = object : TypeToken<ArrayList<String>>() {

        }.type
        return gson.fromJson<ArrayList<String>>(json, type)
    }

    @Suppress("DEPRECATION")
    fun clearArrayList(context: Context, key: String) {
        val prefs = SharedpreferenceHandler(context)
        prefs.saveData(key, null)
    }

    fun getMobileNoLimitBasedOnCountry(strCountryCode: String?): Int {
        var mobileNoLimit = 12

        when (strCountryCode) {
            "BH" -> //Bahrain
                mobileNoLimit = 8
            "QA" -> //Qatar
                mobileNoLimit = 8
            "SA" -> //Saudi arabic
                mobileNoLimit = 10
            "AE" -> //UAE
                mobileNoLimit = 10
            "KW" -> //Kuwait
                mobileNoLimit = 8
            "OM" -> //Oman
                mobileNoLimit = 8
        }
        return mobileNoLimit
    }

    fun getMobileNoLimitBasedOnCountryUpdated(
        strCountryCode: String?,
        strMobileNumber: String?
    ): Int {
        var mobileNoLimit = 12

//            if (strMobileNumber!!.startsWith("0")) {
//                 println("Here i am mobile start 111  " + strCountryCode)
//            } else {
//                println("Here i am mobile start 222 " + strCountryCode)
//            }
        when (strCountryCode) {
            "BH" -> //Bahrain
                mobileNoLimit = 8
            "QA" -> //Qatar
                mobileNoLimit = 8
            "SA" -> //Saudi arabic
            {
                mobileNoLimit = if (strMobileNumber?.startsWith("0") == true) {
                    10
                } else {
                    9
                }
            }
            "AE" -> //UAE
                mobileNoLimit = 9
            "KW" -> //Kuwait
                mobileNoLimit = 8
            "OM" -> //Oman
                mobileNoLimit = 8
        }
        return mobileNoLimit
    }

    //
    fun isInternational(strCountryCode: String?): Boolean {
        var isInterNational = true

        when (strCountryCode) {
            "BH" -> //Bahrain
                isInterNational = false
            "QA" -> //Qatar
                isInterNational = false
            "SA" -> //Saudi arabic
                isInterNational = false
            "AE" -> //UAE
                isInterNational = false
            "KW" -> //Kuwait
                isInterNational = false
            "OM" -> //Oman
                isInterNational = false
        }
        return isInterNational
    }


    fun removePlus(code: String): String {
        return code.replace("+", "")
    }

    fun addPlus(code: String): String {
        return "+$code"
    }


    fun removeUnnamedRoad(strText: String?): String {
        return strText?.replace("(?i)Unnamed Road".toRegex(), "") ?: ""
    }

}