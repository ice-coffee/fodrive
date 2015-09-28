package com.jinglingtec.ijiazu.bluetooth;

public interface BleCommands
{
    int SCAN_BLE_DEVICE = 0x201;

    // ------------------------------------------------------
    // commands send from BleThread --->  BleController
    // ------------------------------------------------------
    // ble thread has ready
    //    int BLE_THREAD_HAS_READY = 0x600;
    // read BLE device data is ready
    int BLE_READ_DATA_READY = 0x601;
    // BLE characteristic changed
    int BLE_CHARACTERISTIC_CHANGED = 0x602;
    // BLE connected
    int BLE_CONNECTED = 0x604;
    // BLE dis connected
    int BLE_DIS_CONNECTED = 0x605;

    //    int

    // ------------------------------------------------------
    // commands send from BleController ---> BleThread
    // ------------------------------------------------------
    // ble thread has ready
    int KILL_BLE_THREAD = 0x700;
    //    int SCAN_BLE_DEVICE = 0x701;
    //    int STOP_BLE_DEVICE = 0x702;

    // read specific BLE device property
    int READ_SPECIFIC_PROPERTY = 0x801;
}

