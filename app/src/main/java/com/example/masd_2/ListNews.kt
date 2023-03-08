package com.example.masd_2

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.masd_2.Adapter.Adapter
import com.example.masd_2.Adapter.ListNewsAdapter
import com.example.masd_2.Common.Common
import com.example.masd_2.Interface.NewsService
import com.example.masd_2.Model.Article
import com.example.masd_2.Model.News
import com.example.masd_2.Model.WebSite
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

        val noElements = findViewById<TextView>(R.id.textViewNoElements)
        noElements.visibility = View.GONE
            mService = Common.newsService
            dialog = SpotsDialog.Builder().setContext(this).build()
            val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh)
            swipeRefresh.setOnRefreshListener {
                loadNews(source, true)
            }

            val dialogLayout = findViewById<DiagonalLayout>(R.id.diagonalLayout)
            dialogLayout.setOnClickListener() {
                val detail = Intent(baseContext, NewsDetail::class.java)
                detail.putExtra("webUrl", webHostUrl)
                baseContext.startActivity(detail)
            }

            val recyclerView = findViewById<RecyclerView>(R.id.list_news)
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(this)

            if (intent != null) {
                val title = findViewById<TextView>(R.id.action_bar_title)
                title.text = intent.getStringExtra("theme")!!
                val ic_back = findViewById<ImageView>(R.id.ivBack)
                ic_back.setOnClickListener() {
                    finish()
                }
                source = intent.getStringExtra("source")!!
                if (!source.isEmpty()) {
                    loadNews(source, false)
                }
            }

        val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query?.length == 0)
                    loadNews(source, false)
                else
                    loadNews(source + "&q=" + query!!, false)
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {

                return true
            }
        })
        val closeButton = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        closeButton.setOnClickListener() {
            searchView.setQuery("", false)
            val noElements = findViewById<TextView>(R.id.textViewNoElements)
            noElements.visibility = View.GONE
            loadNews(source, false)
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
                        val diagonal = findViewById<DiagonalLayout>(R.id.diagonalLayout)
                        if (response.isSuccessful && response.body()!!.articles!!.size == 0) {
                            val noElements = findViewById<TextView>(R.id.textViewNoElements)
                            dialog.dismiss()
                            noElements.visibility = View.VISIBLE
                            adapter = ListNewsAdapter(listOf(), baseContext)
                            adapter.notifyDataSetChanged()
                            list_news.adapter = adapter
                            diagonal.visibility = View.GONE
                            return
                        }
                        dialog.dismiss()
                        diagonal.visibility = View.VISIBLE
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
                        val diagonal = findViewById<DiagonalLayout>(R.id.diagonalLayout)
                        if (response.isSuccessful && response.body()!!.articles!!.size == 0) {
                            swipeRefresh.isRefreshing = false
                            val noElements = findViewById<TextView>(R.id.textViewNoElements)
                            noElements.visibility = View.VISIBLE
                            adapter = ListNewsAdapter(listOf(), baseContext)
                            adapter.notifyDataSetChanged()
                            list_news.adapter = adapter
                            diagonal.visibility = View.GONE
                            return
                        }
                        swipeRefresh.isRefreshing = false
                        diagonal.visibility = View.VISIBLE
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
        }
    }
}
