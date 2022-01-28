package com.certify.vendor.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.certify.vendor.R
import com.certify.vendor.adapter.AppointmentListAdapter
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppointmentDataSource
import com.certify.vendor.data.LoginDataSource
import com.certify.vendor.model.AppointmentViewModel

class AppointmentListFragment : BaseFragment() {

    private lateinit var appointmentViewModel : AppointmentViewModel
    private var recyclerView: RecyclerView? = null
    private var adapter : AppointmentListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_appointment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appointmentViewModel = ViewModelProviders.of(this@AppointmentListFragment).get(AppointmentViewModel::class.java)
        baseViewModel = appointmentViewModel
        appointmentViewModel.init(context)
        appointmentViewModel.getAppointments()
        initView()
        initRecyclerView()
        setAppointmentListener()
    }

    private fun initView() {
        progressIndicator = view?.findViewById(R.id.progress_indicator)
        val userName : TextView? = view?.findViewById(R.id.appt_user_name)
        userName?.text = String.format(getString(R.string.appt_user_name), LoginDataSource.loginData?.firstName)
        val userPic : ImageView? = view?.findViewById(R.id.user_profile_pic)
        if (LoginDataSource.userProfilePicEncoded?.isNotEmpty() == true) {
            userPic?.setImageBitmap(Utils.decodeBase64ToImage(LoginDataSource.userProfilePicEncoded))
        }
    }

    private fun initRecyclerView() {
        recyclerView = view?.findViewById(R.id.appt_recycler_view)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = AppointmentListAdapter(context, AppointmentDataSource.getAppointmentList())
        recyclerView?.adapter = adapter
    }

    private fun setAppointmentListener() {
        appointmentViewModel.appointmentLiveData.observe(viewLifecycleOwner, {
            if (it && AppointmentDataSource.getAppointmentList().isNotEmpty()) {
                adapter?.updateAppointmentList(AppointmentDataSource.getAppointmentList())
                recyclerView?.adapter?.notifyDataSetChanged()
            } else {
                Toast.makeText(context, getString(R.string.no_appointments), Toast.LENGTH_LONG).show()
            }
        })
    }
}