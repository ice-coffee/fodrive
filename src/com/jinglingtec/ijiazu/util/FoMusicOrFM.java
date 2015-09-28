package com.jinglingtec.ijiazu.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

/**
 * Created by coffee on 14-11-24.
 */
public class FoMusicOrFM
{
    private Context context;

    private String mf;

    private String[] musicApp = {FoConstants.KWMUSIC, "aaa", "bbb"};
    private int[] musicAppVersion = {6600};
    private String[] fmApp = {FoConstants.QTFM, "aaa", "bbb"};
    private int[] fmAppVersion = {460};

    private List<String> appList = null;

    public FoMusicOrFM(Context context, String mf)
    {
        this.context = context;
        this.mf = mf;

        appList = getAppList();
    }

    public List<String> getAppList()
    {

        List<String> list = new ArrayList<String>();

        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packages.size(); i++)
        {
            PackageInfo packageInfo = packages.get(i);

            String appName = packageInfo.applicationInfo.packageName;
            //            String name = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
            //
            //            Log.e("appanmeandname",name+appName);

            if (null == list)
            {
                return null;
            }

            if (null != appName)
            {
                list.add(appName);
            }
        }

        return list;

    }

    public List<Boolean> getHaveAppList()
    {
        if (null == appList)
        {
            return null;
        }

        List<Boolean> haveList = new ArrayList<Boolean>();

        if (FoConstants.MUSIC.equals(mf))
        {
            for (int j = 0; j < musicApp.length; j++)
            {
                String listAppName = musicApp[j];

                if (appList.contains(listAppName))
                {
                    haveList.add(true);
                }
                else
                {
                    haveList.add(false);
                }
            }

            return haveList;
        }
        else if (FoConstants.FM.equals(mf))
        {
            for (int j = 0; j < fmApp.length; j++)
            {
                String listAppName = fmApp[j];
                if (appList.contains(listAppName))
                {
                    haveList.add(true);
                }
                else
                {
                    haveList.add(false);
                }
            }

            return haveList;
        }


        return null;
    }

}
