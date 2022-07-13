package com.certify.vendor.data

import com.certify.vendor.api.response.AppointmentData
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils

object AppointmentDataSource {

    private var appointmentList = arrayListOf<AppointmentData>()
    private var postAppointmentList = arrayListOf<AppointmentData>()
    private var expiredAppointmentList = arrayListOf<AppointmentData>()
    private lateinit var appointmentData: AppointmentData
    private var unauthorized = 0
      var isSchedule = true
      var appointmentType = Constants.AppointmentTypes.UPCOMING.name



    fun addAppointmentList(apptmentList: List<AppointmentData>) {
        appointmentList.clear()
        appointmentList.addAll(apptmentList.sortedBy { it.start })
    }

    fun addPostAppointmentList(apptmentList: List<AppointmentData>) {
        postAppointmentList.clear()
        postAppointmentList.addAll(apptmentList.sortedByDescending { it.start })
    }

    fun addExpiredAppointmentList(apptmentList: List<AppointmentData>) {
        expiredAppointmentList.clear()
        expiredAppointmentList.addAll(apptmentList.sortedByDescending { it.start })
    }

    fun getAppointmentList() = appointmentList
    fun getPostAppointmentList() = postAppointmentList
    fun getExpiredAppointmentList() = expiredAppointmentList

    fun clearAppointment() {
        appointmentList.clear()
        postAppointmentList.clear()
        expiredAppointmentList.clear()
    }


    fun getAppointmentData(): AppointmentData = appointmentData

    fun setAppointmentData(appointmentData: AppointmentData) {
        this.appointmentData = appointmentData
    }

    fun setUnauthorized(unauthorized: Int) {
        this.unauthorized = unauthorized
    }

    fun getUnauthorized() = unauthorized
}