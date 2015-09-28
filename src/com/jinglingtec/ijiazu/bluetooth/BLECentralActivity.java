package com.jinglingtec.ijiazu.bluetooth;//package com.jinglingtec.fodrive.bluetooth;
//
//import android.app.Activity;
//import android.bluetooth.*;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.View;
//import android.widget.TextView;
//import com.jinglingtec.fodrive.R;
//
//import java.util.List;
//import java.util.UUID;
//
//public class BLECentralActivity extends Activity implements
//        BluetoothAdapter.LeScanCallback
//{
//
//    private BluetoothAdapter btAdapter;
//    private BluetoothGatt gatt;
//    private List<BluetoothGattService> serviceList;
//    private List<BluetoothGattCharacteristic> characterList;
//
//    // added by luming
//    private static Handler mHandler = null;
//    private TextView tvBtInfo;
//
//    private static final long SCAN_PERIOD = 10000;
//    private long mChangeCount = 0;
//
//    private String mRemoteBtAddr = "";
//
//    /**
//     * Battery Level characteristic
//     */
//    private final static UUID BATTERY_LEVEL_CHARACTERISTIC_UUID = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");
//    private BluetoothGattCharacteristic mBatteryCharacteristic = null;
//
//    private final static String FODRIVE_DEVICE_NAME = "fo link";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//
//        super.onCreate(savedInstanceState);
//        this.setContentView(R.layout.ble_central);
//
//        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        btAdapter = bluetoothManager.getAdapter();
//
//        // added by luming
//        initView();
//        initHandler();
//
//        if (btAdapter != null)
//        {
//            tvBtInfo.append("Bluetooth adpater is available!\n");
//        }
//
//    }
//
//    // added by luming
//    private void initView()
//    {
//        tvBtInfo = (TextView) findViewById(R.id.btInfo);
//        tvBtInfo.setText("");
//    }
//
//    private void initHandler()
//    {
//        if (mHandler != null)
//        {
//            return;
//        }
//
//        mHandler = new Handler()
//        {
//            public void handleMessage(Message msg)
//            {
//                if (msg.what == 1)
//                {
//                    if (msg.obj == null)
//                    {
//                        return;
//                    }
//                    tvBtInfo.append((String) msg.obj);
//                }
//            }
//        };
//    }
//
//    private void scanLeDevice(boolean enable)
//    {
//        if (enable)
//        {
//            mHandler.postDelayed(new Runnable()
//            {
//
//                @Override
//                public void run()
//                {
//                    btAdapter.stopLeScan(BLECentralActivity.this);
//                }
//            }, SCAN_PERIOD);
//            btAdapter.startLeScan(this);
//        }
//        else
//        {
//            btAdapter.stopLeScan(this);
//        }
//    }
//
//    @Override
//    protected void onStart()
//    {
//        super.onStart();
//    }
//
//    @Override
//    protected void onStop()
//    {
//        btAdapter.stopLeScan(this);
//        super.onStop();
//    }
//
//    public void onButtonClicked(View v)
//    {
//        Log.d("Chris", "onButtonClicked");
//        // btAdapter.startLeScan(this);
//        switch (v.getId())
//        {
//            case R.id.start:
//                scanLeDevice(true);
//                break;
//            case R.id.clear:
//                tvBtInfo.setText("");
//                break;
//            case R.id.battery:
//                if (mBatteryCharacteristic != null)
//                {
//                    gatt.readCharacteristic(mBatteryCharacteristic);
//                }
//                else
//                {
//                    updateBtInfo("\nmBatteryCharacteristic is NULL!\n");
//                }
//                break;
//            default:
//                break;
//        }
//
//    }
//
//    @Override
//    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
//    {
//        if (!device.getName().equals(FODRIVE_DEVICE_NAME))
//        {
//            return;
//        }
//
//        btAdapter.stopLeScan(this);
//        gatt = device.connectGatt(this, false, gattCallback);
//
//        Log.d("Chris", "Device Name:" + device.getName());
//        mRemoteBtAddr = device.getAddress();
//
//        // ½�����
//        updateBtInfo("Time: " + System.currentTimeMillis() + ", Device Name: "
//                + device.getName() + ", Address:" + mRemoteBtAddr + "\n");
//
//        mChangeCount = 0;
//
//    }
//
//    private BluetoothGattCallback gattCallback = new BluetoothGattCallback()
//    {
//
//        @Override
//        public void onConnectionStateChange(BluetoothGatt gatt, int status,
//                                            int newState)
//        {
//            Log.d("Chris", "onConnectionStateChange");
//            switch (newState)
//            {
//                case BluetoothProfile.STATE_CONNECTED:
//                    Log.d("Chris", "STATE_CONNECTED");
//                    gatt.discoverServices();
//
//                    // added by luming
//                    updateBtInfo("Time: "
//                            + System.currentTimeMillis()
//                            + ", Device is connected! Start service discovering ...\n");
//
//                    break;
//                case BluetoothProfile.STATE_DISCONNECTED:
//                    Log.d("Chris", "STATE_DISCONNECTED");
//                    updateBtInfo("Time: " + System.currentTimeMillis()
//                            + ", Device is disconnected! Start reconnecting ...\n");
//                /*
//                mRemoteDevice = btAdapter.getRemoteDevice(mRemoteBtAddr);
//				gatt = mRemoteDevice.connectGatt(BLECentralActivity.this, false, gattCallback);
//				*/
//                    break;
//                case BluetoothProfile.STATE_CONNECTING:
//                    Log.d("Chris", "STATE_CONNECTING");
//                    updateBtInfo("Time: " + System.currentTimeMillis()
//                            + ", Device is connecting!\n");
//                    break;
//                case BluetoothProfile.STATE_DISCONNECTING:
//                    updateBtInfo("Time: " + System.currentTimeMillis()
//                            + ", Device is disconnecting!\n");
//                    Log.d("Chris", "STATE_DISCONNECTING");
//                    break;
//            }
//
//            super.onConnectionStateChange(gatt, status, newState);
//        }
//
//        @Override
//        public void onServicesDiscovered(BluetoothGatt gatt, int status)
//        {
//            Log.d("Chris", "onServicesDiscovered");
//
//            //final String ServiceUUID = "09de2880-1415-4e2c-a48a-7208e3288537";
//            final String DescriptorUUID = "00002902-0000-1000-8000-0805f9b34fb";
//            final String FoLinkUUID = "0000ffff-0000-1000-8000-00805f9b34fb";
//            final String BatteryUUID = "0000180f-0000-1000-8000-00805f9b34fb";
//            final String BatteryLevel = "00002a19-0000-1000-8000-00805f9b34fb";
//
//            String szInfo = "";
//            if (status == BluetoothGatt.GATT_SUCCESS)
//            {
//
//                serviceList = gatt.getServices();
//                for (int i = 0; i < serviceList.size(); i++)
//                {
//                    BluetoothGattService theService = serviceList.get(i);
//                    Log.d("Chris", "ServiceName:" + theService.getUuid());
//                    if (!theService.getUuid().toString().equals(FoLinkUUID)
//                            && !theService.getUuid().toString().equals(BatteryUUID))
//                    {
//                        continue;
//                    }
//                    szInfo += "Time: " + System.currentTimeMillis()
//                            + "\n ServiceName:" + theService.getUuid() + "\n";
//                    characterList = theService.getCharacteristics();
//                    for (int j = 0; j < characterList.size(); j++)
//                    {
//                        BluetoothGattCharacteristic theCharacteristic = characterList
//                                .get(j);
//                        if (theCharacteristic.getUuid().toString().equals(BatteryLevel))
//                        {
//                            Log.d("luming", "mBatteryCharacteristic:" + theCharacteristic.getUuid());
//                            mBatteryCharacteristic = theCharacteristic;
//                        }
//                        Log.d("Chris",
//                                "---CharacterName:"
//                                        + characterList.get(j).getUuid());
//                        szInfo += "---Character:"
//                                + theCharacteristic.getUuid()
//                                + ", "
//                                + String.format("%08x",
//                                theCharacteristic.getProperties())
//                                // + ", " + String.format("%08x",
//                                // theCharacteristic.getPermissions())
//                                + "\n";
//                        List<BluetoothGattDescriptor> descriptorList = theCharacteristic
//                                .getDescriptors();
//                        if (descriptorList == null)
//                        {
//                            szInfo += "descriptorList is null!\n";
//                        }
//                        else
//                        {
//                            szInfo += "descriptorList has "
//                                    + descriptorList.size() + " items!\n";
//                            for (int k = 0; k < descriptorList.size(); k++)
//                            {
//                                BluetoothGattDescriptor theDescriptor = descriptorList
//                                        .get(k);
//                                Log.d("Chris", "---DescriptorName:"
//                                        + theDescriptor.getUuid());
//
//                                szInfo += "desc " + k + ": "
//                                        + theDescriptor.getUuid().toString()
//                                        + "\n";
//
//                            }
//                        }
//
//                        final int iFlag = theCharacteristic.getProperties();
//                        if (0 != (iFlag & BluetoothGattCharacteristic.PROPERTY_NOTIFY))
//                        {
//
//                            gatt.setCharacteristicNotification(
//                                    theCharacteristic, true);
//                            BluetoothGattDescriptor theDescriptor = theCharacteristic
//                                    .getDescriptor(UUID
//                                            .fromString(DescriptorUUID));
//                            theDescriptor
//                                    .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                            gatt.writeDescriptor(theDescriptor);
//
//                        }
//                        if (0 != (iFlag & BluetoothGattCharacteristic.PROPERTY_READ))
//                        {
//                            gatt.readCharacteristic(theCharacteristic);
//                        }
//                        if (0 != (iFlag & BluetoothGattCharacteristic.PROPERTY_INDICATE))
//                        {
//                            ;
//                        }
//                        if (0 != (iFlag & BluetoothGattCharacteristic.PROPERTY_WRITE))
//                        {
//                            // gatt.writeCharacteristic(theCharacteristic);
//                        }
//                    }
//                }
//            }
//            updateBtInfo(szInfo);
//
//            super.onServicesDiscovered(gatt, status);
//        }
//
//        @Override
//        public void onCharacteristicRead(BluetoothGatt gatt,
//                                         BluetoothGattCharacteristic characteristic, int status)
//        {
//            Log.d("Chris", "onCharacteristicRead");
//            super.onCharacteristicRead(gatt, characteristic, status);
//
//            if (status != BluetoothGatt.GATT_SUCCESS)
//            {
//                return;
//            }
//
//            String szInfo = "Time: " + System.currentTimeMillis() + "\n";
//            szInfo += "onCharacteristicRead: " + characteristic.getUuid()
//                    + "\n";
//            final byte[] data = characteristic.getValue();
//            if (data != null && data.length > 0)
//            {
//                szInfo += "len=" + data.length + "\n";
//                final StringBuilder stringBuilder = new StringBuilder(
//                        data.length);
//                for (byte byteChar : data)
//                {
//                    stringBuilder.append(String.format("%x", byteChar));
//                }
//                szInfo += stringBuilder.toString();
//
//            }
//            else
//            {
//                szInfo += "null";
//            }
//            szInfo += "\n";
//
//            updateBtInfo(szInfo);
//        }
//
//        @Override
//        public void onCharacteristicWrite(BluetoothGatt gatt,
//                                          BluetoothGattCharacteristic characteristic, int status)
//        {
//            Log.d("Chris", "onCharacteristicWrite");
//            super.onCharacteristicWrite(gatt, characteristic, status);
//        }
//
//        @Override
//        public void onCharacteristicChanged(BluetoothGatt gatt,
//                                            BluetoothGattCharacteristic characteristic)
//        {
//            Log.d("Chris", "onCharacteristicChanged");
//
//            mChangeCount++;
//
//            String szInfo = "Time: " + System.currentTimeMillis() + ", ";
//            szInfo += "onCharacteristicChanged: " + mChangeCount /*+ characteristic.getUuid()*/
//                    + "\n";
//            final byte[] data = characteristic.getValue();
//            if (data != null && data.length > 0)
//            {
//                szInfo += "len=" + data.length + ", ";
//                final StringBuilder stringBuilder = new StringBuilder(
//                        data.length);
//                szInfo += "value=";
//                for (byte byteChar : data)
//                {
//                    //stringBuilder.append(String.format("%x", byteChar));
//                    stringBuilder.append(String.valueOf(byteChar));
//                }
//                szInfo += stringBuilder.toString() + "\n";
//
//            }
//            else
//            {
//                szInfo += "null\n";
//            }
//
//            switch (data[0])
//            {
//                case 0x1:
//                    szInfo += "David �绰��";
//                    break;
//                case 0x2:
//                    szInfo += "David ���裡";
//                    break;
//                case 0x3:
//                    szInfo += "David OK��";
//                    break;
//                case 0x4:
//                    szInfo += "David ��̨��";
//                    break;
//                case 0x5:
//                    szInfo += "David ������";
//                    break;
//
//            }
//            szInfo += "\n";
//
//            updateBtInfo(szInfo);
//
//            if ((mChangeCount % 10) == 0)
//            {
//                //gatt.disconnect();
//            }
//
//            super.onCharacteristicChanged(gatt, characteristic);
//        }
//
//        @Override
//        public void onDescriptorRead(BluetoothGatt gatt,
//                                     BluetoothGattDescriptor descriptor, int status)
//        {
//            Log.d("Chris", "onDescriptorRead");
//            super.onDescriptorRead(gatt, descriptor, status);
//
//        }
//
//        @Override
//        public void onDescriptorWrite(BluetoothGatt gatt,
//                                      BluetoothGattDescriptor descriptor, int status)
//        {
//            Log.d("Chris", "onDescriptorWrite");
//            super.onDescriptorWrite(gatt, descriptor, status);
//        }
//
//        @Override
//        public void onReliableWriteCompleted(BluetoothGatt gatt, int status)
//        {
//            Log.d("Chris", "onReliableWriteCompleted");
//            super.onReliableWriteCompleted(gatt, status);
//        }
//
//        @Override
//        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status)
//        {
//            Log.d("Chris", "onReadRemoteRssi");
//            super.onReadRemoteRssi(gatt, rssi, status);
//        }
//
//    };
//
//    @Override
//    protected void onDestroy()
//    {
//        if (gatt != null)
//        {
//            gatt.close();
//        }
//
//        super.onDestroy();
//    }
//
//    // added by luming
//    private void updateBtInfo(String str)
//    {
//        if (str == null)
//        {
//            return;
//        }
//
//        Message msg = mHandler.obtainMessage();
//        msg.what = 1;
//        msg.obj = str;
//        msg.sendToTarget();
//    }
//
//}
