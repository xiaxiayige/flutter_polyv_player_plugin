package com.gxqedtech.polyv_player_plugin

import android.util.Log
import androidx.annotation.NonNull;
import com.gxqedtech.polyv_player_plugin.view.PolyvViewControl
import com.gxqedtech.polyv_player_plugin.view.PolyvViewFactory
import com.gxqedtech.polyv_player_plugin.view.live.PolyvLiveViewFactory

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.lang.ref.WeakReference

/** PolyvPlayerPlugin */
public class PolyvPlayerPlugin: FlutterPlugin, MethodCallHandler,ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var polyvViewFactory : PolyvViewFactory
  private lateinit var polyvLiveViewFactory : PolyvLiveViewFactory
  private var polyvViewControl: PolyvViewControl?=null
  private var mFlutterPluginBinding:FlutterPlugin.FlutterPluginBinding?=null
  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    Log.i("AAA","3333333333333333333333333")
    mFlutterPluginBinding=flutterPluginBinding
    channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "polyv_player_plugin")
    channel.setMethodCallHandler(this)
    polyvViewFactory= PolyvViewFactory(WeakReference(channel))
    flutterPluginBinding .platformViewRegistry.registerViewFactory("PolyvView", polyvViewFactory)
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "polyv_player_plugin")
      channel.setMethodCallHandler(PolyvPlayerPlugin())
    }
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "playControl") {
      polyvViewControl = PolyvViewControl(WeakReference(polyvViewFactory.polyvPlayerView))
//      Log.d("aaa","=========================> $polyvViewControl , ${call.arguments}")
      val callResult = polyvViewControl?.control(call.arguments as Map<String, Any>)
      if(callResult!=null){
        result.success(callResult)
      }
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onDetachedFromActivity() {

  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {

  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    polyvLiveViewFactory= PolyvLiveViewFactory(WeakReference(binding.activity))
    mFlutterPluginBinding?.platformViewRegistry?.registerViewFactory("PolyvLiveView", polyvLiveViewFactory)
  }

  override fun onDetachedFromActivityForConfigChanges() {

  }
}
