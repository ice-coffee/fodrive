package com.jinglingtec.ijiazu.invokeApps.fm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.jinglingtec.ijiazu.invokeApps.FMAdapter;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoMusicOrFM;
import com.jinglingtec.ijiazu.util.FoSettingOrDown;

import java.util.List;

public class InvokeQingting implements FMAdapter
{
    // is it playing state ?
    private boolean isPlaying = false;

    public final String ACTION_CAR_PLAY_NEXT = "fm.qingting.qtradio.CAR_PLAY_NEXT";//车载接口
    //    public final String ACTION_CAR_PLAY_PRE = "fm.qingting.qtradio.CAR_PLAY_PRE";
    public final String ACTION_CAR_PLAY = "fm.qingting.qtradio.CAR_PLAY";
    public final String ACTION_CAR_PAUSE = "fm.qingting.qtradio.CAR_PAUSE";
    public static final String ACTION_CAR_PLAY_PRE_CATEGORY = "fm.qingting.qtradio.CAR_PLAY_PRE_CATEGORY";
    public static final String ACTION_CAR_PLAY_NEXT_CATEGORY = "fm.qingting.qtradio.CAR_PLAY_NEXT_CATEGORY";

    @Override
    public void initialize(Context ctx)
    {
    }

    @Override
    public void singleClick(Context ctx)
    {
        launchQingting(ctx);
        if (!isPlaying)
        {
            sendAction(ctx, ACTION_CAR_PLAY);
            isPlaying = true;
        }
        else
        {
            sendAction(ctx, ACTION_CAR_PLAY_NEXT);
        }
    }

    @Override
    public void doubleClick(Context ctx)
    {
        //        Log.e("fmlaunch", "doubleclick" + isPlaying);
        launchQingting(ctx);
        if (isPlaying)
        {
            sendAction(ctx, ACTION_CAR_PLAY_NEXT_CATEGORY);
        }
        else
        {
            singleClick(ctx);
        }
    }

    @Override
    public void longPressed(Context ctx)
    {

        sendAction(ctx, ACTION_CAR_PAUSE);

        isPlaying = false;
    }

    @Override
    public void release(Context ctx)
    {
        isPlaying = false;
    }

    // launch Qingting fm application
    private void launchQingting(Context ctx)
    {
        //开始前先检测是否app存在
        if (!checkHave(ctx))
        {
            //            Log.e("fmlaunch", "nothave");
            //提示用户下载
            FoSettingOrDown.remindUsers(ctx, FoConstants.FM);
            return;
        }

        //        if (!isPlaying)
        //        {
        //            FoWhetherOpen open = new FoWhetherOpen(ctx);
        //            if (!open.showRunningTasks())
        //            {
        //                TTSPlayerUtil.play(ctx, ctx.getResources().getString(R.string.open_fm));
        //            }
        //
        //        }
        //        Log.e("fmlaunch","have");
        // Target app package name
        String PACKAGE_NAME = "fm.qingting.qtradio";
        Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
        if (intent != null)
        {
            intent.setAction("fm.qingting.qtradio.CAR_PLAY");
            intent.putExtra("client_id", "a1d182f150f9a4fb78c0cd8bc425abddc");
            intent.putExtra("invoke_action", "play");
            ctx.startActivity(intent);
        }
    }

    /**
     * 电台app检测
     * false 没有
     */
    public boolean checkHave(Context ctx)
    {
        boolean haveFm = false;
        FoMusicOrFM mf = new FoMusicOrFM(ctx, FoConstants.FM);
        List<Boolean> bools = mf.getHaveAppList();
        for (int i = 0; i < bools.size(); i++)
        {
            if (bools.get(i))
            {
                haveFm = true;
            }
        }
        return haveFm;
    }

    // send action to qingting fm
    private void sendAction(Context ctx, String action)
    {
        Intent intent = new Intent();
        intent.setAction(action);
        //        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.sendBroadcast(intent);
    }
}
