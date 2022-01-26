package com.certify.vendor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.certify.vendor.R
import com.certify.vendor.api.response.AppointmentData

class AppointmentListAdapter(var appointmentList : ArrayList<AppointmentData>) : RecyclerView.Adapter<AppointmentListAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.appointment_row, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.appointmentDate.text = appointmentList.get(position).start
        holder.appointmentTime.text = appointmentList.get(position).end
        val facilityAddress = appointmentList.get(position).facilityAddress
        holder.appointmentLocation.text = facilityAddress.address1 + "\n" +
                                          facilityAddress.address2 + "," + facilityAddress.city + " "
                                          facilityAddress.state + " " + facilityAddress.zip
    }

    override fun getItemCount() = appointmentList.size

    fun updateAppointmentList(appmentList: List<AppointmentData>) {
        appointmentList.addAll(appmentList)
    }

    class AppointmentViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var appointmentDate = view.findViewById<TextView>(R.id.appointment_date)
        var appointmentTime = view.findViewById<TextView>(R.id.appointment_time)
        var appointmentLocation = view.findViewById<TextView>(R.id.appointment_location)
    }
}