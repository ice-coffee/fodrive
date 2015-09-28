package com.jinglingtec.ijiazu.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by coffee on 15-1-15.
 */
public class FoNet
{

    public static boolean getNetState(Context ctx)
    {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info == null)
        {
            Log.e("fonetgetnet", "info is null");
            return false;

        }
        else if (info.getType() == ConnectivityManager.TYPE_WIFI)
        {
            Log.e("fonetgetnet", "typewifi");
            return true;
        }
        else if (info.getType() == ConnectivityManager.TYPE_MOBILE)
        {
            Log.e("fonetgetnet", "typemobile");
            return true;
        }

        return false;
    }

}
