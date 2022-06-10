package com.certify.vendor.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.certify.vendor.activity.UpComingAppointmentFragment

class AppointmentTypeAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return UpComingAppointmentFragment()
            1 -> return UpComingAppointmentFragment()
        }
        return UpComingAppointmentFragment()
    }
}