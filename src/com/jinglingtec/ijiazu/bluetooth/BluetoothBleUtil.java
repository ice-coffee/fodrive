package com.jinglingtec.ijiazu.bluetooth;//package com.jinglingtec.fodrive.bluetooth;
//
//import android.bluetooth.*;
//import android.content.Context;
//import android.os.Handler;
//import android.util.Log;
//
//import java.util.List;
//import java.util.UUID;
//
//public class BluetoothBleUtil implements BluetoothAdapter.LeScanCallback
//{
//   private final static String mTag = "BluetoothBleUtil";
//
//   private BluetoothAdapter mBtAdapter = null;
//   //    private BluetoothGatt mGatt;
////    private List<BluetoothGattService> mServiceList;
////    private List<BluetoothGattCharacteristic> mCharacterList;
//   private Handler mHandler = null;
//
//   private static final long SCAN_PERIOD = 10000;
//
//   // context
//   private Context mCtx = null;
//
//   // ble listener
//   private BleListener mBleListener = null;
//
//   /**
//    * Battery Level characteristic
//    */
////    private final static UUID BATTERY_LEVEL_CHARACTERISTIC_UUID = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");
////    private BluetoothGattCharacteristic mBatteryCharacteristic = null;
//
//   private final static String FODRIVE_DEVICE_NAME = "fo link";
//
//   //for debug
//   private int mChangeCount = 0;
//
//
////    // constructor
////    public BluetoothBleUtil()
////    {
////    }
//
//   // initialize this object
//   public boolean initialize(Context ctx, BleListener listener)
//   {
//       if (null == ctx)
//       {
//           return false;
//       }
//
//       mCtx = ctx;
//       mBleListener = listener;
//
//       BluetoothManager bluetoothManager = (BluetoothManager) mCtx.getSystemService(Context.BLUETOOTH_SERVICE);
//       if (null != bluetoothManager)
//       {
//           mBtAdapter = bluetoothManager.getAdapter();
//       }
//       mHandler = new Handler();
//
//       return ((null != mHandler) && (null != mBtAdapter));
//   }
//
//   // release this object
//   public void release()
//   {
//       if (null != mBtAdapter)
//       {
//           mBtAdapter.stopLeScan(this);
//           mBtAdapter.cancelDiscovery();
//           mBtAdapter.disable();
//       }
//       mBtAdapter = null;
//       mHandler = null;
//       mCtx = null;
//   }
//
//   public boolean isBtEnabled()
//   {
//       return ((null != mBtAdapter) ? mBtAdapter.isEnabled() : false);
////        if (mBtAdapter == null)
////        {
////            return false;
////        }
////
////        if (mBtAdapter.isEnabled())
////        {
////            return true;
////        }
////        else
////        {
////            return false;
////        }
//   }
//
//   /*
//       public void enableBt(Context context)
//       {
//           Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//           startActivityForResult(intent, ENABLE_BLUETOOTHE);
//       }
//   */
//   public void scanLeDevice(boolean enable)
//   {
//       Log.d(mTag, "enter public void scanLeDevice(boolean enable)");
//       if ((null == mHandler) || (null == this.mBtAdapter))
//       {
////            Log.d(mTag, "enter public void scanLeDevice(boolean enable), something is empty");
//           return;
//       }
//
//       if (enable)
//       {
//           Log.d(mTag, "start to scan ble device");
//           mHandler.postDelayed(new Runnable()
//           {
//               @Override
//               public void run()
//               {
//                   if (null != mBtAdapter)
//                   {
//                       mBtAdapter.stopLeScan(BluetoothBleUtil.this);
//                   }
//               }
//           }, SCAN_PERIOD);
//
//           mBtAdapter.startLeScan(this);
//       }
//       else
//       {
//           Log.d(mTag, "stop ble device");
//           mBtAdapter.stopLeScan(this);
//       }
//   }
//
//   @Override
//   public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
//   {
//       Log.d(mTag, "Device Name:" + device.getName());
//       if ((null == device) || !FODRIVE_DEVICE_NAME.equals(device.getName()) || (null == mBtAdapter))
//       {
//           return;
//       }
//
//       mBtAdapter.stopLeScan(this);
//       BluetoothGatt mGatt = device.connectGatt(mCtx, false, mGattCallback);
//   }
//
//   private BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
//   {
//
//       @Override
//       public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
//       {
//           super.onConnectionStateChange(gatt, status, newState);
//
//           Log.d(mTag, "onConnectionStateChange");
//           switch (newState)
//           {
//               case BluetoothProfile.STATE_CONNECTED:
//                   Log.d(mTag, "STATE_CONNECTED");
//                   gatt.discoverServices();
//
//                   // added by luming
//                   updateBtInfo("Time: "
//                           + System.currentTimeMillis()
//                           + ", Device is connected! Start service discovering ...\n");
//
//                   if (null != mBleListener)
//                   {
//                       mBleListener.conntected();
//                   }
//                   break;
//               case BluetoothProfile.STATE_DISCONNECTED:
//                   Log.d("Chris", "STATE_DISCONNECTED");
//                   updateBtInfo("Time: " + System.currentTimeMillis()
//                           + ", Device is disconnected! Start reconnecting ...\n");
//               /*
//               mRemoteDevice = btAdapter.getRemoteDevice(mRemoteBtAddr);
//				gatt = mRemoteDevice.connectGatt(BLECentralActivity.this, false, gattCallback);
//				*/
//                   if (null != mBleListener)
//                   {
//                       mBleListener.disconnected();
//                   }
//                   break;
//               case BluetoothProfile.STATE_CONNECTING:
//                   Log.d(mTag, "STATE_CONNECTING");
//                   updateBtInfo("Time: " + System.currentTimeMillis()
//                           + ", Device is connecting!\n");
//                   break;
//               case BluetoothProfile.STATE_DISCONNECTING:
//                   updateBtInfo("Time: " + System.currentTimeMillis()
//                           + ", Device is disconnecting!\n");
//                   Log.d(mTag, "STATE_DISCONNECTING");
//                   break;
//           }
//       }
//
//       @Override
//       public void onServicesDiscovered(BluetoothGatt gatt, int status)
//       {
//           super.onServicesDiscovered(gatt, status);
//
//           Log.d(mTag, "onServicesDiscovered");
//
//           //final String ServiceUUID = "09de2880-1415-4e2c-a48a-7208e3288537";
//           final String DescriptorUUID = "00002902-0000-1000-8000-0805f9b34fb";
//           final String FoLinkUUID = "0000ffff-0000-1000-8000-00805f9b34fb";
//           final String BatteryUUID = "0000180f-0000-1000-8000-00805f9b34fb";
//           final String BatteryLevel = "00002a19-0000-1000-8000-00805f9b34fb";
//
//           String szInfo = "";
//           if (status == BluetoothGatt.GATT_SUCCESS)
//           {
//               List<BluetoothGattService> mServiceList = gatt.getServices();
//               if (null == mServiceList)
//               {
//                   return;
//               }
//
//               for (int i = 0; i < mServiceList.size(); i++)
//               {
//                   BluetoothGattService theService = mServiceList.get(i);
//                   if (null == theService)
//                   {
//                       continue;
//                   }
//
//                   String serviceUUID = "";
//                   final UUID uuidSvs = theService.getUuid();
//                   if (null != uuidSvs)
//                   {
//                       serviceUUID = uuidSvs.toString();
//                       Log.d(mTag, "ServiceName:" + serviceUUID);
//                   }
//                   if (!FoLinkUUID.equals(serviceUUID) && !BatteryUUID.equals(serviceUUID))
//                   {
//                       continue;
//                   }
////                    if (!theService.getUuid().toString().equals(FoLinkUUID)
////                            && !theService.getUuid().toString().equals(BatteryUUID))
////                    {
////                        continue;
////                    }
//
//                   szInfo += "Time: " + System.currentTimeMillis()
//                           + "\n ServiceName:" + serviceUUID + "\n";
//
//                   List<BluetoothGattCharacteristic> mCharacterList = theService.getCharacteristics();
//                   if (null == mCharacterList)
//                   {
//                       continue;
//                   }
//
//                   for (int j = 0; j < mCharacterList.size(); j++)
//                   {
//                       BluetoothGattCharacteristic theCharacteristic = mCharacterList.get(j);
//                       final UUID uuidCharacter = (null != theCharacteristic) ? theCharacteristic.getUuid() : null;
//                       if (null == uuidCharacter)
//                       {
//                           continue;
//                       }
//
////                        if (theCharacteristic.getUuid().toString().equals(BatteryLevel))
//                       if (BatteryLevel.equals(uuidCharacter.toString()))
//                       {
//                           Log.d(mTag, "mBatteryCharacteristic:" + uuidCharacter);
////                            mBatteryCharacteristic = theCharacteristic;
//                       }
//                       Log.d(mTag,
//                               "---CharacterName:"
//                                       + mCharacterList.get(j).getUuid());
//                       szInfo += "---Character:"
//                               + uuidCharacter
//                               + ", "
//                               + String.format("%08x",
//                               theCharacteristic.getProperties())
//                               // + ", " + String.format("%08x",
//                               // theCharacteristic.getPermissions())
//                               + "\n";
//                       List<BluetoothGattDescriptor> descriptorList = theCharacteristic.getDescriptors();
//                       if (descriptorList == null)
//                       {
//                           szInfo += "descriptorList is null!\n";
//                       }
//                       else
//                       {
//                           szInfo += "descriptorList has "
//                                   + descriptorList.size() + " items!\n";
//                           for (int k = 0; k < descriptorList.size(); k++)
//                           {
//                               BluetoothGattDescriptor theDescriptor = descriptorList.get(k);
//
//                               Log.d(mTag, "---DescriptorName:"
//                                       + theDescriptor.getUuid());
//
//                               szInfo += "desc " + k + ": "
//                                       + theDescriptor.getUuid().toString()
//                                       + "\n";
//                           }
//                       }
//
//                       final int iFlag = theCharacteristic.getProperties();
//                       if (0 != (iFlag & BluetoothGattCharacteristic.PROPERTY_NOTIFY))
//                       {
//
//                           gatt.setCharacteristicNotification(theCharacteristic, true);
//                           BluetoothGattDescriptor theDescriptor = theCharacteristic
//                                   .getDescriptor(UUID.fromString(DescriptorUUID));
//                           theDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                           gatt.writeDescriptor(theDescriptor);
//                       }
//
//                       if (0 != (iFlag & BluetoothGattCharacteristic.PROPERTY_READ))
//                       {
//                           gatt.readCharacteristic(theCharacteristic);
//                       }
//                       if (0 != (iFlag & BluetoothGattCharacteristic.PROPERTY_INDICATE))
//                       {
//                           ;
//                       }
//                       if (0 != (iFlag & BluetoothGattCharacteristic.PROPERTY_WRITE))
//                       {
//                           // gatt.writeCharacteristic(theCharacteristic);
//                       }
//                   }
//               }
//           }
//           updateBtInfo(szInfo);
//       }
//
//       @Override
//       public void onCharacteristicRead(BluetoothGatt gatt,
//                                        BluetoothGattCharacteristic characteristic, int status)
//       {
//           Log.d(mTag, "onCharacteristicRead");
//           super.onCharacteristicRead(gatt, characteristic, status);
//
//           if ((status != BluetoothGatt.GATT_SUCCESS) || (null == gatt) || (null == characteristic))
//           {
//               return;
//           }
//
//           String szInfo = "Time: " + System.currentTimeMillis() + "\n";
//           szInfo += "onCharacteristicRead: " + characteristic.getUuid() + "\n";
//
//           final byte[] data = characteristic.getValue();
//           if ((data != null) && (data.length > 0))
//           {
//               szInfo += "len=" + data.length + "\n";
//               final StringBuilder stringBuilder = new StringBuilder(data.length);
//               for (byte byteChar : data)
//               {
//                   stringBuilder.append(String.format("%x", byteChar));
//               }
//               szInfo += stringBuilder.toString();
//
//               if (null != mBleListener)
//               {
//                   mBleListener.readReady(data);
//               }
//           }
//           else
//           {
//               szInfo += "null";
//           }
//           szInfo += "\n";
//
//           updateBtInfo(szInfo);
//       }
//
//       @Override
//       public void onCharacteristicWrite(BluetoothGatt gatt,
//                                         BluetoothGattCharacteristic characteristic, int status)
//       {
//           Log.d(mTag, "onCharacteristicWrite");
//           super.onCharacteristicWrite(gatt, characteristic, status);
//       }
//
//       @Override
//       public void onCharacteristicChanged(BluetoothGatt gatt,
//                                           BluetoothGattCharacteristic characteristic)
//       {
//           Log.d(mTag, "onCharacteristicChanged");
//
//           mChangeCount++;
//
//           String szInfo = "Time: " + System.currentTimeMillis() + ", ";
//           szInfo += "onCharacteristicChanged: " + mChangeCount /*+ characteristic.getUuid()*/ + "\n";
//
//           final byte[] data = characteristic.getValue();
//           if ((data != null) && (data.length > 0))
//           {
//               szInfo += "len=" + data.length + ", ";
//               final StringBuilder stringBuilder = new StringBuilder(data.length);
//               szInfo += "value=";
//               for (byte byteChar : data)
//               {
//                   //stringBuilder.append(String.format("%x", byteChar));
//                   stringBuilder.append(String.valueOf(byteChar));
//               }
//               szInfo += stringBuilder.toString() + "\n";
//
//               if (null != mBleListener)
//               {
//                   mBleListener.characteristicChanged(data);
//               }
//           }
//           else
//           {
//               szInfo += "null\n";
//           }
//
//           switch (data[0])
//           {
//               case 0x1:
//                   szInfo += "David �绰��";
//                   break;
//               case 0x2:
//                   szInfo += "David ���裡";
//                   break;
//               case 0x3:
//                   szInfo += "David OK��";
//                   break;
//               case 0x4:
//                   szInfo += "David ��̨��";
//                   break;
//               case 0x5:
//                   szInfo += "David ������";
//                   break;
//
//           }
//           szInfo += "\n";
//
//           updateBtInfo(szInfo);
//
//           if ((mChangeCount % 10) == 0)
//           {
//               //gatt.disconnect();
//           }
//
//           super.onCharacteristicChanged(gatt, characteristic);
//       }
//
//       @Override
//       public void onDescriptorRead(BluetoothGatt gatt,
//                                    BluetoothGattDescriptor descriptor, int status)
//       {
//           Log.d(mTag, "onDescriptorRead");
//           super.onDescriptorRead(gatt, descriptor, status);
//
//       }
//
//       @Override
//       public void onDescriptorWrite(BluetoothGatt gatt,
//                                     BluetoothGattDescriptor descriptor, int status)
//       {
//           Log.d(mTag, "onDescriptorWrite");
//           super.onDescriptorWrite(gatt, descriptor, status);
//       }
//
//       @Override
//       public void onReliableWriteCompleted(BluetoothGatt gatt, int status)
//       {
//           Log.d(mTag, "onReliableWriteCompleted");
//           super.onReliableWriteCompleted(gatt, status);
//       }
//
//       @Override
//       public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status)
//       {
//           Log.d(mTag, "onReadRemoteRssi");
//           super.onReadRemoteRssi(gatt, rssi, status);
//       }
//
//   };
//
//   public void updateBtInfo(String string)
//   {
//       return;
//   }
//}
