package com.certify.vendor.activity

import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.certify.vendor.R
import com.certify.vendor.api.response.FacilityData
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.data.FacilityDataSource
import com.certify.vendor.databinding.*
import com.certify.vendor.model.FacilityViewModel
import java.util.*


class ScheduleAppoinmentFragment : BaseFragment() {
    private val TAG = ScheduleAppoinmentFragment::class.java.name
    private var sharedPreferences: SharedPreferences? = null
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
        baseViewModel = facilityViewModel
        return fragmentBinding?.root?.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
        facilityViewModel?.init(context)
        // facilityViewModel?.facility(AppSharedPreferences.readInt(sharedPreferences, Constants.VENDOR_ID))
        FacilityDataSource.addFacilityList()
        initSpinner()
        setOnClickListener()
        timePicker()

    }

    private fun timePicker() {
        mTimePicker = TimePickerDialog(requireContext(),
            { view, hourOfDay, minute ->
                Toast.makeText(
                    requireContext(),
                    String.format("%d : %d", hourOfDay, minute),
                    Toast.LENGTH_SHORT
                ).show()
            }, hour, minute, false
        )
    }

    private fun setOnClickListener() {
        calendarLayoutBinding?.textClockStart?.setOnClickListener {
            mTimePicker?.show()
        }
        calendarLayoutBinding?.textClockEnd?.setOnClickListener {
            mTimePicker?.show()
        }
        calendarLayoutBinding?.buttonBack?.setOnClickListener {
            fragmentMainLayoutBinding?.constraintLayoutFacility?.setVisibility(View.VISIBLE)
            calendarLayoutBinding?.constraintLayoutCalendar?.setVisibility(View.GONE)
            submitLayoutBinding?.constraintSubmitLayout?.setVisibility(View.GONE)
            successLayoutBinding?.constraintLayoutSuccess?.setVisibility(View.GONE)
        }
        calendarLayoutBinding?.buttonNext?.setOnClickListener {
            fragmentMainLayoutBinding?.constraintLayoutFacility?.setVisibility(View.GONE)
            calendarLayoutBinding?.constraintLayoutCalendar?.setVisibility(View.GONE)
            submitLayoutBinding?.constraintSubmitLayout?.setVisibility(View.VISIBLE)
            successLayoutBinding?.constraintLayoutSuccess?.setVisibility(View.GONE)
        }
        submitLayoutBinding?.buttonSubmit?.setOnClickListener {
            fragmentMainLayoutBinding?.constraintLayoutFacility?.setVisibility(View.GONE)
            calendarLayoutBinding?.constraintLayoutCalendar?.setVisibility(View.GONE)
            submitLayoutBinding?.constraintSubmitLayout?.setVisibility(View.GONE)
            successLayoutBinding?.constraintLayoutSuccess?.setVisibility(View.VISIBLE)
        }

        successLayoutBinding?.buttonGotoAppoinment?.setOnClickListener {
           launchAppointmentActivity()
        }
    }
    fun launchAppointmentActivity() {
        startActivity(Intent(requireContext(), AppointmentActivity::class.java))
    }
    private fun initSpinner() {
        FacilityDataSource.getFacilityList()
        fragmentMainLayoutBinding?.spinnerFacility?.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            FacilityDataSource.getFacilityList()
        )

        fragmentMainLayoutBinding?.spinnerFacility?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                val myModel: FacilityData = parent.getSelectedItem() as FacilityData
                /* Toast.makeText(requireContext(),
                      " selected item" +
                             "" +myModel.facilityId+myModel.facilityName, Toast.LENGTH_SHORT).show()*/
                updateUI(myModel)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

    }

    private fun updateUI(myModel: FacilityData) {
        if (!myModel.facilityName.equals("Select Facility")) {
            calendarLayoutBinding?.constraintLayoutCalendar?.setVisibility(View.VISIBLE)
            fragmentMainLayoutBinding?.constraintLayoutFacility?.setVisibility(View.GONE)
            submitLayoutBinding?.constraintSubmitLayout?.setVisibility(View.GONE)
            successLayoutBinding?.constraintLayoutSuccess?.setVisibility(View.GONE)
        } else {
            fragmentMainLayoutBinding?.constraintLayoutFacility?.setVisibility(View.VISIBLE)
            calendarLayoutBinding?.constraintLayoutCalendar?.setVisibility(View.GONE)
            submitLayoutBinding?.constraintSubmitLayout?.setVisibility(View.GONE)
            successLayoutBinding?.constraintLayoutSuccess?.setVisibility(View.GONE)

        }
    }

}