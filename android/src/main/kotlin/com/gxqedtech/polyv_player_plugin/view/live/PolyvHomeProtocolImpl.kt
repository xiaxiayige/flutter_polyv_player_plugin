package com.gxqedtech.polyv_player_plugin.view.live

import android.view.ViewGroup
import com.easefun.polyv.businesssdk.api.common.player.microplayer.PolyvCommonVideoView
import com.easefun.polyv.cloudclass.chat.PolyvChatManager
import com.easefun.polyv.cloudclass.playback.video.PolyvPlaybackVideoView
import com.easefun.polyv.cloudclass.video.PolyvCloudClassVideoView
import com.easefun.polyv.cloudclassdemo.watch.IPolyvHomeProtocol
import com.easefun.polyv.cloudclassdemo.watch.player.live.PolyvCloudClassVideoHelper

class PolyvHomeProtocolImpl(val livePlayerHelper: PolyvCloudClassVideoHelper):IPolyvHomeProtocol {
    override fun isSelectedChat()=false

    override fun isSelectedQuiz()=false

    override fun getSessionId(): String {
        if (videoView is PolyvCloudClassVideoView && videoView.modleVO != null) {
            return (videoView as PolyvCloudClassVideoView).modleVO.channelSessionId
        } else if (videoView is PolyvPlaybackVideoView && videoView.modleVO != null) {
            return (videoView as PolyvPlaybackVideoView).modleVO.channelSessionId
        }
        return ""
    }

    override fun addUnreadQuiz(unreadCount: Int) {
    }

    override fun sendDanmu(content: CharSequence?) {
        livePlayerHelper.sendDanmuMessage(content)
    }

    override fun getImageViewerContainer(): ViewGroup? {
        return null
    }

    override fun updatePaintStatus(showPaint: Boolean) {
        //TODO 麦克风相关
//        if (linkMicParent != null) {
//            linkMicParent.hideBrushColor(showPaint);
//        }
    }

    override fun moveChatLocation(downChat: Boolean) {
    }

    override fun addUnreadChat(unreadCount: Int) {
    }

    override fun getChatManager(): PolyvChatManager? {
        return null
    }

    override fun getVideoView(): PolyvCommonVideoView<*, *> {
        return livePlayerHelper.getVideoView()
    }

    override fun getChatEditContainer(): ViewGroup? {
        return null
    }
}