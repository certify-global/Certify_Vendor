package com.certify.vendor.activity

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.certify.vendor.R
import com.certify.vendor.adapter.MemberListAdapter
import com.certify.vendor.api.response.AppointmentData
import com.certify.vendor.api.response.DepartmentforfacilityList
import com.certify.vendor.api.response.ResponseDataMember
import com.certify.vendor.callback.ItemOnClickCallback
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.data.AppointmentDataSource
import com.certify.vendor.data.FacilityDataSource
import com.certify.vendor.databinding.FragmentAppointmentDitailsBinding
import com.certify.vendor.model.FacilityViewModel
import com.certify.vendor.model.UpdateAppointmentViewModel
import java.util.prefs.Preferences

class AppointmentViewFragment : Fragment(), ItemOnClickCallback {

    private val TAG = AppointmentViewFragment::class.java.name
    private var sharedPreferences: SharedPreferences? = null
    private lateinit var fragmentAppointmentDitailsBinding: FragmentAppointmentDitailsBinding
    private var facilityViewModel: FacilityViewModel? = null
    private var updateAppointmentViewModel: UpdateAppointmentViewModel? = null
    private var departmentId: Int = 0
    private var pDialog: Dialog? = null
    private lateinit var appointmentData: AppointmentData
    private var memberList: List<ResponseDataMember>? = ArrayList()
    private var adapterMemberList: MemberListAdapter? = null
    private var contactMemberId: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAppointmentDitailsBinding = FragmentAppointmentDitailsBinding.inflate(inflater, container, false)
        facilityViewModel = ViewModelProvider(this).get(FacilityViewModel::class.java)
        updateAppointmentViewModel = ViewModelProvider(this).get(UpdateAppointmentViewModel::class.java)
        return fragmentAppointmentDitailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
        initView(requireContext())
        setFacilityListener()
        setOnBackPress()
    }

    private fun initView(context: Context) {
        appointmentData = AppointmentDataSource.getAppointmentData()
        facilityViewModel?.init(context)
        pDialog = Utils.ShowProgressDialog(requireContext())
        fragmentAppointmentDitailsBinding?.recMemberList?.layoutManager = LinearLayoutManager(context)
        fragmentAppointmentDitailsBinding?.recMemberList?.addItemDecoration(DividerItemDecoration(fragmentAppointmentDitailsBinding?.recMemberList?.context, DividerItemDecoration.VERTICAL))
        adapterMemberList = MemberListAdapter(requireContext(), this, memberList!!)
        fragmentAppointmentDitailsBinding?.recMemberList?.adapter = adapterMemberList
//
        // updateAppointmentViewModel.init()
        fragmentAppointmentDitailsBinding.imgBack.setOnClickListener {
            activity?.finish()
        }
        fragmentAppointmentDitailsBinding.etPurposeOfViewValue.addTextChangedListener {
            editValidations()
        }
        fragmentAppointmentDitailsBinding.etPersonViewValue.addTextChangedListener {
            if (!fragmentAppointmentDitailsBinding.etPersonViewValue.text.toString().equals(appointmentData.contactPerson) && it.toString().length > 2) {
                facilityViewModel?.getOnSiteContact(appointmentData?.facilityId!!, it.toString())
            } else {
                editValidations()
            }
        }
        fragmentAppointmentDitailsBinding.buttonCancel.setOnClickListener { activity?.finish() }
        fragmentAppointmentDitailsBinding.buttonSubmit.setOnClickListener {
            pDialog?.show()
            updateAppointmentViewModel?.updateVendorAppointment(appointmentData.appointmentId, contactMemberId, fragmentAppointmentDitailsBinding.etPurposeOfViewValue.text.toString(), departmentId)
        }
        contactMemberId = appointmentData.contactName
        if (appointmentData.facilityId != 0)
            facilityViewModel?.departmentLocationWith(appointmentData.facilityId)
        else fragmentAppointmentDitailsBinding.spinnerDepartment.visibility = View.GONE
        fragmentAppointmentDitailsBinding.tvCompanyViewValue.text = AppSharedPreferences.readString(sharedPreferences, Constants.VENDOR_COMPANY_NAME)
        fragmentAppointmentDitailsBinding.tvFacilityViewValue.text = appointmentData.facilityName
        fragmentAppointmentDitailsBinding.tvLocationViewValue.text = appointmentData.locationName
        fragmentAppointmentDitailsBinding.tvNameViewValue.text = String.format("%s %s", AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME), AppSharedPreferences.readString(sharedPreferences, Constants.LAST_NAME))

        val dateStr = context.getString(R.string.appointment_time).let {
            String.format(it, Utils.getTime(appointmentData.start), Utils.getTime(appointmentData.end), Utils.getDate(appointmentData.start, "dd MMM yyyy"))
        }
        fragmentAppointmentDitailsBinding.tvAppointmentTimeViewValue.text = dateStr
