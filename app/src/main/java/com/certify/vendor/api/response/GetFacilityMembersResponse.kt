package com.certify.vendor.api.response

data class GetFacilityMembersResponse(
    val responseCode: Int, val responseSubCode: Int, val responseMessage: String?,
    var responseData: ArrayList<ResponseDataMember>
)

data class ResponseDataMember(val memberName: String, val memberId: String, val memberType: String, val individualId: Int)
