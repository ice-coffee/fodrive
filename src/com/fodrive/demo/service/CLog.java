package com.fodrive.demo.service;

import android.util.Log;

public class CLog
{
    private static boolean IS_TEST = true;
    private static String TAGAPP = "com.jinglingtec.ijiazu";

    public static void i(String tag, String msg)
    {
        if (IS_TEST)
        {
            Log.i(TAGAPP, tag + "-->" + msg);
        }
    }

    public static void e(String tag, String msg)
    {
        if (IS_TEST)
        {
            Log.e(TAGAPP, tag + "-->" + msg);
        }
    }

    public static void d(String tag, String msg)
    {
        if (IS_TEST)
        {
            Log.d(TAGAPP, tag + "-->" + msg);
        }

    }
}
