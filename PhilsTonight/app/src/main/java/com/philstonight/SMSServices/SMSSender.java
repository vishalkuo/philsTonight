package com.philstonight.SMSServices;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import com.philstonight.Globals;

/**
 * Created by vishalkuo on 15-09-19.
 */
public class SMSSender {
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    private Context c;

    public SMSSender(Context c) {
        this.c = c;
    }

    public void sendMessage(String phoneNumber){
        PendingIntent sentPI = PendingIntent.getBroadcast(c, 0, new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(c, 0, new Intent(DELIVERED), 0);

        c.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        }, new IntentFilter(SENT));

        c.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, Globals.philsTonight, sentPI, deliveredPI);
    }
}
