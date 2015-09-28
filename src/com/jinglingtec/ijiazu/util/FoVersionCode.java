package com.jinglingtec.ijiazu.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by coffee on 14-12-17.
 */
public class FoVersionCode
{

    public static String getIjiazuVersionName(Context ctx)
    {
        try
        {
            PackageManager manager = ctx.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(ctx.getPackageName(), 0);

            if (null != packageInfo)
            {
                return packageInfo.versionName;
            }
        } catch (Exception e)
        {
            Log.e("versionmessage", e.getMessage());
        }

        return null;
    }

    public static int getIjiazuVersionCode(Context ctx)
    {
        try
        {
            PackageManager manager = ctx.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(ctx.getPackageName(), 0);

            if (null != packageInfo)
            {
                return packageInfo.versionCode;
            }
        } catch (Exception e)
        {
            Log.e("versionmessage", e.getMessage());
        }

        return 0;
    }

}
