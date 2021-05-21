package com.example.geoloacationfinder

import android.location.Location
import android.location.LocationListener
import android.os.Bundle


class MyLocationListener(private val locationDelegate: LocationDelegate): LocationListener {

    interface LocationDelegate {
        fun provideLocation(location: Location)
    }
    override fun onLocationChanged(location: Location) {
        locationDelegate.provideLocation(location)
    }

    @Deprecated("")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}
}