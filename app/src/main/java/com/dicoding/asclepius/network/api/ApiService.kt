package com.dicoding.asclepius.network.api

import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("everything")
    fun getNews(
        @Query("q") q: String = "cancer",
        @Query("language") language: String = "en",
        @Query("pageSize") pageSize: Int = 12,
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY
    ): Call<NewsResponse>
}