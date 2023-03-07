package com.example.masd_2.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.masd_2.Common.ISO8601Parser
import com.example.masd_2.Model.Article
import com.example.masd_2.NewsDetail
import com.example.masd_2.R
import com.github.curioustechizen.ago.RelativeTimeTextView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.ParseException
import java.util.Date

class ListNewsAdapter(articleList: List<Article>, contextMain: Context) : RecyclerView.Adapter<ListNewsAdapter.ListNewsViewHolder>() {

    var articleList = articleList
    var context = contextMain

    class ListNewsViewHolder(itemView: View, contextMain: Context) : RecyclerView.ViewHolder(itemView) {

        val context = contextMain
        var article_title = itemView.findViewById<TextView>(R.id.article_title)
        var article_time = itemView.findViewById<RelativeTimeTextView>(R.id.article_time)
        var article_image = itemView.findViewById<CircleImageView>(R.id.article_image)

        fun setData(url: String) {
            itemView.setOnClickListener() {
                val detail = Intent(context, NewsDetail::class.java)
                detail.putExtra("webUrl", url)
                detail.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(detail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListNewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ListNewsViewHolder(inflater.inflate(R.layout.news_layout, parent, false), context)
    }

    override fun onBindViewHolder(holder: ListNewsViewHolder, position: Int) {
        Picasso.with(context).load(articleList[position].urlToImage).into(holder.article_image)
        if (articleList[position].title!!.length > 65) {
            holder.article_title.text = articleList[position].title!!.substring(0, 65) + "..."
        } else {
            holder.article_title.text = articleList[position].title!!
        }

        if (articleList[position].publishedAt != null) {
            var date: Date?=null
            try{
                date = ISO8601Parser.parse(articleList[position].publishedAt!!)
            } catch (ex: ParseException) {
                ex.printStackTrace()
            }
            if (date != null)
                holder.article_time.setReferenceTime(date!!.time)
            else
                holder.article_time.text = ""
            holder.setData(articleList[position].url!!)
        }
    }

    override fun getItemCount(): Int {
        return articleList.size
    }
}