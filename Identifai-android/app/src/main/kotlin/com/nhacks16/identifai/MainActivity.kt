package com.nhacks16.identifai

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import org.jetbrains.anko.recyclerview.v7.recyclerView
import java.util.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private var recyclerView : RecyclerView by Delegates.notNull<RecyclerView>()
    private var service: AlertService by Delegates.notNull<AlertService>()
    private var serviceIsBound = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val alertBinder = binder as AlertService.AlertServiceBinder
            service = alertBinder.getService()
            serviceIsBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceIsBound = false
        }
    }
    private var adapter: AlertAdapter by Delegates.notNull<AlertAdapter>();
    override fun onStart() {
        super.onStart()

        val intent = Intent(this, AlertService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recyclerView = recyclerView() {
        }
        val arr = ArrayList<Alert>()
        arr.add(Alert("March 12th 2016, 9:32:43 am", "http://i.imgur.com/QmvfS2v.jpg", "person"));
        arr.add(Alert("March 12th 2016, 9:32:43 am", "http://i.imgur.com/QmvfS2v.jpg", "person"));
        arr.add(Alert("March 12th 2016, 9:32:43 am", "http://i.imgur.com/QmvfS2v.jpg", "person"));
        arr.add(Alert("March 12th 2016, 9:32:43 am", "http://i.imgur.com/QmvfS2v.jpg", "person"));
        arr.add(Alert("March 12th 2016, 9:32:43 am", "http://i.imgur.com/QmvfS2v.jpg", "person"));
        arr.add(Alert("March 12th 2016, 9:32:43 am", "http://i.imgur.com/QmvfS2v.jpg", "person"));
        adapter = AlertAdapter(this, arr)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()

        if (serviceIsBound) {
            unbindService(connection)
            serviceIsBound = false
        }
    }
}
