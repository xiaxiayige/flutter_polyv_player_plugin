package com.gxqedtech.polyv_player_plugin.view

import android.util.Log
import java.lang.ref.WeakReference
import java.util.*


class PolyvViewControl(val polyvView: WeakReference<PolyvPlayerView>){
    private  val TAG = "PolyvViewControl"
    companion object{
        val CONTROL_KEY="key" //
        val CONTROL_VALUE="value" //
        val CONTROL_PLAY="play" //播放
        val CONTROL_STOP="stop" //停止
        val CONTROL_PAUSE="pause" //暂停
        val CONTROL_SEEKTO="seekTo" //跳转
        val CONTROL_DURATION="duration" //总时长
        val CONTROL_CURRENTPOSITION="currentPosition" //当前时长

        val VIDEO_STATUS_ONPREPARED="onPrepared" //视频状态
    }

    private fun getDuration() =polyvView.get()?.duration()

    private fun currentPosition() =polyvView.get()?.currentPosition()

    //播放
    private fun play(){
        polyvView.get()?.play()
    }

    //暂停播放
    private fun pause(){
        polyvView.get()?.pause()
    }

    //暂停播放
    private fun stop(){
        polyvView.get()?.stop()
    }

    private  fun seekToPosition(position:Int){
        polyvView.get()?.seekTo(position)
    }

    fun control(arguments: Map<String,Any>):Any? {
        Log.d(TAG,arguments.toString())
        when(arguments[CONTROL_KEY] ){
            CONTROL_PLAY->play()
            CONTROL_PAUSE->pause()
            CONTROL_STOP->stop()
            CONTROL_SEEKTO->{
                val seekToPosition = arguments[CONTROL_VALUE]
                seekToPosition(seekToPosition.toString().toInt())
            }
            CONTROL_DURATION-> return getDuration()?:0
            CONTROL_CURRENTPOSITION-> return currentPosition()?:0
        }
        return null
    }


}