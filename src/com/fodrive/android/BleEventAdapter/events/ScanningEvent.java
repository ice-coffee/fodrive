package com.fodrive.android.BleEventAdapter.events;

import java.util.Set;

import android.bluetooth.BluetoothDevice;


public class ScanningEvent
{

    public static final int scanning = 1;
    public static final int scan_suc = 2;
    public static final int scan_stop = 3;

    private int mScanning;

    private final BluetoothDevice[] mBluetoothDevices;

    public ScanningEvent(int scanning, Set<BluetoothDevice> pBluetoothDevices)
    {
        this.mScanning = scanning;
        mBluetoothDevices = pBluetoothDevices.toArray(new BluetoothDevice[pBluetoothDevices.size()]);
    }

    public int getMScanning()
    {
        return mScanning;
    }

    public BluetoothDevice[] getmBluetoothDevices()
    {
        return mBluetoothDevices;
    }

    @Override
    public String toString()
    {
        return "Scanning Event : " + mScanning;
    }
}
