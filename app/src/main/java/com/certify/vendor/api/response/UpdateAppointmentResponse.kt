package com.certify.vendor.api.response

data class UpdateAppointmentResponse(val responseCode : Int, val responseSubCode : Int, val responseMessage : String?,
                                     val responseData : Boolean?,val errorMessage : String?)
