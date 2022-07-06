package com.certify.vendor.adapter

import android.annotation.SuppressLint
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
import com.certify.vendor.api.response.ResponseDataMember
import com.certify.vendor.callback.AppointmentCheckIn
import com.certify.vendor.callback.ItemOnClickCallback
import com.certify.vendor.common.Utils
import com.certify.vendor.common.Utils.Companion.getDateValidation

class MemberListAdapter(
    var context: Context,
    var itemOnClickCallback: ItemOnClickCallback,
    var memberList: List<ResponseDataMember>,
) : RecyclerView.Adapter<MemberListAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {

        holder.name.text = memberList.get(position).memberName
        holder.id.text = memberList.get(position).memberId
        holder.type.text = memberList.get(position).memberType
        holder.itemView.setOnClickListener(View.OnClickListener {
            itemOnClickCallback.onItemOnClickCallBack(position)
        })

    }

    override fun getItemCount() = memberList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateMemberList(memberList: List<ResponseDataMember>) {
        this.memberList = memberList
        notifyDataSetChanged()
    }

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name = view.findViewById<TextView>(R.id.tv_member_name)
        var id = view.findViewById<TextView>(R.id.tv_member_id)
        var type = view.findViewById<TextView>(R.id.tv_member_type)

    }
}