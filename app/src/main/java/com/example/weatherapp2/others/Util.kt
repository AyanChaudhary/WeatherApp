package com.example.weatherapp2.others

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Util {

    companion object{
        fun formattedTime(ms: Long) : String{
            val value= Date(ms*1000L)
            val sdf= SimpleDateFormat("dd/MM/yyyy \n HH:mm a", Locale.ENGLISH)
            return sdf.format(value)
        }
    }

}