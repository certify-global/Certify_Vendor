package com.certify.vendor.activity

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.certify.vendor.R
import com.certify.vendor.adapter.MemberListAdapter
import com.certify.vendor.api.response.DepartmentforfacilityList
import com.certify.vendor.api.response.FacilityData
import com.certify.vendor.api.response.LocationforfacilityList
import com.certify.vendor.api.response.ResponseDataMember
import com.certify.vendor.callback.ItemOnClickCallback
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.data.FacilityDataSource
import com.certify.vendor.databinding.*
import com.certify.vendor.model.FacilityViewModel
import com.certify.vendor.model.ScheduleAppointmentViewModel
import java.util.*


class ScheduleAppoinmentFragment : Fragment(), ItemOnClickCallback {
    private val TAG = ScheduleAppoinmentFragment::class.java.name
    private var sharedPreferences: SharedPreferences? = null
    private var scheduleAppointmentViewModel: ScheduleAppointmentViewModel? = null
    var fragmentBinding: ScheduleAppoinmentLayoutBinding? = null
    var fragmentMainLayoutBinding: MainLayoutBinding? = null
    var calendarLayoutBinding: CalendarLayoutBinding? = null
    private var facilityViewModel: FacilityViewModel? = null
    var submitLayoutBinding: SubmitLayoutBinding? = null
    var successLayoutBinding: SuccessLayoutBinding? = null
    var mTimePicker: TimePickerDialog? = null
    val mcurrentTime = Calendar.getInstance()
    val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
    val minute = mcurrentTime.get(Calendar.MINUTE)
    private lateinit var adapter: ArrayAdapter<FacilityData>
    var selectedDate: String = ""
    var startTime: String = ""
    var endTime: String = ""
    var locationId: Int = 0
    var departmentId: Int = 0
    var contactMemberId: Int = 0

