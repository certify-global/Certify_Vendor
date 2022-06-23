package com.certify.vendor.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.certify.vendor.R
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.data.AppointmentDataSource
import com.certify.vendor.databinding.FragmentAppointmentDitailsBinding

class AppointmentViewFragment : Fragment() {

    private val TAG = AppointmentViewFragment::class.java.name
    private var sharedPreferences: SharedPreferences? = null
    private lateinit var fragmentAppointmentDitailsBinding: FragmentAppointmentDitailsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAppointmentDitailsBinding = FragmentAppointmentDitailsBinding.inflate(inflater, container, false)
        return fragmentAppointmentDitailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
        initView(requireContext());
    }

    private fun initView(context: Context) {
        fragmentAppointmentDitailsBinding.imgBack.setOnClickListener { findNavController().popBackStack() }
        var appointmentData = AppointmentDataSource.getAppointmentData()
        fragmentAppointmentDitailsBinding.tvFacilityViewValue.text = appointmentData.facilityName
        fragmentAppointmentDitailsBinding.tvLocationViewValue.text = appointmentData.locationName
        val dateStr = context.getString(R.string.appointment_time).let {
            String.format(it, Utils.getTime(appointmentData.start), Utils.getTime(appointmentData.end), Utils.getDate(appointmentData.start, "dd MMM yyyy"))
        }
        fragmentAppointmentDitailsBinding.tvAppointmentTimeViewValue.text = dateStr
        val address = context.getString(R.string.appointment_location).let {
            String.format(it, appointmentData.facilityAddress.address1, appointmentData.facilityAddress.address2, appointmentData.facilityAddress.city, appointmentData.facilityAddress.state, appointmentData.facilityAddress.zip)
        }
        fragmentAppointmentDitailsBinding.tvAddressViewValue.text = address
        fragmentAppointmentDitailsBinding.tvDepartmentViewValue.text = ""
        fragmentAppointmentDitailsBinding.tvPurposeOfViewValue.text = appointmentData.visitReason

        when (appointmentData.statusFlag) {
            1 -> {
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.setTextColor(context.getColor(R.color.blue))
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.text = Constants.AppointmentStatus.CHECKEDIN.name
            }
            5 -> {
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.setTextColor(context.getColor(R.color.declined_cancelled))
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.text = Constants.AppointmentStatus.CANCELLED.name
            }
            7 -> {
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.setTextColor(context.getColor(R.color.pending))
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.text = Constants.AppointmentStatus.PENDING.name
            }
            10 -> {
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.setTextColor(context.getColor(R.color.blue))
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.text = Constants.AppointmentStatus.CHECKEDOUT.name
            }
            12 -> {
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.setTextColor(context.getColor(R.color.green))
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.text = Constants.AppointmentStatus.APPROVED.name
            }
            13 -> {
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.setTextColor(context.getColor(R.color.declined_cancelled))
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.text = Constants.AppointmentStatus.DECLINED.name
            }
            0 -> {
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.setTextColor(context.getColor(R.color.pending))
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.text = Constants.AppointmentStatus.PENDING.name
            }
        }

    }
}