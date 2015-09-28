package com.jinglingtec.ijiazu.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.activity.RegisterEcarActivity;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.BaiduNavimainActivity;
import com.jinglingtec.ijiazu.invokeApps.fm.FMActivity;
import com.jinglingtec.ijiazu.invokeApps.music.MusicActivity;
import com.jinglingtec.ijiazu.invokeApps.telephone.TelephoneEditActivity;

/**
 * Created by coffee on 14-12-11.
 */
public class FoSettingOrDown
{

    public static void remindUsers(Context ctx, String key)
    {
        if (FoConstants.FM.equals(key))
        {

            String localClassName = getLocalClassName(ctx);
            if (null == localClassName)
            {
                return;
            }
            if (!FMActivity.class.getName().equals(localClassName))
            {
                Intent intent = new Intent(ctx, FMActivity.class);
                intent.putExtra(FoConstants.FC, FoConstants.FM);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
            TTSPlayerUtil.play(ctx, ctx.getResources().getString(R.string.please_download_fm));
        }
        else if (FoConstants.MUSIC.equals(key))
        {

            String localClassName = getLocalClassName(ctx);
            if (null == localClassName)
            {
                return;
            }
            if (!MusicActivity.class.getName().equals(localClassName))
            {
                Intent intent = new Intent(ctx, MusicActivity.class);
                intent.putExtra(FoConstants.FC, FoConstants.MUSIC);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
            TTSPlayerUtil.play(ctx, ctx.getResources().getString(R.string.please_download_music));
        }
        else if (FoConstants.TELEPHONE.equals(key))
        {

            //            String localClassName = getLocalClassName(ctx);
            //            if (null == localClassName)
            //            {
            //                return;
            //            }
            //            if (!TelephoneEditActivity.class.getName().equals(localClassName))
            {
                Intent intent = new Intent(ctx, TelephoneEditActivity.class);
                intent.putExtra(FoConstants.TELEPHONE_SETTING, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
            TTSPlayerUtil.play(ctx, ctx.getResources().getString(R.string.set_the_phone));
        }
        else if (FoConstants.NAVI.equals(key))
        {

            String localClassName = getLocalClassName(ctx);
            if (null == localClassName)
            {
                return;
            }
            if (!BaiduNavimainActivity.class.getName().equals(localClassName))
            {
                Intent intent = new Intent(ctx, BaiduNavimainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
            TTSPlayerUtil.play(ctx, ctx.getResources().getString(R.string.please_set_end_address));
        }
        else if (FoConstants.ECAR.equals(key))
        {

            String localClassName = getLocalClassName(ctx);
            if (null == localClassName)
            {
                return;
            }
            if (!RegisterEcarActivity.class.getName().equals(localClassName))
            {
                Intent intent = new Intent(ctx, RegisterEcarActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
            TTSPlayerUtil.play(ctx, ctx.getResources().getString(R.string.please_register_ecar));
        }
        else if (FoConstants.NAVI_ECAR.equals(key))
        {

            String localClassName = getLocalClassName(ctx);
            if (null == localClassName)
            {
                return;
            }
            if (!BaiduNavimainActivity.class.getName().equals(localClassName))
            {
                Intent intent = new Intent(ctx, BaiduNavimainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
            TTSPlayerUtil.play(ctx, ctx.getResources().getString(R.string.please_set_start_address));
        }
    }

    //判断当前activity
    public static String getLocalClassName(Context ctx)
    {
        if (null != ctx)
        {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (null != am)
            {
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                if (null != cn)
                {
                    return cn.getClassName();
                }
            }
        }
        return null;
    }
}
