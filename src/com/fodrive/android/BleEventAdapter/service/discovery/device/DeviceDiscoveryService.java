package com.fodrive.android.BleEventAdapter.service.discovery.device;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.fodrive.android.BleEventAdapter.BleEventBus;
import com.fodrive.android.BleEventAdapter.events.DiscoveredDevicesEvent;
import com.fodrive.squareup.otto.Produce;
import com.jinglingtec.ijiazu.util.FoUtil;

@SuppressLint("NewApi")
public class DeviceDiscoveryService extends Service
{

    private static final long SCAN_PERIOD = 1000 * 7; // 7 seconds
    private BleEventBus mBleEventBus;
    //private Set<BluetoothDevice> mBluetoothDevices = new HashSet<BluetoothDevice>();
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private boolean mScanning = false;
    private String TAG = "DeviceDiscoveryService";

    @Override
    public void onCreate()
    {
        FoUtil.printProcIDandName(this, TAG);
        Log.i(TAG, TAG + "-->onCreate()");
        mBleEventBus = BleEventBus.getInstance();
        mBleEventBus.register(this);
        @SuppressWarnings("static-access")
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(this.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(TAG, TAG + "-->onStartCommand()");
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
        {
            stopSelf();
        }
        else
        {
            scanLeDevice(true);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void scanLeDevice(final boolean enable)
    {
        if (enable)
        {
            startScanning();
        }

        // else {
        // stopScanning();
        // }
    }

    private void startScanning()
    {
        Log.i(TAG, TAG + "-->startScanning()");
        mHandler = new Handler();
        // Stops scanning after a
        // ... pre-defined scan period.
        mHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (mScanning)
                {
                    stopScanning();
                }

            }
        }, SCAN_PERIOD);

        mScanning = true;
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        // mBleEventBus.post(new
        // ScanningEvent(ScanningEvent.scanning,mBluetoothDevices));
    }

    private void stopScanning()
    {
        Log.i(TAG, TAG + "-->stopScanning()");
        mScanning = false;
        if (null != mBluetoothAdapter)
        {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        //		mBleEventBus.post(new ScanningEvent(ScanningEvent.scan_stop,
        //				mBluetoothDevices));
        DeviceDiscoveryService.this.stopSelf();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
    {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
        {
            Log.i(TAG, TAG + "-->mLeScanCallback()");
            //mBluetoothDevices.add(device);
            mBleEventBus.post(new DiscoveredDevicesEvent(device, scanRecord));
        }
    };

    @Produce
    public DiscoveredDevicesEvent produceAnswer()
    {
        return new DiscoveredDevicesEvent(null, null);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.i(TAG, TAG + "-->onDestroy()");
        if (mScanning)
        {
            stopScanning();
        }

        mBleEventBus.unregister(this);
    }

    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
