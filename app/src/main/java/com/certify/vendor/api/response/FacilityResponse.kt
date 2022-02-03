package com.certify.vendor.api.response

data class FacilityResponse (val responseCode : Int?, val responseSubCode : Int?,
                             val responseMessage : String?, val responseData: ArrayList<FacilityData>?)

data class FacilityData (val facilityId : String, val facilityName : String)

