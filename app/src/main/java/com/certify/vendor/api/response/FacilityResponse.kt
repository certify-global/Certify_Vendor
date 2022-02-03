package com.certify.vendor.api.response

data class FacilityResponse (val responseCode : Int?, val responseSubCode : Int?,
                             val responseMessage : String?, val responseData: ArrayList<FacilityData>?)

data class FacilityData (val facilityId : String, val facilityName : String,  val state : String,
                         val city : String, val phone : String, val streetAddress : String,
                         val zip : String, val email : String, val isAppointmentRequired : Boolean
                         ){
        override fun toString(): String {
            return facilityName
        }
}


