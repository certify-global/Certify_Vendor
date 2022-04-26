package com.certify.vendor.api

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.certify.vendor.common.Constants
import com.certify.vendor.data.AppSharedPreferences
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@SuppressLint("StaticFieldLeak")
object RetrofitInstance {

    private var context: Context? = null
    lateinit var apiInterface : ApiInterface

    fun init (context: Context?) {
        this.context = context
        createRetrofitInstance(context)
    }

    private fun createRetrofitInstance(context: Context?) {
        apiInterface = Retrofit.Builder().run {
            baseUrl("https://apiqa.certify.me/VCSAPI/")
            addConverterFactory(GsonConverterFactory.create())
            client(createOkHttpClient())
            build()
        }.create(ApiInterface::class.java)
    }

    private fun createOkHttpClient() : OkHttpClient {
        val sharedPreferences = AppSharedPreferences.getSharedPreferences(this.context)
        val okHttpClient = OkHttpClient.Builder().apply {
            addInterceptor { chain ->
                val requestOriginal = chain.request()
                val requestBuilder = requestOriginal.newBuilder().apply {
                    header("Accept", "application/json")
                    header("Content-Type", "application/json")
                    header("grant_type", "password")
                    header("Authorization", "Bearer " + sharedPreferences?.getString(Constants.ACCESS_TOKEN, ""))
                    method(requestOriginal.method(), requestOriginal.body())
                }
                chain.proceed(requestBuilder.build())
            }
        }
        return okHttpClient.build()
    }
}