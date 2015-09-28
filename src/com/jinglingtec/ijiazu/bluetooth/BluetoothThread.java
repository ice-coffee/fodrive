package com.jinglingtec.ijiazu.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.jinglingtec.ijiazu.bluetooth.bleutil.UtilBleController;
import com.jinglingtec.ijiazu.util.FoUtil;

public class BluetoothThread extends Thread
{
    public final String TAG = "BluetoothThread";
    private static BluetoothThread bluetoothThread = null;

    // service context point to the owner service of myself
    private Context myCtx = null;

    // my handler, that means, handler of BluetoothThread
    private Handler handlerBleThread = null;
    // handler of ble controller, send message to ble controller via this one
    //    private Handler handlerBleController = null;

    // buletooth utility instance
    private UtilBleController utilBleController = null;

    // sign, the ble device conntected or not
    //    private boolean isBleConntected = false;
    // sign, the general bluetooth device enabled/disabled
    private boolean isBluetoothEnabled = false;

    // get the bluetooth device enable/disable event broadcast
    private BluetoothStateReceiver bluetoothStateReceiver = null;

    //
    private BLEDeviceStateReceiver bleDeviceStateReceiver = null;

    // get bluetooth thread instance
    public static BluetoothThread getInstance()
    {
        if (null == bluetoothThread)
        {
            bluetoothThread = new BluetoothThread();
        }
        return bluetoothThread;
    }

    // kill bluetooth thread
    public static void releaseThread()
    {
        if (null != bluetoothThread)
        {
            BluetoothThread thread = bluetoothThread;
            bluetoothThread = null;
            if (null != thread.handlerBleThread)
            {
                thread.handlerBleThread.sendEmptyMessage(BleCommands.KILL_BLE_THREAD);
            }
        }
    }

    // private constructor
    private BluetoothThread()
    {
    }

    // set outter parameters
    public boolean setParams(Context contx/*, Handler bleHandler*/)
    {
        this.myCtx = contx;
        //        this.handlerBleController = bleHandler;
        return true;
    }


