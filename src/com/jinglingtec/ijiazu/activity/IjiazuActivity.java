package com.jinglingtec.ijiazu.activity;

import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.accountmgr.LoginActivity;
import com.jinglingtec.ijiazu.bluetooth.BleConstants;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.BaiduNavimainActivity;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils.FoLocation;
import com.jinglingtec.ijiazu.invokeApps.ecar.EcarAdapter;
import com.jinglingtec.ijiazu.invokeApps.fm.FMActivity;
import com.jinglingtec.ijiazu.invokeApps.music.MusicActivity;
import com.jinglingtec.ijiazu.invokeApps.telephone.TelephoneActivity;
import com.jinglingtec.ijiazu.services.TelephoneService;
import com.jinglingtec.ijiazu.util.*;

public class IjiazuActivity extends BaseActivity
{
    public static final String TAG = "IjiazuActivity";

    // fodrive service
    //    private IjiazuServiceController serviceController = null;
    // fodrive service connection
    //    private IjiazuServiceConnection ijiazuServiceConn = null;

    // broadcast receiver to monitor the hardware states
    private HardwareStateReceiver hardwareStateReceiver = null;

    //ecar service
    //    private EcarAdapter ecarAdapter = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ijiazu_activity);

        showGuideSplash();

        //        bindIjiazuService();
        FoUtil.enableBluetooth(this, true);

        // create & register the hardware monitor receiver
        hardwareStateReceiver = new HardwareStateReceiver();
        this.registerReceiver(hardwareStateReceiver, new IntentFilter(BleConstants.HARDWARE_MONITOR_FILTER));

        CheckLatestInfo.checkLatest(this);

        //startTelephoneService
        Intent intent = new Intent(IjiazuActivity.this, TelephoneService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);

        //开启定位当前位置
        new FoLocation(getApplicationContext(), this);

        //注册shareSDK
        //        ShareSDK.initSDK(this);

        //        FoUtil.printProcIDandName(this, TAG);
        //        KeyActionCenter.register(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        setTitleText(R.string.app_name);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //        if (null != serviceController)
        //        {
        //            onBleConnectChanged(this.serviceController.isBleConnected());
        //        }

        // send broadcast to get ble connection state

        sendBroadcast(new Intent(BleConstants.BLE_DEVICE_STATE));
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        //        serviceController = null;

        // unregister the hardware monitor receiver
        if (null != hardwareStateReceiver)
        {
            this.unregisterReceiver(this.hardwareStateReceiver);
            this.hardwareStateReceiver = null;
        }

        // unbind the fodrive service
        //        if (null != ijiazuServiceConn)
        //        {
        //            unbindService(ijiazuServiceConn);
        //        }
        //        ijiazuServiceConn = null;

        //stopTelephoneService
        Intent intent = new Intent(IjiazuActivity.this, TelephoneService.class);
        stopService(intent);

        //stop ecar service
        //        if (null != ecarAdapter)
        //        {
        //            ecarAdapter.destoryEcar(getApplicationContext());
        //        }

        //解shareSDK
        //        ShareSDK.stopSDK(this);

        //        //铃声解绑
        //        FoPreference.putBoolean(FoConstants.KEY_PRESS_BAR,false);

