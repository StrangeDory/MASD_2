package com.example.masd_2.Common

import com.example.masd_2.Interface.NewsService
import com.example.masd_2.Remote.RetrofitClient

object Common {
    val BASE_URL = "https://newsapi.org/"
    val API_KEY = "eaec8f99eedc44efab4415d2c9c21afa"

    val newsService: NewsService
    get() = RetrofitClient.getClient(BASE_URL).create(NewsService::class.java)

    fun getNewsAPI(source: String): String {
        val apiURL = StringBuilder("https://newsapi.org/v2/top-headlines?sources=")
            .append(source).append("&apiKey=").append(API_KEY).toString()
        return apiURL
    }

    fun getSourceSearch(searchStr: String): String {
        val apiURL = StringBuilder("https://newsapi.org/v2/sources?q=").append(searchStr)
            .append("&apiKey=").append(API_KEY).toString()
        return apiURL
    }
}