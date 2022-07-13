package com.certify.vendor.api.response

data class DataResponse(val responseCode : Int, val responseSubCode : Int, val responseMessage : String?,
                        val errorMessage : String?)
