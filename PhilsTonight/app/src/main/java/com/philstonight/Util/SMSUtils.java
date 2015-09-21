package com.philstonight.Util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import com.philstonight.Globals;

/**
 * Created by vishalkuo on 15-09-21.
 */
public class SMSUtils {


    public static BroadcastReceiver generateBroadcastReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Globals.SENT.equals(intent.getAction()))
                {
                    switch (getResultCode())
                    {
                        case Activity.RESULT_OK:
                            break;

                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            UIUtils.toastShort("Generic failure", context);
                            break;

                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            UIUtils.toastShort("No service", context);
                            break;

                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            UIUtils.toastShort("Null PDU", context);
                            break;

                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            UIUtils.toastShort("Radio off", context);
                            break;
                    }
                }
                else if (Globals.DELIVERED.equals(intent.getAction()))
                {
                    switch (getResultCode())
                    {
                        case Activity.RESULT_OK:
                            break;

                        case Activity.RESULT_CANCELED:
                            break;
                    }
                }
            }
        };
    }
}
