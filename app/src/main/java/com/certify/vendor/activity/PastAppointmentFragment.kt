package com.certify.vendor.activity

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.certify.vendor.R
import com.certify.vendor.adapter.AppointmentListAdapter
import com.certify.vendor.api.response.AppointmentData
import com.certify.vendor.callback.AppointmentCheckIn
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.data.AppointmentDataSource
import com.certify.vendor.model.AppointmentViewModel

class PastAppointmentFragment : Fragment(), AppointmentCheckIn {
    private lateinit var appointmentViewModel: AppointmentViewModel
    private var recyclerView: RecyclerView? = null
    private var tvNoOAppointment: TextView? = null
    private var adapter: AppointmentListAdapter? = null
    private var sharedPreferences: SharedPreferences? = null
    private var pDialog: Dialog? = null
    private lateinit var upcomingAppointView: View
    private var swipeRefreshLayoutPost: SwipeRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        upcomingAppointView = inflater.inflate(R.layout.fragment_post_expired_appointment, container, false)
        return upcomingAppointView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        appointmentViewModel = ViewModelProvider(this)
            .get(AppointmentViewModel::class.java)
        pDialog?.show()
        appointmentViewModel.init(context)

        appointmentViewModel.getPastAppointments()
        setAppointmentListener()

    }

    private fun initView() {
        sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
        pDialog = Utils.ShowProgressDialog(requireContext())
        recyclerView = upcomingAppointView.findViewById(R.id.post_expired_recycler_view)
        swipeRefreshLayoutPost = upcomingAppointView.findViewById(R.id.swipeContainer_post)
        tvNoOAppointment = upcomingAppointView.findViewById(R.id.tv_no_appointment)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = AppointmentListAdapter(requireContext(), this, AppointmentDataSource.getPostAppointmentList(), Constants.AppointmentTypes.PAST.name, sharedPreferences!!)
        recyclerView?.adapter = adapter
        swipeRefreshLayoutPost?.setOnRefreshListener {
            appointmentViewModel.getPastAppointments()
        }
    }


    private fun setAppointmentListener() {
        appointmentViewModel.pastAppointmentLiveData.observe(viewLifecycleOwner) {
            pDialog?.dismiss()
            swipeRefreshLayoutPost?.isRefreshing = false
            if (AppointmentDataSource.getUnauthorized() == 401) {
                Toast.makeText(context, context?.getString(R.string.session_timeout), Toast.LENGTH_LONG).show()
                Utils.logOut(context)
                return@observe
            }
            if (it.isNotEmpty()) {
                adapter?.updateAppointmentList(it)
                recyclerView?.adapter?.notifyDataSetChanged()
                tvNoOAppointment?.visibility = View.GONE
                recyclerView?.visibility = View.VISIBLE
            } else {
                recyclerView?.visibility = View.GONE
                tvNoOAppointment?.visibility = View.VISIBLE

            }
        }

    }

    override fun onAppointmentCheckIn(value: AppointmentData) {
        TODO("Not yet implemented")
    }

    override fun onAppointmentDetails(value: AppointmentData) {
        AppointmentDataSource.setAppointmentData(value)
        AppointmentDataSource.isSchedule = false
        AppointmentDataSource.appointmentType = Constants.AppointmentTypes.PAST.name
        startActivity(Intent(activity, ScheduleActivity::class.java))
    }
}