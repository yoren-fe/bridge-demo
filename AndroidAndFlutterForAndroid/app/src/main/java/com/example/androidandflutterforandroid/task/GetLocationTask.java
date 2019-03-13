package com.example.androidandflutterforandroid.task;

import android.Manifest;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.androidandflutterforandroid.bridge.TaskDistributor;
import com.example.androidandflutterforandroid.util.LocationUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class GetLocationTask implements Runnable {
    private FragmentActivity activity;
    private TaskDistributor taskDistributor;

    public GetLocationTask(FragmentActivity activity, TaskDistributor taskDistributor) {
        this.activity = activity;
        this.taskDistributor = taskDistributor;
    }


    @Override
    public void run() {
        new RxPermissions(activity)
                .requestEachCombined(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Observer<Permission>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.e("onSubscribe");

                    }

                    @Override
                    public void onNext(Permission permission) {
                        Timber.e("onNext: %s", permission.granted);
                        if (permission.granted) {
                            getLocation();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("onError");

                    }

                    @Override
                    public void onComplete() {
                        Timber.e("onComplete");

                    }
                });

    }

    private void getLocation() {
        Location location = LocationUtils.getLocation(activity, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    Timber.e(location.toString());
                    taskDistributor.responseJSON.put("log", location.getLongitude());
                    taskDistributor.responseJSON.put("lat", location.getLatitude());
                    taskDistributor.result.success(taskDistributor.responseJSON.toString());
                } catch (JSONException e) {
                    taskDistributor.result.error(e.getMessage(), null, null);
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Timber.e("onStatusChanged");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Timber.e("onProviderEnabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Timber.e("onProviderDisabled");
            }
        });
        if (location != null) {
            try {
                Timber.e(location.toString());
                taskDistributor.responseJSON.put("log", location.getLongitude());
                taskDistributor.responseJSON.put("lat", location.getLatitude());
                taskDistributor.result.success(taskDistributor.responseJSON.toString());
            } catch (JSONException e) {
                taskDistributor.result.error(e.getMessage(), null, null);
                e.printStackTrace();
            }
        }
    }
}
