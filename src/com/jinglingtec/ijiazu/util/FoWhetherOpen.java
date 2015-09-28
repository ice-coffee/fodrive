package com.jinglingtec.ijiazu.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.List;

/**
 * 判断app是否开启
 */
public class FoWhetherOpen
{

    private ActivityManager mAm;

    public FoWhetherOpen(Context context)
    {
        mAm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    // 运行中的任务
    public boolean showRunningTasks()
    {

        if (null == mAm)
        {
            return false;
        }

        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);

        if (null == taskList)
        {
            return false;
        }

        for (ActivityManager.RunningTaskInfo rti : taskList)
        {

            String packageName = rti.baseActivity.getPackageName();

            if (FoConstants.QTFM.equals(packageName))
            {
                return true;
            }
        }

        return false;
    }

}
