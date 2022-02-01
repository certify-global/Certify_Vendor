package com.certify.vendor.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.certify.vendor.R
import com.certify.vendor.adapter.AppointmentListAdapter
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.data.AppointmentDataSource
import com.certify.vendor.data.LoginDataSource
import com.certify.vendor.model.AppointmentViewModel

class AppointmentListFragment : BaseFragment() {

    private lateinit var appointmentViewModel: AppointmentViewModel
    private var recyclerView: RecyclerView? = null
    private var llNoAppointment: LinearLayout? = null
    private var llUpcomingAppointment: LinearLayout? = null
    private var adapter: AppointmentListAdapter? = null
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_appointment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appointmentViewModel = ViewModelProviders.of(this@AppointmentListFragment)
            .get(AppointmentViewModel::class.java)
        sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
        baseViewModel = appointmentViewModel
        appointmentViewModel.init(context)
        appointmentViewModel.getAppointments(AppSharedPreferences.readInt(sharedPreferences, Constants.VENDOR_ID))
        initView()
        initRecyclerView()
        setAppointmentListener()
    }

    private fun initView() {
        progressIndicator = view?.findViewById(R.id.progress_indicator)
        val userName: TextView? = view?.findViewById(R.id.appt_user_name)
        userName?.text =
            String.format(
                getString(R.string.appt_user_name),
                AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME)
            )
        val userPic: ImageView? = view?.findViewById(R.id.user_profile_pic)
        val userPicStr =
            AppSharedPreferences.readString(sharedPreferences, Constants.USER_PROFILE_PIC)
        if (userPicStr.isNotEmpty()) {
            userPic?.setImageBitmap(Utils.decodeBase64ToImage(userPicStr))
        }
    }

    private fun initRecyclerView() {
        recyclerView = view?.findViewById(R.id.appt_recycler_view)
        llNoAppointment = view?.findViewById(R.id.ll_no_appointment)
        llUpcomingAppointment = view?.findViewById(R.id.upcoming_appointment_layout)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = AppointmentListAdapter(context, AppointmentDataSource.getAppointmentList())
        recyclerView?.adapter = adapter
    }

    private fun setAppointmentListener() {
        appointmentViewModel.appointmentLiveData.observe(viewLifecycleOwner, {
            if (it && AppointmentDataSource.getAppointmentList().isNotEmpty()) {
                adapter?.updateAppointmentList(AppointmentDataSource.getAppointmentList())
                recyclerView?.adapter?.notifyDataSetChanged()
                llNoAppointment?.visibility = View.GONE
                recyclerView?.visibility = View.VISIBLE
                llUpcomingAppointment?.visibility = View.VISIBLE

            } else {
                recyclerView?.visibility = View.GONE
                llUpcomingAppointment?.visibility = View.GONE
                llNoAppointment?.visibility = View.VISIBLE
            }
        })
    }
}