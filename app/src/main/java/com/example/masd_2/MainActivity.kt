package com.example.masd_2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.masd_2.Adapter.Adapter
import com.example.masd_2.Adapter.ListNewsAdapter
import com.example.masd_2.Common.Common
import com.example.masd_2.Interface.NewsService
import com.example.masd_2.Model.News
import com.example.masd_2.Model.Source
import com.example.masd_2.Model.WebSite
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class MainActivity : AppCompatActivity() {

    lateinit var layoutManager: LinearLayoutManager
    lateinit var mService: NewsService
    lateinit var adapter: Adapter
    lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Paper.init(this)
        mService = Common.newsService
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        swipeRefresh.setOnRefreshListener { 
            loadWebSiteSource(true)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycle_view_source_news)
        recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        dialog = SpotsDialog.Builder().setContext(this).build()
        loadWebSiteSource(false)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_search, menu)
//        val search = menu!!.findItem(R.id.appSearchBar)
//        val searchView = search.actionView as SearchView
//        searchView.queryHint = "Search"
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                mService.getSourceSearch(Common.getSourceSearch(query!!))
//                    .enqueue(object : retrofit2.Callback<WebSite> {
//                        @SuppressLint("NotifyDataSetChanged")
//                        override fun onResponse(call: Call<WebSite>, response: Response<WebSite>) {
//                            val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
//                            val recyclerView = findViewById<RecyclerView>(R.id.recycle_view_source_news)
//                            swipeRefresh.isRefreshing = true
//                            adapter = Adapter(response.body()!!, baseContext)
//                            adapter.notifyDataSetChanged()
//                            recyclerView.adapter = adapter
//                            swipeRefresh.isRefreshing = false
//                        }
//
//                        override fun onFailure(call: Call<WebSite>, t: Throwable) {
//                            TODO("Not yet implemented")
//                        }
//
//
//                    })
//                return false
//            }
//            override fun onQueryTextChange(newText: String?): Boolean {
//
//                return true
//            }
//        })
//        return super.onCreateOptionsMenu(menu)
//    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadWebSiteSource(isRefresh: Boolean) {
        val recyclerView = findViewById<RecyclerView>(R.id.recycle_view_source_news)
        if (!isRefresh) {
            val cache = Paper.book().read<String>("cache")
            if (cache != null && !cache.isBlank() && cache != "null") {
                val webSite = Gson().fromJson<WebSite>(cache, WebSite::class.java)
                adapter = Adapter(webSite, this)
                adapter.notifyDataSetChanged()
                recyclerView.adapter = adapter
            } else {
                dialog.show()
                    mService.sources.enqueue(object : retrofit2.Callback<WebSite> {
                        override fun onResponse(call: Call<WebSite>, response: Response<WebSite>) {
                            adapter = Adapter(response.body()!!, baseContext)
                            adapter.notifyDataSetChanged()
                            recyclerView.adapter = adapter
                            Paper.book().write("cache", Gson().toJson(response.body()!!))
                            dialog.dismiss()
                        }

                        override fun onFailure(call: Call<WebSite>, t: Throwable) {
                            Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
                        }

                    })
            }
        } else {
            val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
            swipeRefresh.isRefreshing = true
            mService.sources.enqueue(object : retrofit2.Callback<WebSite>{
                override fun onResponse(call: Call<WebSite>, response: Response<WebSite>) {
                    adapter = Adapter(response.body()!!, baseContext)
                    adapter.notifyDataSetChanged()
                    recyclerView.adapter = adapter
                    Paper.book().write("cache", Gson().toJson(response.body()!!))
                    swipeRefresh.isRefreshing = false
                }

                override fun onFailure(call: Call<WebSite>, t: Throwable) {
                    Toast.makeText(baseContext, "Failure", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }
}