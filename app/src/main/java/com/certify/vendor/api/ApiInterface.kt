package com.certify.vendor.api

import com.certify.vendor.api.request.GetAppointmentRequest
import com.certify.vendor.api.request.LoginRequest
import com.certify.vendor.api.response.GetAppointmentResponse
import com.certify.vendor.api.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST("Login/GetUserInfo")
    fun loginUser(@Body loginRequest: LoginRequest) : Call<LoginResponse>

    @POST("Appointment/Get")
    fun getAppointments(@Body appointmentRequest: GetAppointmentRequest) : Call<GetAppointmentResponse>

}