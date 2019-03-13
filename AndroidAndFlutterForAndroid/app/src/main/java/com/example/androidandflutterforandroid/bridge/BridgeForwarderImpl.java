package com.example.androidandflutterforandroid.bridge;

import android.content.Context;
import android.util.Log;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;


public class BridgeForwarderImpl implements IBridgeForwarder {
    private Context context;

    public BridgeForwarderImpl(Context context) {
        this.context = context;
    }

    @Override
    public void receiveMessageFromJs(MethodCall methodCall, MethodChannel.Result result) {
        try {
            new TaskDistributor(context, this, methodCall, result).invokeTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessageToJs(String dataFromNativeString, MethodChannel.Result result) {
        Log.e("sendMessageToJs", dataFromNativeString);
        if (result != null) {
            result.success(dataFromNativeString);
        }
    }
}
