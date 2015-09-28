package com.fodrive.android.BleEventAdapter.service.gatt;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.fodrive.android.BleEventAdapter.BleEventAdapter;
import com.fodrive.android.BleEventAdapter.BleEventBus;
import com.fodrive.android.BleEventAdapter.events.CharacteristicChangedEvent;
import com.fodrive.android.BleEventAdapter.events.CharacteristicWriteEvent;
import com.fodrive.android.BleEventAdapter.events.DescriptorReadEvent;
import com.fodrive.android.BleEventAdapter.events.DiscoveryServiceEvent;
import com.fodrive.android.BleEventAdapter.events.ReadRemoteRssiEvent;
import com.fodrive.android.BleEventAdapter.events.ReliableWriteCompleted;
import com.fodrive.android.BleEventAdapter.events.ServiceDiscoveredEvent;
import com.jinglingtec.ijiazu.data.Depot;
import com.jinglingtec.ijiazu.util.FoLogger;
import com.jinglingtec.ijiazu.util.FoUtil;

@SuppressLint("NewApi")
public class GattService extends Service
{

    protected static final String TAG = "GattService";

    private BleEventBus mBleEventBus;

    private BluetoothDevice mDevice;

    /**
     * 设备所有属性的集合
     */
    private List<List<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<List<BluetoothGattCharacteristic>>();


    /**
     * 蓝牙通讯协议栈对象
     */
    private BluetoothGatt mBluetoothGatt;

    //    public void setmBluetoothGatt(BluetoothGatt mBluetoothGatt)
    //    {
    //        this.mBluetoothGatt = mBluetoothGatt;
    //    }

    private BluetoothGatt getmBluetoothGatt()
    {
        return mBluetoothGatt;
    }

    /**
     * 设备的write属性
     */
    private BluetoothGattCharacteristic mWriteChar;

    public BluetoothGattCharacteristic getmWriteChar()
    {
        return mWriteChar;
    }

    private void setmWriteChar(BluetoothGattCharacteristic mWriteChar)
    {
        this.mWriteChar = mWriteChar;
    }

    // write specific data
    //    private void writeSpecifidData(final BluetoothGatt gatt, byte dataType)
    public void writeSpecifidData(byte dataType)
    {
        Log.d(TAG, "try to write , onDescriptorWrite:" + dataType);
        if ((null == this.mBluetoothGatt) || (null == this.mWriteChar))
        {
            return;
        }

        byte[] _message = {dataType};
        mWriteChar.setValue(_message);
        this.mBluetoothGatt.writeCharacteristic(mWriteChar);
    }


    /**
     * 设备的notify属性
     */
    private BluetoothGattCharacteristic mNotifyChar;

    public BluetoothGattCharacteristic getmNotifyChar()
    {
        return mNotifyChar;
    }

    private void setmNotifyChar(BluetoothGattCharacteristic mNotifyChar)
    {
        this.mNotifyChar = mNotifyChar;
    }

    /**
     * 设备的notify 属性uuid
     */
    public final static UUID UUID_NOTIFY = UUID.fromString(SampleGattAttributes.CHAR_NOTIFY_UUID);
    /**
     * 设备的write 属性uuid
     */
    private static final Object UUID_WRITE = UUID.fromString(SampleGattAttributes.CHAR_WRITE_UUID);


