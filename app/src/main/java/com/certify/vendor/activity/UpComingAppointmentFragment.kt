package com.certify.vendor.activity

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.certify.vendor.R
import com.certify.vendor.adapter.AppointmentListAdapter
import com.certify.vendor.api.response.AppointmentData
import com.certify.vendor.badge.BadgeController
import com.certify.vendor.callback.AppointmentCheckIn
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.data.AppointmentDataSource
import com.certify.vendor.model.AppointmentViewModel
import com.certify.vendor.model.BadgeViewModel
import com.certify.vendor.model.UpdateAppointmentViewModel

class UpComingAppointmentFragment : Fragment(), AppointmentCheckIn {
    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var updateAppointmentViewModel: UpdateAppointmentViewModel
    private lateinit var badgeViewModel: BadgeViewModel

    private var recyclerView: RecyclerView? = null
    private var llNoAppointment: LinearLayout? = null
    private var adapter: AppointmentListAdapter? = null
    private var sharedPreferences: SharedPreferences? = null
    private var pDialog: Dialog? = null
    private lateinit var upcomingAppointView: View
    private var textviewscheduleAppoinment: TextView? = null

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
        updateAppointmentViewModel = ViewModelProvider(this)
            .get(UpdateAppointmentViewModel::class.java)
        badgeViewModel = ViewModelProvider(this).get(BadgeViewModel::class.java)
        if (!pDialog!!.isShowing)
            pDialog?.show()
        appointmentViewModel.init(context)
        sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
        appointmentViewModel.getAppointments(AppSharedPreferences.readInt(sharedPreferences, Constants.VENDOR_ID))
        setAppointmentListener()
        setBadgeListener()
        updateAppointmentListener()
    }

    private fun initView() {
        pDialog = Utils.ShowProgressDialog(requireContext())
        recyclerView = upcomingAppointView.findViewById(R.id.upcoming_recycler_view)
        llNoAppointment = upcomingAppointView.findViewById(R.id.ll_no_appointment)
        textviewscheduleAppoinment = upcomingAppointView.findViewById(R.id.textview_scheduleAppoinment)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = AppointmentListAdapter(requireContext(), this, AppointmentDataSource.getAppointmentList(), "UpComing")
        recyclerView?.adapter = adapter
        textviewscheduleAppoinment?.setOnClickListener {
            findNavController().navigate(R.id.scheduleFragment)
        }

    }


    private fun setAppointmentListener() {
        appointmentViewModel.appointmentLiveData.observe(viewLifecycleOwner) {
            pDialog?.dismiss()
            if (AppointmentDataSource.getUnauthorized() == 401) {
                Toast.makeText(context, context?.getString(R.string.session_timeout), Toast.LENGTH_LONG).show()
                Utils.logOut(context)
                return@observe
            }
            if (it && AppointmentDataSource.getAppointmentList().isNotEmpty()) {
                adapter?.updateAppointmentList(AppointmentDataSource.getAppointmentList())
                recyclerView?.adapter?.notifyDataSetChanged()
                llNoAppointment?.visibility = View.GONE
                recyclerView?.visibility = View.VISIBLE
            } else {
                recyclerView?.visibility = View.GONE
                llNoAppointment?.visibility = View.VISIBLE

            }
        }

    }

    private fun setBadgeUI(statDate: String, endDate: String, appointmentStatus: Int, vendorGuid: String) = try {
        val badgeUILayout: ConstraintLayout = upcomingAppointView.findViewById(R.id.badge_screen)
        val badgeLayout = upcomingAppointView.findViewById<LinearLayout>(R.id.rl_badge_status)
        val userImage: ImageView? = upcomingAppointView.findViewById(R.id.img_user_badge)
        val QRCodeImage: ImageView? = upcomingAppointView.findViewById(R.id.img_qa_badge)
        val companyName: TextView? = upcomingAppointView.findViewById(R.id.tv_company_name)
        val badgeId: TextView? = upcomingAppointView.findViewById(R.id.tv_id_badge)
        val userName: TextView? = upcomingAppointView.findViewById(R.id.tv_user_name_badge)
        val validity: TextView? = upcomingAppointView.findViewById(R.id.tv_expires_badge)
        val timeStamp: TextView? = upcomingAppointView.findViewById(R.id.tv_appt_badge)
        //val inactive: ImageView? = appointView.findViewById(R.id.img_inactive_badge)
        if (appointmentStatus == 3) {
            //inactive?.visibility = View.VISIBLE
            badgeLayout.visibility = View.GONE
            AppSharedPreferences.writeSp((AppSharedPreferences.getSharedPreferences(context)), Constants.APPOINT_END_TIME, "")

        } else {
            //inactive?.visibility = View.GONE
            badgeLayout.visibility = View.VISIBLE
            val userPicStr = AppSharedPreferences.readString(sharedPreferences, Constants.USER_PROFILE_PIC)
            if (userPicStr.isNotEmpty()) userImage?.setImageBitmap(Utils.decodeBase64ToImage(userPicStr))
            badgeId?.text = String.format("%s%s", getString(R.string.id), AppSharedPreferences.readString(sharedPreferences, Constants.BADGE_ID))
            AppSharedPreferences.writeSp((AppSharedPreferences.getSharedPreferences(context)), Constants.APPOINT_END_TIME, endDate)
            QRCodeImage?.setImageBitmap(Utils.QRCodeGenerator(AppSharedPreferences.readString(sharedPreferences, Constants.VENDOR_GUID), 150, 150))

            companyName?.text = AppSharedPreferences.readString(sharedPreferences, Constants.VENDOR_COMPANY_NAME)
            userName?.text = String.format(
                getString(R.string.badge_user_name),
                AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME),
                AppSharedPreferences.readString(sharedPreferences, Constants.LAST_NAME)
            )
            val dateStr = endDate.let { Utils.getDate(it, "MM/dd/yyyy") }
            validity?.text = String.format(getString(R.string.expires), dateStr)
            AppSharedPreferences.writeSp((AppSharedPreferences.getSharedPreferences(context)), Constants.APPOINT_DATE, dateStr)
            val apptTime = Utils.getTime(statDate) + "-" + Utils.getTime(endDate)
            val timeStampStr = context?.getString(R.string.appointment_status)?.let {
                String.format(it, apptTime)
            }
            timeStamp?.text = timeStampStr
            AppSharedPreferences.writeSp((AppSharedPreferences.getSharedPreferences(context)), Constants.APPOINT_TIME, apptTime)
        }
        BadgeController.getInstance().convertUIToImage(badgeUILayout, context)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun onAppointmentCheckIn(appoinmentValue: AppointmentData) {
        if (BadgeController.getInstance().connectionState == BadgeController.BadgeConnectionState.CONNECTED) {
            AppointmentDataSource.setAppointmentData(appoinmentValue)
            BadgeController.getInstance().disconnectDevice()
            return
        }
        pDialog?.show()
        var appointment: Int
        if (appoinmentValue.statusFlag != 1) {
            appointment = 2
        } else {
            appointment = 3
        }

        setBadgeUI(appoinmentValue.start, appoinmentValue.end, appointment, appoinmentValue.vendorGuid)
        updateAppointmentViewModel.init(context)
        updateAppointmentViewModel.updateAppointments(
            "",
            "",
            "",
            appoinmentValue.visitReason,
            appoinmentValue.appointmentId,
            appointment,
            appoinmentValue.facilityId
        )
    }

    fun updateAppointmentListener() {
        updateAppointmentViewModel.updateAppointmentLiveData.observe(viewLifecycleOwner) {
            pDialog?.dismiss()
            appointmentViewModel.getAppointments(AppSharedPreferences.readInt(sharedPreferences, Constants.VENDOR_ID))

        }
    }

    private fun setBadgeListener() {
        badgeViewModel.init(this.context)
        badgeViewModel.badgeConnectionStatus.observe(viewLifecycleOwner) {
            if (it == BadgeController.BadgeConnectionState.DISCONNECTED.value) {
                onAppointmentCheckIn(AppointmentDataSource.getAppointmentData())
            }
        }
    }
}