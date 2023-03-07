package com.example.masd_2

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.masd_2.Adapter.ListNewsAdapter
import com.example.masd_2.Common.Common
import com.example.masd_2.Interface.NewsService
import com.example.masd_2.Model.News
import com.flaviofaria.kenburnsview.KenBurnsView
import com.github.florent37.diagonallayout.DiagonalLayout
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListNews : AppCompatActivity() {

    var source = ""
    var webHostUrl: String?=null
    lateinit var dialog: AlertDialog
    lateinit var mService: NewsService
    lateinit var adapter: ListNewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_news)

        mService = Common.newsService
        dialog = SpotsDialog.Builder().setContext(this).build()
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh)
        swipeRefresh.setOnRefreshListener {
            loadNews(source, true)
        }

        val dialogLayout = findViewById<DiagonalLayout>(R.id.diagonalLayout)
        dialogLayout.setOnClickListener() {

        }

        val recyclerView = findViewById<RecyclerView>(R.id.list_news)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (intent != null) {
            source = intent.getStringExtra("source")!!
            if (!source.isEmpty()) {
                loadNews(source, false)
            }
        }
    }

    private fun loadNews(source: String, isRefreshed: Boolean) {
        val list_news = findViewById<RecyclerView>(R.id.list_news)
        val top_image = findViewById<KenBurnsView>(R.id.top_image)
        val top_title = findViewById<TextView>(R.id.top_title)
        val top_author = findViewById<TextView>(R.id.top_author)
        if (isRefreshed) {
            dialog.show()
            mService.getNewsFromSource(Common.getNewsAPI(source))
                .enqueue(object : Callback<News> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(call: Call<News>, response: Response<News>) {
                        dialog.dismiss()
                        Picasso.with(baseContext).load(response.body()!!.articles!![0].urlToImage).into(top_image)
                        top_title.text = response.body()!!.articles!![0].title
                        top_author.text = response.body()!!.articles!![0].author
                        webHostUrl = response.body()!!.articles!![0].url

                        val removeFirstItem = response.body()!!.articles
                        removeFirstItem!!.removeAt(0)
                        adapter = ListNewsAdapter(removeFirstItem, baseContext)
                        adapter.notifyDataSetChanged()
                        list_news.adapter = adapter
                    }

                    override fun onFailure(call: Call<News>, t: Throwable) {
                        Log.e("error", t.toString())
                    }

                })
        } else {
            val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh)
            swipeRefresh.isRefreshing = true
            mService.getNewsFromSource(Common.getNewsAPI(source))
                .enqueue(object : Callback<News> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(call: Call<News>, response: Response<News>) {
                        swipeRefresh.isRefreshing = false
                        Picasso.with(baseContext).load(response.body()!!.articles!![0].urlToImage).into(top_image)
                        top_title.text = response.body()!!.articles!![0].title
                        top_author.text = response.body()!!.articles!![0].author
                        webHostUrl = response.body()!!.articles!![0].url

                        val removeFirstItem = response.body()!!.articles
                        removeFirstItem!!.removeAt(0)
                        adapter = ListNewsAdapter(removeFirstItem, baseContext)
                        adapter.notifyDataSetChanged()
                        list_news.adapter = adapter
                    }

                    override fun onFailure(call: Call<News>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                })
        }
    }
}