        super.onDestroy();
    }

    // bind fodrive service
    //    private void bindIjiazuService()
    //    {
    //        ijiazuServiceConn = new IjiazuServiceConnection();
    //        Intent psIntent = new Intent(this, IjiazuService.class);
    //        bindService(psIntent, ijiazuServiceConn, Service.BIND_AUTO_CREATE);
    //    }


    // create a service connection
    //    private class IjiazuServiceConnection implements ServiceConnection
    //    {
    //        @Override
    //        public void onServiceDisconnected(ComponentName name)
    //        {
    //            Log.d(TAG, " ijiazu service dis-connected");
    //        }
    //
    //        @Override
    //        public void onServiceConnected(ComponentName name, IBinder service)
    //        {
    //            IjiazuService ijiazuService = ((IjiazuService.IjiazuServiceBinder) service).getService();
    //            serviceController = ijiazuService.getServiceController();
    //            Log.d(TAG, " ijiazu service connected");
    //        }
    //    }

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
                case BleConstants.BLE_NOTIFY_TYPE_BATTERY_LEVEL:
                    //                    int batteryLevel = intent.getIntExtra(BleConstants.READ_BATTERY_LEVEL, -1);
                    byte[] data = intent.getByteArrayExtra(BleConstants.READ_BATTERY_LEVEL);
                    //                    Log.i(TAG, "battery level: " + batteryLevel);
                    onHardwareBatteryChanged(data);
                    break;
                default:
                    break;
            }

        }
    }


    // the ble device connection state changed
    private void onBleConnectChanged(boolean newState)
    {
        //        String info = newState ? "Ble Connected !" : "Ble Dis Connected !";

        ImageView tv = (ImageView) this.findViewById(R.id.bleConnectionState);
        if (newState)
        {
            tv.setBackground(getResources().getDrawable(R.drawable.bluetooth));
        }
        else
        {
            tv.setBackground(getResources().getDrawable(R.drawable.bluetooth_error));
        }
        //        tv.setText(info);
        //        Log.d(TAG, info);
    }

    // hardware battery level changed
    private void onHardwareBatteryChanged(byte[] data)
    {
        if ((null == data) || (data.length < 1))
        {
            return;
        }

        if (FoLogger.isDebuggable)
        {
            String battery = FoUtil.byteArrayToString(data);
            Log.d(TAG, "characteristic Changed: " + battery);
        }

        //        ImageView tv = (ImageView) this.findViewById(R.id.bleBatteryLevel);
        //        tv.setText("the battery level is: " + battery);
    }

    // navigation button clicked
    public void onNavigationClicked(View v)
    {
        //        Toast.makeText(this, "not cimplement yet", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, BaiduNavimainActivity.class);
        startActivity(intent);
    }

    //  music button clicked
    public void onMusicClicked(View v)
    {
        //        Toast.makeText(this, "not implement yet", Toast.LENGTH_SHORT).show();
        //        boolean bool = FoPreference.getBoolean(FoConstants.KEY_PRESS_BAR,false);
        //        if (!bool)
        //        {
        //            new FoBell(getApplicationContext());
        //        }
        //        else
        //        {
        //            FoBell.playSound();
        //            FoBell.setStreamVolume();
        //        }
        //        FoBell foBell = new FoBell(getApplicationContext());
        //        foBell.

        Intent intent = new Intent(this, MusicActivity.class);
        startActivity(intent);
    }

    public void onFoKeyClicked(View view)
    {
        boolean reEcar = FoPreference.getBoolean(FoConstants.ECAR, false);

        Log.e(TAG, "ecaradapter");

        if (reEcar)
        {
            //register ecar
            EcarAdapter ecarAdapter = new EcarAdapter();
            ecarAdapter.dialEcar(this);
        }
        else
        {
            //提示用户注册翼卡服务
            FoSettingOrDown.remindUsers(getApplicationContext(), FoConstants.ECAR);
        }
    }

    // Telephone button clicked
    public void onTelphoneClicked(View v)
    {

        Intent intent = new Intent(this, TelephoneActivity.class);
        startActivity(intent);
    }

    // FM button clicked
    public void onFMClicked(View v)
    {
        //        Toast.makeText(this, "not implement yet", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, FMActivity.class);
        startActivity(intent);
    }

    // Fo button clicked
    public void onFOClicked(View v)
    {
        Intent intent = new Intent(IjiazuActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    // Setting button clicked
    public void onSettingClicked(View v)
    {
        Intent intent = new Intent(IjiazuActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    // Setting button clicked
    public void onShareCLicked(View v)
    {
        //        showOnekeyshare();
    }

    // show the splash screen or guideline
    private void showGuideSplash()
    {
        final Intent intentParam = this.getIntent();
        if (null != intentParam)
        {
            boolean disableSplash = intentParam.getBooleanExtra(FoConstants.IS_DISABLE_SPLASHSCREEN, false);
            if (disableSplash)
            {
                return;
            }
        }

        //        if (FoPreference.getBoolean(FoConstants.IS_FIRST_RUN, true))
        //        {
        //            // this application is first run, show guideline activity
        //            Intent intent = new Intent(IjiazuActivity.this, GuidelineActivity.class);
        //            startActivity(intent);
        //        }
        //        else
        {
            // show splash screen
            Intent intent = new Intent(IjiazuActivity.this, SplashscreenActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void onBackPressed()
    {
        confirmCloseApp();
    }

    // confirm close app
    protected void confirmCloseApp()
    {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        builder.setIconAttribute(android.R.attr.dialogIcon);

        final String from = getResources().getString(R.string.app_name);
        final String message = getResources().getString(R.string.confirm_quit_ijiaqu);
        builder.setMessage(message);
        builder.setTitle(from);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                ThreadUtil.destroyPool();
                IjiazuActivity.this.finish();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}
