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
    public static void saveSquadMemberToSharedPrefs(SquadMember squadMember, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(Globals.SQUAD_LIST, Context.MODE_PRIVATE).edit();
        editor.putString(squadMember.getName(), squadMember.getNumber());
        editor.commit();
    }

    public static void loadSquadMemberFromSharedPrefs(Context context, List<SquadMember> squadList){
        SharedPreferences preferences = context.getSharedPreferences(Globals.SQUAD_LIST, Context.MODE_PRIVATE);
        Map<String, ?> keys = preferences.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()){
            squadList.add(new SquadMember(entry.getKey(), entry.getValue().toString()));
        }
    }

    public static void deleteSquadMemberFromSharedPrefs(Context context, String name){
        SharedPreferences.Editor editor = context.getSharedPreferences(Globals.SQUAD_LIST, Context.MODE_PRIVATE).edit();
        editor.remove(name);
        editor.commit();
    }

    public static void saveToPrefs(String key, String value, String prefsName, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String loadPrefValue(String key, String prefsName, Context context){
        SharedPreferences preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }
}
