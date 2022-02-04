package com.certify.vendor.common

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import android.util.Log
import com.certify.vendor.R
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {
        private val TAG = Utils::class.java.name

        fun encodeToBase64(data: String): String = Base64.encodeToString(data.toByteArray(), Base64.DEFAULT)

        fun decodeBase64ToImage(data: String?) : Bitmap {
            val decodeData = Base64.decode(data, 0)
            return BitmapFactory.decodeByteArray(decodeData, 0, decodeData.size)
        }

        fun getDate(inputData: String, format: String) : String {
            try {
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date : Date? = simpleDateFormat.parse(inputData)
                val simpleDateFormat2 = SimpleDateFormat(format, Locale.getDefault())
                return simpleDateFormat2.format(date)
            } catch (e: Exception) {
                Log.e(TAG, "Error in parsing the date " + e.message)
            }
            return ""
        }

        fun getTime(inputData: String) : String {
            try {
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = simpleDateFormat.parse(inputData)
                val simpleDateFormat2 = SimpleDateFormat("h:mm a", Locale.getDefault())
                return simpleDateFormat2.format(date)
            } catch (e: Exception) {
                Log.e(TAG, "Error in parsing the time " + e.message)
            }
            return ""
        }

        fun setBitmapScale(selectBitmap: Bitmap): Bitmap? {
            val fw = 640f
            val fh = 400f
            val rotatebm = Bitmap.createBitmap(selectBitmap, 0, 0, selectBitmap.width, selectBitmap.height, null, true)
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
            val newbm = Bitmap.createBitmap(rotatebm, 0, 0, rotatebm.width, rotatebm.height, sMatrix, true)
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
        fun getTime24to12(inputData: String) : String {
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
        fun getCurrentTime24() : String {
            val currentTime: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            return currentTime;
        }
        fun getCurrentTime() : String {
            val currentTime: String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
             return currentTime;
        }
        fun getCurrentDate() : String {
            val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            return currentDate;
        }

        fun isTimeBigger(startTime:String,endTime:String): Boolean {
            var result = false
            var simpleDateFormat = SimpleDateFormat("HH:mm")
            var startDate = simpleDateFormat.parse(startTime)
            var endDate = simpleDateFormat.parse(endTime)
            if (endDate!!.time > startDate!!.time) {
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


    }


}