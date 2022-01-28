package com.certify.vendor.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.repo.AppointmentRepository

class AppointmentViewModel : BaseViewModel() {

    var appointmentLiveData = MutableLiveData<Boolean>()
    private var appointmentRepository = AppointmentRepository()

    fun init (context: Context?) {
        RetrofitInstance.init(context)
    }

    fun getAppointments() {
        loading.value = true
        appointmentRepository.getAppointments { isSuccess, appointmentResponse ->
            loading.value = false
            appointmentLiveData.value = isSuccess
        }
    }
}