package com.certify.vendor.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.repo.AppointmentRepository
import com.certify.vendor.repo.ScheduleAppoinmentRepository

class ScheduleAppointmentViewModel : BaseViewModel() {

    var scheduleAppointmentLiveData = MutableLiveData<Boolean>()
    private var appointmentRepository = ScheduleAppoinmentRepository()

    fun init (context: Context?) {
        RetrofitInstance.init(context)
    }

    fun scheduleAppointments(vendorId: Int,selectedDate: String,
                             startTime: String, endTime: String, contactName: String,visitReason: String,facilityID: Int) {
        loading.value = true
        appointmentRepository.scheduleAppoinments(vendorId,selectedDate,startTime,endTime,contactName,visitReason,facilityID) { isSuccess, appointmentResponse ->
            loading.value = false
            scheduleAppointmentLiveData.value = isSuccess
        }
    }
}