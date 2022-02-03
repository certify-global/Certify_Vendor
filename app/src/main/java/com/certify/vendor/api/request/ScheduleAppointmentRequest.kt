package com.certify.vendor.api.request

import java.io.Serializable

data class ScheduleAppointmentRequest(val vendorId : Int, val certifyId : Int, val facilityId : Int, val userId : Int,
                                      val source : String, val date : String, val start : String, val end : String,
                                      val contactName : String, val visitReason : String) : Serializable