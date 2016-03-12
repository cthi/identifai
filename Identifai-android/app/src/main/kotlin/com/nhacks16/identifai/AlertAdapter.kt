package com.nhacks16.identifai

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class AlertAdapter(val context: Context, val alerts: List<Alert> ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val alertHolder = holder as AlertHolder
        alertHolder.date.text = alerts.get(position).time
        alertHolder.event.text = "Detected a potential " + alerts.get(position).type + "."
        Glide.with(context).load(alerts.get(position).url).into(alertHolder.image)
    }

    override fun getItemCount(): Int {
        return alerts.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, p1: Int): RecyclerView.ViewHolder? {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.alert_item, parent, false);
        val holder = AlertHolder(view)

        return holder
    }
}

class AlertHolder : RecyclerView.ViewHolder {
    var image : ImageView
    var date : TextView
    val event : TextView

    constructor(view: View) : super(view) {
        image = view.findViewById(R.id.alert_image) as ImageView
        date = view.findViewById(R.id.alert_date) as TextView
        event = view.findViewById(R.id.alert_event) as TextView
    }
}
