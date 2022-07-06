package com.certify.vendor.api.request

import java.io.Serializable

data class ScheduleAppointmentRequest(val facilityId : Int, val source : String, val date : String,
                                      val start : String, val end : String, val contactName : Int, val visitReason : String,val locationId:Int,val departmentId:Int) : Serializable