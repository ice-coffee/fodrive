package com.jinglingtec.ijiazu.invokeApps.baidunavi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.navisdk.BNaviEngineManager;
import com.baidu.navisdk.BaiduNaviManager;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.activity.BaseActivity;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils.Address;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils.AddressInfos;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils.RoutePlan;
import com.jinglingtec.ijiazu.util.FoConstants;

/**
 * 蓝牙导航操作中转站
 */

public class RoutePlanActivity extends BaseActivity
{
    private boolean mIsEngineInitSuccess = false;

    private boolean isKey = false;
    private Address address;

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            startNavi();
            super.handleMessage(msg);
        }
    };

    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);

        setContentView(R.layout.activity_baidunavi_routeplan);

        isKey = BaiduNaviManager.getInstance().checkEngineStatus(this);

        if (isKey)
        {
            startNavi();
        }
        else
        {
            init();
        }

    }

    public void init()
    {
        //初始化导航引擎
        BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(), mNaviEngineInitListener, new LBSAuthManagerListener()
        {
            @Override
            public void onAuthResult(int status, String msg)
            {
                String str;
                if (0 == status)
                {
                    str = FoConstants.KEY_OK;
                }
                else
                {
                    str = FoConstants.KEY_FAILURE + "," + msg;
                }
                Log.e("keyvalidation", str);
            }
        });
    }

    /**
     * 如果导航验证完毕那么直接进入算路--导航！
     * 如果导航没有验证成功那么开启一个子线程在其中每0.5秒检测是否验证完毕
     */
    public void startNavi()
    {
        Intent intent = getIntent();
        if (null != intent)
        {
            String endAddr = intent.getStringExtra(FoConstants.END_ADDRESS);

            if (null != endAddr && endAddr.length() > 1)
            {
                address = AddressInfos.getPoiAddress(endAddr);
            }
        }

        Log.e("endaddressend", address.getAddress() + address.getName());

        new RoutePlan(getApplicationContext(), RoutePlanActivity.this, address);

    }

    /**
     * key校验相关的两个函数
     */
    private String getSdcardDir()
    {
        if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment.getExternalStorageState()))
        {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private BNaviEngineManager.NaviEngineInitListener mNaviEngineInitListener = new BNaviEngineManager.NaviEngineInitListener()
    {
        public void engineInitSuccess()
        {
            handler.sendEmptyMessage(0);
            mIsEngineInitSuccess = true;
        }

        public void engineInitStart()
        {
        }

        public void engineInitFail()
        {
        }
    };

    @Override
    protected void onPause()
    {

        super.onPause();
    }
}
