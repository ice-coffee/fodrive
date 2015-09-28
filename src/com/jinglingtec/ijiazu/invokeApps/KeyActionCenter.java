package com.jinglingtec.ijiazu.invokeApps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.jinglingtec.ijiazu.bluetooth.BleConstants;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.BaiduNaviAdapter;
import com.jinglingtec.ijiazu.invokeApps.ecar.EcarAdapter;
import com.jinglingtec.ijiazu.invokeApps.fm.InvokeQingting;
import com.jinglingtec.ijiazu.invokeApps.music.InvokeDuoMi;
import com.jinglingtec.ijiazu.invokeApps.music.InvokeKuwo;
import com.jinglingtec.ijiazu.invokeApps.telephone.PhoneCall;
import com.jinglingtec.ijiazu.invokeApps.telephone.PhoneCallOperator;
import com.jinglingtec.ijiazu.util.FoLogger;
import com.jinglingtec.ijiazu.util.FoUtil;

public class KeyActionCenter
{
    // register key action receiver
    public static void register(Context ctx)
    {
        if (null != instance)
        {
            return;
        }

        // ijiazu main process name
        final String PROCNAME = "com.jinglingtec.ijiazu";
        if (!PROCNAME.equals(FoUtil.getProcessName(ctx, -1)))
        {
            // we ONLY register the KeyActionCenter in the ijiazu main process
            return;
        }

        instance = new KeyActionCenter(ctx);
        instance.registerSelf();

        //        FoUtil.printProcIDandName(ctx, "KeyActionCenter");
    }

    // un-register key action receiver
    public static void unregister()
    {
        if (null == instance)
        {
            return;
        }
        instance.unresisterSelf();
        instance = null;
    }

    // private constructor
    private KeyActionCenter(Context ctxin)
    {
        this.myContext = ctxin;
    }

    // register key action receiver
    private void registerSelf()
    {
        // create & register the key action monitor receiver
        keyActionReceiver = new KeyActionReceiver();
        this.myContext.registerReceiver(keyActionReceiver, new IntentFilter(BleConstants.KEY_ACTION_MONITOR_FILTER));

        bleDeviceStateReciver = new HardwareStateReceiver();
        this.myContext.registerReceiver(bleDeviceStateReciver, new IntentFilter(BleConstants.HARDWARE_MONITOR_FILTER));

        initInvokers();
    }

    // register key action receiver
    private void unresisterSelf()
    {
        // unregister the hardware monitor receiver
        if ((null != bleDeviceStateReciver) && (null != this.myContext))
        {
            this.myContext.unregisterReceiver(this.bleDeviceStateReciver);
        }

        if ((null != keyActionReceiver) && (null != this.myContext))
        {
            this.myContext.unregisterReceiver(this.keyActionReceiver);
        }

        this.bleDeviceStateReciver = null;
        this.keyActionReceiver = null;
        reset();
    }

    // initialize various application invoker
    // such as:  phone call, music, navigation, and so on
    private void initInvokers()
    {
        // setup phone call operator
        this.phoneCallAdapter = new PhoneCallOperator();
        phoneCallAdapter.initialize(this.myContext);

        // setup music invoker
        this.musicAdapter = new InvokeKuwo();
        //        this.musicAdapter = new InvokeDuoMi();

        // setup FM invoker
        this.fmAdapter = new InvokeQingting();

        // setup navigator invoker
        this.navigatorAdapter = new BaiduNaviAdapter();

        //        fokeyAdapter = new FokeyImpl();
        fokeyAdapter = new EcarAdapter();
        fokeyAdapter.initialize(this.myContext);
    }

    // reset
    private void reset()
    {
        if (null != musicAdapter)
        {
            musicAdapter.release(this.myContext);
        }
        musicAdapter = null;

        if (null != phoneCallAdapter)
        {
            phoneCallAdapter.release();
        }
        phoneCallAdapter = null;

        if (null != fmAdapter)
        {
            fmAdapter.release(this.myContext);
        }
        fmAdapter = null;

        if (null != navigatorAdapter)
        {
            navigatorAdapter.release();
        }
        navigatorAdapter = null;

        if (null != fokeyAdapter)
        {
            fokeyAdapter.release(this.myContext);
        }
        fokeyAdapter = null;
    }

