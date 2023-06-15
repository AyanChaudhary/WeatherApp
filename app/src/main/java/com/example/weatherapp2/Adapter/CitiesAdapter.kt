package com.example.weatherapp2.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp2.Model.Weather
import com.example.weatherapp2.databinding.ItemLayoutForCitiesViewBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CitiesAdapter:RecyclerView.Adapter<CitiesAdapter.CityViewHolder>() {

    inner class CityViewHolder(val binding: ItemLayoutForCitiesViewBinding):RecyclerView.ViewHolder(binding.root)

    val differCallBack = object :DiffUtil.ItemCallback<Weather>(){
        override fun areItemsTheSame(oldItem: Weather, newItem: Weather): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Weather, newItem: Weather): Boolean {
            return oldItem==newItem
        }
    }

    val differ =AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        return CityViewHolder(ItemLayoutForCitiesViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val item=differ.currentList[position]
        holder.binding.apply {
            tvCityName.text="City Name : ${item.name}"
            tvCityDescription.text="Description : ${item.weather[0].description}"
            tvCityFeelLike.text="Feels like : ${item.main.feels_like}"
            tvCityTemp.text="Temp : ${item.main.temp}°C"
            tvCityPressure.text="Pressure : ${item.main.pressure}"
            tvCitySunrise.text="Sunrise : ${formattedTime(item.sys.sunrise.toLong())}"
            tvCitySunset.text="Sunset : ${formattedTime(item.sys.sunset.toLong())}"
            tvCityMaxTemp.text="Max Temp : ${item.main.temp_max.toInt()}°C"
            tvCityMinTemp.text="Min Temp : ${item.main.temp_min.toInt()}°C"
            tvCityWindSpeed.text="Wind Speed : ${item.wind.speed} m/s"
            tvCityPressure.text="Pressure : ${item.main.pressure} hPa"
        }

    }

    override fun getItemCount(): Int =differ.currentList.size

    private fun formattedTime(ms: Long) : String{
        val value= Date(ms*1000L)
        val sdf= SimpleDateFormat("HH:mm a", Locale.ENGLISH)
        return sdf.format(value)
    }
}