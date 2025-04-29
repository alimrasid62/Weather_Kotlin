package com.alimrasid.cuaca_1.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.alimrasid.cuaca_1.R
import com.alimrasid.cuaca_1.api.RetrofitClient
import com.alimrasid.cuaca_1.api.WeatherResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private val API_KEY = "iNTNO6vRQlWVu1qN00k6j6zNc1xjBKwj"

    private lateinit var mainContainer: ConstraintLayout

    private lateinit var tvDate: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvWeather: TextView
    private lateinit var tvWind: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var imgWeather: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mainContainer = this.findViewById(R.id.main_container)

        tvDate = findViewById(R.id.tv_date)
        tvLocation = findViewById(R.id.tv_location)
        tvTemperature = findViewById(R.id.tv_temperature)
        tvWeather = findViewById(R.id.tv_weather)
        tvWind = findViewById(R.id.tv_wind)
        tvHumidity = findViewById(R.id.tv_humidity)
        imgWeather = findViewById(R.id.img_weather)

        setCurrentDate()

        fetchWeather("semarang")
    }

    private fun setCurrentDate() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
        val dateText = dateFormat.format(calendar.time)
        tvDate.text = "$dateText"
    }

    private fun fetchWeather(location: String) {
        val call = RetrofitClient.instance.getRealtimeWeather(location, API_KEY)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weather = response.body()?.data?.values
                    val temperature = weather?.temperature
                    val humidity = weather?.humidity
                    val windSpeed = weather?.windSpeed

                    // Set data ke TextView
                    tvLocation.text = location.capitalize()
                    tvTemperature.text = "${temperature?.toInt() ?: 0}°"
                    tvHumidity.text = "${humidity?.toInt() ?: 0} %"
                    tvWind.text = "${windSpeed?.toInt() ?: 0} km/h"

                    // Atur cuaca (Sunny, Cloudy, Rainy)
                    val weatherCondition = when {
                        temperature != null && temperature > 30 -> "Sunny"
                        humidity != null && humidity > 70 -> "Rainy"
                        else -> "Cloudy"
                    }

                    tvWeather.text = weatherCondition

                    // Ganti gambar sesuai cuaca
                    when (weatherCondition) {
                        "Sunny" -> {
                            imgWeather.setImageResource(R.drawable.ic_sunny)
                            mainContainer.setBackgroundResource(R.drawable.bg_sunny)
                        }
                        "Rainy" -> {
                            imgWeather.setImageResource(R.drawable.ic_rainy)
                            mainContainer.setBackgroundResource(R.drawable.bg_rainy)
                        }
                        "Cloudy" -> {
                            imgWeather.setImageResource(R.drawable.ic_cloudy)
                            mainContainer.setBackgroundResource(R.drawable.bg_gradient)
                        }
                    }


                } else {
                    Toast.makeText(this@HomeActivity, "Failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}