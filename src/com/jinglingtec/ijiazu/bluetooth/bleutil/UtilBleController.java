package com.jinglingtec.ijiazu.bluetooth.bleutil;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.fodrive.android.BleEventAdapter.BleEventAdapter;
import com.fodrive.android.BleEventAdapter.BleEventBus;
import com.fodrive.android.BleEventAdapter.events.CharacteristicChangedEvent;
import com.fodrive.android.BleEventAdapter.events.DiscoveredDevicesEvent;
import com.fodrive.android.BleEventAdapter.events.DiscoveryServiceEvent;
import com.fodrive.android.BleEventAdapter.events.ServiceDiscoveredEvent;
import com.fodrive.android.BleEventAdapter.service.gatt.GattConnectionState;
import com.fodrive.squareup.otto.Subscribe;
import com.jinglingtec.ijiazu.bluetooth.BleConstants;
import com.jinglingtec.ijiazu.bluetooth.BleListener;
import com.jinglingtec.ijiazu.data.Depot;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoLogger;
import com.jinglingtec.ijiazu.util.FoUtil;

public class UtilBleController
{
    private static final String TAG = "UtilBleController";

    private static final UtilBleController insance = new UtilBleController();
    private static final BleEventAdapter BLE_EVENT_ADAPTER_INSTANCE = BleEventAdapter.getInstance();
    private static final BleEventBus BLE_EVENT_BUS_INSTANCE = BleEventBus.getInstance();

    private BleListener bleListener = null;

    private Context myctx = null;

    private boolean mRegisterStatus = false;

    // mac address that current connected.
    private String macAddress = null;
    // sign, is ble connected
    private boolean isBleConnected = false;

    // only
    private UtilBleController()
    {
        Log.d(TAG, "constructor");
    }

    //
    public static UtilBleController getInstance()
    {
        return insance;
    }

    // destory the util ble object
    public static void destroy()
    {
        Log.d(TAG, "destory");
        if (insance.mRegisterStatus)
        {
            BLE_EVENT_ADAPTER_INSTANCE.closeConnection(insance.myctx);
            BLE_EVENT_BUS_INSTANCE.unregister(insance.myctx);
            insance.mRegisterStatus = false;
        }
        insance.isBleConnected = false;
    }

    // set outter parameters
    public boolean setParams(Context ctx, BleListener listener)
    {
        this.myctx = ctx;
        this.bleListener = listener;
        return true;
    }

    //    //
    //    public void readBatteryLevel()
    //    {
    //
    //    }


    /**
     * initBle 初始化蓝牙
     */
    //    private void initBle()
    public void startScanBleDevice()
    {
        Log.d(TAG, "start scan ble device, is ble connected: " + isBleConnected);
        if (!mRegisterStatus)
        {
            /**
             * 注册当前类到bleEventbus
             */
            BLE_EVENT_BUS_INSTANCE.register(this);
            mRegisterStatus = true;
        }

        if (!isBleConnected)
        {
            BLE_EVENT_ADAPTER_INSTANCE.stopScanning(this.myctx);
            BLE_EVENT_ADAPTER_INSTANCE.startScanning(this.myctx);
        }

        //        BluetoothDevice _BluetoothDevice = getBlueToothDeviceFromConnected();
        //        if (_BluetoothDevice == null)
        //        {
        //            /**
        //             * 开始搜索
        //             */
        //            BLE_EVENT_ADAPTER_INSTANCE.stopScanning(this.myctx);
        //            BLE_EVENT_ADAPTER_INSTANCE.startScanning(this.myctx);
        //        }
        //        else
        //        {
        //        /**
        //         * 不需要搜索 已经连上了
        //         */
        //        }

    }


    //    /**
    //     * getBlueToothDeviceFromConnected 得到已经连接的蓝牙
    //     * <p/>
    //     * //     * @param 要查询是否连接的蓝牙的mac地址
    //     *
    //     * @return 蓝牙对象
    //     * @Exception 异常对象    null
    //     * @since CodingExample　Ver(编码范例查看) 1.1
    //     */
    //    public BluetoothDevice getBlueToothDeviceFromConnected()
    //    {
    //        final BluetoothManager bluetoothManager =
    //                (BluetoothManager) myctx.getSystemService(Context.BLUETOOTH_SERVICE);
    //        if (null == bluetoothManager)
    //        {
    //            return null;
    //        }
    //
    //        List<BluetoothDevice> listDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);
    //        if (null == listDevices)
    //        {
    //            return null;
    //        }
    //
    //        for (int i = listDevices.size() - 1; i > -1; i--)
    //        {
    //            BluetoothDevice device = listDevices.get(i);
    ////            String _mac = _BluetoothDevice.getAddress();
    ////            if (pMac.equalsIgnoreCase(_mac))
    ////            {
    ////                return _BluetoothDevice;
    ////            }
    //            if ((null != device) && (BluetoothDevice.DEVICE_TYPE_LE == device.getType())
    //                    && (BleConstants.IJIAZU_DEVICE_NAME.equals(device.getName())))
    //            {
    //                Log.d(TAG, "getBlueToothDeviceFromConnected, device name:" + device.getName() + " device address:" + device.getAddress() +
    //                        " device type:" + device.getType() + " device uuid:" + device.getUuids());
    //                return device;
    //            }
    //        }
    //        return null;
    //    }


