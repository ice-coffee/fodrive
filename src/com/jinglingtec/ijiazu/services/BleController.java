package com.jinglingtec.ijiazu.services;

import android.content.Context;
import com.jinglingtec.ijiazu.bluetooth.BluetoothThread;

public class BleController //implements IjiazuServiceController
{
    //    private final String TAG = "BleController";

    // singleton instance
    private static BleController instance = null;

    // context of fodrive service
    private Context myContext = null;

    // get ble controller instance
    public static BleController getInstance()
    {
        if (null == instance)
        {
            instance = new BleController();
        }
        return instance;
    }

    // destroy
    public static void destroy()
    {
        BluetoothThread.releaseThread();
        if (null != instance)
        {
            instance.reset();
            instance = null;
        }
    }

    // private constructor, avoid create instance outside
    private BleController()
    {
    }

    // init ble controller
    public void initBleController(final Context ctx)
    {
        this.myContext = ctx;

        // create bluetooth thread
        BluetoothThread blethread = BluetoothThread.getInstance();
        //        blethread.setParams(this.myContext, messageHandler);
        blethread.setParams(this.myContext);
        blethread.start();
    }

    // reset this instance
    private void reset()
    {
        this.myContext = null;
    }

}

