package com.certify.vendor.activity

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
import com.certify.vendor.common.Constants
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.data.FacilityDataSource
import com.certify.vendor.databinding.MainLayoutBinding
import com.certify.vendor.databinding.ScheduleAppoinmentLayoutBinding
import com.certify.vendor.model.FacilityViewModel


class ScheduleAppoinmentFragment : BaseFragment(){
    private val TAG = ScheduleAppoinmentFragment::class.java.name
    private var sharedPreferences: SharedPreferences? = null
    var fragmentBinding: ScheduleAppoinmentLayoutBinding? = null
    var fragmentMainLayoutBinding: MainLayoutBinding? = null
    private var facilityViewModel : FacilityViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = DataBindingUtil.inflate(inflater,
            R.layout.schedule_appoinment_layout, container, false)
        fragmentMainLayoutBinding=fragmentBinding?.facilityLayout
        facilityViewModel = ViewModelProviders.of(this@ScheduleAppoinmentFragment).get(FacilityViewModel::class.java)
        baseViewModel = facilityViewModel
        return fragmentBinding?.root?.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
        facilityViewModel?.init(context)
        facilityViewModel?.facility(AppSharedPreferences.readInt(sharedPreferences, Constants.VENDOR_ID))
        initSpinner()

    }

    private fun initSpinner() {
      fragmentMainLayoutBinding?.spinnerFacility?.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, FacilityDataSource.getFacilityList())

       fragmentMainLayoutBinding?.spinnerFacility?.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    val myModel: FacilityData = parent.getSelectedItem() as FacilityData
                    Toast.makeText(requireContext(),
                         " selected item" +
                                "" +myModel.facilityId+myModel.facilityName, Toast.LENGTH_SHORT).show()
                    updateUI(myModel)

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }

    }

    private fun updateUI(myModel:FacilityData) {
        if(!myModel.facilityName.equals("Select Facility")){
          fragmentMainLayoutBinding?.constraintLayoutFacility?.setVisibility(View.GONE)
        }else{
            fragmentMainLayoutBinding?.constraintLayoutFacility?.setVisibility(View.VISIBLE)

        }
    }

}