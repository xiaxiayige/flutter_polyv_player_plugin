import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

/**
 * 保利威直播View
 */
class PolyvPlayerLiveView extends StatefulWidget {
  String channelId;
  String uId;
  String userName;

  PolyvPlayerLiveView({@required this.channelId,@required this.uId,@required this.userName});

  @override
  _PolyvPlayerLiveViewState createState() => _PolyvPlayerLiveViewState();
}

class _PolyvPlayerLiveViewState extends State<PolyvPlayerLiveView> {
  final String viewType = 'PolyvLiveView';
  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.black,
      child: AndroidView(
        viewType: viewType,
        layoutDirection: TextDirection.ltr,
        creationParams: {"channelId":"${widget.channelId}","uId":"${widget.uId}","userName":"${widget.userName}"},
        creationParamsCodec: StandardMessageCodec(),
      ),
    );;
  }
}
