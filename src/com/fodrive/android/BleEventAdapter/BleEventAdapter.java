package com.fodrive.android.BleEventAdapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import com.fodrive.android.BleEventAdapter.service.discovery.device.DeviceDiscoveryService;
import com.fodrive.android.BleEventAdapter.service.gatt.GattService;

/**
 * @author leequer
 */
public class BleEventAdapter
{

    private static BleEventAdapter ourInstance = new BleEventAdapter();

    private BluetoothDevice bluetoothDevice;

    public static BleEventAdapter getInstance()
    {
        return ourInstance;
    }

    public BluetoothDevice getBluetoothDevice()
    {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice)
    {
        this.bluetoothDevice = bluetoothDevice;
    }

    public void connectDevice(Context context, BluetoothDevice device)
    {

        context.startService(new Intent(context, GattService.class));

    }

    public void closeConnection(Context context)
    {
        context.stopService(new Intent(context, GattService.class));
    }

    public void startScanning(Context context)
    {
        context.startService(new Intent(context, DeviceDiscoveryService.class));
    }

    public void stopScanning(Context context)
    {
        context.stopService(new Intent(context, DeviceDiscoveryService.class));
    }
}
