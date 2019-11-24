package com.youngtech.fabartists.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by VARUN on 01/01/19.
 */
public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;
    private static String receiverString;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        for (int i = 0; i < pdus.length; i++) {

            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String senderNum = currentMessage.getDisplayOriginatingAddress();
            String message = currentMessage.getDisplayMessageBody();
            Log.i("RECEIVER", "senderNum: " + senderNum + " message: " + message);

            if (!TextUtils.isEmpty(receiverString) && message.contains(receiverString)) { //If message received is from required number.
                //If bound a listener interface, callback the overriden method.
                if (mListener != null) {
                    mListener.messageReceived(message);
                }
            }
        }

    }

    public static void bindListener(SmsListener listener, String msg) {
        mListener = listener;
        receiverString = msg;
    }


}

