package com.certify.vendor.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.certify.vendor.R
import com.certify.vendor.api.response.AppointmentData
import com.certify.vendor.badge.Badge
import com.certify.vendor.common.Utils

class AppointmentListAdapter(
    var context: Context?,
    var appointmentList: List<AppointmentData>
) : RecyclerView.Adapter<AppointmentListAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.appointment_row, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.appointmentDate.text =
            Utils.getDate(appointmentList.get(position).start, "dd MMM yyyy")
        holder.appointmentTime.text = context?.getString(R.string.appointment_time)?.let {
            String.format(
                it,
                Utils.getTime(appointmentList.get(position).start),
                Utils.getTime(appointmentList.get(position).end)
            )
        }
        val facilityAddress = appointmentList.get(position).facilityAddress
        holder.appointmentLocation.text = context?.getString(R.string.appointment_location)?.let {
            String.format(
                it,
                facilityAddress.address1, facilityAddress.address2,
                facilityAddress.city, facilityAddress.state, facilityAddress.zip
            )
        }
        holder.checkInOut.setOnClickListener {
            //Badge.init(context)
        }
    }

    override fun getItemCount() = appointmentList.size

    fun updateAppointmentList(appmentList: List<AppointmentData>) {
        Log.i(
            "updateAppointmentList ",
            "" + appointmentList?.size + "appmentList" + appmentList.size
        )
        appointmentList = appmentList
    }

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var appointmentDate = view.findViewById<TextView>(R.id.appointment_date)
        var appointmentTime = view.findViewById<TextView>(R.id.appointment_time)
        var appointmentLocation = view.findViewById<TextView>(R.id.appointment_location)
        var checkInOut = view.findViewById<TextView>(R.id.check_in)
    }
}