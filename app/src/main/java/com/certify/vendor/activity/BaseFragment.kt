package com.certify.vendor.activity

import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.certify.vendor.model.BaseViewModel

open class BaseFragment : Fragment() {

    var baseViewModel : BaseViewModel? = null
    var progressIndicator : ProgressBar? = null

    override fun onStart() {
        super.onStart()
        setProgressBar()
    }

    private fun setProgressBar() {
        baseViewModel?.loading?.observe(this, {
            if (it) {
                progressIndicator?.visibility = View.VISIBLE
            } else {
                progressIndicator?.visibility = View.GONE
            }
        })
    }
}