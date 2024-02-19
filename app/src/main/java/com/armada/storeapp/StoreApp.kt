package com.armada.storeapp

import androidx.multidex.MultiDexApplication
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Singleton

@HiltAndroidApp
@Singleton
class StoreApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
//        setPosUrl()
    }

//    private fun setPosUrl() {
//        val sharedpreferenceHandler = SharedpreferenceHandler(this)
////        val baseUrl = sharedpreferenceHandler.getData(SharedpreferenceHandler.POS_URL, "")
////        if (baseUrl.equals("")) {
////            sharedpreferenceHandler.saveData(
////                SharedpreferenceHandler.POS_URL,
////                "http://10.110.31.189:6663/"
////            )
////        }
//
////        if (baseUrl.equals("")) {
//        sharedpreferenceHandler.saveData(
//            SharedpreferenceHandler.POS_URL,
//            "https://api.armadagroupco.com:7790/"
//        )
//        "http://10.110.31.189:6661/"
//        "https://api.armadagroupco.com:7790/" current latest live url
//        https://api.armadagroupco.com:8687/api/
//        http://10.110.31.187:7773/
//        http://10.110.31.190:7771/
//        }
//    }
}