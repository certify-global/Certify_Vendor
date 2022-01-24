package com.certify.vendor.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.certify.vendor.R
import com.certify.vendor.databinding.FragmentLoginMobileBinding

class LoginMobileFragment : Fragment() {

    private lateinit var loginMobileBinding : FragmentLoginMobileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login_mobile, container, false)
        return view
    }
}