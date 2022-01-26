package com.certify.vendor.api.request

import java.io.Serializable

data class GetAppointmentRequest(val vendorId : Int, val certifyId : Int, val facilityId : Int, val userId : Int,
                                 val source : String, val appointmentStatus : Int, val recordsPerPage : Int, val pageNumber : Int,
                                 val isUpcoming : Boolean) : Serializable