package com.jinglingtec.ijiazu.invokeApps.music;

import android.content.Context;
import cn.kuwo.autosdk.api.KWAPI;
import cn.kuwo.autosdk.api.PlayState;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.invokeApps.MusicAdapter;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoMusicOrFM;
import com.jinglingtec.ijiazu.util.FoSettingOrDown;
import com.jinglingtec.ijiazu.util.TTSPlayerUtil;

import java.util.List;

public class InvokeKuwo implements MusicAdapter
{
    // Kuwo API instance
    private KWAPI mKwapi = null;
    // is kuwo music playing?
    // Note: it's not accuracy
    private boolean isPlaying = false;

    @Override
    public void initialize(Context ctx)
    {
        if (null == mKwapi)
        {
            // Kuwo API Key
            final String KUWO_API_KEY = "auto";
            mKwapi = KWAPI.createKWAPI(ctx, KUWO_API_KEY);
        }
    }

    @Override
    public void singleClick(Context ctx)
    {

        if (!checkHave(ctx))
        {
            //提示用户下载
            FoSettingOrDown.remindUsers(ctx, FoConstants.MUSIC);
            return;
        }

        if (null == mKwapi)
        {
            //            TTSPlayerUtil.play(ctx, ctx.getResources().getString(R.string.open_music));
            initialize(ctx);
            mKwapi.startAPP(ctx, true);
        }
        if (null != mKwapi)
        {
            mKwapi.startAPP(ctx, true);
            if (!isPlaying)
            {
                // not playing right now, start to play
                //                mKwapi.setPlayState(ctx, PlayState.STATE_PLAY);
                isPlaying = true;
            }
            else
            {
                // it's in playing state, so play next
                mKwapi.setPlayState(ctx, PlayState.STATE_NEXT);
            }
        }
    }

    @Override
    public void doubleClick(Context ctx)
    {
        singleClick(ctx);
        // switch to next channel
        // Kuwo not supports this actions, so keep blank here

        //        if (null == mKwapi)
        //        {
        //            initialize(ctx);
        //        }
        //        if (null != mKwapi)
        //        {
        //            mKwapi.startAPP(ctx, true);
        //            mKwapi.setPlayState(ctx, PlayState.STATE_NEXT);
        //        }
    }

    @Override
    public void longPressed(Context ctx)
    {
        if (null != mKwapi)
        {
            mKwapi.setPlayState(ctx, PlayState.STATE_PAUSE);
            isPlaying = false;
        }
    }

    @Override
    public void release(Context ctx)
    {
        if (null != mKwapi)
        {
            mKwapi.exitAPP(ctx);
            mKwapi = null;
            isPlaying = false;
        }
    }

    /**
     * 音乐app检测
     * false 没有
     */
    public boolean checkHave(Context ctx)
    {
        boolean haveFm = false;
        FoMusicOrFM mf = new FoMusicOrFM(ctx, FoConstants.MUSIC);

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

}
