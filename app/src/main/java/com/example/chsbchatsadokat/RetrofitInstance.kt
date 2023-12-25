package com.example.chsbchatsadokat.firebasechat
import com.example.chsbchatsadokat.firebasechat.`interface`.NotificationApi
import com.example.chsbchatsadokat.Constans.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object{
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        val api by lazy {
            retrofit.create(NotificationApi::class.java)
        }
    }
}