package com.nhacks16.identifai

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import io.socket.client.IO

class AlertService : Service() {
    private val binder = AlertServiceBinder()
    private val socket = IO.socket("http://10.129.253.186:8080")

    inner class AlertServiceBinder : Binder() {
        fun getService(): AlertService {
            return this@AlertService
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (null == intent) {
           stopSelf()
        }

        socket.connect()

        socket.on("alert recv", { args ->
            print(args)
        })

        return Service.START_STICKY
    }
}

