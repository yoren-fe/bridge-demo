package com.example.androidandflutterforandroid.util

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.location.Address
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresPermission
import android.widget.Toast
import com.blankj.utilcode.util.Utils
import timber.log.Timber
import java.io.IOException
import java.util.Locale

@SuppressLint("MissingPermission")
class LocationUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    private class MyLocationListener : LocationListener {
        /**
         * This function is triggered when the coordinates change.
         * If the supplier passes the same coordinates, it will not be triggered.
         *
         * @param location coordinate
         */
        override fun onLocationChanged(location: Location) {
            mListener?.onLocationChanged(location)
        }

        /**
         * This function is triggered when the provider directly switches between the available,
         * temporarily unavailable, and no service states.
         *
         * @param provider provider
         * @param status status
         * @param extras Provider optional package
         */
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            mListener?.onStatusChanged(provider, status, extras)
            when (status) {
                LocationProvider.AVAILABLE -> Timber.d("Current GPS status is visible")
                LocationProvider.OUT_OF_SERVICE -> Timber.d("Current GPS status is out of service status")
                LocationProvider.TEMPORARILY_UNAVAILABLE -> Timber.d("The current GPS status is suspended.")
                else -> {
                }
            }
        }

        /**
         * This function is triggered when the provider is enabled, such as GPS is turned on.
         */
        override fun onProviderEnabled(provider: String) {}

        /**
         * This function is triggered when the provider is disabled, such as GPS is turned off.
         */
        override fun onProviderDisabled(provider: String) {}
    }

    interface OnLocationChangeListener {

        /**
         * Get the last reserved coordinates
         *
         * @param location coordinate
         */
        fun getLastKnownLocation(location: Location)

        /**
         * This function is triggered when the coordinates change.
         * If the Provider passes the same coordinates, it will not be triggered.
         *
         * @param location coordinate
         */
        fun onLocationChanged(location: Location)

        /**
         * This function is triggered when the provider directly switches between the available,
         * temporarily unavailable, and no service states.
         *
         * @param provider provider
         * @param status status
         * @param extras Provider optional package
         */
        fun onStatusChanged(provider: String, status: Int, extras: Bundle) // 位置状态发生改变
    }

    companion object {
        private const val TWO_MINUTES = 1000 * 60 * 2
        private const val MIN_TIME_BETWEEN_UPDATES = 1000 * 60
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES = 20f

        private var mListener: OnLocationChangeListener? = null
        private var myLocationListener: MyLocationListener? = null
        private var mLocationManager: LocationManager? = null

        /**
         * you have to check for Location Permission before use this method
         * add this code <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"></uses-permission> to your Manifest file.
         * you have also implement LocationListener and passed it to the method.
         *
         * @param context
         * @param listener
         * @return `Location`
         */

        @JvmStatic
        @SuppressLint("MissingPermission")
        fun getLocation(context: Context, listener: LocationListener): Location? {
            var location: Location? = null
            try {
                mLocationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
                if (!isLocationEnabled) {
                    // no Network and GPS providers is enabled
                    Toast.makeText(context, " you have to open GPS or INTERNET", Toast.LENGTH_LONG)
                            .show()
                } else {
                    // when GPS is enabled.
                    if (isGpsEnabled) {
                        mLocationManager!!.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BETWEEN_UPDATES.toLong(),
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                listener
                        )

                        if (mLocationManager != null) {
                            location = mLocationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (location != null) {
                                mLocationManager!!.removeUpdates(listener)
                                return location
                            }
                        }
                    }
                    if (isLocationEnabled) {
                        mLocationManager?.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BETWEEN_UPDATES.toLong(),
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                listener
                        )

                        location = mLocationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            mLocationManager!!.removeUpdates(listener)
                            return location
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return location
        }

        /**
         * Determine if Gps is available
         *
         * @return `true`: yes<br></br>`false`: no
         */
        val isGpsEnabled: Boolean
            get() {
                val lm = Utils.getApp().getSystemService(LOCATION_SERVICE) as LocationManager
                return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
            }

        /**
         * Determine if positioning is available
         *
         * @return `true`: yes<br></br>`false`: no
         */
        val isLocationEnabled: Boolean
            get() {
                val lm = Utils.getApp().getSystemService(LOCATION_SERVICE) as LocationManager
                return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
            }

        /**
         * Open the Gps settings interface
         */
        fun openGpsSettings() {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            Utils.getApp().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        /**
         * register
         *
         * Remember to call after use[.unregister]
         *
         * Need to add permissions `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * Need to add permissions `<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />`
         *
         * Need to add permissions `<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />`
         *
         * If `minDistance` is 0, it is updated periodically by `minTime`;
         *
         * `minDistance` is not 0, then `minDistance` shall prevail;
         *
         * Both are 0, so refresh at any time.
         *
         * @param minTime Location information update period (unit: milliseconds)
         * @param minDistance Position change minimum distance: When the position distance changes beyond this value,
         *                    the position information will be updated (unit: meter)
         * @param listener Location refresh callback interface
         * @return `true`: Initialization succeeded<br></br>`false`: Initialization failed
         */
        @RequiresPermission(ACCESS_FINE_LOCATION)
        fun register(minTime: Long, minDistance: Long, listener: OnLocationChangeListener?): Boolean {
            if (listener == null) {
                return false
            }
            mLocationManager = Utils.getApp().getSystemService(LOCATION_SERVICE) as LocationManager

            if (!mLocationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !mLocationManager!!.isProviderEnabled(
                            LocationManager.GPS_PROVIDER
                    )
            ) {
                return false
            }
            mListener = listener
            val provider = mLocationManager!!.getBestProvider(criteria, true)
            val location = mLocationManager!!.getLastKnownLocation(provider)
            if (location != null) {
                listener.getLastKnownLocation(location)
            }
            if (myLocationListener == null) {
                myLocationListener = MyLocationListener()
            }
            mLocationManager!!.requestLocationUpdates(provider, minTime, minDistance.toFloat(), myLocationListener)
            return true
        }

        /**
         * unregister
         */
        @RequiresPermission(ACCESS_COARSE_LOCATION)
        fun unregister() {
            if (mLocationManager != null) {
                if (myLocationListener != null) {
                    mLocationManager!!.removeUpdates(myLocationListener)
                    myLocationListener = null
                }
                mLocationManager = null
            }
            if (mListener != null) {
                mListener = null
            }
        }

        /**
         * Set positioning parameters
         *
         * @return [Criteria]
         */
        private // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        // 设置是否要求速度
        // 设置是否允许运营商收费
        // 设置是否需要方位信息
        // 设置是否需要海拔信息
        // 设置对电源的需求
        val criteria: Criteria
            get() {
                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_FINE
                criteria.isSpeedRequired = false
                criteria.isCostAllowed = false
                criteria.isBearingRequired = false
                criteria.isAltitudeRequired = false
                criteria.powerRequirement = Criteria.POWER_LOW
                return criteria
            }

        /**
         * Get location based on latitude and longitude
         *
         * @param latitude latitude
         * @param longitude longitude
         * @return [Address]
         */
        fun getAddress(latitude: Double, longitude: Double): Address? {
            val geocoder = Geocoder(Utils.getApp(), Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses.size > 0) {
                    return addresses[0]
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        /**
         * Get the country based on latitude and longitude
         *
         * @param latitude latitude
         * @param longitude longitude
         * @return Country
         */
        fun getCountryName(latitude: Double, longitude: Double): String {
            val address = getAddress(latitude, longitude)
            return if (address == null) "unknown" else address.countryName
        }

        /**
         * Get the location based on latitude and longitude
         *
         * @param latitude latitude
         * @param longitude longitude
         * @return location
         */
        fun getLocality(latitude: Double, longitude: Double): String {
            val address = getAddress(latitude, longitude)
            return if (address == null) "unknown" else address.locality
        }

        /**
         * Get the street based on latitude and longitude
         *
         * @param latitude latitude
         * @param longitude longitude
         * @return Street
         */
        fun getStreet(latitude: Double, longitude: Double): String {
            val address = getAddress(latitude, longitude)
            return if (address == null) "unknown" else address.getAddressLine(0)
        }

        /**
         * Is it better
         *
         * @param newLocation The new Location that you want to evaluate
         * @param currentBestLocation The current Location fix, to which you want to compare the new one
         * @return `true`: Yes<br></br>`false`: No
         */
        fun isBetterLocation(newLocation: Location, currentBestLocation: Location?): Boolean {
            if (currentBestLocation == null) {
                // A new location is always better than no location
                return true
            }

            // Check whether the new location fix is newer or older
            val timeDelta = newLocation.time - currentBestLocation.time
            val isSignificantlyNewer = timeDelta > TWO_MINUTES
            val isSignificantlyOlder = timeDelta < -TWO_MINUTES
            val isNewer = timeDelta > 0

            // If it's been more than two minutes since the current location, use the new location
            // because the user has likely moved
            if (isSignificantlyNewer) {
                return true
                // If the new location is more than two minutes older, it must be worse
            } else if (isSignificantlyOlder) {
                return false
            }

            // Check whether the new location fix is more or less accurate
            val accuracyDelta = (newLocation.accuracy - currentBestLocation.accuracy).toInt()
            val isLessAccurate = accuracyDelta > 0
            val isMoreAccurate = accuracyDelta < 0
            val isSignificantlyLessAccurate = accuracyDelta > 200

            // Check if the old and new location are from the same provider
            val isFromSameProvider = isSameProvider(newLocation.provider, currentBestLocation.provider)

            // Determine location quality using a combination of timeliness and accuracy
            if (isMoreAccurate) {
                return true
            } else if (isNewer && !isLessAccurate) {
                return true
            } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
                return true
            }
            return false
        }

        /**
         * Is it the same provider
         *
         * @param provider0 Provider 0
         * @param provider1 Provider 1
         * @return `true`: Yes<br></br>`false`: No
         */
        fun isSameProvider(provider0: String?, provider1: String?): Boolean {
            return if (provider0 == null) {
                provider1 == null
            } else provider0 == provider1
        }
    }
}
