package com.certify.vendor.activity

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.certify.vendor.R
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.data.LoginDataSource
import com.certify.vendor.databinding.FragmentLoginBinding
import com.certify.vendor.model.LoginViewModel

class LoginMainFragment : BaseFragment() {

    private lateinit var fragmentLoginBinding: FragmentLoginBinding
    private var loginViewModel: LoginViewModel? = null
    private var pDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)
        fragmentLoginBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = loginViewModel
        }
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        return fragmentLoginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setClickListener()
        setLoginDataListener()
        loginViewModel?.init(context)
    }

    private fun initView() {
        pDialog = Utils.ShowProgressDialog(requireContext())
        fragmentLoginBinding.userName.addTextChangedListener(loginTextWatcher)
        fragmentLoginBinding.incPassword.etPassword.addTextChangedListener(loginTextWatcher)
    }

    private val loginTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (validateEmptyFields()) {
                fragmentLoginBinding.signIn.isEnabled = true
                fragmentLoginBinding.signIn.alpha = 1f
            } else {
                fragmentLoginBinding.signIn.isEnabled = false
                fragmentLoginBinding.signIn.alpha = .5f
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private fun validateEmptyFields(): Boolean {
        if (fragmentLoginBinding.userName.text.isNullOrEmpty() || fragmentLoginBinding.incPassword.etPassword.text.isNullOrEmpty())
            return false
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(fragmentLoginBinding.userName.text).matches())
            return false
        return true
    }

    private fun setClickListener() {
        fragmentLoginBinding.incPassword.icPasswordShow.setOnClickListener(View.OnClickListener {
            showPass()
        })
        fragmentLoginBinding.incPassword.icPasswordHide.setOnClickListener(View.OnClickListener {
            hidePass()
        })
        fragmentLoginBinding.signIn.setOnClickListener {
            if (fragmentLoginBinding.userName.text.toString().isNotEmpty() && fragmentLoginBinding.incPassword.etPassword.text.toString().isNotEmpty()) {
                pDialog?.show()
                loginViewModel?.login(
                    fragmentLoginBinding.userName.text.toString(),
                    fragmentLoginBinding.incPassword.etPassword.text.toString()
                )
            }
            if (fragmentLoginBinding.userName.text.toString().isEmpty()) {
                fragmentLoginBinding.userName.error = getString(R.string.user_name_error)
            } else if (fragmentLoginBinding.incPassword.etPassword.text.toString().isEmpty()) {
                fragmentLoginBinding.incPassword.etPassword.error = getString(R.string.pass_word_error)
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

    private fun showPass() {
        fragmentLoginBinding.incPassword.icPasswordShow.setVisibility(View.GONE)
        fragmentLoginBinding.incPassword.icPasswordHide.setVisibility(View.VISIBLE)
        fragmentLoginBinding.incPassword.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance())
    }

    private fun hidePass() {
        fragmentLoginBinding.incPassword.icPasswordShow.setVisibility(View.VISIBLE)
        fragmentLoginBinding.incPassword.icPasswordHide.setVisibility(View.GONE)
        fragmentLoginBinding.incPassword.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
    }

    private fun setLoginDataListener() {
        loginViewModel?.signInLiveData?.observe(viewLifecycleOwner) {
            pDialog?.dismiss()
            if (it) {
                AppSharedPreferences.writeSp(
                    (AppSharedPreferences.getSharedPreferences(context)),
                    Constants.IS_LOGGEDIN, true
                )
                AppSharedPreferences.writeSp(
                    (AppSharedPreferences.getSharedPreferences(context)),
                    Constants.FIRST_NAME, LoginDataSource.loginData?.firstName
                )
                AppSharedPreferences.writeSp(
                    (AppSharedPreferences.getSharedPreferences(context)),
                    Constants.VENDOR_ID, LoginDataSource.loginData?.vendorId!!
                )
                AppSharedPreferences.writeSp(
                    (AppSharedPreferences.getSharedPreferences(context)),
                    Constants.USER_PROFILE_PIC, LoginDataSource.userProfilePicEncoded
                )
                AppSharedPreferences.writeSp(
                    (AppSharedPreferences.getSharedPreferences(context)),
                    Constants.BADGE_EXPIRY, LoginDataSource.loginData?.badgeExpiry
                )
                AppSharedPreferences.writeSp(
                    (AppSharedPreferences.getSharedPreferences(context)),
                    Constants.LAST_NAME, LoginDataSource.loginData?.lastName
                )
                AppSharedPreferences.writeSp(
                    (AppSharedPreferences.getSharedPreferences(context)),
                    Constants.BADGE_ID, LoginDataSource.loginData?.badgeId!!
                )
                AppSharedPreferences.writeSp(
                    (AppSharedPreferences.getSharedPreferences(context)),
                    Constants.VENDOR_COMPANY_NAME, LoginDataSource.loginData?.vendorCompanyName
                )
                AppSharedPreferences.writeSp(
                    (AppSharedPreferences.getSharedPreferences(context)),
                    Constants.USER_EMAIL, LoginDataSource.loginData?.userEmail
                )
                activity?.finish()
            } else {
                if (loginViewModel?.responseMessage?.value!!.isNotEmpty()) {
                    Toast.makeText(context, loginViewModel?.responseMessage?.value, Toast.LENGTH_LONG).show()
                } else
                    Toast.makeText(context, getString(R.string.login_error), Toast.LENGTH_LONG).show()
            }
        }
    }
}