package com.certify.vendor.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.certify.vendor.Controller.AppointmentController
import com.certify.vendor.R
import com.certify.vendor.api.response.AppointmentData
import com.certify.vendor.badge.Badge
import com.certify.vendor.callback.AppointmentCheckIn
import com.certify.vendor.common.Utils
import com.certify.vendor.common.Utils.Companion.getDateValidation
import com.certify.vendor.data.AppointmentDataSource

class AppointmentListAdapter(
    var context: Context?,
    var appointmentLagenar: AppointmentCheckIn,
    var appointmentList: List<AppointmentData>
) : RecyclerView.Adapter<AppointmentListAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.appointment_row, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val facilityAddress = appointmentList.get(position).facilityAddress
        val address = context?.getString(R.string.appointment_location)?.let {
            String.format(
                it,
                facilityAddress.address1, facilityAddress.address2,
                facilityAddress.city, facilityAddress.state, facilityAddress.zip
            )
        }
        if (getDateValidation(appointmentList.get(position).end) <= 0) {
            if (Utils.isCheckInTime(
                    appointmentList.get(position).start,
                    appointmentList.get(position).end
                )
            ) {

                if (AppointmentController.getInstance()
                        ?.getAddressToLatLon(address!!)!! && Utils.getDateCompare(
                        appointmentList.get(
                            position
                        ).start, appointmentList.get(position).end
                    )
                )
                    holder.checkInOut.visibility = View.VISIBLE
                else holder.checkInOut.visibility = View.GONE
                if (appointmentList.get(position).statusFlag == 1) holder.checkInOut.text =
                    context?.getString(R.string.check_out)
                else  holder.checkInOut.text = context?.getString(R.string.check_in)
            } else {
                holder.checkInOut.visibility = View.GONE
            }
            if (isUpcoming) {
                holder.appointmentStatus.text = context?.getString(R.string.upcoming_appointment)
                isUpcoming = false;
            }
            if (position == 0) holder.appointmentLayout.visibility = View.VISIBLE
            else holder.appointmentLayout.visibility = View.GONE
        } else {
            holder.checkInOut.visibility = View.GONE
            if (ispast) {
                holder.appointmentStatus.text = context?.getString(R.string.past_appointment)
                pastAppointPosition = position
                holder.appointmentLayout.visibility = View.VISIBLE
                ispast = false
            }
            if (pastAppointPosition == position)
                holder.appointmentLayout.visibility = View.VISIBLE
            else holder.appointmentLayout.visibility = View.GONE
        }
        holder.appointmentDate.text =
            Utils.getDate(appointmentList.get(position).start, "dd MMM yyyy")
        holder.appointmentTime.text = context?.getString(R.string.appointment_time)?.let {
            String.format(
                it,
                Utils.getTime(appointmentList.get(position).start),
                Utils.getTime(appointmentList.get(position).end)
            )
        }
        holder.appointmentLocation.text = address
        holder.checkInOut.setOnClickListener {
            appointmentLagenar.onAppointmentCheckIn(appointmentList.get(position))
        }

        when (appointmentList.get(position).statusFlag) {
            1 -> context?.getColor(R.color.check_in)
                ?.let { holder.viewColor.setBackgroundColor(it) }
            5 -> context?.getColor(R.color.cancelled)
                ?.let { holder.viewColor.setBackgroundColor(it) }
            7 -> context?.getColor(R.color.pending)?.let { holder.viewColor.setBackgroundColor(it) }
            10 -> context?.getColor(R.color.checkout)
                ?.let { holder.viewColor.setBackgroundColor(it) }
            12 -> context?.getColor(R.color.approved)
                ?.let { holder.viewColor.setBackgroundColor(it) }
            13 -> context?.getColor(R.color.declined)
                ?.let { holder.viewColor.setBackgroundColor(it) }
            0 -> context?.getColor(R.color.white)?.let { holder.viewColor.setBackgroundColor(it) }

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
        var appointmentStatus = view.findViewById<TextView>(R.id.appointment_status)
        var appointmentLayout = view.findViewById<LinearLayout>(R.id.appointment_layout)
        var viewColor = view.findViewById<View>(R.id.view_type)

    }

    var isUpcoming: Boolean = true
    var ispast: Boolean = true
    var pastAppointPosition = -1

}