package com.certify.vendor.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.certify.vendor.Controller.AppointmentController
import com.certify.vendor.R
import com.certify.vendor.api.response.AppointmentData
import com.certify.vendor.badge.Badge
import com.certify.vendor.callback.AppointmentCheckIn
import com.certify.vendor.callback.SettingCallback
import com.certify.vendor.common.Utils
import com.certify.vendor.common.Utils.Companion.getDateValidation
import com.certify.vendor.data.AppointmentDataSource

class SettingsAdapter(
    var context: Context?,
    var settingListener: SettingCallback,
) : RecyclerView.Adapter<SettingsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.setting_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cardViewMyaccount.setOnClickListener {
            settingListener.onSettingCallBack(0)
        }
        holder.cardViewPrivacy.setOnClickListener {
            settingListener.onSettingCallBack(1)
        }
        holder.cardViewTerms.setOnClickListener {
            settingListener.onSettingCallBack(2)
        }
        holder.cardViewLogout.setOnClickListener {
            settingListener.onSettingCallBack(3)
        }


    }

    override fun getItemCount(): Int {
        return 1
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardViewMyaccount = view.findViewById<CardView>(R.id.cardView_myaccount)
        var cardViewPrivacy = view.findViewById<CardView>(R.id.cardView_privacy)
        var cardViewTerms = view.findViewById<CardView>(R.id.cardView_terms)
        var cardViewLogout = view.findViewById<CardView>(R.id.cardView_logout)


    }

}