    // broadcast receiver of hardware state,
    // such as: battery level, bluetooth connect state
    private class KeyActionReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //            FoUtil.printProcIDandName(myContext, "111111111111111");
            byte[] data = intent.getByteArrayExtra(BleConstants.KEY_ACTION_DATA);
            //            final int senderID = intent.getIntExtra(BleConstants.KEY_ACTION_DATA_MAGIC, -1);
            //            if (senderID == android.os.Process.myPid())
            //                return;
            onBleCharacteristicChanged(data);
            //            FoUtil.printProcIDandName(myContext, "33333333333333333333");
        }
    }

    // broadcast receiver of hardware state,
    // such as: battery level, bluetooth connect state
    private class HardwareStateReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // get the notification type
            final int notifyType = intent.getIntExtra(BleConstants.BLE_DEVICE_NOTIFICATION, -1);

            switch (notifyType)
            {
                case BleConstants.BLE_NOTIFY_TYPE_STATE_CHANGED:
                    boolean bleState = intent.getBooleanExtra(BleConstants.BLE_STATE_CHANGED, false);
                    //                    Log.i(TAG, "the new BLE state: " + bleState);
                    onBleConnectChanged(bleState);
                    break;
                default:
                    break;
            }

        }
    }

    // ble device connection state changed
    private void onBleConnectChanged(boolean bleStateNew)
    {
        if (null != phoneCallAdapter)
        {
            phoneCallAdapter.onBleStateChanged(bleStateNew);
        }
    }

    // ble Device Characteristic changed
    private void onBleCharacteristicChanged(final byte[] data)
    {
        // the data should contains 2 bytes at least
        if ((null == data) || (data.length < 2))
        {
            return;
        }

        if (FoLogger.isDebuggable)
        {
            String dat = FoUtil.byteArrayToHex(data);
            Log.d(TAG, "收到通知-onCharacteristicChanged: 0x" + dat);
        }

        // the first byte indicates which key was pressed
        final byte keyCode = data[0];
        // the second byte indicates what action it is,
        // they are: single click, double click or long pressed
        final byte actionCode = data[1];

        if ((BleConstants.KEYCODE_TELPHONE != keyCode) &&
                (null != this.phoneCallAdapter) && !this.phoneCallAdapter.isIdleState())
        {
            // 如果手机不是在空闲状态，则不能做其他事情，
            // 比如：听音乐，听电台，导航，等等
            return;
        }

        switch (keyCode)
        {
            case BleConstants.KEYCODE_TELPHONE:
                onPhoneKeyPressed(actionCode);
                break;
            case BleConstants.KEYCODE_MUSIC:
                onMusicKeyPressed(actionCode);
                break;
            case BleConstants.KEYCODE_OK:
                onFokeyPressed(actionCode);
                break;
            case BleConstants.KEYCODE_FM:
                onFMKeyPressed(actionCode);
                break;
            case BleConstants.KEYCODE_NAVIGATOR:
                onNavigationKeyPressed(actionCode);
                break;
            default:
                return;
        }

        //        new FoBell(myContext,1);

        conflictCheck(keyCode, actionCode);
    }

    // conflict check
    private void conflictCheck(byte keyCode, byte actionCode)
    {
        if ((BleConstants.ACTION_CLICK != actionCode) && (BleConstants.ACTION_DOUBLE_CLICK != actionCode))
        {
            // ONLY the single click, double click will check conflict
            // because it may initiate one another app
            // so we need pause/stop the ongoing app
            // NOTE: not accurate here
            //       due to we can NOT get the really state of 3rd app
            return;
        }

        switch (keyCode)
        {
            case BleConstants.KEYCODE_MUSIC:
                stopFM();
                break;
            case BleConstants.KEYCODE_FM:
                stopMusic();
                break;
            case BleConstants.KEYCODE_NAVIGATOR:
                stopFM();
                stopMusic();
                break;
            //            case BleConstants.KEYCODE_TELPHONE:
            //                break;
            //            case BleConstants.KEYCODE_OK:
            //                break;
            default:
                break;
        }
    }

    // stop or pause music
    private void stopMusic()
    {
        if (this.isMusicPlaying && (null != this.musicAdapter))
        {
            // pause the FM playing
            this.musicAdapter.longPressed(this.myContext);
            this.isMusicPlaying = false;
        }
    }

    // stop or pause FM
    private void stopFM()
    {
        if (this.isFMPlaying && (null != this.fmAdapter))
        {
            // pause the FM playing
            this.fmAdapter.longPressed(this.myContext);
            this.isFMPlaying = false;
        }
    }

    // music key pressed
    private void onMusicKeyPressed(final byte action)
    {
        if (null == this.musicAdapter)
        {
            return;
        }

        switch (action)
        {
            case BleConstants.ACTION_CLICK:
                // single click initiates play music
                this.musicAdapter.singleClick(this.myContext);
                this.isMusicPlaying = true;
                break;
            case BleConstants.ACTION_DOUBLE_CLICK:
                // double click initiates
                this.musicAdapter.doubleClick(this.myContext);
                this.isMusicPlaying = true;
                break;
            case BleConstants.ACTION_LONG_PRESS:
                this.musicAdapter.longPressed(this.myContext);
                this.isMusicPlaying = false;
                break;
            default:
                break;
        }
    }

    // fm key pressed
    private void onFMKeyPressed(final byte action)
    {
        if (null == this.fmAdapter)
        {
            return;
        }

        switch (action)
        {
            case BleConstants.ACTION_CLICK:
                // single click initiates play music
                this.fmAdapter.singleClick(this.myContext);
                this.isFMPlaying = true;
                break;
            case BleConstants.ACTION_DOUBLE_CLICK:
                // double click initiates
                this.fmAdapter.doubleClick(this.myContext);
                this.isFMPlaying = true;
                break;
            case BleConstants.ACTION_LONG_PRESS:
                this.fmAdapter.longPressed(this.myContext);
                this.isFMPlaying = false;
                break;
            default:
                break;
        }
    }

    // phone key pressed
    private void onPhoneKeyPressed(final byte action)
    {
        Log.e(TAG, "on phone key pressed:" + action);
        if (null == this.phoneCallAdapter)
        {
            return;
        }

        Log.e(TAG, "on phone key pressed 2:" + action);
        switch (action)
        {
            case BleConstants.ACTION_CLICK:
                // single click, answer phone call
                this.phoneCallAdapter.singleClicked(this.myContext);
                break;
            case BleConstants.ACTION_DOUBLE_CLICK:
                // double click, end call
                this.phoneCallAdapter.doubleClicked(this.myContext);
                break;
            case BleConstants.ACTION_LONG_PRESS:
                // long press
                this.phoneCallAdapter.longPressed(this.myContext);
                break;
            default:
                break;
        }
    }

    // navigation key pressed
    private void onNavigationKeyPressed(final byte action)
    {
        if (null == this.navigatorAdapter)
        {
            return;
        }

        switch (action)
        {
            case BleConstants.ACTION_CLICK:
                // single click initiates play music
                this.navigatorAdapter.singleClick(this.myContext);
                this.isNavigating = true;
                break;
            case BleConstants.ACTION_DOUBLE_CLICK:
                // double click initiates
                this.navigatorAdapter.doubleClick(this.myContext);
                this.isNavigating = true;
                break;
            case BleConstants.ACTION_LONG_PRESS:
                this.navigatorAdapter.longPressed(this.myContext);
                this.isNavigating = false;
                break;
            default:
                break;
        }
    }

    // ok key pressed
    private void onFokeyPressed(final byte action)
    {
        if (null == this.fokeyAdapter)
        {
            return;
        }

        switch (action)
        {
            case BleConstants.ACTION_CLICK:
                this.fokeyAdapter.singleClick(this.myContext);
                break;
            case BleConstants.ACTION_DOUBLE_CLICK:
                this.fokeyAdapter.doubleClick(this.myContext);
                break;
            case BleConstants.ACTION_LONG_PRESS:
                this.fokeyAdapter.longPressed(this.myContext);
                break;
            default:
                break;
        }
    }


    // ----------------------------------
    private static String TAG = "KeyActionCenter";
    private static KeyActionCenter instance = null;

    private Context myContext = null;
    private KeyActionReceiver keyActionReceiver = null;
    private HardwareStateReceiver bleDeviceStateReciver = null;

    // Fokey Adapter
    private FokeyAdapter fokeyAdapter = null;
    // FM adapter to invoke FM application
    private FMAdapter fmAdapter = null;
    // music adapter to invoke music application
    private MusicAdapter musicAdapter = null;
    // navigator adapter to launch music application
    private NavigatorAdapter navigatorAdapter = null;
    // phone call adapter
    private PhoneCall phoneCallAdapter = null;

    // playing state
    private boolean isMusicPlaying = false;
    private boolean isFMPlaying = false;
    private boolean isNavigating = false;

}
