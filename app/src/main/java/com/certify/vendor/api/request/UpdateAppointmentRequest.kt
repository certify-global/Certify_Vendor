package com.certify.vendor.api.request

import java.io.Serializable

data class UpdateAppointmentRequest(val vendorId : Int, val certifyId : Int, val facilityId : Int, val userId : Int,
                                    val source : String,  val start : String, val end : String,
                                    val visitReason : String, val appointmentId : Int,val action:Int) : Serializable



