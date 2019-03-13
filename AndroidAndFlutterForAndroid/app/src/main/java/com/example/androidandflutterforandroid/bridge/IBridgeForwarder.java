package com.example.androidandflutterforandroid.bridge;


import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public interface IBridgeForwarder {
    void receiveMessageFromJs(MethodCall methodCall, MethodChannel.Result result);

    void sendMessageToJs(String dataFromNativeString, MethodChannel.Result result);
}
