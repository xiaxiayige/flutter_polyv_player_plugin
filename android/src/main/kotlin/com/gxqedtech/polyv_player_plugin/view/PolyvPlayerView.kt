package com.gxqedtech.polyv_player_plugin.view

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.easefun.polyvsdk.PolyvSDKClient
import com.easefun.polyvsdk.video.PolyvVideoView
import com.easefun.polyvsdk.video.listener.IPolyvOnPreparedListener2
import com.google.android.exoplayer.util.PlayerControl
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import java.lang.ref.WeakReference

//保利威点播View
class PolyvPlayerView(context: Context, id: Int, creationParams: Map<String, String>,val methodChannel: WeakReference<MethodChannel>?):PlatformView, IPolyvOnPreparedListener2 {
    private  val TAG = "PolyvPlayerView"
    private val polyvVideoView: PolyvVideoView
    override fun getView(): View {
        return polyvVideoView
    }

    override fun dispose() {
        polyvVideoView.release()
        methodChannel?.clear()
    }

    init {
        initSdk(context)
        polyvVideoView = PolyvVideoView(context)
        polyvVideoView.setBackgroundColor(Color.BLACK)
        polyvVideoView.id= id
        polyvVideoView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        val vid = creationParams["vid"]?:""
        val  mp4Url= creationParams["mp4Url"]?:""
        if(TextUtils.isEmpty(mp4Url) && !TextUtils.isEmpty(vid)){
            polyvVideoView.setVid(vid)
        }else if(!TextUtils.isEmpty(mp4Url)){
            polyvVideoView.setVideoPath(mp4Url)
        }else{
            Log.e(TAG,"player error , mp4Url = $mp4Url , vid = $vid ")
        }
        polyvVideoView.setOnPreparedListener(this)
    }

   fun initSdk(context: Context){
       val client = PolyvSDKClient.getInstance()
       client.settingsWithConfigString(
               "",
               "",
               "")
       client.initSetting(context.applicationContext)
   }

    fun pause(){
        polyvVideoView.pause()
    }

    fun play(){
        polyvVideoView.start()
    }

    fun stop() {
        polyvVideoView.stopPlayback()
    }

    fun seekTo(position: Int) {
        polyvVideoView.seekTo(position)
    }

    //总时长
    fun duration()=polyvVideoView.duration

    //当前进度
    fun currentPosition()=polyvVideoView.currentPosition

    override fun onPrepared() {
        //开始播放
        polyvVideoView.start()
        Log.d(TAG, "onPreparedonPreparedonPreparedonPrepared: ")
        methodChannel?.get()?.invokeMethod(PolyvViewControl.VIDEO_STATUS_ONPREPARED,null)
    }
}
