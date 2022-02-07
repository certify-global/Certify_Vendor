package com.certify.vendor.data

import com.certify.vendor.api.response.AppointmentData
import com.certify.vendor.common.Utils

object AppointmentDataSource {

    private var appointmentList = arrayListOf<AppointmentData>()
    private var isupcoming = true
    private var pastAppointPosition = -1


    fun addAppointmentList(apptmentList: List<AppointmentData>) {//Utils.getDate(apptmentList.get(0).start,"yyyy-MM-dd")
        appointmentList.clear()
        appointmentList.addAll(apptmentList.sortedByDescending {it.start })
    }


    fun getAppointmentList() = appointmentList

    fun getAppointmentIsUpcoming() = isupcoming
    fun setAppointmentIsUpcoming(status: Boolean) {
        isupcoming = status;
    }
    fun getPastAppointPosition() = pastAppointPosition
}