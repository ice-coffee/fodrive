package com.jinglingtec.ijiazu.invokeApps.music;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.jinglingtec.ijiazu.invokeApps.MusicAdapter;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoSettingOrDown;

public class InvokeDuoMi implements MusicAdapter
{
    private final int STARTDUOMI = 0;//开启并播放
    private final int PLAYDUOMI = 1;//暂停/播放
    private final int NEXTDUOMI = 2;//下一首
    private final int NEXTLISTDUOMI = 3;//下个列表

    private String TAG = "duomiadapter";

    // is duomi music playing?
    // Note: it's not accuracy
    private boolean isPlaying = false;

    @Override
    public void initialize(Context ctx)
    {

    }

    /**
     * 如果播放状态isPlaying为true则开启多米并播放，如果为false则播放下一首
     * 经过测试发现下一首和下个列表都有开启多米app的功能
     *
     * @param ctx
     */
    @Override
    public void singleClick(Context ctx)
    {
        //检测多米音乐是否已经在本机安装，如果没有安装那么提示用户下载
        if (!checkApp(ctx))
        {
            //提示用户下载
            FoSettingOrDown.remindUsers(ctx, FoConstants.MUSIC);
            isPlaying = false;
            return;
        }

        if (!isPlaying)
        {
            //开启多米并播放
            runningDuoMi(ctx, STARTDUOMI);

            isPlaying = true;
        }
        else
        {
            // 播放下一首
            runningDuoMi(ctx, NEXTDUOMI);
        }

    }

    /**
     * 开启多米并播放
     * 修改isPlaying多米音乐的播放状态
     * 经过测试发现下一首和下个列表都有开启多米app的功能
     *
     * @param ctx
     */
    @Override
    public void doubleClick(Context ctx)
    {
        //检测多米音乐是否已经在本机安装，如果没有安装那么提示用户下载
        if (!checkApp(ctx))
        {
            //提示用户下载
            FoSettingOrDown.remindUsers(ctx, FoConstants.MUSIC);
            isPlaying = false;
            return;
        }

        //开启多米并播放
        runningDuoMi(ctx, NEXTLISTDUOMI);

        //修改播放状态
        if (!isPlaying)
        {
            isPlaying = true;
        }
    }

    /**
     * 暂停多米播放
     * 修改isPlaying多米音乐的播放状态
     *
     * @param ctx
     */
    @Override
    public void longPressed(Context ctx)
    {
        //检测多米音乐是否已经在本机安装，如果没有安装那么提示用户下载
        if (!checkApp(ctx))
        {
            //提示用户下载
            FoSettingOrDown.remindUsers(ctx, FoConstants.MUSIC);
            isPlaying = false;
            return;
        }

        if (isPlaying)
        {
            runningDuoMi(ctx, PLAYDUOMI);
            isPlaying = false;
        }
    }

    @Override
    public void release(Context ctx)
    {

    }

    /**
     * 检查此多米入口是否为空，若为空则说明本机还没有安装多米app，反之可以进行操作
     *
     * @param ctx
     *
     * @return
     */
    public boolean checkApp(Context ctx)
    {
        //多米包名
        final String packageName = "com.duomi.android";
        //检查此入口是否为空，若为空则说明本机不存在多米app，反之可以进行之后的操作
        if (ctx.getPackageManager().getLaunchIntentForPackage(packageName) == null)
        {
            return false;
        }

        return true;
    }

    public void runningDuoMi(Context ctx, int mode)
    {
        final String app_id = "28a48fb7cdad4098a523a83e7422ed7c";

        Intent intent = new Intent();
        intent.addCategory("android.intent.category.OPENABLE");
        intent.addCategory("android.intent.category.BROWSABLE");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("app_id", app_id);
        //调用路径必须正确
        switch (mode)
        {
            case STARTDUOMI:
                intent.setData(Uri.parse("duomi://invoke_action=play-start"));
                break;
            case NEXTLISTDUOMI:
                intent.setData(Uri.parse("duomi://invoke_action=next-play-list"));//暂时无法调用
                break;
            case NEXTDUOMI:
                intent.setData(Uri.parse("duomi://invoke_action=play-next-song"));
                break;
            case PLAYDUOMI:
                intent.setData(Uri.parse("duomi://invoke_action=play-toggle"));
                break;
        }
        ctx.startActivity(intent);
    }

}
