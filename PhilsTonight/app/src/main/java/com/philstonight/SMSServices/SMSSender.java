package com.philstonight.SMSServices;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import com.philstonight.Globals;

import java.util.ArrayList;

/**
 * Created by vishalkuo on 15-09-19.
 */
public class SMSSender {
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    private Context c;
    private static SmsManager smsManager = null;

    public SMSSender(Context c) {
        this.c = c;
        if (smsManager == null) {
            smsManager = SmsManager.getDefault();
        }
    }

    public static void sendMessage(String phoneNumber){
        smsManager.sendTextMessage(phoneNumber, null, Globals.philsTonight, null, null);
    }
}
