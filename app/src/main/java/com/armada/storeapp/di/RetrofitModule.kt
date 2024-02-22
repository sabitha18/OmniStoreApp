package com.armada.storeapp.di

import android.app.Application
import android.content.Context
import com.armada.storeapp.data.*
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    //    private val WMS_URL = "http://10.110.31.90:8009/"
   // private val POS_URL="https://api.armadagroupco.com:7790/" // live
    private val POS_URL="http://10.110.31.189:6663/" // UAT URL
    //    private val POS_URL="https://api.armadagroupco.com:7791"
//    private val POS_URL="http://10.110.31.187:7790/"
//    private val POS_URL = "http://10.110.31.189:6661/"
//    private val WMS_URL = "http://195.39.137.54:8009/"
    private val WMS_URL = "http://10.110.31.90:8009/"
    private val RIVA_DOMAIN = "https://www.rivafashion.com/rest/V1/"
    private val RIVA_SECONDARY_DOMAIN = "https://mob2.rivafashion.com/api/ver7/"
    private val PAYMENT_GATEWAY_URL = "https://api.tap.company/v2/"

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
//        val interceptor = Interceptor { chain ->
//            val newRequest =
//                chain.request().newBuilder().addHeader("Accept", "application/json")
//                    .addHeader("Content-Type", "application/json")
//                    .addHeader("Content-type", "application/x-www-form-urlencoded")
//                    .build()
//            chain.proceed(newRequest)
//        }
        return OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .readTimeout(2 * 120, TimeUnit.SECONDS)
            .connectTimeout(2 * 120, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return GsonConverterFactory.create(gson)
    }

    @Singleton
    @Provides
    @Named("WMS")
    fun provideWMSRetrofitClient(gsonConverterFactory: GsonConverterFactory): Retrofit {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return Retrofit.Builder()

            .baseUrl(WMS_URL)
            .client(getPOSClient())
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    //    @Named("POS_URL") posUrl: String
    @Singleton
    @Provides
    @Named("POS")
    fun providePOSRetrofitClient(
        gsonConverterFactory: GsonConverterFactory

    ): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        return Retrofit.Builder()

            .baseUrl(POS_URL)
            .client(trustEveryone())
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    @Named("RIVA_DOMAIN")
    fun provideRivaRetrofitClient(gsonConverterFactory: GsonConverterFactory): Retrofit {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY) // Choose the desired logging level

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        return Retrofit.Builder()

            .baseUrl(RIVA_DOMAIN)
            .client(getPOSClient())
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    @Named("RIVA_SUBDOMAIN")
    fun provideRivaSecondaryRetrofitClient(gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()

            .baseUrl(RIVA_SECONDARY_DOMAIN)
            .client(trustEveryone())
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    @Named("PAYMENT_GATEWAY")
    fun providePaymentGatewayRetrofitClient(gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()

            .baseUrl(PAYMENT_GATEWAY_URL)
            .client(trustEveryone())
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    @Named("POS_URL")
    fun providePOS_URL(context: Context): String {
        val sharedpreferenceHandler = SharedpreferenceHandler(context)
        val posURL = sharedpreferenceHandler.getData(SharedpreferenceHandler.POS_URL, "")
        return posURL!!

    }

//    @Provides
//    @Singleton
//    @Named("WMS")
//    fun provideBaseURL(context: Context): String {
//        val sharedpreferenceHandler = SharedpreferenceHandler(context)
//        val baseURL = sharedpreferenceHandler.getData(SharedpreferenceHandler.WMS_URL, "")
//        return baseURL!!
//
//    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Singleton
    @Provides
    fun providesWMSApiService(@Named("WMS") retrofit: Retrofit): WarehouseApiService =
        retrofit.create(WarehouseApiService::class.java)

    @Singleton
    @Provides
    fun provideApiService(@Named("POS") retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideRivaApiService(@Named("RIVA_DOMAIN") retrofit: Retrofit): RivaApiService =
        retrofit.create(RivaApiService::class.java)

    @Singleton
    @Provides
    fun provideRivaSecondaryApiService(@Named("RIVA_SUBDOMAIN") retrofit: Retrofit): RivaSecondaryApiService =
        retrofit.create(RivaSecondaryApiService::class.java)

    @Singleton
    @Provides
    fun providePaymentApiService(@Named("PAYMENT_GATEWAY") retrofit: Retrofit): PaymentGatewayApiService =
        retrofit.create(PaymentGatewayApiService::class.java)


    private fun getPOSClient(): OkHttpClient? {
        return try {
            HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
            val context = SSLContext.getInstance("TLS")
            val trustManager = object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                    return arrayOfNulls(0)
                }
            }
            context.init(null, arrayOf(trustManager), SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(
                context.socketFactory
            )
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val interceptor = Interceptor { chain ->
                val newRequest =
                    chain.request().newBuilder()
//                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")
//                        .addHeader("Content-type", "application/x-www-form-urlencoded")
                        .build()
                chain.proceed(newRequest)
            }
            OkHttpClient.Builder()
                .sslSocketFactory(context.socketFactory, trustManager)
                .hostnameVerifier(HostnameVerifier { _, _ -> true })
                .addInterceptor(interceptor)
                .addInterceptor(loggingInterceptor)
                .readTimeout(2 * 120, TimeUnit.SECONDS)
                .connectTimeout(2 * 120, TimeUnit.SECONDS)
//                .addInterceptor { chain ->
////                    val newRequest = chain.request().newBuilder()
////                        .addHeader("Authorization", Constants.BEARER_TOKEN)
////                        .build()
////                    chain.proceed(newRequest)
//
//                    val original = chain.request()
//
//                    val request = original.newBuilder()
//                        .addHeader("Authorization", "Bearer "+Constants.BEARER_TOKEN)
//                        .method(original.method, original.body)
//                        .build()
//
//                     chain.proceed(request)
//                }
                .build()

        } catch (e: Exception) { // should never happen
            throw java.lang.RuntimeException(e)
        }
    }

    private fun trustEveryone(): OkHttpClient? {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
        }
        return try {
            HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
            val context = SSLContext.getInstance("TLS")
            val trustManager = object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                    return arrayOfNulls(0)
                }
            }
            context.init(null, arrayOf(trustManager), SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(
                context.socketFactory
            )
            OkHttpClient.Builder()
                .sslSocketFactory(context.socketFactory, trustManager)
                .hostnameVerifier(HostnameVerifier { _, _ -> true })
                .addInterceptor(loggingInterceptor)
                .readTimeout(2 * 120, TimeUnit.SECONDS)
                .connectTimeout(2 * 120, TimeUnit.SECONDS)
                .build()

        } catch (e: Exception) { // should never happen
            throw java.lang.RuntimeException(e)
        }
    }

}