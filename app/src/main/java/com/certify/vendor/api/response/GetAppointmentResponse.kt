package com.certify.vendor.api.response

data class GetAppointmentResponse(
    val responseCode: Int, val responseSubCode: Int, val responseMessage: String?,
    val responseData: List<AppointmentData>?, val errorMessage: String?
)

data class AppointmentData(
    val appointmentId: Int, val vendorId: Int, val facilityId: Int, val facilityName: String, val locationName: String,
    val facilityAddress: FacilityAddress, val start: String, val end: String, val visitReason: String,
    val statusFlag: Int, val reasonForDecline: String, val mobileCheckinAllowed: Int, val vendorGuid: String,
    val contactName: Int, val source: String, val approverName: String, val appointmentGuid: String, val contactPerson: String,val departmentId: Int
)

data class FacilityAddress(val address1: String, val address2: String, val city: String, val zip: String, val state: String)
