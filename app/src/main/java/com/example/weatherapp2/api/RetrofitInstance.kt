package com.example.weatherapp2.api

import com.example.weatherapp2.others.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        val client=OkHttpClient.Builder().build()
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
    val api by lazy{
        retrofit.create(weatherApiInterface::class.java)
    }

}