    // main run method
    public void run()
    {
        Log.d(TAG, " start to run");
        try
        {
            //            Logger.dInfo(TAG, "enter run() method");
            Looper.prepare();
            initThread();
            Looper.loop();

            //            Logger.dInfo(TAG, "exit run() method");
            reset();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.d(TAG, " exit");
    }

    // initialize this thread
    private void initThread()
    {
        this.isBluetoothEnabled = FoUtil.isBluetoothEnabled(this.myCtx);

        utilBleController = UtilBleController.getInstance();
        utilBleController.setParams(this.myCtx, new BleDeviceListener());

        // is it need move this one into to run() ???
        handlerBleThread = new Handler()
        {
            public void handleMessage(Message msg)
            {
                handleBleControllerMessage(msg);
            }
        };

        loopingBleDevice();

        this.registerBluetoothReceiver();
        this.registerBleDeviceReceiver();
    }

    // quit the ble thread
    private void quitBleThread()
    {
        UtilBleController.destroy();
        this.unregisterBluetoothReceiver();
        this.unregisterBleDeviceReceiver();

        this.utilBleController = null;
        //        this.isBleConntected = false;

        Looper.myLooper().quit();
    }

    // reset this object
    private void reset()
    {
        myCtx = null;
        //        handlerBleController = null;
        utilBleController = null;
        //        isBleConntected = false;
    }

    // deal with message that sent from the ble service
    private void handleBleControllerMessage(Message msg)
    {
        switch (msg.what)
        {
            case BleCommands.SCAN_BLE_DEVICE:
                //                if ((null != utilBleController) && !utilBleController.isBleConnected() &&
                //                        FoUtil.isBluetoothEnabled(this.myCtx))
                if (null != utilBleController)
                {
                    Log.d(TAG, "scan ble device");
                    // start to scan BLE device
                    utilBleController.startScanBleDevice();
                }
                break;
            case BleCommands.KILL_BLE_THREAD:
                quitBleThread();
                break;
            case BleCommands.READ_SPECIFIC_PROPERTY:
                if (null != utilBleController)
                {
                    utilBleController.readMoreData();
                }
                break;
            default:
                break;
        }
    }

    // send message to Ble controller
    //    private void sendMsg2BleController(int msgID, int arg_1, Object objt)
    //    {
    //        if (null == this.handlerBleController)
    //        {
    //            return;
    //        }
    //
    //        Message msg = this.handlerBleController.obtainMessage();
    //        msg.what = msgID;
    //        msg.arg1 = arg_1;
    //        msg.obj = objt;
    //        msg.sendToTarget();
    //    }

    // listener class to get the notification of ble event
    class BleDeviceListener implements BleListener
    {
        // ble connected
        //        @Override
        //        public void conntected_one()
        //        {
        //            Log.d(TAG, "ble connected, phase one");
        //        }

        @Override
        public void conntected()
        {
            Log.d(TAG, "ble connected, phase two");
            //            isBleConntected = true;
            //            sendMsg2BleController(BleCommands.BLE_CONNECTED, 0, null);
            handlerBleThread.sendEmptyMessage(BleCommands.READ_SPECIFIC_PROPERTY);
            broadcastBleConnectState();
        }

        // ble dis connected
        @Override
        public void disconnected()
        {
            Log.d(TAG, "ble dis-connected");
            //            isBleConntected = false;
            //            sendMsg2BleController(BleCommands.BLE_DIS_CONNECTED, 0, null);
            broadcastBleConnectState();

            loopingBleDevice();
        }

        // ble characteristic changed
        // paired BLE device initiate this action
        @Override
        public void characteristicChanged(byte[] data)
        {
            broadcastKeyAction(data);
            //            sendMsg2BleController(BleCommands.BLE_CHARACTERISTIC_CHANGED, 0, data);
            //            if (FoLogger.isDebuggable)
            //            {
            //                String tmp = "";
            //                for (byte by : data)
            //                {
            //                    tmp += by;
            //                }
            //                Log.d(TAG, "ble device characteristicChanged, the data is: " + tmp);
            //            }
        }

        // broadcast the ble connect state to activity
        private void broadcastKeyAction(byte[] data)
        {
            if (null == myCtx)
            {
                return;
            }

            //            FoUtil.printProcIDandName(myCtx, "22222222222222");
            final Intent intent = new Intent(BleConstants.KEY_ACTION_MONITOR_FILTER);
            intent.putExtra(BleConstants.KEY_ACTION_DATA, data);
            //            intent.putExtra(BleConstants.KEY_ACTION_DATA_MAGIC, android.os.Process.myPid());

            myCtx.sendBroadcast(intent);
        }
    }


    // looping to scan the ble device
    private void loopingBleDevice()
    {
        if (!isBluetoothEnabled)
        {
            return;
        }

        final Runnable loopingRunnable = new Runnable()
        {
            // the interval time
            // that looping ble device if the bluetook is enable
            // Note: this interval had better more than 1 second than BLE Device Discovery process
            // Refer to: DeviceDiscoveryService.java: SCAN_PERIOD
            private final int LOOPING_BLE_DEVICE_INTERVAL = 8 * 1000;

            private Runnable selfRunnable = this;

            @Override
            public void run()
            {
                if (isBluetoothEnabled && (null != selfRunnable) && (null != utilBleController) && !utilBleController.isBleConnected())
                {
                    // bluetooth is enabled
                    // and ble deice is not connected, continue to scan it
                    handlerBleThread.postDelayed(selfRunnable, LOOPING_BLE_DEVICE_INTERVAL);
                    handlerBleThread.sendEmptyMessage(BleCommands.SCAN_BLE_DEVICE);
                }
                else
                {
                    handlerBleThread.removeCallbacks(selfRunnable);
                    selfRunnable = null;
                }
            }
        };

        //        handlerBleThread.post(loopingRunnable);
        handlerBleThread.postDelayed(loopingRunnable, 10); // delay 10 milliseconds
    }


    // broadcast receiver of bluetooth state
    private class BluetoothStateReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

            switch (state)
            {
                case BluetoothAdapter.STATE_TURNING_ON:
                    //                    Log.i(TAG, "Bluetooth turning on");
                    break;
                case BluetoothAdapter.STATE_ON:
                    //                    Log.i(TAG, "Bluetooth on");
                    onBluetoothEnabled();
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    //                    Log.i(TAG, "Bluetooth turning off");
                    isBluetoothEnabled = false;
                    break;
                case BluetoothAdapter.STATE_OFF:
                    //                    Log.i(TAG, "Bluetooth off");
                    //remove to STATE_TURNING off by lm
                    //bluetoothDisabled();
                    onBluetoothDisabled();
                    break;
                default:
                    break;
            }
        }

    }

    // bluetooth device enabled
    private void onBluetoothEnabled()
    {
        Log.i(TAG, "手机蓝牙打开 . . .");
        isBluetoothEnabled = true;
        loopingBleDevice();
    }

    // bluetooth device disabled
    private void onBluetoothDisabled()
    {
        Log.i(TAG, "手机蓝牙关闭 . . .");
        isBluetoothEnabled = false;
        //        destroyGizwitController();
        UtilBleController.destroy();
        //        sendMsg2BleController(BleCommands.BLE_DIS_CONNECTED, 0, null);
        broadcastBleConnectState();
    }


    // register bluetooth receiver
    private void registerBluetoothReceiver()
    {
        if (null != this.myCtx)
        {
            // monitor the bluetooth state change event
            this.bluetoothStateReceiver = new BluetoothStateReceiver();
            this.myCtx.registerReceiver(this.bluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        }
    }

    // unregister bluetooth receiver
    private void unregisterBluetoothReceiver()
    {
        if ((null != this.myCtx) && (null != this.bluetoothStateReceiver))
        {
            this.myCtx.unregisterReceiver(bluetoothStateReceiver);
            this.bluetoothStateReceiver = null;
        }
    }


    private void registerBleDeviceReceiver()
    {
        if (null != this.myCtx)
        {
            // monitor the bluetooth state change event
            this.bleDeviceStateReceiver = new BLEDeviceStateReceiver();
            this.myCtx.registerReceiver(this.bleDeviceStateReceiver, new IntentFilter(BleConstants.BLE_DEVICE_STATE));
        }
    }

    // unregister bluetooth receiver
    private void unregisterBleDeviceReceiver()
    {
        if ((null != this.myCtx) && (null != this.bleDeviceStateReceiver))
        {
            this.myCtx.unregisterReceiver(bleDeviceStateReceiver);
        }
        this.bleDeviceStateReceiver = null;
    }


    // broadcast receiver of report ble device state
    private class BLEDeviceStateReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            broadcastBleConnectState();
        }

    }

    // broadcast the ble connect state to activity
    private void broadcastBleConnectState()
    {
        if ((null == myCtx) || (null == utilBleController))
        {
            return;
        }

        final Intent intent = new Intent(BleConstants.HARDWARE_MONITOR_FILTER);
        intent.putExtra(BleConstants.BLE_DEVICE_NOTIFICATION, BleConstants.BLE_NOTIFY_TYPE_STATE_CHANGED);
        intent.putExtra(BleConstants.BLE_STATE_CHANGED, utilBleController.isBleConnected());
        myCtx.sendBroadcast(intent);
    }
}
