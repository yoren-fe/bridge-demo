package com.example.androidandflutterforandroid.bridge;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;

import com.example.androidandflutterforandroid.task.GetLocationTask;

import org.json.JSONException;
import org.json.JSONObject;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class TaskDistributor {
    private Context context;
    public BridgeForwarderImpl bridgeForwarder;
    public JSONObject dataJSON;
    public MethodChannel.Result result;
    public JSONObject responseJSON = new JSONObject();
    public MethodCall methodCall;

    public TaskDistributor(Context context, BridgeForwarderImpl bridgeForwarder, MethodCall methodCall, MethodChannel.Result result) {
        try {
            this.context = context;
            this.bridgeForwarder = bridgeForwarder;
            this.dataJSON = new JSONObject((String) methodCall.arguments);
            this.methodCall = methodCall;
            this.result = result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void invokeTask() {
        try {
            String functionName = dataJSON.getString("functionName");
            switch (functionName) {
                case "getLocation": {
                    new Handler(Looper.getMainLooper()).post(new GetLocationTask((FragmentActivity) context, this));
                    break;
                }
            }
        } catch (Exception e) {

        }
    }
}
