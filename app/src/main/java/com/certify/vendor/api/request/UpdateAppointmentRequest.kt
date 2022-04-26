package com.certify.vendor.api.request

import java.io.Serializable

data class UpdateAppointmentRequest(val facilityId : Int, val source : String, val date : String?, val start : String,
                                    val end : String, val visitReason : String, val appointmentId : Int, val action:Int) : Serializable



