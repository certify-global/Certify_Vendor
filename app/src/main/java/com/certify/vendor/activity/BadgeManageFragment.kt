package com.certify.vendor.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.certify.vendor.R
import com.certify.vendor.badge.BadgeFirmwareUpdate
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.databinding.FragmentManageBadgeBinding
import com.certify.vendor.model.BadgeViewModel


class BadgeManageFragment : Fragment() {

    private val TAG = BadgeManageFragment::class.java.name
    private lateinit var badgeManageFragmentBinding : FragmentManageBadgeBinding
    private var badgeViewModel : BadgeViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        badgeManageFragmentBinding = FragmentManageBadgeBinding.inflate(inflater, container, false)
        badgeManageFragmentBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = badgeViewModel
        }
        badgeViewModel = ViewModelProvider(this).get(BadgeViewModel::class.java)
        return badgeManageFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUserProfile()
        setFirmwareInfo()
    }

    private fun setUserProfile() {
        val sharedPreferences = AppSharedPreferences.getSharedPreferences(this.context)
        val userPicStr =
            AppSharedPreferences.readString(sharedPreferences, Constants.USER_PROFILE_PIC)
        if (userPicStr.isNotEmpty())
            badgeManageFragmentBinding.badgeUserImage.setImageBitmap(Utils.decodeBase64ToImage(userPicStr))

        badgeManageFragmentBinding.badgeUserName.text = String.format(
            getString(R.string.badge_user_name),
            AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME),
            AppSharedPreferences.readString(sharedPreferences, Constants.LAST_NAME)
        )

        badgeManageFragmentBinding.badgeId.text = String.format(
            "%s%s",
            getString(R.string.id),
            AppSharedPreferences.readString(sharedPreferences, Constants.BADGE_ID)
        )
        badgeManageFragmentBinding.badgeStatus.text = getString(R.string.active)
        badgeManageFragmentBinding.badgeCompanyName.text = AppSharedPreferences.readString(sharedPreferences, Constants.VENDOR_COMPANY_NAME)
        badgeManageFragmentBinding.badgeExpires.text = AppSharedPreferences.readString(sharedPreferences, Constants.APPOINT_DATE)
    }

    private fun setFirmwareInfo() {
        badgeViewModel?.init(this.context)
        val sharedPreferences = AppSharedPreferences.getSharedPreferences(this.context)
        badgeManageFragmentBinding.firmwareVersion.text = String.format(getString(R.string.firmware_version),
                                                                        sharedPreferences?.getString(Constants.BADGE_FIRMWARE_VERSION,
                                                                        BadgeFirmwareUpdate.FIRMWARE_VERSION1))
        if (badgeViewModel?.isBadgeFirmwareUpdateToDate(this.context) == true) {
            badgeManageFragmentBinding.firmwareUpdateAvailable.text =
                getString(R.string.firmware_upto_date)
            badgeManageFragmentBinding.firmwareUpdate.isEnabled = false
            badgeManageFragmentBinding.firmwareUpdate.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.gray))
        } else {
            badgeManageFragmentBinding.firmwareUpdate.isEnabled = true
            badgeManageFragmentBinding.firmwareUpdateAvailable.text =
                getString(R.string.new_firmware_update_available)
        }
        badgeManageFragmentBinding.firmwareLastUpdated.text = String.format(getString(R.string.firmware_last_updated),
                                                                            sharedPreferences?.getString(Constants.BADGE_FW_UPDATE_TIME, ""))
        badgeManageFragmentBinding.firmwareUpdate.setOnClickListener {
            showFirmwareUpdateDialog()
        }
    }

    private fun showFirmwareUpdateDialog() {
        val alertDialog = this.context?.let { AlertDialog.Builder(it) }
        val view = layoutInflater.inflate(R.layout.fragment_badgefw, null)
        alertDialog?.setView(view)
        val progressBar = view.findViewById<ProgressBar>(R.id.fw_update_status)
        val updatePercent = view.findViewById<TextView>(R.id.firmware_update_percent)
        val dialog = alertDialog?.create()
        dialog?.setCancelable(false)
        badgeViewModel?.firmwareProgress?.observe(viewLifecycleOwner) {
            progressBar.setProgress(it, true)
            updatePercent.text = "$it%"
            if (it == 101) {
                dialog?.dismiss()
                setFirmwareInfo()
            }
        }
        dialog?.show()
        badgeViewModel?.writeBadgeFirmware(this)
    }
}