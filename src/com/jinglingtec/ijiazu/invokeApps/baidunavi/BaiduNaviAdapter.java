package com.jinglingtec.ijiazu.invokeApps.baidunavi;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.baidu.navisdk.comapi.tts.BNTTSPlayer;
import com.baidu.navisdk.ui.routeguide.BNavigator;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.invokeApps.NavigatorAdapter;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoPreference;
import com.jinglingtec.ijiazu.util.FoSettingOrDown;

public class BaiduNaviAdapter implements NavigatorAdapter
{

    private String TAG = "beginnavi";

    /**
     * 对于是否正在导航进行判断
     *
     * @return
     */
    private boolean navi(String foc)
    {
        return FoPreference.getBoolean(foc, false);
    }

    @Override
    public void singleClick(final Context ctx)
    {
        Log.e(TAG, "singleClick");

        if (navi(FoConstants.IS_NAVING))
        {
            if (navi(FoConstants.NAVI_HOME))
            {
                return;
            }
            else
            {
                finishNavi(ctx);
            }
        }

        String HomeAddress = FoPreference.getString(FoConstants.HOME);
        String StartAddress = FoPreference.getString(FoConstants.START_ADDRESS);

        Log.e("homeandstart", HomeAddress + StartAddress);

        if (HomeAddress == null || StartAddress == null)
        {
            //提示用户设置
            FoSettingOrDown.remindUsers(ctx, FoConstants.NAVI);
            return;
        }

        FoPreference.putString(FoConstants.NAVI_TO, FoConstants.NAVI_HOME);
        Intent intent = new Intent(ctx, RoutePlanActivity.class);
        intent.putExtra(FoConstants.END_ADDRESS, HomeAddress);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);

    }

    @Override
    public void doubleClick(Context ctx)
    {
        Log.e(TAG, "doubleClick");

        if (navi(FoConstants.IS_NAVING))
        {
            if (navi(FoConstants.NAVI_COMPANY))
            {
                return;
            }
            else
            {
                finishNavi(ctx);
            }
        }

        String companyAddress = FoPreference.getString(FoConstants.COMPANY);
        String StartAddress = FoPreference.getString(FoConstants.START_ADDRESS);
        if (companyAddress == null || StartAddress == null)
        {
            //提示用户设置
            FoSettingOrDown.remindUsers(ctx, FoConstants.NAVI);
            return;
        }

        FoPreference.putString(FoConstants.NAVI_TO, FoConstants.NAVI_COMPANY);
        Intent intent = new Intent(ctx, RoutePlanActivity.class);
        intent.putExtra(FoConstants.END_ADDRESS, companyAddress);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);

    }

    public void longPressed(Context ctx)
    {
        Log.e(TAG, "longPressed");

        finishNavi(ctx);

        //        TTSPlayerUtil.play(ctx, ctx.getResources().getString(R.string.navi_close));
        //         BNTTSPlayer.stopTTS();

    }

    @Override
    public void release()
    {

    }

    private void finishNavi(Context ctx)
    {

        if (!navi(FoConstants.IS_NAVING))
        {
            return;
        }

        if (null != BNavigatorActivity.instance)
        {
            //            BNavigator.getInstance().onBackPressed();
            //            BNTTSPlayer.playTTSText(ctx.getString(R.string.navi_close), 0);
            BNavigatorActivity.instance.finish();
        }

    }

}

