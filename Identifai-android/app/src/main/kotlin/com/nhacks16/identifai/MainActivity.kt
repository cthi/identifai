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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import org.jetbrains.anko.recyclerview.v7.recyclerView
import java.util.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView by Delegates.notNull<RecyclerView>()
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
    private val arr = ArrayList<Alert>()
    private var state = arrayOf(0, 1, 2)
    private var people = true
    private var dog = true
    private var cat = true

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, AlertService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recyclerView = recyclerView()

        adapter = AlertAdapter(this, arr)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        AlertBus.observable().subscribe { alert ->
            runOnUiThread {
                var shouldAdd = true;

                if (people && cat && dog) {
                    arr.add(0, alert)
                } else {
                    for (i in 0..2) {
                        if (i == 0 && people) {
                            shouldAdd = shouldAdd && alert.type.contains("Person")
                        } else if (i == 1 && cat) {
                            shouldAdd = shouldAdd && alert.type.contains("Cat")
                        } else if (i == 2 && dog) {
                            shouldAdd = shouldAdd && alert.type.contains("Dog")
                        }
                    }

                    if (shouldAdd) {
                        arr.add(0, alert)
                    }
                }

                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (serviceIsBound) {
            unbindService(connection)
            serviceIsBound = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        MenuInflater(this).inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.categories -> showCategoryDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCategoryDialog() {
        MaterialDialog.Builder(this)
                .title(R.string.categories)
                .items(R.array.categories)
                .positiveText(R.string.Ok)
                .itemsCallbackMultiChoice(state, { materialDialog, ints, arrayOfCharSequences ->
                    handleFilters(ints)
                })
                .show();
    }

    private fun handleFilters(arr : Array<Int>) : Boolean {
        state = arr
        people = false
        dog = false
        cat = false

        state.forEach({
            when (it) {
                0 -> people = true
                1 -> cat = true
                2 -> dog = true
            }
        })
        println(people)
        println(cat)
        println(dog)
        return true;
    }
}
