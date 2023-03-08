package com.example.masd_2.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.masd_2.ListNews
import com.example.masd_2.Model.WebSite
import com.example.masd_2.R

class Adapter(webSite: WebSite, contextMain: Context) : RecyclerView.Adapter<Adapter.Holder>() {

    var context = contextMain
    var webSite = webSite

    class Holder(itemView: View, contextMain: Context) : RecyclerView.ViewHolder(itemView) {

        var context = contextMain
        var source_title = itemView.findViewById<TextView>(R.id.source_news_name)

        fun setData(webSite: WebSite) {
            itemView.setOnClickListener() {
                val intent = Intent(context, ListNews::class.java)
                intent.putExtra("source", webSite.sources!![position].id)
                intent.putExtra("theme", webSite.sources!![position].name)
                context.startActivity(intent)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        return Holder(inflater.inflate(R.layout.source_news_layout, parent, false), context)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.source_title.text = webSite.sources!![position].name
        holder.setData(webSite)
    }

    override fun getItemCount(): Int {
        return webSite.sources!!.size
    }
}
