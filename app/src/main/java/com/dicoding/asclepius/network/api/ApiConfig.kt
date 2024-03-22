package com.dicoding.asclepius.network.api

import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.helper.API
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiConfig {

    private fun getAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val req = chain.request()
            val headers = req.newBuilder()
                .addHeader(API.AUTHORIZATION, BuildConfig.API_KEY)
                .build()
            chain.proceed(headers)
        }
    }

    private fun getClientInterceptor(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(getAuthInterceptor())
            .build()
    }

    fun getInstance(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(getClientInterceptor())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}