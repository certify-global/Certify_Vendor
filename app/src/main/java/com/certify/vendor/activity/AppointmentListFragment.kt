package com.certify.vendor.activity

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.certify.vendor.R
import com.certify.vendor.adapter.AppointmentListAdapter
import com.certify.vendor.badge.BadgeController
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
    private var pDialog: Dialog? = null


    //private lateinit var badgeViewDevice: View
    private lateinit var appointView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // badgeViewDevice = inflater.inflate(R.layout.badge_device_layout, container, false)
        appointView = inflater.inflate(R.layout.fragment_appointment, container, false)
        return appointView
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
        pDialog?.show()
        appointmentViewModel.getAppointments(AppSharedPreferences.readInt(sharedPreferences, Constants.VENDOR_ID))
        initRecyclerView()
        setAppointmentListener()
    }

    private fun initView() {
        pDialog = Utils.ShowProgressDialog(requireContext())
        progressIndicator = appointView.findViewById(R.id.progress_indicator)
        val userName: TextView? = appointView.findViewById(R.id.appt_user_name)
        userName?.text =
            String.format(getString(R.string.appt_user_name),AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME))
            String.format(
                getString(R.string.appt_user_name),
                AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME)
            )
        val userPic: ImageView? = appointView.findViewById(R.id.user_profile_pic)
        val userPicStr =
            AppSharedPreferences.readString(sharedPreferences, Constants.USER_PROFILE_PIC)
        if (userPicStr.isNotEmpty()) {
            userPic?.setImageBitmap(Utils.decodeBase64ToImage(userPicStr))
        }
    }

    private fun initRecyclerView() {
        recyclerView = appointView.findViewById(R.id.appt_recycler_view)
        llNoAppointment = appointView.findViewById(R.id.ll_no_appointment)
        // llUpcomingAppointment = appointView.findViewById(R.id.upcoming_appointment_layout)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = AppointmentListAdapter(context, AppointmentDataSource.getAppointmentList())
        recyclerView?.adapter = adapter
    }

    private fun setAppointmentListener() {
        appointmentViewModel.appointmentLiveData.observe(viewLifecycleOwner, {
            pDialog?.dismiss()
            if (it && AppointmentDataSource.getAppointmentList().isNotEmpty()) {
                adapter?.updateAppointmentList(AppointmentDataSource.getAppointmentList())
                recyclerView?.adapter?.notifyDataSetChanged()
                llNoAppointment?.visibility = View.GONE
                recyclerView?.visibility = View.VISIBLE
                if (Utils.getDateValidation(AppointmentDataSource.getAppointmentList()[0].end))
                    llNoAppointment?.visibility = View.VISIBLE
                else llNoAppointment?.visibility = View.GONE
            } else {
                recyclerView?.visibility = View.GONE
                llNoAppointment?.visibility = View.VISIBLE
            }
        })
    }

    private fun setBadgeUI() {
        try {
            val badgeUILayout: ConstraintLayout = appointView.findViewById(R.id.badge_screen)
            val userImage: ImageView? = appointView.findViewById(R.id.img_user_badge)
            val userPicStr =
                AppSharedPreferences.readString(sharedPreferences, Constants.USER_PROFILE_PIC)
            if (userPicStr.isNotEmpty())
                userImage?.setImageBitmap(Utils.decodeBase64ToImage(userPicStr))
            val badgeId: TextView? = appointView.findViewById(R.id.tv_id_badge)
            badgeId?.text = String.format(
                "%s%s",
                getString(R.string.id),
                AppSharedPreferences.readString(sharedPreferences, Constants.BADGE_ID)
            )
            val companyName: TextView? = appointView.findViewById(R.id.tv_company_name_badge)

            companyName?.text =
                AppSharedPreferences.readString(sharedPreferences, Constants.VENDOR_COMPANY_NAME)
            val userName: TextView? = appointView.findViewById(R.id.tv_user_name_badge)
            userName?.text = String.format(
                getString(R.string.badge_user_name),
                AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME),
                AppSharedPreferences.readString(sharedPreferences, Constants.LAST_NAME)
            )

            val validity: TextView? = appointView.findViewById(R.id.tv_expires_date_badge)
            validity?.text =
                AppSharedPreferences.readString(sharedPreferences, Constants.BADGE_EXPIRY)?.let { Utils.getDate(it, "dd-MM-yyyy") }
            val timeStamp: TextView? = appointView.findViewById(R.id.tv_time_badge)
            timeStamp?.text =
                AppSharedPreferences.readString(sharedPreferences, Constants.BADGE_EXPIRY)?.let { Utils.getDate(it, "HH:mm a") }
            if (Utils.getDateValidation(AppSharedPreferences.readString(sharedPreferences, Constants.BADGE_EXPIRY))) {
                timeStamp?.setTextColor(resources.getColor(R.color.green))
                validity?.setTextColor(resources.getColor(R.color.green))

            } else {
                validity?.setTextColor(resources.getColor(R.color.red))
                timeStamp?.setTextColor(resources.getColor(R.color.red))
            }
            BadgeController.getInstance().convertUIToImage(badgeUILayout, context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}