package com.fodrive.android.BleEventAdapter.events;

import android.bluetooth.BluetoothDevice;

public class DiscoveredDevicesEvent
{

    private final BluetoothDevice device;
    private final byte[] mScanData;

    public DiscoveredDevicesEvent(BluetoothDevice device, byte[] pScanData)
    {
        this.device = device;
        mScanData = pScanData;
    }


    public BluetoothDevice getDevice()
    {
        return device;
    }

    @Override
    public String toString()
    {
        return "DiscoveredDevicesEvent{" + '}';
    }

    public byte[] getScanData()
    {
        return mScanData;
    }
}
