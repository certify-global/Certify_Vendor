package com.certify.vendor.common

class Constants {

    companion object {
        val VENDOR_APP: String = "MOBILE_APP"
        val ACCESS_TOKEN = "AccessToken"
        val SHARED_PREFS = "vendorCertify"
        val IS_LOGGEDIN = "isLoggedIn"
        val FIRST_NAME = "firstName"
        val USER_EMAIL = "userEmail"
        val USER_PROFILE_PIC = "userProfilePic"
        val VENDOR_ID = "vendorId"
        val VENDOR_GUID = "vendorGuid"
        val VENDOR_COMPANY_NAME = "vendorCompanyName"
        val LAST_NAME = "lastName"
        val APPOINT_TIME = "appointTime"
        val APPOINT_DATE = "appointDate"
        val APPOINT_END_TIME = "appoint_end_time"
        val BADGE_ID = "BadgeId"
        val BADGE_EXPIRY = "BadgeExpiry"
        val BADGE_EXPIRY_MM_DD_YY = "BadgeExpiry_mm_dd_yy"
        val BADGE_FIRMWARE_VERSION = "BadgeFirmwareVersion"
        val BADGE_FW_UPDATE_TIME = "BadgeFWUpdateTime"
        val BADGE_MAC_ADDRESS = "BadgeMacAddress"
        val BADGE_BATTERY_STATUS = "BadgeBattery"
        val BADGE_DEVICE_UPDATED = "BadgeDeviceUpdated"


    }

    enum class AppointmentStatus {
        PENDING,
        APPROVED,
        DECLINED,
        CANCELLED,
        CHECKEDIN,
        CHECKEDOUT,
        EXPIRED

    }

    enum class AppointmentTypes {
        EXPIRED,
        PAST,
        UPCOMING

    }
}