package com.jinglingtec.ijiazu.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionChangeReceiver extends BroadcastReceiver
{
    String packnameString = null;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        packnameString = context.getPackageName();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info == null)
        {

        }
        else if (info.getType() == ConnectivityManager.TYPE_WIFI)
        {

        }
        else if (info.getType() == ConnectivityManager.TYPE_MOBILE)
        {

        }

    }
}