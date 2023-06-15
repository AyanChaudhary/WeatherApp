package com.example.weatherapp2.ui

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapp2.Adapter.CitiesAdapter
import com.example.weatherapp2.Datastore.weatherDetails
import com.example.weatherapp2.Model.Weather
import com.example.weatherapp2.api.RetrofitInstance
import com.example.weatherapp2.databinding.ActivityMainBinding
import com.example.weatherapp2.others.ConnectionLiveData
import com.example.weatherapp2.others.Constants
import com.example.weatherapp2.others.PermissionUtil
import com.example.weatherapp2.others.Util
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    lateinit var weatherDetails: weatherDetails
    private lateinit var citiesAdapter: CitiesAdapter
    private lateinit var weatherList: MutableList<Weather>
    val sizeofList : MutableLiveData<Int> = MutableLiveData()
    private lateinit var perms : PermissionUtil

    private lateinit var cld : ConnectionLiveData



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        weatherDetails= weatherDetails(this)
        readDataFromDataStore()
        setupRecyclerView()
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
        perms= PermissionUtil(application,this)
        checkNetworkConnection()
        sizeofList.observe(this, Observer {
            it?.let {
                if(it==6){
                    binding.progressForRecyclerView.visibility=View.INVISIBLE
                    citiesAdapter.differ.submitList(weatherList.toList())
                    binding.progressForRecyclerView.visibility=View.INVISIBLE
                }
            }
        })
    }
    private fun checkNetworkConnection() {
        cld = ConnectionLiveData(application)
        cld.observe(this) { isConnected ->
            if (isConnected) {
                Snackbar.make(binding.root, "Internet Connected", Snackbar.LENGTH_SHORT).show()
                getCurrentLocation()
                getDataForCities()
            } else {
                Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_SHORT).show()
                readDataFromDataStore()
            }
        }
    }
    private fun getCurrentLocation(){
        if(perms.checkPermission()){
            if(perms.checkGPS()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){task->
                    val location: Location?=task.result
                    location?.let {
                        Log.d("TAG", "getCurrentLocation: ${it.latitude}, ${it.longitude}")
                        getCurrentLocationWeather(it.latitude,it.longitude)
                    }?:Toast.makeText(this,"location NULL",Toast.LENGTH_SHORT).show()
                }
            }else{
                Snackbar.make(binding.root,"Please enable the location",Snackbar.LENGTH_SHORT).show()
                readDataFromDataStore()
            }
        }else{
            perms.requstPermission()
        }
    }

    private fun setupRecyclerView(){
        citiesAdapter= CitiesAdapter()
        binding.rv.apply {
            adapter=citiesAdapter
            layoutManager=LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
//            layoutManager=StackLayoutManager()
        }
    }

    private fun getDataForCities() {
        val citiesList= listOf("New York","Singapore","Mumbai","Delhi","Sydney","Melbourne")
        for(i in citiesList){
            getWeather(i)
        }
    }

    private fun getWeather(city : String) {
        weatherList= mutableListOf()
        Log.d("tag","starting fetching ${city}")
        CoroutineScope(Dispatchers.IO).launch {
            val response=try {
                RetrofitInstance.api.getWeatherByCity(city)
            }catch (e:IOException){
                Log.d("tag","e:${e.message}")
                Toast.makeText(this@MainActivity,"error is $e",Toast.LENGTH_SHORT).show()
                return@launch
            }catch (e:HttpException){
                Log.d("tag","e:${e.message}")
                Toast.makeText(this@MainActivity,"error is $e",Toast.LENGTH_SHORT).show()
                return@launch
            }

            if(response.isSuccessful && response.body()!=null){
                val data=response.body()
                data?.let {
                    weatherList.add(it)
                    sizeofList.postValue(weatherList.size)
                    Log.d("tag","${city} adding to list....")
                }
            }
        }
    }

    private fun getCurrentLocationWeather(latitude: Double, longitude: Double) {
        binding.llProgressbar.visibility= View.VISIBLE
        binding.progressForRecyclerView.visibility=View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val response=try {
                RetrofitInstance.api.getWeatherDetails(latitude,longitude)
            }catch (e:IOException){
                Toast.makeText(this@MainActivity,"error is $e",Toast.LENGTH_SHORT).show()
                Snackbar.make(binding.root,"error is $e",Snackbar.LENGTH_SHORT).show()
                return@launch
            }catch (e:HttpException){
                Toast.makeText(this@MainActivity,"error is $e",Toast.LENGTH_SHORT).show()
                Snackbar.make(binding.root,"error is $e",Snackbar.LENGTH_SHORT).show()
                return@launch
            }

            if(response.isSuccessful && response.body()!=null){
                withContext(Dispatchers.Main){
                    val data=response.body()
                    data?.let {
                        binding.llProgressbar.visibility=View.GONE
                        binding.apply {
                            tvLocation.text="${data.name}\n${data.sys.country}"
                            tvDescription.text=data.weather[0].main
                            tvTemp.text=data.main.temp.toInt().toString()+"°C"
                            val iconId=data.weather[0].icon
                            val imageUri="https://openweathermap.org/img/wn/${iconId}@2x.png"
                            Glide.with(this@MainActivity).load(imageUri).into(ivImage)
                            tvFeelsLike.text="Feels like : "+data.main.feels_like.toInt().toString()+"°C"
                            tvMaxTemp.text="Max Temp : "+data.main.temp_max.toInt().toString()+"°C"
                            tvMinTemp.text="Min Temp : "+data.main.temp_min.toInt().toString()+"°C"
                            tvSunrise.text="Sunrise\n"+Util.formattedTime(data.sys.sunrise.toLong())
                            tvSunset.text="Sunset\n"+Util.formattedTime(data.sys.sunset.toLong())
                            tvWindSpeed.text="Wind speed\n"+data.wind.speed.toString()+" m/s"
                            tvPressure.text="Pressure\n"+data.main.pressure.toString()+" hPa"
                            tvHumidity.text="Humidity\n"+data.main.humidity.toString()+" %"
                            tvAirQuality.text="Weather Type\n${data.weather[0].description}"
                            tvLastUpdateTime.text="${Util.formattedTime(data.dt.toLong())}"
                        }
                        saveDataToDataStore()
                    }
                }
            }
        }

    }

    private fun saveDataToDataStore() {
        lifecycleScope.launch {
            weatherDetails.writeToDataStore("location",binding.tvLocation.text.toString())
            weatherDetails.writeToDataStore("description",binding.tvDescription.text.toString())
            weatherDetails.writeToDataStore("temp",binding.tvTemp.text.toString())
            weatherDetails.writeToDataStore("feels_like",binding.tvFeelsLike.text.toString())
            weatherDetails.writeToDataStore("max_temp",binding.tvMaxTemp.text.toString())
            weatherDetails.writeToDataStore("min_temp",binding.tvMinTemp.text.toString())
            weatherDetails.writeToDataStore("sunrise",binding.tvSunrise.text.toString())
            weatherDetails.writeToDataStore("sunset",binding.tvSunset.text.toString())
            weatherDetails.writeToDataStore("windspeed",binding.tvWindSpeed.text.toString())
            weatherDetails.writeToDataStore("pressure",binding.tvPressure.text.toString())
            weatherDetails.writeToDataStore("humidity",binding.tvHumidity.text.toString())
            weatherDetails.writeToDataStore("last_update_time",binding.tvLastUpdateTime.text.toString())
            weatherDetails.writeToDataStore("airquality",binding.tvAirQuality.text.toString())
        }
    }

    private fun readDataFromDataStore(){
        lifecycleScope.launch {
            binding.apply {
                tvLocation.text=weatherDetails.readFromDataStore("location")?:"no value"
                tvDescription.text=weatherDetails.readFromDataStore("description")?:"no value"
                tvTemp.text=weatherDetails.readFromDataStore("temp")?:"no value"
                tvFeelsLike.text=weatherDetails.readFromDataStore("feels_like")?:"no value"
                tvMaxTemp.text=weatherDetails.readFromDataStore("max_temp")?:"no value"
                tvMinTemp.text=weatherDetails.readFromDataStore("min_temp")?:"no value"
                tvSunrise.text=weatherDetails.readFromDataStore("sunrise")?:"no value"
                tvSunset.text=weatherDetails.readFromDataStore("sunset")?:"no value"
                tvWindSpeed.text=weatherDetails.readFromDataStore("windspeed")?:"no value"
                tvPressure.text=weatherDetails.readFromDataStore("pressure")?:"no value"
                tvHumidity.text=weatherDetails.readFromDataStore("humidity")?:"no value"
                tvLastUpdateTime.text=weatherDetails.readFromDataStore("last_update_time")?:"no value"
                tvAirQuality.text=weatherDetails.readFromDataStore("airquality")?:"no value"
            }
        }
    }


    override fun onResume() {
        super.onResume()
        readDataFromDataStore()
    }



    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==Constants.PERMISSION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"granted",Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }else{
                Toast.makeText(this,"error",Toast.LENGTH_SHORT).show()
            }
        }
    }

}