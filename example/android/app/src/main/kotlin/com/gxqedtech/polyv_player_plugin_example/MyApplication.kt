package com.gxqedtech.polyv_player_plugin_example

import android.content.Context
import androidx.multidex.MultiDex
import com.easefun.polyv.cloudclassdemo.PolyvCloudClassApp

class MyApplication: io.flutter.app.FlutterApplication() {
    override fun onCreate() {
        super.onCreate()
        PolyvCloudClassApp.initPolyv(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

}