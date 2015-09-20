package com.philstonight.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.philstonight.Globals;
import com.philstonight.Models.SquadMember;

import java.util.List;
import java.util.Map;

/**
 * Created by vishalkuo on 15-09-19.
 */
public class SharedPrefsUtils {
    public static void saveToSharedPrefs(SquadMember squadMember, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(Globals.PREFSNAME, Context.MODE_PRIVATE).edit();
        editor.putString(squadMember.getName(), squadMember.getNumber());
        editor.commit();
    }

    public static void loadSharedPrefs(Context context, List<SquadMember> squadList){
        SharedPreferences preferences = context.getSharedPreferences(Globals.PREFSNAME, Context.MODE_PRIVATE);
        Map<String, ?> keys = preferences.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()){
            squadList.add(new SquadMember(entry.getKey(), entry.getValue().toString()));
        }
    }
}
