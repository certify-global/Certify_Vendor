package com.certify.vendor.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.certify.vendor.badge.BadgeController
import com.certify.vendor.databinding.FragmentBadgefwBinding
import com.certify.vendor.model.BadgeFWUpdateViewModel

class BadgeFWUpdateFragment : Fragment() {

    private lateinit var fragmentBadgeFWBinding : FragmentBadgefwBinding
    private var badgeFWViewModel : BadgeFWUpdateViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //TODO: UI as per the mock ups
        fragmentBadgeFWBinding = FragmentBadgefwBinding.inflate(inflater, container, false)
        fragmentBadgeFWBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = badgeFWViewModel
        }
        badgeFWViewModel = ViewModelProvider(this).get(BadgeFWUpdateViewModel::class.java)
        return fragmentBadgeFWBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFirmwareUpdateStatus()
        BadgeController.getInstance().initBle(this.context)
        badgeFWViewModel?.writeBadgeFirmware(viewLifecycleOwner)
    }

    private fun setFirmwareUpdateStatus() {
        badgeFWViewModel?.progress?.observe(viewLifecycleOwner) {
            Log.d("BadgeFirmwareUpdate", "Firmware UI update UI $it")
            fragmentBadgeFWBinding.fwUpdateStatus.setProgress(it, true)
            if (it == 101) {
                closeFragment()
            }
        }
    }

    private fun closeFragment() {
        findNavController().popBackStack()
    }
}