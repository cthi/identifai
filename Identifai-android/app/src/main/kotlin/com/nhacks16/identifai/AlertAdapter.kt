package com.nhacks16.identifai

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class AlertAdapter(val context: Context, val alerts: List<Alert>) : RecyclerView.Adapter<AlertHolder>() {
    override fun onBindViewHolder(holder: AlertHolder?, position: Int) {
        holder?.date?.text = alerts[position].time
        holder?.event?.text = "Detected a potential " + alerts[position].type + "."
        Glide.with(context).load(alerts[position].url).into(holder?.image)
    }

    override fun getItemCount(): Int {
        return alerts.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, p1: Int): AlertHolder? {
        return AlertHolder(LayoutInflater.from(parent?.context).inflate(R.layout.alert_item, parent, false));
    }
}

class AlertHolder : RecyclerView.ViewHolder {
    var image: ImageView
    var date: TextView
    val event: TextView

    constructor(view: View) : super(view) {
        image = view.findViewById(R.id.alert_image) as ImageView
        date = view.findViewById(R.id.alert_date) as TextView
        event = view.findViewById(R.id.alert_event) as TextView
    }
}
