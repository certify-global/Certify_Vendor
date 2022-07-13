package com.certify.vendor.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.certify.vendor.R
import com.certify.vendor.api.response.AppointmentData
import com.certify.vendor.callback.AppointmentCheckIn
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.common.Utils.Companion.getDateValidation

class AppointmentListAdapter(
    var context: Context,
    var appointmentLagenar: AppointmentCheckIn,
    var appointmentList: List<AppointmentData>,
    var selectionType: String
) : RecyclerView.Adapter<AppointmentListAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.appointment_row, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {

        if (appointmentList.get(position).locationName.isNotEmpty()) {
//            val address = context.getString(R.string.appointment_location).let {
//                String.format(it, facilityAddress.address1, facilityAddress.address2, facilityAddress.city, facilityAddress.state, facilityAddress.zip)
//            }
            holder.appointmentLocation.text = appointmentList.get(position).locationName
        } else {
            holder.appointmentLocation.text = ""
        }
        if (getDateValidation(appointmentList[position].start, appointmentList[position].end)) {
            if (appointmentList[position].mobileCheckinAllowed == 1 && Utils.getDateCompare(
                    appointmentList[position].start, appointmentList[position].end
                )
            ) {

                if ((appointmentList[position].statusFlag == 12 || appointmentList[position].statusFlag == 1))
                    holder.checkInOut.visibility = View.VISIBLE
                else holder.checkInOut.visibility = View.GONE
                if (appointmentList[position].statusFlag == 1) holder.checkInOut.text = context?.getString(R.string.check_out)
                else holder.checkInOut.text = context?.getString(R.string.check_in)
            } else {
                holder.checkInOut.visibility = View.GONE
            }


        } else {
            holder.checkInOut.visibility = View.GONE

        }
        holder.appointmentDate.text = appointmentList[position].facilityName
        holder.appointmentTime.text = context.getString(R.string.appointment_time).let {
            String.format(it, Utils.getTime(appointmentList[position].start), Utils.getTime(appointmentList[position].end), Utils.getDate(appointmentList[position].start, "dd MMM yyyy"))
        }
        holder.checkInOut.setOnClickListener {
            appointmentLagenar.onAppointmentCheckIn(appointmentList.get(position))
        }
        holder.llBackGround.setOnClickListener {

            appointmentLagenar.onAppointmentDetails(appointmentList.get(position))
        }
        if (selectionType.equals(Constants.AppointmentTypes.EXPIRED.name)) {
            readColor(holder)
        } else if (selectionType.equals(Constants.AppointmentTypes.PAST.name)) {
            blueColor(holder)
        } else {
            when (appointmentList.get(position).statusFlag) {
                1 -> blueColor(holder)
                5 -> readColor(holder)
                7 -> yellowColor(holder)
                10 -> blueColor(holder)
                12 -> greenColor(holder)
                13 -> readColor(holder)
                0 -> yellowColor(holder)
            }
        }


    }

    fun readColor(holder: AppointmentViewHolder) {
        holder.viewColor.setBackgroundColor(context.getColor(R.color.declined_cancelled))
        holder.llBackGround.setBackgroundColor(context.getColor(R.color.declined_cancelled_light))
        holder.appointmentDate.setTextColor(context.getColor(R.color.declined_cancelled))
        holder.appointmentTime.setTextColor(context.getColor(R.color.declined_cancelled))
        holder.imgLocation.setImageResource(R.drawable.ic_location_today_appt)


    }

    fun yellowColor(holder: AppointmentViewHolder) {
        holder.viewColor.setBackgroundColor(context.getColor(R.color.pending))
        holder.llBackGround.setBackgroundColor(context.getColor(R.color.pending_light))
        holder.appointmentDate.setTextColor(context.getColor(R.color.pending))
        holder.appointmentTime.setTextColor(context.getColor(R.color.pending))
        holder.imgLocation.setImageResource(R.drawable.ic_location_upcoming_appt)

    }

    fun blueColor(holder: AppointmentViewHolder) {
        holder.viewColor.setBackgroundColor(context.getColor(R.color.check_in_out))
        holder.llBackGround.setBackgroundColor(context.getColor(R.color.check_in_out_light))
        holder.appointmentDate.setTextColor(context.getColor(R.color.check_in_out))
        holder.appointmentTime.setTextColor(context.getColor(R.color.check_in_out))
        holder.imgLocation.setImageResource(R.drawable.ic_location_past_appt)

    }

    fun greenColor(holder: AppointmentViewHolder) {
        holder.viewColor.setBackgroundColor(context.getColor(R.color.approved))
        holder.llBackGround.setBackgroundColor(context.getColor(R.color.approved_light))
        holder.appointmentDate.setTextColor(context.getColor(R.color.approved))
        holder.appointmentTime.setTextColor(context.getColor(R.color.approved))
        holder.imgLocation.setImageResource(R.drawable.ic_location_green)

    }

    override fun getItemCount() = appointmentList.size

    fun updateAppointmentList(appmentList: List<AppointmentData>) {
        appointmentList = appmentList
    }

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var appointmentDate = view.findViewById<TextView>(R.id.appointment_date)
        var appointmentTime = view.findViewById<TextView>(R.id.appointment_time)
        var appointmentLocationLayout = view.findViewById<LinearLayout>(R.id.appointment_location_layout)
        var appointmentLocation = view.findViewById<TextView>(R.id.appointment_location)
        var checkInOut = view.findViewById<TextView>(R.id.check_in)
        var viewColor = view.findViewById<View>(R.id.view_type)
        var llBackGround = view.findViewById<View>(R.id.ll_back_ground)
        var imgLocation = view.findViewById<ImageView>(R.id.img_location)

    }
}