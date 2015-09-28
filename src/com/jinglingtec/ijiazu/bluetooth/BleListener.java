package com.jinglingtec.ijiazu.bluetooth;

public interface BleListener
{
    //    // ble connected, phase 1
    //    void conntected_one();
    //
    //    // ble connected, phase 2
    //    void conntected_two();

    // ble connected
    void conntected();

    // ble dis connected
    void disconnected();

    // read specific data ready,
    // mobile phone initiate this action
    //    void readReady(byte[] data);

    // ble characteristic changed
    // paired BLE device initiate this action
    void characteristicChanged(byte[] data);
}
