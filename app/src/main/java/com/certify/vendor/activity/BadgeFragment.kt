package com.certify.vendor.activity

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.certify.vendor.R
import com.certify.vendor.badge.BadgeController
import com.certify.vendor.common.Utils
import com.certify.vendor.data.LoginDataSource

class BadgeFragment : Fragment() {

    private val TAG = BadgeFragment::class.java.name
    private lateinit var badgeView : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_badge, container, false)
        badgeView = inflater.inflate(R.layout.badge_device_layout, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val badgeUILayout : ConstraintLayout = badgeView.findViewById(R.id.badge_screen)
        val userImage : ImageView? = badgeView.findViewById(R.id.badge_user_image)
        userImage?.setImageBitmap(Utils.decodeBase64ToImage(LoginDataSource.userProfilePicEncoded))
        val badgeId : TextView? = badgeView.findViewById(R.id.badge_id)
        badgeId?.text = LoginDataSource.loginData?.badgeId
        val companyName : TextView? = badgeView.findViewById(R.id.badge_company_name)
        companyName?.text = LoginDataSource.loginData?.vendorCompanyName
        val userName : TextView? = badgeView.findViewById(R.id.badge_user_name)
        userName?.text = String.format(getString(R.string.badge_user_name), LoginDataSource.loginData?.firstName,
                LoginDataSource.loginData?.lastName)
        val status : TextView? = badgeView.findViewById(R.id.badge_status)
        status?.text = getString(R.string.check_in)
        val validity : TextView? = badgeView.findViewById(R.id.badge_validity)
        validity?.text = LoginDataSource.loginData?.badgeExpiry?.let { Utils.getDate(it, "MM-dd-yyyy HH:mm:ss") }
        convertUIToImage(badgeUILayout)
    }

    override fun onStart() {
        super.onStart()
        //BadgeController.getInstance().init(context)
    }

    private fun convertUIToImage(badgeLayout: ConstraintLayout) {
        badgeLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        Log.d(TAG, "Badge Image width and height ${badgeLayout.measuredWidth} ${badgeLayout.measuredHeight}")
        val bitmap = Bitmap.createBitmap(badgeLayout.measuredWidth, badgeLayout.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        badgeLayout.layout(0, 0, badgeLayout.measuredWidth, badgeLayout.measuredHeight)
        badgeLayout.draw(canvas)
        Log.d(TAG, "Badge Convert to image")
        BadgeController.getInstance().initBle(context, bitmap)
    }
}