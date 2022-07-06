package com.certify.vendor.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.repo.ScheduleAppoinmentRepository

class ScheduleAppointmentViewModel : BaseViewModel() {

    var scheduleAppointmentLiveData = MutableLiveData<Boolean>()
    private var appointmentRepository = ScheduleAppoinmentRepository()

    fun init (context: Context?) {
        RetrofitInstance.init(context)
    }

    fun scheduleAppointments(selectedDate: String,
                             startTime: String, endTime: String, contactName: Int,visitReason: String,facilityID: Int,locationId:Int, departmentId:Int) {
        loading.value = true
        appointmentRepository.scheduleAppoinments(selectedDate,startTime,endTime,contactName,visitReason,facilityID,locationId,departmentId) { isSuccess, appointmentResponse ->
            loading.value = false
            scheduleAppointmentLiveData.value = isSuccess
        }
    }
}