    /**
     * onDiscoveredDevice 搜索设备结果的回调方法
     * <p/>
     * TODO 当开启了搜索后 搜索结果会在这个方法中获得
     * <p/>
     * //     * @param DiscoveredDevicesEvent 搜索设备返回事件对象
     *
     * @return void
     *
     * @Exception 异常对象
     * @since CodingExample　Ver(编码范例查看) 1.1
     */
    @Subscribe
    public void onDiscoveredDevice(DiscoveredDevicesEvent event)
    {
        if ((null == event) || (null == event.getDevice()))
        {
            return;
        }

        BluetoothDevice device = event.getDevice();
        if ((BluetoothDevice.DEVICE_TYPE_LE != device.getType()) || (null == event.getScanData()) || !isFodriveDevice(event.getScanData()))
        {
            return;
        }

        macAddress = device.getAddress();
        if ((null != FoConstants.myDeviceMac) && !FoConstants.myDeviceMac.equals(macAddress.toUpperCase()))
        {
            return;
        }

        BLE_EVENT_ADAPTER_INSTANCE.stopScanning(this.myctx);

        BLE_EVENT_ADAPTER_INSTANCE.setBluetoothDevice(device);
        BLE_EVENT_ADAPTER_INSTANCE.connectDevice(this.myctx, device);


        Log.d(TAG, "MacAddress:" + macAddress + " szScanRecord:" + event.getScanData());
    }

    // check is it the fodrive BLE device
    // It depends on the scan data, if it contains the magic string, yes, otherwise no
    private boolean isFodriveDevice(byte[] scanData23)
    {
        final String scanData = FoUtil.byteArrayToHex(scanData23);
        return ((null != scanData) && scanData.contains(BleConstants.IJIAZU_SECRET_KEY));
    }

    /**
     * onDiscoveryService 接收设备断开和连接的状态返回
     * <p/>
     * //     * @param DiscoveryServiceEvent 返回的连接状态的事件对象
     *
     * @author leequer
     */
    @Subscribe
    public void onDiscoveryService(DiscoveryServiceEvent pEvent)
    {
        if (null == pEvent)
        {
            return;
        }

        int _status = pEvent.getmStatus();
        switch (_status)
        {
            case GattConnectionState.STATE_CONNECTED:
            {
                Log.d(TAG, "ble connected ");
                isBleConnected = true;
                //                if (null != bleListener)
                //                {
                //                    bleListener.conntected_one();
                //                }
                break;
            }
            case GattConnectionState.STATE_DISCONNECTED:
            {
                Log.d(TAG, "ble dis connected by device");
                isBleConnected = false;
                macAddress = null;
                BLE_EVENT_ADAPTER_INSTANCE.closeConnection(this.myctx);
                if (null != bleListener)
                {
                    bleListener.disconnected();
                }
                break;
            }
            default:
                break;
        }

    }

    /**
     * onServiceDiscovered 当蓝牙所有的服务都被读取后回调方法
     * <p/>
     * //     * @param ServiceDiscoveredEvent 服务被发现事件
     */
    @Subscribe
    public void onServiceDiscovered(ServiceDiscoveredEvent event)
    {
        if (null == event)
        {
            return;
        }

        int _status = event.getmStatus();
        switch (_status)
        {
            case BluetoothGatt.GATT_SUCCESS:
            {
                Log.d(TAG, "蓝牙属性去读完毕,通知属性被注册完成");
                if (null != bleListener)
                {
                    bleListener.conntected();
                }
                //                readMoreData();
                break;
            }

            default:
                break;
        }

    }

    /**
     * onCharacteristicChanged 当设备属性值被改变时返回
     */
    @Subscribe
    public void onCharacteristicChanged(CharacteristicChangedEvent pEvent)
    {
        if ((null == pEvent) || (null == bleListener))
        {
            return;
        }

        BluetoothGattCharacteristic characteristic = pEvent.getCharacteristic();
        if (null != characteristic)
        {
            byte[] value = characteristic.getValue();
            if (FoLogger.isDebuggable)
            {
                String dat = FoUtil.byteArrayToHex(value);
                Log.d(TAG, "收到通知-onCharacteristicChanged: 0x" + dat);
            }
            bleListener.characteristicChanged(value);
        }
    }

    // get current device's mac address
    public String getMacAddress()
    {
        return this.macAddress;
    }

    // check is ble connected
    public boolean isBleConnected()
    {
        return isBleConnected;
    }


    // private

    // read more data,
    // they are : battery level, device ID
    public void readMoreData()
    {
        final int oneSecond = 1000;

        Handler handlerReadData = new Handler();
        // read device id after one second
        handlerReadData.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (null != Depot.gattService)
                {
                    final byte deviceIDCode = (byte) 0xA1;
                    Depot.gattService.writeSpecifidData(deviceIDCode);
                }
            }
        }, oneSecond);

        // read battery level after 2 seconds
        handlerReadData.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (null != Depot.gattService)
                {
                    final byte batteryCode = (byte) 0xA2;
                    Depot.gattService.writeSpecifidData(batteryCode);
                }
            }
        }, oneSecond * 2);

    }
}
