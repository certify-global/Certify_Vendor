package com.certify.vendor.api.response

data class FacilityResponse (val responseCode : Int?, val responseSubCode : Int?,
                             val responseMessage : String?, val responseData: ArrayList<FacilityData>?,
                             val errorMessage : String?)

data class FacilityData (val facilityId : Int, val facilityName : String,  val state : String,
                         val city : String, val phone : String, val streetAddress : String,
                         val zip : String, val email : String, val lastVisit : String, val futureVisit : String,
                         val totalRequirementCount : Int, val isAppointmentRequired : Boolean,
                         val incompleteRequirementCount:Int, val approvedby : String, val exestatus : Int) {
        override fun toString(): String {
            return facilityName
        }
}


