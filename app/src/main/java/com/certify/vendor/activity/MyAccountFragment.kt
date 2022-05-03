package com.certify.vendor.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.databinding.FragmentAccountBinding

class MyAccountFragment : Fragment() {

    private var sharedPreferences: SharedPreferences? = null
    private lateinit var accountFragmentBinding : FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        accountFragmentBinding = FragmentAccountBinding.inflate(inflater, container, false)
        accountFragmentBinding.apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return accountFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateUI()
        setClickListener()
    }

    private fun updateUI() {
        sharedPreferences = AppSharedPreferences.getSharedPreferences(this.context)
        val userPicStr =
            AppSharedPreferences.readString(sharedPreferences, Constants.USER_PROFILE_PIC)
        if (userPicStr.isNotEmpty()) {
            accountFragmentBinding.imageViewUser.setImageBitmap(Utils.decodeBase64ToImage(userPicStr))
        }
        accountFragmentBinding.editTextName.setText(AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME)+" " +   AppSharedPreferences.readString(sharedPreferences, Constants.LAST_NAME))
        accountFragmentBinding.editTextEmail.setText(AppSharedPreferences.readString(sharedPreferences, Constants.USER_EMAIL))
    }

    private fun setClickListener() {
        accountFragmentBinding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}