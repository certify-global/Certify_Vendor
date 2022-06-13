package com.certify.vendor.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.api.response.AppointmentData
import com.certify.vendor.data.AppointmentDataSource
import com.certify.vendor.repo.AppointmentRepository

class AppointmentViewModel : BaseViewModel() {

    var appointmentLiveData = MutableLiveData<Boolean>()
    var pastAppointmentLiveData = MutableLiveData<List<AppointmentData>>()
    var expiredAppointmentLiveData = MutableLiveData<List<AppointmentData>>()

    private var appointmentRepository = AppointmentRepository()

    fun init(context: Context?) {
        RetrofitInstance.init(context)
    }

    fun getAppointments(vendorId: Int) {
        loading.value = true
        appointmentRepository.getAppointments(vendorId) { isSuccess, appointmentResponse ->
            loading.value = false
            appointmentLiveData.value = isSuccess
        }
    }

    fun getPastAppointments() {
        appointmentRepository.getPastAppointments() { appointmentResponse ->
            if (appointmentResponse?.responseCode == 1) {
                pastAppointmentLiveData.value = appointmentResponse.responseData as List<AppointmentData>
                AppointmentDataSource.addPostAppointmentList(appointmentResponse.responseData)
            } else {
                pastAppointmentLiveData.value = ArrayList<AppointmentData>()
                AppointmentDataSource.addPostAppointmentList(ArrayList<AppointmentData>())
            }
        }
    }

    fun getExpiredAppointments() {
        appointmentRepository.getExpiredAppointments() { appointmentResponse ->
            if (appointmentResponse?.responseCode == 1) {
                expiredAppointmentLiveData.value = appointmentResponse.responseData as List<AppointmentData>
                AppointmentDataSource.addExpiredAppointmentList(appointmentResponse.responseData)

            } else {
                expiredAppointmentLiveData.value = ArrayList<AppointmentData>()
                AppointmentDataSource.addExpiredAppointmentList(ArrayList<AppointmentData>())
            }
        }
    }
}