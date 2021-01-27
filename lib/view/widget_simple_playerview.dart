import 'dart:async';

import 'package:flutter/material.dart';
import 'package:polyv_player_plugin/view/widget_play_control_view.dart';
import 'package:polyv_player_plugin/view/widget_polyvplayer_view.dart';

class SimplePlayerView extends StatefulWidget {
  String vid;
  String playUrl;

  SimplePlayerView({this.vid, this.playUrl});

  @override
  _SimplePlayerViewState createState() => _SimplePlayerViewState();
}

class _SimplePlayerViewState extends State<SimplePlayerView> {

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        PolyvPlayerView(
          vid: widget.vid,
          playUrl:widget.playUrl,
        ),
        PlayControlView()
      ],
    );
  }
}
