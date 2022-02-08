package com.certify.vendor.Controller

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import java.util.*


public class AppointmentController {

    private var contextM: Context? = null
    private var userLocation: Location? = Location("")

    @SuppressLint("StaticFieldLeak")
    companion object {
        private var instance: AppointmentController? = null
        fun getInstance(): AppointmentController? {
            if (instance == null) {
                instance = AppointmentController()
            }
            return instance
        }
    }

    fun initAppointment(context: Context) {
        this.contextM = context

    }

    fun setAppointmentLocation(location: Location) {
        this.userLocation = location
    }

    fun getAddressToLatLon(address: String):Boolean {
        val geocoder = Geocoder(contextM, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocationName(address, 1)
            if (addressList != null && addressList.size > 0) {
                val addressLocation = addressList.get(0)
                val location = Location("")
                location.latitude = addressLocation.latitude
                location.longitude = addressLocation.longitude

                val distanceInMeters = userLocation?.distanceTo(location)
                val km = distanceInMeters?.div(1000)
                Log.i("getUserLocation", "distanceInMeters :" + distanceInMeters + "km = " + km +"km!! < 0.06" +(km!! < 3))
                return km!! < 3
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}