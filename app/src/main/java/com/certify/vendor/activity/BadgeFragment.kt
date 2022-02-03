package com.certify.vendor.activity

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
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
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.data.LoginDataSource

class BadgeFragment : Fragment() {

    private val TAG = BadgeFragment::class.java.name
    private lateinit var badgeView: View
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        badgeView = inflater.inflate(R.layout.badge_device_layout, container, false)
        return badgeView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
        val badgeUILayout: ConstraintLayout = badgeView.findViewById(R.id.badge_screen)
        val userImage: ImageView? = badgeView.findViewById(R.id.badge_user_image)
        val userPicStr =
            AppSharedPreferences.readString(sharedPreferences, Constants.USER_PROFILE_PIC)
        if (userPicStr.isNotEmpty())
            userImage?.setImageBitmap(Utils.decodeBase64ToImage(userPicStr))
        val badgeId: TextView? = badgeView.findViewById(R.id.badge_id)
        badgeId?.text = AppSharedPreferences.readString(sharedPreferences, Constants.BADGE_ID)
        val companyName: TextView? = badgeView.findViewById(R.id.badge_company_name)
        val userName: TextView? = badgeView.findViewById(R.id.badge_user_name)
        userName?.text = String.format(
            getString(R.string.badge_user_name), AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME),
            AppSharedPreferences.readString(sharedPreferences, Constants.LAST_NAME)
        )
        val status: TextView? = badgeView.findViewById(R.id.badge_status)
        status?.text = getString(R.string.check_in)
        val validity: TextView? = badgeView.findViewById(R.id.badge_expires)
        validity?.text =
            AppSharedPreferences.readString(sharedPreferences, Constants.BADGE_EXPIRY)?.let { Utils.getDate(it, "MM-dd-yyyy HH:mm:ss") }
        val word: Spannable = SpannableString("Your message")
        convertUIToImage(badgeUILayout)
    }

    override fun onStart() {
        super.onStart()
        //BadgeController.getInstance().init(context)
    }

    private fun convertUIToImage(badgeLayout: ConstraintLayout) {
        badgeLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        Log.d(
            TAG,
            "Badge Image width and height ${badgeLayout.measuredWidth} ${badgeLayout.measuredHeight}"
        )
        val bitmap = Bitmap.createBitmap(
            badgeLayout.measuredWidth,
            badgeLayout.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        badgeLayout.layout(0, 0, badgeLayout.measuredWidth, badgeLayout.measuredHeight)
        badgeLayout.draw(canvas)
        Log.d(TAG, "Badge Convert to image")
        BadgeController.getInstance().initBle(context, bitmap)
    }
}