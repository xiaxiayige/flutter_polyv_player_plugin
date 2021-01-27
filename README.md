# polyv_player_plugin

polyv player 

## Getting Started

 保利威Android插件,支持点播和直播，只支持播放功能，更多功能，UI样式自己clone代码后可再定义


## 基本使用

1.点播

	SimplePlayerView(vid: 你的点播ID,playUrl:可播放的Mp4链接地址,) //如果有vid优先播放Vid的内容，否则播放playUrl的内容

2.直播

	PolyvPlayerLiveView(channelId:"",userName:"",uId:""); //channelId = 直播Id ， userName和 uId ==> sdk初始化配置需要



## 注意

目前仅支持Android版本，可自己下载修改融合到项目中