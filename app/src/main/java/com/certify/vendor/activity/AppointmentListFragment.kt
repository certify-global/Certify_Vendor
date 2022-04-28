package com.certify.vendor.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.certify.vendor.controller.AppointmentController
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
import com.certify.vendor.model.UpdateAppointmentViewModel

class AppointmentListFragment : BaseFragment(), AppointmentCheckIn {

    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var updateAppointmentViewModel: UpdateAppointmentViewModel

    private var recyclerView: RecyclerView? = null
    private var llNoAppointment: LinearLayout? = null
    private var adapter: AppointmentListAdapter? = null
    private var sharedPreferences: SharedPreferences? = null
    private var pDialog: Dialog? = null
    private var userLocation: Location? = Location("")
    private var textviewscheduleAppoinment: TextView? = null


    //private lateinit var badgeViewDevice: View
    private lateinit var appointView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        appointView = inflater.inflate(R.layout.fragment_appointment, container, false)
        return appointView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { it ->
            AppointmentController.getInstance()?.initAppointment(
                it
            )
        }
        getUserLocation()

        appointmentViewModel = ViewModelProvider(this)
            .get(AppointmentViewModel::class.java)
        updateAppointmentViewModel = ViewModelProvider(this)
            .get(UpdateAppointmentViewModel::class.java)
        sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
        baseViewModel = appointmentViewModel
        baseViewModel = updateAppointmentViewModel
        initView()
        setOnClickListener()
        pDialog?.show()
        appointmentViewModel.init(context)
        appointmentViewModel.getAppointments(
            AppSharedPreferences.readInt(
                sharedPreferences,
                Constants.VENDOR_ID
            )
        )
        initRecyclerView()
        setAppointmentListener()
        updateAppointmentListener()
        Utils.enableBluetooth()
    }

    private fun setOnClickListener() {
        textviewscheduleAppoinment?.setOnClickListener {
            findNavController().navigate(R.id.scheduleFragment)
        }
    }

    private fun initView() {
        pDialog = Utils.ShowProgressDialog(requireContext())
        progressIndicator = appointView.findViewById(R.id.progress_indicator)
        val userName: TextView? = appointView.findViewById(R.id.appt_user_name)
        textviewscheduleAppoinment = appointView.findViewById(R.id.textview_scheduleAppoinment)
        userName?.text =
            String.format(
                getString(R.string.appt_user_name),
                AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME)
            )
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
        adapter = AppointmentListAdapter(context, this, AppointmentDataSource.getAppointmentList())
        recyclerView?.adapter = adapter
    }

    private fun setAppointmentListener() {
        appointmentViewModel.appointmentLiveData.observe(viewLifecycleOwner) {
            pDialog?.dismiss()
            if (it && AppointmentDataSource.getAppointmentList().isNotEmpty()) {
                adapter?.updateAppointmentList(AppointmentDataSource.getAppointmentList())
                recyclerView?.adapter?.notifyDataSetChanged()
                llNoAppointment?.visibility = View.GONE
                recyclerView?.visibility = View.VISIBLE
                AppSharedPreferences.writeSp((AppSharedPreferences.getSharedPreferences(context)), Constants.VENDOR_GUID, AppointmentDataSource.getAppointmentList()[0].vendorGuid)

                if (Utils.getDateValidation(
                        AppointmentDataSource.getAppointmentList()[0].start,
                        AppointmentDataSource.getAppointmentList()[0].end
                    )
                )
                    llNoAppointment?.visibility = View.GONE
                else llNoAppointment?.visibility = View.VISIBLE
            } else {
                recyclerView?.visibility = View.GONE
                llNoAppointment?.visibility = View.VISIBLE
            }
        }
    }

    private fun setBadgeUI(statDate: String, endDate: String, appointmentStatus: Int,vendorGuid: String) = try {
        val badgeUILayout: ConstraintLayout = appointView.findViewById(R.id.badge_screen)
        val userImage: ImageView? = appointView.findViewById(R.id.img_user_badge)
        val QRCodeImage: ImageView? = appointView.findViewById(R.id.img_qa_badge)
        val companyName: TextView? = appointView.findViewById(R.id.tv_company_name)
        val badgeId: TextView? = appointView.findViewById(R.id.tv_id_badge)
        val userName: TextView? = appointView.findViewById(R.id.tv_user_name_badge)
        val validity: TextView? = appointView.findViewById(R.id.tv_expires_date_badge)
        val timeStamp: TextView? = appointView.findViewById(R.id.tv_appt_badge)
        //val inactive: ImageView? = appointView.findViewById(R.id.img_inactive_badge)
        if (appointmentStatus == 3) {
            //inactive?.visibility = View.VISIBLE
            AppSharedPreferences.writeSp((AppSharedPreferences.getSharedPreferences(context)), Constants.APPOINT_END_TIME, "")

        } else {
            //inactive?.visibility = View.GONE
            val userPicStr = AppSharedPreferences.readString(sharedPreferences, Constants.USER_PROFILE_PIC)
            if (userPicStr.isNotEmpty()) userImage?.setImageBitmap(Utils.decodeBase64ToImage(userPicStr))
            badgeId?.text = String.format(
                "%s%s", getString(R.string.id), AppSharedPreferences.readString(sharedPreferences, Constants.BADGE_ID))
            AppSharedPreferences.writeSp((AppSharedPreferences.getSharedPreferences(context)), Constants.APPOINT_END_TIME, endDate)
            QRCodeImage?.setImageBitmap(Utils.QRCodeGenerator(vendorGuid, 150, 150))
            AppSharedPreferences.writeSp((AppSharedPreferences.getSharedPreferences(context)), Constants.VENDOR_GUID, vendorGuid)

            companyName?.text = AppSharedPreferences.readString(sharedPreferences, Constants.VENDOR_COMPANY_NAME)
            userName?.text = String.format(
                getString(R.string.badge_user_name),
                AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME),
                AppSharedPreferences.readString(sharedPreferences, Constants.LAST_NAME)
            )
            val dateStr = endDate.let { Utils.getDate(it, "MM/dd/yyyy") }
            validity?.text = dateStr
            AppSharedPreferences.writeSp((AppSharedPreferences.getSharedPreferences(context)), Constants.APPOINT_DATE, dateStr)
            val timeStampStr = context?.getString(R.string.appointment_status)?.let {
                String.format(it, Utils.getTime(statDate) + "-" + Utils.getTime(endDate))
            }
            timeStamp?.text = timeStampStr
            AppSharedPreferences.writeSp((AppSharedPreferences.getSharedPreferences(context)), Constants.APPOINT_TIME, timeStampStr)
        }
        BadgeController.getInstance().convertUIToImage(badgeUILayout, context)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun onAppointmentCheckIn(appoinmentValue: AppointmentData) {

        var appointment: Int
        if (appoinmentValue.statusFlag != 1) {
            appointment = 2
        } else {
            appointment = 3
        }

        setBadgeUI(appoinmentValue.start, appoinmentValue.end, appointment, appoinmentValue.vendorGuid)

        pDialog?.show()
        updateAppointmentViewModel.init(context)
        updateAppointmentViewModel.updateAppointments(
            "",
            "",
            "",
            "",
            appoinmentValue.appointmentId,
            appointment,
            appoinmentValue.facilityId
        )
    }

    fun updateAppointmentListener() {
        updateAppointmentViewModel.updateAppointmentLiveData.observe(viewLifecycleOwner) {
            appointmentViewModel.getAppointments(AppSharedPreferences.readInt(
                    sharedPreferences,
                    Constants.VENDOR_ID
                )
            )

        }
    }

    @SuppressLint("MissingPermission")
    fun getUserLocation() {
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val bestProvider = lm.getBestProvider(criteria, false)
        if (bestProvider != null) {
            val location = lm.getLastKnownLocation(bestProvider!!)
            if (location == null) {
                //   Toast.makeText(activity, "Location Not found", Toast.LENGTH_LONG).show()
            } else {
                val geocoder = Geocoder(activity)
                try {
                    val user =
                        geocoder.getFromLocation(
                            location.getLatitude(),
                            location.getLongitude(),
                            1
                        );
                    userLocation?.latitude = user.get(0).latitude;
                    userLocation?.longitude = user.get(0).longitude;
                    Log.i(
                        "getUserLocation",
                        "" + user.get(0).latitude + "," + user.get(0).longitude
                    )
                    userLocation?.let { it1 ->
                        AppointmentController.getInstance()?.setAppointmentLocation(
                            it1
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}