import 'dart:async';

import 'package:flutter/material.dart';
import 'package:polyv_player_plugin/polyv_player_plugin.dart';
import 'package:polyv_player_plugin/view/play_control.dart';
import 'package:seekbar/seekbar.dart';

//底部控制播放进度View
class PlayControlView extends StatefulWidget {
  bool isLive=false;

  PlayControlView({this.isLive});

  @override
  _PlayControlViewState createState() => _PlayControlViewState();
}

class _PlayControlViewState extends State<PlayControlView> {

  static int PLAY_ING=1; //播放中
  static int PLAY_PAUSE=2; //暂停播放
  static int PLAY_STOP=2; //播放结束

  int playStaus=PLAY_ING;

  String durationTime;
  String currentPosition;
  int duration=0;
  double seekBarProgress=0;
  double seekBarValue=0;

  bool controlViewIsHide = true;
  Timer _playerControlTimer;
  @override
  void dispose() {
    if(_timer !=null){
      _timer.cancel();
      _timer=null;
    }
    if(_playerControlTimer!=null){
      _playerControlTimer.cancel();
      _playerControlTimer=null;
    }
    super.dispose();
  }

  @override
  void initState() {
    PolyvPlayerControl.channel.setMethodCallHandler((call){
      if(call.method=="onPrepared"){ //开始视频播放
        _startPlay();
        getDuration();
      }
    });
    super.initState();
  }


  format(Duration d) => d.toString().split('.').first.padLeft(8, "0");

  Future<void> getDuration() async {
    duration = await PolyvPlayerControl.getPlayInfo(PlayControl.CONTROL_DURATION);
    final d1 = Duration(milliseconds:duration);
     setState(() {durationTime = format(d1);});
  }

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: (){
        if(controlViewIsHide){
          _hideControlView();
        }else{
          _showControlView();
        }
      },
      child: Container(
        height: double.infinity,
        width: double.infinity,
        child: Visibility(
          visible: controlViewIsHide,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Visibility(
                visible: true,
                child: InkWell(
                  onTap: (){
                    Navigator.pop(context);
                  },
                  child: SizedBox(
                    height: 50,
                    width: 50,
                    child: Icon(Icons.arrow_back_ios,color: Colors.white,),
                  ),
                ),
              ),
              if(widget.isLive==false)
                Padding(
                padding: EdgeInsets.all(16),
                child: Row(
                  children: [
                    InkWell(onTap: (){
                      if(playStaus == PLAY_ING){
                        PolyvPlayerControl.playControl(PlayControl.CONTROL_PAUSE);
                        playStaus=PLAY_PAUSE;
                        setState(() {});
                      }else{
                        PolyvPlayerControl.playControl(PlayControl.CONTROL_PLAY);
                        playStaus=PLAY_ING;
                        setState(() {});
                      }
                    }
                        ,child: Icon(playStaus ==PLAY_ING ? Icons.pause:Icons.play_arrow,size: 28,color: Colors.white,)),
                    Text("${currentPosition??"00:00:00"}",style: TextStyle(color: Colors.white,fontSize: 10),),
                    Expanded(child: Padding(padding: EdgeInsets.only(left: 8,right: 8),
                        child: SeekBar(progressColor:Theme.of(context).accentColor,
                          barColor: Colors.grey,
                          value: seekBarValue,
                          onProgressChanged: (progress){
                          _showControlView();
                            seekBarProgress = progress;
                          },
                          onStopTrackingTouch: (){
                            _seekTo(seekBarProgress);
                            _hideControlView();
                          },
                        ))),
                    Text("${durationTime??"00:00:00"}",style: TextStyle(color: Colors.white,fontSize: 10),),
                  ],
                ),
              )
            ],
          ),
        ),
      ),
    );
  }

  Timer _timer;
  int lastPosition=-1; // 上一次播放时间
  void _startPlay() {
    _timer = Timer.periodic(Duration(seconds: 1), (timer) async {
      if(playStaus==PLAY_PAUSE|| playStaus==PLAY_STOP) return ;
      int position =   await PolyvPlayerControl.getPlayInfo(PlayControl.CONTROL_CURRENTTIME);
      if(position<0) return;
      if(position>=duration){ //播放结束
        playStaus = PLAY_STOP;
        setState(() {});
        return;
      }
      if(lastPosition==position){
          //视频缓冲中
      }
      setState(() {
        lastPosition=position;
        currentPosition =format(Duration(milliseconds: position));
        seekBarValue = position/duration;
      });
    });
    _hideControlView();
  }

  void _seekTo(double seekBarProgress) {
    int toPosition=(seekBarProgress*duration).toInt();
    PolyvPlayerControl.playControl(PlayControl.CONTROL_SEEKTO,"$toPosition");
  }

  //隐藏控制view
  void _hideControlView() {
    if(_playerControlTimer!=null &&_playerControlTimer.isActive){
      _playerControlTimer.cancel();
      _playerControlTimer=null;
    }
    _playerControlTimer= Timer.periodic(Duration(seconds: 1), (timer) {
      if(timer.tick==5){
        _playerControlTimer.cancel();
        _playerControlTimer=null;
        controlViewIsHide=false;
        setState(() {});
      }
    });
  }

  void _showControlView() {
    if(_playerControlTimer!=null &&_playerControlTimer.isActive){
      _playerControlTimer.cancel();
      _playerControlTimer=null;
    }
    controlViewIsHide=true;
    setState(() {});
  }
}
