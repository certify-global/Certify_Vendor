package com.certify.vendor.api.response

data class DepartmentLocationResponse(
    val responseCode: Int?, val responseSubCode: Int?,
    val responseMessage: String?, val responseData: ResponseData?,
    val errorMessage: String?
)

data class ResponseData(
    val facilityId: Int, val healthSystemId: Int, val locationforfacilityList: ArrayList<LocationforfacilityList>,
    val departmentforfacilityList: ArrayList<DepartmentforfacilityList>
)

data class LocationforfacilityList(val facilityId: Int, val locationId: Int, val locationName: String) {
    override fun toString(): String {
        return locationName
    }
}

data class DepartmentforfacilityList(val departmentId: Int, val departmentName: String) {
    override fun toString(): String {
        return departmentName
    }
}


