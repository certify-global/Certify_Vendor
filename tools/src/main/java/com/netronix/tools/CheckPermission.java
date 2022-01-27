package com.netronix.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.fragment.app.Fragment;

/**
 * Created by 103001 on 2017/11/17.
 */

public class CheckPermission {
    private static final String TAG = CheckPermission.class.getSimpleName();
    private static final int REQUEST_CODE = 1;
    private static String[] mPerm;


    static private boolean _checkStart (Context context, int aryResId, boolean isActivity, Activity activity, Fragment frg)
    {
        mPerm = context.getResources().getStringArray(aryResId);
        if (mPerm.length==0) {
            Log.i(TAG, "_checkStart: Application require no permissions.");
            return false;
        }

        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            ArrayList<String> reqPerms = new ArrayList<String>();
            for (int i=0; i<mPerm.length; i++) {

                if (context.checkSelfPermission(mPerm[i])!= PackageManager.PERMISSION_GRANTED ) {

                    if (mPerm[i].equals("android.permission.ACCESS_BACKGROUND_LOCATION")) {
                        if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
                            reqPerms.add(mPerm[i]);
                        }
                        continue;
                    }

                    reqPerms.add(mPerm[i]);
                }
            }

            if (0==reqPerms.size()) {
                Log.i(TAG, "_checkStart: All permissions are granted.");
                return false; // all permissions are granted.
            }

            if (isActivity) {
                //activity.requestPermissions((String[])reqPerms.toArray(), REQUEST_CODE);// will crash
                activity.requestPermissions(reqPerms.toArray(new String[0]), REQUEST_CODE);
                //activity.requestPermissions(mPerm, REQUEST_CODE);
            }
            else {
                frg.requestPermissions(reqPerms.toArray(new String[0]), REQUEST_CODE);
                //frg.requestPermissions(mPerm, REQUEST_CODE);
            }

            Log.i(TAG, "_checkStart: checking permission, waiting for onRequestPermissionsResult()");
            return true; // start checking permission -- caller should wait for "onRequestPermissionsResult()".
        }
        else {
            Log.i(TAG, "_checkStart: permission check is not required in android 6 below versions.");
            return false;
        }
    }

    static public boolean checkStart (Context context, Fragment frg, int aryResId) {
        return _checkStart(context, aryResId, false, null, frg);
    }

    static boolean checkStart (Activity activity, int aryResId) {
        return _checkStart(activity, aryResId, true, activity, null);
    }

    public enum Result {
        PERMISSIONS_GRANTED,
        REQUEST_CODE_NOT_MATCH,
        PERMISSIONS_NOT_GRANTED
    }

    public static Result onRequestPermissionsResult(Context context, int requestCode,
                                                    String permissions[], int[] grantResults) {

        if (requestCode!=REQUEST_CODE) {
            return Result.REQUEST_CODE_NOT_MATCH;
        }



        boolean isGranted = true;

        for (int i=0; i<grantResults.length; i++) {
            if (grantResults[i]!= PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                Log.i(TAG, permissions[i]+" not granted.");
            }
        }

        if (isGranted==true) {
            Log.i(TAG, "All permissions are granted.");
            return Result.PERMISSIONS_GRANTED;
        }

        return Result.PERMISSIONS_NOT_GRANTED;
    }

    static public void showAppExitDialog (final Activity activity) {

        // permission not granted.
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("App exit")
                .setMessage("Exit this app due to permissions not granted.")
                .setPositiveButton(android.R.string.ok, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        activity.finish();
                    }
                });
        builder.show();
    }

    static public void showAppExitDialog (final Fragment frg) {

        // permission not granted.
        final AlertDialog.Builder builder = new AlertDialog.Builder(frg.getContext());
        builder.setTitle("App exit")
                .setMessage("Exit this app due to permissions not granted.")
                .setPositiveButton(android.R.string.ok, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        frg.getActivity().moveTaskToBack(true);
                        frg.getActivity().finish();
                    }
                });
        builder.show();
    }
}
