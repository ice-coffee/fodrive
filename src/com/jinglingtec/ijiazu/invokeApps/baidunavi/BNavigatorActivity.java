package com.jinglingtec.ijiazu.invokeApps.baidunavi;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.comapi.mapcontrol.BNMapController;
import com.baidu.navisdk.comapi.routeplan.BNRoutePlaner;
import com.baidu.navisdk.comapi.tts.BNTTSPlayer;
import com.baidu.navisdk.comapi.tts.BNavigatorTTSPlayer;
import com.baidu.navisdk.comapi.tts.IBNTTSPlayerListener;
import com.baidu.navisdk.model.datastruct.LocData;
import com.baidu.navisdk.model.datastruct.SensorData;
import com.baidu.navisdk.ui.routeguide.BNavigator;
import com.baidu.navisdk.ui.routeguide.IBNavigatorListener;
import com.baidu.navisdk.ui.widget.RoutePlanObserver;
import com.baidu.navisdk.ui.widget.RoutePlanObserver.IJumpToDownloadListener;
import com.baidu.nplatform.comapi.map.MapGLSurfaceView;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.activity.BaseActivity;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoPreference;

/**
 * 导航实现主类com.jinglingtec.ijiazu.invokeApps.baidunavi.BNavigatorActivity@426f88c8
 * 基本没有改动，除instance作为结束的调用外
 */

public class BNavigatorActivity extends BaseActivity
{

    public static Activity instance = null;

    private String road = null;

    //    private String endAddressName = null;

    private int voice = 0;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        instance = this;

        Intent intent = getIntent();
        if (null != intent)
        {
            //            endAddressName = intent.getStringExtra(FoConstants.NAVI_NAME);
        }

        //创建NmapView
        MapGLSurfaceView nMapView = BaiduNaviManager.getInstance().createNMapView(this);

        //创建导航视图
        View navigatorView = BNavigator.getInstance().init(BNavigatorActivity.this, getIntent().getExtras(), nMapView);

        //填充视图
        setContentView(navigatorView);
        BNavigator.getInstance().setListener(mBNavigatorListener);
        BNavigator.getInstance().startNav();

        //记录导航状态
        FoPreference.putBoolean(FoConstants.IS_NAVING, true);
        String navi_to = FoPreference.getString(FoConstants.NAVI_TO);
        if (FoConstants.NAVI_HOME.equals(navi_to))
        {
            FoPreference.putBoolean(FoConstants.NAVI_HOME, true);
        }

        if (FoConstants.NAVI_COMPANY.equals(navi_to))
        {
            FoPreference.putBoolean(FoConstants.NAVI_COMPANY, true);
        }

        Log.e("navigatorint", "bnavigator text :" + (voice++));

        final String timeAndMileage = null;

        // 初始化TTS. 开发者也可以使用独立TTS模块，不用使用导航SDK提供的TTS
        BNTTSPlayer.initPlayer();
        //设置TTS播放回调
        BNavigatorTTSPlayer.setTTSPlayerListener(new IBNTTSPlayerListener()
        {

            @Override
            public int playTTSText(String arg0, int arg1)
            {
                if (getResources().getString(R.string.navi_start).equals(arg0))
                {
                    String navi_name = FoPreference.getString(FoConstants.NAVI_NAME);
                    if (null != navi_name)
                    {
                        String distance_time = FoPreference.getString(FoConstants.NAVI_INFO);
                        arg0 = arg0 + getResources().getString(R.string.destination) + navi_name + "," + distance_time;
                    }
                }

                String[] roadMessages = arg0.split(",");
                if (getResources().getString(R.string.planSuccess).equals(roadMessages[0]))
                {
                    arg0 = roadMessages[1] + roadMessages[2];
                    FoPreference.putString(FoConstants.NAVI_INFO, arg0);

                    return 0;
                }

                //                Log.e("mileagemessgae", arg0);

                int play = BNTTSPlayer.playTTSText(arg0, arg1);

                Log.e("navigatorint", "play text :" + (voice++));
                //                Log.e("navigatorint","play string :"+endAddressName);

                //开发者可以使用其他TTS的API
                return play;
            }

            @Override
            public void phoneHangUp()
            {
                //手机挂断
            }

            @Override
            public void phoneCalling()
            {
                //通话中
            }

            @Override
            public int getTTSState()
            {
                //开发者可以使用其他TTS的API,
                return BNTTSPlayer.getTTSState();
            }
        });

        BNRoutePlaner.getInstance().setObserver(new RoutePlanObserver(this, new IJumpToDownloadListener()
        {

            @Override
            public void onJumpToDownloadOfflineData()
            {
                // TODO Auto-generated method stub

            }
        }));

    }

    private IBNavigatorListener mBNavigatorListener = new IBNavigatorListener()
    {

        @Override
        public void onYawingRequestSuccess()
        {
            // TODO 偏航请求成功

        }

        @Override
        public void onYawingRequestStart()
        {
            // TODO 开始偏航请求

        }

        @Override
        public void onPageJump(int jumpTiming, Object arg)
        {
            // TODO 页面跳转回调
            if (IBNavigatorListener.PAGE_JUMP_WHEN_GUIDE_END == jumpTiming)
            {
                finish();
            }
            else if (IBNavigatorListener.PAGE_JUMP_WHEN_ROUTE_PLAN_FAIL == jumpTiming)
            {
                finish();
            }
        }

        @Override
        public void notifyGPSStatusData(int arg0)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void notifyLoacteData(LocData arg0)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void notifyNmeaData(String arg0)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void notifySensorData(SensorData arg0)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void notifyStartNav()
        {
            // TODO Auto-generated method stub
            BaiduNaviManager.getInstance().dismissWaitProgressDialog();
        }

        @Override
        public void notifyViewModeChanged(int arg0)
        {
            // TODO Auto-generated method stub

        }

    };

    @Override
    public void onResume()
    {
        BNavigator.getInstance().resume();
        super.onResume();
        BNMapController.getInstance().onResume();
    }

    @Override
    public void onPause()
    {
        BNavigator.getInstance().pause();
        BNavigatorTTSPlayer.pauseVoiceTTSOutput();
        super.onPause();
        BNMapController.getInstance().onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        BNavigator.getInstance().onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    public void onBackPressed()
    {
        BNavigator.getInstance().onBackPressed();
    }

    @Override
    public void onDestroy()
    {
        //记录导航状态
        FoPreference.putBoolean(FoConstants.IS_NAVING, false);
        FoPreference.putBoolean(FoConstants.NAVI_HOME, false);
        FoPreference.putBoolean(FoConstants.NAVI_COMPANY, false);

        //        endAddressName = null;
        voice = 0;

        BNavigator.destory();
        BNRoutePlaner.getInstance().setObserver(null);
        super.onDestroy();
    }
}
