
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class PolyvPlayerView extends StatefulWidget {
  String vid;
  String playUrl;
  PolyvPlayerView({@required this.vid, @required this.playUrl});

  @override
  _PolyvPlayerViewState createState() => _PolyvPlayerViewState();
}

class _PolyvPlayerViewState extends State<PolyvPlayerView> {
  final String viewType = 'PolyvView';

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.black,
      child: Platform.isAndroid? AndroidView(
        viewType: viewType,
        layoutDirection: TextDirection.ltr,
        creationParams: {"mp4Url":widget.playUrl,"vid":widget.vid},
        creationParamsCodec: const StandardMessageCodec(),
      ):UiKitView(
        viewType: viewType,
        layoutDirection: TextDirection.ltr,
        creationParams: {"mp4Url":widget.playUrl,"vid":widget.vid},
        creationParamsCodec: const StandardMessageCodec(),
      ),
    );
  }
}
