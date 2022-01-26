package com.certify.vendor.data

import com.certify.vendor.api.response.AppointmentData

object AppointmentDataSource {

    private var appointmentList = arrayListOf<AppointmentData>()

    fun addAppointmentList(apptmentList : List<AppointmentData>) {
        appointmentList.addAll(apptmentList)
    }

    fun getAppointmentList() = appointmentList
}