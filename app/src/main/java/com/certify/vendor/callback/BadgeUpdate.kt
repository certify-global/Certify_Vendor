package com.certify.vendor.callback

import com.certify.vendor.api.request.GetAppointmentRequest
import com.certify.vendor.api.request.LoginRequest
import com.certify.vendor.api.response.GetAppointmentResponse
import com.certify.vendor.api.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface BadgeUpdate {
    fun badgeUIUpdate()
}