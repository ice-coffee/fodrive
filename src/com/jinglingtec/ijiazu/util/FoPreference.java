package com.jinglingtec.ijiazu.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.jinglingtec.ijiazu.IjiazuApp;

public class FoPreference
{
    // put a double value
    public static void putDouble(String key, double data)
    {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(key, String.valueOf(data));
        editor.commit();
    }

    // get a double value
    public static double getDouble(String key)
    {
        String tmp = getPreference().getString(key, null);
        return ((null != tmp) ? Double.valueOf(tmp): 0);
    }

    public static void removeString(String key)
    {
        SharedPreferences sp = getPreference();
        if (null != sp)
        {
            SharedPreferences.Editor editor = sp.edit();
            if (null != editor)
            {
                editor.remove(key);
                editor.commit();
            }
        }
    }

    //put a boolean value
    public static void putBoolean(String key, boolean data)
    {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(key, data);
        editor.commit();
    }

    //get a boolean value
    public static boolean getBoolean(String key, boolean defaultValue)
    {
        return getPreference().getBoolean(key, defaultValue);
    }

    // put a String value
    public static void putString(String key, String data)
    {
        if ((null == data) || (data.length() < 1))
        {
            return;
        }

        SharedPreferences.Editor editor = getEditor();
        editor.putString(key, String.valueOf(data));
        editor.commit();
    }

    // get a String value
    public static String getString(String key)
    {
        String tmp = getPreference().getString(key, null);
        return tmp;
    }

    // put a int value
    public static void putInt(String key, int data)
    {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(key, data);
        editor.commit();
    }

    // get a int value
    public static int getInt(String key)
    {
        return getPreference().getInt(key, 1);
    }


    // get the shared preference
    public static SharedPreferences getPreference()
    {
        return IjiazuApp.getContext().getSharedPreferences(FD_PREFERENCE_ENTRY, Context.MODE_PRIVATE);
    }

    // get the shared preference editor for put data
    private static SharedPreferences.Editor getEditor()
    {
        return getPreference().edit();
    }

    // fodrive preference entry
    private final static String FD_PREFERENCE_ENTRY = "fodrive.preference";

    // public key
    public final static String CONTACTS_ITEM = "contactsItem";

}
