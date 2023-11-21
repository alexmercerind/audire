package com.alexmercerind.audire.api.shazam

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class ShazamRetrofitInstance {
    companion object {

        private const val BASE_URL = "https://amp.shazam.com/"

        private val instance: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api: ShazamAPI by lazy {
            instance.create(ShazamAPI::class.java)
        }
    }
}
