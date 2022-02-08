package com.certify.vendor.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.certify.vendor.R
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppSharedPreferences

class BadgeFragment : Fragment() {

    private val TAG = BadgeFragment::class.java.name
    private lateinit var badgeView: View

    private var sharedPreferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        badgeView = inflater.inflate(R.layout.fragment_badge, container, false)
        return badgeView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
            val userImage: ImageView? = badgeView.findViewById(R.id.badge_user_image)
            val userPicStr =
                AppSharedPreferences.readString(sharedPreferences, Constants.USER_PROFILE_PIC)
            if (userPicStr.isNotEmpty())
                userImage?.setImageBitmap(Utils.decodeBase64ToImage(userPicStr))
            val badgeId: TextView? = badgeView.findViewById(R.id.badge_id)
            badgeId?.text = String.format(
                "%s%s",
                getString(R.string.id),
                AppSharedPreferences.readString(sharedPreferences, Constants.BADGE_ID)
            )
            val companyName: TextView? = badgeView.findViewById(R.id.badge_company_name)
            companyName?.text =
                AppSharedPreferences.readString(sharedPreferences, Constants.VENDOR_COMPANY_NAME)
            val userName: TextView? = badgeView.findViewById(R.id.badge_user_name)
            userName?.text = String.format(
                getString(R.string.badge_user_name),
                AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME),
                AppSharedPreferences.readString(sharedPreferences, Constants.LAST_NAME)
            )

            val validity: TextView? = badgeView.findViewById(R.id.badge_expires_date)
            validity?.text =
                AppSharedPreferences.readString(sharedPreferences, Constants.APPOINT_DATE)
            val timeStamp: TextView? = badgeView.findViewById(R.id.badge_time)
            timeStamp?.text =
                AppSharedPreferences.readString(sharedPreferences, Constants.APPOINT_TIME)

            val qrCodeImage: ImageView? = badgeView.findViewById(R.id.badge_qr_code)
            qrCodeImage?.setImageBitmap(
                Utils.QRCodeGenerator(
                    AppSharedPreferences.readString(
                        sharedPreferences,
                        Constants.BADGE_ID
                    )
                )
            )
                timeStamp?.setTextColor(resources.getColor(R.color.green))
                validity?.setTextColor(resources.getColor(R.color.green))

           /* } else {
                validity?.setTextColor(resources.getColor(R.color.red))
                timeStamp?.setTextColor(resources.getColor(R.color.red))
            }*/
            val imgInactive: ImageView? = badgeView.findViewById(R.id.img_inactive)
            val llView: LinearLayout? = badgeView.findViewById(R.id.ll_view)
            if (AppSharedPreferences.readString(sharedPreferences, Constants.APPOINT_DATE)
                    .isEmpty()
            ) {
                imgInactive?.visibility = View.VISIBLE
                llView?.alpha = .2f
            } else imgInactive?.visibility = View.GONE
        } catch (e: Exception) {

        }
        //  BadgeController.getInstance().convertUIToImage(badgeUILayout)
    }

    override fun onStart() {
        super.onStart()
        //BadgeController.getInstance().init(context)
    }


}