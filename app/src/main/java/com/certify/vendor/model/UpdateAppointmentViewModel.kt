package com.certify.vendor.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.api.response.DataResponse
import com.certify.vendor.repo.UpdateAppointmentRepository

class UpdateAppointmentViewModel : BaseViewModel() {

    var updateAppointmentLiveData = MutableLiveData<Boolean>()
    var dataResponse = MutableLiveData<DataResponse>()
    private var appointmentRepository = UpdateAppointmentRepository()

    fun init(context: Context?) {
        RetrofitInstance.init(context)
    }

    fun updateAppointments(date: String, startTime: String, endTime: String, visitReason: String, appointmentId: Int, action: Int, facilityID: Int) {
        loading.value = true
        appointmentRepository.updateAppointment(date, startTime, endTime, visitReason, appointmentId, action, facilityID) { isSuccess, appointmentResponse ->
            loading.value = false
            updateAppointmentLiveData.value = isSuccess
        }
    }

    fun updateVendorAppointment(appointmentId: Int, contactId: Int, visitReason: String, departmentId: Int) {

        appointmentRepository.editVendorAppointment(appointmentId, contactId, visitReason, departmentId) { isSuccess, response ->
            Log.i("updateVendorAppointment Members = ", "" + response)
            dataResponse.value = response
        }
    }
}