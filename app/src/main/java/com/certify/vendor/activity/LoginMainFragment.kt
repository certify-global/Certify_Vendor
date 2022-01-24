package com.certify.vendor.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.certify.vendor.R
import com.certify.vendor.databinding.FragmentLoginBinding
import com.certify.vendor.model.LoginViewModel

class LoginMainFragment : Fragment() {

    private lateinit var fragmentLoginBinding : FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)
        fragmentLoginBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = ViewModelProviders.of(this@LoginMainFragment).get(LoginViewModel::class.java)
        }
        return fragmentLoginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
    }

    private fun setClickListener() {
        fragmentLoginBinding.signInMobile.setOnClickListener {
            activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.action_loginMainFragment_to_loginMobileFragment)
        }
    }
}