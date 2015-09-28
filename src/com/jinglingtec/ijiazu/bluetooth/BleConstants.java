package com.jinglingtec.ijiazu.bluetooth;

public interface BleConstants
{
    // Fodrive ble device name
    //    String IJIAZU_DEVICE_NAME = "fo link";
    String IJIAZU_SECRET_KEY = "20140722";


    //    // hardware code definition
    //    // key code: telphone
    //    byte KEYCODE_TELPHONE = 1;
    //    // key code: music
    //    byte KEYCODE_MUSIC = 2;
    //    // key code: ok
    //    byte KEYCODE_OK = 3;
    //    // key code: FM
    //    byte KEYCODE_FM = 4;
    //    // key code: navigator
    //    byte KEYCODE_NAVIGATOR = 5;

    // hardware code definition
    // key code: telphone
    byte KEYCODE_TELPHONE = 0x1;
    // key code: ok
    byte KEYCODE_OK = 0x2;
    // key code: music
    byte KEYCODE_MUSIC = 0x4;
    // key code: navigator
    byte KEYCODE_NAVIGATOR = 0x8;
    // key code: FM
    byte KEYCODE_FM = 0x10;

    // action on the hardware 
    // single click
    byte ACTION_CLICK = 1;
    // double click
    byte ACTION_DOUBLE_CLICK = 2;
    // long press 
    byte ACTION_LONG_PRESS = 3;


    // used as broadcast intent filter sign
    public static final String HARDWARE_MONITOR_FILTER = "com.jinglingtec.fodrive.hardware.monitor";
    // used as broadcast sign when BLE state changed, connected, disconnected
    public static final String BLE_DEVICE_NOTIFICATION = "com.jinglingtec.fodrive.ble.notification";
    public static final int BLE_NOTIFY_TYPE_STATE_CHANGED = 17;
    public static final int BLE_NOTIFY_TYPE_BATTERY_LEVEL = 18;
    //    public static final int BLE_NOTIFY_TYPE_NAVI_KEY_PRESSED = 19;
    //    public static final int BLE_NOTIFY_TYPE_PHONE_KEY_PRESSED = 20;

    public static final String BLE_STATE_CHANGED = "com.jinglingtec.fodrive.ble.state.changed";
    //    public static final int BLE_STATE_CONNECTED = 21;
    //    public static final int BLE_STATE_DISCONNECTED = 22;

    // used as broadcast sign when read battery level
    public static final String READ_BATTERY_LEVEL = "com.jinglingtec.fodrive.battery.levle";

    //    public static final String NAVI_KEY_ACTION_CODE = "ijiazu.navi.key.action.code";
    //    public static final String PHONE_KEY_ACTION_CODE = "ijiazu.phone.key.action.code";

    // key action broadcast intent filter
    public static final String KEY_ACTION_MONITOR_FILTER = "com.jinglingtec.ijiazu.key.action.filter";
    public static final String KEY_ACTION_DATA = "com.jinglingtec.ijiazu.key.action.data";
    //    public static final String KEY_ACTION_DATA_MAGIC = "com.jinglingtec.ijiazu.key.action.data.magic";

    // key for BLE device state
    public static final String BLE_DEVICE_STATE = "com.jinglingtec.ijiazu.ble.device.state";
}
