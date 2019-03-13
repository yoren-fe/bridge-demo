package com.example.androidandflutterforandroid;

import android.os.Bundle;
import android.util.Log;

import com.example.androidandflutterforandroid.bridge.BridgeForwarderImpl;

import io.flutter.app.FlutterFragmentActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;
import timber.log.Timber;

public class SecondActivity extends FlutterFragmentActivity implements MethodChannel.MethodCallHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        MethodChannel channel = new MethodChannel(getFlutterView(), "test.channel");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        Log.e("SecondActivity", methodCall.method);
        switch (methodCall.method) {
            case "callNative":
                new BridgeForwarderImpl(this).receiveMessageFromJs(methodCall, result);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getFlutterView().destroy();
    }
}
