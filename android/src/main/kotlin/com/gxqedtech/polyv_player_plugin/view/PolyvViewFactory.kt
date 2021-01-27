package com.gxqedtech.polyv_player_plugin.view

import android.content.Context
import android.view.View
import android.widget.TextView
import io.flutter.plugin.common.BinaryCodec
import io.flutter.plugin.common.MessageCodec
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import java.lang.ref.WeakReference

class PolyvViewFactory(val methodChannel: WeakReference<MethodChannel>?): PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    lateinit var polyvPlayerView:PolyvPlayerView

    override fun create(context: Context, viewId: Int, args: Any): PlatformView {
        val creationParams = args as Map<String, String>
        polyvPlayerView = PolyvPlayerView(context, viewId, creationParams,methodChannel)
        return polyvPlayerView
    }
}