package com.certify.vendor.activity

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.certify.vendor.R
import com.certify.vendor.api.response.FacilityData
import com.certify.vendor.common.Constants
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.data.AppointmentDataSource
import com.certify.vendor.data.FacilityDataSource
import com.certify.vendor.databinding.*
import com.certify.vendor.model.AppointmentViewModel
import com.certify.vendor.model.FacilityViewModel
import com.certify.vendor.model.ScheduleAppointmentViewModel
import java.text.SimpleDateFormat
import java.util.*


class ScheduleAppoinmentFragment : BaseFragment() {
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
    var facilityName: FacilityData?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.schedule_appoinment_layout, container, false
        )
        fragmentMainLayoutBinding = fragmentBinding?.facilityLayout
        calendarLayoutBinding = fragmentBinding?.facilityCalendarLayout
        submitLayoutBinding = fragmentBinding?.appoinmentSubmitLayout
        successLayoutBinding = fragmentBinding?.appoinmentSuccessLayout
        facilityViewModel = ViewModelProviders.of(this@ScheduleAppoinmentFragment)
            .get(FacilityViewModel::class.java)
        scheduleAppointmentViewModel = ViewModelProviders.of(this@ScheduleAppoinmentFragment)
            .get(ScheduleAppointmentViewModel::class.java)
        baseViewModel = facilityViewModel
        baseViewModel = scheduleAppointmentViewModel
        return fragmentBinding?.root?.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView();
        facilityViewModel?.facility(AppSharedPreferences.readInt(sharedPreferences,Constants.VENDOR_ID))
        setOnClickListener()
        setFacilityListener()
    }

    private fun initView() {
        progressIndicator = view?.findViewById(R.id.progress_indicator)
        sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
        facilityViewModel?.init(context)

    }
    private fun setOnClickListener() {
        calendarLayoutBinding?.textClockStart?.setOnClickListener {
            mTimePicker = TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minute ->
                    val minute = if (minute < 10) "0$minute" else minute.toString()
                    startTime= String.format("%d", hourOfDay)+":"+minute
                    calendarLayoutBinding?.textClockStart?.setText(startTime)
                    Toast.makeText(
                        requireContext(),
                       startTime,
                        Toast.LENGTH_SHORT
                    ).show()
                }, hour, minute, false
            )
            mTimePicker?.show()
        }
        calendarLayoutBinding?.textClockEnd?.setOnClickListener {
            mTimePicker = TimePickerDialog(
                        requireContext(),
                { view, hourOfDay, minute ->
                    val minute = if (minute < 10) "0$minute" else minute.toString()
                    endTime= String.format("%d", hourOfDay)+":"+minute
                    calendarLayoutBinding?.textClockEnd?.setText(endTime)
                    Toast.makeText(
                        requireContext(),
                       endTime,
                        Toast.LENGTH_SHORT
                    ).show()
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
        calendarLayoutBinding?.buttonNext?.setOnClickListener {
           launchAppoinmentSchedulePage()
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
            Toast.makeText(
                requireContext(),
                selectedDate,
                Toast.LENGTH_SHORT
            ).show()
        }
        submitLayoutBinding?.buttonSubmit?.setOnClickListener {
            var contactName: String=  submitLayoutBinding?.editTextTextPersonName?.text.toString()
            var visitReason=  submitLayoutBinding?.editTextVisit?.text.toString()
            scheduleAppointmentViewModel?.scheduleAppointments(AppSharedPreferences.readInt(sharedPreferences,Constants.VENDOR_ID),selectedDate
            ,startTime,endTime, contactName,visitReason)

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
                view: View, position: Int, id: Long
            ) {
              facilityName = parent.getSelectedItem() as FacilityData
                updateUI(facilityName!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

    }

    private fun updateUI(myModel: FacilityData) {
        if (!myModel.facilityName.equals("Select Facility") && myModel.incompleteRequirementCount ==0) {
            calendarLayoutBinding?.constraintLayoutCalendar?.setVisibility(View.VISIBLE)
            fragmentMainLayoutBinding?.constraintLayoutFacility?.setVisibility(View.GONE)
            submitLayoutBinding?.constraintSubmitLayout?.setVisibility(View.GONE)
            successLayoutBinding?.constraintLayoutSuccess?.setVisibility(View.GONE)
        }else if(myModel.incompleteRequirementCount !=0) {
                 basicAlert()
        }else {
            fragmentMainLayoutBinding?.constraintLayoutFacility?.setVisibility(View.VISIBLE)
            calendarLayoutBinding?.constraintLayoutCalendar?.setVisibility(View.GONE)
            submitLayoutBinding?.constraintSubmitLayout?.setVisibility(View.GONE)
            successLayoutBinding?.constraintLayoutSuccess?.setVisibility(View.GONE)

        }
    }

    private fun setFacilityListener() {
        facilityViewModel?.facilityLiveData?.observe(viewLifecycleOwner, {
            if (it && FacilityDataSource.getFacilityList().isNotEmpty()) {
                initSpinner()
            } else {
                Toast.makeText(
                    requireContext(), "Facility not found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        scheduleAppointmentViewModel?.scheduleAppointmentLiveData?.observe(viewLifecycleOwner,{
            if(it){
                launchSuccesPage();
            }else{
                Toast.makeText(
                    requireContext(), "Something went wrong with schedule appoinment",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun launchSuccesPage() {
        fragmentMainLayoutBinding?.constraintLayoutFacility?.setVisibility(View.GONE)
        calendarLayoutBinding?.constraintLayoutCalendar?.setVisibility(View.GONE)
        submitLayoutBinding?.constraintSubmitLayout?.setVisibility(View.GONE)
        successLayoutBinding?.constraintLayoutSuccess?.setVisibility(View.VISIBLE)
    }

    private fun launchAppoinmentSchedulePage() {
        fragmentMainLayoutBinding?.constraintLayoutFacility?.setVisibility(View.GONE)
        calendarLayoutBinding?.constraintLayoutCalendar?.setVisibility(View.GONE)
        submitLayoutBinding?.constraintSubmitLayout?.setVisibility(View.VISIBLE)
        successLayoutBinding?.constraintLayoutSuccess?.setVisibility(View.GONE)
        submitLayoutBinding?.appointmentDate?.text=selectedDate
        submitLayoutBinding?.appointmentTime?.text=startTime+"-"+endTime
        submitLayoutBinding?.appointmentPlace?.text=resources.getString(R.string.appoinment)+" "+facilityName?.facilityName
        submitLayoutBinding?.appointmentLocation?.text=facilityName.toString()+","+ facilityName?.streetAddress+"\n"
        ""+facilityName?.city+","+facilityName?.state+","+facilityName?.zip

        submitLayoutBinding?.appointmentDate?.text=selectedDate

    }

    fun launchAppointmentActivity() {
        startActivity(Intent(requireContext(), AppointmentActivity::class.java))
    }

    fun basicAlert(){
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage(resources.getString(R.string.incomplete_credentials))
            .setCancelable(false)
            .setPositiveButton("Cancel") { dialog, id ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }

}