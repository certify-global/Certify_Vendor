package com.certify.vendor.model

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.certify.vendor.badge.BadgeController
import com.certify.vendor.badge.BadgeFirmwareUpdate

class BadgeFWUpdateViewModel : ViewModel() {

    val progress = MutableLiveData<Int>()

    fun writeBadgeFirmware (lifecycleOwner: LifecycleOwner) {
        BadgeController.getInstance().writeFirmwareOTA()
        BadgeFirmwareUpdate.fwUpdateProgress.observe(lifecycleOwner) {
            progress.value = it
        }
    }


}