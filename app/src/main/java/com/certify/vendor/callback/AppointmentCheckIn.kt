package com.certify.vendor.callback

import com.certify.vendor.api.response.AppointmentData

interface AppointmentCheckIn {
    fun onAppointmentCheckIn(value: AppointmentData)
    fun onAppointmentDetails(value: AppointmentData)
}