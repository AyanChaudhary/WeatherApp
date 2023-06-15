package com.example.weatherapp2.api

import com.example.weatherapp2.others.Constants
import com.example.weatherapp2.Model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface weatherApiInterface {

    @GET("weather?appid=${Constants.API_KEY}")
    suspend fun getWeatherDetails(
        @Query("lat")lat:Double,
        @Query("lon")long:Double,
        @Query("units")units:String="metric"
    ) : Response<Weather>

    @GET("weather?appid=${Constants.API_KEY}")
    suspend fun getWeatherByCity(
        @Query("q")q:String,
        @Query("units")units:String="metric"
    ) : Response<Weather>
}