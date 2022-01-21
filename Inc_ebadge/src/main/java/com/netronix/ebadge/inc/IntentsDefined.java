package com.netronix.ebadge.inc;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;


public class IntentsDefined {
    static final String TAG = IntentsDefined.class.getSimpleName();

    public static final String Version = "1.0";
    public static final String forceOTAVersion = "V0.3.19";
    public static final int forceOTAVersionV0 = 0;
    public static final int forceOTAVersionV1 = 3;
    public static final int forceOTAVersionV2 = 19;
    public static boolean isForceOTA = false;

    public enum ProductFlavor {
        NB(1),

        ;
        private int id;

        ProductFlavor(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public enum Badge_action {
        Template_Image(1),
        Template_ADD(2),
        Transfer_Image(3),
        OTA(4),
        Bettary(5),

        ;
        private int id;
        Badge_action(int id) {this.id=id;}
        public int getId() {return id;}
    }

    public enum Connection_status {
        Auth_Fail(1),
        Connected_Sending(2),
        Sending_Success(3),
        Send_Fail(4),

        ;
        private int id;
        Connection_status(int id) {this.id=id;}
        public int getId() {return id;}
    }

    public enum INTENT_action_name {
        ToService_Command(100),

        ToApp_CommandResult(1000),
        ToApp_Progress(1001),
        ToApp_ReportStatus(1002),
        ;
        private int id;
        INTENT_action_name(int id) {this.id=id;}
        public int getId() {return id;}
    }

    public enum ExtraValueType {
        Int,
        BluetoothDevice,
        BadgeArgument,
        BadgeBitmap,
        Status,
        ErrorCode,
        ;
    }

    public enum INTENT_extra_name {
        Command(1, ExtraValueType.Int),
        CommandResult(2, ExtraValueType.Int),
        ReportStatus(3, ExtraValueType.Status),
        ErrorCode(4, ExtraValueType.ErrorCode),

        BluetoothDevice(1000, ExtraValueType.BluetoothDevice),
        BadgeArgument(1500, ExtraValueType.BadgeArgument),
        BadgeBitmap(2000, ExtraValueType.BadgeBitmap),
        ;

        private int id;
        private ExtraValueType type;
        INTENT_extra_name(int id, ExtraValueType type) {this.id=id;}
        public int getId() {return id;}

    }

    public enum ExtraValue_Command {
        Connect(10),
        Disconnect(100),
        SendImage(200),
        ;

        private int id;
        ExtraValue_Command(int id) {this.id=id;}
        public int getId() {return id;}

        public static final int ID_MIN=0;
        public static final int ID_MAX=1000;
        public static ExtraValue_Command getById (int id) {
            if (id==Connect.id) return Connect;
            if (id==Disconnect.id) return Disconnect;
            if (id==SendImage.id) return SendImage;
            Log.i(TAG, "ExtraValue_Command.getById - unknown id="+id);
            return null;
        }
    }

    public enum ExtraValue_CommandResult {
        Success(0),
        Failed(1),
        UnknownCommand(10),
        ErrorParameter(12),

        ConnectionNotExist(20),
        ConnectionNotReady(21),
        ConnectionExist(22),
        IgnoreDueToPreviousCommandStillRunning(23),
        ;

        private int id;
        ExtraValue_CommandResult(int id) {this.id=id;}
        public int getId() {return id;}

        public static ExtraValue_CommandResult getById (int id) {
            if (Success.id==id) { return Success; }
            if (Failed.id==id) { return Failed; }
            if (UnknownCommand.id==id) { return UnknownCommand; }
            if (ErrorParameter.id==id) { return ErrorParameter; }
            if (ConnectionNotExist.id==id) { return ConnectionNotExist; }
            if (ConnectionNotReady.id==id) { return ConnectionNotReady; }
            if (ConnectionExist.id==id) { return ConnectionExist; }
            if (IgnoreDueToPreviousCommandStillRunning.id==id) { return IgnoreDueToPreviousCommandStillRunning; }
            return null;
        }
    }


    public enum ScaleType{
        aspectFill(1), //aspect_fill
        aspectFit(2), //aspect_fit
        scaleFit(3); //scale_fit

        private int id;

        ScaleType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }



    public enum Status {
        Scanning(10),
        Connecting(20),
        /**
         * The device is connected.
         */
        Connected(30),
        /**
         * The service start disconnect process.
         */
        Disconnecting(40),
        /**
         * The device is disconnected.
         */
        Disconnected(50),


        // status [
        send_file_size(100),
        send_file(101),
        done(102),
        // status ]

        ;

        int val;
        Status (int val) {
            this.val = val;
        }
        public int getValue() { return val; }

        /**
         * Convert an integer to the specific {@link Status}.
         * @param val Integer error number.
         * @return The specific {@link Status}.
         */
        public static Status getStatus (int val ) {
            if (val==Scanning.val) { return Scanning; }
            if (val==Connecting.val) { return Connecting; }
            if (val==Connected.val) { return Connected; }
            if (val==Disconnecting.val) { return Disconnecting; }
            if (val==Disconnected.val) { return Disconnected; }

            if (val==send_file_size.val) { return send_file_size; }
            if (val==send_file.val) { return send_file; }
            if (val==done.val) { return done; }

            return null;
        }
    }

    public enum ErrorCode {

        Service_Release_success(999),

        /**
         * Success.
         */
        Success(0),
        /**
         * Device not found.
         */
        Device_not_found(10),
        /**
         * Attempt to connect to device but failed.
         */
        Connect_failed(20),
        /**
         * Bluetooth read failed.
         * This happened when service failed to read device GUID.
         */
        Read_failed(30),
        /**
         * Service is not available. Previous job still running. Please call later.
         */
        Device_bonding_has_been_cleared(40),

        /**
         * Send Image success.
         */
        Send_success(100),


        /**
         * Get Status success.
         */
        Get_status_success(110),


        /**
         * Send imageCommand success.
         */
        Send_image_cmd_success(111),


        Send_customer_num_success(112),

        Start_ota_action(113),

        Start_check_battery_action(114),

        Send_customer_num_fail(119),

        Send_Success_NextOne(120),

        Force_ota(130),

        Device_Power_off(220),


        /**
         * The service received a not supported intent action.
         */
        Intent_not_support(300),

        // error codes [
        failed_send_file_size(1001),
        failed_send_file(1002),
        // error codes ]



        Send_Image_fail(200),
        /**
         * Set Refresh fail.
         */

        Get_Status_fail(204),
        Set_Device_Time_fail(210),

        ;

        int val;
        ErrorCode (int val) {
            this.val = val;
        }
        public int getValue() { return val; }

        /**
         * Convert an integer to the specific {@link ErrorCode}.
         * @param val Integer error number.
         * @return The specific {@link ErrorCode}.
         */
        public static ErrorCode getErrorCode (int val ) {
            if (val==Success.val) { return Success; }
            if (val==Device_not_found.val) { return Device_not_found; }
            if (val==Connect_failed.val) { return Connect_failed; }
            if (val==Send_Image_fail.val) { return Send_Image_fail; }
            if (val==Read_failed.val) { return Read_failed; }
            if (val==Intent_not_support.val) { return Intent_not_support; }
            if (val==Send_success.val) { return Send_success; }
            if (val==Send_image_cmd_success.val) { return Send_image_cmd_success; }
            if (val==Send_customer_num_success.val) { return Send_customer_num_success; }
            if (val==Start_ota_action.val) { return Start_ota_action; }
            if (val==Start_check_battery_action.val) { return Start_check_battery_action; }
            if (val==Send_customer_num_fail.val) { return Send_customer_num_fail; }
            if (val==Service_Release_success.val) { return Service_Release_success; }
            if (val==Send_Success_NextOne.val) { return Send_Success_NextOne; }
            if (val==Force_ota.val) { return Force_ota; }

            if (val==failed_send_file_size.val) { return failed_send_file_size; }
            if (val==failed_send_file.val) { return failed_send_file; }
            return null;
        }
    }

    public static class BadgeArgument implements Parcelable {
        private static Bitmap imageBitmap;
        private static Bitmap ditherBitmap;
        private static BluetoothDevice dev = null;
        private static int imgArrayLength = 128000;
        private static byte[] imageArray = new byte[imgArrayLength];
        private static int bAction = 1;
        private static int flavor = 1;
        private static String fwVersion = null;
        private static ArrayList<TYPE_Image> imagesArray = new ArrayList<>();
        private static ArrayList<TYPE_Text> textsArray = new ArrayList<>();

        public SparseArray childrenStates;

        public BadgeArgument() {
        }

        public boolean isValid () {
            if (null!=dev && null!=imageBitmap) {
                return true;
            }
            return false;
        }

        protected BadgeArgument(Parcel in) {
            //in.readParcelable(Bitmap.class.getClassLoader());
            in.readParcelable(BluetoothDevice.class.getClassLoader());
            imgArrayLength = in.readInt();
            imageArray = new byte[imgArrayLength];
            in.readByteArray(imageArray);
            in.readInt();
            in.readString();
            in.readInt();
            in.readSparseArray(null);
            //in.createTypedArrayList(ArrayList.CREATOR);
        }



        @Override
        public void writeToParcel(Parcel dest, int flags) {
            //dest.writeParcelable(imageBitmap, flags);
            dest.writeParcelable(dev, flags);
            dest.writeInt(imgArrayLength);
            dest.writeByteArray(imageArray);
            dest.writeInt(bAction);
            dest.writeString(fwVersion);
            dest.writeInt(flavor);
            dest.writeSparseArray(childrenStates);
            //dest.writeTypedList();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<BadgeArgument> CREATOR = new Creator<BadgeArgument>() {

            @Override
            public BadgeArgument createFromParcel(Parcel in) {
                return new BadgeArgument(in);
            }

            @Override
            public BadgeArgument[] newArray(int size) {
                return new BadgeArgument[size];
            }
        };

        public ProductFlavor getFlavor(){
            return ProductFlavor.NB;
        }

        public void setFlavor(int flavor){
            this.flavor = flavor;
        }

        public Bitmap getImageBitmap() {
            return imageBitmap;
        }

        public void setImageBitmap(Bitmap imageBitmap) {
            this.imageBitmap = imageBitmap;
        }


        public void setBitmapArray(byte[] imageArray) {
            this.imageArray = imageArray;
        }

        public byte[] getImageArray() {
            return imageArray;
        }


        public SparseArray getChildrenStates() {
            return childrenStates;
        }

        public void setChildrenStates(SparseArray childrenStates) {
            this.childrenStates = childrenStates;
        }

        public BluetoothDevice getDev() {
            return dev;
        }

        public void setDev(BluetoothDevice dev) {
            this.dev = dev;
        }

        public void setAction(int bAction) {
            this.bAction = bAction;
        }

        public Badge_action getAction() {
            if(bAction == Badge_action.Template_Image.getId()) {
                return Badge_action.Template_Image;
            } else if(bAction == Badge_action.Transfer_Image.getId()) {
                return Badge_action.Transfer_Image;
            } else if(bAction == Badge_action.Template_ADD.getId()) {
                return Badge_action.Template_ADD;
            } else if(bAction == Badge_action.OTA.getId()) {
                return Badge_action.OTA;
            } else {
                return Badge_action.Bettary;
            }
        }

        public void setFWVersion(String fwVersion) {
            this.fwVersion = fwVersion;
        }

        public String getFWVersion() {
           return fwVersion;
        }

        public void setForceOTA(boolean b){
            isForceOTA = b;
        }

        public boolean getForceOTA(){
            return isForceOTA;
        }

        public void setImageArray(ArrayList<TYPE_Image> type_images) {
            imagesArray = type_images;
        }

        public static ArrayList<TYPE_Image> getImagesArray() {
            return imagesArray;
        }

        public void setTextsArray(ArrayList<TYPE_Text> type_texts) {
            textsArray = type_texts;
        }

        public static ArrayList<TYPE_Text> getTextsArray() {
            return textsArray;
        }
    }

    public enum Action {
        /**
         * The {@link #Response} action is used to notify the result of the user action.
         * For example, when user send an {@link #ChangeImage} intent to the service,
         * an {@link #Response} intent will be broadcast by the service when write tag is done.
         */
        Response,

        /**
         * Braodcast to scan Bluetooth device
         */
        Connect,


        SendCustomerNum,

        SetCustomerNum,


        CheckBattery,


        ChangeImage,


        /**
         * Get Status
         */
        GetStatus,

        /**
         * Get Battery
         */
        GetBattery,



        /**
         * Set Device time to Device
         */
        SetDeviceTime,

        /**
         * Check FW version after OTA success
         */
        CheckFWVersion,
        /**

         */
        ReportStatus,
        /**
         */
        ReportGetData,

        /**
         * Write Passenger Data progress
         */
        ReportWriteProgress,

        /**
         * Write Passenger OTA Data progress
         */
        ReportWriteOTAProgress,


        /**
         * Report OTA Result
         */
        ReportOTAStep,

        /**
         */
        ReportScanDevice;

    }

    public enum ExtraName {
        /**
         * The value type is an integer.
         * The acceptable values are defined in the enumeration {@link ErrorCode}.
         * Take {@link ErrorCode#Device_not_found} for example, the value should be
         * {@link ErrorCode#getValue()}.
         */
        ErrorCode,

        /**
         * The value type is an integer which are defined {@link Status#getValue()}.
         * Take {@link Status#Scanning} for example, its values is 10.
         */
        Status,
        /**
         * The value type is a string.
         */
        GetScanDevice,
        /**
         * The value type is a string.
         */
        GetData,

        /**
         * Write Passenger Data progress
         */
        WriteProgress,


        /**
         * Write Passenger OTA Data progress
         */
        WriteOTAProgress,

        /**
         * Report OTA Result
         */
        ReportOTAResult,

        /**
         * The Char UUID type.
         */
        GetCmdAction;

        ;
    }
    static Intent writeIntent;

    public static class TYPE_Text {
        // Template text attribites.
        //String columnName;

        public String type;
        public String uuid;
        public float left;
        public float top;
        public float width;
        public float height;
        public String fontColor;
        public String align;
        public String text;
        public float fontSize;
        public String field;
        public boolean isBold = false;
        public boolean isItalic = false;
        public boolean isUnderlined = false;

        // User text.

        public int getInt_fontColor () {
            return Color.parseColor(fontColor);
        }

        public int getInt_fontSize () {
            // delete non-digits, not works for decimal point.
            //String str = fontSize.replaceAll("\\D+","");
            // delete non-digits, works for decimal point.
            //String str = fontSize.replaceAll("[^\\.0123456789]","");

            int sz = (int)fontSize;
            return sz;
        }


/*
        String getValue () {
            return columnName;
        }*/

        @Override
        public String toString() {
            return "TYPE_Text{" +
                    "type='" + type + '\'' +
                    ", uuid='" + uuid + '\'' +
                    ", left=" + left +
                    ", top=" + top +
                    ", width=" + width +
                    ", height=" + height +
                    ", fontColor='" + fontColor + '\'' +
                    ", align='" + align + '\'' +
                    ", text='" + text + '\'' +
                    ", fontSize=" + fontSize +
                    ", field='" + field + '\'' +
                    ", isBold=" + isBold +
                    ", isItalic=" + isItalic +
                    ", isUnderlined=" + isUnderlined +
                    '}';
        }

    }
    public static class TYPE_Image {
        //String columnName;
        public String type;
        public String imgName;
        public String uuid;
        public String scaleMode;
        public float left;
        public float top;
        public float width;
        public float height;
        public String field;
        public boolean isForceCircle;

        // User value.
        public String value_path;


        void setValue_path(String imagePath) {
            value_path = imagePath;
        }
        String getValue_path() {
            return value_path;
        }

        void setImageName(String img){
            imgName = img;
        }

        public void setScaleMode(String mode){
            scaleMode = mode;
        }

        String getScaleMode(){
            return scaleMode;
        }

        public String getValue_getImage() {
            return imgName;
        }


        @Override
        public String toString() {
            return "TYPE_Image{" +
                    "imgName='" + imgName + '\'' +
                    ", scaleMode='" + scaleMode + '\'' +
                    ", uuid='" + uuid + '\'' +
                    ", type='" + type + '\'' +
                    ", isForceCircle=" + isForceCircle +
                    ", left=" + left +
                    ", top=" + top +
                    ", width=" + width +
                    ", height=" + height +
                    ", field='" + field + '\'' +
                    ", value_path='" + value_path + '\'' +
                    '}';
        }
    }
}

