package com.netronix.tools;

import android.os.Handler;

public class QuickHandler extends Handler {

    public boolean sendMsg (long delay, int what, int arg1, int arg2, Object obj) {
        return sendMessageDelayed(obtainMessage(what, arg1, arg2, obj), delay);
    }

    public boolean sendMsg (long delay, int what, int arg1, int arg2) {
        return sendMessageDelayed(obtainMessage(what, arg1, arg2, null), delay);
    }

    public boolean sendMsg (long delay, int what, int arg1) {
        return sendMessageDelayed(obtainMessage(what, arg1, 0, null), delay);
    }

    public boolean sendMsg (long delay, int what, Object obj) {
        return sendMessageDelayed(obtainMessage(what, 0, 0, obj), delay);
    }

    public boolean sendMsg (long delay, int what) {
        return sendMessageDelayed(obtainMessage(what, 0, 0, null), delay);
    }

}
