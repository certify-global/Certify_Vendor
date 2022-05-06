package com.certify.vendor.data

import com.certify.vendor.api.response.AppointmentData
import com.certify.vendor.common.Utils

object AppointmentDataSource {

    private var appointmentList = arrayListOf<AppointmentData>()
    private var isupcoming = true
    private var pastAppointPosition = -1
    private lateinit var appointmentData : AppointmentData

    fun addAppointmentList(apptmentList: List<AppointmentData>) {
        appointmentList.clear()
        appointmentList.addAll(apptmentList.sortedByDescending {it.start })
    }


    fun getAppointmentList() = appointmentList
    fun clearAppointment() {
        appointmentList.clear()
    }
    fun getAppointmentIsUpcoming() = isupcoming
    fun setAppointmentIsUpcoming(status: Boolean) {
        isupcoming = status;
    }
    fun getPastAppointPosition() = pastAppointPosition

    fun getAppointmentData() : AppointmentData = appointmentData

    fun setAppointmentData(appointmentData: AppointmentData) {
        this.appointmentData = appointmentData
    }
}