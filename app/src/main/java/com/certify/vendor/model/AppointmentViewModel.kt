package com.certify.vendor.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.api.response.AppointmentData
import com.certify.vendor.repo.AppointmentRepository

class AppointmentViewModel : BaseViewModel() {

    var appointmentLiveData = MutableLiveData<Boolean>()
    var pastAppointmentLiveData = MutableLiveData<List<AppointmentData>>()
    private var appointmentRepository = AppointmentRepository()

    fun init (context: Context?) {
        RetrofitInstance.init(context)
    }

    fun getAppointments(vendorId: Int) {
        loading.value = true
        appointmentRepository.getAppointments(vendorId) { isSuccess, appointmentResponse ->
            loading.value = false
            appointmentLiveData.value = isSuccess
        }
    }
    fun getPastAppointments(vendorId: Int) {
        appointmentRepository.getPastAppointments(vendorId) { appointmentResponse ->
            if(appointmentResponse?.responseCode == 1)
            pastAppointmentLiveData.value = appointmentResponse.responseData as List<AppointmentData>
        }
    }
}