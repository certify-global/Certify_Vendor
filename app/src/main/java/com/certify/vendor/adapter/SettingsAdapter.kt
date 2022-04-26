package com.certify.vendor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.certify.vendor.R
import com.certify.vendor.callback.SettingCallback

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
        holder.myAccount.setOnClickListener {
            settingListener.onSettingCallBack(0)
        }
        holder.eBadge.setOnClickListener {
            settingListener.onSettingCallBack(1)
        }
        holder.privacyPolicy.setOnClickListener {
            settingListener.onSettingCallBack(2)
        }
        holder.terms.setOnClickListener {
            settingListener.onSettingCallBack(3)
        }
        holder.logOut.setOnClickListener {
            settingListener.onSettingCallBack(4)
        }

    }

    override fun getItemCount(): Int {
        return 1
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var myAccount = view.findViewById<LinearLayout>(R.id.myaccount)
        var eBadge = view.findViewById<LinearLayout>(R.id.ebadge)
        var privacyPolicy = view.findViewById<TextView>(R.id.privacy_policy)
        var terms = view.findViewById<TextView>(R.id.terms)
        var logOut = view.findViewById<TextView>(R.id.log_out)

    }

}