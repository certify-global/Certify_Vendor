package com.certify.vendor.api.request

import java.io.Serializable

data class EditVendorAppointmentRequest(val appointmentId: Int, val visitReason: String, val contactName: Int, val departmentId: Int) : Serializable



