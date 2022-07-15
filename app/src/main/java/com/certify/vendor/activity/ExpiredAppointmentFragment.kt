package com.certify.vendor.activity

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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

class ExpiredAppointmentFragment : Fragment(), AppointmentCheckIn {
    private lateinit var appointmentViewModel: AppointmentViewModel
    private var recyclerView: RecyclerView? = null
    private var tvNoOAppointment: TextView? = null
    private var adapter: AppointmentListAdapter? = null
    private var sharedPreferences: SharedPreferences? = null
    private var pDialog: Dialog? = null
    private lateinit var expiredAppointmentView: View
    private var swipeRefreshLayoutExpired: SwipeRefreshLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        expiredAppointmentView = inflater.inflate(R.layout.fragment_post_expired_appointment, container, false)
        return expiredAppointmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        appointmentViewModel = ViewModelProvider(this)
            .get(AppointmentViewModel::class.java)
        pDialog?.show()
        appointmentViewModel.init(context)
        appointmentViewModel.getExpiredAppointments()
        setAppointmentListener()
    }

    private fun initView() {
        sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
        pDialog = Utils.ShowProgressDialog(requireContext())
        recyclerView = expiredAppointmentView.findViewById(R.id.post_expired_recycler_view)
        swipeRefreshLayoutExpired = expiredAppointmentView.findViewById(R.id.swipeContainer_post)
        tvNoOAppointment = expiredAppointmentView.findViewById(R.id.tv_no_appointment)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = AppointmentListAdapter(requireContext(), this, AppointmentDataSource.getExpiredAppointmentList(), Constants.AppointmentTypes.EXPIRED.name, sharedPreferences!!)
        recyclerView?.adapter = adapter
        swipeRefreshLayoutExpired?.setOnRefreshListener { appointmentViewModel.getExpiredAppointments() }
    }


    private fun setAppointmentListener() {
        appointmentViewModel.expiredAppointmentLiveData.observe(viewLifecycleOwner) {
            pDialog?.dismiss()
            swipeRefreshLayoutExpired?.isRefreshing = false
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
        AppointmentDataSource.appointmentType = Constants.AppointmentTypes.EXPIRED.name
        startActivity(Intent(activity, ScheduleActivity::class.java))
    }
}