package com.certify.vendor.activity

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private var llNoAppointment: LinearLayout? = null
    private var adapter: AppointmentListAdapter? = null
    private var sharedPreferences: SharedPreferences? = null
    private var pDialog: Dialog? = null
    private lateinit var upcomingAppointView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        upcomingAppointView = inflater.inflate(R.layout.fragment_up_coming, container, false)
        return upcomingAppointView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        appointmentViewModel = ViewModelProvider(this)
            .get(AppointmentViewModel::class.java)
        pDialog?.show()
        appointmentViewModel.init(context)
        sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
        appointmentViewModel.getPastAppointments()
        setAppointmentListener()

    }
    private fun initView() {
       pDialog = Utils.ShowProgressDialog(requireContext())
        recyclerView = upcomingAppointView.findViewById(R.id.upcoming_recycler_view)
        llNoAppointment = upcomingAppointView.findViewById(R.id.ll_no_appointment)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = AppointmentListAdapter(context, this, AppointmentDataSource.getPostAppointmentList(),"Past")
        recyclerView?.adapter = adapter
    }


    private fun setAppointmentListener() {
        appointmentViewModel.pastAppointmentLiveData.observe(viewLifecycleOwner) {
            pDialog?.dismiss()
            if (AppointmentDataSource.getUnauthorized() == 401) {
                Toast.makeText(context, context?.getString(R.string.session_timeout), Toast.LENGTH_LONG).show()
                Utils.logOut(context)
                return@observe
            }
            if (it.isNotEmpty()) {
                adapter?.updateAppointmentList(it)
                recyclerView?.adapter?.notifyDataSetChanged()
                llNoAppointment?.visibility = View.GONE
                recyclerView?.visibility = View.VISIBLE
            } else {
                recyclerView?.visibility = View.GONE
                llNoAppointment?.visibility = View.VISIBLE

            }
        }

    }

    override fun onAppointmentCheckIn(value: AppointmentData) {
        TODO("Not yet implemented")
    }
}