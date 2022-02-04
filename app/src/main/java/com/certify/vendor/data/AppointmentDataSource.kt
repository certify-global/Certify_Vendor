package com.certify.vendor.data

import com.certify.vendor.api.response.AppointmentData

object AppointmentDataSource {

    private var appointmentList = arrayListOf<AppointmentData>()
    private var isupcoming = true
    private var pastAppointPosition = -1


    fun addAppointmentList(apptmentList: List<AppointmentData>) {
        appointmentList.clear()
        appointmentList.addAll(apptmentList.reversed())
    }


    fun getAppointmentList() = appointmentList

    fun getAppointmentIsUpcoming() = isupcoming
    fun setAppointmentIsUpcoming(status: Boolean) {
        isupcoming = status;
    }
    fun getPastAppointPosition() = pastAppointPosition
}