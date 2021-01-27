package com.gxqedtech.polyv_player_plugin_example

import com.gxqedtech.polyv_player_plugin.view.PolyvViewFactory
import com.gxqedtech.polyv_player_plugin.view.live.PolyvLiveViewFactory
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.lang.ref.WeakReference

class MainActivity: FlutterFragmentActivity() {

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        val methodChannel = MethodChannel(flutterEngine.getDartExecutor(), "polyv_player_plugin")
        flutterEngine.platformViewsController.registry.registerViewFactory("PolyvView", PolyvViewFactory(WeakReference(methodChannel)))
        flutterEngine.platformViewsController.registry.registerViewFactory("PolyvLiveView", PolyvLiveViewFactory(WeakReference(this)))
    }
}
