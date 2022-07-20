package com.certify.vendor.common

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.certify.vendor.R
import com.certify.vendor.activity.LoginActivity
import com.certify.vendor.api.response.FacilityAddress
import com.certify.vendor.data.AppSharedPreferences.Companion.getSharedPreferences
import com.certify.vendor.data.AppointmentDataSource
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {
        private val TAG = Utils::class.java.name
        const val PERMISSION_REQUEST_CODE = 200

        fun permissionCheck(context: Context?) {
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissionList = arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE
                )
                ActivityCompat.requestPermissions((context as Activity?)!!, permissionList, PERMISSION_REQUEST_CODE)
            }
        }

        fun permissionCheckAllS(context: Context?) {
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissionList = arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_NETWORK_STATE
                )
                ActivityCompat.requestPermissions((context as Activity?)!!, permissionList, PERMISSION_REQUEST_CODE)
            }
        }

        fun permissionCheckBluetooth(context: Context?): Boolean {
           // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
           // }
            return true
        }

        fun encodeToBase64(data: String): String =
            Base64.encodeToString(data.toByteArray(), Base64.DEFAULT)

        fun decodeBase64ToImage(data: String?): Bitmap {
            val decodeData = Base64.decode(data, 0)
            return BitmapFactory.decodeByteArray(decodeData, 0, decodeData.size)
        }

        fun getDate(inputData: String, format: String): String {
            try {
                val simpleDateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date: Date? = simpleDateFormat.parse(inputData)
                val simpleDateFormat2 = SimpleDateFormat(format, Locale.getDefault())
                return simpleDateFormat2.format(date)
            } catch (e: Exception) {
                Log.e(TAG, "getDate(inputData: String, format: String)" + e.message)
            }
            return ""
        }

        fun getDateExpired(inputData: String, format: String): String {
            try {
                val yyyyMMDD = inputData.split("T")
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date: Date? = simpleDateFormat.parse(yyyyMMDD.get(0))
                val simpleDateFormat2 = SimpleDateFormat(format, Locale.getDefault())
                return simpleDateFormat2.format(date)
            } catch (e: Exception) {
                Log.e(TAG, "getDate(inputData: String, format: String)" + e.message)
            }
            return ""
        }

        fun getTime(inputData: String): String {
            try {
                val simpleDateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = simpleDateFormat.parse(inputData)
                val simpleDateFormat2 = SimpleDateFormat("h:mm a", Locale.getDefault())
                return simpleDateFormat2.format(date)
            } catch (e: Exception) {
                Log.e(TAG, "getTime(inputData: String)" + e.message)
            }
            return ""
        }

        fun getDateValidation(startDate: String, endDate: String): Boolean {
            try {
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val startDateTime = format.parse(startDate)
                val endDateTime = format.parse(endDate)
                val currentDateTime = Date(System.currentTimeMillis())
                Log.i(
                    TAG,
                    "getDateValidation =" + ((startDateTime.time >= currentDateTime.time) || (endDateTime.time > currentDateTime.time))
                )

                return ((startDateTime.time >= currentDateTime.time) || (endDateTime.time > currentDateTime.time))
            } catch (e: Exception) {
                Log.e(TAG, "getDateValidation(startDate: String, endDate: String)" + e.message)

            }
            return false
        }

        fun getDateCompare(startDate: String, endDate: String): Boolean {
            try {
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val startDateTime = format.parse(startDate)
                val endDateTime = format.parse(endDate)
                val currentDateTime = Date(System.currentTimeMillis())
                Log.i(
                    TAG,
                    "getDateCompare : totalHours =" + (startDateTime.time > currentDateTime.time) + " ,totalHoursE  =" + (endDateTime.time > currentDateTime.time) + "  ==" + (!(startDateTime.time > currentDateTime.time) && (endDateTime.time > currentDateTime.time))
                )

                return (startDateTime.time <= currentDateTime.time) && (endDateTime.time > currentDateTime.time)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

        fun getDateCompareEndDate(endDate: String): Boolean {
            try {
                if (endDate.isEmpty()) return true;
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val endDateTime = format.parse(endDate)
                val currentDateTime = Date(System.currentTimeMillis())
                return endDateTime.time < currentDateTime.time
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        }

        fun setBitmapScale(selectBitmap: Bitmap): Bitmap? {
            val fw = 640f
            val fh = 400f
            val rotatebm = Bitmap.createBitmap(
                selectBitmap,
                0,
                0,
                selectBitmap.width,
                selectBitmap.height,
                null,
                true
            )
            val scaleBitmap = rotatebm.width.toFloat() / rotatebm.height
            val scaleSpace = fw / fh
            val scaleWidth = fw / rotatebm.width
            val scaleHeight = fh / rotatebm.height
            val sMatrix = Matrix()
            if (scaleBitmap < scaleSpace) {
                sMatrix.postScale(scaleWidth, scaleWidth)
            } else {
                sMatrix.postScale(scaleHeight, scaleHeight)
            }
            val newbm =
                Bitmap.createBitmap(rotatebm, 0, 0, rotatebm.width, rotatebm.height, sMatrix, true)
            val w = newbm.width
            val h = newbm.height
            val retX: Int
            val retY: Int
            if (w > h) {
                retX = Math.abs(newbm.width - fw.toInt()) / 2
                retY = 0
            } else {
                retX = 0
                retY = Math.abs(newbm.height - fh.toInt()) / 2
            }
            return Bitmap.createBitmap(newbm, retX, retY, 640, 400, null, true)
        }

        fun getmonthstring(inputDate: String): String? {
            try {
                val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                val outputFormat: DateFormat = SimpleDateFormat("dd MMMM yyyy")
                val date = inputFormat.parse(inputDate)
                return outputFormat.format(date)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return ""
        }

        fun getTime24to12(inputData: String): String {
            try {
                try {
                    val _24HourSDF = SimpleDateFormat("HH:mm")
                    val _12HourSDF = SimpleDateFormat("hh:mm a")
                    val _24HourDt = _24HourSDF.parse(inputData)
                    return _12HourSDF.format(_24HourDt)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in parsing the time " + e.message)
            }
            return ""
        }

        fun getTime12to24(inputData: String): String {
            try {
                try {
                    val _24HourSDF = SimpleDateFormat("HH:mm")
                    val _12HourSDF = SimpleDateFormat("hh:mm a")
                    val _12HourDt = _12HourSDF.parse(inputData)
                    return _24HourSDF.format(_12HourDt)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in parsing the time " + e.message)
            }
            return ""
        }

        fun getCurrentTime24(): String {
            val currentTime: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            return currentTime;
        }

        fun getCurrentTime(): String {
            val currentTime: String =
                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            return currentTime;
        }
        fun getCurrentTimeIncrement(extraMinutes:Int): String {
            val df = SimpleDateFormat("hh:mm a")
            val d = df.parse(getCurrentTime())
            val cal = Calendar.getInstance()
            cal.time = d
            cal.add(Calendar.MINUTE, extraMinutes)
            val newTime = df.format(cal.time)
            return newTime.toString();
        }

        fun getCurrentDate(): String {
            val currentDate: String =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            return currentDate;
        }

        fun getCurrentDate(dateFormat: String): String = SimpleDateFormat(dateFormat, Locale.getDefault()).format(Date())

        fun isTimeBigger(startTime: String, endTime: String): Boolean {
            var result = false
            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val startDate = simpleDateFormat.parse(startTime)
            val endDate = simpleDateFormat.parse(endTime)
            val currentTime: String =
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val currentDate = simpleDateFormat.parse(currentTime)
            if ((startDate!!.time > currentDate!!.time) && (endDate!!.time > startDate.time)) {
                result = true
            }
            return result
        }

        fun ShowProgressDialog(context: Context?): Dialog? {
            val dialog = Dialog(context!!)
            dialog.setContentView(R.layout.progress_bar)
            dialog.setCancelable(false)
            return dialog
        }

        @SuppressLint("MissingPermission")
        fun enableBluetooth() {
            try {
                val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                bluetoothAdapter.enable()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        fun QRCodeGenerator(guid: String?, width: Int, height: Int): Bitmap? {
            val multiFormatWriter = MultiFormatWriter()
            try {
                val bitMatrix: BitMatrix =
                    multiFormatWriter.encode(guid, BarcodeFormat.QR_CODE, width, height)
                val barcodeEncoder = BarcodeEncoder()
                return barcodeEncoder.createBitmap(bitMatrix)
            } catch (e: WriterException) {
                e.printStackTrace()
            }
            return null
        }

        fun logOut(context: Context?) {
            val sharedPreferences: SharedPreferences? = getSharedPreferences(context)
            val editor = sharedPreferences?.edit()
            editor?.clear()
            AppointmentDataSource.clearAppointment()
            editor?.commit()
            context?.startActivity(Intent(context, LoginActivity::class.java))
        }

        fun logOutDialog(context: Context?) {
            val d = Dialog(context!!)
            d.requestWindowFeature(Window.FEATURE_NO_TITLE)
            d.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            d.setCancelable(false)
            d.setContentView(R.layout.dialog_device)
            val tv_confirm = d.findViewById<TextView>(R.id.tv_confirm)
            val tv_cancel = d.findViewById<TextView>(R.id.tv_cancel)
            tv_confirm.setOnClickListener {
                d.dismiss()
                logOut(context)
            }
            tv_cancel.setOnClickListener { d.dismiss() }
            d.show()
        }

        fun basicAlert(context: Context?, str: String) {
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setMessage(str)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, id ->
                    dialog.cancel()
                }
            val alert = dialogBuilder.create()
            alert.show()
        }

        fun validateFacilityAddress(location: FacilityAddress?): Boolean {
            return (location?.address1.isNullOrEmpty() && location?.city.isNullOrEmpty() && location?.state.isNullOrEmpty()
                    && location?.zip.isNullOrEmpty())
        }

        fun convertDpToPixel(dp: Float, context: Context?): Float {
            return dp * (context?.resources?.displayMetrics?.densityDpi?.toFloat()!! / DisplayMetrics.DENSITY_DEFAULT)
        }

        fun convertPixelsToDp(px: Float, context: Context?): Float {
            return px / (context?.resources?.displayMetrics?.densityDpi?.toFloat()!! / DisplayMetrics.DENSITY_DEFAULT)
        }

        fun hideKeyboard(activity: Activity) {
            try {
                val view = activity.currentFocus
                val methodManager =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                methodManager.hideSoftInputFromWindow(
                    view!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        @SuppressLint("MissingPermission")
        fun disableBluetooth() {
            try {
                val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                bluetoothAdapter.disable()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        fun isDateBigger(selectedDate: String?, inputDate: String?, format: String?): Boolean {
            var result = false
            val sdf = SimpleDateFormat(format)
            var dt1: Date? = null
            var dt2: Date? = null
            try {
                dt1 = sdf.parse(selectedDate)
                dt2 = sdf.parse(inputDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (dt1!!.time > dt2!!.time) {
                result = true
            }
            return result
        }
    }
}