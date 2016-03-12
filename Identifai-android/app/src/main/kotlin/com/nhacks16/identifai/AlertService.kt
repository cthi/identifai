package com.nhacks16.identifai

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONArray

class AlertService : Service() {
    private val binder = AlertServiceBinder()
    private val socket: Socket? = IO.socket("http://10.129.253.187:3000")

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

        socket?.connect()

        socket?.on(Socket.EVENT_CONNECT, { args ->
            println(args.toString())
        })

        socket?.on("New Picture", { args ->
            val arrayResponse = args[0] as JSONArray
            AlertBus.post(Alert(arrayResponse.getString(1), arrayResponse.getString(0), arrayResponse.getString(2)))
        })

        return Service.START_STICKY
    }
}
