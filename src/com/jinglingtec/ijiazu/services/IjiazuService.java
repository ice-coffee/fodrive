package com.jinglingtec.ijiazu.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.jinglingtec.ijiazu.util.FoLogger;
import com.jinglingtec.ijiazu.util.FoUtil;


public class IjiazuService extends Service
{
    private final String TAG = "IjiazuService";

    //
    private final IjiazuServiceBinder IjiazuServiceBinder = new IjiazuServiceBinder();
    // ble controller
    //    private BleController bleController = null;

    @Override
    public void onCreate()
    {
        FoUtil.printProcIDandName(this, TAG);
        Log.d(TAG, "onCreate start");
        //        FoLogger.addLog(TAG + " onCreate");
        //        super.onCreate();

        BleController bleController = BleController.getInstance();
        bleController.initBleController(this);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(TAG, "on bind");
        //        FoLogger.addLog(TAG + " onBind");
        return this.IjiazuServiceBinder;
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "on destroy");
        //        FoLogger.addLog(TAG + " onDestroy");
        //        if (null != bleController)
        //        {
        //            bleController.destroyBleController();
        //            bleController = null;
        //        }
        BleController.destroy();
        //        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "on start command");
        return START_STICKY;
    }

    // IjiazuServiceBinder to get service handler
    public class IjiazuServiceBinder extends Binder
    {
        public IjiazuService getService()
        {
            Log.d(TAG, "get ijiazu service");
            //            FoLogger.addLog(TAG + " getService");
            return IjiazuService.this;
        }
    }

    // get the service controller
    //    public IjiazuServiceController getServiceController()
    //    {
    //        Log.d(TAG, "get ijiazu service controller");
    //        return BleController.getInstance();
    //    }

    // scan ble device
    //    public void scanBleDevice()
    //    {
    //        if (null != bleController)
    //        {
    //            bleController.scanBleDevice();
    //        }
    //    }
}
