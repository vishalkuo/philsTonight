package com.philstonight.Util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by vishalkuo on 15-09-19.
 */
public class UIUtils {

    public static void toastShort(String msg, Context c)
    {
        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
    }
}
