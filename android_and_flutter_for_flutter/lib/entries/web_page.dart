import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:webview_flutter/webview_flutter.dart';

class WebPage extends StatelessWidget {
  static const String _HANDLER_NAME = 'srsBridge';
  static const MethodChannel _methodChannel = MethodChannel('test.channel');

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      body: WebView(
        initialUrl: 'file:///android_asset/index.html',
        javascriptMode: JavascriptMode.unrestricted,
        onWebViewCreated: (controller) {
          controller.registerHandler(_HANDLER_NAME);
          controller.setMethodCallHandler(onJsBridgeCall);
        },
      ),
    );
  }

  Future<dynamic> onJsBridgeCall(MethodCall call) async {
    debugPrint('call: $call');
    String arguments = call.arguments;
    if (arguments.isNotEmpty) {
      return Future.error('Arguments is empty');
      // return _methodChannel.invokeMethod('callNative', arguments);
    } else {
      return Future.error('Arguments is empty');
    }
  }
}
