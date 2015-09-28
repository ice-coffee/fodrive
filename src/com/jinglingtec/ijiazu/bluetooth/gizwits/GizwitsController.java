package com.jinglingtec.ijiazu.bluetooth.gizwits;//package com.jinglingtec.ijiazu.bluetooth.gizwits;
//
//import android.app.Service;
//import android.bluetooth.BluetoothDevice;
//import android.content.*;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.util.Log;
//import com.jinglingtec.ijiazu.bluetooth.BleListener;
//import com.xtremeprog.sdk.ble.BleGattCharacteristic;
//import com.xtremeprog.sdk.ble.BleGattService;
//import com.xtremeprog.sdk.ble.BleService;
//import com.xtremeprog.sdk.ble.IBle;
//
//import java.util.ArrayList;
//import java.util.UUID;
//
//public class GizwitsController
//{
//    private final String TAG = "GizwitsController";
//
//    private final String szBindMac = "ff015b000200";
//    private final String FoLinkKey = "20140722";
//    private final String FoLinkUUID_Service = "0000ffff-0000-1000-8000-00805f9b34fb";
//    private final String FoLinkUUID_Charas = "0000ff01-0000-1000-8000-00805f9b34fb";
//    private final String BatteryUUID_Service = "0000180f-0000-1000-8000-00805f9b34fb";
//    private final String BatteryUUID_Charas = "00002a19-0000-1000-8000-00805f9b34fb";
//
//    private BleGattService gattBatteryService = null;
//
//    // singleton instance
//    public static GizwitsController instance = null;
//
//
//    // context of fodrive service
//    private Context myContext = null;
//
//    private BleService gizService = null;
//    private IBle bleInterface = null;
//    // gizwits service connectionmyContext
//    private ServiceConnection gizServiceConnection = null;
//
//    // is it scanning the ble device?
//    private boolean isScanningBle = false;
//    // Fodrive ble device address
//    private String bleDeviceAddress = null;
//
//    // broadcast receiver of scan bluetooth device
//    private BroadcastReceiver bleDeviceReceiver = null;
//    // broadcast receiver of connect ble device
////    private BroadcastReceiver connectBleDeviceReceiver = null;
//
//    // ble device listener
//    private BleListener bleDeviceListener = null;
//
//    // get gizwits controller instance
//    public static GizwitsController getInstance()
//    {
//        if (null == instance)
//        {
//            instance = new GizwitsController();
//        }
//        return instance;
//    }
//
//    // destroy
//    public static void destroy()
//    {
//        if (null != instance)
//        {
//            instance.unbindGizwitService();
//            instance = null;
//        }
//    }
//
//    // private constructor, avoid create instance outside
//    private GizwitsController()
//    {
//    }
//
//    // set Ble Device event listener
//    public void setBleListener(BleListener listener)
//    {
//        bleDeviceListener = listener;
//    }
//
//    // gizwits service connection class
//    private class GizwitsServiceConnection implements ServiceConnection
//    {
//        @Override
//        public void onServiceConnected(ComponentName className, IBinder rawBinder)
//        {
//            gizService = ((BleService.LocalBinder) rawBinder).getService();
//            if (null == gizService)
//            {
//                return;
//            }
//
//            bleInterface = gizService.getBle();
//            if ((bleInterface != null))
//            {
//                if (bleInterface.adapterEnabled())
//                {
//                    startScanBleDevice();
//                }
//                else
//                {
//                    // TODO: enalbe adapter
//                }
//            }
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName classname)
//        {
//            gizService = null;
//            gizServiceConnection = null;
//            bleInterface = null;
//        }
//    }
//
//    // bind to gizwits service
//    // start scan the ble device immediately after bind it successful
//    public void bindGizwitService(Context ctx)
//    {
//        if (null != gizServiceConnection)
//        {
//            return;
//        }
//
//        myContext = ctx;
//        gizServiceConnection = new GizwitsServiceConnection();
//
//        final Intent bindIntent = new Intent(myContext, BleService.class);
//        boolean ret = myContext.bindService(bindIntent, gizServiceConnection, Service.BIND_AUTO_CREATE);
//        if (false == ret)
//        {
//            gizServiceConnection = null;
//        }
//    }
//
//    // unbind gizwits service
//    public void unbindGizwitService()
//    {
//        if ((null != gizService) && (null != bleDeviceReceiver))
//        {
//            gizService.unregisterReceiver(bleDeviceReceiver);
//        }
//
//        if ((null != bleInterface) && (bleDeviceAddress != null))
//        {
//            bleInterface.disconnect(bleDeviceAddress);
//        }
//        if ((null != gizServiceConnection) && (null != myContext))
//        {
//            myContext.unbindService(gizServiceConnection);
//        }
//
//        reset();
//    }
//
//    // read battery level
//    public void readBatteryLevel()
//    {
//        // testing, begin
//        if (null == bleDeviceListener)
//        {
//            Log.d(TAG, "bleDeviceListener is null !");
//            return;
//        }
//
//        if (null == gattBatteryService)
//        {
//            Log.d(TAG, "gattBatteryService is null !");
//            return;
//        }
//
//        BleGattCharacteristic batteryCharas = gattBatteryService.getCharacteristic(UUID.fromString(BatteryUUID_Charas));
//        bleInterface.requestReadCharacteristic(bleDeviceAddress, batteryCharas);
//        // testing, end
//    }
//
//    // reset this object
//    private void reset()
//    {
//        gizService = null;
//        bleInterface = null;
//        gizServiceConnection = null;
//        isScanningBle = false;
//        bleDeviceAddress = null;
//        bleDeviceReceiver = null;
////        connectBleDeviceReceiver = null;
//        bleDeviceListener = null;
//        myContext = null;
//    }
//
//    // scan ble device
//    public void startScanBleDevice()
//    {
//        if ((null == gizService) || (null == bleInterface) || isScanningBle)
//        {
//            return;
//        }
//
//        Log.d(TAG, "GizwitsController: startScanBleDevice");
//
//        // define the default time of scan the ble device
//        final long SCAN_PERIOD = 10000;
//        final Handler handlerStopScan = new Handler();
//
//        // Stops scanning after a pre-defined scan period.
//        handlerStopScan.postDelayed(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                stopScanBleDevice();
//
//            }
//        }, SCAN_PERIOD);
//
//        isScanningBle = true;
//        if (null == bleDeviceReceiver)
//        {
//            bleDeviceReceiver = new GizwitsBleDeviceReceiver();
//            gizService.registerReceiver(bleDeviceReceiver, BleService.getIntentFilter());
//        }
//        if (bleInterface != null)
//        {
//            bleInterface.startScan();
//        }
//    }
//
//    // stop scan ble device
//    private void stopScanBleDevice()
//    {
//        if (bleInterface != null)
//        {
//            bleInterface.stopScan();
//        }
//
//        isScanningBle = false;
//    }
//
//    // the scan ble device process found a bluetooth device
//    private void onBluetoothDeviceFound(BluetoothDevice device, int rssi,
//                                        byte[] scanRecord, int source)
//    {
//        //scanRecord���汣�����ַ�20140722������App����ʶ��Ӳ��
//        String szScanRecord = "";
//        if (null == device)
//        {
//            return;
//        }
//
//        for(int i=0; i<scanRecord.length; i++)
//        {
//            szScanRecord += String.format("%02x", scanRecord[i]);
//        }
//
//        if(!szScanRecord.contains(FoLinkKey))
//        {
//            Log.d(TAG, "szScanRecord="+szScanRecord);
//            return;
//        }
//
////        if(!szScanRecord.contains(szBindMac))
////        {
////            Log.d(TAG, "MAC is not bound!");
////            return;
////        }
//
//        stopScanBleDevice();
//
//        Log.d(TAG, "name="+device.getName()+", rssi="+rssi+", scanRecord="+szScanRecord+", source="+source);
//
//        bleDeviceAddress = device.getAddress();
//        if ((null == bleDeviceAddress) || (bleDeviceAddress.length() < 1))
//        {
//            // oh, something wrong
//            return;
//        }
//        Log.d(TAG, "mac="+bleDeviceAddress);
//
////            connectBleDeviceReceiver = new ConnectBleDeviceReceiver();
////            gizService.registerReceiver(connectBleDeviceReceiver, BleService.getIntentFilter());
//        bleInterface.requestConnect(bleDeviceAddress);
//
//    }
//
//    // ble device discovered
//    private void onBleDeviceDiscovered(ArrayList<BleGattService> bleGattService)
//    {
//        if (null == bleGattService)
//        {
//            // sometimes, bleGattService equals to NULL,
//            // we got the log from umeng statistics
//            return;
//        }
//
//        BleGattCharacteristic gattCharacteristic = null;
//        String szUUID;
//        for (BleGattService gattService : bleGattService)
//        {
//            final java.util.UUID devUUID = gattService.getUuid();
//            if (null == devUUID)
//            {
//                continue;
//            }
//
//            szUUID = devUUID.toString().toLowerCase();
//            Log.d(TAG, "onBleDeviceDiscovered, uuid= " + szUUID + " service name:" + gattService.getName());
//
//            if (FoLinkUUID_Service.equals(szUUID))
//            {
//                gattCharacteristic = gattService.getCharacteristic(UUID.fromString(FoLinkUUID_Charas));
//                bleInterface.requestCharacteristicNotification(bleDeviceAddress, gattCharacteristic);
//                Log.d(TAG, "onBleDeviceDiscovered, requestCharacteristicNotification uuid= " + szUUID);
//            }
//
//            if (BatteryUUID_Service.equals(szUUID))
//            {
//                //gattBatteryService = gattService;
//                gattCharacteristic = gattService.getCharacteristic(UUID.fromString(BatteryUUID_Charas));
//                bleInterface.requestReadCharacteristic(bleDeviceAddress, gattCharacteristic);
//                Log.d(TAG, "onBleDeviceDiscovered, requestReadCharacteristic uuid= " + szUUID);
//            }
//        }
//    }
//
//    // broadcast receiver of scan bluetooth device
//    private class GizwitsBleDeviceReceiver extends BroadcastReceiver
//    {
//        @Override
//        public void onReceive(Context context, Intent intent)
//        {
//            final String action = intent.getAction();
//
//            if (BleService.BLE_NO_BT_ADAPTER.equals(action))
//            {
//                return;
//            }
//            else if (BleService.BLE_NOT_SUPPORTED.equals(action))
//            {
//                return;
//            }
//            else if (BleService.BLE_DEVICE_FOUND.equals(action))
//            {
//                // device found
//                Bundle extras = intent.getExtras();
//                if (null != extras)
//                {
//                    BluetoothDevice device = extras.getParcelable(BleService.EXTRA_DEVICE);
//                    int rssi = intent.getIntExtra(BleService.EXTRA_RSSI, 0);
//                    byte[] scanRecord = intent.getByteArrayExtra(BleService.EXTRA_SCAN_RECORD);
//                    int source = intent.getIntExtra(BleService.EXTRA_SOURCE, 0);
//                    onBluetoothDeviceFound(device, rssi, scanRecord, source);
//                }
//
//                return;
//            }
//            else if (BleService.BLE_SERVICE_DISCOVERED.equals(action))
//            {
//                if(null != bleDeviceAddress)
//                {
//                    onBleDeviceDiscovered(bleInterface.getServices(bleDeviceAddress));
//                }
//                return;
//            }
//            else if (BleService.BLE_GATT_CONNECTED.equals(action))
//            {
//                Log.d(TAG, "ble gatt Connected...");
//                if (null != bleDeviceListener)
//                {
//                    bleDeviceListener.conntected();
//                }
//                return;
//            }
//            else if (BleService.BLE_GATT_DISCONNECTED.equals(action))
//            {
//                Log.d(TAG, "ble gatt dis Connected...");
//                if (null != bleDeviceListener)
//                {
//                    bleDeviceListener.disconnected();
//                }
//                return;
//            }
//            else if (BleService.BLE_CHARACTERISTIC_READ.equals(action))
//            {
//                Bundle extras = intent.getExtras();
//                if((null != extras) && (null != bleDeviceListener))
//                {
//                    byte[] data = extras.getByteArray(BleService.EXTRA_VALUE);
//                    bleDeviceListener.readReady(data);
//                }
//                return;
//            }
//            else if (BleService.BLE_CHARACTERISTIC_CHANGED.equals(action))
//            {
//                Bundle extras = intent.getExtras();
//                if((null != extras) && (null != bleDeviceListener))
//                {
//                    final byte[] data = extras.getByteArray(BleService.EXTRA_VALUE);
//                    bleDeviceListener.characteristicChanged(data);
//                }
//                return;
//            }
////            else if (BleService.BLE_GATT_DISCONNECTED.equals(action))
////            {
////            }
////            else if (BleService.BLE_CHARACTERISTIC_NOTIFICATION.equals(action))
////            {
////            }
////            else if (BleService.BLE_CHARACTERISTIC_INDICATION.equals(action))
////            {
////            }
////            else if (BleService.BLE_CHARACTERISTIC_WRITE.equals(action))
////            {
////            }
//        }
//    }
//
//    ;
//
//}
///*
//    // broadcast receiver of scan bluetooth device
//    private class GizwitsBleDeviceReceiver_bak extends BroadcastReceiver
//    {
//        @Override
//        public void onReceive(Context context, Intent intent)
//        {
//            final String action = intent.getAction();
//
//            // phase 1:
//            // this is get notification of scan bluetooth device
//            // will get all notifications for all bluetooth device
//            // we need select our specific BLE device to take further action
//            if (BleService.BLE_NOT_SUPPORTED.equals(action))
//            {
//                // "Ble not support", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            else if (BleService.BLE_DEVICE_FOUND.equals(action))
//            {
//                // device found
//                final Bundle extras = intent.getExtras();
//                if (null != extras)
//                {
//                    BluetoothDevice device = extras.getParcelable(BleService.EXTRA_DEVICE);
//                    int rssi = intent.getIntExtra(BleService.EXTRA_RSSI, 0);
//                    byte[] scanRecord = intent.getByteArrayExtra(BleService.EXTRA_SCAN_RECORD);
//                    int source = intent.getIntExtra(BleService.EXTRA_SOURCE, 0);
//                    onBluetoothDeviceFound(device, rssi, scanRecord, source);
//                }
//                return;
//            }
//            else if (BleService.BLE_NO_BT_ADAPTER.equals(action))
//            {
//                // "No bluetooth adapter", Toast.LENGTH_SHORT)
//                return;
//            }
//
//            // phase 2:
//            // connect our specific BLE device,
//            // states:  connected, dis-connected, discovere,
//            final Bundle extras = intent.getExtras();
//            if (extras == null)
//            {
//                return;
//            }
//
//            if (null == bleDeviceAddress)
//            {
//                Log.d(TAG, "ScanBleDeviceReceiver, bleDeviceAddress is null!");
//                return;
//            }
//
//            if (null == extras.getString(BleService.EXTRA_ADDR))
//            {
//                Log.d(TAG, "ScanBleDeviceReceiver, extras.getString(BleService.EXTRA_ADDR) is null!");
//                return;
//            }
//
//            if (!bleDeviceAddress.equals(extras.getString(BleService.EXTRA_ADDR)))
//            {
//                Log.d(TAG, "ScanBleDeviceReceiver, BleService.EXTRA_ADDR is not my ble device address, do nothing");
//                return;
//            }
//
//            if (BleService.BLE_GATT_CONNECTED.equals(action))
//            {
//                Log.d(TAG, "ScanBleDeviceReceiver, ble gatt Connected");
//                if (null != bleDeviceListener)
//                {
//                    bleDeviceListener.conntected();
//                }
//                return;
//            }
//            else if (BleService.BLE_GATT_DISCONNECTED.equals(action))
//            {
//                Log.d(TAG, "ScanBleDeviceReceiver, ble gatt dis Connected");
//                if (null != bleDeviceListener)
//                {
//                    bleDeviceListener.disconnected();
//                }
//                return;
//            }
//            else if (BleService.BLE_SERVICE_DISCOVERED.equals(action))
//            {
//                onBleDeviceDiscovered(bleInterface.getServices(bleDeviceAddress));
////                displayGattServices(bleInterface.getServices(mDeviceAddress));
//                return;
//            }
//
//            // phase 3:
//            // interaction with our specific BLE device, Fo-Link
//            final String devUUID = extras.getString(BleService.EXTRA_UUID);
//            if (null == devUUID)
//            {
//                return;
//            }
//
//            final String uuid = devUUID.toLowerCase();
//            if ((null == uuid) || (!FoLinkUUID_Charas.equals(uuid) && !BatteryUUID_Charas.equals(uuid)))
//            {
//                Log.d(TAG, "ScanBleDeviceReceiver, uuid is NULL or not the folink uuid");
//                Log.d(TAG, "UUID=" + uuid);
//                return;
//            }
//
//            if (BleService.BLE_CHARACTERISTIC_READ.equals(action))
//            {
//                // read ble device, some data comes
//                if (null != bleDeviceListener)
//                {
//                    final byte[] data = extras.getByteArray(BleService.EXTRA_VALUE);
//                    bleDeviceListener.readReady(data);
//                }
//                return;
//            }
//            else if (BleService.BLE_CHARACTERISTIC_CHANGED.equals(action))
//            {
//                // ble device characteristic changed
//                if (null != bleDeviceListener)
//                {
//                    final byte[] data = extras.getByteArray(BleService.EXTRA_VALUE);
//                    bleDeviceListener.characteristicChanged(data);
//                }
//                return;
//            }
////            else if (BleService.BLE_GATT_DISCONNECTED.equals(action))
////            {
////            }
////            else if (BleService.BLE_CHARACTERISTIC_NOTIFICATION.equals(action))
////            {
////            }
////            else if (BleService.BLE_CHARACTERISTIC_INDICATION.equals(action))
////            {
////            }
////            else if (BleService.BLE_CHARACTERISTIC_WRITE.equals(action))
////            {
////            }
//        }
//    }
//
//    ;
//
//}
//*/
