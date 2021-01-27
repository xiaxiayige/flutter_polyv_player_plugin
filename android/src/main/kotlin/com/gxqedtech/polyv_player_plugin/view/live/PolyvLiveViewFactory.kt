package com.gxqedtech.polyv_player_plugin.view.live

import android.app.Activity
import android.content.Context
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import java.lang.ref.WeakReference

//保利威直播sdk
class PolyvLiveViewFactory(val activity:WeakReference<Activity>): PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    lateinit var polyvLivePlayerView: PolyvLiveRoomView

    override fun create(context: Context, viewId: Int, args: Any): PlatformView {
        val creationParams = args as Map<String, String>
        polyvLivePlayerView = PolyvLiveRoomView(context, viewId, creationParams,activity)
        return polyvLivePlayerView
    }
}