package com.certify.vendor.api

import com.certify.vendor.api.request.*
import com.certify.vendor.api.response.*
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @POST("GetUserInfo")
    fun loginUser(@Body loginRequest: LoginRequest) : Call<LoginResponse>

    @POST("GetAppointments")
    fun getAppointments(@Body appointmentRequest: GetAppointmentRequest) : Call<GetAppointmentResponse>

    @GET("GetVendorFacilitiesWithDetails")
    fun getFacilityList(@Query("vendorId") vendorId: Int): Call<FacilityResponse>

    @POST("ScheduleAppointment")
    fun scheduleAppoinments(@Body scheduleAppointmentRequest: ScheduleAppointmentRequest) : Call<ScheduleAppointmentResponse>

    @POST("UpdateAppointment")
    fun updateAppointment(@Body updateAppointmentRequest: UpdateAppointmentRequest) : Call<UpdateAppointmentResponse>

    @POST("UpdateUserInfo")
    fun updateUserInfo(@Body updateUserRequest: UpdateUserRequest) : Call<UpdateUserResponse>

    @POST("GetDeparmentLocationWithFacilityId")
    fun getDepartmentLocationWith(@Body departmentLocationRequest: DepartmentLocationRequest): Call<DepartmentLocationResponse>

}