package com.certify.vendor.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.certify.vendor.R
import com.certify.vendor.adapter.SettingsAdapter
import com.certify.vendor.badge.BadgeController
import com.certify.vendor.callback.SettingCallback
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppSharedPreferences

class SettingsFragment : Fragment(),SettingCallback {

    private val TAG = SettingsFragment::class.java.name
    private lateinit var setingView: View
    private var sharedPreferences: SharedPreferences? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: SettingsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setingView = inflater.inflate(R.layout.settings_layout, container, false)
        return setingView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
        initView();
        initRecyclerView()
        setOnBackPress()
    }

    private fun initRecyclerView() {
        adapter = SettingsAdapter(context, this)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        adapter?.notifyDataSetChanged()

    }

    private fun initView() {
        recyclerView = setingView.findViewById(R.id.recyclerView_settings)

    }

    override fun onSettingCallBack(position: Int) {
        when(position){
            0 -> launchMyAccount()
            1 -> launchBadgeManageFragment()
            2 -> launchWebView("https://www.certify.me/legal/privacy-policy/")
            3 -> launchWebView("https://www.certify.me/legal/terms-of-use/")
            4 -> Utils.logOutDialog(requireContext())
        }
    }

    private fun launchMyAccount() {
        findNavController().navigate(R.id.myAccountFragment)
    }

    private fun launchBadgeManageFragment() {
        findNavController().navigate(R.id.badgeManageFragment)
    }

    private fun launchWebView(urlValue:String) {
        val intent = Intent(requireContext(), WebViewActivity::class.java)
        intent.putExtra("url",urlValue)
        startActivity(intent)
    }

    private fun setOnBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override
            fun handleOnBackPressed() {
                BadgeController.getInstance().onBadgeClose()
                BadgeController.getInstance().isBadgeDisconnected = false
                val sharedPreferences = AppSharedPreferences.getSharedPreferences(activity)
                AppSharedPreferences.writeSp(sharedPreferences, Constants.BADGE_DEVICE_UPDATED, false)
                BadgeController.getInstance().unRegisterReceiver()
                activity?.finish()
            }
        })
    }
}