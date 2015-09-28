package com.jinglingtec.ijiazu.invokeApps.music;

import android.content.Context;
import android.content.Intent;

import com.jinglingtec.ijiazu.invokeApps.MusicAdapter;

public class InvokeMeileScene implements MusicAdapter
{
    // Target app package name
    final static String PACKAGE_NAME = "com.meile.mobile.scene";
    // fodrive application ID
    final static String FODRIVE_APPID = "a1d182f150f9a4fb78c0cd8bc425abddc";
    // parameter key : client id
    final static String CLIENT_ID = "client_id";
    // parameter key : invoke action
    final static String INVOKE_ACTION = "invoke_action";

    // action name : start to play
    final static String PLAY_START = "play-start";
    // action name : play next music
    final static String PLAY_NEXT = "play-next";
    // action name : toggle play/pause state
    final static String PLAY_TOGGLE = "play-toggle";

    @Override
    public void initialize(Context ctx)
    {
    }

    @Override
    public void singleClick(Context ctx)
    {
        launchMeilePlayer(ctx);
    }

    @Override
    public void doubleClick(Context ctx)
    {
        meileNext(ctx);
    }

    @Override
    public void longPressed(Context ctx)
    {
        meilePausePlay(ctx);
    }

    @Override
    public void release(Context ctx)
    {

    }

    public static void launchMeilePlayer(Context ctx)
    {
        Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
        if (intent != null)
        {
            intent.putExtra(CLIENT_ID, FODRIVE_APPID);
            intent.putExtra(INVOKE_ACTION, PLAY_START);
            ctx.startActivity(intent);
        }
    }


    public void meilePausePlay(Context ctx)
    {
        Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
        if (intent != null)
        {
            intent.putExtra(CLIENT_ID, FODRIVE_APPID);
            intent.putExtra(INVOKE_ACTION, PLAY_TOGGLE);
            ctx.startActivity(intent);
        }
    }

    public void meileNext(Context ctx)
    {
        Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
        if (intent != null)
        {
            intent.putExtra(CLIENT_ID, FODRIVE_APPID);
            intent.putExtra(INVOKE_ACTION, PLAY_NEXT);
            ctx.startActivity(intent);
        }
    }

}
