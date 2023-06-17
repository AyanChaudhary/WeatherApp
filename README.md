
# Weather App

Weather App is a simple weather forecast app, which uses some APIs to fetch  forecast data from the [OpenWeatherMap](https://openweathermap.org/current) based on current device location, cities and countries. It shows previously fetched data in case of no internet connection and also displays weather forecast details of some well known metropolitan cities.


## Screenshots

![App Screenshot](https://github.com/AyanChaudhary/WeatherApp/assets/112795104/c99be0d5-25b6-45c8-8173-280a23ff7c49)


## Installation

Weather uses the [OpenWeatherMap](https://openweathermap.org/current) to fetch weather data. To run this app, you'll need an OpenWeatherMap API key. This is entirely free. To do so :
1. Create your account [here](https://home.openweathermap.org/users/sign_in) and get your OpenWeatherMap API key.
2. Open the project, and go to the `Constants.kt` file.
3. Replace `const val API_KEY = "` by your key.
    
## Libraries and Tools 

- [Data Store](https://developer.android.com/topic/libraries/architecture/datastore)
- [Live Data](https://developer.android.com/topic/libraries/architecture/livedata)
- [Retrofit](https://square.github.io/retrofit/)
- [Okhttp](https://github.com/square/okhttp)
- [Glide](https://github.com/bumptech/glide)
- [View Binding](https://developer.android.com/topic/libraries/view-binding)
- [Coroutines](https://developer.android.com/kotlin/coroutines)
