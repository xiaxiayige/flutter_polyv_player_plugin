package com.gxqedtech.polyv_player_plugin.view.live

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.easefun.polyv.commonui.utils.PolyvSingleRelayBus
import com.easefun.polyv.businesssdk.PolyvChatDomainManager
import com.easefun.polyv.businesssdk.model.chat.PolyvChatDomain
import com.easefun.polyv.businesssdk.model.video.PolyvBaseVideoParams
import com.easefun.polyv.businesssdk.model.video.PolyvCloudClassVideoParams
import com.easefun.polyv.businesssdk.service.PolyvLoginManager
import com.easefun.polyv.businesssdk.vodplayer.PolyvVodSDKClient
import com.easefun.polyv.cloudclass.chat.PolyvChatApiRequestHelper
import com.easefun.polyv.cloudclass.chat.PolyvChatManager
import com.easefun.polyv.cloudclass.config.PolyvLiveSDKClient
import com.easefun.polyv.cloudclass.model.PolyvLiveClassDetailVO
import com.easefun.polyv.cloudclass.model.PolyvLiveStatusVO
import com.easefun.polyv.cloudclass.net.PolyvApiManager
import com.easefun.polyv.cloudclassdemo.watch.linkMic.widget.PolyvLinkMicParent
import com.easefun.polyv.cloudclassdemo.watch.player.live.PolyvCloudClassMediaController
import com.easefun.polyv.cloudclassdemo.watch.player.live.PolyvCloudClassVideoHelper
import com.easefun.polyv.cloudclassdemo.watch.player.live.PolyvCloudClassVideoItem
import com.easefun.polyv.commonui.player.ppt.PolyvPPTItem
import com.easefun.polyv.commonui.widget.PolyvTouchContainerView
import com.easefun.polyv.foundationsdk.config.PolyvPlayOption
import com.easefun.polyv.foundationsdk.log.PolyvCommonLog
import com.easefun.polyv.foundationsdk.net.PolyvResponseBean
import com.easefun.polyv.foundationsdk.net.PolyvResponseExcutor
import com.easefun.polyv.foundationsdk.net.PolyvrResponseCallback
import com.easefun.polyv.foundationsdk.utils.PolyvScreenUtils
import com.easefun.polyv.linkmic.PolyvLinkMicClient
import com.easefun.polyv.thirdpart.blankj.utilcode.util.ScreenUtils
import com.easefun.polyv.thirdpart.blankj.utilcode.util.ToastUtils
import com.gxqedtech.polyv_player_plugin.PolyvConfig
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import retrofit2.HttpException
import java.io.IOException
import java.lang.ref.WeakReference

