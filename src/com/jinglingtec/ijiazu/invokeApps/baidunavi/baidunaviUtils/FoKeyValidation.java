package com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.navisdk.BNaviEngineManager;
import com.baidu.navisdk.BaiduNaviManager;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.RoutePlanActivity;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoPreference;

/**
 * Created by coffee on 14-11-14.
 */
public class FoKeyValidation
{

    private boolean mIsEngineInitSuccess = false;

    public FoKeyValidation(Activity app)
    {
        //初始化导航引擎
        BaiduNaviManager.getInstance().initEngine(app, getSdcardDir(), mNaviEngineInitListener, new LBSAuthManagerListener()
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
            mIsEngineInitSuccess = true;
        }

        public void engineInitStart()
        {
        }

        public void engineInitFail()
        {
        }
    };
}
