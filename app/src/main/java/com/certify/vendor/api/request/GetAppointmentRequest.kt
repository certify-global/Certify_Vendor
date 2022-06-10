package com.certify.vendor.api.request

import java.io.Serializable

data class GetAppointmentRequest(val source : String, val appointmentStatus : Int, val isUpcoming : Int) : Serializable