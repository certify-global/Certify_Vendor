package com.certify.vendor.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.certify.vendor.R
//import com.certify.vendor.databinding.FragmentLoginBinding
import com.certify.vendor.model.LoginViewModel

class LoginMainFragment : Fragment() {

    //private lateinit var fragmentLoginBinding : FragmentLoginBinding
    private var loginViewModel : LoginViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        /*fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)
        fragmentLoginBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            loginViewModel = ViewModelProviders.of(this@LoginMainFragment).get(LoginViewModel::class.java)
            viewmodel = loginViewModel
        }
        return fragmentLoginBinding.root*/
        loginViewModel = ViewModelProviders.of(this@LoginMainFragment).get(LoginViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
        setLoginDataListener()
        loginViewModel?.init(context)
    }

    private fun setClickListener() {
        val signInButton : TextView? = view?.findViewById(R.id.sign_in)
        val userName : EditText? = view?.findViewById(R.id.user_name)
        val passwd: EditText? = view?.findViewById(R.id.pass_word)
        signInButton?.setOnClickListener {
            if (userName?.text.toString().isNotEmpty() && passwd?.text.toString().isNotEmpty()) {
                loginViewModel?.login(userName?.text.toString(), passwd?.text.toString())
            }
            if (userName?.text.toString().isEmpty()) {
                userName?.error = getString(R.string.user_name_error)
            } else if (passwd?.text.toString().isEmpty()) {
                passwd?.error = getString(R.string.pass_word_error)
            }
        }
        /*fragmentLoginBinding.signInMobile.setOnClickListener {
            activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.action_loginMainFragment_to_loginMobileFragment)
        }

        fragmentLoginBinding.signIn.setOnClickListener {
            loginViewModel?.login(fragmentLoginBinding.userName.text.toString(),
                                    fragmentLoginBinding.passWord.text.toString())
        }*/
    }

    private fun setLoginDataListener() {
        loginViewModel?.signInLiveData?.observe(viewLifecycleOwner, {
            if (it) {
                activity?.finish()
            } else {
                Toast.makeText(context, getString(R.string.login_error), Toast.LENGTH_LONG).show()
            }
        })
    }
}