//        val address = context.getString(R.string.appointment_location).let {
//            String.format(it, appointmentData.facilityAddress.address1, appointmentData.facilityAddress.address2, appointmentData.facilityAddress.city, appointmentData.facilityAddress.state, appointmentData.facilityAddress.zip)
//        }
        fragmentAppointmentDitailsBinding.etPersonViewValue.setText(appointmentData.contactPerson)
        fragmentAppointmentDitailsBinding.etPurposeOfViewValue.setText(appointmentData.visitReason)
        fragmentAppointmentDitailsBinding.tvApprovedByValue.text = appointmentData.approverName
        fragmentAppointmentDitailsBinding.buttonSubmit.visibility = View.GONE

        when (appointmentData.statusFlag) {
            1 -> {
                fragmentAppointmentDitailsBinding.buttonSubmit.visibility = View.VISIBLE
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.setTextColor(context.getColor(R.color.blue))
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.text = Constants.AppointmentStatus.CHECKEDIN.name
            }
            5 -> {
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.setTextColor(context.getColor(R.color.declined_cancelled))
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.text = Constants.AppointmentStatus.CANCELLED.name
            }
            7 -> {
                fragmentAppointmentDitailsBinding.buttonSubmit.visibility = View.VISIBLE
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.setTextColor(context.getColor(R.color.pending))
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.text = Constants.AppointmentStatus.PENDING.name
            }
            10 -> {
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.setTextColor(context.getColor(R.color.blue))
                fragmentAppointmentDitailsBinding.tvAppointmentStatusViewValue.text = Constants.AppointmentStatus.CHECKEDOUT.name
            }
            12 -> {
                fragmentAppointmentDitailsBinding.buttonSubmit.visibility = View.VISIBLE
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
        if (!AppointmentDataSource.appointmentType.equals(Constants.AppointmentTypes.UPCOMING.name)) {
            fragmentAppointmentDitailsBinding.buttonSubmit.visibility = View.GONE
            fragmentAppointmentDitailsBinding.buttonCancel.visibility = View.GONE
            fragmentAppointmentDitailsBinding.etPersonViewValue.isEnabled = false
            fragmentAppointmentDitailsBinding.etPurposeOfViewValue.isEnabled = false
            fragmentAppointmentDitailsBinding.spinnerDepartment.isEnabled = false
        }
    }

    private fun setFacilityListener() {
        try {
            facilityViewModel?.departmentLocationWithLiveData?.observe(viewLifecycleOwner) {
                if (it == null) return@observe
                if (it.responseData?.departmentforfacilityList?.size!! > 0) {
                    fragmentAppointmentDitailsBinding.spinnerDepartment?.visibility = View.VISIBLE
                    // FacilityDataSource.addDepartmentList(it.responseData.departmentforfacilityList)
                    initSpinnerDepartment(it.responseData.departmentforfacilityList)
                } else {
                    fragmentAppointmentDitailsBinding.spinnerDepartment?.visibility = View.GONE
                }
            }
            facilityViewModel?.facilityMembersRequestWithLiveData?.observe(viewLifecycleOwner) {
                if (it.responseCode == 1 && !it.responseData.isEmpty()) {
                    Utils.hideKeyboard(requireActivity())
                    memberList = it.responseData
                    adapterMemberList?.updateMemberList(memberList!!)
                    fragmentAppointmentDitailsBinding?.recMemberList?.visibility = View.VISIBLE
                } else fragmentAppointmentDitailsBinding?.recMemberList?.visibility = View.GONE

            }

            updateAppointmentViewModel?.dataResponse?.observe(viewLifecycleOwner) {
                if (pDialog != null) pDialog?.cancel()
                if (it == null) return@observe
                if (it.responseCode == 1) {
                    fragmentAppointmentDitailsBinding.buttonSubmit.isEnabled = false
                    fragmentAppointmentDitailsBinding.buttonSubmit.alpha = .7f
                } else {
                    Toast.makeText(context, it?.responseMessage, Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {

        }
    }

    private fun initSpinnerDepartment(departmentforfacilityList: List<DepartmentforfacilityList>) {
        val adapterDepartment = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, departmentforfacilityList)
        fragmentAppointmentDitailsBinding?.spinnerDepartment?.adapter = adapterDepartment
        fragmentAppointmentDitailsBinding?.spinnerDepartment?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // if (position == 0) return
                val departmentTemp = parent.getSelectedItem() as DepartmentforfacilityList
                departmentId = departmentTemp.departmentId
                editValidations()
//                if ((FacilityDataSource.getLocationForFacilityList().size > 0 && locationId > 0) || FacilityDataSource.getLocationForFacilityList().size == 0)
//                    updateUI(facilityData!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        for (item in departmentforfacilityList.indices) {
            if (appointmentData.departmentId == departmentforfacilityList.get(item).departmentId)
                fragmentAppointmentDitailsBinding?.spinnerDepartment.setSelection(item)
        }
    }

    private fun setOnBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override
            fun handleOnBackPressed() {
                activity?.finish()
            }
        })
    }

    private fun editValidations() {
        if (departmentId == appointmentData.departmentId && fragmentAppointmentDitailsBinding.etPurposeOfViewValue.text.toString().equals(appointmentData.visitReason) && fragmentAppointmentDitailsBinding.etPersonViewValue.text.toString().equals(appointmentData.contactPerson)) {
            fragmentAppointmentDitailsBinding.buttonSubmit.isEnabled = false
            fragmentAppointmentDitailsBinding.buttonSubmit.alpha = .7f
        } else {
            fragmentAppointmentDitailsBinding.buttonSubmit.isEnabled = true
            fragmentAppointmentDitailsBinding.buttonSubmit.alpha = 1f
        }
    }

    override fun onItemOnClickCallBack(positionValue: Int) {
        try {
            contactMemberId = memberList?.get(positionValue)?.individualId!!
            fragmentAppointmentDitailsBinding?.etPersonViewValue?.setText(memberList?.get(positionValue)?.memberName!!)
            fragmentAppointmentDitailsBinding?.etPersonViewValue?.setSelection(memberList?.get(positionValue)?.memberName!!.length)
            editValidations()
        } catch (e: Exception) {

        }
    }
}