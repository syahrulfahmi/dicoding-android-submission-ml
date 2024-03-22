package com.dicoding.asclepius

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.NewsResponse
import com.dicoding.asclepius.helper.Result
import com.dicoding.asclepius.network.api.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewsViewModel : ViewModel() {

    private var _newsResponse: MutableLiveData<Result<NewsResponse>> = MutableLiveData()
    val newsResponse: LiveData<Result<NewsResponse>> = _newsResponse

    init {
        getListNews()
    }

    private fun getListNews() {
        viewModelScope.launch {
            val apiConfig = ApiConfig.getInstance()
            apiConfig.getNews().enqueue(object : Callback<NewsResponse> {
                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    if (response.isSuccessful) {
                        _newsResponse.value = Result.Success(response.body())
                    }
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    _newsResponse.value = Result.Error(t.message.orEmpty())
                }
            })
        }
    }
}