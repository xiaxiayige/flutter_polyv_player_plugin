import 'package:flutter/material.dart';
import 'package:polyv_player_plugin/view/widget_polyvplayer_live_view.dart';
import 'package:polyv_player_plugin/view/widget_simple_playerview.dart';
void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: SafeArea(
        child: Scaffold(
          body: Container(
            child: SimplePlayerView(vid: "e2c61892130b599fb6c57a3be8623a2a_e",playUrl: "",),
            // child: PolyvPlayerLiveView(channelId: "2009565", userName: "cqlsir", uId: "1",),
          )
        ),
      ),
    );
  }
}