    @Override
    public void onCreate()
    {
        FoUtil.printProcIDandName(this, TAG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        mBleEventBus = BleEventBus.getInstance();
        mDevice = BleEventAdapter.getInstance().getBluetoothDevice();
        if (mBluetoothGatt == null || mBluetoothGatt.connect())
        {
            mBluetoothGatt = mDevice.connectGatt(this, false, mGattCallBack);
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy(); // To change body of overridden methods use File |
        // Settings | File Templates.
        Log.i(TAG, "onDestroy");
        if (mBluetoothGatt != null)
        {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }

    }

    private BluetoothGattCallback mGattCallBack = new BluetoothGattCallback()
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {

            super.onConnectionStateChange(gatt, status, newState);

            if (newState == GattConnectionState.STATE_CONNECTED)
            {
                Log.i(TAG, "蓝牙已连接");
                /**
                 * 开始读取设备service和charateristice
                 */
                mBluetoothGatt.discoverServices();
                /**
                 * 通知主界面连接成功
                 */
                mBleEventBus.post(new DiscoveryServiceEvent(GattConnectionState.STATE_CONNECTED));
            }
            else if (newState == BluetoothGatt.STATE_DISCONNECTED)
            {
                Log.i(TAG, "蓝牙连接断开");
                Depot.gattService = null;
                if (mBluetoothGatt != null)
                {
                    mBluetoothGatt.close();
                    mBluetoothGatt = null;
                }
                /**
                 * 通知主界面连接断开
                 */
                mBleEventBus.post(new DiscoveryServiceEvent(GattConnectionState.STATE_DISCONNECTED));
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            super.onServicesDiscovered(gatt, status);
            switch (status)
            {
                case BluetoothGatt.GATT_SUCCESS:
                {
                    /**
                     * 获取所有的服务
                     */
                    List<BluetoothGattService> _list = gatt.getServices();
                    /**
                     * 保存服务列表
                     */
                    setmGattServicesList(_list);
                    /**
                     * 保存写属性
                     */
                    setmWriteChar(getWriteCharacteristics());
                    /**
                     * 保存通知属性
                     */
                    setmNotifyChar(getNotifyCharacteristics());
                    /**
                     * 注册通知属性
                     */
                    if (getmNotifyChar() != null)
                    {
                        boolean _return = registerNotifyChar(gatt, status, getmNotifyChar());
                        if (_return)
                        {
                            Log.i(TAG, "Notify register ok");
                            Depot.gattService = GattService.this;
                            mBleEventBus.post(new ServiceDiscoveredEvent(getmBluetoothGatt(), status));
                        }
                    }

                    break;
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            super.onCharacteristicRead(gatt, characteristic, status);
            // mBleEventBus.post(new CharacteristicReadEvent(mBluetoothGatt,
            // characteristic, status));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            super.onCharacteristicWrite(gatt, characteristic, status);
            mBleEventBus.post(new CharacteristicWriteEvent(mBluetoothGatt, characteristic, status));
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
        {
            super.onCharacteristicChanged(gatt, characteristic);
            if (FoLogger.isDebuggable)
            {
                byte[] value = characteristic.getValue();
                String dat = FoUtil.byteArrayToHex(value);
                Log.d(TAG, "收到通知-GattService.onCharacteristicChanged: 0x" + dat);
            }
            mBleEventBus.post(new CharacteristicChangedEvent(mBluetoothGatt, characteristic));
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
        {
            super.onDescriptorRead(gatt, descriptor, status);
            mBleEventBus.post(new DescriptorReadEvent(mBluetoothGatt, descriptor, status));
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
        {
            super.onDescriptorWrite(gatt, descriptor, status);
            //            Log.e(TAG, "try to write 0x95, onDescriptorWrite");
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                Log.i(TAG, "register ok");
                writeSpecifidData((byte) 0x95);
                //                mWriteChar = getWriteCharacteristics();
                //                if (mWriteChar != null)
                //                {
                //                    Log.i(TAG, "write 0x95");
                //                    byte[] _message = {(byte) 0x95};
                //                    mWriteChar.setValue(_message);
                //                    gatt.writeCharacteristic(mWriteChar);
                //                }
                //                else
                //                {
                //                    Log.e(TAG, "mWriteCharacteristic!=null");
                //                }

            }
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status)
        {
            super.onReliableWriteCompleted(gatt, status);
            mBleEventBus.post(new ReliableWriteCompleted(mBluetoothGatt, status));

        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status)
        {
            super.onReadRemoteRssi(gatt, rssi, status);
            mBleEventBus.post(new ReadRemoteRssiEvent(mBluetoothGatt, rssi, status));
        }
    };

    /**
     * 保存设备的所有服务并解析出每个服务下的属性存到mGattCharacteristics中
     */
    private void setmGattServicesList(List<BluetoothGattService> plist)
    {
        for (int i = plist.size() - 1; i > -1; i--)
        {
            BluetoothGattService _BluetoothGattService = plist.get(i);
            List<BluetoothGattCharacteristic> _gattCharacteristics = _BluetoothGattService.getCharacteristics();
            mGattCharacteristics.add(_gattCharacteristics);
        }

    }

    /**
     * 获取写的属性对象
     *
     * @return
     */
    private BluetoothGattCharacteristic getWriteCharacteristics()
    {
        for (int i = mGattCharacteristics.size() - 1; i > -1; i--)
        {
            List<BluetoothGattCharacteristic> _char_list = mGattCharacteristics.get(i);
            for (int j = _char_list.size() - 1; j > -1; j--)
            {
                BluetoothGattCharacteristic _char = _char_list.get(j);
                //                Log.e(TAG, _char.getUuid().toString());
                if (UUID_WRITE.equals(_char.getUuid()))
                {
                    return _char;
                }
            }

        }
        return null;
    }

    /**
     * 从mGattCharacteristics中得到 notify属性
     *
     * @return
     */
    private BluetoothGattCharacteristic getNotifyCharacteristics()
    {
        for (int i = mGattCharacteristics.size() - 1; i > -1; i--)
        {
            List<BluetoothGattCharacteristic> _char_list = mGattCharacteristics.get(i);
            for (int j = _char_list.size() - 1; j > -1; j--)
            {
                BluetoothGattCharacteristic _char = _char_list.get(j);
                if (UUID_NOTIFY.equals(_char.getUuid()))
                {
                    return _char;
                }
            }

        }
        return null;
    }

    /**
     * register notifycation char 注册广播通知
     *
     * @param gatt
     * @param status
     * @param pBluetoothGattCharacteristic
     *
     * @return boolean
     */
    private boolean registerNotifyChar(BluetoothGatt gatt, int status, BluetoothGattCharacteristic pBluetoothGattCharacteristic)
    {
        boolean _setcharnotifcation = gatt.setCharacteristicNotification(pBluetoothGattCharacteristic, true);

        BluetoothGattDescriptor descriptor = pBluetoothGattCharacteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
        boolean _setdescripter = descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean _witeDescripter = gatt.writeDescriptor(descriptor);

        if (_witeDescripter && _setdescripter && _setcharnotifcation)
        {
            return true;
        }
        return false;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