    var facilityData: FacilityData? = null
    private var pDialog: Dialog? = null
    private var memberList: List<ResponseDataMember>? = ArrayList()
    private var adapterMemberList: MemberListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.schedule_appoinment_layout, container, false)
        fragmentMainLayoutBinding = fragmentBinding?.facilityLayout
        calendarLayoutBinding = fragmentBinding?.facilityCalendarLayout
        submitLayoutBinding = fragmentBinding?.appoinmentSubmitLayout
        successLayoutBinding = fragmentBinding?.appoinmentSuccessLayout
        facilityViewModel = ViewModelProvider(this).get(FacilityViewModel::class.java)
        scheduleAppointmentViewModel =
            ViewModelProvider(this).get(ScheduleAppointmentViewModel::class.java)
        // baseViewModel = facilityViewModel
        //   baseViewModel = scheduleAppointmentViewModel
        return fragmentBinding?.root?.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().getWindow()
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initView();
        pDialog?.show()
        facilityViewModel?.facility(
            AppSharedPreferences.readInt(
                sharedPreferences,
                Constants.VENDOR_ID
            )
        )
        setOnClickListener()
        setFacilityListener()
        setOnBackPress()
    }

    private fun initView() {
        sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
        facilityViewModel?.init(context)
        calendarLayoutBinding?.textClockStart?.setText(Utils.getCurrentTime())
        calendarLayoutBinding?.textClockEnd?.setText(Utils.getCurrentTime())
        selectedDate = Utils.getCurrentDate()
        startTime = Utils.getCurrentTime24()
        endTime = Utils.getCurrentTime24()
        pDialog = Utils.ShowProgressDialog(requireContext())
        submitLayoutBinding?.recMemberList?.layoutManager = LinearLayoutManager(context)
        submitLayoutBinding?.recMemberList?.addItemDecoration(
            DividerItemDecoration(
                submitLayoutBinding?.recMemberList?.context,
                DividerItemDecoration.VERTICAL
            )
        )
        adapterMemberList = MemberListAdapter(requireContext(), this, memberList!!)
        submitLayoutBinding?.recMemberList?.adapter = adapterMemberList
//        var mBottomNavigationView:BottomNavigationView = (requireActivity().findViewById<View>(R.id.navigation_menu_view) as BottomNavigationView)
//        mBottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked()

    }

    private fun setOnClickListener() {
        calendarLayoutBinding?.textClockStart?.setOnClickListener {
            mTimePicker = TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minute ->
                    if (hourOfDay >=  mcurrentTime.get(Calendar.HOUR_OF_DAY) && minute >= mcurrentTime.get(Calendar.MINUTE)) {
                        val minute = if (minute < 10) "0$minute" else minute.toString()
                        startTime = String.format("%d", hourOfDay) + ":" + minute
                        calendarLayoutBinding?.textClockStart?.setText(Utils.getTime24to12(startTime))
                    } else {
                        Toast.makeText(
                            context,
                            context?.getString(R.string.invalid_time),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }, hour, minute, false
            )
            mTimePicker?.show()
        }
        calendarLayoutBinding?.textClockEnd?.setOnClickListener {
            mTimePicker = TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minute ->
                    if (hourOfDay >=  mcurrentTime.get(Calendar.HOUR_OF_DAY) && minute >= mcurrentTime.get(Calendar.MINUTE)) {
                        val minute = if (minute < 10) "0$minute" else minute.toString()
                        endTime = String.format("%d", hourOfDay) + ":" + minute
                        calendarLayoutBinding?.textClockEnd?.setText(Utils.getTime24to12(endTime))
                    } else {
                        Toast.makeText(
                            context,
                            context?.getString(R.string.invalid_time),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, hour, minute, false
            )
            mTimePicker?.show()
        }
        calendarLayoutBinding?.buttonBack?.setOnClickListener {
            fragmentMainLayoutBinding?.constraintLayoutFacility?.setVisibility(View.VISIBLE)
            calendarLayoutBinding?.constraintLayoutCalendar?.setVisibility(View.GONE)
            submitLayoutBinding?.constraintSubmitLayout?.setVisibility(View.GONE)
            successLayoutBinding?.constraintLayoutSuccess?.setVisibility(View.GONE)
        }
        fragmentMainLayoutBinding?.tvFacilityNext?.setOnClickListener {
            if (FacilityDataSource.getFacilityList().size > 1 && facilityData != null)
                updateUI(facilityData!!)
            else
                Utils.basicAlert(context, resources.getString(R.string.select_facility))
        }
        calendarLayoutBinding?.buttonNext?.setOnClickListener {
            if (Utils.isTimeBigger(startTime, endTime)) {
                launchAppoinmentSchedulePage()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Start time should be greater than the system Time",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        successLayoutBinding?.buttonGotoAppoinment?.setOnClickListener {
            launchAppointmentActivity()
        }
        calendarLayoutBinding?.calendarViewFacility?.setMinDate(Date().time)
        calendarLayoutBinding?.calendarViewFacility?.setOnDateChangeListener { calendarView, year, month, dayOfmonth ->
            val month = month + 1
            val monthStr = if (month < 10) "0$month" else month.toString()
            val dayStr = if (dayOfmonth < 10) "0$dayOfmonth" else dayOfmonth.toString()
            selectedDate = "$year-$monthStr-$dayStr"

        }
        submitLayoutBinding?.reSchedule?.setOnClickListener {
            launchCalendarLayout()
        }
        submitLayoutBinding?.editTextTextPersonName?.addTextChangedListener {
            if (it.toString().length > 2) {
                facilityViewModel?.getOnSiteContact(facilityData?.facilityId!!, it.toString())
            } else {
                validationSubmitButton()
            }
        }
        submitLayoutBinding?.editTextVisit?.addTextChangedListener {
            validationSubmitButton()
        }
        submitLayoutBinding?.buttonCancel?.setOnClickListener { activity?.finish() }
        submitLayoutBinding?.buttonSubmit?.setOnClickListener {
            var visitReason = submitLayoutBinding?.editTextVisit?.text.toString()
            if (validateVisitPersonName()) {
                pDialog?.show()
                scheduleAppointmentViewModel?.scheduleAppointments(
                    selectedDate,
                    startTime,
                    endTime,
                    contactMemberId,
                    visitReason,
                    facilityData!!.facilityId,
                    locationId,
                    departmentId
                )
            }
        }
    }

    private fun initSpinner() {
        val customObjects = FacilityDataSource.getFacilityList()
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, customObjects)
        fragmentMainLayoutBinding?.spinnerFacility?.adapter = adapter
        fragmentMainLayoutBinding?.spinnerFacility?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                FacilityDataSource.addLocationForFacilityList(ArrayList())
                FacilityDataSource.addDepartmentList(ArrayList())
                facilityData = parent.getSelectedItem() as FacilityData
                departmentId = 0
                locationId = 0
                contactMemberId = 0;
                if (position == 0) return
                if (pDialog != null && pDialog?.isShowing == false)
                    pDialog?.show()
                facilityViewModel?.departmentLocationWith(facilityData?.facilityId!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

    }

    private fun initSpinnerLocation() {
        val customObjects = FacilityDataSource.getLocationForFacilityList()
        val adapterLoc =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, customObjects)
        fragmentMainLayoutBinding?.spinnerLocation?.adapter = adapterLoc
        fragmentMainLayoutBinding?.spinnerLocation?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == 0) return
                val temp = parent.getSelectedItem() as LocationforfacilityList
                locationId = temp.locationId
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

    }

    private fun initSpinnerDepartment() {
        val customObjects = FacilityDataSource.getDepartmentList()
        val adapterDepartment =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, customObjects)
        fragmentMainLayoutBinding?.spinnerDepartment?.adapter = adapterDepartment
        fragmentMainLayoutBinding?.spinnerDepartment?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == 0) return
                val departmentTemp = parent.getSelectedItem() as DepartmentforfacilityList
                departmentId = departmentTemp.departmentId
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

    }

    private fun updateUI(myModel: FacilityData) {

        if (!myModel.facilityName.equals("Select Facility") && myModel.incompleteRequirementCount == 0) {
            launchCalendarLayout();
        } else if (myModel.incompleteRequirementCount != 0) {
            basicAlert()
        } else {
            launchMainLayout()

        }
    }

    private fun setFacilityListener() {
        facilityViewModel?.facilityLiveData?.observe(viewLifecycleOwner) {
            pDialog?.dismiss()
            if (it && FacilityDataSource.getFacilityList().isNotEmpty()) {
                initSpinner()
            } else {
                Toast.makeText(
                    requireContext(), "Facility not found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        facilityViewModel?.departmentLocationWithLiveData?.observe(viewLifecycleOwner) {
            pDialog?.dismiss()
            if (it == null) return@observe
            if (it.responseData?.locationforfacilityList?.size!! > 0) {
                fragmentBinding?.facilityLayout?.spinnerLocation?.visibility = View.VISIBLE
                fragmentBinding?.facilityLayout?.imgLocation?.visibility = View.VISIBLE
                FacilityDataSource.addLocationForFacilityList(it.responseData?.locationforfacilityList)
                initSpinnerLocation()
            } else {
                fragmentBinding?.facilityLayout?.spinnerLocation?.visibility = View.GONE
                fragmentBinding?.facilityLayout?.imgLocation?.visibility = View.GONE
            }

            if (it.responseData.departmentforfacilityList.size > 0) {
                fragmentBinding?.facilityLayout?.spinnerDepartment?.visibility = View.VISIBLE
                fragmentBinding?.facilityLayout?.imgDepartment?.visibility = View.VISIBLE
                FacilityDataSource.addDepartmentList(it.responseData.departmentforfacilityList)
                initSpinnerDepartment()
            } else {
                fragmentBinding?.facilityLayout?.spinnerDepartment?.visibility = View.GONE
                fragmentBinding?.facilityLayout?.imgDepartment?.visibility = View.GONE
            }
//            if (FacilityDataSource.getLocationForFacilityList().size == 0 && FacilityDataSource.getDepartmentList().size == 0) {
//                updateUI(facilityData!!)
//            }
        }
        facilityViewModel?.facilityMembersRequestWithLiveData?.observe(viewLifecycleOwner) {
            if (it.responseCode == 1 && !it.responseData.isEmpty()) {
                Utils.hideKeyboard(requireActivity())
                memberList = it.responseData
                adapterMemberList?.updateMemberList(memberList!!)
                submitLayoutBinding?.recMemberList?.visibility = View.VISIBLE
            } else submitLayoutBinding?.recMemberList?.visibility = View.GONE

        }
        scheduleAppointmentViewModel?.scheduleAppointmentResponseLiveData?.observe(
            viewLifecycleOwner
        ) {
            pDialog?.dismiss()
            if (it == null) {
                Toast.makeText(
                    requireContext(),
                    "Something went wrong with schedule appoinment",
                    Toast.LENGTH_SHORT
                ).show()
                return@observe
            }
            if (it.responseCode == 1) {
                launchSuccesPage();
            } else if (it.responseMessage.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Something went wrong with schedule appoinment",
                    Toast.LENGTH_SHORT
                ).show()
            } else Toast.makeText(requireContext(), it.responseMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchSuccesPage() {
        fragmentMainLayoutBinding?.constraintLayoutFacility?.setVisibility(View.GONE)
        calendarLayoutBinding?.constraintLayoutCalendar?.setVisibility(View.GONE)
        submitLayoutBinding?.constraintSubmitLayout?.setVisibility(View.GONE)
        successLayoutBinding?.constraintLayoutSuccess?.setVisibility(View.VISIBLE)
        successLayoutBinding?.textViewConfirmation?.setText(
            resources.getString(R.string.appoinment_confirmation) + " " + facilityData?.facilityName + " " +
                    Utils.getmonthstring(selectedDate) + " at " + Utils.getTime24to12(startTime)
        )
    }

    private fun launchAppoinmentSchedulePage() {
        fragmentMainLayoutBinding?.constraintLayoutFacility?.setVisibility(View.GONE)
        calendarLayoutBinding?.constraintLayoutCalendar?.setVisibility(View.GONE)
        submitLayoutBinding?.constraintSubmitLayout?.setVisibility(View.VISIBLE)
        successLayoutBinding?.constraintLayoutSuccess?.setVisibility(View.GONE)
        submitLayoutBinding?.appointmentDate?.text = Utils.getmonthstring(selectedDate)
        submitLayoutBinding?.appointmentTime?.text = startTime + "-" + endTime
        submitLayoutBinding?.appointmentPlace?.text =
            resources.getString(R.string.appoinment) + " " + facilityData?.facilityName
        submitLayoutBinding?.appointmentLocation?.text =
            facilityData?.facilityName + "," + facilityData?.streetAddress + "," + facilityData?.city + "," + facilityData?.state + " " + facilityData?.zip

    }

    private fun launchMainLayout() {
        fragmentMainLayoutBinding?.constraintLayoutFacility?.setVisibility(View.VISIBLE)
        calendarLayoutBinding?.constraintLayoutCalendar?.setVisibility(View.GONE)
        submitLayoutBinding?.constraintSubmitLayout?.setVisibility(View.GONE)
        successLayoutBinding?.constraintLayoutSuccess?.setVisibility(View.GONE)
    }

    private fun launchCalendarLayout() {
        calendarLayoutBinding?.constraintLayoutCalendar?.setVisibility(View.VISIBLE)
        fragmentMainLayoutBinding?.constraintLayoutFacility?.setVisibility(View.GONE)
        submitLayoutBinding?.constraintSubmitLayout?.setVisibility(View.GONE)
        successLayoutBinding?.constraintLayoutSuccess?.setVisibility(View.GONE)
    }


    fun launchAppointmentActivity() {
        activity?.finish()
        //startActivity(Intent(requireContext(), AppointmentActivity::class.java))
    }

    fun basicAlert() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage(resources.getString(R.string.incomplete_credentials))
            .setCancelable(false)
            .setPositiveButton("Cancel") { dialog, id ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }

    private fun validateVisitPersonName(): Boolean {
        if (submitLayoutBinding?.editTextVisit?.getText().toString().isEmpty()) {
            submitLayoutBinding?.editTextVisit?.setError(resources.getString(R.string.please_enter_reason))
            return false
        }
        if (submitLayoutBinding?.editTextTextPersonName?.getText().toString().isEmpty()) {
            submitLayoutBinding?.editTextTextPersonName?.setError(resources.getString(R.string.please_enter_person))
            return false
        }
        return true
    }

    private fun setOnBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override
                fun handleOnBackPressed() {
                    activity?.finish()
                }
            })
    }

    override fun onItemOnClickCallBack(positionValue: Int) {
        try {
            contactMemberId = memberList?.get(positionValue)?.individualId!!
            submitLayoutBinding?.editTextTextPersonName?.setText(memberList?.get(positionValue)?.memberName!!)
            submitLayoutBinding?.editTextTextPersonName?.setSelection(memberList?.get(positionValue)?.memberName!!.length)
            validationSubmitButton()
        } catch (e: Exception) {

        }
    }

    fun validationSubmitButton() {
        try {
            if (contactMemberId == 0 || submitLayoutBinding?.editTextVisit?.text!!.isEmpty() || submitLayoutBinding?.editTextTextPersonName?.text!!.isEmpty()) {
                submitLayoutBinding?.buttonSubmit?.alpha = .7f
                submitLayoutBinding?.buttonSubmit?.isEnabled = false
                submitLayoutBinding?.recMemberList?.visibility = View.GONE
            } else {
                submitLayoutBinding?.buttonSubmit?.alpha = 1f
                submitLayoutBinding?.buttonSubmit?.isEnabled = true
            }
        } catch (e: Exception) {

        }
    }
}