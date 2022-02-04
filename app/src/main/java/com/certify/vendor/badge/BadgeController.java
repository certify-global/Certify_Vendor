package com.certify.vendor.badge;

import static android.bluetooth.BluetoothDevice.BOND_BONDED;
import static android.bluetooth.BluetoothDevice.BOND_BONDING;
import static android.bluetooth.BluetoothDevice.BOND_NONE;
import static com.netronix.ble.scan.BleScanConfig.ScanApi.API_21;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.certify.vendor.common.Utils;
import com.netronix.ble.connect.BLEManager;
import com.netronix.ble.connect.data.Statics;
import com.netronix.ble.connect.dither.Common;
import com.netronix.ble.connect.dither.Raw7ColorConvertor;
import com.netronix.ble.scan.BleDevice;
import com.netronix.ble.scan.BleScanCallback;
import com.netronix.ble.scan.BleScanConfig;
import com.netronix.ble.scan.BleScanProc;
import com.netronix.ebadge.inc.IntentsDefined;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class BadgeController {
    private static final String TAG = BadgeController.class.getSimpleName();
    private static BadgeController instance = null;
    private Context context;
    private BleScanProc bleScanProc;
    private IntentsDefined.BadgeArgument badgeArg = new IntentsDefined.BadgeArgument();
    EbadgeBroadcastReceiver mBR = new EbadgeBroadcastReceiver();
    private NtxBleReceiver ntxBleReceiver = new NtxBleReceiver();
    private ScanConfig mSC = new ScanConfig();

    public static BadgeController getInstance() {
        if (instance == null) {
            instance = new BadgeController();
        }
        return instance;
    }

    public void initBle(Context context, Bitmap bitmap) {
        this.context = context;
        BLEManager.start(context);
        bleScanProc = new BleScanProc();
        badgeArg.setFlavor(IntentsDefined.ProductFlavor.NB.getId());
        mBR.register(context);
        ntxBleReceiver.register(context);
        setImage(bitmap);
        startScan();
    }

    private void startScan() {
        bleScanProc.startScan(context, new BleScanCallback() {
            @Override
            public void onLeScan(BleDevice bleDevice, int rssi, byte[] scanRecord) {
                Log.d(TAG, "Badge on Scan result");
                bleScanProc.stopScan();
                badgeArg.setDev(bleDevice.getBluetoothDevice());
                connectDevice();
            }

            @Override
            public void onScanFailed(ERR_SCAN err_scan) {
                Log.e(TAG, "Badge Scan failed");
            }

            @Override
            public void onScanApiSwitch(BleScanConfig.ScanApi scanApi) {

            }

            @Override
            public BleScanConfig getBleScanConfig() {
                return mSC;
            }

            @Override
            public String getOnScanCheckNamePrefix() {
                return null;
            }

            @Override
            public List<UUID> getOnScanCheckServiceUUIDs() {
                return Common.gBleDevInfo.getBroadcastCheckUUIDs();
            }
        });
    }

    private void connectDevice() {
        Log.d(TAG, "Badge connect ");
        BLEManager.connect(badgeArg);
    }

    private void setImage(Bitmap bitmap) {
        badgeArg.setAction(IntentsDefined.Badge_action.Template_Image.getId());
        Bitmap scaleBitmap = Utils.Companion.setBitmapScale(bitmap);
        Raw7ColorConvertor raw7ColorConvertor = new Raw7ColorConvertor();

        ByteBuffer outputBuffer;

        outputBuffer = BLEManager.make7bitRaw(scaleBitmap);
        byte[] outputBytes = new byte[outputBuffer.remaining()];
        outputBuffer.get(outputBytes, 0, outputBytes.length);


        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inPreferredConfig = Bitmap.Config.ARGB_8888;

        badgeArg.setBitmapArray(outputBuffer.array());
        /*Bitmap finalBitmap = Util.setBitmapScale(bitmap);
        badgeArg.setImageBitmap(finalBitmap);
        badgeArg.setAction(IntentsDefined.Badge_action.Transfer_Image.getId());
        *///BLEManager.setImageByte(badgeArg);
    }

    private void getBattery() {
        Log.d(TAG, "Badge battery ");
        BLEManager.disconnect();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "Badge battery connect ");
                badgeArg.setAction(IntentsDefined.Badge_action.Bettary.getId());
                BLEManager.connect(badgeArg);
            }
        }, 10 * 1000);
    }

    enum BondState {
        none(BOND_NONE),
        bonding(BOND_BONDING),
        bonded(BOND_BONDED),
        ;

        private int id;
        BondState(int id) { this.id=id; }
        public int getId () { return id; }

        public static BondState getBondState (int id) {
            if (none.id==id) { return none; }
            if (bonding.id==id) { return bonding; }
            if (bonded.id==id) { return bonded; }
            return null;
        }
    }

    class ScanConfig extends BleScanConfig.Default {

        @Override
        public Context getContext() {
            return context;
        }

        @Override
        public ScanApi getScanApi() {
            return API_21;
        }
    }

    class NtxBleReceiver extends BroadcastReceiver {

        boolean mIsRegistered = false;

        void register(Context context) {
            IntentFilter iF = new IntentFilter();

            iF.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
            iF.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

            iF.addAction(IntentsDefined.INTENT_action_name.ToApp_CommandResult.toString());
            iF.addAction(IntentsDefined.INTENT_action_name.ToApp_Progress.toString());
            iF.addAction(IntentsDefined.INTENT_action_name.ToApp_ReportStatus.toString());

            context.registerReceiver(mBR, iF);
            mIsRegistered = true;
            Log.i(TAG, "BleReceiver - registered !");
        }

        void unRegister(Context context) {
            context.unregisterReceiver(mBR);
            mIsRegistered = false;
            Log.i(TAG, "BleReceiver - unregistered !");
        }

        NtxBleReceiver() {
            reset();
        }

        void reset() {
        }

        Intent mUserResult;

        Intent getUserResult() {
            return mUserResult;
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "Badge intent action Ntx " + action);

            if (action.equals(IntentsDefined.INTENT_action_name.ToApp_CommandResult.toString())) { //

                int cmd = intent.getIntExtra(IntentsDefined.INTENT_extra_name.Command.toString(), -1);
                int result = intent.getIntExtra(IntentsDefined.INTENT_extra_name.CommandResult.toString(), -1);

                IntentsDefined.ExtraValue_Command exCmd = IntentsDefined.ExtraValue_Command.getById(cmd);

                Log.i(TAG, "onReceived : ToApp_CommandResult cmd=" + exCmd.toString() + " result=" + result);

                if (cmd == IntentsDefined.ExtraValue_Command.Disconnect.getId()) {
                    //mH.sendMessage(mH.obtainMessage(MSG.disconnect.getWhat(), 1, result));
                    return;
                }
                if (cmd == IntentsDefined.ExtraValue_Command.Connect.getId()) {
                    //mH.sendMessage(mH.obtainMessage(MSG.connect.getWhat(), 1, result));
                    return;
                }

                Log.i(TAG, "Unknown ToApp_CommandResult received !");

            } else if (action.equals(IntentsDefined.INTENT_action_name.ToApp_ReportStatus.toString())) {
                int istatus = intent.getIntExtra(IntentsDefined.INTENT_extra_name.ReportStatus.toString(), -1);
                IntentsDefined.Status status = IntentsDefined.Status.getStatus(istatus);

                if (null == status) {
                    Log.e(TAG, "onReceive : ToApp_ReportStatus - received unknown status " + istatus);
                    //return;
                }
                int ierr = intent.getIntExtra(IntentsDefined.INTENT_extra_name.ErrorCode.toString(), -1);

                //mH.sendMessage(mH.obtainMessage(MSG.status.what, istatus, ierr));

            } else if (action.equals(IntentsDefined.INTENT_action_name.ToApp_Progress.toString())) {
                // todo.
            } else if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
                Log.i(TAG, "onReceive : ACTION_PAIRING_REQUEST");

            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                int state = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);
                BondState old = BondState.getBondState(state);
                state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                BondState cur = BondState.getBondState(state);

                Log.i(TAG, "onReceive : " + old + " -> " + cur);

                if (BondState.bonding == cur) {
                    dev.setPin("123456".getBytes()); //
                } else if (BondState.bonded == cur) {
                    //mH.sendMessage(mH.obtainMessage(MSG.start.getWhat(), 0, 0));
                } else if (BondState.none == cur) {
                }
            }
        }
    }

    class EbadgeBroadcastReceiver extends BroadcastReceiver {

        boolean mIsRegistered = false;

        void register(Context context) {
            IntentFilter iF = new IntentFilter();
            iF.addAction(IntentsDefined.Action.Response.toString());
            iF.addAction(IntentsDefined.Action.ReportStatus.toString());
            iF.addAction(IntentsDefined.Action.ReportGetData.toString());
            iF.addAction(IntentsDefined.Action.ReportScanDevice.toString());
            iF.addAction(IntentsDefined.Action.ReportWriteProgress.toString());
            iF.addAction(IntentsDefined.Action.ReportWriteOTAProgress.toString());
            iF.addAction(IntentsDefined.Action.ReportOTAStep.toString());
            iF.addAction(Statics.BLUETOOTH_GATT_UPDATE.toString());

            context.registerReceiver(mBR, iF);
            mIsRegistered = true;
            Log.i(TAG, "EbadgeBroadcastReceiver - registered !");
        }

        void unRegister(Context context) {
            context.unregisterReceiver(mBR);
            mIsRegistered = false;
            Log.i(TAG, "EbadgeBroadcastReceiver - unregistered !");
        }


        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.d(TAG, "Badge intent action Ebadge " + intent.getAction());
            if (action.equals(IntentsDefined.Action.Response.toString())) { //Response of command
                int err = intent.getIntExtra(IntentsDefined.ExtraName.ErrorCode.toString(), -1);
                IntentsDefined.ErrorCode errCode = IntentsDefined.ErrorCode.getErrorCode(err);
                Log.i(TAG, "onReceived : Response[" + errCode + "]");
                if (errCode == IntentsDefined.ErrorCode.Send_Success_NextOne) {
                    getBattery();
                }
            } else if (action.equals(IntentsDefined.Action.ReportStatus.toString())) { //return the connectiong Status between app and device
                int status = intent.getIntExtra(IntentsDefined.ExtraName.Status.toString(), -1);
                Log.i(TAG, "onReceived : ReportStatus = " + status);
            } else if (action.equals(IntentsDefined.Action.ReportGetData.toString())) {
                String getData = intent.getStringExtra(IntentsDefined.ExtraName.GetData.toString());
                String getCmdAction = intent.getStringExtra(IntentsDefined.ExtraName.GetCmdAction.toString());
                String reportData = String.format(getCmdAction + ":" + getData);
                Log.i(TAG, "onReceived : ReportGetData getData = " + getData);
                //mH.sendMessageDelayed(mH.obtainMessage(MSG_bleGetData, reportData), 0);
                /*badgeArg.setAction(IntentsDefined.Badge_action.Template_Image.getId());
                setImage();*/
            } else if (action.equals(IntentsDefined.Action.ReportWriteProgress.toString())) {
                /*precent = intent.getIntExtra(IntentsDefined.ExtraName.WriteProgress.toString(), -1);
                mH.sendMessageDelayed(mH.obtainMessage(MSG_sendingPrecent), 0);*/
            } else if (action.equals(IntentsDefined.Action.ReportWriteOTAProgress.toString())) {
                /*otaPecent = intent.getIntExtra(IntentsDefined.ExtraName.WriteOTAProgress.toString(), -1);
                mH.sendMessageDelayed(mH.obtainMessage(MSG_sendingOTAPrecent), 0);*/
            } else if (action.equals(IntentsDefined.Action.ReportOTAStep.toString())) {
                /*OTAResult = intent.getIntExtra(IntentsDefined.ExtraName.ReportOTAResult.toString(), -1);
                if (OTAResult == Statics.OTA_STEP_SET_CHUNK_GONE) {
                    mf_suota.setChunkGone();
                } else if (OTAResult == Statics.OTA_STEP_ON_SUCCESS) {
                    mf_suota.setOTASuccess();
                    args.setForceOTA(false);
                } else if (OTAResult == Statics.OTA_STEP_ON_ERROR) {
                    mH.sendMessageDelayed(mH.obtainMessage(MSG_bleDisconnected), 0);
                }*/
            } else if (action.equals(Statics.BLUETOOTH_GATT_UPDATE)) {
                int OTAStep = intent.getIntExtra("step", -1);
                //mf_suota.processStep(intent);
            }
        }
    }

    public void convertUIToImage(ConstraintLayout badgeLayout, Context context) {
        badgeLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        Bitmap bitmap = Bitmap.createBitmap(
                badgeLayout.getMeasuredWidth(),
                badgeLayout.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        badgeLayout.layout(0, 0, badgeLayout.getMeasuredWidth(), badgeLayout.getMeasuredHeight());
        badgeLayout.draw(canvas);
        Log.d(TAG, "Badge Convert to image");
        BadgeController.getInstance().initBle(context, bitmap);
    }
}
