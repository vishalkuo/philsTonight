package com.philstonight.SMSServices;

import android.content.Context;
import android.telephony.SmsManager;

import com.philstonight.Globals;

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
    }

    public static void sendMessage(String phoneNumber){
        if (smsManager == null) {
            smsManager = SmsManager.getDefault();
        }
        smsManager.sendTextMessage(phoneNumber, null, Globals.philsTonight, null, null);
    }
}
