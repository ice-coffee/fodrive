package com.jinglingtec.ijiazu;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.baidu.mapapi.SDKInitializer;
import com.fodrive.demo.service.CLog;
import com.fodrive.demo.service.SaveLog;
import com.fodrive.demo.service.Service_SaveLog;
import com.jinglingtec.ijiazu.invokeApps.KeyActionCenter;
import com.jinglingtec.ijiazu.services.IjiazuService;
import com.jinglingtec.ijiazu.util.FoLogger;

public class IjiazuApp extends Application
{
    private final String TAG = "IjiazuApp";

    private static IjiazuApp instance = null;

    // broadcast receiver to monitor the hardware states
    //    private HardwareStateReceiver hardwareStateReceiver = null;

    public IjiazuApp()
    {
        Log.d(TAG, "constructor");
        //        FoLogger.addLog(TAG + " Constructor");
        instance = this;
    }

    public static Context getContext()
    {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate()
    {
        Log.d(TAG, "onCreate");
        //        FoLogger.addLog(TAG + " onCreate");
        super.onCreate();

        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);

        startService(new Intent(this, IjiazuService.class));

        KeyActionCenter.register(this);

        if (FoLogger.isDebuggable)
        {
            bindService(new Intent(this, Service_SaveLog.class), connSaveLog, Context.BIND_AUTO_CREATE);
        }
    }

    // save log service connection
    public ServiceConnection connSaveLog = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            CLog.i(TAG, "ServiceConnection save log-> onServiceConnected");
            mSaveLog = SaveLog.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName className)
        {
            CLog.i(TAG, "onServiceDisconnected savelog-> onServiceDisconnected");
        }
    };

    /**
     * 日志保存服务绑定
     */
    public SaveLog mSaveLog;

}
