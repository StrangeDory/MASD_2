package com.example.masd_2.Interface

import com.example.masd_2.Model.News
import com.example.masd_2.Model.Source
import com.example.masd_2.Model.WebSite
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface NewsService {
    @get:GET("v2/top-headlines/sources?apiKey=eaec8f99eedc44efab4415d2c9c21afa")

    val sources: Call<WebSite>

    @GET
    fun getNewsFromSource(@Url url: String): Call<News>

    @GET
    fun getSourceSearch(@Url url: String): Call<WebSite>
}