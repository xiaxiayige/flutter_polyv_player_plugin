
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:polyv_player_plugin/view/play_control.dart';

class PolyvPlayerControl  {
  static  const MethodChannel _channel =const MethodChannel('polyv_player_plugin');

  static MethodChannel  get channel =>_channel;

  //播放控制
  static void playControl(String controlCmd,[String value]) => _channel.invokeMethod(PlayControl.METHOD_PLAYCONTROL,{PlayControl.PLAYCONTROL_KEY:controlCmd,PlayControl.PLAYCONTROL_VALUE:value});

  //获取总的播放时常
  static Future<int> getPlayInfo(String controlCmd) async =>
      await _channel.invokeMethod(PlayControl.METHOD_PLAYCONTROL,{PlayControl.PLAYCONTROL_KEY:controlCmd});



}
