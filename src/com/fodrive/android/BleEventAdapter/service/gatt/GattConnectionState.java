package com.fodrive.android.BleEventAdapter.service.gatt;


import android.bluetooth.BluetoothProfile;

public abstract class GattConnectionState
{
    //    public static final int STATE_DISCONNECTED = 0;
    //    public static final int STATE_CONNECTING = 1;
    //    public static final int STATE_CONNECTED = 2;

    public static final int STATE_DISCONNECTED = BluetoothProfile.STATE_DISCONNECTED;
    //    public static final int STATE_CONNECTING = BluetoothProfile.STATE_CONNECTING;
    public static final int STATE_CONNECTED = BluetoothProfile.STATE_CONNECTED;
}