//保利威直播课堂
class PolyvLiveRoomView(val context: Context, id: Int, val creationParams: Map<String, String>,val activity:WeakReference<Activity>)
    :PlatformView{

   lateinit var cloudClassVideoItem : PolyvCloudClassVideoItem
   lateinit var container :FrameLayout

    private var getTokenDisposable: Disposable? = null
    private  var verifyDispose:Disposable? = null
    private  var requestLiveClassDetailDispose:Disposable? = null
    private var channelId:String=""
    private var uId=""
    private var userName=""

    //是否是普通直播(是否有PPT)
    private var isNormalLive=false
    //直播类型
    private var playMode = PolyvPlayOption.PLAYMODE_LIVE
    //是否是参与者
    private var isParticipant = false



    override fun getView(): View? {
        return container
    }

    init {
        container = FrameLayout(context)
        initPlayerView()
    }



    private fun initPlayerView() {
        channelId =  creationParams["channelId"] as String
        uId=creationParams["uId"] as String
        userName=creationParams["userName"] as String
        _checkLogin()
    }

    private fun _checkLogin() {
        getTokenDisposable =  PolyvLoginManager.checkLoginToken(PolyvConfig.USER_ID,PolyvConfig.APP_SECRET,PolyvConfig.APP_ID,"$channelId","",
                object : PolyvrResponseCallback<PolyvChatDomain>() {
                    override fun onSuccess(p0: PolyvChatDomain) {
                        PolyvLinkMicClient.getInstance().setAppIdSecret(PolyvConfig.APP_ID, PolyvConfig.APP_SECRET)
                        PolyvLiveSDKClient.getInstance().setAppIdSecret(PolyvConfig.APP_ID, PolyvConfig.APP_SECRET)
                        PolyvVodSDKClient.getInstance().initConfig(PolyvConfig.APP_ID, PolyvConfig.APP_SECRET)
                        requestLiveStatus(channelId)
                        PolyvChatDomainManager.getInstance().setChatDomain(p0)
                        PolyvScreenUtils.lockOrientation()
                    }
                })
    }


    private fun requestLiveStatus(channelId: String) {
        verifyDispose = PolyvResponseExcutor.excuteUndefinData(PolyvApiManager.getPolyvLiveStatusApi()
                .geLiveStatusJson(channelId), object : PolyvrResponseCallback<PolyvLiveStatusVO<*>?>() {
            override fun onSuccess(statusVO: PolyvLiveStatusVO<*>?) {
                val data: String = statusVO?.getData()?:""
                val dataArr = data.split(",").toTypedArray()
                val isAlone = "alone" == dataArr[1] //是否有ppt
                initialParams(isAlone)
            }

            override fun onError(p0: Throwable?) {
                super.onError(p0)
                errorStatus(p0)
            }

            override fun onFailure(p0: PolyvResponseBean<PolyvLiveStatusVO<*>?>?) {
                super.onFailure(p0)
                failedStatus(p0?.getMessage())
            }

        })
    }




    private fun initialParams(alone: Boolean) {
        isNormalLive = alone
        playMode = PolyvPlayOption.PLAYMODE_LIVE
        isParticipant = false
        initialStudentIdAndNickName()
        initial()
        requestLiveClassDetailApi()
    }

    private fun initial() {
        val ratio = 9.0f / 16 //播放器使用16:9比例
        if(container is Activity){
            PolyvScreenUtils.generateHeightByRatio(container as Activity, ratio)
        }
//        initialLinkMic()
        initialPPT()
        initialVideo()
//        initialOretation()
    }



    private fun initialVideo() {
        val vlp: ViewGroup.LayoutParams = container.getLayoutParams()
        vlp.width = ViewGroup.LayoutParams.MATCH_PARENT
        vlp.height = ViewGroup.LayoutParams.MATCH_PARENT
        initialLiveVideo()
    }

    //直播与点播辅助类
    private var livePlayerHelper: PolyvCloudClassVideoHelper? = null
    private fun initialLiveVideo() {
        if(activity.get()!=null){
            cloudClassVideoItem = PolyvCloudClassVideoItem(activity.get()!!)
            livePlayerHelper = PolyvCloudClassVideoHelper(cloudClassVideoItem,
                    if (isNormalLive) null else PolyvPPTItem<PolyvCloudClassMediaController>(context),
                    PolyvChatManager.getInstance(), channelId)
            livePlayerHelper!!.addVideoPlayer(container)
            livePlayerHelper!!.initConfig(isNormalLive)
            livePlayerHelper!!.addPPT(videoPptContainer)
            //TODO 连麦
            //livePlayerHelper.addLinkMicLayout(linkMicParent);
            val cloudClassVideoParams = PolyvCloudClassVideoParams(channelId, PolyvConfig.USER_ID, viewerId)
            cloudClassVideoParams.buildOptions(PolyvBaseVideoParams.WAIT_AD, true)
                    .buildOptions(PolyvBaseVideoParams.MARQUEE, true)
                    .buildOptions(PolyvBaseVideoParams.PARAMS2, userName)
            livePlayerHelper!!.startPlay(cloudClassVideoParams)
            livePlayerHelper!!.addHomeProtocol(PolyvHomeProtocolImpl(livePlayerHelper!!))

            // TODO 连麦相关
//        if (linkMicParent != null) {
////            linkMicParent!!.addClassHelper(livePlayerHelper)
////        }
////        if (isParticipant) {
////            livePlayerHelper!!.joinLink(true)
////       }
        }


    }


    //初始化PPt相关
    private  var videoPptContainer: PolyvTouchContainerView?=null
    private fun initialPPT() {
        videoPptContainer =PolyvTouchContainerView(context)

        videoPptContainer!!.setOriginLeft(ScreenUtils.getScreenWidth() - PolyvScreenUtils.dip2px(activity.get(), 144f))

        videoPptContainer?.viewTreeObserver?.addOnGlobalLayoutListener aa@{
            val rlp: ViewGroup.MarginLayoutParams? = getLayoutParamsLayout(videoPptContainer!!)
            if (rlp != null) {
                val isLive = playMode == PolyvPlayOption.PLAYMODE_LIVE
                rlp.leftMargin = if (isLive) 0 else (videoPptContainer!!.parent as View).measuredWidth - videoPptContainer!!.measuredWidth
                if (container.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    rlp.topMargin = 0
                    videoPptContainer!!.setContainerMove(true)
                } else { // 若初始为竖屏
                    rlp.topMargin = container.getBottom()
                    videoPptContainer!!.setContainerMove(!isLive)
                }
                videoPptContainer!!.setOriginTop(rlp!!.topMargin)
                videoPptContainer!!.layoutParams = rlp
            }
        }
    }

    private fun getLayoutParamsLayout(layout: View): ViewGroup.MarginLayoutParams? {
        var rlp: ViewGroup.MarginLayoutParams? = null
        if (layout.parent is RelativeLayout) {
            rlp = layout.layoutParams as RelativeLayout.LayoutParams
        } else if (layout.parent is LinearLayout) {
            rlp = layout.layoutParams as LinearLayout.LayoutParams
        } else if (layout.parent is FrameLayout) {
            rlp = layout.layoutParams as FrameLayout.LayoutParams
        }
        return rlp
    }


    private var linkMicParent: PolyvLinkMicParent? = null

    // TODO 连麦相关操作
    private fun initialLinkMic() {

    }

    private fun requestLiveClassDetailApi() {
        requestLiveClassDetailDispose = PolyvChatApiRequestHelper.getInstance()
                .requestLiveClassDetailApi(channelId)
                .subscribe(object : Consumer<PolyvLiveClassDetailVO?> {
                    @Throws(Exception::class)
                    override fun accept(polyvLiveClassDetailVO: PolyvLiveClassDetailVO?) {
                        val isLive = polyvLiveClassDetailVO?.data?.watchStatus == "live"
                        if (!isLive) {
                            val startTime = polyvLiveClassDetailVO?.data?.startTime
                            if (startTime != null && cloudClassVideoItem != null) {
                                cloudClassVideoItem.startLiveTimeCountDown(startTime.toString())
                            }
                        }
                    }
                })
    }

    private var viewerId=""
    private var viewerName=""

    private fun initialStudentIdAndNickName() {
            viewerId = "" + uId
            viewerName = "" + userName
    }

    fun errorStatus(e: Throwable?) {
        e?.printStackTrace()
        PolyvCommonLog.exception(e)
        if (e is HttpException) {
            try {
                ToastUtils.showLong(e.response().errorBody()!!.string())
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
        } else {
            ToastUtils.showLong("${e?.message}")
        }
    }

    fun failedStatus(message: String?) {
        ToastUtils.setGravity(Gravity.CENTER, 0, 0)
        ToastUtils.showLong(message)
    }


    override fun dispose() {
        getTokenDisposable?.dispose()
        getTokenDisposable?.dispose()
        requestLiveClassDetailDispose?.dispose()
        livePlayerHelper?.destory()
        PolyvSingleRelayBus.clear()
    }

}